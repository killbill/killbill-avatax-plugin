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

// https://developer.avalara.com/api-reference/avatax/rest/v2/models/AvaTaxMessage/
public class AvaTaxMessage {

    public String summary;
    public String details;
    public String refersTo;
    public String severity;
    public String source;

    @Override
    public String toString() {
        return "AvaTaxMessage{" +
               "summary='" + summary + '\'' +
               ", details='" + details + '\'' +
               ", refersTo='" + refersTo + '\'' +
               ", severity='" + severity + '\'' +
               ", source='" + source + '\'' +
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

        final AvaTaxMessage that = (AvaTaxMessage) o;

        if (summary != null ? !summary.equals(that.summary) : that.summary != null) {
            return false;
        }
        if (details != null ? !details.equals(that.details) : that.details != null) {
            return false;
        }
        if (refersTo != null ? !refersTo.equals(that.refersTo) : that.refersTo != null) {
            return false;
        }
        if (severity != null ? !severity.equals(that.severity) : that.severity != null) {
            return false;
        }
        return source != null ? source.equals(that.source) : that.source == null;
    }

    @Override
    public int hashCode() {
        int result = summary != null ? summary.hashCode() : 0;
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (refersTo != null ? refersTo.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }
}
