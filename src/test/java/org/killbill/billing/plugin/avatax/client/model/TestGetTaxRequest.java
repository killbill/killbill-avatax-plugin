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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.killbill.billing.plugin.avatax.AvaTaxTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestGetTaxRequest extends AvaTaxTestBase {

    @Test(groups = "fast")
    public void testSerialization() throws Exception {
        final String request = "{\n" +
                               "  \"DocDate\" : \"2014-01-01\",\n" +
                               "  \"CustomerCode\" : \"ABC4335\",\n" +
                               "  \"Addresses\" : [ {\n" +
                               "    \"AddressCode\" : \"01\",\n" +
                               "    \"Line1\" : \"45 Fremont Street\",\n" +
                               "    \"City\" : \"San Francisco\",\n" +
                               "    \"Region\" : \"CA\"\n" +
                               "  }, {\n" +
                               "    \"AddressCode\" : \"02\",\n" +
                               "    \"Line1\" : \"118 N Clark St\",\n" +
                               "    \"Line2\" : \"Suite 100\",\n" +
                               "    \"Line3\" : \"ATTN Accounts Payable\",\n" +
                               "    \"City\" : \"Chicago\",\n" +
                               "    \"Region\" : \"IL\",\n" +
                               "    \"PostalCode\" : \"60602\",\n" +
                               "    \"Country\" : \"US\"\n" +
                               "  }, {\n" +
                               "    \"AddressCode\" : \"03\",\n" +
                               "    \"Latitude\" : 47.627935,\n" +
                               "    \"Longitude\" : -122.51702\n" +
                               "  } ],\n" +
                               "  \"Lines\" : [ {\n" +
                               "    \"LineNo\" : \"01\",\n" +
                               "    \"DestinationCode\" : \"02\",\n" +
                               "    \"OriginCode\" : \"01\",\n" +
                               "    \"ItemCode\" : \"N543\",\n" +
                               "    \"Qty\" : 1,\n" +
                               "    \"Amount\" : 10,\n" +
                               "    \"TaxCode\" : \"NT\",\n" +
                               "    \"CustomerUsageType\" : \"L\",\n" +
                               "    \"Description\" : \"Red Size 7 Widget\",\n" +
                               "    \"Discounted\" : true,\n" +
                               "    \"TaxIncluded\" : true,\n" +
                               "    \"Ref1\" : \"ref123\",\n" +
                               "    \"Ref2\" : \"ref456\",\n" +
                               "    \"BusinessIdentificationNo\" : \"234243\"\n" +
                               "  }, {\n" +
                               "    \"LineNo\" : \"02\",\n" +
                               "    \"DestinationCode\" : \"03\",\n" +
                               "    \"OriginCode\" : \"01\",\n" +
                               "    \"ItemCode\" : \"T345\",\n" +
                               "    \"Qty\" : 3,\n" +
                               "    \"Amount\" : 150,\n" +
                               "    \"TaxCode\" : \"PC030147\",\n" +
                               "    \"Description\" : \"Size 10 Green Running Shoe\"\n" +
                               "  }, {\n" +
                               "    \"LineNo\" : \"02-FR\",\n" +
                               "    \"DestinationCode\" : \"03\",\n" +
                               "    \"OriginCode\" : \"01\",\n" +
                               "    \"ItemCode\" : \"FREIGHT\",\n" +
                               "    \"Qty\" : 1,\n" +
                               "    \"Amount\" : 15,\n" +
                               "    \"TaxCode\" : \"FR\",\n" +
                               "    \"Description\" : \"Shipping Charge\"\n" +
                               "  } ],\n" +
                               "  \"DocCode\" : \"INV001\",\n" +
                               "  \"DocType\" : \"SalesInvoice\",\n" +
                               "  \"CompanyCode\" : \"APITrialCompany\",\n" +
                               "  \"Commit\" : false,\n" +
                               "  \"DetailLevel\" : \"Tax\",\n" +
                               "  \"Client\" : \"AvaTaxSample\",\n" +
                               "  \"CustomerUsageType\" : \"G\",\n" +
                               "  \"ExemptionNo\" : \"12345\",\n" +
                               "  \"Discount\" : 50,\n" +
                               "  \"TaxOverride\" : {\n" +
                               "    \"TaxOverrideType\" : \"TaxDate\",\n" +
                               "    \"Reason\" : \"Adjustment for return\",\n" +
                               "    \"TaxAmount\" : \"0\",\n" +
                               "    \"TaxDate\" : \"2013-07-01\"\n" +
                               "  },\n" +
                               "  \"BusinessIdentificationNo\" : \"234243\",\n" +
                               "  \"PurchaseOrderNo\" : \"PO123456\",\n" +
                               "  \"ReferenceCode\" : \"ref123456\",\n" +
                               "  \"PosLaneCode\" : \"09\",\n" +
                               "  \"CurrencyCode\" : \"USD\"\n" +
                               "}";

        final GetTaxRequest getTaxRequest = new GetTaxRequest();
        // Document Level Elements
        getTaxRequest.CustomerCode = "ABC4335";
        getTaxRequest.DocDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2014-01-01");
        getTaxRequest.CompanyCode = "APITrialCompany";
        getTaxRequest.Client = "AvaTaxSample";
        getTaxRequest.DocCode = "INV001";
        getTaxRequest.DetailLevel = DetailLevel.Tax;
        getTaxRequest.Commit = false;
        getTaxRequest.DocType = DocType.SalesInvoice;
        getTaxRequest.CustomerUsageType = "G";
        getTaxRequest.ExemptionNo = "12345";
        getTaxRequest.BusinessIdentificationNo = "234243";
        getTaxRequest.Discount = new BigDecimal(50);
        getTaxRequest.TaxOverride = new TaxOverrideDef();
        getTaxRequest.TaxOverride.TaxOverrideType = "TaxDate";
        getTaxRequest.TaxOverride.Reason = "Adjustment for return";
        getTaxRequest.TaxOverride.TaxDate = "2013-07-01";
        getTaxRequest.TaxOverride.TaxAmount = "0";
        getTaxRequest.PurchaseOrderNo = "PO123456";
        getTaxRequest.ReferenceCode = "ref123456";
        getTaxRequest.PosLaneCode = "09";
        getTaxRequest.CurrencyCode = "USD";

        final Address address1 = new Address();
        address1.AddressCode = "01";
        address1.Line1 = "45 Fremont Street";
        address1.City = "San Francisco";
        address1.Region = "CA";

        final Address address2 = new Address();
        address2.AddressCode = "02";
        address2.Line1 = "118 N Clark St";
        address2.Line2 = "Suite 100";
        address2.Line3 = "ATTN Accounts Payable";
        address2.City = "Chicago";
        address2.Region = "IL";
        address2.Country = "US";
        address2.PostalCode = "60602";

        final Address address3 = new Address();
        address3.AddressCode = "03";
        address3.Latitude = new BigDecimal("47.627935");
        address3.Longitude = new BigDecimal("-122.51702");
        getTaxRequest.Addresses = new Address[]{address1, address2, address3};

        final Line line1 = new Line();
        line1.LineNo = "01";
        line1.ItemCode = "N543";
        line1.Qty = new BigDecimal(1);
        line1.Amount = new BigDecimal(10);
        line1.OriginCode = "01";
        line1.DestinationCode = "02";
        line1.Description = "Red Size 7 Widget";
        line1.TaxCode = "NT";
        line1.CustomerUsageType = "L";
        line1.BusinessIdentificationNo = "234243";
        line1.Discounted = true;
        line1.TaxIncluded = true;
        line1.Ref1 = "ref123";
        line1.Ref2 = "ref456";

        final Line line2 = new Line();
        line2.LineNo = "02";
        line2.ItemCode = "T345";
        line2.Qty = new BigDecimal("3");
        line2.Amount = new BigDecimal("150");
        line2.OriginCode = "01";
        line2.DestinationCode = "03";
        line2.Description = "Size 10 Green Running Shoe";
        line2.TaxCode = "PC030147";

        final Line line3 = new Line();
        line3.LineNo = "02-FR";
        line3.ItemCode = "FREIGHT";
        line3.Qty = new BigDecimal("1");
        line3.Amount = new BigDecimal("15");
        line3.OriginCode = "01";
        line3.DestinationCode = "03";
        line3.Description = "Shipping Charge";
        line3.TaxCode = "FR";

        getTaxRequest.Lines = new Line[]{line1, line2, line3};

        Assert.assertEquals(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getTaxRequest), request);
    }
}
