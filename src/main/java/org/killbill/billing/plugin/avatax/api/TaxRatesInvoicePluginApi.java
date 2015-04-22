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

import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.invoice.PluginInvoicePluginApi;
import org.killbill.billing.plugin.avatax.core.TaxRatesConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;
import org.killbill.killbill.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillLogService;

public class TaxRatesInvoicePluginApi extends PluginInvoicePluginApi {

    private final TaxRatesTaxCalculator calculator;

    public TaxRatesInvoicePluginApi(final TaxRatesConfigurationHandler taxRatesConfigurationHandler,
                                    final AvaTaxDao dao,
                                    final OSGIKillbillAPI killbillApi,
                                    final OSGIConfigPropertiesService configProperties,
                                    final OSGIKillbillLogService logService,
                                    final Clock clock) {
        super(killbillApi, configProperties, logService, clock);
        this.calculator = new TaxRatesTaxCalculator(taxRatesConfigurationHandler, dao, clock);
    }

    @Override
    public List<InvoiceItem> getAdditionalInvoiceItems(final Invoice invoice, final Iterable<PluginProperty> properties, final CallContext context) {
        return getAdditionalTaxInvoiceItems(calculator, invoice, properties, context);
    }
}
