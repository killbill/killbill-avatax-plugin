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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/TaxOverrideModel/
public class TaxOverrideModel {

    public String type;
    public String reason;
    public String taxAmount;
    public String taxDate;

    @Override
    public String toString() {
        return "TaxOverrideModel{" +
               "type='" + type + '\'' +
               ", reason='" + reason + '\'' +
               ", taxAmount='" + taxAmount + '\'' +
               ", taxDate='" + taxDate + '\'' +
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

        final TaxOverrideModel that = (TaxOverrideModel) o;

        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) {
            return false;
        }
        if (taxAmount != null ? !taxAmount.equals(that.taxAmount) : that.taxAmount != null) {
            return false;
        }
        return taxDate != null ? taxDate.equals(that.taxDate) : that.taxDate == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (taxAmount != null ? taxAmount.hashCode() : 0);
        result = 31 * result + (taxDate != null ? taxDate.hashCode() : 0);
        return result;
    }
}
