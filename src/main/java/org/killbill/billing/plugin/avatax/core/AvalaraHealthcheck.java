/*
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2020-2020 The Billing Project, LLC
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

import java.util.Map;

import javax.annotation.Nullable;

import org.killbill.billing.osgi.api.Healthcheck;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;
import org.killbill.billing.plugin.avatax.client.model.PingResult;
import org.killbill.billing.tenant.api.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Healthcheck for both APIs
public class AvalaraHealthcheck implements Healthcheck {

    private static final Logger logger = LoggerFactory.getLogger(AvalaraHealthcheck.class);

    private AvaTaxConfigurationHandler avaTaxConfigurationHandler;
    private TaxRatesConfigurationHandler taxRatesConfigurationHandler;

    public AvalaraHealthcheck(final AvaTaxConfigurationHandler avaTaxConfigurationHandler,
                              final TaxRatesConfigurationHandler taxRatesConfigurationHandler) {
        this.avaTaxConfigurationHandler = avaTaxConfigurationHandler;
        this.taxRatesConfigurationHandler = taxRatesConfigurationHandler;
    }

    @Override
    public HealthStatus getHealthStatus(@Nullable final Tenant tenant, @Nullable final Map properties) {
        if (tenant == null) {
            // The plugin is running
            return HealthStatus.healthy("Avalara OK (unauthenticated)");
        } else {
            final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(tenant.getId());
            final TaxRatesClient taxRatesClient = taxRatesConfigurationHandler.getConfigurable(tenant.getId());

            // Specifying the tenant lets you also validate the tenant configuration
            try {
                if (avaTaxClient.isConfigured()) {
                    final PingResult pingResult = avaTaxClient.ping();
                    return pingResult.authenticated ? HealthStatus.healthy() : HealthStatus.unHealthy("AvaTax client unauthenticated");
                } else if (taxRatesClient.isConfigured()) {
                    final PingResult pingResult = taxRatesClient.ping();
                    return pingResult.authenticated ? HealthStatus.healthy() : HealthStatus.unHealthy("TaxRates client unauthenticated");
                } else {
                    // Not configured for that tenant?
                    return HealthStatus.unHealthy("Avalara credentials missing");
                }
            } catch (final AvaTaxClientException e) {
                return HealthStatus.unHealthy(e.getMessage());
            }
        }
    }
}
