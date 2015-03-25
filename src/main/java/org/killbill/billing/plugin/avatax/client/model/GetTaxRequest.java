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
import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;

public class GetTaxRequest {

    // Required for tax calculation
    public Date DocDate; // Must be valid YYYY-MM-DD format
    public String CustomerCode;
    public Address[] Addresses;
    public Line[] Lines;
    // Best Practice for tax calculation
    public String DocCode;
    public DocType DocType;
    public String CompanyCode;
    public Boolean Commit;
    public DetailLevel DetailLevel;
    public String Client;
    // Use where appropriate to the situation
    public String CustomerUsageType;
    public String ExemptionNo;
    public BigDecimal Discount;
    public TaxOverrideDef TaxOverride;
    public String BusinessIdentificationNo;

    // Optional
    public String PurchaseOrderNo;
    public String PaymentDate;
    public String ReferenceCode;
    public String PosLaneCode;
    public String CurrencyCode;

    public enum SystemCustomerUsageType {
        L, // "Other",
        A, // "Federal government",
        B, // "State government",
        C, // "Tribe / Status Indian / Indian Band",
        D, // "Foreign diplomat",
        E, // "Charitable or benevolent organization",
        F, // "Religious or educational organization",
        G, // "Resale",
        H, // "Commercial agricultural production",
        I, //  "Industrial production / manufacturer",
        J, //  "Direct pay permit",
        K, //  "Direct Mail",
        N, //  "Local Government",
        P, //  "Commercial Aquaculture",
        Q, //  "Commercial Fishery",
        R  // "Non-resident"
    }

