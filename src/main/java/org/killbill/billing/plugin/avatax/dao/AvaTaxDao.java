/*
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2014-2020 The Billing Project, LLC
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.plugin.avatax.client.model.AvaTaxErrors;
import org.killbill.billing.plugin.avatax.client.model.CommonResponse;
import org.killbill.billing.plugin.avatax.client.model.TransactionModel;
import org.killbill.billing.plugin.avatax.client.model.TaxRateResult;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxResponsesRecord;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxTaxCodesRecord;
import org.killbill.billing.plugin.dao.PluginDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import static org.killbill.billing.plugin.avatax.dao.gen.tables.AvataxResponses.AVATAX_RESPONSES;
import static org.killbill.billing.plugin.avatax.dao.gen.tables.AvataxTaxCodes.AVATAX_TAX_CODES;

public class AvaTaxDao extends PluginDao {

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxDao.class);

    private static final String SUCCESS = CommonResponse.SeverityLevel.Success.name();
    private static final String ERROR = CommonResponse.SeverityLevel.Error.name();

    public AvaTaxDao(final DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    public void setTaxCode(final String productName,
                           @Nullable final String taxCode,
                           final DateTime utcNow,
                           final UUID kbTenantId) throws SQLException {
        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                           .transaction(new TransactionalRunnable() {
                               @Override
                               public void run(final Configuration configuration) throws Exception {
                                   final DSLContext dslContext = DSL.using(configuration);

                                   dslContext.delete(AVATAX_TAX_CODES)
                                             .where(AVATAX_TAX_CODES.PRODUCT_NAME.equal(productName))
                                             .and(AVATAX_TAX_CODES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                             .execute();

                                   if (taxCode != null) {
                                       dslContext.insertInto(AVATAX_TAX_CODES,
                                                             AVATAX_TAX_CODES.PRODUCT_NAME,
                                                             AVATAX_TAX_CODES.TAX_CODE,
                                                             AVATAX_TAX_CODES.CREATED_DATE,
                                                             AVATAX_TAX_CODES.KB_TENANT_ID)
                                                 .values(productName,
                                                         taxCode,
                                                         toTimestamp(utcNow),
                                                         kbTenantId.toString())
                                                 .execute();
                                   }
                               }
                           });
                        return null;
                    }
                });
    }

    public List<AvataxTaxCodesRecord> getTaxCodes(final UUID kbTenantId) throws SQLException {
        return execute(dataSource.getConnection(),
                       new WithConnectionCallback<List<AvataxTaxCodesRecord>>() {
                           @Override
                           public List<AvataxTaxCodesRecord> withConnection(final Connection conn) throws SQLException {
                               return DSL.using(conn, dialect, settings)
                                         .selectFrom(AVATAX_TAX_CODES)
                                         .where(AVATAX_TAX_CODES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                         .orderBy(AVATAX_TAX_CODES.RECORD_ID.asc())
                                         .fetch();
                           }
                       });
    }

    public String getTaxCode(final String productName,
                             final UUID kbTenantId) throws SQLException {
        final AvataxTaxCodesRecord record = execute(dataSource.getConnection(),
                                                    new WithConnectionCallback<AvataxTaxCodesRecord>() {
                                                        @Override
                                                        public AvataxTaxCodesRecord withConnection(final Connection conn) throws SQLException {
                                                            return DSL.using(conn, dialect, settings)
                                                                      .selectFrom(AVATAX_TAX_CODES)
                                                                      .where(AVATAX_TAX_CODES.PRODUCT_NAME.equal(productName))
                                                                      .and(AVATAX_TAX_CODES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                                                      .orderBy(AVATAX_TAX_CODES.RECORD_ID.asc())
                                                                      .fetchAny();
                                                        }
                                                    });
        if (record == null) {
            return null;
        } else {
            return record.getTaxCode();
        }
    }

    public void addResponse(final UUID kbAccountId,
                            final UUID kbInvoiceId,
                            final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                            final TaxRateResult taxRateResult,
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
                                       AVATAX_RESPONSES.TOTAL_TAX,
                                       AVATAX_RESPONSES.RESULT_CODE,
                                       AVATAX_RESPONSES.CREATED_DATE,
                                       AVATAX_RESPONSES.KB_TENANT_ID)
                           .values(kbAccountId.toString(),
                                   kbInvoiceId.toString(),
                                   kbInvoiceItemsIdsAsString(kbInvoiceItems),
                                   BigDecimal.valueOf(taxRateResult.totalRate),
                                   SUCCESS,
                                   toTimestamp(utcNow),
                                   kbTenantId.toString())
                           .execute();
                        return null;
                    }
                });
    }

    // Success
    public void addResponse(final UUID kbAccountId,
                            final UUID kbInvoiceId,
                            final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                            final TransactionModel taxResult,
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
                                   taxResult.code,
                                   toTimestamp(taxResult.date),
                                   null,
                                   BigDecimal.valueOf(taxResult.totalAmount),
                                   BigDecimal.valueOf(taxResult.totalDiscount),
                                   BigDecimal.valueOf(taxResult.totalExempt),
                                   BigDecimal.valueOf(taxResult.totalTaxable),
                                   BigDecimal.valueOf(taxResult.totalTax),
                                   BigDecimal.valueOf(taxResult.totalTaxCalculated),
                                   toTimestamp(taxResult.taxDate),
                                   asString(taxResult.lines),
                                   asString(taxResult.summary),
                                   asString(taxResult.addresses),
                                   SUCCESS,
                                   asString(taxResult.messages),
                                   null,
                                   toTimestamp(utcNow),
                                   kbTenantId.toString())
                           .execute();
                        return null;
                    }
                });
    }

    // !Success
    public void addResponse(final UUID kbAccountId,
                            final UUID kbInvoiceId,
                            final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                            final AvaTaxErrors errors,
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
                                       AVATAX_RESPONSES.RESULT_CODE,
                                       AVATAX_RESPONSES.ADDITIONAL_DATA,
                                       AVATAX_RESPONSES.CREATED_DATE,
                                       AVATAX_RESPONSES.KB_TENANT_ID)
                           .values(kbAccountId.toString(),
                                   kbInvoiceId.toString(),
                                   kbInvoiceItemsIdsAsString(kbInvoiceItems),
                                   ERROR,
                                   asString(errors),
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
                                         .and(AVATAX_RESPONSES.RESULT_CODE.equal(SUCCESS))
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
            for (final Entry<UUID, Set<UUID>> entry : kbInvoiceItemsIdsAsMap.entrySet()) {
                if (kbInvoiceItemsIds.get(entry.getKey()) == null) {
                    kbInvoiceItemsIds.put(entry.getKey(), new HashSet<UUID>());
                }
                kbInvoiceItemsIds.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }

    private String kbInvoiceItemsIdsAsString(final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems) throws SQLException {
        final Map<UUID, Set<UUID>> kbInvoiceItemsIds = Maps.<UUID, Iterable<InvoiceItem>, Set<UUID>>transformValues(kbInvoiceItems,
                                                                                                                    new Function<Iterable<InvoiceItem>, Set<UUID>>() {
                                                                                                                        @Override
                                                                                                                        public Set<UUID> apply(final Iterable<InvoiceItem> invoiceItems) {
                                                                                                                            final Set<UUID> invoiceItemIds = new HashSet<UUID>();
                                                                                                                            if (invoiceItems == null) {
                                                                                                                                return invoiceItemIds;
                                                                                                                            }
                                                                                                                            for (final InvoiceItem invoiceItem : invoiceItems) {
                                                                                                                                invoiceItemIds.add(invoiceItem.getId());
                                                                                                                            }
                                                                                                                            return invoiceItemIds;
                                                                                                                        }
                                                                                                                    });
        return asString(kbInvoiceItemsIds);
    }
}
