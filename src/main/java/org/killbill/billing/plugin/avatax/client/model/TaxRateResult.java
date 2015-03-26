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

import java.util.List;

public class TaxRateResult {

    public double totalRate;
    public List<JurisTaxRate> rates;

    public String simplifiedToString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("totalRate=").append(totalRate);
        if (rates != null) {
            sb.append(", rates=[");
            boolean first = true;
            for (final JurisTaxRate rate : rates) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(rate.rate);
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaxRateResult{");
        sb.append("totalRate=").append(totalRate);
        sb.append(", rates=").append(rates);
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

        final TaxRateResult that = (TaxRateResult) o;

        if (Double.compare(that.totalRate, totalRate) != 0) {
            return false;
        }
        if (rates != null ? !rates.equals(that.rates) : that.rates != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        final long temp;
        temp = Double.doubleToLongBits(totalRate);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (rates != null ? rates.hashCode() : 0);
        return result;
    }
}
