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

package org.killbill.billing.plugin.avatax;

import java.security.GeneralSecurityException;
import java.util.Properties;

import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.TaxRatesClient;
import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class AvaTaxRemoteTestBase {

    // To run these tests, you need a properties file in the classpath (e.g. src/test/resources/avatax.properties)
    // See README.md for details on the required properties
    private static final String AVATAX_PROPERTIES = "avatax.properties";

    protected AvaTaxClient client;
    protected TaxRatesClient taxRatesClient;
    protected String companyCode;
    protected AvaTaxDao dao;

    @BeforeSuite(groups = {"slow", "integration"})
    public void setUpBeforeSuite() throws Exception {
        EmbeddedDbHelper.instance().startDb();
    }

    @BeforeMethod(groups = {"slow", "integration"})
    public void setUpBeforeMethod() throws Exception {
        EmbeddedDbHelper.instance().resetDB();
        dao = new AvaTaxDao(EmbeddedDbHelper.instance().getDataSource());
    }

    @BeforeMethod(groups = "integration")
    public void setUpBeforeMethod2() throws Exception {
        Properties properties = new Properties();
        try {
            properties = TestUtils.loadProperties(AVATAX_PROPERTIES);
        } catch (final RuntimeException ignored) {
            // Look up environment variables instead
            properties.put("org.killbill.billing.plugin.avatax.url", System.getenv("AVATAX_URL"));
            properties.put("org.killbill.billing.plugin.avatax.accountId", System.getenv("AVATAX_ACCOUNT_ID"));
            properties.put("org.killbill.billing.plugin.avatax.licenseKey", System.getenv("AVATAX_LICENSE_KEY"));
            properties.put("org.killbill.billing.plugin.avatax.companyCode", System.getenv("AVATAX_COMPANY_CODE"));
            properties.put("org.killbill.billing.plugin.avatax.commitDocuments", System.getenv("AVATAX_COMMIT_DOCUMENTS"));
            properties.put("org.killbill.billing.plugin.avatax.taxratesapi.url", System.getenv("AVATAX_TAXRATESAPI_URL"));
            properties.put("org.killbill.billing.plugin.avatax.taxratesapi.accountId", System.getenv("AVATAX_TAXRATESAPI_ACCOUNT_ID"));
            properties.put("org.killbill.billing.plugin.avatax.taxratesapi.licenseKey", System.getenv("AVATAX_TAXRATESAPI_LICENSE_KEY"));
            
        }
        buildAvataxClient(properties);
        buildTaxRatesClient(properties);
    }

    @AfterSuite(groups = {"slow", "integration"})
    public void tearDownAfterSuite() throws Exception {
        EmbeddedDbHelper.instance().stopDB();
    }

    private void buildAvataxClient(final Properties properties) throws GeneralSecurityException {
        client = new AvaTaxClient(properties);
        companyCode = properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "companyCode");
    }

    private void buildTaxRatesClient(final Properties properties) throws GeneralSecurityException {
        taxRatesClient = new TaxRatesClient(properties);
    }
}
