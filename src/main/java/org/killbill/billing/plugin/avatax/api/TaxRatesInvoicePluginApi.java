/*
 * Copyright 2014-2022 Groupon, Inc
 * Copyright 2020-2022 Equinix, Inc
 * Copyright 2014-2022 The Billing Project, LLC
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

import java.util.List;

import org.killbill.billing.account.api.Account;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.plugin.api.AdditionalItemsResult;
import org.killbill.billing.invoice.plugin.api.InvoiceContext;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.invoice.PluginInvoicePluginApi;
import org.killbill.billing.plugin.avatax.core.TaxRatesConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.Clock;

public class TaxRatesInvoicePluginApi extends PluginInvoicePluginApi {

    private final TaxRatesTaxCalculator calculator;

    public TaxRatesInvoicePluginApi(final TaxRatesConfigurationHandler taxRatesConfigurationHandler,
                                    final AvaTaxDao dao,
                                    final OSGIKillbillAPI killbillApi,
                                    final OSGIConfigPropertiesService configProperties,
                                    final Clock clock) {
        super(killbillApi, configProperties, clock);
        this.calculator = new TaxRatesTaxCalculator(taxRatesConfigurationHandler, dao, clock, killbillApi);
    }

    @Override
    public AdditionalItemsResult getAdditionalInvoiceItems(final Invoice invoice, final boolean dryRun, final Iterable<PluginProperty> properties, final InvoiceContext context) {
        final Account account = getAccount(invoice.getAccountId(), context);

        try {
            final List<InvoiceItem> additionalItems = calculator.compute(account, invoice, dryRun, properties, context);
            return new AvataxAdditionalItemsResult(additionalItems, null);
        } catch (final Exception e) {
            // Prevent invoice generation
            throw new RuntimeException(e);
        }
    }
}
