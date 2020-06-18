/*
 * Copyright 2015-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2015-2020 The Billing Project, LLC
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
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.client.model.RateModel;
import org.killbill.billing.plugin.avatax.client.model.TaxRateResult;
import org.killbill.billing.plugin.avatax.core.TaxRatesConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.plugin.util.KillBillMoney;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TaxRatesTaxCalculator extends AvaTaxTaxCalculatorBase {

    public static final String RATE_TYPE = "rateType";

    private static final Logger logger = LoggerFactory.getLogger(TaxRatesTaxCalculator.class);

    private final TaxRatesConfigurationHandler taxRatesConfigurationHandler;

    public TaxRatesTaxCalculator(final TaxRatesConfigurationHandler taxRatesConfigurationHandler,
                                 final AvaTaxDao dao,
                                 final Clock clock,
                                 final OSGIKillbillAPI osgiKillbillAPI) {
        super(dao, clock, osgiKillbillAPI);
        this.taxRatesConfigurationHandler = taxRatesConfigurationHandler;
    }

    @Override
    protected Collection<InvoiceItem> buildInvoiceItems(final Account account,
                                                        final Invoice newInvoice,
                                                        final Invoice invoice,
                                                        final Map<UUID, InvoiceItem> taxableItems,
                                                        @Nullable final Map<UUID, List<InvoiceItem>> adjustmentItems,
                                                        @Nullable final String originalInvoiceReferenceCode,
                                                        final boolean dryRun,
                                                        final Iterable<PluginProperty> pluginProperties,
                                                        final UUID kbTenantId,
                                                        final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                                                        final LocalDate utcToday) throws SQLException {
        final TaxRateResult taxRates = getTaxRates(account, kbTenantId);
        if (taxRates == null) {
            return ImmutableList.<InvoiceItem>of();
        }
        logger.info("TaxRateResult for account {}: {}", account.getId(), taxRates.simplifiedToString());
        if (!dryRun) {
            dao.addResponse(account.getId(), newInvoice.getId(), kbInvoiceItems, taxRates, clock.getUTCNow(), kbTenantId);
        }

        final Collection<InvoiceItem> newTaxItems = new LinkedList<InvoiceItem>();
        for (final InvoiceItem taxableItem : taxableItems.values()) {
            if (adjustmentItems != null) {
                final InvoiceItem adjustmentItem;
                if (adjustmentItems.get(taxableItem.getId()) != null && adjustmentItems.get(taxableItem.getId()).size() == 1) {
                    // Could be a repair or an item adjustment: in either case, we use it to compute the service period
                    adjustmentItem = adjustmentItems.get(taxableItem.getId()).get(0);
                } else {
                    // Multiple adjustments: use the original service period
                    adjustmentItem = null;
                }
                final BigDecimal adjustmentAmount = sum(adjustmentItems.get(taxableItem.getId()));
                newTaxItems.addAll(buildInvoiceItems(newInvoice, taxableItem, adjustmentItem, pluginProperties, adjustmentAmount, taxRates));
            } else {
                newTaxItems.addAll(buildInvoiceItems(newInvoice, taxableItem, null, pluginProperties, taxableItem.getAmount(), taxRates));
            }
        }

        return newTaxItems;
    }

    private Collection<InvoiceItem> buildInvoiceItems(final Invoice newInvoice,
                                                      final InvoiceItem taxableItem,
                                                      @Nullable final InvoiceItem repairItem,
                                                      final Iterable<PluginProperty> pluginProperties,
                                                      final BigDecimal netItemAmount,
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
            final BigDecimal rawAmount = BigDecimal.valueOf(taxRates.totalRate).multiply(netItemAmount);
            // Use KillBillMoney to ensure we use the same rounding everywhere
            final BigDecimal amount = KillBillMoney.of(rawAmount, taxableItem.getCurrency());
            final InvoiceItem taxItem = buildTaxItem(taxableItem,
                                                     newInvoice.getId(),
                                                     repairItem,
                                                     amount,
                                                     "Tax");
            if (taxItem != null) {
                newTaxItems.add(taxItem);
            }
        } else {
            for (final RateModel rateModel : taxRates.rates) {
                if (rateTypes.isEmpty() || rateTypes.contains(rateModel.type)) {
                    final BigDecimal rawAmount = BigDecimal.valueOf(rateModel.rate).multiply(netItemAmount);
                    // Use KillBillMoney to ensure we use the same rounding everywhere
                    final BigDecimal amount = KillBillMoney.of(rawAmount, taxableItem.getCurrency());
                    final InvoiceItem taxItem = buildTaxItem(taxableItem,
                                                             newInvoice.getId(),
                                                             repairItem,
                                                             amount,
                                                             MoreObjects.firstNonNull(rateModel.name, "Tax"));
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
