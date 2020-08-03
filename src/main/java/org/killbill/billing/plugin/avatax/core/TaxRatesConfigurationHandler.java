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

import java.security.GeneralSecurityException;
import java.util.Properties;

import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.plugin.api.notification.PluginTenantConfigurableConfigurationHandler;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;

public class TaxRatesConfigurationHandler extends PluginTenantConfigurableConfigurationHandler<TaxRatesClient> {

    public TaxRatesConfigurationHandler(final String pluginName,
                                        final OSGIKillbillAPI osgiKillbillAPI) {
        super(pluginName, osgiKillbillAPI);
    }

    @Override
    protected TaxRatesClient createConfigurable(final Properties properties) {
        try {
            return new TaxRatesClient(properties);
        } catch (final GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}

