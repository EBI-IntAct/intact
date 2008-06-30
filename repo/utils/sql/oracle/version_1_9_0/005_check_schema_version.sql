-- Checks on the current schema version and aborts if it doesn't match.
--
-- author: Samuel Kerrien (skerrien@ebi.ac.uk)
-- date:   20080-06-30

set serveroutput on

DECLARE

  -- Defines the version of the database we are expecting to find.
  c_expected_schema_version VARCHAR2(15) DEFAULT '1.8.0';

  v_db_version ia_db_info.value%TYPE;

BEGIN

  SELECT value INTO v_db_version
  FROM ia_db_info
  WHERE dbi_key = 'schema_version';

  IF( v_db_version <> c_expected_schema_version ) THEN

    RAISE_APPLICATION_ERROR( -20010, 'Aborting database schema update, schema version expected is '|| c_expected_schema_version ||' but was ' || v_db_version );

  ELSE

    DBMS_OUTPUT.put_line('Current version of the database is: ' || v_db_version );

  END IF;

END;