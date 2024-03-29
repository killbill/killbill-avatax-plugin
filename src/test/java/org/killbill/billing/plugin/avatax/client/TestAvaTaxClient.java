/*
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2021 Equinix, Inc
 * Copyright 2014-2021 The Billing Project, LLC
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

import java.math.BigDecimal;
import java.util.UUID;

import org.joda.time.DateTime;
import org.killbill.billing.plugin.avatax.AvaTaxRemoteTestBase;
import org.killbill.billing.plugin.avatax.client.model.AddressLocationInfo;
import org.killbill.billing.plugin.avatax.client.model.AddressesModel;
import org.killbill.billing.plugin.avatax.client.model.CreateTransactionModel;
import org.killbill.billing.plugin.avatax.client.model.DocType;
import org.killbill.billing.plugin.avatax.client.model.LineItemModel;
import org.killbill.billing.plugin.avatax.client.model.TransactionModel;
import org.testng.Assert;
import org.testng.annotations.Test;

// To debug the request: -Dorg.slf4j.simpleLogger.log.org=DEBUG
public class TestAvaTaxClient extends AvaTaxRemoteTestBase {

    @Test(groups = "integration")
    public void testCreateTransaction() throws Exception {
        final CreateTransactionModel createTransactionModel = new CreateTransactionModel();
        createTransactionModel.customerCode = UUID.randomUUID().toString();
        createTransactionModel.date = new DateTime("2014-05-01").toDate();
        createTransactionModel.companyCode = companyCode;
        createTransactionModel.code = UUID.randomUUID().toString();
        createTransactionModel.currencyCode = "USD";
        createTransactionModel.debugLevel = "Diagnostic";
        // To see it in the UI
        createTransactionModel.type = DocType.SalesInvoice;
        createTransactionModel.commit = false;

        final AddressLocationInfo addressLocationInfo = new AddressLocationInfo();
        addressLocationInfo.line1 = "45 Fremont Street";
        addressLocationInfo.city = "San Francisco";
        addressLocationInfo.region = "CA";
        createTransactionModel.addresses = new AddressesModel();
        createTransactionModel.addresses.singleLocation = addressLocationInfo;

        final LineItemModel lineItemModel = new LineItemModel();
        lineItemModel.number = UUID.randomUUID().toString();
        lineItemModel.itemCode = UUID.randomUUID().toString();
        lineItemModel.quantity = new BigDecimal("1");
        lineItemModel.amount = new BigDecimal("100");
        lineItemModel.addresses = createTransactionModel.addresses;
        lineItemModel.description = UUID.randomUUID().toString();
        createTransactionModel.lines = new LineItemModel[]{lineItemModel};

        final TransactionModel result = client.createTransaction(createTransactionModel);
        Assert.assertNotEquals(result.id, 0);
        Assert.assertNotNull(result.code);
        Assert.assertNotEquals(result.companyId, 0);
        // See https://help.avalara.com/Avalara_AvaTax_Update/Transaction_status_in_AvaTax
        Assert.assertEquals(result.status, "Saved");
        Assert.assertEquals(result.type, "SalesInvoice");
        Assert.assertEquals(result.lines.length, 1);
        Assert.assertTrue(result.lines[0].details.length >= 1);
        Assert.assertEquals(result.addresses.length, 1);
        Assert.assertTrue(result.summary.length >= 1);
        // Diagnostic level
        Assert.assertTrue(result.messages.length > 1);

        TransactionModel committedTransaction = client.commitTransaction(result.code);
        Assert.assertEquals(committedTransaction.status, "Committed");

        committedTransaction = client.getTransactionByCode(result.code);
        Assert.assertEquals(committedTransaction.status, "Committed");

        TransactionModel voidedTransaction = client.voidTransaction(result.code);
        Assert.assertEquals(voidedTransaction.status, "Cancelled");

        voidedTransaction = client.getTransactionByCode(result.code);
        Assert.assertEquals(voidedTransaction.status, "Cancelled");
    }
}
