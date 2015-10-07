/*
 * Copyright 2015 Groupon, Inc
 * Copyright 2015 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.plugin.avatax.api;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.client.model.JurisTaxRate;
import org.killbill.billing.plugin.avatax.client.model.TaxRateResult;
import org.killbill.billing.plugin.avatax.core.TaxRatesConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TaxRatesTaxCalculator extends AvaTaxTaxCalculatorBase {

    public static final String RATE_TYPE = "rateType";

    private static final Logger logger = LoggerFactory.getLogger(TaxRatesTaxCalculator.class);

    private final TaxRatesConfigurationHandler taxRatesConfigurationHandler;

    public TaxRatesTaxCalculator(final TaxRatesConfigurationHandler taxRatesConfigurationHandler, final AvaTaxDao dao, final Clock clock) {
        super(dao, clock);
        this.taxRatesConfigurationHandler = taxRatesConfigurationHandler;
    }

    @Override
    protected Collection<InvoiceItem> buildInvoiceItems(final Account account,
                                                        final Invoice newInvoice,
                                                        final Invoice invoice,
                                                        final Map<UUID, InvoiceItem> taxableItems,
                                                        @Nullable final Map<UUID, Collection<InvoiceItem>> adjustmentItems,
                                                        @Nullable final String originalInvoiceReferenceCode,
                                                        final boolean dryRun,
                                                        final Iterable<PluginProperty> pluginProperties,
                                                        final UUID kbTenantId,
                                                        final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                                                        final LocalDate utcToday) throws AvaTaxClientException, SQLException {
        // Expected tax rates
        final TaxRateResult taxRates = getTaxRates(account, kbTenantId);
        if (taxRates == null) {
            return ImmutableList.<InvoiceItem>of();
        }
        logger.info("TaxRateResult for account {}: {}", account.getId(), taxRates.simplifiedToString());

        dao.addResponse(account.getId(), invoice.getId(), kbInvoiceItems, taxRates, clock.getUTCNow(), kbTenantId);

        final Collection<InvoiceItem> newTaxItems = new LinkedList<InvoiceItem>();
        for (final InvoiceItem taxableItem : taxableItems.values()) {
            final Collection<InvoiceItem> adjustmentsForTaxableItem = adjustmentItems == null ? null : adjustmentItems.get(taxableItem.getId());
            final BigDecimal netItemAmount = netAmount(taxableItem, adjustmentsForTaxableItem);
            newTaxItems.addAll(buildInvoiceItems(newInvoice, taxableItem, pluginProperties, netItemAmount, utcToday, taxRates));
        }

        return newTaxItems;
    }

    private Collection<InvoiceItem> buildInvoiceItems(final Invoice newInvoice,
                                                      final InvoiceItem taxableItem,
                                                      final Iterable<PluginProperty> pluginProperties,
                                                      final BigDecimal netItemAmount,
                                                      final LocalDate utcToday,
                                                      final TaxRateResult taxRates) {
        final List<String> rateTypes = ImmutableList.<String>copyOf(Iterables.transform(PluginProperties.findPluginProperties(RATE_TYPE, pluginProperties),
                                                                                        new Function<PluginProperty, String>() {
                                                                                            @Override
                                                                                            public String apply(final PluginProperty pluginProperty) {
                                                                                                return pluginProperty.getValue().toString();
                                                                                            }
                                                                                        }));

        final Collection<InvoiceItem> newTaxItems = new LinkedList<InvoiceItem>();
        if (taxRates.rates == null || taxRates.rates.isEmpty()) {
            final InvoiceItem taxItem = buildTaxItem(taxableItem,
                                                     newInvoice.getId(),
                                                     utcToday,
                                                     BigDecimal.valueOf(taxRates.totalRate).multiply(netItemAmount),
                                                     "Tax");
            if (taxItem != null) {
                newTaxItems.add(taxItem);
            }
        } else {
            for (final JurisTaxRate jurisTaxRate : taxRates.rates) {
                if (rateTypes.isEmpty() || rateTypes.contains(jurisTaxRate.type)) {
                    final InvoiceItem taxItem = buildTaxItem(taxableItem,
                                                             newInvoice.getId(),
                                                             utcToday,
                                                             BigDecimal.valueOf(jurisTaxRate.rate).multiply(netItemAmount),
                                                             Objects.firstNonNull(jurisTaxRate.name, "Tax"));
                    if (taxItem != null) {
                        newTaxItems.add(taxItem);
                    }
                }
            }
        }
        return newTaxItems;
    }

    private TaxRateResult getTaxRates(final Account account, final UUID kbTenantId) {
        try {
            if (account.getAddress1() != null &&
                account.getCity() != null &&
                account.getStateOrProvince() != null &&
                account.getPostalCode() != null &&
                account.getCountry() != null) {
                return taxRatesConfigurationHandler.getConfigurable(kbTenantId).fromAddress(account.getAddress1(), account.getCity(), account.getStateOrProvince(), account.getPostalCode(), account.getCountry());
            } else if (account.getPostalCode() != null && account.getCountry() != null) {
                return taxRatesConfigurationHandler.getConfigurable(kbTenantId).fromPostal(account.getPostalCode(), account.getCountry());
            } else {
                logger.warn("Not enough information to retrieve tax rates for account {}", account.getId());
                return null;
            }
        } catch (final AvaTaxClientException e) {
            logger.warn("Unable to retrieve tax rates", e);
            return null;
        }
    }
}