    public String simplifiedToString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DocCode=").append(DocCode);
        sb.append(", DocDate=").append(DocDate == null ? null : new DateTime(DocDate).toString());
        sb.append(", CustomerCode=").append(CustomerCode);
        sb.append(", ReferenceCode=").append(ReferenceCode);
        sb.append(", Lines=[");
        boolean first = true;
        for (final Line line : Lines) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(line.Amount);
        }
        sb.append("]");
        if (TaxOverride != null) {
            sb.append(", TaxOverride.TaxDate=").append(TaxOverride.TaxDate == null ? "null" : new DateTime(TaxOverride.TaxDate).toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetTaxRequest{");
        sb.append("DocDate=").append(DocDate);
        sb.append(", CustomerCode='").append(CustomerCode).append('\'');
        sb.append(", Addresses=").append(Arrays.toString(Addresses));
        sb.append(", Lines=").append(Arrays.toString(Lines));
        sb.append(", DocCode='").append(DocCode).append('\'');
        sb.append(", DocType=").append(DocType);
        sb.append(", CompanyCode='").append(CompanyCode).append('\'');
        sb.append(", Commit=").append(Commit);
        sb.append(", DetailLevel=").append(DetailLevel);
        sb.append(", Client='").append(Client).append('\'');
        sb.append(", CustomerUsageType='").append(CustomerUsageType).append('\'');
        sb.append(", ExemptionNo='").append(ExemptionNo).append('\'');
        sb.append(", Discount=").append(Discount);
        sb.append(", TaxOverride=").append(TaxOverride);
        sb.append(", BusinessIdentificationNo='").append(BusinessIdentificationNo).append('\'');
        sb.append(", PurchaseOrderNo='").append(PurchaseOrderNo).append('\'');
        sb.append(", PaymentDate='").append(PaymentDate).append('\'');
        sb.append(", ReferenceCode='").append(ReferenceCode).append('\'');
        sb.append(", PosLaneCode='").append(PosLaneCode).append('\'');
        sb.append(", CurrencyCode='").append(CurrencyCode).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GetTaxRequest that = (GetTaxRequest) o;

        if (!Arrays.equals(Addresses, that.Addresses)) {
            return false;
        }
        if (BusinessIdentificationNo != null ? !BusinessIdentificationNo.equals(that.BusinessIdentificationNo) : that.BusinessIdentificationNo != null) {
            return false;
        }
        if (Client != null ? !Client.equals(that.Client) : that.Client != null) {
            return false;
        }
        if (Commit != null ? !Commit.equals(that.Commit) : that.Commit != null) {
            return false;
        }
        if (CompanyCode != null ? !CompanyCode.equals(that.CompanyCode) : that.CompanyCode != null) {
            return false;
        }
        if (CurrencyCode != null ? !CurrencyCode.equals(that.CurrencyCode) : that.CurrencyCode != null) {
            return false;
        }
        if (CustomerCode != null ? !CustomerCode.equals(that.CustomerCode) : that.CustomerCode != null) {
            return false;
        }
        if (CustomerUsageType != null ? !CustomerUsageType.equals(that.CustomerUsageType) : that.CustomerUsageType != null) {
            return false;
        }
        if (DetailLevel != that.DetailLevel) {
            return false;
        }
        if (Discount != null ? !Discount.equals(that.Discount) : that.Discount != null) {
            return false;
        }
        if (DocCode != null ? !DocCode.equals(that.DocCode) : that.DocCode != null) {
            return false;
        }
        if (DocDate != null ? !DocDate.equals(that.DocDate) : that.DocDate != null) {
            return false;
        }
        if (DocType != that.DocType) {
            return false;
        }
        if (ExemptionNo != null ? !ExemptionNo.equals(that.ExemptionNo) : that.ExemptionNo != null) {
            return false;
        }
        if (!Arrays.equals(Lines, that.Lines)) {
            return false;
        }
        if (PaymentDate != null ? !PaymentDate.equals(that.PaymentDate) : that.PaymentDate != null) {
            return false;
        }
        if (PosLaneCode != null ? !PosLaneCode.equals(that.PosLaneCode) : that.PosLaneCode != null) {
            return false;
        }
        if (PurchaseOrderNo != null ? !PurchaseOrderNo.equals(that.PurchaseOrderNo) : that.PurchaseOrderNo != null) {
            return false;
        }
        if (ReferenceCode != null ? !ReferenceCode.equals(that.ReferenceCode) : that.ReferenceCode != null) {
            return false;
        }
        if (TaxOverride != null ? !TaxOverride.equals(that.TaxOverride) : that.TaxOverride != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = DocDate != null ? DocDate.hashCode() : 0;
        result = 31 * result + (CustomerCode != null ? CustomerCode.hashCode() : 0);
        result = 31 * result + (Addresses != null ? Arrays.hashCode(Addresses) : 0);
        result = 31 * result + (Lines != null ? Arrays.hashCode(Lines) : 0);
        result = 31 * result + (DocCode != null ? DocCode.hashCode() : 0);
        result = 31 * result + (DocType != null ? DocType.hashCode() : 0);
        result = 31 * result + (CompanyCode != null ? CompanyCode.hashCode() : 0);
        result = 31 * result + (Commit != null ? Commit.hashCode() : 0);
        result = 31 * result + (DetailLevel != null ? DetailLevel.hashCode() : 0);
        result = 31 * result + (Client != null ? Client.hashCode() : 0);
        result = 31 * result + (CustomerUsageType != null ? CustomerUsageType.hashCode() : 0);
        result = 31 * result + (ExemptionNo != null ? ExemptionNo.hashCode() : 0);
        result = 31 * result + (Discount != null ? Discount.hashCode() : 0);
        result = 31 * result + (TaxOverride != null ? TaxOverride.hashCode() : 0);
        result = 31 * result + (BusinessIdentificationNo != null ? BusinessIdentificationNo.hashCode() : 0);
        result = 31 * result + (PurchaseOrderNo != null ? PurchaseOrderNo.hashCode() : 0);
        result = 31 * result + (PaymentDate != null ? PaymentDate.hashCode() : 0);
        result = 31 * result + (ReferenceCode != null ? ReferenceCode.hashCode() : 0);
        result = 31 * result + (PosLaneCode != null ? PosLaneCode.hashCode() : 0);
        result = 31 * result + (CurrencyCode != null ? CurrencyCode.hashCode() : 0);
        return result;
    }
}
