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

package org.killbill.billing.plugin.avatax.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.killbill.billing.plugin.avatax.client.model.Address;
import org.killbill.billing.plugin.avatax.client.model.CancelTaxRequest;
import org.killbill.billing.plugin.avatax.client.model.CancelTaxResult;
import org.killbill.billing.plugin.avatax.client.model.GeoTaxResult;
import org.killbill.billing.plugin.avatax.client.model.GetTaxRequest;
import org.killbill.billing.plugin.avatax.client.model.GetTaxResult;
import org.killbill.billing.plugin.avatax.client.model.ValidateResult;
import org.killbill.billing.plugin.util.http.HttpClient;
import org.killbill.billing.plugin.util.http.InvalidRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class AvaTaxClient extends HttpClient {

    public AvaTaxClient(final String url,
                        final String username,
                        final String password,
                        final String proxyHost,
                        final Integer proxyPort,
                        final Boolean strictSSL) throws GeneralSecurityException {
        super(url, username, password, proxyHost, proxyPort, strictSSL);
    }

    @Override
    protected ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = super.createObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return objectMapper;
    }

    public ValidateResult validateAddress(final Address address) throws AvaTaxClientException {
        final ImmutableMap.Builder<String, String> immutableMapBuilder = ImmutableMap.<String, String>builder();
        immutableMapBuilder.put("Line1", address.Line1);
        immutableMapBuilder.put("Line2", address.Line2);
        immutableMapBuilder.put("Line3", address.Line3);
        immutableMapBuilder.put("City", address.City);
        immutableMapBuilder.put("Region", address.Region);
        immutableMapBuilder.put("PostalCode", address.PostalCode);
        immutableMapBuilder.put("Country", address.Country);

        try {
            return doCall(GET,
                          url + "/1.0/address/validate",
                          null,
                          immutableMapBuilder.build(),
                          ValidateResult.class);
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
            try {
                return deserializeResponse(e.getResponse(), ValidateResult.class);
            } catch (final IOException e1) {
                throw new AvaTaxClientException(e1);
            }
        }
    }

    public GetTaxResult getTax(final GetTaxRequest taxRequest) throws AvaTaxClientException {
        try {
            return doCall(POST,
                          url + "/1.0/tax/get",
                          serialize(taxRequest),
                          DEFAULT_OPTIONS,
                          GetTaxResult.class);
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
            try {
                return deserializeResponse(e.getResponse(), GetTaxResult.class);
            } catch (final IOException e1) {
                throw new AvaTaxClientException(e1);
            }
        }
    }

    public CancelTaxResult cancelTax(final CancelTaxRequest cancelTaxRequest) throws AvaTaxClientException {
        // Note: tax/cancel will return a 200 response even if the document could not be cancelled.
        // Special attention needs to be paid to the ResultCode.
        try {
            return doCall(POST,
                          url + "/1.0/tax/cancel",
                          serialize(cancelTaxRequest),
                          DEFAULT_OPTIONS,
                          CancelTaxResult.class);
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
            try {
                return deserializeResponse(e.getResponse(), CancelTaxResult.class);
            } catch (final IOException e1) {
                throw new AvaTaxClientException(e1);
            }
        }
    }

    public GeoTaxResult estimateTax(final Double latitude, final Double longitude, final Double saleAmount) throws AvaTaxClientException {
        try {
            return doCall(GET,
                          url + "/1.0/tax/" + latitude.toString() + "," + longitude.toString() + "/get",
                          null,
                          ImmutableMap.<String, String>of("saleamount", saleAmount.toString()),
                          GeoTaxResult.class);
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
            try {
                return deserializeResponse(e.getResponse(), GeoTaxResult.class);
            } catch (final IOException e1) {
                throw new AvaTaxClientException(e1);
            }
        }
    }

    private String serialize(final Object o) throws AvaTaxClientException {
        try {
            return mapper.writeValueAsString(o);
        } catch (final JsonProcessingException e) {
            throw new AvaTaxClientException(e);
        }
    }
}
