/*! SET storage_engine=INNODB */;

drop table if exists avatax_responses;
create table avatax_responses (
  record_id serial unique
, kb_account_id char(36) not null
, kb_invoice_id char(36) not null
, kb_invoice_item_ids longtext default null
, doc_code varchar(255) default null
, doc_date datetime default null
, timestamp datetime default null
, total_amount numeric(15,9) default null
, total_discount numeric(15,9) default null
, total_exemption numeric(15,9) default null
, total_taxable numeric(15,9) default null
, total_tax numeric(15,9) default null
, total_tax_calculated numeric(15,9) default null
, tax_date datetime default null
, tax_lines longtext default null
, tax_summary longtext default null
, tax_addresses longtext default null
, result_code varchar(255) default null
, messages longtext default null
, additional_data longtext default null
, created_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create index avatax_responses_kb_account_id on avatax_responses(kb_account_id);
create index avatax_responses_kb_invoice_id on avatax_responses(kb_invoice_id);

drop table if exists avatax_tax_codes;
create table avatax_tax_codes (
  record_id serial unique
, product_name varchar(255) not null
, tax_code varchar(255) not null
, created_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create index avatax_tax_codes_product_name on avatax_tax_codes(product_name);
create unique index avatax_tax_codes_product_name_tax_code on avatax_tax_codes(product_name, tax_code);
