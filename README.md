AvaTax plugin
=============

Kill Bill tax plugin for [Avalara AvaTax](http://www.avalara.com/products/avatax/) and [Avalara Tax Rates API](http://taxratesapi.avalara.com/).

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22avatax-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:avatax-plugin`.

Kill Bill compatibility
-----------------------

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.1.y          | 0.14.z            |

Requirements
------------

The plugin needs a database. The latest version of the schema can be found [here](https://github.com/killbill/killbill-avatax-plugin/blob/master/src/main/resources/ddl.sql).

Configuration
-------------

### Avalara AvaTax

The following properties are required:

* `org.killbill.billing.plugin.avatax.url`: AvaTax endpoint (e.g. https://development.avalara.net)
* `org.killbill.billing.plugin.avatax.accountNumber`: your AvaTax account number
* `org.killbill.billing.plugin.avatax.licenseKey`: your license key

The following properties are optional:

* `org.killbill.billing.plugin.avatax.companyCode`: your default company code (can be passed using the plugin property `companyCode`)

### Tax Rates API

The following properties are required:

* `org.killbill.billing.plugin.avatax.taxratesapi.url`: Tax Rates API endpoint (e.g. https://taxrates.api.avalara.com)
* `org.killbill.billing.plugin.avatax.taxratesapi.apiKey`: your API Key

You can pass the `rateType` plugin property to specify which rate(s) to take into account.

### Common properties

For both APIs, the following properties are optional:

* `org.killbill.billing.plugin.avatax.proxyHost`: proxy host
* `org.killbill.billing.plugin.avatax.proxyPort`: proxy port
* `org.killbill.billing.plugin.avatax.strictSSL`: if false, unverified certificates are trusted

These properties can be specified globally via System Properties or on a per tenant basis:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d 'org.killbill.billing.plugin.avatax.url=XXX
org.killbill.billing.plugin.avatax.accountNumber=YYY
org.killbill.billing.plugin.avatax.licenseKey=ZZZ' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-avatax
```

AvaTax tax calculation details
------------------------------

### Marking an account as tax exempt

Set the `customerUsageType` custom field on the account object (e.g. `E` for charitable or benevolent organizations).

See [Handling tax exempt customers](http://developer.avalara.com/api-docs/designing-your-integration/handling-tax-exempt-customers) for more details.


### Setting tax codes

There are several ways to configure tax codes:

* For external charges, set the `taxCode` custom field on the invoice item object (e.g. `PC040100` for general clothing products)
* For subscriptions, you can store the tax code for each product in your catalog as follows:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: application/json' \
     -d '{"productName":"Super","taxCode":"DC010200"}' \
     http://127.0.0.1:8080/plugins/killbill-avatax/taxCodes
```

See [What Is a Tax Code?](https://help.avalara.com/000_AvaTax_Calc/000AvaTaxCalc_User_Guide/040_Managing_Tax_Profiles/050_Tax_Codes/001_What_is_a_Tax_Code) for more details.
