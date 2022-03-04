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

package org.killbill.billing.plugin.avatax.api;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceStatus;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.client.model.AddressLocationInfo;
import org.killbill.billing.plugin.avatax.client.model.AddressesModel;
import org.killbill.billing.plugin.avatax.client.model.CreateTransactionModel;
import org.killbill.billing.plugin.avatax.client.model.DocType;
import org.killbill.billing.plugin.avatax.client.model.LineItemModel;
import org.killbill.billing.plugin.avatax.client.model.TaxOverrideModel;
import org.killbill.billing.plugin.avatax.client.model.TransactionLineDetailModel;
import org.killbill.billing.plugin.avatax.client.model.TransactionLineModel;
import org.killbill.billing.plugin.avatax.client.model.TransactionModel;
import org.killbill.billing.plugin.avatax.core.AvaTaxConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class AvaTaxTaxCalculator extends AvaTaxTaxCalculatorBase {

    public static final String PROPERTY_COMPANY_CODE = "companyCode";
    public static final String CUSTOMER_USAGE_TYPE = "customerUsageType";
    public static final String TAX_CODE = "taxCode";
    public static final String LOCATION_CODE = "locationCode";
    public static final String LOCATION_ADDRESS1 = "locationAddress1";
    public static final String LOCATION_ADDRESS2 = "locationAddress2";
    public static final String LOCATION_CITY = "locationCity";
    public static final String LOCATION_REGION = "locationRegion";
    public static final String LOCATION_POSTAL_CODE = "locationPostalCode";
    public static final String LOCATION_COUNTRY = "locationCountry";

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxTaxCalculator.class);

    private final AvaTaxConfigurationHandler avaTaxConfigurationHandler;

    public AvaTaxTaxCalculator(final AvaTaxConfigurationHandler avaTaxConfigurationHandler,
                               final AvaTaxDao dao,
                               final Clock clock,
                               final OSGIKillbillAPI osgiKillbillAPI) {
        super(dao, clock, osgiKillbillAPI);
        this.avaTaxConfigurationHandler = avaTaxConfigurationHandler;
    }

    @Override
    protected Collection<InvoiceItem> buildInvoiceItems(final Account account,
                                                        final Invoice newInvoice,
                                                        final Invoice invoice,
                                                        final Map<UUID, InvoiceItem> taxableItems,
                                                        @Nullable final Map<UUID, List<InvoiceItem>> adjustmentItems,
                                                        @Nullable final String originalInvoiceReferenceCode,
                                                        final boolean dryRun,
                                                        final Iterable<PluginProperty> pluginProperties,
                                                        final UUID kbTenantId,
                                                        final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                                                        final LocalDate utcToday) throws AvaTaxClientException, SQLException {
        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(kbTenantId);
        final String companyCode = avaTaxClient.getCompanyCode();
        final boolean shouldCommitDocuments = avaTaxClient.shouldCommitDocuments();

        final CreateTransactionModel taxRequest = toTaxRequest(companyCode,
                                                               account,
                                                               invoice,
                                                               taxableItems.values(),
                                                               adjustmentItems,
                                                               originalInvoiceReferenceCode,
                                                               dryRun,
                                                               shouldCommitDocuments,
                                                               pluginProperties,
                                                               utcToday,
                                                               avaTaxClient.shouldSkipAnomalousAdjustments());
        if (taxRequest == null) {
            return ImmutableList.<InvoiceItem>of();
        }                                                       
        logger.info("CreateTransaction req: {}", taxRequest.simplifiedToString());

        try {
            final TransactionModel taxResult = avaTaxClient.createTransaction(taxRequest);
            logger.info("CreateTransaction res: {}", taxResult.simplifiedToString());
            if (!dryRun) {
                dao.addResponse(account.getId(), newInvoice.getId(), kbInvoiceItems, taxResult, clock.getUTCNow(), kbTenantId);
            }

            if (taxResult.lines == null || taxResult.lines.length == 0) {
                logger.info("Nothing to tax for taxable items: {}", kbInvoiceItems.keySet());
                return ImmutableList.<InvoiceItem>of();
            }

            final Collection<InvoiceItem> invoiceItems = new LinkedList<InvoiceItem>();
            for (final TransactionLineModel transactionLineModel : taxResult.lines) {
                // See convention in toLine() below
                final UUID invoiceItemId = UUID.fromString(transactionLineModel.lineNumber);
                final InvoiceItem adjustmentItem;
                if (adjustmentItems != null &&
                    adjustmentItems.get(invoiceItemId) != null &&
                    adjustmentItems.get(invoiceItemId).size() == 1) {
                    // Could be a repair or an item adjustment: in either case, we use it to compute the service period
                    adjustmentItem = adjustmentItems.get(invoiceItemId).get(0);
                } else {
                    // No repair or multiple adjustments: use the original service period
                    adjustmentItem = null;
                }
                invoiceItems.addAll(toInvoiceItems(newInvoice.getId(), taxableItems.get(invoiceItemId), transactionLineModel, adjustmentItem));
            }

            return invoiceItems;
        } catch (final AvaTaxClientException e) {
            if (e.getErrors() != null) {
                dao.addResponse(account.getId(), invoice.getId(), kbInvoiceItems, e.getErrors(), clock.getUTCNow(), kbTenantId);
                logger.warn("CreateTransaction res: {}", e.getErrors());
            }
            throw e;
        }
    }

    private Collection<InvoiceItem> toInvoiceItems(final UUID invoiceId,
                                                   final InvoiceItem taxableItem,
                                                   final TransactionLineModel transactionLineModel,
                                                   @Nullable final InvoiceItem adjustmentItem) {
        if (transactionLineModel.details == null || transactionLineModel.details.length == 0) {
            final InvoiceItem taxItem = buildTaxItem(taxableItem, invoiceId, adjustmentItem, BigDecimal.valueOf(transactionLineModel.tax), "Tax");
            if (taxItem == null) {
                return ImmutableList.<InvoiceItem>of();
            } else {
                return ImmutableList.<InvoiceItem>of(taxItem);
            }
        } else {
            final Collection<InvoiceItem> invoiceItems = new LinkedList<InvoiceItem>();
            for (final TransactionLineDetailModel transactionLineDetailModel : transactionLineModel.details) {
                final String description = MoreObjects.firstNonNull(transactionLineDetailModel.taxName, MoreObjects.firstNonNull(transactionLineDetailModel.taxName, "Tax"));
                final InvoiceItem taxItem = buildTaxItem(taxableItem, invoiceId, adjustmentItem, BigDecimal.valueOf(transactionLineDetailModel.tax), description);
                if (taxItem != null) {
                    invoiceItems.add(taxItem);
                }
            }
            return invoiceItems;
        }
    }

    /**
     * Given some invoice items for a given invoice, prepare a CreateTransactionModel entity for AvaTax. At this point, we've already
     * verified that these items need to be created and don't exist in AvaTax yet.
     * <p/>
     * Note that this will create either a Sales or a Return document, but not both. This means that
     * if <b>adjustmentItems</b> is specified, originalInvoiceReferenceCode must be specified and
     * the <b>taxableItems</b> must already be committed in AvaTax (Return). You cannot create and adjust items at the same time.
     * <p/>
     * In case of subsequent Return, only pass the new adjustments in <b>adjustmentItems</b>.
     * @param companyCode                  Company code
     * @param account                      Kill Bill account
     * @param invoice                      Kill Bill invoice associated with the taxable items (either the new invoice or an historical invoice for returns)
     * @param taxableItems                 new taxable invoice items or original, already taxed, items if they're being adjusted
     * @param adjustmentItems              new taxableItem adjustment items, used to compute the amount of tax to return (null for Sales document), keyed by taxable item id
     * @param originalInvoiceReferenceCode the original AvaTax reference code (null for Sales document)
     * @param dryRun                       true if the invoice won't be persisted
     * @param shouldCommitDocuments        true if the AvaTax document should be committed
     * @param pluginProperties             Kill Bill plugin properties
     * @param utcToday                     today's date
     */
    private CreateTransactionModel toTaxRequest(final String companyCode,
                                                final Account account,
                                                final Invoice invoice,
                                                final Collection<InvoiceItem> taxableItems,
                                                @Nullable final Map<UUID, List<InvoiceItem>> adjustmentItems,
                                                @Nullable final String originalInvoiceReferenceCode,
                                                final boolean dryRun,
                                                final boolean shouldCommitDocuments,
                                                final Iterable<PluginProperty> pluginProperties,
                                                final LocalDate utcToday,
                                                final boolean skipAnomalousAdjustments) {
        
        try {
            Preconditions.checkState((originalInvoiceReferenceCode == null && (adjustmentItems == null || adjustmentItems.isEmpty())) ||
                                        (originalInvoiceReferenceCode != null && (adjustmentItems != null && !adjustmentItems.isEmpty())),
                                        "Invalid combination of originalInvoiceReferenceCode %s and adjustments %s", originalInvoiceReferenceCode, adjustmentItems);
        } catch (IllegalStateException e) {                                     
            if (skipAnomalousAdjustments) {
                logger.warn("Returning null tax request instead of throwing IllegalStateException exception for Invalid combination of originalInvoiceReferenceCode {} and adjustments {}",
                originalInvoiceReferenceCode, adjustmentItems);
                return null;
            } else {
                throw e;
            }     
        }                                 

        Preconditions.checkState((adjustmentItems == null || adjustmentItems.isEmpty()) || adjustmentItems.size() == taxableItems.size(),
                                 "Invalid number of adjustments %s for taxable items %s", adjustmentItems, taxableItems);

        final CreateTransactionModel taxRequest = new CreateTransactionModel();

        // The DocCode needs to be unique to be able to support multiple returns for the same invoice
        // Note: for certification, the invoice id needs to be part of the DocCode (we cannot use the invoice number, as it may not be known yet)
        // Also, DocCode length must be between 1 and 50 characters
        taxRequest.code = String.format("%s_%s", invoice.getId(), UUID.randomUUID().toString().substring(0, 12));
        // For returns, refers to the DocCode of the original invoice
        taxRequest.referenceCode = originalInvoiceReferenceCode;
        // We overload this field to keep a mapping with the Kill Bill invoice
        taxRequest.description = invoice.getId().toString();
        // We want to report the return in the period in which it was processed, but it may have calculated tax in a previous period (which had different tax rates).
        // To handle this, we send the DocDate as the date of return processing, and use TaxOverride.TaxDate to send the date of the original invoice.
        taxRequest.date = utcToday.toDate();
        taxRequest.currencyCode = invoice.getCurrency().name();

        if (dryRun) {
            // This is a temporary document type and is not saved in tax history
            taxRequest.type = originalInvoiceReferenceCode == null ? DocType.SalesOrder : DocType.ReturnOrder;
            taxRequest.commit = false;
        } else {
            // The document is a permanent invoice; document and tax calculation results are saved in the tax history
            taxRequest.type = originalInvoiceReferenceCode == null ? DocType.SalesInvoice : DocType.ReturnInvoice;
            // Commit the invoice in AvaTax if the invoice is being committed (don't commit DRAFT invoices)
            taxRequest.commit = shouldCommitDocuments && invoice.getStatus() == InvoiceStatus.COMMITTED;
        }

        taxRequest.customerCode = MoreObjects.firstNonNull(account.getExternalKey(), account.getId()).toString();
        taxRequest.addresses = toAddress(account, pluginProperties);
        taxRequest.lines = new LineItemModel[taxableItems.size()];

        // Create the individual line items
        final Iterator<InvoiceItem> taxableItemsIterator = taxableItems.iterator();
        int i = 0;
        while (taxableItemsIterator.hasNext()) {
            final InvoiceItem taxableItem = taxableItemsIterator.next();
            taxRequest.lines[i] = toLine(taxableItem,
                                         adjustmentItems == null ? null : adjustmentItems.get(taxableItem.getId()),
                                         invoice.getInvoiceDate(),
                                         pluginProperties);
            i++;
        }

        taxRequest.companyCode = PluginProperties.getValue(PROPERTY_COMPANY_CODE, companyCode, pluginProperties);
        taxRequest.entityUseCode = PluginProperties.findPluginPropertyValue(CUSTOMER_USAGE_TYPE, pluginProperties);

        return taxRequest;
    }

    /**
     * Convert a taxable invoice item (NOT an adjustment) into a Line for AvaTax.
     * <p/>
     * AvaTax Return notes:
     * <ul>
     * <li>pass only line items being returned — do not include the line items that will not be returned (unless all items are returned)</li>
     * <li>set the Amt property to a negative dollar amount on the line items (always leave the Qty as a positive number)</li>
     * </ul>
     *
     * @param taxableItem         new taxable invoice item or original, already taxed, item if it's being adjusted
     * @param adjustmentItems     new taxableItem adjustment items, used to compute the amount of tax to return (null for Sales document)
     * @param originalInvoiceDate date of the original taxableItem's invoice (not the invoice date of the repair for instance), if it's being returned
     * @param pluginProperties    plugin properties
     */
    private LineItemModel toLine(final InvoiceItem taxableItem,
                                 @Nullable final Iterable<InvoiceItem> adjustmentItems,
                                 @Nullable final LocalDate originalInvoiceDate,
                                 final Iterable<PluginProperty> pluginProperties) {
        final LineItemModel lineItemModel = new LineItemModel();
        lineItemModel.number = taxableItem.getId().toString();
        // SKU
        if (taxableItem.getUsageName() == null) {
            if (taxableItem.getPhaseName() == null) {
                if (taxableItem.getPlanName() == null) {
                    lineItemModel.itemCode = taxableItem.getDescription();
                } else {
                    lineItemModel.itemCode = taxableItem.getPlanName();
                }
            } else {
                lineItemModel.itemCode = taxableItem.getPhaseName();
            }
        } else {
            lineItemModel.itemCode = taxableItem.getUsageName();
        }
        lineItemModel.quantity = new BigDecimal(MoreObjects.firstNonNull(taxableItem.getQuantity(), 1).toString());
        lineItemModel.description = taxableItem.getDescription();
        lineItemModel.ref1 = taxableItem.getId().toString();
        lineItemModel.ref2 = taxableItem.getInvoiceId().toString();

        // Compute the amount to tax or the amount to adjust
        final BigDecimal adjustmentAmount = sum(adjustmentItems);
        final boolean isReturnDocument = adjustmentAmount.compareTo(BigDecimal.ZERO) < 0;
        Preconditions.checkState((adjustmentAmount.compareTo(BigDecimal.ZERO) == 0) ||
                                 (isReturnDocument && taxableItem.getAmount().compareTo(adjustmentAmount.negate()) >= 0),
                                 "Invalid adjustmentAmount %s for invoice item %s", adjustmentAmount, taxableItem);
        lineItemModel.amount = isReturnDocument ? adjustmentAmount : taxableItem.getAmount();
        if (isReturnDocument) {
            Preconditions.checkNotNull(adjustmentItems, "Missing adjustments for return document");
            // Adjustment
            lineItemModel.taxOverride = new TaxOverrideModel();
            lineItemModel.taxOverride.type = "TaxDate";
            // Note: we could also look-up the audit logs
            lineItemModel.taxOverride.reason = MoreObjects.firstNonNull(adjustmentItems.iterator().next().getDescription(), "Adjustment");
            lineItemModel.taxOverride.taxAmount = null;
            lineItemModel.taxOverride.taxDate = originalInvoiceDate.toString();
        }

        lineItemModel.taxCode = PluginProperties.findPluginPropertyValue(String.format("%s_%s", TAX_CODE, taxableItem.getId()), pluginProperties);

        final String lineItemLocationCode = PluginProperties.findPluginPropertyValue(String.format("%s_%s", LOCATION_CODE, taxableItem.getId()), pluginProperties);
        if (lineItemLocationCode != null) {
            lineItemModel.addresses = new AddressesModel();
            lineItemModel.addresses.singleLocation = new AddressLocationInfo();
            lineItemModel.addresses.singleLocation.locationCode = lineItemLocationCode;
        }

        return lineItemModel;
    }

    private AddressesModel toAddress(final Account account, final Iterable<PluginProperty> pluginProperties) {
        final AddressLocationInfo addressLocationInfo = new AddressLocationInfo();

        final String line1 = PluginProperties.findPluginPropertyValue(LOCATION_ADDRESS1, pluginProperties);
        if (line1 != null) {
            addressLocationInfo.line1 = line1;
            addressLocationInfo.line2 = PluginProperties.findPluginPropertyValue(LOCATION_ADDRESS2, pluginProperties);
            addressLocationInfo.city = PluginProperties.findPluginPropertyValue(LOCATION_CITY, pluginProperties);
            addressLocationInfo.region = PluginProperties.findPluginPropertyValue(LOCATION_REGION, pluginProperties);
            addressLocationInfo.postalCode = PluginProperties.findPluginPropertyValue(LOCATION_POSTAL_CODE, pluginProperties);
            addressLocationInfo.country = PluginProperties.findPluginPropertyValue(LOCATION_COUNTRY, pluginProperties);
        } else {
            addressLocationInfo.line1 = account.getAddress1();
            addressLocationInfo.line2 = account.getAddress2();
            addressLocationInfo.city = account.getCity();
            addressLocationInfo.region = account.getStateOrProvince();
            addressLocationInfo.postalCode = account.getPostalCode();
            addressLocationInfo.country = account.getCountry();
        }

        // You must provide either a valid postal code, line1 + city + region, or line1 + postal code
        final boolean valid = addressLocationInfo.postalCode != null ||
                              (addressLocationInfo.line1 != null && addressLocationInfo.city != null && addressLocationInfo.region != null);
        if (!valid) {
            return null;
        }

        final AddressesModel addressesModel = new AddressesModel();
        addressesModel.shipTo = addressLocationInfo;

        return addressesModel;
    }
}
