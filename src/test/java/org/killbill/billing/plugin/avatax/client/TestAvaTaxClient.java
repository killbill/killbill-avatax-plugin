/*
 * Copyright 2014-2018 Groupon, Inc
 * Copyright 2014-2018 The Billing Project, LLC
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
import java.util.Arrays;
import java.util.UUID;

import org.joda.time.DateTime;
import org.killbill.billing.plugin.avatax.AvaTaxRemoteTestBase;
import org.killbill.billing.plugin.avatax.client.model.Address;
import org.killbill.billing.plugin.avatax.client.model.CommonResponse;
import org.killbill.billing.plugin.avatax.client.model.DetailLevel;
import org.killbill.billing.plugin.avatax.client.model.DocType;
import org.killbill.billing.plugin.avatax.client.model.GeoTaxResult;
import org.killbill.billing.plugin.avatax.client.model.GetTaxRequest;
import org.killbill.billing.plugin.avatax.client.model.GetTaxResult;
import org.killbill.billing.plugin.avatax.client.model.Line;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAvaTaxClient extends AvaTaxRemoteTestBase {

    @Test(groups = "integration")
    public void testGetTax() throws Exception {
        final GetTaxRequest getTaxRequest = new GetTaxRequest();
        getTaxRequest.CustomerCode = UUID.randomUUID().toString();
        getTaxRequest.DocDate = new DateTime("2014-05-01").toDate();
        getTaxRequest.CompanyCode = companyCode;
        getTaxRequest.Client = "KILL_BILL";
        getTaxRequest.DocCode = UUID.randomUUID().toString();
        getTaxRequest.DetailLevel = DetailLevel.Diagnostic;
        getTaxRequest.CurrencyCode = "USD";
        // To see it in the UI
        getTaxRequest.DocType = DocType.SalesInvoice;
        getTaxRequest.Commit = true;

        final Address address = new Address();
        address.AddressCode = "01";
        address.Line1 = "45 Fremont Street";
        address.City = "San Francisco";
        address.Region = "CA";
        getTaxRequest.Addresses = new Address[]{address};

        final Line line = new Line();
        line.LineNo = UUID.randomUUID().toString();
        line.ItemCode = UUID.randomUUID().toString();
        line.Qty = new BigDecimal("1");
        line.Amount = new BigDecimal("100");
        line.OriginCode = address.AddressCode;
        line.DestinationCode = address.AddressCode;
        line.Description = UUID.randomUUID().toString();
        getTaxRequest.Lines = new Line[]{line};

        final GetTaxResult result = client.getTax(getTaxRequest);
        Assert.assertEquals(result.ResultCode, CommonResponse.SeverityLevel.Success, Arrays.toString(result.Messages));
        // Extra info due to DetailLevel.Diagnostic
        Assert.assertNotNull(result.Messages);
    }

    @Test(groups = "integration")
    public void testEstimateTax() throws Exception {
        final GeoTaxResult result = client.estimateTax(47.627935, -122.51702, 10.0);
        Assert.assertEquals(result.ResultCode, CommonResponse.SeverityLevel.Success, Arrays.toString(result.Messages));
        Assert.assertNull(result.Messages);
    }
}
