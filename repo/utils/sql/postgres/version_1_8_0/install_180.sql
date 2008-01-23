begin;

-- "Creating new table ia_confidence..."
\i010_create_tables.sql

-- "Creating audit tables for new tables..."
-- \i050_create_audit_tables.sql


-- "Adding new field in ia_controlledvocab..."
--
\i100_update_cv_table.sql


-- "Updating ia_controlledvocab audit table..."
--
-- \i110_update_cv_audit_table.sql


-- "Updating ia_controlledvocab audit trigger..."
--
-- \i120_create_triggers.sql


-- "Updating MI identifier in table ia_controlledvocab..."
--
\i130_update_mi_identifier.sql


-- "Add index on MI identifier..."
--
\i140_add_index.sql


-- "Creating public synonyms..."
--
\i150_create_public_synonyms.sql


-- "Updating privileges..."
--
\i160_update_privileges.sql


-- "Update schema version to 1.8.0"
--
UPDATE ia_db_info
set value = '1.8.0'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;

