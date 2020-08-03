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

package org.killbill.billing.plugin.avatax.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.killbill.billing.plugin.avatax.client.model.PingResult;
import org.killbill.billing.plugin.avatax.client.model.TaxRateResult;
import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;
import org.killbill.billing.plugin.util.http.HttpClient;
import org.killbill.billing.plugin.util.http.InvalidRequest;
import org.killbill.billing.plugin.util.http.ResponseFormat;

import com.google.common.collect.ImmutableMap;

public class TaxRatesClient extends HttpClient {

    public static final String KILL_BILL_CLIENT_HEADER = "Kill Bill; 2.0; killbill-avatax; 2.0; NA";

    public TaxRatesClient(final Properties properties) throws GeneralSecurityException {
        super(properties.getProperty(AvaTaxActivator.TAX_RATES_API_PROPERTY_PREFIX + "url"),
              properties.getProperty(AvaTaxActivator.TAX_RATES_API_PROPERTY_PREFIX + "accountId"),
              properties.getProperty(AvaTaxActivator.TAX_RATES_API_PROPERTY_PREFIX + "licenseKey"),
              properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "proxyHost"),
              ClientUtils.getIntegerProperty(properties, "proxyPort"),
              ClientUtils.getBooleanProperty(properties, "strictSSL"));
    }

    public boolean isConfigured() {
        return url != null;
    }

    public TaxRateResult fromPostal(final String postal, final String country) throws AvaTaxClientException {
        try {
            // See https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Free/TaxRatesByPostalCode/
            return doCall(GET,
                          url + "/taxrates/bypostalcode",
                          null,
                          ImmutableMap.<String, String>of("postalCode", postal,
                                                          "country", country),
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          TaxRateResult.class,
                          ResponseFormat.JSON);
        } catch (final InterruptedException e) {
            throw new AvaTaxClientException(e);
        } catch (final ExecutionException e) {
            throw new AvaTaxClientException(e);
        } catch (final TimeoutException e) {
            throw new AvaTaxClientException(e);
        } catch (final IOException e) {
            throw new AvaTaxClientException(e);
        } catch (final URISyntaxException e) {
            throw new AvaTaxClientException(e);
        } catch (final InvalidRequest e) {
            throw new AvaTaxClientException(e);
        }
    }

    public TaxRateResult fromAddress(final String street, final String city, final String state, final String postal, final String country) throws AvaTaxClientException {
        final ImmutableMap.Builder<String, String> queryParams = ImmutableMap.<String, String>builder();
        queryParams.put("line1", street);
        queryParams.put("city", city);
        queryParams.put("region", state);
        queryParams.put("postalCode", postal);
        queryParams.put("country", country);

        try {
            // See https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Free/TaxRatesByAddress/
            return doCall(GET,
                          url + "/taxrates/byaddress",
                          null,
                          queryParams.build(),
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          TaxRateResult.class,
                          ResponseFormat.JSON);
        } catch (final InterruptedException e) {
            throw new AvaTaxClientException(e);
        } catch (final ExecutionException e) {
            throw new AvaTaxClientException(e);
        } catch (final TimeoutException e) {
            throw new AvaTaxClientException(e);
        } catch (final IOException e) {
            throw new AvaTaxClientException(e);
        } catch (final URISyntaxException e) {
            throw new AvaTaxClientException(e);
        } catch (final InvalidRequest e) {
            String message = e.getMessage();
            if (e.getResponse() != null) {
                final String responseBody = e.getResponse().getResponseBody();
                if (responseBody != null) {
                    message += "[" + responseBody + "]";
                }
            }
            throw new AvaTaxClientException(message, e);
        }
    }

    public PingResult ping() throws AvaTaxClientException {
        try {
            // See https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Utilities/Ping/
            return doCall(GET,
                          url + "/utilities/ping",
                          null,
                          ImmutableMap.<String, String>of(),
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          PingResult.class,
                          ResponseFormat.JSON);
        } catch (final InterruptedException e) {
            throw new AvaTaxClientException(e);
        } catch (final ExecutionException e) {
            throw new AvaTaxClientException(e);
        } catch (final TimeoutException e) {
            throw new AvaTaxClientException(e);
        } catch (final IOException e) {
            throw new AvaTaxClientException(e);
        } catch (final URISyntaxException e) {
            throw new AvaTaxClientException(e);
        } catch (final InvalidRequest e) {
            throw new AvaTaxClientException(e);
        }
    }
}
