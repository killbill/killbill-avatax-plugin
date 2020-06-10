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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.killbill.billing.plugin.avatax.AvaTaxTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCreateTransactionModel extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testSerialization() throws Exception {
        final String request = "{\n" +
                               "  \"date\" : \"2020-05-22\",\n" +
                               "  \"customerCode\" : \"ABC\",\n" +
                               "  \"addresses\" : {\n" +
                               "    \"singleLocation\" : {\n" +
                               "      \"line1\" : \"2000 Main Street\",\n" +
                               "      \"city\" : \"Irvine\",\n" +
                               "      \"region\" : \"CA\",\n" +
                               "      \"postalCode\" : \"92614\",\n" +
                               "      \"country\" : \"US\"\n" +
                               "    }\n" +
                               "  },\n" +
                               "  \"lines\" : [ {\n" +
                               "    \"number\" : \"1\",\n" +
                               "    \"itemCode\" : \"Y0001\",\n" +
                               "    \"quantity\" : 1,\n" +
                               "    \"amount\" : 100,\n" +
                               "    \"taxCode\" : \"PS081282\",\n" +
                               "    \"description\" : \"Yarn\"\n" +
                               "  } ],\n" +
                               "  \"type\" : \"SalesInvoice\",\n" +
                               "  \"companyCode\" : \"DEFAULT\",\n" +
                               "  \"commit\" : true,\n" +
                               "  \"purchaseOrderNo\" : \"2020-05-22-001\",\n" +
                               "  \"currencyCode\" : \"USD\",\n" +
                               "  \"description\" : \"Yarn\"\n" +
                               "}";

        final CreateTransactionModel createTransactionModel = new CreateTransactionModel();
        createTransactionModel.type = DocType.SalesInvoice;
        createTransactionModel.companyCode = "DEFAULT";
        createTransactionModel.date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2020-05-22");
        createTransactionModel.customerCode = "ABC";
        createTransactionModel.purchaseOrderNo = "2020-05-22-001";
        createTransactionModel.commit = true;
        createTransactionModel.currencyCode = "USD";
        createTransactionModel.description = "Yarn";

        final LineItemModel lineItemModel1 = new LineItemModel();
        lineItemModel1.number = "1";
        lineItemModel1.quantity = new BigDecimal(1);
        lineItemModel1.amount = new BigDecimal(100);
        lineItemModel1.taxCode = "PS081282";
        lineItemModel1.itemCode = "Y0001";
        lineItemModel1.description = "Yarn";

        createTransactionModel.lines = new LineItemModel[]{lineItemModel1};

        final AddressLocationInfo addressLocationInfo1 = new AddressLocationInfo();
        addressLocationInfo1.line1 = "2000 Main Street";
        addressLocationInfo1.city = "Irvine";
        addressLocationInfo1.region = "CA";
        addressLocationInfo1.country = "US";
        addressLocationInfo1.postalCode = "92614";

        createTransactionModel.addresses = new AddressesModel();
        createTransactionModel.addresses.singleLocation = addressLocationInfo1;

        Assert.assertEquals(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(createTransactionModel), request);
    }
}
