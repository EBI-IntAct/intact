begin;

\i 010_create_alias_tables.sql


\i 020_component_modifications.sql


\i 030_create_alias_deletion_triggers.sql

\i 040_create_xref_deletion_triggers.sql

\i 090_grant_permissions.sql

\i 110_create_procedure_split_alias.sql

\i 130_create_procedure_split_xref.sql

\i 150_run_alias_table_split.sql

\i 160_run_xref_table_split.sql




UPDATE ia_db_info
set value = '1.3.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';

commit;
