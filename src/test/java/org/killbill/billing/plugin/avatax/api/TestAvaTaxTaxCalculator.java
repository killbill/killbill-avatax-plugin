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

package org.killbill.billing.plugin.avatax.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.avatax.AvaTaxRemoteTestBase;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.DefaultClock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TestAvaTaxTaxCalculator extends AvaTaxRemoteTestBase {

    private final Iterable<PluginProperty> pluginProperties = ImmutableList.<PluginProperty>of();
    private final UUID tenantId = UUID.randomUUID();

    private Account account;
    private Invoice newInvoice;
    private AvaTaxDao dao;
    private AvaTaxTaxCalculator calculator;

    @BeforeMethod(groups = "slow")
    public void setUp() throws Exception {
        account = TestUtils.buildAccount(Currency.USD, "45 Fremont Street", null, "San Francisco", "CA", "94105", "US");
        newInvoice = TestUtils.buildInvoice(account);
        dao = new AvaTaxDao(embeddedDB.getDataSource());
        calculator = new AvaTaxTaxCalculator(companyCode, client, dao, new DefaultClock());
    }

    // The test is quite long due to the state that needs to be created in AvaTax
    @Test(groups = "slow")
    public void testComputeItemsOverTime() throws Exception {
        final Invoice invoice = TestUtils.buildInvoice(account);
        final InvoiceItem taxableItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.EXTERNAL_CHARGE, new BigDecimal("100"), null);
        final InvoiceItem taxableItem2 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        final Map<UUID, InvoiceItem> taxableItems1 = ImmutableMap.<UUID, InvoiceItem>of(taxableItem1.getId(), taxableItem1,
                                                                                        taxableItem2.getId(), taxableItem2);
        final ImmutableMap<UUID, Collection<InvoiceItem>> initialAdjustmentItems = ImmutableMap.<UUID, Collection<InvoiceItem>>of();

        // Verify the initial state
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 0);

        // Compute the initial tax items
        final List<InvoiceItem> initialTaxItems = calculator.compute(account, newInvoice, invoice, taxableItems1, initialAdjustmentItems, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 1);

        // Check the created items
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(taxableItem1.getId(), InvoiceItemType.TAX,
                                                                 taxableItem2.getId(), InvoiceItemType.TAX), initialTaxItems);

        // Verify idempotency
        Assert.assertEquals(calculator.compute(account, newInvoice, invoice, taxableItems1, initialAdjustmentItems, pluginProperties, tenantId).size(), 0);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 1);

        // Compute a subsequent adjustment
        final InvoiceItem adjustment1ForInvoiceItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.ITEM_ADJ, BigDecimal.ONE.negate(), taxableItem1.getId());
        final ImmutableMap<UUID, Collection<InvoiceItem>> subsequentAdjustmentItems1 = ImmutableMap.<UUID, Collection<InvoiceItem>>of(taxableItem1.getId(), ImmutableList.<InvoiceItem>of(adjustment1ForInvoiceItem1));
        final List<InvoiceItem> adjustments1 = calculator.compute(account, newInvoice, invoice, taxableItems1, subsequentAdjustmentItems1, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 2);

        // Check the created item
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(taxableItem1.getId(), InvoiceItemType.ITEM_ADJ), adjustments1);

        // Verify idempotency
        Assert.assertEquals(calculator.compute(account, newInvoice, invoice, taxableItems1, subsequentAdjustmentItems1, pluginProperties, tenantId).size(), 0);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 2);

        // Compute a subsequent adjustment (with a new item on a new invoice this time, to simulate a repair)
        final InvoiceItem adjustment2ForInvoiceItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.ITEM_ADJ, BigDecimal.TEN.negate(), taxableItem1.getId());
        final Invoice adjustmentInvoice = TestUtils.buildInvoice(account);
        final InvoiceItem adjustment1ForInvoiceItem2 = TestUtils.buildInvoiceItem(adjustmentInvoice, InvoiceItemType.REPAIR_ADJ, BigDecimal.ONE.negate(), taxableItem2.getId());
        final InvoiceItem taxableItem3 = TestUtils.buildInvoiceItem(adjustmentInvoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        final Map<UUID, InvoiceItem> taxableItems2 = ImmutableMap.<UUID, InvoiceItem>of(taxableItem1.getId(), taxableItem1,
                                                                                        taxableItem2.getId(), taxableItem2,
                                                                                        taxableItem3.getId(), taxableItem3);
        final ImmutableMap<UUID, Collection<InvoiceItem>> subsequentAdjustmentItems2 = ImmutableMap.<UUID, Collection<InvoiceItem>>of(taxableItem1.getId(), ImmutableList.<InvoiceItem>of(adjustment2ForInvoiceItem1),
                                                                                                                                      taxableItem2.getId(), ImmutableList.<InvoiceItem>of(adjustment1ForInvoiceItem2));
        final List<InvoiceItem> adjustments2 = calculator.compute(account, newInvoice, invoice, taxableItems2, subsequentAdjustmentItems2, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 4);

        // Check the created items
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(taxableItem1.getId(), InvoiceItemType.ITEM_ADJ,
                                                                 taxableItem2.getId(), InvoiceItemType.ITEM_ADJ,
                                                                 taxableItem3.getId(), InvoiceItemType.TAX), adjustments2);

        // Verify idempotency
        Assert.assertEquals(calculator.compute(account, newInvoice, invoice, taxableItems2, subsequentAdjustmentItems2, pluginProperties, tenantId).size(), 0);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 4);
    }

    private void checkCreatedItems(final Map<UUID, InvoiceItemType> expectedInvoiceItemTypes, final Collection<InvoiceItem> createdItems) {
        // Times 2 here because there are two tax items generated each time, one for the state (California) and one for the county (San Francisco)
        Assert.assertEquals(createdItems.size(), expectedInvoiceItemTypes.size() * 2);
        for (final InvoiceItem invoiceItem : createdItems) {
            Assert.assertEquals(invoiceItem.getInvoiceId(), newInvoice.getId());
            Assert.assertEquals(invoiceItem.getInvoiceItemType(), expectedInvoiceItemTypes.get(invoiceItem.getLinkedItemId()));
            Assert.assertTrue(InvoiceItemType.TAX.equals(invoiceItem.getInvoiceItemType()) ? invoiceItem.getAmount().compareTo(BigDecimal.ZERO) > 0 : invoiceItem.getAmount().compareTo(BigDecimal.ZERO) < 0);
        }
    }
}
