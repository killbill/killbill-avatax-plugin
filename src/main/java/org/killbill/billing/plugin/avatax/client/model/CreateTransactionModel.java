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

package org.killbill.billing.plugin.avatax.client.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/CreateTransactionModel/
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class CreateTransactionModel {

    public Date date;
    public String customerCode;
    public AddressesModel addresses;
    public LineItemModel[] lines;
    public String code;
    public DocType type;
    public String companyCode;
    public Boolean commit;
    public String entityUseCode;
    public String exemptionNo;
    public BigDecimal discount;
    public TaxOverrideModel taxOverride;
    public String businessIdentificationNo;
    public String purchaseOrderNo;
    public String referenceCode;
    public String posLaneCode;
    public String currencyCode;
    public String description;
    public String debugLevel;

    public String simplifiedToString() {
        final StringBuilder sb = new StringBuilder();
        if (!commit) {
            // AvaTax id, useful only if the document is persisted
            sb.append("code=").append(code);
        } else {
            sb.append("commit=false");
        }
        sb.append(", description=").append(description); // Kill Bill invoiceId
        sb.append(", referenceCode=").append(referenceCode); // Kill Bill original invoiceId
        sb.append(", lines=[");
        boolean first = true;
        for (final LineItemModel lineItemModel : lines) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("code=").append(lineItemModel.taxCode);
            sb.append(" ");
            sb.append("amount=").append(lineItemModel.amount);
        }
        sb.append("]");
        if (taxOverride != null) {
            sb.append(", taxOverride.taxDate=").append(taxOverride.taxDate == null ? "null" : new DateTime(taxOverride.taxDate).toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CreateTransactionModel{" +
               "date=" + date +
               ", customerCode='" + customerCode + '\'' +
               ", addresses=" + addresses +
               ", lines=" + Arrays.toString(lines) +
               ", code='" + code + '\'' +
               ", type=" + type +
               ", companyCode='" + companyCode + '\'' +
               ", commit=" + commit +
               ", entityUseCode='" + entityUseCode + '\'' +
               ", exemptionNo='" + exemptionNo + '\'' +
               ", discount=" + discount +
               ", taxOverride=" + taxOverride +
               ", businessIdentificationNo='" + businessIdentificationNo + '\'' +
               ", purchaseOrderNo='" + purchaseOrderNo + '\'' +
               ", referenceCode='" + referenceCode + '\'' +
               ", posLaneCode='" + posLaneCode + '\'' +
               ", currencyCode='" + currencyCode + '\'' +
               ", description='" + description + '\'' +
               ", debugLevel='" + debugLevel + '\'' +
               '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CreateTransactionModel that = (CreateTransactionModel) o;

        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (customerCode != null ? !customerCode.equals(that.customerCode) : that.customerCode != null) {
            return false;
        }
        if (addresses != null ? !addresses.equals(that.addresses) : that.addresses != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(lines, that.lines)) {
            return false;
        }
        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        if (companyCode != null ? !companyCode.equals(that.companyCode) : that.companyCode != null) {
            return false;
        }
        if (commit != null ? !commit.equals(that.commit) : that.commit != null) {
            return false;
        }
        if (entityUseCode != null ? !entityUseCode.equals(that.entityUseCode) : that.entityUseCode != null) {
            return false;
        }
        if (exemptionNo != null ? !exemptionNo.equals(that.exemptionNo) : that.exemptionNo != null) {
            return false;
        }
        if (discount != null ? !discount.equals(that.discount) : that.discount != null) {
            return false;
        }
        if (taxOverride != null ? !taxOverride.equals(that.taxOverride) : that.taxOverride != null) {
            return false;
        }
        if (businessIdentificationNo != null ? !businessIdentificationNo.equals(that.businessIdentificationNo) : that.businessIdentificationNo != null) {
            return false;
        }
        if (purchaseOrderNo != null ? !purchaseOrderNo.equals(that.purchaseOrderNo) : that.purchaseOrderNo != null) {
            return false;
        }
        if (referenceCode != null ? !referenceCode.equals(that.referenceCode) : that.referenceCode != null) {
            return false;
        }
        if (posLaneCode != null ? !posLaneCode.equals(that.posLaneCode) : that.posLaneCode != null) {
            return false;
        }
        if (currencyCode != null ? !currencyCode.equals(that.currencyCode) : that.currencyCode != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        return debugLevel != null ? debugLevel.equals(that.debugLevel) : that.debugLevel == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (customerCode != null ? customerCode.hashCode() : 0);
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(lines);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (companyCode != null ? companyCode.hashCode() : 0);
        result = 31 * result + (commit != null ? commit.hashCode() : 0);
        result = 31 * result + (entityUseCode != null ? entityUseCode.hashCode() : 0);
        result = 31 * result + (exemptionNo != null ? exemptionNo.hashCode() : 0);
        result = 31 * result + (discount != null ? discount.hashCode() : 0);
        result = 31 * result + (taxOverride != null ? taxOverride.hashCode() : 0);
        result = 31 * result + (businessIdentificationNo != null ? businessIdentificationNo.hashCode() : 0);
        result = 31 * result + (purchaseOrderNo != null ? purchaseOrderNo.hashCode() : 0);
        result = 31 * result + (referenceCode != null ? referenceCode.hashCode() : 0);
        result = 31 * result + (posLaneCode != null ? posLaneCode.hashCode() : 0);
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (debugLevel != null ? debugLevel.hashCode() : 0);
        return result;
    }
}
