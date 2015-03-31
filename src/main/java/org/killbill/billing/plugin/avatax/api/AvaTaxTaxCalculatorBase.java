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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.invoice.PluginTaxCalculator;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxResponsesRecord;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public abstract class AvaTaxTaxCalculatorBase extends PluginTaxCalculator {

    public static final String PROPERTY_DRY_RUN = "dryRun";

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxTaxCalculatorBase.class);

    protected final AvaTaxDao dao;
    protected final Clock clock;

    public AvaTaxTaxCalculatorBase(final AvaTaxDao dao, final Clock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    @Override
    public List<InvoiceItem> compute(final Account account,
                                     final Invoice newInvoice,
                                     final Invoice invoice,
                                     final Map<UUID, InvoiceItem> taxableItems,
                                     final Map<UUID, Collection<InvoiceItem>> adjustmentItems,
                                     final Iterable<PluginProperty> pluginProperties,
                                     final UUID kbTenantId) {
        // Retrieve what we've already taxed (Tax Rates API) or sent (AvaTax)
        final Map<UUID, Set<UUID>> alreadyTaxedItemsWithAdjustments;
        final String originalInvoiceReferenceCode;
        try {
            final List<AvataxResponsesRecord> responses = dao.getSuccessfulResponses(invoice.getId(), kbTenantId);
            alreadyTaxedItemsWithAdjustments = dao.getTaxedItemsWithAdjustments(responses);
            originalInvoiceReferenceCode = responses.isEmpty() ? null : responses.get(0).getDocCode();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // For AvaTax, we can only send one type of document at a time (Sales or Return). In some cases, we need to send both, for example
        // in the case of repairs (adjustment for the original item, tax for the new item -- all generated items would be on the new invoice)
        final Map<UUID, InvoiceItem> salesTaxItems = new HashMap<UUID, InvoiceItem>();
        final Map<UUID, InvoiceItem> returnTaxItems = new HashMap<UUID, InvoiceItem>();
        final Map<UUID, Collection<InvoiceItem>> adjustmentItemsForReturnTaxItems = new HashMap<UUID, Collection<InvoiceItem>>();
        computeNewItemsToTaxAndExistingItemsToAdjust(taxableItems, adjustmentItems, alreadyTaxedItemsWithAdjustments, salesTaxItems, returnTaxItems, adjustmentItemsForReturnTaxItems);

        final ImmutableList.Builder<InvoiceItem> newInvoiceItemsBuilder = ImmutableList.<InvoiceItem>builder();
        if (!salesTaxItems.isEmpty()) {
            newInvoiceItemsBuilder.addAll(getTax(account, newInvoice, invoice, salesTaxItems, null, null, pluginProperties, kbTenantId));
        }
        if (!returnTaxItems.isEmpty()) {
            newInvoiceItemsBuilder.addAll(getTax(account, newInvoice, invoice, returnTaxItems, adjustmentItemsForReturnTaxItems, originalInvoiceReferenceCode, pluginProperties, kbTenantId));
        }
        return newInvoiceItemsBuilder.build();
    }

    private Iterable<InvoiceItem> getTax(final Account account,
                                         final Invoice newInvoice,
                                         final Invoice invoice,
                                         final Map<UUID, InvoiceItem> taxableItems,
                                         @Nullable final Map<UUID, Collection<InvoiceItem>> adjustmentItems,
                                         @Nullable final String originalInvoiceReferenceCode,
                                         final Iterable<PluginProperty> pluginProperties,
                                         final UUID kbTenantId) {
        // Keep track of the invoice items and adjustments we've already taxed (Tax Rates API) or sent (AvaTax)
        final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems = new HashMap<UUID, Iterable<InvoiceItem>>();
        if (adjustmentItems != null) {
            kbInvoiceItems.putAll(adjustmentItems);
        }
        for (final InvoiceItem taxableItem : taxableItems.values()) {
            if (kbInvoiceItems.get(taxableItem.getId()) == null) {
                kbInvoiceItems.put(taxableItem.getId(), ImmutableList.<InvoiceItem>of());
            }
        }
        // Don't use clock.getUTCToday(), see https://github.com/killbill/killbill-platform/issues/4
        final LocalDate taxItemsDate = newInvoice.getInvoiceDate();

        try {
            return buildInvoiceItems(account, newInvoice, invoice, taxableItems, adjustmentItems, originalInvoiceReferenceCode, pluginProperties, kbTenantId, kbInvoiceItems, taxItemsDate);
        } catch (final AvaTaxClientException e) {
            throw new RuntimeException(e);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Collection<InvoiceItem> buildInvoiceItems(final Account account,
                                                                 final Invoice newInvoice,
                                                                 final Invoice invoice,
                                                                 final Map<UUID, InvoiceItem> taxableItems,
                                                                 @Nullable final Map<UUID, Collection<InvoiceItem>> adjustmentItems,
                                                                 @Nullable final String originalInvoiceReferenceCode,
                                                                 final Iterable<PluginProperty> pluginProperties,
                                                                 final UUID kbTenantId,
                                                                 final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                                                                 final LocalDate utcToday) throws AvaTaxClientException, SQLException;
}
