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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/RateModel/
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class RateModel {

    public double rate;
    public String name;
    public String type;

    @Override
    public String toString() {
        return "RateModel{" +
               "rate=" + rate +
               ", name='" + name + '\'' +
               ", type='" + type + '\'' +
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

        final RateModel rateModel = (RateModel) o;

        if (Double.compare(rateModel.rate, rate) != 0) {
            return false;
        }
        if (name != null ? !name.equals(rateModel.name) : rateModel.name != null) {
            return false;
        }
        return type != null ? type.equals(rateModel.type) : rateModel.type == null;
    }

    @Override
    public int hashCode() {
        int result;
        final long temp;
        temp = Double.doubleToLongBits(rate);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
