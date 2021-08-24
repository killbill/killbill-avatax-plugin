/*
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2021 Equinix, Inc
 * Copyright 2014-2021 The Billing Project, LLC
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

import org.killbill.billing.plugin.avatax.client.model.AvaTaxErrors;
import org.killbill.billing.plugin.avatax.client.model.CreateTransactionModel;
import org.killbill.billing.plugin.avatax.client.model.PingResult;
import org.killbill.billing.plugin.avatax.client.model.TransactionModel;
import org.killbill.billing.plugin.avatax.core.AvaTaxActivator;
import org.killbill.billing.plugin.util.http.HttpClient;
import org.killbill.billing.plugin.util.http.InvalidRequest;
import org.killbill.billing.plugin.util.http.ResponseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

public class AvaTaxClient extends HttpClient {

    public static final String KILL_BILL_CLIENT_HEADER = "Kill Bill; 2.0; killbill-avatax; 2.0; NA";

    private static final Logger logger = LoggerFactory.getLogger(AvaTaxClient.class);

    private final String companyCode;
    private final String sanitizedCompanyCode;
    private final boolean commitDocuments;

    public AvaTaxClient(final Properties properties) throws GeneralSecurityException {
        super(properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "url"),
              properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "accountId"),
              properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "licenseKey"),
              properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "proxyHost"),
              ClientUtils.getIntegerProperty(properties, "proxyPort"),
              ClientUtils.getBooleanProperty(properties, "strictSSL"),
              MoreObjects.firstNonNull(ClientUtils.getIntegerProperty(properties, "connectTimeout"), 10000),
              MoreObjects.firstNonNull(ClientUtils.getIntegerProperty(properties, "readTimeout"), 60000),
              MoreObjects.firstNonNull(ClientUtils.getIntegerProperty(properties, "requestTimeout"), 60000));
        this.companyCode = properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "companyCode", "DEFAULT");
        this.sanitizedCompanyCode = sanitizeCompanyCode(this.companyCode);
        this.commitDocuments = Boolean.parseBoolean(properties.getProperty(AvaTaxActivator.PROPERTY_PREFIX + "commitDocuments"));
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public boolean shouldCommitDocuments() {
        return commitDocuments;
    }

    public boolean isConfigured() {
        return url != null && username != null && password != null;
    }

    @Override
    protected ObjectMapper createObjectMapper() {
        return ClientUtils.createObjectMapper();
    }

    public TransactionModel createTransaction(final CreateTransactionModel createTransactionModel) throws AvaTaxClientException {
        try {
            return doCall(POST,
                          url + "/transactions/create",
                          serialize(createTransactionModel),
                          DEFAULT_OPTIONS,
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          TransactionModel.class,
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
            try {
                final AvaTaxErrors errors = deserializeResponse(e.getResponse(), AvaTaxErrors.class, ResponseFormat.JSON);
                throw new AvaTaxClientException(errors, e);
            } catch (final IOException e1) {
                logger.warn("Invalid AvaTax request: status={}", e.getResponse() == null ? null : e.getResponse().getStatusCode());
                throw new AvaTaxClientException(e);
            }
        }
    }

    public TransactionModel commitTransaction(final String transactionCode) throws AvaTaxClientException {
        logger.info("Committing transaction {}", transactionCode);

        try {
            // See https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Transactions/CommitTransaction/
            return doCall(POST,
                          url + "/companies/" + sanitizedCompanyCode + "/transactions/" + transactionCode + "/commit",
                          serialize(ImmutableMap.<String, Boolean>of("commit", true)),
                          DEFAULT_OPTIONS,
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          TransactionModel.class,
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
            try {
                final AvaTaxErrors errors = deserializeResponse(e.getResponse(), AvaTaxErrors.class, ResponseFormat.JSON);
                throw new AvaTaxClientException(errors, e);
            } catch (final IOException e1) {
                logger.warn("Invalid AvaTax request: status={}", e.getResponse() == null ? null : e.getResponse().getStatusCode());
                throw new AvaTaxClientException(e);
            }
        }
    }

    public TransactionModel voidTransaction(final String transactionCode) throws AvaTaxClientException {
        logger.info("Voiding transaction {}", transactionCode);

        try {
            // See https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Transactions/VoidTransaction/
            return doCall(POST,
                          url + "/companies/" + sanitizedCompanyCode + "/transactions/" + transactionCode + "/void",
                          serialize(ImmutableMap.<String, String>of("code", "DocVoided")),
                          DEFAULT_OPTIONS,
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          TransactionModel.class,
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
            try {
                final AvaTaxErrors errors = deserializeResponse(e.getResponse(), AvaTaxErrors.class, ResponseFormat.JSON);
                throw new AvaTaxClientException(errors, e);
            } catch (final IOException e1) {
                logger.warn("Invalid AvaTax request: status={}", e.getResponse() == null ? null : e.getResponse().getStatusCode());
                throw new AvaTaxClientException(e);
            }
        }
    }

    public TransactionModel getTransactionByCode(final String transactionCode) throws AvaTaxClientException {
        try {
            // See https://developer.avalara.com/api-reference/avatax/rest/v2/methods/Transactions/GetTransactionByCode/
            return doCall(GET,
                          url + "/companies/" + sanitizedCompanyCode + "/transactions/" + transactionCode,
                          null,
                          DEFAULT_OPTIONS,
                          ImmutableMap.<String, String>of("X-Avalara-Client", KILL_BILL_CLIENT_HEADER),
                          TransactionModel.class,
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
            try {
                final AvaTaxErrors errors = deserializeResponse(e.getResponse(), AvaTaxErrors.class, ResponseFormat.JSON);
                throw new AvaTaxClientException(errors, e);
            } catch (final IOException e1) {
                logger.warn("Invalid AvaTax request: status={}", e.getResponse() == null ? null : e.getResponse().getStatusCode());
                throw new AvaTaxClientException(e);
            }
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

    private String serialize(final Object o) throws AvaTaxClientException {
        try {
            return mapper.writeValueAsString(o);
        } catch (final JsonProcessingException e) {
            throw new AvaTaxClientException(e);
        }
    }

    private String sanitizeCompanyCode(final String companyCode) {
        String companyCodeSanitized = companyCode;
        companyCodeSanitized = companyCodeSanitized.replaceAll("/", "_-ava2f-_");
        companyCodeSanitized = companyCodeSanitized.replaceAll("\\+", "_-ava2b-_");
        companyCodeSanitized = companyCodeSanitized.replaceAll("\\?", "_-ava3f-_");
        companyCodeSanitized = companyCodeSanitized.replaceAll("%", "_-ava25-_");
        companyCodeSanitized = companyCodeSanitized.replaceAll("#", "_-ava23-_");
        companyCodeSanitized = companyCodeSanitized.replaceAll(" ", "%20");
        return companyCodeSanitized;
    }
}
