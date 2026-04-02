/*
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2014-2020 The Billing Project, LLC
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

import java.sql.SQLException;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.flywaydb.core.Flyway;
import org.killbill.billing.invoice.plugin.api.InvoicePluginApi;
import org.killbill.billing.osgi.api.Healthcheck;
import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.billing.plugin.api.notification.PluginConfigurationEventHandler;
import org.killbill.billing.plugin.avatax.api.AvalaraInvoicePluginApi;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.plugin.core.resources.jooby.PluginApp;
import org.killbill.billing.plugin.core.resources.jooby.PluginAppBuilder;
import org.killbill.billing.plugin.dao.PluginDao;
import org.killbill.billing.plugin.dao.PluginDao.DBEngine;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvaTaxActivator extends KillbillActivatorBase {

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxActivator.class);

    public static final String PLUGIN_NAME = "killbill-avatax";

    public static final String PROPERTY_PREFIX = "org.killbill.billing.plugin.avatax.";
    public static final String TAX_RATES_API_PROPERTY_PREFIX = "org.killbill.billing.plugin.avatax.taxratesapi.";

    private static final String SHOULD_RUN_MIGRATIONS_PROPERTY = PROPERTY_PREFIX + "runMigrations";

    private AvaTaxConfigurationHandler avaTaxConfigurationHandler;
    private TaxRatesConfigurationHandler taxRatesConfigurationHandler;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        runMigrationsIfEnabled();

        final AvaTaxDao dao = new AvaTaxDao(dataSource.getDataSource());
        final Clock clock = new DefaultClock();

        avaTaxConfigurationHandler = new AvaTaxConfigurationHandler(PLUGIN_NAME, killbillAPI);
        taxRatesConfigurationHandler = new TaxRatesConfigurationHandler(PLUGIN_NAME, killbillAPI);

        // Avalara AvaTax API
        final AvaTaxClient globalAvataxClient = avaTaxConfigurationHandler.createConfigurable(configProperties.getProperties());
        avaTaxConfigurationHandler.setDefaultConfigurable(globalAvataxClient);

        // Avalara Tax Rates API
        final TaxRatesClient globalTaxRatesClient = taxRatesConfigurationHandler.createConfigurable(configProperties.getProperties());
        taxRatesConfigurationHandler.setDefaultConfigurable(globalTaxRatesClient);

        final InvoicePluginApi invoicePluginApi = new AvalaraInvoicePluginApi(avaTaxConfigurationHandler,
                                                                              taxRatesConfigurationHandler,
                                                                              dao,
                                                                              killbillAPI,
                                                                              configProperties,
                                                                              clock);
        registerInvoicePluginApi(context, invoicePluginApi);

        // Expose the healthcheck, so other plugins can check on the AvaTax status
        final Healthcheck avalaraHealthcheck = new AvalaraHealthcheck(avaTaxConfigurationHandler,
                                                                      taxRatesConfigurationHandler);
        registerHealthcheck(context, avalaraHealthcheck);

        // Register the servlet
        final PluginApp pluginApp = new PluginAppBuilder(PLUGIN_NAME,
                                                         killbillAPI,
                                                         dataSource,
                                                         super.clock,
                                                         configProperties).withRouteClass(AvalaraHealthcheckServlet.class)
                                                                          .withRouteClass(AvaTaxTaxCodesServlet.class)
                                                                          .withRouteClass(AvaTaxTransactionsServlet.class)
                                                                          .withService(avalaraHealthcheck)
                                                                          .withService(dao)
                                                                          .withService(avaTaxConfigurationHandler)
                                                                          .build();
        final HttpServlet servlet = PluginApp.createServlet(pluginApp);
        registerServlet(context, servlet);

        registerEventHandler();
    }

    private void registerEventHandler() {
        final PluginConfigurationEventHandler handler = new PluginConfigurationEventHandler(avaTaxConfigurationHandler, taxRatesConfigurationHandler);
        dispatcher.registerEventHandlers(handler);
    }

    private void registerInvoicePluginApi(final BundleContext context, final InvoicePluginApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, InvoicePluginApi.class, api, props);
    }

    private void registerServlet(final BundleContext context, final Servlet servlet) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, Servlet.class, servlet, props);
    }

    private void registerHealthcheck(final BundleContext context, final Healthcheck healthcheck) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, Healthcheck.class, healthcheck, props);
    }

    private void runMigrationsIfEnabled() {
        if (Boolean.parseBoolean(configProperties.getProperties().getProperty(SHOULD_RUN_MIGRATIONS_PROPERTY, "true"))) {
            DBEngine dbEngine;
            try {
                dbEngine = PluginDao.getDBEngine(dataSource.getDataSource());
            } catch (final SQLException e) {
                logger.warn("Unable to determine database engine, defaulting to MySQL migrations", e);
                dbEngine = DBEngine.MYSQL;
            }

            final String locations;
            switch (dbEngine) {
                case POSTGRESQL:
                    locations = "classpath:migration/postgresql";
                    break;
                case GENERIC:
                case H2:
                case MYSQL:
                default:
                    // H2 and GENERIC use MySQL-compatible migration scripts
                    locations = "classpath:migration/mysql";
                    break;
            }

            final Flyway flyway = Flyway.configure(getClass().getClassLoader())
                                        .dataSource(dataSource.getDataSource())
                                        .locations(locations)
                                        .table("avatax_schema_history")
                                        .baselineOnMigrate(true)
                                        .baselineVersion("0")
                                        .load();
            flyway.migrate();
        } else {
            logger.info("Skipping Flyway migrations as '{}' is set to false", SHOULD_RUN_MIGRATIONS_PROPERTY);
        }
    }
}
