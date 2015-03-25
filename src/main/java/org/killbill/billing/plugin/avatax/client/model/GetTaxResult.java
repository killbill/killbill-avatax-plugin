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

import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class GetTaxResult {

    public String DocCode;
    public Date DocDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date Timestamp;
    public double TotalAmount;
    public double TotalDiscount;
    public double TotalExemption;
    public double TotalTaxable;
    public double TotalTax;
    public double TotalTaxCalculated;
    public Date TaxDate;
    public TaxLine[] TaxLines;
    public TaxDetail[] TaxSummary;
    public TaxAddress[] TaxAddresses;
    public CommonResponse.SeverityLevel ResultCode;
    public CommonResponse.Message[] Messages;

    public String simplifiedToString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DocCode=").append(DocCode);
        sb.append(", ResultCode=").append(ResultCode);
        sb.append(", TotalAmount=").append(TotalAmount);
        sb.append(", TotalTaxable=").append(TotalTaxable);
        sb.append(", TotalTax=").append(TotalTax);
        sb.append(", TotalTaxCalculated=").append(TotalTaxCalculated);
        sb.append(", TaxDate=").append(TaxDate == null ? "null" : new DateTime(TaxDate).toString());
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetTaxResult{");
        sb.append("DocCode='").append(DocCode).append('\'');
        sb.append(", DocDate=").append(DocDate);
        sb.append(", Timestamp=").append(Timestamp);
        sb.append(", TotalAmount=").append(TotalAmount);
        sb.append(", TotalDiscount=").append(TotalDiscount);
        sb.append(", TotalExemption=").append(TotalExemption);
        sb.append(", TotalTaxable=").append(TotalTaxable);
        sb.append(", TotalTax=").append(TotalTax);
        sb.append(", TotalTaxCalculated=").append(TotalTaxCalculated);
        sb.append(", TaxDate=").append(TaxDate);
        sb.append(", TaxLines=").append(Arrays.toString(TaxLines));
        sb.append(", TaxSummary=").append(Arrays.toString(TaxSummary));
        sb.append(", TaxAddresses=").append(Arrays.toString(TaxAddresses));
        sb.append(", ResultCode=").append(ResultCode);
        sb.append(", Messages=").append(Arrays.toString(Messages));
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

        final GetTaxResult that = (GetTaxResult) o;

        if (Double.compare(that.TotalAmount, TotalAmount) != 0) {
            return false;
        }
        if (Double.compare(that.TotalDiscount, TotalDiscount) != 0) {
            return false;
        }
        if (Double.compare(that.TotalExemption, TotalExemption) != 0) {
            return false;
        }
        if (Double.compare(that.TotalTax, TotalTax) != 0) {
            return false;
        }
        if (Double.compare(that.TotalTaxCalculated, TotalTaxCalculated) != 0) {
            return false;
        }
        if (Double.compare(that.TotalTaxable, TotalTaxable) != 0) {
            return false;
        }
        if (DocCode != null ? !DocCode.equals(that.DocCode) : that.DocCode != null) {
            return false;
        }
        if (DocDate != null ? !DocDate.equals(that.DocDate) : that.DocDate != null) {
            return false;
        }
        if (!Arrays.equals(Messages, that.Messages)) {
            return false;
        }
        if (ResultCode != that.ResultCode) {
            return false;
        }
        if (!Arrays.equals(TaxAddresses, that.TaxAddresses)) {
            return false;
        }
        if (TaxDate != null ? !TaxDate.equals(that.TaxDate) : that.TaxDate != null) {
            return false;
        }
        if (!Arrays.equals(TaxLines, that.TaxLines)) {
            return false;
        }
        if (!Arrays.equals(TaxSummary, that.TaxSummary)) {
            return false;
        }
        if (Timestamp != null ? !Timestamp.equals(that.Timestamp) : that.Timestamp != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = DocCode != null ? DocCode.hashCode() : 0;
        result = 31 * result + (DocDate != null ? DocDate.hashCode() : 0);
        result = 31 * result + (Timestamp != null ? Timestamp.hashCode() : 0);
        temp = Double.doubleToLongBits(TotalAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(TotalDiscount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(TotalExemption);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(TotalTaxable);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(TotalTax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(TotalTaxCalculated);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (TaxDate != null ? TaxDate.hashCode() : 0);
        result = 31 * result + (TaxLines != null ? Arrays.hashCode(TaxLines) : 0);
        result = 31 * result + (TaxSummary != null ? Arrays.hashCode(TaxSummary) : 0);
        result = 31 * result + (TaxAddresses != null ? Arrays.hashCode(TaxAddresses) : 0);
        result = 31 * result + (ResultCode != null ? ResultCode.hashCode() : 0);
        result = 31 * result + (Messages != null ? Arrays.hashCode(Messages) : 0);
        return result;
    }
}
