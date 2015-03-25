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

// Nested in GetTaxResult object
public class TaxLine {

    public String LineNo;
    public String TaxCode;
    public Boolean Taxability;
    public double Taxable;
    public double Rate;
    public double Tax;
    public double Discount;
    public double TaxCalculated;
    public double Exemption;
    public TaxDetail[] TaxDetails;
    public String BoundaryLevel;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxLine{");
        sb.append("LineNo='").append(LineNo).append('\'');
        sb.append(", TaxCode='").append(TaxCode).append('\'');
        sb.append(", Taxability=").append(Taxability);
        sb.append(", Taxable=").append(Taxable);
        sb.append(", Rate=").append(Rate);
        sb.append(", Tax=").append(Tax);
        sb.append(", Discount=").append(Discount);
        sb.append(", TaxCalculated=").append(TaxCalculated);
        sb.append(", Exemption=").append(Exemption);
        sb.append(", TaxDetails=").append(Arrays.toString(TaxDetails));
        sb.append(", BoundaryLevel='").append(BoundaryLevel).append('\'');
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

        final TaxLine taxLine = (TaxLine) o;

        if (Double.compare(taxLine.Discount, Discount) != 0) {
            return false;
        }
        if (Double.compare(taxLine.Exemption, Exemption) != 0) {
            return false;
        }
        if (Double.compare(taxLine.Rate, Rate) != 0) {
            return false;
        }
        if (Double.compare(taxLine.Tax, Tax) != 0) {
            return false;
        }
        if (Double.compare(taxLine.TaxCalculated, TaxCalculated) != 0) {
            return false;
        }
        if (Double.compare(taxLine.Taxable, Taxable) != 0) {
            return false;
        }
        if (BoundaryLevel != null ? !BoundaryLevel.equals(taxLine.BoundaryLevel) : taxLine.BoundaryLevel != null) {
            return false;
        }
        if (LineNo != null ? !LineNo.equals(taxLine.LineNo) : taxLine.LineNo != null) {
            return false;
        }
        if (TaxCode != null ? !TaxCode.equals(taxLine.TaxCode) : taxLine.TaxCode != null) {
            return false;
        }
        if (!Arrays.equals(TaxDetails, taxLine.TaxDetails)) {
            return false;
        }
        if (Taxability != null ? !Taxability.equals(taxLine.Taxability) : taxLine.Taxability != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = LineNo != null ? LineNo.hashCode() : 0;
        result = 31 * result + (TaxCode != null ? TaxCode.hashCode() : 0);
        result = 31 * result + (Taxability != null ? Taxability.hashCode() : 0);
        temp = Double.doubleToLongBits(Taxable);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Tax);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Discount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(TaxCalculated);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Exemption);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (TaxDetails != null ? Arrays.hashCode(TaxDetails) : 0);
        result = 31 * result + (BoundaryLevel != null ? BoundaryLevel.hashCode() : 0);
        return result;
    }
}
