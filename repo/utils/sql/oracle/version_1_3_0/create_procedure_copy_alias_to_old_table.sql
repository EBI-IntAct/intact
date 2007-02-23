create or replace PROCEDURE copy_alias_to_old_table AS -- cursor over all Aliases
    CURSOR alias_cur IS
    select *
                        from ia_biosource_alias
                union
                        select *
                        from ia_interactor_alias
                union
                        select *
                        from ia_feature_alias
                union
                        select *
                        from ia_experiment_alias
                union
                        select *
                        from ia_controlledvocab_alias
                union
                        select *
                        from ia_publication_alias;

v_alias_rec ia_biosource_alias%ROWTYPE;

BEGIN

  -- TODO clean up all specific alias tables before to fill them up
  -- no check will be done for duplicated rows !!
  DBMS_OUTPUT.PUT_LINE('Copying alais from ia_biosourc_alias, ia_interactor_alias, ia_feature_alias... to ia_alias');
  OPEN alias_cur;
  LOOP

      FETCH alias_cur INTO v_alias_rec;
      EXIT WHEN alias_cur % NOTFOUND;


      INSERT INTO ia_alias(
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

        END LOOP;
        commit;
        CLOSE alias_cur;
        DBMS_OUTPUT.PUT_LINE('Copying alias done');

END;
/

show errors
