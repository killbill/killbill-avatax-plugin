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

package org.killbill.billing.plugin.avatax.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.killbill.billing.plugin.avatax.AvaTaxRemoteTestBase;
import org.killbill.billing.plugin.avatax.client.model.RateModel;
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
        Assert.assertEquals(result.totalRate, 0.08625);
        Assert.assertEquals(result.rates.size(), 4);

        // Sort rates by type then by rate to avoid order-dependent assertions,
        // as the AvaTax API does not guarantee a specific ordering.
        final List<RateModel> sorted = new ArrayList<>(result.rates);
        sorted.sort(Comparator.comparing((RateModel r) -> r.type)
                              .thenComparingDouble(r -> r.rate));

        Assert.assertEquals(sorted.get(0).rate, 0.0025);
        Assert.assertEquals(sorted.get(0).name, "CA COUNTY TAX");
        Assert.assertEquals(sorted.get(0).type, "County");

        Assert.assertEquals(sorted.get(1).rate, 0.01);
        Assert.assertEquals(sorted.get(1).name, "CA SPECIAL TAX");
        Assert.assertEquals(sorted.get(1).type, "Special");

        Assert.assertEquals(sorted.get(2).rate, 0.01375);
        Assert.assertEquals(sorted.get(2).name, "CA SPECIAL TAX");
        Assert.assertEquals(sorted.get(2).type, "Special");

        Assert.assertEquals(sorted.get(3).rate, 0.06);
        Assert.assertEquals(sorted.get(3).name, "CA STATE TAX");
        Assert.assertEquals(sorted.get(3).type, "State");
    }
}
