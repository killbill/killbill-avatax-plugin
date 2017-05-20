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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.avatax.client.AvaTaxClient;
import org.killbill.billing.plugin.avatax.client.AvaTaxClientException;
import org.killbill.billing.plugin.avatax.client.model.Address;
import org.killbill.billing.plugin.avatax.client.model.CommonResponse;
import org.killbill.billing.plugin.avatax.client.model.DetailLevel;
import org.killbill.billing.plugin.avatax.client.model.DocType;
import org.killbill.billing.plugin.avatax.client.model.GetTaxRequest;
import org.killbill.billing.plugin.avatax.client.model.GetTaxResult;
import org.killbill.billing.plugin.avatax.client.model.Line;
import org.killbill.billing.plugin.avatax.client.model.TaxDetail;
import org.killbill.billing.plugin.avatax.client.model.TaxLine;
import org.killbill.billing.plugin.avatax.client.model.TaxOverrideDef;
import org.killbill.billing.plugin.avatax.core.AvaTaxConfigurationHandler;
import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class AvaTaxTaxCalculator extends AvaTaxTaxCalculatorBase {

    public static final String PROPERTY_COMPANY_CODE = "companyCode";
    public static final String CUSTOMER_USAGE_TYPE = "customerUsageType";
    public static final String BUSINESS_IDENTIFICATION_NUMBER = "businessIdentificationNumber";
    public static final String TAX_CODE = "taxCode";

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxTaxCalculator.class);

    private static final String CLIENT_NAME = "KILLBILL";

    private final AvaTaxConfigurationHandler avaTaxConfigurationHandler;

    public AvaTaxTaxCalculator(final AvaTaxConfigurationHandler avaTaxConfigurationHandler, final AvaTaxDao dao, final Clock clock) {
        super(dao, clock);
        this.avaTaxConfigurationHandler = avaTaxConfigurationHandler;
    }

    @Override
    protected Collection<InvoiceItem> buildInvoiceItems(final Account account,
                                                        final Invoice newInvoice,
                                                        final Invoice invoice,
                                                        final Map<UUID, InvoiceItem> taxableItems,
                                                        final Map<UUID, Collection<InvoiceItem>> adjustmentItems,
                                                        @Nullable final String originalInvoiceReferenceCode,
                                                        final boolean dryRun,
                                                        final Iterable<PluginProperty> pluginProperties,
                                                        final UUID kbTenantId,
                                                        final Map<UUID, Iterable<InvoiceItem>> kbInvoiceItems,
                                                        final LocalDate utcToday) throws AvaTaxClientException, SQLException {
        final AvaTaxClient avaTaxClient = avaTaxConfigurationHandler.getConfigurable(kbTenantId);
        final String companyCode = avaTaxClient.getCompanyCode();
        final boolean shouldCommitDocuments = !dryRun && avaTaxClient.shouldCommitDocuments();

        final GetTaxRequest taxRequest = toTaxRequest(companyCode, account, invoice, taxableItems.values(), adjustmentItems, originalInvoiceReferenceCode, !shouldCommitDocuments, pluginProperties, utcToday);
        logger.info("GetTaxRequest: {}", taxRequest.simplifiedToString());

        final GetTaxResult taxResult = avaTaxClient.getTax(taxRequest);
        dao.addResponse(account.getId(), invoice.getId(), kbInvoiceItems, taxResult, clock.getUTCNow(), kbTenantId);

        // Align both log lines for readability
        logger.info(" GetTaxResult: {}", taxResult.simplifiedToString());

        if (!CommonResponse.SeverityLevel.Success.equals(taxResult.ResultCode)) {
            logger.warn("Unsuccessful GetTax request: {}", Arrays.toString(taxResult.Messages));
            return ImmutableList.<InvoiceItem>of();
        } else if (taxResult.TaxLines == null || taxResult.TaxLines.length == 0) {
            logger.info("Nothing to tax for taxable items: {}", kbInvoiceItems.keySet());
            return ImmutableList.<InvoiceItem>of();
        }

        final Collection<InvoiceItem> invoiceItems = new LinkedList<InvoiceItem>();
        for (final TaxLine taxLine : taxResult.TaxLines) {
            // See convention in toLine() below
            final UUID invoiceItemId = UUID.fromString(taxLine.LineNo);
            invoiceItems.addAll(toInvoiceItems(newInvoice.getId(), taxableItems.get(invoiceItemId), taxLine, utcToday));
        }

        return invoiceItems;
    }

    private Collection<InvoiceItem> toInvoiceItems(final UUID invoiceId, final InvoiceItem taxableItem, final TaxLine taxLine, final LocalDate utcToday) {
        if (taxLine.TaxDetails == null || taxLine.TaxDetails.length == 0) {
            final InvoiceItem taxItem = buildTaxItem(taxableItem, invoiceId, utcToday, BigDecimal.valueOf(taxLine.Tax), "Tax");
            if (taxItem == null) {
                return ImmutableList.<InvoiceItem>of();
            } else {
                return ImmutableList.<InvoiceItem>of(taxItem);
            }
        } else {
            final Collection<InvoiceItem> invoiceItems = new LinkedList<InvoiceItem>();
            for (final TaxDetail taxDetail : taxLine.TaxDetails) {
                final String description = MoreObjects.firstNonNull(taxDetail.TaxName, MoreObjects.firstNonNull(taxDetail.JurisName, "Tax"));
                final InvoiceItem taxItem = buildTaxItem(taxableItem, invoiceId, utcToday, BigDecimal.valueOf(taxDetail.Tax), description);
                if (taxItem != null) {
                    invoiceItems.add(taxItem);
                }
            }
            return invoiceItems;
        }
    }

    /**
     * Given some invoice items for a given invoice, prepare a GetTaxRequest for AvaTax. At this point, we've already
     * verified that these items need to be created and don't exist in AvaTax yet.
     * <p/>
     * Note that this will create either a Sales or a Return document, but not both. This means that
     * if <b>adjustmentItems</b> is specified, originalInvoiceReferenceCode must be specified and
     * the <b>taxableItems</b> must already be committed in AvaTax (Return). You cannot create and adjust items at the same time.
     * <p/>
     * In case of subsequent Return, only pass the new adjustments in <b>adjustmentItems</b>.
     *
     * @param account                      Kill Bill account
     * @param invoice                      Kill Bill invoice associated with the taxable items
     * @param taxableItems                 taxable invoice items associated with that invoice
     * @param adjustmentItems              invoice item adjustments associated (empty for Sales document)
     * @param originalInvoiceReferenceCode the original AvaTax reference code  (null for Sales document)
     * @param pluginProperties             Kill Bill plugin properties
     * @param dryRun                       true if the invoice won't be persisted
     * @param utcToday                     today's date
     * @return GetTaxRequest object
     */
    private GetTaxRequest toTaxRequest(final String companyCode,
                                       final Account account,
                                       final Invoice invoice,
                                       final Collection<InvoiceItem> taxableItems,
                                       @Nullable final Map<UUID, Collection<InvoiceItem>> adjustmentItems,
                                       @Nullable final String originalInvoiceReferenceCode,
                                       final boolean dryRun,
                                       final Iterable<PluginProperty> pluginProperties,
                                       final LocalDate utcToday) {
        Preconditions.checkState((originalInvoiceReferenceCode == null && (adjustmentItems == null || adjustmentItems.isEmpty())) ||
                                 (originalInvoiceReferenceCode != null && (adjustmentItems != null && !adjustmentItems.isEmpty())),
                                 "Invalid combination of originalInvoiceReferenceCode %s and adjustments %s", originalInvoiceReferenceCode, adjustmentItems);

        Preconditions.checkState((adjustmentItems == null || adjustmentItems.isEmpty()) || adjustmentItems.size() == taxableItems.size(),
                                 "Invalid number of adjustments %s for taxable items %s", adjustmentItems, taxableItems);

        final GetTaxRequest taxRequest = new GetTaxRequest();

        // The DocCode needs to be unique to be able to support multiple returns for the same invoice
        // Note: for certification, the invoice id needs to be part of the DocCode (we cannot use the invoice number, as it may not be known yet)
        // Also, DocCode length must be between 1 and 50 characters
        taxRequest.DocCode = String.format("%s_%s", invoice.getId(), UUID.randomUUID().toString().substring(0, 12));
        // For returns, refers to the DocCode of the original invoice
        taxRequest.ReferenceCode = originalInvoiceReferenceCode;
        // AvaTax makes no direct association to the original invoice. We overload this field to keep a mapping with the original invoice.
        taxRequest.PurchaseOrderNo = invoice.getId().toString();
        // We want to report the return in the period in which it was processed, but it may have calculated tax in a previous period (which had different tax rates).
        // To handle this, we send the DocDate as the date of return processing, and use TaxOverride.TaxDate to send the date of the original invoice.
        taxRequest.DocDate = utcToday.toDate();
        taxRequest.CurrencyCode = invoice.getCurrency().name();

        if (dryRun) {
            // This is a temporary document type and is not saved in tax history
            taxRequest.DocType = originalInvoiceReferenceCode == null ? DocType.SalesOrder : DocType.ReturnOrder;
            taxRequest.Commit = false;
        } else {
            // The document is a permanent invoice; document and tax calculation results are saved in the tax history
            taxRequest.DocType = originalInvoiceReferenceCode == null ? DocType.SalesInvoice : DocType.ReturnInvoice;
            // Commit the invoice in AvaTax
            taxRequest.Commit = true;
        }

        taxRequest.CustomerCode = MoreObjects.firstNonNull(account.getExternalKey(), account.getId()).toString();
        taxRequest.Addresses = new Address[]{toAddress(account)};
        taxRequest.Lines = new Line[taxableItems.size()];

        // Create the individual line items
        final Iterator<InvoiceItem> taxableItemsIterator = taxableItems.iterator();
        int i = 0;
        while (taxableItemsIterator.hasNext()) {
            final InvoiceItem taxableItem = taxableItemsIterator.next();
            taxRequest.Lines[i] = toLine(invoice, taxableItem, adjustmentItems == null ? null : adjustmentItems.get(taxableItem.getId()), taxRequest.Addresses[0].AddressCode, pluginProperties);
            i++;
        }

        // Done at the line item level
        taxRequest.TaxOverride = null;

        taxRequest.CompanyCode = PluginProperties.getValue(PROPERTY_COMPANY_CODE, companyCode, pluginProperties);
        taxRequest.DetailLevel = DetailLevel.Tax;
        taxRequest.Client = CLIENT_NAME;

        taxRequest.CustomerUsageType = PluginProperties.findPluginPropertyValue(CUSTOMER_USAGE_TYPE, pluginProperties);

        // Nice-to-have (via plugin properties or tags?)
        taxRequest.ExemptionNo = null;
        taxRequest.Discount = BigDecimal.ZERO;
        // Required for VAT
        taxRequest.BusinessIdentificationNo = PluginProperties.findPluginPropertyValue(BUSINESS_IDENTIFICATION_NUMBER, pluginProperties);
        taxRequest.PaymentDate = null;
        taxRequest.PosLaneCode = null;

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
     * @param invoice         invoice associated with the taxable item
     * @param taxableItem     taxable invoice item
     * @param adjustmentItems associated adjustment items (null for Sales document)
     * @param locationCode    associated location from the Addresses field
     * @return Line object
     */
    private Line toLine(final Invoice invoice, final InvoiceItem taxableItem, @Nullable final Iterable<InvoiceItem> adjustmentItems, final String locationCode, final Iterable<PluginProperty> pluginProperties) {
        final Line line = new Line();
        line.LineNo = taxableItem.getId().toString();
        line.DestinationCode = locationCode;
        line.OriginCode = locationCode;
        // SKU
        if (taxableItem.getUsageName() == null) {
            if (taxableItem.getPhaseName() == null) {
                if (taxableItem.getPlanName() == null) {
                    line.ItemCode = taxableItem.getDescription();
                } else {
                    line.ItemCode = taxableItem.getPlanName();
                }
            } else {
                line.ItemCode = taxableItem.getPhaseName();
            }
        } else {
            line.ItemCode = taxableItem.getUsageName();
        }
        line.Qty = BigDecimal.ONE;
        line.Description = taxableItem.getDescription();
        line.Ref1 = taxableItem.getId().toString();
        line.Ref2 = taxableItem.getInvoiceId().toString();

        // Compute the amount to tax or the amount to adjust
        final BigDecimal adjustmentAmount = sum(adjustmentItems);
        final boolean isReturnDocument = adjustmentAmount.compareTo(BigDecimal.ZERO) < 0;
        Preconditions.checkState((adjustmentAmount.compareTo(BigDecimal.ZERO) == 0) ||
                                 (isReturnDocument && taxableItem.getAmount().compareTo(adjustmentAmount.negate()) >= 0),
                                 "Invalid adjustmentAmount %s for invoice item %s", adjustmentAmount, taxableItem);
        line.Amount = isReturnDocument ? adjustmentAmount : taxableItem.getAmount();
        if (isReturnDocument) {
            // Adjustment
            line.TaxOverride = new TaxOverrideDef();
            line.TaxOverride.TaxOverrideType = "TaxDate";
            // Note: we could also look-up the audit logs
            line.TaxOverride.Reason = MoreObjects.firstNonNull(adjustmentItems.iterator().next().getDescription(), "Adjustment");
            line.TaxOverride.TaxAmount = null;
            line.TaxOverride.TaxDate = invoice.getInvoiceDate().toString();
        }

        line.TaxCode = PluginProperties.findPluginPropertyValue(String.format("%s_%s", TAX_CODE, taxableItem.getId()), pluginProperties);

        // Nice-to-have (via plugin properties or tags?)
        line.CustomerUsageType = null;
        line.Discounted = false;
        line.TaxIncluded = false;
        // Required for VAT
        line.BusinessIdentificationNo = null;

        return line;
    }

    private Address toAddress(final Account account) {
        final Address address = new Address();
        address.AddressCode = account.getId().toString();
        address.Line1 = account.getAddress1();
        address.Line2 = account.getAddress2();
        address.Line3 = null;
        address.City = account.getCity();
        address.Region = account.getStateOrProvince();
        address.PostalCode = account.getPostalCode();
        address.Country = account.getCountry();

        // N/A
        address.Latitude = null;
        address.Longitude = null;
        address.TaxRegionId = null;

        return address;
    }
}
