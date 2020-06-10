/*
 * Copyright 2015-2020 Groupon, Inc
 * Copyright 2020-2020 Equinix, Inc
 * Copyright 2015-2020 The Billing Project, LLC
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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/TransactionModel/
public class TransactionModel {

    public long id;
    public String code;
    public long companyId;
    public Date date;
    public String status;
    public String type;
    public Date taxDate;
    public double totalAmount;
    public double totalDiscount;
    public double totalExempt;
    public double totalTaxable;
    public double totalTax;
    public double totalTaxCalculated;
    public TransactionLineModel[] lines;
    public TransactionSummary[] summary;
    public TransactionAddressModel[] addresses;
    public AvaTaxMessage[] messages;

    public String simplifiedToString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("code=").append(code);
        sb.append(", totalAmount=").append(totalAmount);
        sb.append(", totalTaxable=").append(totalTaxable);
        sb.append(", totalTax=").append(totalTax);
        sb.append(", totalTaxCalculated=").append(totalTaxCalculated);
        sb.append(", taxDate=").append(taxDate == null ? "null" : new DateTime(taxDate).toString());
        return sb.toString();
    }

    @Override
    public String toString() {
        return "TransactionModel{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", companyId=" + companyId +
               ", date=" + date +
               ", status='" + status + '\'' +
               ", type='" + type + '\'' +
               ", taxDate=" + taxDate +
               ", totalAmount=" + totalAmount +
               ", totalDiscount=" + totalDiscount +
               ", totalExempt=" + totalExempt +
               ", totalTaxable=" + totalTaxable +
               ", totalTax=" + totalTax +
               ", totalTaxCalculated=" + totalTaxCalculated +
               ", lines=" + Arrays.toString(lines) +
               ", summary=" + Arrays.toString(summary) +
               ", addresses=" + Arrays.toString(addresses) +
               ", messages=" + Arrays.toString(messages) +
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

        final TransactionModel that = (TransactionModel) o;

        if (id != that.id) {
            return false;
        }
        if (companyId != that.companyId) {
            return false;
        }
        if (Double.compare(that.totalAmount, totalAmount) != 0) {
            return false;
        }
        if (Double.compare(that.totalDiscount, totalDiscount) != 0) {
            return false;
        }
        if (Double.compare(that.totalExempt, totalExempt) != 0) {
            return false;
        }
        if (Double.compare(that.totalTaxable, totalTaxable) != 0) {
            return false;
        }
        if (Double.compare(that.totalTax, totalTax) != 0) {
            return false;
        }
        if (Double.compare(that.totalTaxCalculated, totalTaxCalculated) != 0) {
            return false;
        }
        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (status != null ? !status.equals(that.status) : that.status != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (taxDate != null ? !taxDate.equals(that.taxDate) : that.taxDate != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(lines, that.lines)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(summary, that.summary)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(addresses, that.addresses)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (int) (companyId ^ (companyId >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (taxDate != null ? taxDate.hashCode() : 0);
        temp = Double.doubleToLongBits(totalAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalDiscount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalExempt);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalTaxable);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalTax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalTaxCalculated);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(lines);
        result = 31 * result + Arrays.hashCode(summary);
        result = 31 * result + Arrays.hashCode(addresses);
        result = 31 * result + Arrays.hashCode(messages);
        return result;
    }
}
