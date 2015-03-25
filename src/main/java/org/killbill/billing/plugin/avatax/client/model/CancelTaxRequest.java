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

public class CancelTaxRequest {

    public enum CancelCode {
        Unspecified,
        PostFailed,
        DocDeleted,
        DocVoided,
        AdjustmentCancelled;
    }

    public CancelCode CancelCode;
    // The document needs to be uniquely identified by DocCode/DocType/CompanyCode
    public DocType DocType; // Note that the only *meaningful* values for this property here are SalesInvoice, ReturnInvoice, PurchaseInvoice.
    public String CompanyCode;
    public String DocCode;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CancelTaxRequest{");
        sb.append("CancelCode=").append(CancelCode);
        sb.append(", DocType=").append(DocType);
        sb.append(", CompanyCode='").append(CompanyCode).append('\'');
        sb.append(", DocCode='").append(DocCode).append('\'');
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

        final CancelTaxRequest that = (CancelTaxRequest) o;

        if (CancelCode != that.CancelCode) {
            return false;
        }
        if (CompanyCode != null ? !CompanyCode.equals(that.CompanyCode) : that.CompanyCode != null) {
            return false;
        }
        if (DocCode != null ? !DocCode.equals(that.DocCode) : that.DocCode != null) {
            return false;
        }
        if (DocType != that.DocType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = CancelCode != null ? CancelCode.hashCode() : 0;
        result = 31 * result + (DocType != null ? DocType.hashCode() : 0);
        result = 31 * result + (CompanyCode != null ? CompanyCode.hashCode() : 0);
        result = 31 * result + (DocCode != null ? DocCode.hashCode() : 0);
        return result;
    }
}
