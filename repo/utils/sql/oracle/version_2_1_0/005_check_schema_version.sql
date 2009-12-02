-- Checks on the current schema version and aborts if it doesn't match.
--
-- author: Samuel Kerrien (skerrien@ebi.ac.uk)
-- date:   2009-12-02

set serveroutput on

DECLARE

  -- Defines the version of the database we are expecting to find.
  c_expected_schema_version1 VARCHAR2(15) DEFAULT '1.9.0';
  c_expected_schema_version2 VARCHAR2(15) DEFAULT '2.0.0';

  v_db_version ia_db_info.value%TYPE;

BEGIN

  SELECT value INTO v_db_version
  FROM ia_db_info
  WHERE dbi_key = 'schema_version';

  IF( v_db_version <> c_expected_schema_version1 AND  v_db_version <> c_expected_schema_version2 ) THEN

    RAISE_APPLICATION_ERROR( -20010, 'Aborting database schema update, schema version expected is '||
                                     c_expected_schema_version1 ||' or '|| c_expected_schema_version2 ||
                                     'but was ' || v_db_version );

  ELSE

    DBMS_OUTPUT.put_line('Current version of the database is: ' || v_db_version );

  END IF;

END;
/
