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

public class CommonResponse {

    public enum SeverityLevel {
        Success,
        Warning,
        Error,
        Exception;
    }

    public static class Message {

        public String Summary;
        public String Details;
        public String RefersTo;
        public SeverityLevel Severity;
        public String Source;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Message{");
            sb.append("Summary='").append(Summary).append('\'');
            sb.append(", Details='").append(Details).append('\'');
            sb.append(", RefersTo='").append(RefersTo).append('\'');
            sb.append(", Severity=").append(Severity);
            sb.append(", Source='").append(Source).append('\'');
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

            final Message message = (Message) o;

            if (Details != null ? !Details.equals(message.Details) : message.Details != null) {
                return false;
            }
            if (RefersTo != null ? !RefersTo.equals(message.RefersTo) : message.RefersTo != null) {
                return false;
            }
            if (Severity != message.Severity) {
                return false;
            }
            if (Source != null ? !Source.equals(message.Source) : message.Source != null) {
                return false;
            }
            if (Summary != null ? !Summary.equals(message.Summary) : message.Summary != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = Summary != null ? Summary.hashCode() : 0;
            result = 31 * result + (Details != null ? Details.hashCode() : 0);
            result = 31 * result + (RefersTo != null ? RefersTo.hashCode() : 0);
            result = 31 * result + (Severity != null ? Severity.hashCode() : 0);
            result = 31 * result + (Source != null ? Source.hashCode() : 0);
            return result;
        }
    }
}
