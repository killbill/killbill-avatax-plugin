/*
 * Copyright 2015-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2015-2020 The Billing Project, LLC
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
import org.killbill.billing.plugin.avatax.AvaTaxTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTransactionModel extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testDeserialization() throws Exception {
        final String response = "{\n" +
                                "    \"id\": 123456789,\n" +
                                "    \"code\": \"21b4addf-e086-4a0b-9341-e4a0bd7f118d\",\n" +
                                "    \"companyId\": 12345,\n" +
                                "    \"date\": \"2020-05-22\",\n" +
                                "    \"status\": \"Committed\",\n" +
                                "    \"type\": \"SalesInvoice\",\n" +
                                "    \"currencyCode\": \"USD\",\n" +
                                "    \"entityUseCode\": \"\",\n" +
                                "    \"customerVendorCode\": \"ABC\",\n" +
                                "    \"customerCode\": \"ABC\",\n" +
                                "    \"exemptNo\": \"\",\n" +
                                "    \"reconciled\": true,\n" +
                                "    \"locationCode\": \"DEFAULT\",\n" +
                                "    \"salespersonCode\": \"DEF\",\n" +
                                "    \"taxOverrideType\": \"None\",\n" +
                                "    \"taxOverrideAmount\": 0,\n" +
                                "    \"taxOverrideReason\": \"\",\n" +
                                "    \"totalAmount\": 1000,\n" +
                                "    \"totalExempt\": 0,\n" +
                                "    \"totalDiscount\": 0,\n" +
                                "    \"totalTax\": 62.5,\n" +
                                "    \"totalTaxable\": 1000,\n" +
                                "    \"totalTaxCalculated\": 62.5,\n" +
                                "    \"adjustmentReason\": \"NotAdjusted\",\n" +
                                "    \"adjustmentDescription\": \"\",\n" +
                                "    \"locked\": false,\n" +
                                "    \"region\": \"CA\",\n" +
                                "    \"country\": \"US\",\n" +
                                "    \"version\": 0,\n" +
                                "    \"originAddressId\": 123456789,\n" +
                                "    \"destinationAddressId\": 123456789,\n" +
                                "    \"isSellerImporterOfRecord\": false,\n" +
                                "    \"description\": \"Yarn\",\n" +
                                "    \"taxDate\": \"2020-05-22T00:00:00+00:00\",\n" +
                                "    \"lines\": [\n" +
                                "        {\n" +
                                "            \"id\": 123456789,\n" +
                                "            \"transactionId\": 123456789,\n" +
                                "            \"lineNumber\": \"1\",\n" +
                                "            \"boundaryOverrideId\": 0,\n" +
                                "            \"entityUseCode\": \"\",\n" +
                                "            \"description\": \"Yarn\",\n" +
                                "            \"destinationAddressId\": 12345,\n" +
                                "            \"originAddressId\": 123456789,\n" +
                                "            \"discountAmount\": 100,\n" +
                                "            \"discountTypeId\": 0,\n" +
                                "            \"exemptAmount\": 0,\n" +
                                "            \"exemptCertId\": 0,\n" +
                                "            \"exemptNo\": \"\",\n" +
                                "            \"isItemTaxable\": true,\n" +
                                "            \"isSSTP\": false,\n" +
                                "            \"itemCode\": \"116292\",\n" +
                                "            \"lineAmount\": 1000,\n" +
                                "            \"quantity\": 1,\n" +
                                "            \"ref1\": \"Note: Deliver to Bob\",\n" +
                                "            \"reportingDate\": \"2020-05-22\",\n" +
                                "            \"revAccount\": \"\",\n" +
                                "            \"sourcing\": \"Destination\",\n" +
                                "            \"tax\": 62.5,\n" +
                                "            \"taxableAmount\": 1000,\n" +
                                "            \"taxCalculated\": 62.5,\n" +
                                "            \"taxCode\": \"PS081282\",\n" +
                                "            \"taxDate\": \"2020-05-22\",\n" +
                                "            \"taxEngine\": \"\",\n" +
                                "            \"taxOverrideType\": \"None\",\n" +
                                "            \"taxOverrideAmount\": 0,\n" +
                                "            \"taxOverrideReason\": \"\",\n" +
                                "            \"taxIncluded\": false,\n" +
                                "            \"details\": [\n" +
                                "                {\n" +
                                "                    \"id\": 123456789,\n" +
                                "                    \"transactionLineId\": 123456789,\n" +
                                "                    \"transactionId\": 123456789,\n" +
                                "                    \"addressId\": 12345,\n" +
                                "                    \"country\": \"US\",\n" +
                                "                    \"region\": \"CA\",\n" +
                                "                    \"stateFIPS\": \"06\",\n" +
                                "                    \"exemptAmount\": 0,\n" +
                                "                    \"exemptReasonId\": 4,\n" +
                                "                    \"inState\": false,\n" +
                                "                    \"jurisCode\": \"06\",\n" +
                                "                    \"jurisName\": \"CALIFORNIA\",\n" +
                                "                    \"jurisdictionId\": 5000531,\n" +
                                "                    \"signatureCode\": \"AGAM\",\n" +
                                "                    \"stateAssignedNo\": \"\",\n" +
                                "                    \"jurisType\": \"STA\",\n" +
                                "                    \"nonTaxableAmount\": 0,\n" +
                                "                    \"nonTaxableRuleId\": 0,\n" +
                                "                    \"nonTaxableType\": \"BaseRule\",\n" +
                                "                    \"rate\": 0.0625,\n" +
                                "                    \"rateRuleId\": 1321915,\n" +
                                "                    \"rateSourceId\": 3,\n" +
                                "                    \"serCode\": \"\",\n" +
                                "                    \"sourcing\": \"Destination\",\n" +
                                "                    \"tax\": 62.5,\n" +
                                "                    \"taxableAmount\": 1000,\n" +
                                "                    \"taxType\": \"Sales\",\n" +
                                "                    \"taxName\": \"CA STATE TAX\",\n" +
                                "                    \"taxAuthorityTypeId\": 45,\n" +
                                "                    \"taxRegionId\": 2127184,\n" +
                                "                    \"taxCalculated\": 62.5,\n" +
                                "                    \"taxOverride\": 0,\n" +
                                "                    \"rateType\": \"General\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"vatNumberTypeId\": 0\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"addresses\": [\n" +
                                "        {\n" +
                                "            \"id\": 0,\n" +
                                "            \"transactionId\": 0,\n" +
                                "            \"boundaryLevel\": \"Address\",\n" +
                                "            \"line1\": \"100 Ravine Lane Northeast #220\",\n" +
                                "            \"city\": \"Bainbridge Island\",\n" +
                                "            \"region\": \"WA\",\n" +
                                "            \"postalCode\": \"98110\",\n" +
                                "            \"country\": \"US\",\n" +
                                "            \"taxRegionId\": 0\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"taxDetailsByTaxType\": [\n" +
                                "        {\n" +
                                "            \"taxType\": \"SalesAndUse\",\n" +
                                "            \"totalTaxable\": 100,\n" +
                                "            \"totalExempt\": 0.05,\n" +
                                "            \"totalNonTaxable\": 0,\n" +
                                "            \"totalTax\": 0.625\n" +
                                "        }\n" +
                                "    ]\n" +
                                "}\n";
        final TransactionModel transactionModel = mapper.readValue(response, TransactionModel.class);
        Assert.assertEquals(transactionModel.code, "21b4addf-e086-4a0b-9341-e4a0bd7f118d");
        Assert.assertEquals(transactionModel.date, new LocalDate("2020-05-22").toDate());
        Assert.assertEquals(transactionModel.addresses.length, 1);
        Assert.assertEquals(transactionModel.taxDate, new LocalDate("2020-05-22").toDate());
        Assert.assertEquals(transactionModel.lines.length, 1);
        Assert.assertEquals(transactionModel.totalAmount, 1000.0);
        Assert.assertEquals(transactionModel.totalDiscount, 0.0);
        Assert.assertEquals(transactionModel.totalExempt, 0.0);
        Assert.assertEquals(transactionModel.totalTax, 62.5);
        Assert.assertEquals(transactionModel.totalTaxCalculated, 62.5);
        Assert.assertEquals(transactionModel.totalTaxable, 1000.0);
    }
}
