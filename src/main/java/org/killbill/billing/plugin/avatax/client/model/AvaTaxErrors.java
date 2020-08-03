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

public class AvaTaxErrors {

    public AvaTaxError error;

    @Override
    public String toString() {
        return "AvaTaxErrors{" +
               "error=" + error +
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

        final AvaTaxErrors that = (AvaTaxErrors) o;

        return error != null ? error.equals(that.error) : that.error == null;
    }

    @Override
    public int hashCode() {
        return error != null ? error.hashCode() : 0;
    }

    public static class AvaTaxError {

        public String code;
        public String message;
        public String target;
        public AvaTaxErrorDetail[] details;

        @Override
        public String toString() {
            return "AvaTaxError{" +
                   "code='" + code + '\'' +
                   ", message='" + message + '\'' +
                   ", target='" + target + '\'' +
                   ", details=" + Arrays.toString(details) +
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

            final AvaTaxError that = (AvaTaxError) o;

            if (code != null ? !code.equals(that.code) : that.code != null) {
                return false;
            }
            if (message != null ? !message.equals(that.message) : that.message != null) {
                return false;
            }
            if (target != null ? !target.equals(that.target) : that.target != null) {
                return false;
            }
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(details, that.details);
        }

        @Override
        public int hashCode() {
            int result = code != null ? code.hashCode() : 0;
            result = 31 * result + (message != null ? message.hashCode() : 0);
            result = 31 * result + (target != null ? target.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(details);
            return result;
        }
    }

    public static class AvaTaxErrorDetail {

        public String code;
        public int number;
        public String message;
        public String description;
        public String faultCode;
        public String helpLink;
        public String severity;

        @Override
        public String toString() {
            return "AvaTaxErrorDetail{" +
                   "code='" + code + '\'' +
                   ", number=" + number +
                   ", message='" + message + '\'' +
                   ", description='" + description + '\'' +
                   ", faultCode='" + faultCode + '\'' +
                   ", helpLink='" + helpLink + '\'' +
                   ", severity='" + severity + '\'' +
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

            final AvaTaxErrorDetail that = (AvaTaxErrorDetail) o;

            if (number != that.number) {
                return false;
            }
            if (code != null ? !code.equals(that.code) : that.code != null) {
                return false;
            }
            if (message != null ? !message.equals(that.message) : that.message != null) {
                return false;
            }
            if (description != null ? !description.equals(that.description) : that.description != null) {
                return false;
            }
            if (faultCode != null ? !faultCode.equals(that.faultCode) : that.faultCode != null) {
                return false;
            }
            if (helpLink != null ? !helpLink.equals(that.helpLink) : that.helpLink != null) {
                return false;
            }
            return severity != null ? severity.equals(that.severity) : that.severity == null;
        }

        @Override
        public int hashCode() {
            int result = code != null ? code.hashCode() : 0;
            result = 31 * result + number;
            result = 31 * result + (message != null ? message.hashCode() : 0);
            result = 31 * result + (description != null ? description.hashCode() : 0);
            result = 31 * result + (faultCode != null ? faultCode.hashCode() : 0);
            result = 31 * result + (helpLink != null ? helpLink.hashCode() : 0);
            result = 31 * result + (severity != null ? severity.hashCode() : 0);
            return result;
        }
    }
}
