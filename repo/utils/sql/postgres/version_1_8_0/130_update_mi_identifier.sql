--
-- Update MI_IDENTIFIER field in ia_controlledvocab
--
CREATE OR REPLACE FUNCTION ia_update_mi_identifier() RETURNS VOID AS $$  
DECLARE
	identity_ac ia_controlledvocab.ac%TYPE;
	psimi_ac ia_controlledvocab.ac%TYPE;
	v_count INTEGER;
	v_identifier ia_controlledvocab_xref.primaryid%TYPE;

	cv_rec ia_controlledvocab%ROWTYPE;
    -- Cursor on all CvObjects
    cv_cursor CURSOR  FOR
        SELECT *
        FROM ia_controlledvocab
        order by objclass, shortlabel;
    ---FOR UPDATE OF mi_identifier WAIT 30; -- wait 30 sec. before to throw exception is row is not released.

BEGIN


	-- Get psimi_ac --
	SELECT ac
	INTO psimi_ac
	FROM ia_controlledvocab
	WHERE shortlabel = 'psi-mi'; -- do this using MI:xxxx

	-- Get identity_ac 
	SELECT ac
	INTO identity_ac
	FROM ia_controlledvocab
	WHERE shortlabel = 'identity';

    OPEN cv_cursor;
    LOOP 
		FETCH cv_cursor INTO cv_rec;
		EXIT WHEN   NOT FOUND;

		RAISE NOTICE 'Processing % : % ', cv_rec.objclass, cv_rec.shortlabel;
    
        SELECT count(1) INTO v_count
        FROM ia_controlledvocab_xref x
        WHERE parent_ac = cv_rec.ac
              and x.database_ac = psimi_ac
              and x.qualifier_ac = identity_ac;

        IF ( v_count = 1 ) THEN

            SELECT x.primaryid INTO v_identifier
            FROM ia_controlledvocab_xref x
            WHERE parent_ac = cv_rec.ac
                  and x.database_ac = psimi_ac
                  and x.qualifier_ac = identity_ac;

      		RAISE NOTICE 'Updating % with MI identifier: % ',cv_rec.shortlabel, v_identifier;
            UPDATE ia_controlledvocab
            SET    mi_identifier = v_identifier
			WHERE ia_controlledvocab.ac = cv_rec.ac;
          --  WHERE CURRENT OF cv_cursor;

        ELSE

          RAISE NOTICE 'Could not find an MI identifier for %', cv_rec.shortlabel ;

        END IF;

    END LOOP;


	RAISE NOTICE 'MI identifier update complete.';

END;
$$ LANGUAGE plpgsql;

begin;
select ia_update_mi_identifier();
commit;
