begin;

\i 010_component_add_identification.sql

UPDATE ia_db_info
set value = '1.3.1'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

