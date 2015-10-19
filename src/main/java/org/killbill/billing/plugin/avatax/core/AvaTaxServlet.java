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

package org.killbill.billing.plugin.avatax.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.killbill.billing.plugin.avatax.dao.AvaTaxDao;
import org.killbill.billing.plugin.avatax.dao.gen.tables.records.AvataxTaxCodesRecord;
import org.killbill.billing.plugin.core.PluginServlet;
import org.killbill.billing.tenant.api.Tenant;
import org.killbill.clock.Clock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class AvaTaxServlet extends PluginServlet {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static final Pattern TAX_CODES_PATTERN = Pattern.compile("/taxCodes(/([\\w-]+))?");

    private final AvaTaxDao dao;
    private final Clock clock;

    public AvaTaxServlet(final AvaTaxDao dao, final Clock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final Tenant tenant = getTenant(req);
        if (tenant == null) {
            buildNotFoundResponse("No tenant specified", resp);
            return;
        }

        final String pathInfo = req.getPathInfo();
        final Matcher matcher = TAX_CODES_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            final String productName = matcher.group(2);
            if (productName == null) {
                getTaxCodes(tenant, resp);
            } else {
                getTaxCode(productName, tenant, resp);
            }
        } else {
            buildNotFoundResponse("Resource " + pathInfo + " not found", resp);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final Tenant tenant = getTenant(req);
        if (tenant == null) {
            buildNotFoundResponse("No tenant specified", resp);
            return;
        }

        final String pathInfo = req.getPathInfo();
        final Matcher matcher = TAX_CODES_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            addTaxCode(tenant, req, resp);
        } else {
            buildNotFoundResponse("Resource " + pathInfo + " not found", resp);
        }
    }

    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final Tenant tenant = getTenant(req);
        if (tenant == null) {
            buildNotFoundResponse("No tenant specified", resp);
            return;
        }

        final String pathInfo = req.getPathInfo();
        final Matcher matcher = TAX_CODES_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            final String productName = matcher.group(2);
            if (productName != null) {
                deleteTaxCode(productName, tenant, resp);
            } else {
                buildNotFoundResponse("Resource " + pathInfo + " not found", resp);
            }
        } else {
            buildNotFoundResponse("Resource " + pathInfo + " not found", resp);
        }
    }

    private void getTaxCodes(final Tenant tenant, final HttpServletResponse resp) throws IOException {
        final List<AvataxTaxCodesRecord> taxCodesRecords;
        try {
            taxCodesRecords = dao.getTaxCodes(tenant.getId());
        } catch (final SQLException e) {
            buildErrorResponse(e, resp);
            return;
        }

        final byte[] data = jsonMapper.writeValueAsBytes(Lists.<AvataxTaxCodesRecord, TaxCodeJson>transform(taxCodesRecords,
                                                                                                            new Function<AvataxTaxCodesRecord, TaxCodeJson>() {
                                                                                                                @Override
                                                                                                                public TaxCodeJson apply(final AvataxTaxCodesRecord record) {
                                                                                                                    return new TaxCodeJson(record.getProductName(), record.getTaxCode());
                                                                                                                }
                                                                                                            }));

        buildOKResponse(data, resp);
    }

    private void getTaxCode(final String productName, final Tenant tenant, final HttpServletResponse resp) throws IOException {
        final String taxCode;
        try {
            taxCode = dao.getTaxCode(productName, tenant.getId());
        } catch (final SQLException e) {
            buildErrorResponse(e, resp);
            return;
        }

        final TaxCodeJson taxCodeJson = new TaxCodeJson(productName, taxCode);
        final byte[] data = jsonMapper.writeValueAsBytes(taxCodeJson);

        buildOKResponse(data, resp);
    }

    private void addTaxCode(final Tenant tenant, final ServletRequest req, final HttpServletResponse resp) throws IOException {
        final TaxCodeJson taxCodeJson = jsonMapper.readValue(getRequestData(req), TaxCodeJson.class);
        try {
            dao.setTaxCode(taxCodeJson.productName, taxCodeJson.taxCode, clock.getUTCNow(), tenant.getId());
        } catch (final SQLException e) {
            buildErrorResponse(e, resp);
            return;
        }

        buildCreatedResponse("/plugins/killbill-avatax/taxCodes/" + taxCodeJson.productName, resp);
    }

    private void deleteTaxCode(final String productName, final Tenant tenant, final HttpServletResponse resp) throws IOException {
        try {
            dao.setTaxCode(productName, null, clock.getUTCNow(), tenant.getId());
        } catch (final SQLException e) {
            buildErrorResponse(e, resp);
            return;
        }

        buildOKResponse(new byte[]{}, resp);
    }

    private static final class TaxCodeJson {

        public String productName;
        public String taxCode;

        @JsonCreator
        public TaxCodeJson(@JsonProperty("productName") final String productName, @JsonProperty("taxCode") final String taxCode) {
            this.productName = productName;
            this.taxCode = taxCode;
        }
    }
}
