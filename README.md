# AvaTax plugin
![Maven Central](https://img.shields.io/maven-central/v/org.kill-bill.billing.plugin.java/avatax-plugin?color=blue&label=Maven%20Central)

Kill Bill tax plugin for [Avalara AvaTax](https://www.avalara.com/us/en/products/calculations.html).

This integration delegates computation of sales taxes to Avalara, which will appear directly on Kill Bill invoices.

The easiest way to get started with the AvaTax plugin is to take a look at our [AvaTax Plugin Manual](https://docs.killbill.io/latest/avatax-plugin) which provides detailed instructions for installing, configuring and using the plugin.

## Kill Bill compatibility

| Plugin version | Kill Bill version | AvaTax API      |
|---------------:|------------------:| --------------: |
|          0.1.y |            0.14.z | Legacy REST API |
|          0.2.y |            0.15.z | Legacy REST API |
|          0.3.y |            0.16.z | Legacy REST API |
|          0.4.y |            0.18.z | Legacy REST API |
|          0.5.y |            0.19.z | Legacy REST API |
|          0.6.y |            0.20.z | Legacy REST API |
|          0.7.y |            0.22.z | Legacy REST API |
|          0.8.y |            0.22.z | REST API        |
|          0.9.y |            0.24.z | REST API        |

We've upgraded numerous dependencies in 0.8.x (required for Java 11 support).

## Requirements

* An active Avatax account is required to use the plugin. 
* The plugin needs a database. The database tables are automatically created and updated at plugin startup by default. Alternatively, if you would like to manage the database schema manually, you can disable automatic migrations and use the SQL scripts provided in the [src/main/resources/migration](src/main/resources/migration) directory to create or update the database tables as needed. See the [Database Setup](#database-setup) section below for details about disabling automatic migrations.

## Development

To install the plugin from sources:

```
mvn clean install -DskipTests=true
kpm install_java_plugin avatax --from-source-file target/avatax-plugin-*-SNAPSHOT.jar --destination /var/tmp/bundles
```

You must then enable globally the plugin in Kill Bill (`killbill.properties` file):

```
org.killbill.invoice.plugin=killbill-avatax
```

## Configuration

The AvaTax plugin requires some properties to be configured either globally or on a per-tenant basis. 

The following properties are mandatory:

* `org.killbill.billing.plugin.avatax.url`: AvaTax endpoint (e.g. https://sandbox-rest.avatax.com/api/v2)
* `org.killbill.billing.plugin.avatax.accountId`: your AvaTax account number
* `org.killbill.billing.plugin.avatax.licenseKey`: your license key

The following properties are optional:

* `org.killbill.billing.plugin.avatax.companyCode`: your default company code (can be passed using the plugin property `companyCode`)
* `org.killbill.billing.plugin.avatax.commitDocuments`: whether invoices should be committed to Avalara
* `org.killbill.billing.plugin.avatax.adjustments.lenientMode`, when true Avatax-plugin skips any adjustment items from Invoice for which the previousInvoiceId is not present (i.e. missing) or else leads to IllegalStateException and fails to generate invoice
* `org.killbill.billing.plugin.avatax.proxyHost`: proxy host
* `org.killbill.billing.plugin.avatax.proxyPort`: proxy port
* `org.killbill.billing.plugin.avatax.strictSSL`: if false, unverified certificates are trusted
* `org.killbill.billing.plugin.avatax.connectTimeout`: maximum time in millisecond the client can wait when connecting to a remote host
* `org.killbill.billing.plugin.avatax.requestTimeout`: maximum time in millisecond the client waits until the response is completed

These properties can be specified globally via System Properties or on a per-tenant basis:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d 'org.killbill.billing.plugin.avatax.url=XXX
org.killbill.billing.plugin.avatax.accountId=YYY
org.killbill.billing.plugin.avatax.licenseKey=ZZZ' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-avatax
```

Refer to the [Avatax Plugin Manual](https://docs.killbill.io/latest/avatax-plugin#plugin_configuration) for further details.

## Database Setup

The Avatax plugin requires a database. By default, schema migrations run automatically at plugin startup.

To skip automatic migrations (for example, if you prefer to manage the database schema manually), ensure that the following property is set before starting the plugin the first time:

```properties
org.killbill.billing.plugin.avatax.runMigrations=false
```


When automatic migrations are disabled, ensure that the required database tables are created manually using the SQL scripts provided in the [src/main/resources/migration](src/main/resources/migration) directory.

## AvaTax tax calculation details

Taxes are calculated by default using the address specified on the Kill Bill account (set as the `shipTo` element). In case your current e-commerce application doesn't validate addresses, you can use [Avalara's Address Validation service](https://developer.avalara.com/avatax/address-validation/) to do it (Avalara will implicitly validate addresses during the tax calculation and fail the invoice creation if the address is invalid).

You can also specify a location code per line item by passing the `locationCode_<INVOICE_ITEM_ID>` plugin property.

Kill Bill will send to Avalara all invoice line items: make sure to configure in the plugin the tax codes associated with your catalog products (see below).

Dry run invoices will contain a preview of the tax items (the associated document won't be committed to Avalara).

Here is how the main Avalara fields map to Kill Bill:

* Customer Code is mapped to the Kill Bill account external key if present, the account id otherwise
* Description is mapped to the Kill Bill invoice id
* Line item number is mapped to the Kill Bill invoice item id
* Line item code is mapped to the Kill Bill invoice item description, or the plan, phase or usage name (first non null)
* Line item Ref1 is mapped to the Kill Bill invoice item id
* Line item Ref2 is mapped to the Kill Bill invoice id

Documents in Avalara are not automatically voided (as this will depend on your dunning configuration).

See also the [AvaTax Kaui Integration](https://docs.killbill.io/latest/avatax-plugin#_kaui_integration), which helps you administrate the plugin via Kaui.

### Marking an account as tax exempt

The plugin allows accounts to be exempted from tax.  Set the `customerUsageType` custom field on the account object (e.g. `E` for charitable or benevolent organizations).

See [Marking an Account as Tax Exempt](https://docs.killbill.io/latest/avatax-plugin#_marking_an_account_as_tax_exempt) for further details.

### Skipping Tax Calculation

You can skip an account entirely by setting the plugin property `AVALARA_SKIP` at runtime (the plugin property value doesn't matter, it just cannot be blank). See [Skipping Taxation](https://docs.killbill.io/latest/avatax-plugin#_skipping_taxation) for further details.

### Setting tax codes

The plugin allows setting tax codes on products. This allows taxing products differently. See [Tax Calculation by Product Tax Code](https://docs.killbill.io/latest/avatax-plugin#_tax_calculation_by_product_tax_code) for further details.

There are several ways to configure tax codes:

* Set the `taxCode_<INVOICE_ITEM_ID>` plugin property
* Set the `taxCode` custom field on the invoice item object
* Store the tax code in the plugin for each product in your catalog via  [Tax Code APIs](https://docs.killbill.io/latest/avatax-plugin#_tax_code_apis)

### Transactions API

The plugin offers convenient pass-through APIs to retrieve, commit, and void documents. These are documented in the [AvaTax manual](https://docs.killbill.io/latest/avatax-plugin#_transaction_apis).

## About

Kill Bill is the leading Open-Source Subscription Billing & Payments Platform. For more information about the project, go to https://killbill.io/.
