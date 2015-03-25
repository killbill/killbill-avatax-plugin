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

package org.killbill.billing.plugin.avatax.client.model;

import org.killbill.billing.plugin.avatax.AvaTaxTestBase;
import org.killbill.billing.plugin.avatax.client.model.Address.AddressType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestValidateResult extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testDeserialization() throws Exception {
        final String response = "{\n" +
                                "\"ResultCode\": \"Success\",\n" +
                                "\"Address\": {\n" +
                                "    \"Line1\": \"ATTN Accounts Payable\",\n" +
                                "    \"City\": \"Chicago\",\n" +
                                "    \"Region\": \"IL\",\n" +
                                "    \"PostalCode\": \"60602-1304\",\n" +
                                "    \"Country\": \"US\",\n" +
                                "    \"County\": \"Cook\",\n" +
                                "    \"FipsCode\": \"1703100000\",\n" +
                                "    \"CarrierRoute\": \"C012\",\n" +
                                "    \"PostNet\": \"606021304990\",\n" +
                                "    \"AddressType\": \"H\",\n" +
                                "    \"Line2\": \"118 N Clark St Ste 100\"\n" +
                                "    }\n" +
                                "}";

        final ValidateResult validateResult = mapper.readValue(response, ValidateResult.class);
        Assert.assertEquals(validateResult.Address.Line1, "ATTN Accounts Payable");
        Assert.assertEquals(validateResult.Address.City, "Chicago");
        Assert.assertEquals(validateResult.Address.Region, "IL");
        Assert.assertEquals(validateResult.Address.PostalCode, "60602-1304");
        Assert.assertEquals(validateResult.Address.Country, "US");
        Assert.assertEquals(validateResult.Address.County, "Cook");
        Assert.assertEquals(validateResult.Address.FipsCode, "1703100000");
        Assert.assertEquals(validateResult.Address.CarrierRoute, "C012");
        Assert.assertEquals(validateResult.Address.PostNet, "606021304990");
        Assert.assertEquals(validateResult.Address.AddressType, AddressType.H);
        Assert.assertEquals(validateResult.Address.Line2, "118 N Clark St Ste 100");
        Assert.assertEquals(validateResult.ResultCode, CommonResponse.SeverityLevel.Success);
        Assert.assertNull(validateResult.Messages);
    }
}
