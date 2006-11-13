set serveroutput on


-- 
-- Procedure copying the alias audit into their respective tables.
-- NOTE: IA_ALIAS has to have been split heforehand into IA_INTERACTOR_ALIAS, IA_EXPERIMENT_ALIAS... 
--
CREATE OR REPLACE PROCEDURE proc_split_alias_audit AS 

-- cursor over all audit Aliases
CURSOR alias_cur IS
SELECT *
FROM ia_alias_audit;

v_alias_rec ia_alias_audit%ROWTYPE;

-- count of rows
v_count INTEGER;

-- specific type of interactor (dicriminator field)
v_interactor_type ia_interactor_audit.objclass%TYPE;

-- specific type of CV (dicriminator field)
v_controlledvocab_type ia_controlledvocab_audit.objclass%TYPE;

-- AC field of the different tables
v_interactor_ac ia_interactor_audit.ac%TYPE;
v_experiment_ac ia_experiment_audit.ac%TYPE;
v_controlledvocab_ac ia_controlledvocab_audit.ac%TYPE;
v_biosource_ac ia_biosource_audit.ac%TYPE;
v_feature_ac ia_feature_audit.ac%TYPE;
v_publication_ac ia_publication_audit.ac%TYPE;

BEGIN

  dbms_output.enable(1000000000000);

  -- TODO clean up all specific alias tables before to fill them up
  -- no check will be done for duplicated rows !!

  OPEN alias_cur;
  LOOP
      FETCH alias_cur INTO v_alias_rec;
      EXIT WHEN alias_cur % NOTFOUND;

      -- and now process a single Alias audit ...

      --
      -- Look for interactor
      --
      SELECT COUNT(1)
      INTO v_count
      FROM ia_interactor_audit
      WHERE ac = v_alias_rec.PARENT_AC;

      IF(v_count <> 0) THEN

        -- copy Alias into the right table
        INSERT INTO ia_interactor_alias_audit (
            EVENT, 
            AC,
            DEPRECATED,
            CREATED,
            UPDATED,
            USERSTAMP,
            ALIASTYPE_AC,
            PARENT_AC,
            OWNER_AC,
            NAME,
            CREATED_USER
        )
        VALUES (
            v_alias_rec.EVENT,
            v_alias_rec.AC,
            v_alias_rec.DEPRECATED,
            v_alias_rec.CREATED,
            v_alias_rec.UPDATED,
            v_alias_rec.USERSTAMP,
            v_alias_rec.ALIASTYPE_AC,
            v_alias_rec.PARENT_AC,
            v_alias_rec.OWNER_AC,
            v_alias_rec.NAME,
            NVL(v_alias_rec.CREATED_USER, 'N/A')
        );

      ELSE

        --
        -- Look for experiment
        --
        SELECT COUNT(1)
        INTO v_count
        FROM ia_experiment_audit
        WHERE ac = v_alias_rec.PARENT_AC;

        IF(v_count <> 0) THEN

            -- copy Alias into the right table
            INSERT INTO ia_experiment_alias_audit (
                EVENT,
                AC,
                DEPRECATED,
                CREATED,
                UPDATED,
                USERSTAMP,
                ALIASTYPE_AC,
                PARENT_AC,
                OWNER_AC,
                NAME,
                CREATED_USER
            )
            VALUES (
                v_alias_rec.EVENT,
                v_alias_rec.AC,
                v_alias_rec.DEPRECATED,
                v_alias_rec.CREATED,
                v_alias_rec.UPDATED,
                v_alias_rec.USERSTAMP,
                v_alias_rec.ALIASTYPE_AC,
                v_alias_rec.PARENT_AC,
                v_alias_rec.OWNER_AC,
                v_alias_rec.NAME,
                NVL(v_alias_rec.CREATED_USER, 'N/A')
            );
        ELSE

          --
          -- Look for controlled vocabulary
          --
          SELECT COUNT(1)
          INTO v_count
          FROM ia_controlledvocab_audit
          WHERE ac = v_alias_rec.PARENT_AC;

          IF(v_count <> 0) THEN

              -- copy Alias into the right table
              INSERT INTO ia_controlledvocab_alias_audit (
                    EVENT,
                    AC,
                    DEPRECATED,
                    CREATED,
                    UPDATED,
                    USERSTAMP,
                    ALIASTYPE_AC,
                    PARENT_AC,
                    OWNER_AC,
                    NAME,
                    CREATED_USER
                )
                VALUES (
                    v_alias_rec.EVENT,
                    v_alias_rec.AC,
                    v_alias_rec.DEPRECATED,
                    v_alias_rec.CREATED,
                    v_alias_rec.UPDATED,
                    v_alias_rec.USERSTAMP,
                    v_alias_rec.ALIASTYPE_AC,
                    v_alias_rec.PARENT_AC,
                    v_alias_rec.OWNER_AC,
                    v_alias_rec.NAME,
                    NVL(v_alias_rec.CREATED_USER, 'N/A')
                );

          ELSE

            --
            -- Look for biosource
            --
            SELECT COUNT(1)
            INTO v_count
            FROM ia_biosource_audit
            WHERE ac = v_alias_rec.PARENT_AC;

            IF(v_count <> 0) THEN

                -- copy Alias into the right table
                INSERT INTO ia_biosource_alias_audit (
                    EVENT,
                    AC,
                    DEPRECATED,
                    CREATED,
                    UPDATED,
                    USERSTAMP,
                    ALIASTYPE_AC,
                    PARENT_AC,
                    OWNER_AC,
                    NAME,
                    CREATED_USER
                )
                VALUES (
                    v_alias_rec.EVENT,
                    v_alias_rec.AC,
                    v_alias_rec.DEPRECATED,
                    v_alias_rec.CREATED,
                    v_alias_rec.UPDATED,
                    v_alias_rec.USERSTAMP,
                    v_alias_rec.ALIASTYPE_AC,
                    v_alias_rec.PARENT_AC,
                    v_alias_rec.OWNER_AC,
                    v_alias_rec.NAME,
                    NVL(v_alias_rec.CREATED_USER, 'N/A')
                );

            ELSE

              --
              -- Look for feature
              --
              SELECT COUNT(1)
              INTO v_count
              FROM ia_feature_audit
              WHERE ac = v_alias_rec.PARENT_AC;

              IF(v_count <> 0) THEN

                  -- copy Alias into the right table
                  INSERT INTO ia_feature_alias_audit (
                        EVENT,
                        AC,
                        DEPRECATED,
                        CREATED,
                        UPDATED,
                        USERSTAMP,
                        ALIASTYPE_AC,
                        PARENT_AC,
                        OWNER_AC,
                        NAME,
                        CREATED_USER
                    )
                    VALUES (
                        v_alias_rec.EVENT,
                        v_alias_rec.AC,
                        v_alias_rec.DEPRECATED,
                        v_alias_rec.CREATED,
                        v_alias_rec.UPDATED,
                        v_alias_rec.USERSTAMP,
                        v_alias_rec.ALIASTYPE_AC,
                        v_alias_rec.PARENT_AC,
                        v_alias_rec.OWNER_AC,
                        v_alias_rec.NAME,
                        NVL(v_alias_rec.CREATED_USER, 'N/A')
                    );
              ELSE

                --
                -- Look for publication
                --
                SELECT COUNT(1)
                INTO v_count
                FROM ia_publication_audit
                WHERE ac = v_alias_rec.PARENT_AC;

                IF(v_count <> 0) THEN

                    -- copy Alias into the right table
                    INSERT INTO ia_publication_alias_audit (
                        EVENT,
                        AC,
                        DEPRECATED,
                        CREATED,
                        UPDATED,
                        USERSTAMP,
                        ALIASTYPE_AC,
                        PARENT_AC,
                        OWNER_AC,
                        NAME,
                        CREATED_USER
                    )
                    VALUES (
                        v_alias_rec.EVENT,
                        v_alias_rec.AC,
                        v_alias_rec.DEPRECATED,
                        v_alias_rec.CREATED,
                        v_alias_rec.UPDATED,
                        v_alias_rec.USERSTAMP,
                        v_alias_rec.ALIASTYPE_AC,
                        v_alias_rec.PARENT_AC,
                        v_alias_rec.OWNER_AC,
                        v_alias_rec.NAME,
                        NVL(v_alias_rec.CREATED_USER, 'N/A')
                    );

                ELSE

                  --
                  -- Look for publication
                  --
                  SELECT COUNT(1)
                  INTO v_count
                  FROM ia_component_audit
                  WHERE ac = v_alias_rec.PARENT_AC;

                  IF(v_count <> 0) THEN

                      -- copy Alias into the right table
                      INSERT INTO ia_component_alias_audit (
                          EVENT,
                          AC,
                          DEPRECATED,
                          CREATED,
                          UPDATED,
                          USERSTAMP,
                          ALIASTYPE_AC,
                          PARENT_AC,
                          OWNER_AC,
                          NAME,
                          CREATED_USER
                      )
                      VALUES (
                          v_alias_rec.EVENT,
                          v_alias_rec.AC,
                          v_alias_rec.DEPRECATED,
                          v_alias_rec.CREATED,
                          v_alias_rec.UPDATED,
                          v_alias_rec.USERSTAMP,
                          v_alias_rec.ALIASTYPE_AC,
                          v_alias_rec.PARENT_AC,
                          v_alias_rec.OWNER_AC,
                          v_alias_rec.NAME,
                          NVL(v_alias_rec.CREATED_USER, 'N/A')
                      );
  
                  ELSE
  
                    --
                    -- Nothing found, delete this orphan alias
                    --
  
                    DBMS_OUTPUT.PUT_LINE('Deleting orphan Alias...');
                    DBMS_OUTPUT.PUT_LINE('DELETE FROM ia_alias WHERE ac = ' || v_alias_rec.ac || ''';' );
  
                  END IF;  -- components
                END IF;  -- publications
              END IF;  -- feature
            END IF;  -- biosource
          END IF;  -- controlledvocab
        END IF;  -- experiemnt
      END IF; -- interactor

  END LOOP;

CLOSE alias_cur;

END;
/

show error

