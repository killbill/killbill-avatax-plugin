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

package org.killbill.billing.plugin.avatax;

import java.security.GeneralSecurityException;
import java.util.Properties;

import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.TestWithEmbeddedDBBase;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;
import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;
import org.testng.annotations.BeforeMethod;

public abstract class AvaTaxRemoteTestBase extends TestWithEmbeddedDBBase {

    // To run these tests, you need a properties file in the classpath (e.g. src/test/resources/avatax.properties)
    // See README.md for details on the required properties
    private static final String AVATAX_PROPERTIES = "avatax.properties";

    protected AvaTaxClient client;
    protected TaxRatesClient taxRatesClient;
    protected String companyCode;

    @BeforeMethod(groups = "slow")
    public void beforeMethod() throws Exception {
        final Properties properties = TestUtils.loadProperties(AVATAX_PROPERTIES);
        buildAvataxClient(properties);
        buildTaxRatesClient(properties);
    }

    private void buildAvataxClient(final Properties properties) throws GeneralSecurityException {
        client = new AvaTaxClient(properties);
        companyCode = properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "companyCode");
    }

    private void buildTaxRatesClient(final Properties properties) throws GeneralSecurityException {
        taxRatesClient = new TaxRatesClient(properties);
    }
}
