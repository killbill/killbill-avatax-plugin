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

package org.killbill.billing.plugin.avatax.client;

import org.killbill.billing.plugin.avatax.AvaTaxRemoteTestBase;
import org.killbill.billing.plugin.avatax.client.model.TaxRateResult;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTaxRatesClient extends AvaTaxRemoteTestBase {

    @Test(groups = "integration")
    public void testFromPostal() throws Exception {
        final TaxRateResult result = taxRatesClient.fromPostal("94105-2204", "US");
        checkSFRates(result);
    }

    @Test(groups = "integration")
    public void testFromAddress() throws Exception {
        final TaxRateResult result = taxRatesClient.fromAddress("45 Fremont St", "San Francisco", "CA", "94105", "US");
        checkSFRates(result);
    }

    private void checkSFRates(final TaxRateResult result) {
        Assert.assertEquals(result.totalRate, 8.75);
        Assert.assertEquals(result.rates.size(), 4);

        Assert.assertEquals(result.rates.get(0).rate, 0.25);
        Assert.assertEquals(result.rates.get(0).name, "SAN FRANCISCO");
        Assert.assertEquals(result.rates.get(0).type, "County");

        Assert.assertEquals(result.rates.get(1).rate, 6.25);
        Assert.assertEquals(result.rates.get(1).name, "CALIFORNIA");
        Assert.assertEquals(result.rates.get(1).type, "State");

        Assert.assertEquals(result.rates.get(2).rate, 1.0);
        Assert.assertEquals(result.rates.get(2).name, "SAN FRANCISCO CO LOCAL TAX SL");
        Assert.assertEquals(result.rates.get(2).type, "Special");

        Assert.assertEquals(result.rates.get(3).rate, 1.25);
        Assert.assertEquals(result.rates.get(3).name, "SAN FRANCISCO COUNTY DISTRICT TAX SP");
        Assert.assertEquals(result.rates.get(3).type, "Special");
    }
}
