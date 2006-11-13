set serveroutput on


--
-- Splits the IA_ALIAS table into multiple tables
--
CREATE OR REPLACE PROCEDURE proc_split_alias AS 

-- cursor over all Aliases
CURSOR alias_cur IS
SELECT *
FROM ia_alias;

v_alias_rec ia_alias%ROWTYPE;

-- count of rows
v_count INTEGER;

-- specific type of interactor (dicriminator field)
v_interactor_type ia_interactor.objclass%TYPE;

-- specific type of CV (dicriminator field)
v_controlledvocab_type ia_controlledvocab.objclass%TYPE;

-- AC field of the different tables
v_interactor_ac ia_interactor.ac%TYPE;
v_experiment_ac ia_experiment.ac%TYPE;
v_controlledvocab_ac ia_controlledvocab.ac%TYPE;
v_biosource_ac ia_biosource.ac%TYPE;
v_feature_ac ia_feature.ac%TYPE;
v_publication_ac ia_publication.ac%TYPE;

BEGIN

  dbms_output.enable(1000000000000);
  
  -- NOTE: no check will be done for duplicated rows !!

  OPEN alias_cur;
  LOOP
      FETCH alias_cur INTO v_alias_rec;
      EXIT WHEN alias_cur % NOTFOUND;

      -- and now process a single Alias...

      --
      -- Look for interactor
      --
      SELECT COUNT(1)
      INTO v_count
      FROM ia_interactor
      WHERE ac = v_alias_rec.PARENT_AC;

      IF(v_count <> 0) THEN

        -- copy Alias into the right table
        INSERT INTO ia_interactor_alias (
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
            v_alias_rec.AC,
            v_alias_rec.DEPRECATED,
            v_alias_rec.CREATED,
            v_alias_rec.UPDATED,
            v_alias_rec.USERSTAMP,
            v_alias_rec.ALIASTYPE_AC,
            v_alias_rec.PARENT_AC,
            v_alias_rec.OWNER_AC,
            v_alias_rec.NAME,
            v_alias_rec.CREATED_USER
        );

      ELSE

        --
        -- Look for experiment
        --
        SELECT COUNT(1)
        INTO v_count
        FROM ia_experiment
        WHERE ac = v_alias_rec.PARENT_AC;

        IF(v_count <> 0) THEN

            -- copy Alias into the right table
            INSERT INTO ia_experiment_alias (
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
                v_alias_rec.AC,
                v_alias_rec.DEPRECATED,
                v_alias_rec.CREATED,
                v_alias_rec.UPDATED,
                v_alias_rec.USERSTAMP,
                v_alias_rec.ALIASTYPE_AC,
                v_alias_rec.PARENT_AC,
                v_alias_rec.OWNER_AC,
                v_alias_rec.NAME,
                v_alias_rec.CREATED_USER
            );
        ELSE

          --
          -- Look for controlled vocabulary
          --
          SELECT COUNT(1)
          INTO v_count
          FROM ia_controlledvocab
          WHERE ac = v_alias_rec.PARENT_AC;

          IF(v_count <> 0) THEN

              -- copy Alias into the right table
              INSERT INTO ia_controlledvocab_alias (
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
                    v_alias_rec.AC,
                    v_alias_rec.DEPRECATED,
                    v_alias_rec.CREATED,
                    v_alias_rec.UPDATED,
                    v_alias_rec.USERSTAMP,
                    v_alias_rec.ALIASTYPE_AC,
                    v_alias_rec.PARENT_AC,
                    v_alias_rec.OWNER_AC,
                    v_alias_rec.NAME,
                    v_alias_rec.CREATED_USER
                );

          ELSE

            --
            -- Look for biosource
            --
            SELECT COUNT(1)
            INTO v_count
            FROM ia_biosource
            WHERE ac = v_alias_rec.PARENT_AC;

            IF(v_count <> 0) THEN

                -- copy Alias into the right table
                INSERT INTO ia_biosource_alias (
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
                    v_alias_rec.AC,
                    v_alias_rec.DEPRECATED,
                    v_alias_rec.CREATED,
                    v_alias_rec.UPDATED,
                    v_alias_rec.USERSTAMP,
                    v_alias_rec.ALIASTYPE_AC,
                    v_alias_rec.PARENT_AC,
                    v_alias_rec.OWNER_AC,
                    v_alias_rec.NAME,
                    v_alias_rec.CREATED_USER
                );

            ELSE

              --
              -- Look for feature
              --
              SELECT COUNT(1)
              INTO v_count
              FROM ia_feature
              WHERE ac = v_alias_rec.PARENT_AC;

              IF(v_count <> 0) THEN

                  -- copy Alias into the right table
                  INSERT INTO ia_feature_alias (
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
                        v_alias_rec.AC,
                        v_alias_rec.DEPRECATED,
                        v_alias_rec.CREATED,
                        v_alias_rec.UPDATED,
                        v_alias_rec.USERSTAMP,
                        v_alias_rec.ALIASTYPE_AC,
                        v_alias_rec.PARENT_AC,
                        v_alias_rec.OWNER_AC,
                        v_alias_rec.NAME,
                        v_alias_rec.CREATED_USER
                    );
              ELSE

                --
                -- Look for publication
                --
                SELECT COUNT(1)
                INTO v_count
                FROM ia_publication
                WHERE ac = v_alias_rec.PARENT_AC;

                IF(v_count <> 0) THEN

                    -- copy Alias into the right table
                    INSERT INTO ia_publication_alias (
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
                        v_alias_rec.AC,
                        v_alias_rec.DEPRECATED,
                        v_alias_rec.CREATED,
                        v_alias_rec.UPDATED,
                        v_alias_rec.USERSTAMP,
                        v_alias_rec.ALIASTYPE_AC,
                        v_alias_rec.PARENT_AC,
                        v_alias_rec.OWNER_AC,
                        v_alias_rec.NAME,
                        v_alias_rec.CREATED_USER
                    );

                ELSE

                  --
                  -- Nothing found, delete this orphan alias
                  --

                  DBMS_OUTPUT.PUT_LINE('Deleting orphan Alias...');
                  DBMS_OUTPUT.PUT_LINE('DELETE FROM ia_alias WHERE ac = ' || v_alias_rec.ac || ''';');

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


