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

public class TaxOverrideDef {

    public String TaxOverrideType; // Limited permitted values: TaxAmount, Exemption, TaxDate
    public String Reason;
    public String TaxAmount; // If included, must be valid decimal
    public String TaxDate; // If included, must be valid date

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxOverrideDef{");
        sb.append("TaxOverrideType='").append(TaxOverrideType).append('\'');
        sb.append(", Reason='").append(Reason).append('\'');
        sb.append(", TaxAmount='").append(TaxAmount).append('\'');
        sb.append(", TaxDate='").append(TaxDate).append('\'');
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

        final TaxOverrideDef that = (TaxOverrideDef) o;

        if (Reason != null ? !Reason.equals(that.Reason) : that.Reason != null) {
            return false;
        }
        if (TaxAmount != null ? !TaxAmount.equals(that.TaxAmount) : that.TaxAmount != null) {
            return false;
        }
        if (TaxDate != null ? !TaxDate.equals(that.TaxDate) : that.TaxDate != null) {
            return false;
        }
        if (TaxOverrideType != null ? !TaxOverrideType.equals(that.TaxOverrideType) : that.TaxOverrideType != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = TaxOverrideType != null ? TaxOverrideType.hashCode() : 0;
        result = 31 * result + (Reason != null ? Reason.hashCode() : 0);
        result = 31 * result + (TaxAmount != null ? TaxAmount.hashCode() : 0);
        result = 31 * result + (TaxDate != null ? TaxDate.hashCode() : 0);
        return result;
    }
}
