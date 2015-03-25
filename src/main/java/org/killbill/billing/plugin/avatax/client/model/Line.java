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

public class Line {

    public String LineNo; // Required
    public String DestinationCode; // Required
    public String OriginCode; // Required
    public String ItemCode; // Required
    public BigDecimal Qty; // Required
    public BigDecimal Amount; // Required
    public String TaxCode; // Best practice
    public String CustomerUsageType;
    public String Description; // Best Practice
    public Boolean Discounted;
    public Boolean TaxIncluded;
    public String Ref1;
    public String Ref2;
    public String BusinessIdentificationNo;
    public TaxOverrideDef TaxOverride;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Line{");
        sb.append("LineNo='").append(LineNo).append('\'');
        sb.append(", DestinationCode='").append(DestinationCode).append('\'');
        sb.append(", OriginCode='").append(OriginCode).append('\'');
        sb.append(", ItemCode='").append(ItemCode).append('\'');
        sb.append(", Qty=").append(Qty);
        sb.append(", Amount=").append(Amount);
        sb.append(", TaxCode='").append(TaxCode).append('\'');
        sb.append(", CustomerUsageType='").append(CustomerUsageType).append('\'');
        sb.append(", Description='").append(Description).append('\'');
        sb.append(", Discounted=").append(Discounted);
        sb.append(", TaxIncluded=").append(TaxIncluded);
        sb.append(", Ref1='").append(Ref1).append('\'');
        sb.append(", Ref2='").append(Ref2).append('\'');
        sb.append(", BusinessIdentificationNo='").append(BusinessIdentificationNo).append('\'');
        sb.append(", TaxOverride=").append(TaxOverride);
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

        final Line line = (Line) o;

        if (Amount != null ? !Amount.equals(line.Amount) : line.Amount != null) {
            return false;
        }
        if (BusinessIdentificationNo != null ? !BusinessIdentificationNo.equals(line.BusinessIdentificationNo) : line.BusinessIdentificationNo != null) {
            return false;
        }
        if (CustomerUsageType != null ? !CustomerUsageType.equals(line.CustomerUsageType) : line.CustomerUsageType != null) {
            return false;
        }
        if (Description != null ? !Description.equals(line.Description) : line.Description != null) {
            return false;
        }
        if (DestinationCode != null ? !DestinationCode.equals(line.DestinationCode) : line.DestinationCode != null) {
            return false;
        }
        if (Discounted != null ? !Discounted.equals(line.Discounted) : line.Discounted != null) {
            return false;
        }
        if (ItemCode != null ? !ItemCode.equals(line.ItemCode) : line.ItemCode != null) {
            return false;
        }
        if (LineNo != null ? !LineNo.equals(line.LineNo) : line.LineNo != null) {
            return false;
        }
        if (OriginCode != null ? !OriginCode.equals(line.OriginCode) : line.OriginCode != null) {
            return false;
        }
        if (Qty != null ? !Qty.equals(line.Qty) : line.Qty != null) {
            return false;
        }
        if (Ref1 != null ? !Ref1.equals(line.Ref1) : line.Ref1 != null) {
            return false;
        }
        if (Ref2 != null ? !Ref2.equals(line.Ref2) : line.Ref2 != null) {
            return false;
        }
        if (TaxCode != null ? !TaxCode.equals(line.TaxCode) : line.TaxCode != null) {
            return false;
        }
        if (TaxIncluded != null ? !TaxIncluded.equals(line.TaxIncluded) : line.TaxIncluded != null) {
            return false;
        }
        if (TaxOverride != null ? !TaxOverride.equals(line.TaxOverride) : line.TaxOverride != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = LineNo != null ? LineNo.hashCode() : 0;
        result = 31 * result + (DestinationCode != null ? DestinationCode.hashCode() : 0);
        result = 31 * result + (OriginCode != null ? OriginCode.hashCode() : 0);
        result = 31 * result + (ItemCode != null ? ItemCode.hashCode() : 0);
        result = 31 * result + (Qty != null ? Qty.hashCode() : 0);
        result = 31 * result + (Amount != null ? Amount.hashCode() : 0);
        result = 31 * result + (TaxCode != null ? TaxCode.hashCode() : 0);
        result = 31 * result + (CustomerUsageType != null ? CustomerUsageType.hashCode() : 0);
        result = 31 * result + (Description != null ? Description.hashCode() : 0);
        result = 31 * result + (Discounted != null ? Discounted.hashCode() : 0);
        result = 31 * result + (TaxIncluded != null ? TaxIncluded.hashCode() : 0);
        result = 31 * result + (Ref1 != null ? Ref1.hashCode() : 0);
        result = 31 * result + (Ref2 != null ? Ref2.hashCode() : 0);
        result = 31 * result + (BusinessIdentificationNo != null ? BusinessIdentificationNo.hashCode() : 0);
        result = 31 * result + (TaxOverride != null ? TaxOverride.hashCode() : 0);
        return result;
    }
}
