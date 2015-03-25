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

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.killbill.billing.plugin.avatax.AvaTaxTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGetTaxResult extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testDeserialization() throws Exception {
        final String response = "{\n" +
                                "    \"DocCode\": \"INV001\",\n" +
                                "    \"DocDate\": \"2014-01-01\",\n" +
                                "    \"ResultCode\": \"Success\",\n" +
                                "    \"TaxAddresses\": [\n" +
                                "        {\n" +
                                "            \"Address\": \"118 N Clark St\",\n" +
                                "            \"AddressCode\": \"02\",\n" +
                                "            \"City\": \"Chicago\",\n" +
                                "            \"Country\": \"US\",\n" +
                                "            \"JurisCode\": \"1703114000\",\n" +
                                "            \"Latitude\": \"41.884132\",\n" +
                                "            \"Longitude\": \"-87.631048\",\n" +
                                "            \"PostalCode\": \"60602\",\n" +
                                "            \"Region\": \"IL\",\n" +
                                "            \"TaxRegionId\": \"2062953\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"Address\": \"45 Fremont Street\",\n" +
                                "            \"AddressCode\": \"01\",\n" +
                                "            \"City\": \"San Francisco\",\n" +
                                "            \"Country\": \"US\",\n" +
                                "            \"JurisCode\": \"0607500000\",\n" +
                                "            \"Latitude\": \"37.791119\",\n" +
                                "            \"Longitude\": \"-122.397366\",\n" +
                                "            \"Region\": \"CA\",\n" +
                                "            \"TaxRegionId\": \"2113460\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"AddressCode\": \"03\",\n" +
                                "            \"Country\": \"US\",\n" +
                                "            \"JurisCode\": \"5303503736\",\n" +
                                "            \"Latitude\": \"47.627935\",\n" +
                                "            \"Longitude\": \"-122.51702\",\n" +
                                "            \"Region\": \"WA\",\n" +
                                "            \"TaxRegionId\": \"2109716\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"TaxDate\": \"2013-07-01\",\n" +
                                "    \"TaxLines\": [\n" +
                                "        {\n" +
                                "            \"BoundaryLevel\": \"Address\",\n" +
                                "            \"Discount\": \"10\",\n" +
                                "            \"Exemption\": \"0\",\n" +
                                "            \"LineNo\": \"01\",\n" +
                                "            \"Rate\": \"0.092500\",\n" +
                                "            \"Tax\": \"0\",\n" +
                                "            \"TaxCalculated\": \"0\",\n" +
                                "            \"TaxCode\": \"NT\",\n" +
                                "            \"TaxDetails\": [\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"17\",\n" +
                                "                    \"JurisName\": \"ILLINOIS\",\n" +
                                "                    \"JurisType\": \"State\",\n" +
                                "                    \"Rate\": \"0.062500\",\n" +
                                "                    \"Region\": \"IL\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"IL STATE TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"031\",\n" +
                                "                    \"JurisName\": \"COOK\",\n" +
                                "                    \"JurisType\": \"County\",\n" +
                                "                    \"Rate\": \"0.007500\",\n" +
                                "                    \"Region\": \"IL\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"IL COUNTY TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"14000\",\n" +
                                "                    \"JurisName\": \"CHICAGO\",\n" +
                                "                    \"JurisType\": \"City\",\n" +
                                "                    \"Rate\": \"0.012500\",\n" +
                                "                    \"Region\": \"IL\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"IL CITY TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"AQOF\",\n" +
                                "                    \"JurisName\": \"REGIONAL TRANSPORT. AUTHORITY (RTA)\",\n" +
                                "                    \"JurisType\": \"Special\",\n" +
                                "                    \"Rate\": \"0.010000\",\n" +
                                "                    \"Region\": \"IL\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"IL SPECIAL TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"Taxability\": \"false\",\n" +
                                "            \"Taxable\": \"0\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"BoundaryLevel\": \"Zip5\",\n" +
                                "            \"Discount\": \"0\",\n" +
                                "            \"Exemption\": \"150\",\n" +
                                "            \"LineNo\": \"02\",\n" +
                                "            \"Rate\": \"0.086000\",\n" +
                                "            \"Tax\": \"0\",\n" +
                                "            \"TaxCalculated\": \"0\",\n" +
                                "            \"TaxCode\": \"PC030147\",\n" +
                                "            \"TaxDetails\": [\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"53\",\n" +
                                "                    \"JurisName\": \"WASHINGTON\",\n" +
                                "                    \"JurisType\": \"State\",\n" +
                                "                    \"Rate\": \"0.065000\",\n" +
                                "                    \"Region\": \"WA\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"WA STATE TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"03736\",\n" +
                                "                    \"JurisName\": \"BAINBRIDGE ISLAND\",\n" +
                                "                    \"JurisType\": \"City\",\n" +
                                "                    \"Rate\": \"0.021000\",\n" +
                                "                    \"Region\": \"WA\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"WA CITY TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"Taxability\": \"true\",\n" +
                                "            \"Taxable\": \"0\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"BoundaryLevel\": \"Zip5\",\n" +
                                "            \"Discount\": \"0\",\n" +
                                "            \"Exemption\": \"15\",\n" +
                                "            \"LineNo\": \"02-FR\",\n" +
                                "            \"Rate\": \"0.086000\",\n" +
                                "            \"Tax\": \"0\",\n" +
                                "            \"TaxCalculated\": \"0\",\n" +
                                "            \"TaxCode\": \"FR\",\n" +
                                "            \"TaxDetails\": [\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"53\",\n" +
                                "                    \"JurisName\": \"WASHINGTON\",\n" +
                                "                    \"JurisType\": \"State\",\n" +
                                "                    \"Rate\": \"0.065000\",\n" +
                                "                    \"Region\": \"WA\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"WA STATE TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                },\n" +
                                "                {\n" +
                                "                    \"Country\": \"US\",\n" +
                                "                    \"JurisCode\": \"03736\",\n" +
                                "                    \"JurisName\": \"BAINBRIDGE ISLAND\",\n" +
                                "                    \"JurisType\": \"City\",\n" +
                                "                    \"Rate\": \"0.021000\",\n" +
                                "                    \"Region\": \"WA\",\n" +
                                "                    \"Tax\": \"0\",\n" +
                                "                    \"TaxName\": \"WA CITY TAX\",\n" +
                                "                    \"Taxable\": \"0\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"Taxability\": \"true\",\n" +
                                "            \"Taxable\": \"0\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"Timestamp\": \"2015-02-26T04:56:58.0496334Z\",\n" +
                                "    \"TotalAmount\": \"175\",\n" +
                                "    \"TotalDiscount\": \"10\",\n" +
                                "    \"TotalExemption\": \"165\",\n" +
                                "    \"TotalTax\": \"0\",\n" +
                                "    \"TotalTaxCalculated\": \"0\",\n" +
                                "    \"TotalTaxable\": \"0\"\n" +
                                "}";
        final GetTaxResult getTaxResult = mapper.readValue(response, GetTaxResult.class);
        Assert.assertEquals(getTaxResult.DocCode, "INV001");
        Assert.assertEquals(getTaxResult.DocDate, new LocalDate("2014-01-01").toDate());
        Assert.assertEquals(getTaxResult.ResultCode, CommonResponse.SeverityLevel.Success);
        Assert.assertEquals(getTaxResult.TaxAddresses.length, 3);
        Assert.assertEquals(getTaxResult.TaxDate, new LocalDate("2013-07-01").toDate());
        Assert.assertEquals(getTaxResult.TaxLines.length, 3);
        Assert.assertEquals(getTaxResult.Timestamp, ISODateTimeFormat.dateTimeParser().parseDateTime("2015-02-26T04:56:58Z").toDate());
        Assert.assertEquals(getTaxResult.TotalAmount, 175.0);
        Assert.assertEquals(getTaxResult.TotalDiscount, 10.0);
        Assert.assertEquals(getTaxResult.TotalExemption, 165.0);
        Assert.assertEquals(getTaxResult.TotalTax, 0.0);
        Assert.assertEquals(getTaxResult.TotalTaxCalculated, 0.0);
        Assert.assertEquals(getTaxResult.TotalTaxable, 0.0);
    }
}
