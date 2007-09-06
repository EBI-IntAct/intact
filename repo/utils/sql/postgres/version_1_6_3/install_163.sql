begin;
SELECT '********************************************************************************';
SELECT '"Creating new tables required for migration..."';

\i 100_create_tables.sql



SELECT '********************************************************************************';
SELECT '"updating priviledges on new tables..."';
\i 160_update_privileges.sql


SELECT '********************************************************************************';
SELECT '"Update schema version to 1.6.3"';
UPDATE ia_db_info
set value = '1.6.3'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;
