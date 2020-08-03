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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/TransactionLineDetailModel/
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class TransactionLineDetailModel {

    public long id;
    public double exemptAmount;
    public double nonTaxableAmount;
    public double rate;
    public double tax;
    public double taxableAmount;
    public String taxName;

    @Override
    public String toString() {
        return "TransactionLineDetailModel{" +
               "id=" + id +
               ", exemptAmount=" + exemptAmount +
               ", nonTaxableAmount=" + nonTaxableAmount +
               ", rate=" + rate +
               ", tax=" + tax +
               ", taxableAmount=" + taxableAmount +
               ", taxName='" + taxName + '\'' +
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

        final TransactionLineDetailModel that = (TransactionLineDetailModel) o;

        if (id != that.id) {
            return false;
        }
        if (Double.compare(that.exemptAmount, exemptAmount) != 0) {
            return false;
        }
        if (Double.compare(that.nonTaxableAmount, nonTaxableAmount) != 0) {
            return false;
        }
        if (Double.compare(that.rate, rate) != 0) {
            return false;
        }
        if (Double.compare(that.tax, tax) != 0) {
            return false;
        }
        if (Double.compare(that.taxableAmount, taxableAmount) != 0) {
            return false;
        }
        return taxName != null ? taxName.equals(that.taxName) : that.taxName == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(exemptAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(nonTaxableAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(taxableAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (taxName != null ? taxName.hashCode() : 0);
        return result;
    }
}
