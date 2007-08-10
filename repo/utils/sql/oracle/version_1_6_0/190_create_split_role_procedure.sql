--
-- Procedure allowing to split the role into biological and experimental role.
-- When migrating from schema version < 1.6.0 the former column 'role' was
-- renamed experimentalRole_ac and CvComponentRole is divided into 2 new classes
-- namely CvExperimentalRole and CvBiologicalRole.
-- The first step of the procedure is to move data related to biological role
-- into the correct column (that is biologicalrole_ac).
-- Once done most rows of the IA_Component table will either have
-- experimentalrole_ac or biologicalrole_ac null, in which case we fill the null
-- columns with
--     * 'neutral component' for experimental role
--     * 'unspecified role'  for biological role
--
CREATE OR REPLACE PROCEDURE proc_split_component_role AS

-- cursor over all components
CURSOR component_cur IS
       SELECT *
       FROM ia_component
FOR UPDATE;

v_comp_rec ia_component%ROWTYPE;

v_exp_role           ia_component.experimentalrole_ac%TYPE;
v_exp_unspecified_ac ia_component.experimentalrole_ac%TYPE;
v_bio_unspecified_ac ia_component.biologicalrole_ac%TYPE;

-- count of rows
v_count INTEGER;

BEGIN

  DBMS_OUTPUT.ENABLE(1000000);

  -- Check that we do not have any CvComponentRole anymore
  select count(1) into v_count
  from ia_controlledvocab
  where objclass like '%CvComponentRole%';

  if( v_count > 0 ) then
    RAISE_APPLICATION_ERROR( -21000, 'Found Could not find ExperimentalRole( neutral component )' );
  end if;

  -- load Experimental role AC for 'unspecified'
  select ac into v_exp_unspecified_ac
  from ia_controlledvocab
  where     shortlabel = 'unspecified role'
        and objclass like '%ExperimentalRole%';

  if( v_exp_unspecified_ac is null ) then
    RAISE_APPLICATION_ERROR( -21000, 'Could not find ExperimentalRole( neutral component )' );
  end if;

  -- load Biological role AC for unspecified
  select ac into v_bio_unspecified_ac
  from ia_controlledvocab
  where     shortlabel = 'unspecified role'
        and objclass like '%BiologicalRole%';

  if( v_bio_unspecified_ac is null ) then
    RAISE_APPLICATION_ERROR( -21000, 'Could not find BiologicalRole( unspecified role )' );
  end if;

  OPEN component_cur;
  LOOP
      FETCH component_cur INTO v_comp_rec;
      EXIT WHEN component_cur % NOTFOUND;

      -- if experimentalRole_ac contains a biologicalRole, move it to biologicalRole_ac
      select count(1) into v_count
      from ia_controlledvocab cv
      where objclass like '%BiologicalRole%'
            and ac = v_comp_rec.experimentalRole_ac;

      if( v_count = 1 ) then
      
        -- There's a mix up here ... fix it !
        -- (!) potential problem if data in biologicalrole_ac: we lose'em
        
        if( v_comp_rec.biologicalrole_ac is not null ) then
          dbms_output.put_line( 'Non null biological role ('|| v_comp_rec.biologicalrole_ac ||')will be lost on component: '|| v_comp_rec.ac );
        end if;
        
        v_exp_role := v_comp_rec.experimentalRole_ac;
        update ia_component
        set experimentalRole_ac = v_exp_unspecified_ac,
            biologicalrole_ac = v_exp_role
        where current of component_cur;
      
      else
      
        update ia_component
        set biologicalrole_ac = v_bio_unspecified_ac
        where current of component_cur;
      
      end if;

  END LOOP;

CLOSE component_cur;

END;
/

