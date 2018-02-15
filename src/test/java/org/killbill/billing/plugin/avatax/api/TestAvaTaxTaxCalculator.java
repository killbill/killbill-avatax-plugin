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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.Payment;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.api.invoice.PluginTaxCalculator;
import org.killbill.billing.plugin.avatax.AvaTaxRemoteTestBase;
import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;
import org.killbill.billing.plugin.avatax.core.AvaTaxConfigurationHandler;
import org.killbill.billing.plugin.avatax.core.TaxRatesConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TestAvaTaxTaxCalculator extends AvaTaxRemoteTestBase {

    private final Clock clock = new DefaultClock();
    private final Collection<PluginProperty> pluginProperties = new LinkedList<PluginProperty>();
    private final UUID tenantId = UUID.randomUUID();

    // Avalara requires to test at least two unique addresses for certification
    private Account account;
    private Account account2;
    private Account account3;
    private Invoice newInvoice;
    private Invoice newInvoice2;
    private AvaTaxDao dao;
    private OSGIKillbillAPI osgiKillbillAPI;
    private OSGIKillbillLogService osgiKillbillLogService;

    @BeforeMethod(groups = "integration")
    public void setUp() throws Exception {
        pluginProperties.add(new PluginProperty(TaxRatesTaxCalculator.RATE_TYPE, "County", false));
        pluginProperties.add(new PluginProperty(TaxRatesTaxCalculator.RATE_TYPE, "State", false));

        account = TestUtils.buildAccount(Currency.USD, "45 Fremont Street", null, "San Francisco", "CA", "94105", "US");
        account2 = TestUtils.buildAccount(Currency.USD, "118 N Clark St Ste 100", null, "San Francisco", "CA", "94105", "US");
        account3 = TestUtils.buildAccount(Currency.USD, "118 N Clark St Ste 100", null, "Chicago", "IL", "60602", "US");

        newInvoice = TestUtils.buildInvoice(account);
        newInvoice2 = TestUtils.buildInvoice(account2);

        dao = new AvaTaxDao(embeddedDB.getDataSource());

        osgiKillbillAPI = TestUtils.buildOSGIKillbillAPI(account);
        osgiKillbillLogService = TestUtils.buildLogService();
    }

    @Test(groups = "integration")
    public void testWithAvaTaxTaxCalculator() throws Exception {
        final AvaTaxConfigurationHandler avaTaxConfigurationHandler = new AvaTaxConfigurationHandler(AvaTaxActivator.PLUGIN_NAME, osgiKillbillAPI, osgiKillbillLogService);
        avaTaxConfigurationHandler.setDefaultConfigurable(client);
        final PluginTaxCalculator calculator = new AvaTaxTaxCalculator(avaTaxConfigurationHandler, dao, clock);
        testComputeItemsOverTime(calculator);
    }

    @Test(groups = "integration")
    public void testExemptionWithAvaTaxTaxCalculator() throws Exception {
        final AvaTaxConfigurationHandler avaTaxConfigurationHandler = new AvaTaxConfigurationHandler(AvaTaxActivator.PLUGIN_NAME, osgiKillbillAPI, osgiKillbillLogService);
        avaTaxConfigurationHandler.setDefaultConfigurable(client);
        final PluginTaxCalculator calculator = new AvaTaxTaxCalculator(avaTaxConfigurationHandler, dao, clock);

        final Collection<PluginProperty> exemptProperties = new LinkedList<PluginProperty>(pluginProperties);
        // A - FEDERAL GOV
        exemptProperties.add(new PluginProperty(AvaTaxTaxCalculator.CUSTOMER_USAGE_TYPE, "A", false));

        final Invoice invoice = TestUtils.buildInvoice(account);
        final InvoiceItem taxableItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.EXTERNAL_CHARGE, new BigDecimal("100"), null);
        final InvoiceItem taxableItem2 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        final Map<UUID, InvoiceItem> taxableItems1 = ImmutableMap.<UUID, InvoiceItem>of(taxableItem1.getId(), taxableItem1,
                                                                                        taxableItem2.getId(), taxableItem2);

        // Compute the tax items
        final List<InvoiceItem> initialTaxItems = calculator.compute(account, newInvoice, invoice, taxableItems1, ImmutableMap.<UUID, Collection<InvoiceItem>>of(), false, exemptProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 1);

        // Check the created items
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(), initialTaxItems, newInvoice);
    }

    @Test(groups = "integration")
    public void testInvoiceItemAdjustmentOnNewInvoiceWithAvaTaxTaxCalculator() throws Exception {
        final AvaTaxConfigurationHandler avaTaxConfigurationHandler = new AvaTaxConfigurationHandler(AvaTaxActivator.PLUGIN_NAME, osgiKillbillAPI, osgiKillbillLogService);
        avaTaxConfigurationHandler.setDefaultConfigurable(client);
        final PluginTaxCalculator calculator = new AvaTaxTaxCalculator(avaTaxConfigurationHandler, dao, clock);

        final Invoice invoice = TestUtils.buildInvoice(account3);
        final InvoiceItem taxableItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.EXTERNAL_CHARGE, new BigDecimal("100"), null);
        final InvoiceItem adjustment1ForInvoiceItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.ITEM_ADJ, BigDecimal.ONE.negate(), taxableItem1.getId());
        final Map<UUID, InvoiceItem> taxableItems1 = ImmutableMap.<UUID, InvoiceItem>of(taxableItem1.getId(), taxableItem1);
        final ImmutableMap<UUID, Collection<InvoiceItem>> adjustmentItems1 = ImmutableMap.<UUID, Collection<InvoiceItem>>of(taxableItem1.getId(), ImmutableList.<InvoiceItem>of(adjustment1ForInvoiceItem1));

        // Compute the tax items
        final List<InvoiceItem> initialTaxItems = calculator.compute(account3, invoice, invoice, taxableItems1, adjustmentItems1, false, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 2);

        // Check the created items
        Assert.assertEquals(initialTaxItems.size(), 8);
    }

    @Test(groups = "integration")
    public void testShippingChargeWithAvaTaxTaxCalculator() throws Exception {
        final AvaTaxConfigurationHandler avaTaxConfigurationHandler = new AvaTaxConfigurationHandler(AvaTaxActivator.PLUGIN_NAME, osgiKillbillAPI, osgiKillbillLogService);
        avaTaxConfigurationHandler.setDefaultConfigurable(client);
        final PluginTaxCalculator calculator = new AvaTaxTaxCalculator(avaTaxConfigurationHandler, dao, clock);

        final Invoice invoice = TestUtils.buildInvoice(account);
        final InvoiceItem taxableItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.EXTERNAL_CHARGE, new BigDecimal("100"), null);
        Mockito.when(taxableItem1.getUsageName()).thenReturn("FREIGHT");
        Mockito.when(taxableItem1.getDescription()).thenReturn("Shipping Charge");
        pluginProperties.add(new PluginProperty(String.format("%s_%s", AvaTaxTaxCalculator.TAX_CODE, taxableItem1.getId()), "FR", false));
        final InvoiceItem taxableItem2 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        Mockito.when(taxableItem2.getDescription()).thenReturn(UUID.randomUUID().toString());
        pluginProperties.add(new PluginProperty(String.format("%s_%s", AvaTaxTaxCalculator.TAX_CODE, taxableItem2.getId()), "DC010200", false));

        final Map<UUID, InvoiceItem> taxableItems1 = ImmutableMap.<UUID, InvoiceItem>of(taxableItem1.getId(), taxableItem1,
                                                                                        taxableItem2.getId(), taxableItem2);
        final ImmutableMap<UUID, Collection<InvoiceItem>> initialAdjustmentItems = ImmutableMap.<UUID, Collection<InvoiceItem>>of();

        // Compute the initial tax items
        final List<InvoiceItem> initialTaxItems = calculator.compute(account, newInvoice, invoice, taxableItems1, initialAdjustmentItems, false, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 1);

        // Check the created items
        Assert.assertEquals(initialTaxItems.size(), 0);
    }

    @Test(groups = "integration")
    public void testWithTaxRatesTaxCalculator() throws Exception {
        final TaxRatesConfigurationHandler taxRatesConfigurationHandler = new TaxRatesConfigurationHandler(AvaTaxActivator.PLUGIN_NAME, osgiKillbillAPI, osgiKillbillLogService);
        taxRatesConfigurationHandler.setDefaultConfigurable(taxRatesClient);
        final PluginTaxCalculator calculator = new TaxRatesTaxCalculator(taxRatesConfigurationHandler, dao, clock);
        testComputeItemsOverTime(calculator);
    }

    // The test is quite long due to the state that needs to be created in AvaTax
    private void testComputeItemsOverTime(final PluginTaxCalculator calculator) throws Exception {
        testComputeItemsOverTime(calculator, account, newInvoice);
        testComputeItemsOverTime(calculator, account2, newInvoice2);
    }

    private void testComputeItemsOverTime(final PluginTaxCalculator calculator, final Account account, final Invoice newInvoice) throws Exception {
        final Invoice invoice = TestUtils.buildInvoice(account);
        // Avalara requires testing multiple descriptions and multiple tax codes for certification
        final InvoiceItem taxableItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.EXTERNAL_CHARGE, new BigDecimal("100"), null);
        Mockito.when(taxableItem1.getDescription()).thenReturn(UUID.randomUUID().toString());
        pluginProperties.add(new PluginProperty(String.format("%s_%s", AvaTaxTaxCalculator.TAX_CODE, taxableItem1.getId()), "PC030100", false));
        final InvoiceItem taxableItem2 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.RECURRING, BigDecimal.TEN, null);
        Mockito.when(taxableItem2.getDescription()).thenReturn(UUID.randomUUID().toString());
        pluginProperties.add(new PluginProperty(String.format("%s_%s", AvaTaxTaxCalculator.TAX_CODE, taxableItem2.getId()), "PC040100", false));
        final Map<UUID, InvoiceItem> taxableItems1 = ImmutableMap.<UUID, InvoiceItem>of(taxableItem1.getId(), taxableItem1,
                                                                                        taxableItem2.getId(), taxableItem2);
        final ImmutableMap<UUID, Collection<InvoiceItem>> initialAdjustmentItems = ImmutableMap.<UUID, Collection<InvoiceItem>>of();

        // Verify the initial state
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 0);

        // Compute the initial tax items
        final List<InvoiceItem> initialTaxItems = calculator.compute(account, newInvoice, invoice, taxableItems1, initialAdjustmentItems, false, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 1);

        // Check the created items
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(taxableItem1.getId(), InvoiceItemType.TAX,
                                                                 taxableItem2.getId(), InvoiceItemType.TAX), initialTaxItems, newInvoice);

        // Verify idempotency
        Assert.assertEquals(calculator.compute(account, newInvoice, invoice, taxableItems1, initialAdjustmentItems, false, pluginProperties, tenantId).size(), 0);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 1);

        // Compute a subsequent adjustment
        final InvoiceItem adjustment1ForInvoiceItem1 = TestUtils.buildInvoiceItem(invoice, InvoiceItemType.ITEM_ADJ, BigDecimal.ONE.negate(), taxableItem1.getId());
        final ImmutableMap<UUID, Collection<InvoiceItem>> subsequentAdjustmentItems1 = ImmutableMap.<UUID, Collection<InvoiceItem>>of(taxableItem1.getId(), ImmutableList.<InvoiceItem>of(adjustment1ForInvoiceItem1));
        final List<InvoiceItem> adjustments1 = calculator.compute(account, newInvoice, invoice, taxableItems1, subsequentAdjustmentItems1, false, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 2);

        // Check the created item
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(taxableItem1.getId(), InvoiceItemType.TAX), adjustments1, newInvoice);

        // Verify idempotency
        Assert.assertEquals(calculator.compute(account, newInvoice, invoice, taxableItems1, subsequentAdjustmentItems1, false, pluginProperties, tenantId).size(), 0);
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
        final List<InvoiceItem> adjustments2 = calculator.compute(account, newInvoice, invoice, taxableItems2, subsequentAdjustmentItems2, false, pluginProperties, tenantId);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 4);

        // Check the created items
        checkCreatedItems(ImmutableMap.<UUID, InvoiceItemType>of(taxableItem1.getId(), InvoiceItemType.TAX,
                                                                 taxableItem2.getId(), InvoiceItemType.TAX,
                                                                 taxableItem3.getId(), InvoiceItemType.TAX), adjustments2, newInvoice);

        // Verify idempotency
        Assert.assertEquals(calculator.compute(account, newInvoice, invoice, taxableItems2, subsequentAdjustmentItems2, false, pluginProperties, tenantId).size(), 0);
        Assert.assertEquals(dao.getSuccessfulResponses(invoice.getId(), tenantId).size(), 4);
    }

    private void checkCreatedItems(final Map<UUID, InvoiceItemType> expectedInvoiceItemTypes, final Iterable<InvoiceItem> createdItems, final Invoice newInvoice) {
        for (final InvoiceItem invoiceItem : createdItems) {
            Assert.assertEquals(invoiceItem.getInvoiceId(), newInvoice.getId());
            Assert.assertEquals(invoiceItem.getInvoiceItemType(), expectedInvoiceItemTypes.get(invoiceItem.getLinkedItemId()));
        }
    }
}
