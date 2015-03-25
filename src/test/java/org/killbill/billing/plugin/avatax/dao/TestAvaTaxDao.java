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

package org.killbill.billing.plugin.avatax.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.TestWithEmbeddedDBBase;
import org.killbill.billing.plugin.avatax.client.model.CommonResponse;
import org.killbill.billing.plugin.avatax.client.model.GetTaxResult;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxResponsesRecord;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TestAvaTaxDao extends TestWithEmbeddedDBBase {

    private AvaTaxDao dao;

    @BeforeMethod(groups = "slow")
    public void setUp() throws Exception {
        dao = new AvaTaxDao(embeddedDB.getDataSource());
    }

    @Test(groups = "slow")
    public void testCreateReadResponses() throws Exception {
        final Account account = TestUtils.buildAccount(Currency.USD, "US");
        final Invoice invoice = TestUtils.buildInvoice(account);
        final UUID kbAccountId = account.getId();
        final UUID kbInvoiceId = invoice.getId();
        final UUID kbTenantId = UUID.randomUUID();

        final InvoiceItem taxableItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        final InvoiceItem adjustmentItem11 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.ITEM_ADJ, BigDecimal.ONE.negate(), taxableItem1.getId());
        final InvoiceItem adjustmentItem12 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.REPAIR_ADJ, BigDecimal.ONE.negate(), taxableItem1.getId());

        final InvoiceItem taxableItem2 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        final InvoiceItem adjustmentItem21 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.REPAIR_ADJ, BigDecimal.ONE.negate(), taxableItem2.getId());

        final GetTaxResult taxResultS1 = new GetTaxResult();
        taxResultS1.DocCode = UUID.randomUUID().toString();
        taxResultS1.ResultCode = CommonResponse.SeverityLevel.Success;

        final GetTaxResult taxResultS2 = new GetTaxResult();
        taxResultS2.DocCode = UUID.randomUUID().toString();
        taxResultS2.ResultCode = CommonResponse.SeverityLevel.Success;

        final GetTaxResult taxResultE = new GetTaxResult();
        taxResultE.DocCode = UUID.randomUUID().toString();
        taxResultE.ResultCode = CommonResponse.SeverityLevel.Error;

        // Success
        dao.addResponse(kbAccountId,
                        kbInvoiceId,
                        ImmutableMap.<UUID, Iterable<InvoiceItem>>of(taxableItem1.getId(), ImmutableList.<InvoiceItem>of(),
                                                                     taxableItem2.getId(), ImmutableList.<InvoiceItem>of()),
                        taxResultS1,
                        new DateTime(DateTimeZone.UTC),
                        kbTenantId);
        // Success (subsequent adjustments)
        dao.addResponse(kbAccountId,
                        kbInvoiceId,
                        ImmutableMap.<UUID, Iterable<InvoiceItem>>of(taxableItem1.getId(), ImmutableList.<InvoiceItem>of(adjustmentItem11, adjustmentItem12),
                                                                     taxableItem2.getId(), ImmutableList.<InvoiceItem>of(adjustmentItem21)),
                        taxResultS2,
                        new DateTime(DateTimeZone.UTC),
                        kbTenantId);
        // Error
        dao.addResponse(kbAccountId,
                        kbInvoiceId,
                        ImmutableMap.<UUID, Iterable<InvoiceItem>>of(),
                        taxResultE,
                        new DateTime(DateTimeZone.UTC),
                        kbTenantId);
        // Other invoice
        dao.addResponse(kbAccountId,
                        UUID.randomUUID(),
                        ImmutableMap.<UUID, Iterable<InvoiceItem>>of(),
                        new GetTaxResult(),
                        new DateTime(DateTimeZone.UTC),
                        kbTenantId);

        final List<AvataxResponsesRecord> responses = dao.getSuccessfulResponses(kbInvoiceId, kbTenantId);
        Assert.assertEquals(responses.size(), 2);
        Assert.assertEquals(responses.get(0).getDocCode(), taxResultS1.DocCode);
        Assert.assertEquals(responses.get(1).getDocCode(), taxResultS2.DocCode);

        final Map<UUID, Set<UUID>> kbInvoiceItems = dao.getTaxedItemsWithAdjustments(responses);
        Assert.assertEquals(kbInvoiceItems.size(), 2);
        Assert.assertEquals(kbInvoiceItems.get(taxableItem1.getId()).size(), 2);
        Assert.assertTrue(kbInvoiceItems.get(taxableItem1.getId()).contains(adjustmentItem11.getId()));
        Assert.assertTrue(kbInvoiceItems.get(taxableItem1.getId()).contains(adjustmentItem12.getId()));
        Assert.assertEquals(kbInvoiceItems.get(taxableItem2.getId()).size(), 1);
        Assert.assertTrue(kbInvoiceItems.get(taxableItem2.getId()).contains(adjustmentItem21.getId()));
    }
}
