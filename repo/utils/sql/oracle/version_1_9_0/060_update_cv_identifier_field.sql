-- Update IDENTIFIER field in ia_controlledvocab
--
-- How do we do it ?
-- The idea is that all CVs should have an identifier.
-- The identifier is the identifier of the sole Xref( CvQualifierXref(identity) ).
-- If more than one is available, PSI-MI is the first choice.
--
-- author: Samuel Kerrien (skerrien@ebi.ac.uk)
-- date:   20080-06-30

-- UPDATE ia_controlledvocab SET mi_identifier = NULL;

DECLARE

    v_count_many_identity   INTEGER := 0;
    v_count_update   INTEGER := 0;
    v_count_terms    INTEGER := 0;

    identity_ac      ia_controlledvocab.ac%TYPE;
    psimi_ac         ia_controlledvocab.ac%TYPE;
    qualifier_ac     ia_controlledvocab_xref.qualifier_ac%TYPE;
    v_count          INTEGER;

    v_primaryid      ia_controlledvocab_xref.primaryid%TYPE;
    v_database_ac    ia_controlledvocab_xref.database_ac%TYPE;
    v_qualifier_ac   ia_controlledvocab_xref.qualifier_ac%TYPE;

    v_identifier     ia_controlledvocab_xref.primaryid%TYPE;
    v_psi_identifier ia_controlledvocab_xref.primaryid%TYPE;

    -- Cursor on all CvObjects
    CURSOR cv_cursor IS
        SELECT ac, shortlabel, objclass, mi_identifier
        FROM ia_controlledvocab
        order by objclass, shortlabel
    FOR UPDATE OF mi_identifier WAIT 30; -- wait 30 sec. before to throw exception is row is not released.

    -- Cursor on identty xref of Cv objects
    CURSOR cv_identity_cursor (p_cv_ac     ia_controlledvocab.ac%TYPE,
                               p_qualifier ia_controlledvocab_xref.qualifier_ac%TYPE)
    IS
        SELECT primaryid, database_ac, qualifier_ac
        FROM ia_controlledvocab_xref x
        WHERE     parent_ac = p_cv_ac
              and qualifier_ac = p_qualifier
    FOR UPDATE OF qualifier_ac WAIT 30; -- wait 30 sec. before to throw exception is row is not released.

BEGIN

	DBMS_OUTPUT.ENABLE(1000000000000);

	-- Get psimi_ac --
	SELECT cv.ac INTO psimi_ac
	FROM ia_controlledvocab cv, ia_controlledvocab_xref x
	WHERE     cv.ac = x.parent_ac
        and primaryId = 'MI:0488'; -- psi-mi

  DBMS_OUTPUT.PUT_LINE('Found CvDatabase( psi-mi ) by MI identifier (MI:0488):  '|| psimi_ac );

	-- Get identity_ac --
	SELECT cv.ac	INTO identity_ac
	FROM ia_controlledvocab cv, ia_controlledvocab_xref x
	WHERE     cv.ac = x.parent_ac
        and primaryid = 'MI:0356'; -- identity

  DBMS_OUTPUT.PUT_LINE('Found CvXrefQualifier( identity ) by MI identifier (MI:0356):  '|| identity_ac );

  FOR cv_rec IN cv_cursor
  LOOP

      -- initialize variables
      v_psi_identifier := NULL;
      v_identifier := NULL;
      v_count := 0;

      DBMS_OUTPUT.PUT_LINE( chr(10)||'Processing '|| cv_rec.objclass ||': '|| cv_rec.shortlabel ||'');
      v_count_terms := v_count_terms + 1;

      IF ( cv_rec.mi_identifier IS NULL) THEN

          DBMS_OUTPUT.PUT_LINE( '   ' || cv_rec.shortlabel || ' doesn''t have an identifier. Looking for its identity...');

          -- fetch and iterate over all identities
          OPEN cv_identity_cursor( cv_rec.ac, identity_ac );
          LOOP
            FETCH cv_identity_cursor INTO v_primaryid, v_database_ac, v_qualifier_ac ;
            EXIT WHEN v_psi_identifier IS NOT NULL OR cv_identity_cursor%NOTFOUND;

            IF( v_database_ac = psimi_ac ) THEN
                -- TODO check that it's not already set !!
                v_psi_identifier := v_primaryid;
            ELSE
                v_identifier := v_primaryid;
            END IF;

            -- count identities
            v_count := v_count + 1;

          END LOOP;
          CLOSE cv_identity_cursor;

          IF( v_psi_identifier IS NOT NULL ) THEN

              DBMS_OUTPUT.PUT_LINE('   Updating '|| cv_rec.shortlabel ||' with MI identifier: '|| v_psi_identifier);

              --UPDATE ia_controlledvocab
              --SET    mi_identifier = v_psi_identifier
              --WHERE CURRENT OF cv_cursor;

              v_count_update := v_count_update + 1;

          ELSIF( v_identifier IS NOT NULL ) THEN

              IF( v_count = 1 ) THEN

                  DBMS_OUTPUT.PUT_LINE('   Updating '|| cv_rec.shortlabel ||' with non MI identifier: '|| v_identifier);
                  --UPDATE ia_controlledvocab
                  --SET    mi_identifier = v_identifier
                  --WHERE CURRENT OF cv_cursor;

                  v_count_update := v_count_update + 1;

              ELSE

                  --UPDATE ia_controlledvocab
                  --SET    mi_identifier = v_identifier
                  --WHERE CURRENT OF cv_cursor;

                  DBMS_OUTPUT.PUT_LINE('   Updating '|| cv_rec.shortlabel ||' with non MI identifier: '|| v_identifier || ' (chosen amongst many !!!)');
                  v_count_many_identity := v_count_many_identity + 1;
                  -- more than one NON PSI identity found, do fail.
                  -- RAISE_APPLICATION_ERROR( -20010, 'Found a Controlled vicabulary term with more than one non psi-mi identity: ' || cv_rec.ac );

              END IF;

          END IF;

          DBMS_OUTPUT.PUT_LINE('   Updating '|| cv_rec.shortlabel ||' with MI identifier: '|| v_identifier);

      ELSE

          DBMS_OUTPUT.PUT_LINE('   '|| cv_rec.shortlabel ||' has already an identifier: '|| cv_rec.mi_identifier );

      END IF; -- mi_identifier not null

  END LOOP;

	COMMIT;

  DBMS_OUTPUT.PUT_LINE('MI identifier update complete.');

  DBMS_OUTPUT.PUT_LINE('');
  DBMS_OUTPUT.PUT_LINE('Statistics');
  DBMS_OUTPUT.PUT_LINE('----------');
  DBMS_OUTPUT.PUT_LINE('Count of term processed: ' || v_count_terms);
  DBMS_OUTPUT.PUT_LINE('Count of term updated:   ' || v_count_update);
  DBMS_OUTPUT.PUT_LINE('Count of term updated that had more than one identity:   ' || v_count_many_identity);

END;
/



