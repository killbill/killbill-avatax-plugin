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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.jooq.impl.DSL;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.plugin.avatax.client.model.CommonResponse;
import org.killbill.billing.plugin.avatax.client.model.GetTaxResult;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxResponsesRecord;
import org.killbill.billing.plugin.dao.PluginDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import static org.killbill.billing.plugin.avatax.dao.gen.tables.AvataxResponses.AVATAX_RESPONSES;

public class AvaTaxDao extends PluginDao {

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxDao.class);

    public AvaTaxDao(final DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    public void addResponse(final UUID kbAccountId,
                            final UUID kbInvoiceId,
                            final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                            final GetTaxResult taxResult,
                            final DateTime utcNow,
                            final UUID kbTenantId) throws SQLException {
        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                           .insertInto(AVATAX_RESPONSES,
                                       AVATAX_RESPONSES.KB_ACCOUNT_ID,
                                       AVATAX_RESPONSES.KB_INVOICE_ID,
                                       AVATAX_RESPONSES.KB_INVOICE_ITEM_IDS,
                                       AVATAX_RESPONSES.DOC_CODE,
                                       AVATAX_RESPONSES.DOC_DATE,
                                       AVATAX_RESPONSES.TIMESTAMP,
                                       AVATAX_RESPONSES.TOTAL_AMOUNT,
                                       AVATAX_RESPONSES.TOTAL_DISCOUNT,
                                       AVATAX_RESPONSES.TOTAL_EXEMPTION,
                                       AVATAX_RESPONSES.TOTAL_TAXABLE,
                                       AVATAX_RESPONSES.TOTAL_TAX,
                                       AVATAX_RESPONSES.TOTAL_TAX_CALCULATED,
                                       AVATAX_RESPONSES.TAX_DATE,
                                       AVATAX_RESPONSES.TAX_LINES,
                                       AVATAX_RESPONSES.TAX_SUMMARY,
                                       AVATAX_RESPONSES.TAX_ADDRESSES,
                                       AVATAX_RESPONSES.RESULT_CODE,
                                       AVATAX_RESPONSES.MESSAGES,
                                       AVATAX_RESPONSES.ADDITIONAL_DATA,
                                       AVATAX_RESPONSES.CREATED_DATE,
                                       AVATAX_RESPONSES.KB_TENANT_ID)
                           .values(kbAccountId.toString(),
                                   kbInvoiceId.toString(),
                                   kbInvoiceItemsIdsAsString(kbInvoiceItems),
                                   taxResult.DocCode,
                                   toTimestamp(taxResult.DocDate),
                                   toTimestamp(taxResult.Timestamp),
                                   BigDecimal.valueOf(taxResult.TotalAmount),
                                   BigDecimal.valueOf(taxResult.TotalDiscount),
                                   BigDecimal.valueOf(taxResult.TotalExemption),
                                   BigDecimal.valueOf(taxResult.TotalTaxable),
                                   BigDecimal.valueOf(taxResult.TotalTax),
                                   BigDecimal.valueOf(taxResult.TotalTaxCalculated),
                                   toTimestamp(taxResult.TaxDate),
                                   asString(taxResult.TaxLines),
                                   asString(taxResult.TaxSummary),
                                   asString(taxResult.TaxAddresses),
                                   taxResult.ResultCode == null ? null : taxResult.ResultCode.name(),
                                   asString(taxResult.Messages),
                                   null,
                                   toTimestamp(utcNow),
                                   kbTenantId.toString())
                           .execute();
                        return null;
                    }
                });
    }

    // TODO Check committed docs only - but DocStatus isn't returned?
    public List<AvataxResponsesRecord> getSuccessfulResponses(final UUID invoiceId, final UUID kbTenantId) throws SQLException {
        return execute(dataSource.getConnection(),
                       new WithConnectionCallback<List<AvataxResponsesRecord>>() {
                           @Override
                           public List<AvataxResponsesRecord> withConnection(final Connection conn) throws SQLException {
                               return DSL.using(conn, dialect, settings)
                                         .selectFrom(AVATAX_RESPONSES)
                                         .where(AVATAX_RESPONSES.KB_INVOICE_ID.equal(invoiceId.toString()))
                                         .and(AVATAX_RESPONSES.RESULT_CODE.equal(CommonResponse.SeverityLevel.Success.name()))
                                         .and(AVATAX_RESPONSES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                         .orderBy(AVATAX_RESPONSES.RECORD_ID.asc())
                                         .fetch();
                           }
                       });
    }

    /**
     * Retrieve all taxed items (successfully committed in AvaTax), along with the associated adjustments that have already
     * been taken into account.
     *
     * @param responses existing AvaTax responses
     * @return Mapping between taxed invoice item ids and associated adjustments (if any)
     */
    public Map<UUID, Set<UUID>> getTaxedItemsWithAdjustments(final Iterable<AvataxResponsesRecord> responses) {
        final Map<UUID, Set<UUID>> kbInvoiceItemsIds = new HashMap<UUID, Set<UUID>>();
        for (final AvataxResponsesRecord response : responses) {
            try {
                kbInvoiceItemsIdsFromString(response.getKbInvoiceItemIds(), kbInvoiceItemsIds);
            } catch (final IOException e) {
                logger.warn("Corrupted entry for response record_id {}: {}", response.getRecordId(), response.getKbInvoiceItemIds());
            }
        }

        return kbInvoiceItemsIds;
    }

    private void kbInvoiceItemsIdsFromString(@Nullable final String kbInvoiceItemsIdsAsString, final Map<UUID, Set<UUID>> kbInvoiceItemsIds) throws IOException {
        if (Strings.emptyToNull(kbInvoiceItemsIdsAsString) != null) {
            final Map<UUID, Set<UUID>> kbInvoiceItemsIdsAsMap = objectMapper.readValue(kbInvoiceItemsIdsAsString, new TypeReference<Map<UUID, Set<UUID>>>() {});
            for (final UUID kbInvoiceItemId : kbInvoiceItemsIdsAsMap.keySet()) {
                if (kbInvoiceItemsIds.get(kbInvoiceItemId) == null) {
                    kbInvoiceItemsIds.put(kbInvoiceItemId, new HashSet<UUID>());
                }
                kbInvoiceItemsIds.get(kbInvoiceItemId).addAll(kbInvoiceItemsIdsAsMap.get(kbInvoiceItemId));
            }
        }
    }

    private String kbInvoiceItemsIdsAsString(final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems) throws SQLException {
        final Map<UUID, Set<UUID>> kbInvoiceItemsIds = Maps.<UUID, Iterable<InvoiceItem>, Set<UUID>>transformValues(kbInvoiceItems,
                                                                                                                    new Function<Iterable<InvoiceItem>, Set<UUID>>() {
                                                                                                                        @Override
                                                                                                                        public Set<UUID> apply(final Iterable<InvoiceItem> invoiceItems) {
                                                                                                                            final Set<UUID> invoiceItemIds = new HashSet<UUID>();
                                                                                                                            for (final InvoiceItem invoiceItem : invoiceItems) {
                                                                                                                                invoiceItemIds.add(invoiceItem.getId());
                                                                                                                            }
                                                                                                                            return invoiceItemIds;
                                                                                                                        }
                                                                                                                    });
        return asString(kbInvoiceItemsIds);
    }
}
