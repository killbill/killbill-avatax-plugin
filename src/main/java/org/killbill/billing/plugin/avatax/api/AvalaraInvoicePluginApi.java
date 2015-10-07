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

import java.util.List;
import java.util.UUID;

import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.plugin.api.InvoicePluginApi;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;
import org.killbill.billing.plugin.avatax.core.AvaTaxConfigurationHandler;
import org.killbill.billing.plugin.avatax.core.TaxRatesConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;
import org.killbill.killbill.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;

import com.google.common.collect.ImmutableList;

public class AvalaraInvoicePluginApi implements InvoicePluginApi {

    private final AvaTaxConfigurationHandler avaTaxConfigurationHandler;
    private final TaxRatesConfigurationHandler taxRatesConfigurationHandler;
    private final AvaTaxInvoicePluginApi avaTaxInvoicePluginApi;
    private final TaxRatesInvoicePluginApi taxRatesInvoicePluginApi;

    public AvalaraInvoicePluginApi(final AvaTaxConfigurationHandler avaTaxConfigurationHandler,
                                   final TaxRatesConfigurationHandler taxRatesConfigurationHandler,
                                   final AvaTaxDao dao,
                                   final OSGIKillbillAPI killbillAPI,
                                   final OSGIConfigPropertiesService configProperties,
                                   final OSGIKillbillLogService logService,
                                   final Clock clock) {
        this.avaTaxConfigurationHandler = avaTaxConfigurationHandler;
        this.taxRatesConfigurationHandler = taxRatesConfigurationHandler;

        this.avaTaxInvoicePluginApi = new AvaTaxInvoicePluginApi(avaTaxConfigurationHandler,
                                                                 dao,
                                                                 killbillAPI,
                                                                 configProperties,
                                                                 logService,
                                                                 clock);
        this.taxRatesInvoicePluginApi = new TaxRatesInvoicePluginApi(taxRatesConfigurationHandler,
                                                                     dao,
                                                                     killbillAPI,
                                                                     configProperties,
                                                                     logService,
                                                                     clock);
    }

    @Override
    public List<InvoiceItem> getAdditionalInvoiceItems(final Invoice invoice, final boolean dryRun, final Iterable<PluginProperty> properties, final CallContext callContext) {
        final UUID kbTenantId = callContext.getTenantId();
        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(kbTenantId);
        final TaxRatesClient taxRatesClient = taxRatesConfigurationHandler.getConfigurable(kbTenantId);

        // Note: there is a small window of doom here if the reconfiguration happens at the wrong time

        if (avaTaxClient.isConfigured()) {
            // If a per tenant taxRatesClient is configured and a global avaTaxClient, we would use the latter
            // Should this behavior be configurable?
            return avaTaxInvoicePluginApi.getAdditionalInvoiceItems(invoice, dryRun, properties, callContext);
        } else if (taxRatesClient.isConfigured()) {
            return taxRatesInvoicePluginApi.getAdditionalInvoiceItems(invoice, dryRun, properties, callContext);
        } else {
            // Not configured for that tenant?
            return ImmutableList.<InvoiceItem>of();
        }
    }
}
