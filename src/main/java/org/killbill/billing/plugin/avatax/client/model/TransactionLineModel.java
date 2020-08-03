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

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/TransactionLineModel/
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class TransactionLineModel {

    public String lineNumber;
    public String taxCode;
    public Boolean isItemTaxable;
    public double taxableAmount;
    public double tax;
    public double discountAmount;
    public double taxCalculated;
    public double exemptAmount;
    public TransactionLineDetailModel[] details;
    public String boundaryOverrideId;

    @Override
    public String toString() {
        return "TransactionLineModel{" +
               "lineNumber='" + lineNumber + '\'' +
               ", taxCode='" + taxCode + '\'' +
               ", isItemTaxable=" + isItemTaxable +
               ", taxableAmount=" + taxableAmount +
               ", tax=" + tax +
               ", discountAmount=" + discountAmount +
               ", taxCalculated=" + taxCalculated +
               ", exemptAmount=" + exemptAmount +
               ", details=" + Arrays.toString(details) +
               ", boundaryOverrideId='" + boundaryOverrideId + '\'' +
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

        final TransactionLineModel that = (TransactionLineModel) o;

        if (Double.compare(that.taxableAmount, taxableAmount) != 0) {
            return false;
        }
        if (Double.compare(that.tax, tax) != 0) {
            return false;
        }
        if (Double.compare(that.discountAmount, discountAmount) != 0) {
            return false;
        }
        if (Double.compare(that.taxCalculated, taxCalculated) != 0) {
            return false;
        }
        if (Double.compare(that.exemptAmount, exemptAmount) != 0) {
            return false;
        }
        if (lineNumber != null ? !lineNumber.equals(that.lineNumber) : that.lineNumber != null) {
            return false;
        }
        if (taxCode != null ? !taxCode.equals(that.taxCode) : that.taxCode != null) {
            return false;
        }
        if (isItemTaxable != null ? !isItemTaxable.equals(that.isItemTaxable) : that.isItemTaxable != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(details, that.details)) {
            return false;
        }
        return boundaryOverrideId != null ? boundaryOverrideId.equals(that.boundaryOverrideId) : that.boundaryOverrideId == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = lineNumber != null ? lineNumber.hashCode() : 0;
        result = 31 * result + (taxCode != null ? taxCode.hashCode() : 0);
        result = 31 * result + (isItemTaxable != null ? isItemTaxable.hashCode() : 0);
        temp = Double.doubleToLongBits(taxableAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(discountAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(taxCalculated);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(exemptAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(details);
        result = 31 * result + (boundaryOverrideId != null ? boundaryOverrideId.hashCode() : 0);
        return result;
    }
}
