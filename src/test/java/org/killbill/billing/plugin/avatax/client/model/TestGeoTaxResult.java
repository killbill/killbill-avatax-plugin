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
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGeoTaxResult extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testDeserialization() throws Exception {
        final String response = "{\n" +
                                "\"Rate\": 0.087,\n" +
                                "\"Tax\": 0.87,\n" +
                                "\"ResultCode\": \"Success\",\n" +
                                "\"TaxDetails\": [\n" +
                                "        {\n" +
                                "            \"Country\": \"US\",\n" +
                                "            \"Region\": \"WA\",\n" +
                                "            \"JurisType\": \"State\",\n" +
                                "            \"Rate\": 0.065,\n" +
                                "            \"Tax\": 0.65,\n" +
                                "            \"JurisName\": \"WASHINGTON\",\n" +
                                "            \"TaxName\": \"WA STATE TAX\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"Country\": \"US\",\n" +
                                "            \"Region\": \"WA\",\n" +
                                "            \"JurisType\": \"City\",\n" +
                                "            \"Rate\": 0.022,\n" +
                                "            \"Tax\": 0.22,\n" +
                                "            \"JurisName\": \"BAINBRIDGE ISLAND\",\n" +
                                "            \"TaxName\": \"WA CITY TAX\"\n" +
                                "        }\n" +
                                "    ]\n" +
                                "}";

        final GeoTaxResult geoTaxResult = mapper.readValue(response, GeoTaxResult.class);
        Assert.assertEquals(geoTaxResult.Rate, 0.087);
        Assert.assertEquals(geoTaxResult.Tax, 0.87);
        Assert.assertEquals(geoTaxResult.TaxDetails.length, 2);
        Assert.assertEquals(geoTaxResult.TaxDetails[0].Country, "US");
        Assert.assertEquals(geoTaxResult.TaxDetails[0].Region, "WA");
        Assert.assertEquals(geoTaxResult.TaxDetails[0].JurisType, "State");
        Assert.assertEquals(geoTaxResult.TaxDetails[0].Rate, 0.065);
        Assert.assertEquals(geoTaxResult.TaxDetails[0].Tax, 0.65);
        Assert.assertEquals(geoTaxResult.TaxDetails[0].JurisName, "WASHINGTON");
        Assert.assertEquals(geoTaxResult.TaxDetails[0].TaxName, "WA STATE TAX");
        Assert.assertEquals(geoTaxResult.TaxDetails[1].Country, "US");
        Assert.assertEquals(geoTaxResult.TaxDetails[1].Region, "WA");
        Assert.assertEquals(geoTaxResult.TaxDetails[1].JurisType, "City");
        Assert.assertEquals(geoTaxResult.TaxDetails[1].Rate, 0.022);
        Assert.assertEquals(geoTaxResult.TaxDetails[1].Tax, 0.22);
        Assert.assertEquals(geoTaxResult.TaxDetails[1].JurisName, "BAINBRIDGE ISLAND");
        Assert.assertEquals(geoTaxResult.TaxDetails[1].TaxName, "WA CITY TAX");
        Assert.assertEquals(geoTaxResult.ResultCode, CommonResponse.SeverityLevel.Success);
        Assert.assertNull(geoTaxResult.Messages);
    }
}
