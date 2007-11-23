--
-- Update MI_IDENTIFIER field in ia_controlledvocab
--
DECLARE
	identity_ac ia_controlledvocab.ac%TYPE;
	psimi_ac ia_controlledvocab.ac%TYPE;
	v_count INTEGER;
	v_identifier ia_controlledvocab_xref.primaryid%TYPE;


    -- Cursor on all CvObjects
    CURSOR cv_cursor IS
        SELECT ac, shortlabel, objclass, mi_identifier
        FROM ia_controlledvocab
        order by objclass, shortlabel
    FOR UPDATE OF mi_identifier WAIT 30; -- wait 30 sec. before to throw exception is row is not released.

BEGIN

	DBMS_OUTPUT.ENABLE(1000000000000);

	-- Get psimi_ac --
	SELECT ac
	INTO psimi_ac
	FROM ia_controlledvocab
	WHERE shortlabel = 'psi-mi'; -- do this using MI:xxxx

	-- Get identity_ac --
	SELECT ac
	INTO identity_ac
	FROM ia_controlledvocab
	WHERE shortlabel = 'identity';

    FOR cv_rec IN cv_cursor
    LOOP

        DBMS_OUTPUT.PUT_LINE('Processing '|| cv_rec.objclass ||': '|| cv_rec.shortlabel ||'');

        SELECT count(1) INTO v_count
        FROM ia_controlledvocab_xref x
        WHERE parent_ac = cv_rec.ac
              and x.database_ac = psimi_ac
              and x.qualifier_ac = identity_ac;

        IF ( v_count = 1 ) THEN

            -- search MI identifier for current CV term
            SELECT x.primaryid INTO v_identifier
            FROM ia_controlledvocab_xref x
            WHERE parent_ac = cv_rec.ac
                  and x.database_ac = psimi_ac
                  and x.qualifier_ac = identity_ac;

            DBMS_OUTPUT.PUT_LINE('Updating '|| cv_rec.shortlabel ||' with MI identifier: '|| v_identifier);

            UPDATE ia_controlledvocab
            SET    mi_identifier = v_identifier
            WHERE CURRENT OF cv_cursor;

        ELSE

            DBMS_OUTPUT.PUT_LINE('Could not find an MI identifier for '|| cv_rec.shortlabel );

        END IF;

    END LOOP;

	COMMIT;

    DBMS_OUTPUT.PUT_LINE('MI identifier update complete.');

END;
/
