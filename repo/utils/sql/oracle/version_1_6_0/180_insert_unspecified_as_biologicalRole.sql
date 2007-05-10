declare
	identity_ac ia_controlledvocab.ac%TYPE;
	psimi_ac ia_controlledvocab.ac%TYPE;
	unspecified_biorole_ac ia_controlledvocab.ac%TYPE;
	owner_ac ia_controlledvocab.ac%TYPE;
	pubmed_ac ia_controlledvocab.ac%TYPE;
	primaryref_ac ia_controlledvocab.ac%TYPE;
	
begin
	dbms_output.enable(1000000000000);

	------------------
	-- get psimi_ac --
	------------------
	select ac
	into psimi_ac
	from ia_controlledvocab
	where shortlabel = 'psi-mi';
	
	------------------
	-- get owner_ac --
	------------------
	select owner_ac
	into owner_ac
	from ia_controlledvocab
	where shortlabel = 'psi-mi';
	
	---------------------
	-- get identity_ac --
	---------------------
	select ac
	into identity_ac
	from ia_controlledvocab
	where shortlabel = 'identity';

	--------------------------------
	-- get unspecified_biorole_ac --
	--------------------------------
	select 'EBI-' || Intact_ac.nextval
	into unspecified_biorole_ac
	from dual;
	
	-------------------
	-- get pubmed_ac --
	-------------------
	select cv.ac
	into pubmed_ac
	from ia_controlledvocab cv, ia_controlledvocab_xref x
	where cv.ac = x.parent_ac
	and x.primaryId = 'MI:0446'
	and x.database_ac = psimi_ac
	and x.qualifier_ac = identity_ac

	 -------------------
	 -- get pubmed_ac --
	 -------------------
	 select cv.ac
	 into primaryref_ac
	 from ia_controlledvocab cv, ia_controlledvocab_xref x
	 where cv.ac = x.parent_ac
	 and x.primaryId = 'MI:0358'
	 and x.database_ac = psimi_ac
	 and x.qualifier_ac = identity_ac

	-------------------------------------------------------------
	-- Insert "unspecified role" biorole in ia_controlledvocab --
	-------------------------------------------------------------	
	insert into ia_controlledvocab (ac, 
					owner_ac, 
					objclass, 
					shortlabel, 
					fullname)
	values (unspecified_biorole_ac, 
		owner_ac, 
		'uk.ac.ebi.intact.model.CvBiologicalRole', 
		'unspecified role',
		'unspecified role');

	---------------------------------------------------------------
	-- Add the psi-mi identity xref to the "unspecified role" cv --
	---------------------------------------------------------------
	insert into ia_controlledvocab_xref (parent_ac, 
					     ac, 
					     qualifier_ac, 
					     database_ac, 
					     owner_ac, 
					     primaryId)
	values (unspecified_biorole_ac, 
		'EBI-' || Intact_ac.nextval, 
		identity_ac, 
		psimi_ac, 
		owner_ac, 
		'MI:0499');

        ------------------------------------------------------------------
        -- Add the psi-mi primary-ref xref to the "unspecified role" cv --
        ------------------------------------------------------------------
	insert into ia_controlledvocab_xref (parent_ac,
                                     ac,
                                     qualifier_ac,
                                     database_ac,
                                     owner_ac,
                                     primaryId)
	values (unspecified_biorole_ac,
        	'EBI-' || Intact_ac.nextval,
        	primaryref_ac,
        	pubmed_ac,
        	owner_ac, 
		'14755292');

end;
/

