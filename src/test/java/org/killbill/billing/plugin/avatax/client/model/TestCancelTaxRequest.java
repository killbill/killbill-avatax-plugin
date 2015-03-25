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
import org.killbill.billing.plugin.avatax.client.model.CancelTaxRequest.CancelCode;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCancelTaxRequest extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testSerialization() throws Exception {
        final String request = "{\n" +
                               "  \"CancelCode\" : \"DocVoided\",\n" +
                               "  \"DocType\" : \"SalesInvoice\",\n" +
                               "  \"CompanyCode\" : \"APITrialCompany\",\n" +
                               "  \"DocCode\" : \"INV001\"\n" +
                               "}";

        final CancelTaxRequest cancelTaxRequest = new CancelTaxRequest();
        cancelTaxRequest.CompanyCode = "APITrialCompany";
        cancelTaxRequest.DocType = DocType.SalesInvoice;
        cancelTaxRequest.DocCode = "INV001";
        cancelTaxRequest.CancelCode = CancelCode.DocVoided;

        Assert.assertEquals(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cancelTaxRequest), request);
    }
}
