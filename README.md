AvaTax plugin
=============

Kill Bill tax plugin for [Avalara AvaTax](http://www.avalara.com/products/avatax/).

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22avatax-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:avatax-plugin`.

Kill Bill compatibility
-----------------------

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.x.y          | 0.14.z            |

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

This plugin also supports the free [Avalara Tax Rates API](http://taxratesapi.avalara.com/).

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
