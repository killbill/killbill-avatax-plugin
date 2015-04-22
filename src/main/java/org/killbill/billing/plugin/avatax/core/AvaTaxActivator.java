/*
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
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

package org.killbill.billing.plugin.avatax.core;

import java.util.Hashtable;

import org.killbill.billing.invoice.plugin.api.InvoicePluginApi;
import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.plugin.api.notification.PluginConfigurationEventHandler;
import org.killbill.billing.plugin.avatax.api.AvalaraInvoicePluginApi;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.killbill.killbill.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.killbill.osgi.libs.killbill.OSGIKillbillEventDispatcher;
import org.osgi.framework.BundleContext;

public class AvaTaxActivator extends KillbillActivatorBase {

    public static final String PLUGIN_NAME = "killbill-avatax";

    public static final String PROPERTY_PREFIX = "org.killbill.billing.plugin.avatax.";
    public static final String TAX_RATES_API_PROPERTY_PREFIX = "org.killbill.billing.plugin.avatax.taxratesapi.";

    private AvaTaxConfigurationHandler avaTaxConfigurationHandler;
    private TaxRatesConfigurationHandler taxRatesConfigurationHandler;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        final AvaTaxDao dao = new AvaTaxDao(dataSource.getDataSource());
        final Clock clock = new DefaultClock();

        // Avalara AvaTax API
        final AvaTaxClient globalAvataxClient = new AvaTaxClient(configProperties.getProperties());
        avaTaxConfigurationHandler.setDefaultConfigurable(globalAvataxClient);

        // Avalara Tax Rates API
        final TaxRatesClient globalTaxRatesClient = new TaxRatesClient(configProperties.getProperties());
        taxRatesConfigurationHandler.setDefaultConfigurable(globalTaxRatesClient);

        final InvoicePluginApi invoicePluginApi = new AvalaraInvoicePluginApi(avaTaxConfigurationHandler,
                                                                              taxRatesConfigurationHandler,
                                                                              dao,
                                                                              killbillAPI,
                                                                              configProperties,
                                                                              logService,
                                                                              clock);
        registerInvoicePluginApi(context, invoicePluginApi);
    }

    @Override
    public OSGIKillbillEventDispatcher.OSGIKillbillEventHandler getOSGIKillbillEventHandler() {
        avaTaxConfigurationHandler = new AvaTaxConfigurationHandler(PLUGIN_NAME, killbillAPI, logService);
        taxRatesConfigurationHandler = new TaxRatesConfigurationHandler(PLUGIN_NAME, killbillAPI, logService);
        return new PluginConfigurationEventHandler(avaTaxConfigurationHandler, taxRatesConfigurationHandler);
    }

    private void registerInvoicePluginApi(final BundleContext context, final InvoicePluginApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, InvoicePluginApi.class, api, props);
    }
}
