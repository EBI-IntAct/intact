SET serveroutput ON SIZE 1000000

spool run_update.LOG

PROMPT UPDATE records  IA_ALIAS without records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_ALIAS 
SET created_user = userstamp 
WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_ALIAS_AUDIT);
  

PROMPT UPDATE records  IA_ALIAS WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;	  
UPDATE IA_ALIAS base 
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_ALIAS_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_ALIAS_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_ALIAS_AUDIT);

COMMIT;
	  

PROMPT UPDATE records  IA_ANNOTATION without records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;
UPDATE IA_ANNOTATION
SET created_user = userstamp 
WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_ANNOTATION_AUDIT);
	  

PROMPT UPDATE records IA_ANNOTATION base WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_ANNOTATION base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_ANNOTATION_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_ANNOTATION_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_ANNOTATION_AUDIT);
 
COMMIT;

	  
PROMPT UPDATE records  IA_BIOSOURCE without records IN AUDIT-TABLE
UPDATE IA_BIOSOURCE
 SET created_user = userstamp 
 WHERE ac NOT IN	
 	   (SELECT AC	
	   FROM IA_BIOSOURCE_AUDIT);

PROMPT UPDATE records  IA_BIOSOURCE WITH records IN AUDIT-TABLE	   
UPDATE IA_BIOSOURCE base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_BIOSOURCE_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(TIMESTAMP) aud1_min_timestamp
	            FROM IA_BIOSOURCE_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.TIMESTAMP =  aud1_min_timestamp
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
 	   (SELECT AC	
	   FROM IA_BIOSOURCE_AUDIT);
 
COMMIT;
	   

PROMPT UPDATE records IA_COMPONENT without records IN AUDIT-TABLE
UPDATE IA_COMPONENT 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_COMPONENT_AUDIT);

	  
PROMPT UPDATE records IA_COMPONENT base WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_COMPONENT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_COMPONENT_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_COMPONENT_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_COMPONENT_AUDIT);

COMMIT;	  
	  

PROMPT UPDATE records IA_CONTROLLEDVOCAB without records IN AUDIT-TABLE
UPDATE IA_CONTROLLEDVOCAB
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_CONTROLLEDVOCAB_AUDIT);
	  
PROMPT UPDATE records IA_CONTROLLEDVOCAB WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_CONTROLLEDVOCAB base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_CONTROLLEDVOCAB_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_CONTROLLEDVOCAB_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_CONTROLLEDVOCAB_AUDIT);

COMMIT;
	  

PROMPT UPDATE records IA_EXPERIMENT without records IN AUDIT-TABLE
UPDATE IA_EXPERIMENT 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_EXPERIMENT_AUDIT);

PROMPT UPDATE records IA_EXPERIMENT  WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_EXPERIMENT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_EXPERIMENT_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_EXPERIMENT_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_EXPERIMENT_AUDIT);
 
COMMIT;
	  

PROMPT UPDATE records IA_FEATURE without records IN AUDIT-TABLE
UPDATE IA_FEATURE 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_FEATURE_AUDIT);
	  
PROMPT UPDATE records IA_FEATURE  WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_FEATURE base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_FEATURE_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(TIMESTAMP) aud1_min_timestamp
	            FROM IA_FEATURE_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.TIMESTAMP =  aud1_min_timestamp
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_FEATURE_AUDIT);

COMMIT;	  

PROMPT UPDATE records IA_INSTITUTION without records IN AUDIT-TABLE
UPDATE IA_INSTITUTION 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_INSTITUTION_AUDIT);
	  
PROMPT UPDATE records IA_INSTITUTION  WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_INSTITUTION base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_INSTITUTION_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_INSTITUTION_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_INSTITUTION_AUDIT);
 
COMMIT;	  

PROMPT UPDATE records IA_INTACTNODE without records IN AUDIT-TABLE
UPDATE IA_INTACTNODE 
SET created_user = userstamp 
 WHERE ac NOT IN		
	  (SELECT AC	
	  FROM IA_INTACTNODE_AUDIT);
	  
PROMPT UPDATE records IA_INTACTNODE WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_INTACTNODE base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_INTACTNODE_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_INTACTNODE_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_INTACTNODE_AUDIT);
 
COMMIT;	  

PROMPT UPDATE records IA_INTERACTOR without records IN AUDIT-TABLE
UPDATE IA_INTERACTOR 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_INTERACTOR_AUDIT);

PROMPT UPDATE records IA_INTERACTOR  WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_INTERACTOR base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_INTERACTOR_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_INTERACTOR_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_INTERACTOR_AUDIT);
 
COMMIT;	  

--PROMPT Update records IA_PUBMED excluded is this one has not a primary, so not able to join the audit table
-- update IA_PUBMED set created_user = userstamp where ac in	(select ac	from IA_PUBMED	MINUS	SELECT AC	FROM IA_PUBMED_AUDIT); no primary key

PROMPT UPDATE records IA_RANGE without records IN AUDIT-TABLE
UPDATE IA_RANGE 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_RANGE_AUDIT);
	  
PROMPT UPDATE records IA_RANGE WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_RANGE base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_RANGE_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(TIMESTAMP) aud1_min_timestamp
	            FROM IA_RANGE_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.TIMESTAMP =  aud1_min_timestamp
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_RANGE_AUDIT);

COMMIT;	  

PROMPT UPDATE records IA_SEQUENCE_CHUNK without records IN AUDIT-TABLE
UPDATE IA_SEQUENCE_CHUNK 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_SEQUENCE_CHUNK_AUDIT);

PROMPT UPDATE records IA_SEQUENCE_CHUNK WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_SEQUENCE_CHUNK base
SET created_user =
        (SELECT MIN(userstamp) 
	        FROM IA_SEQUENCE_CHUNK_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(ROWID) aud1_min
	            FROM IA_SEQUENCE_CHUNK_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.ROWID =  aud1_min
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_SEQUENCE_CHUNK_AUDIT);

 
COMMIT;	  

	  

PROMPT UPDATE records IA_XREF without records IN AUDIT-TABLE
UPDATE IA_XREF 
SET created_user = userstamp 
 WHERE ac NOT IN	
	  (SELECT AC	
	  FROM IA_XREF_AUDIT);

PROMPT UPDATE records IA_XREF WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_XREF base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_XREF_AUDIT aud2
	        ,(SELECT
	            ac aud1_ac
	            ,MIN(updated) aud1_min_updated
	            FROM IA_XREF_AUDIT aud1
	            GROUP BY ac)
        WHERE aud2.ac = aud1_ac
        AND aud2.updated =  aud1_min_updated
        AND base.ac = aud2.ac
		GROUP BY aud2.ac
		)
WHERE ac IN
	  (SELECT AC	
	  FROM IA_XREF_AUDIT);

COMMIT;	  


PROMPT UPDATE records IA_BIOSOURCE2ANNOT  without records IN AUDIT-TABLE
UPDATE IA_BIOSOURCE2ANNOT 
SET created_user = userstamp 
WHERE (biosource_ac,annotation_ac) NOT  IN 
	(SELECT biosource_ac,annotation_ac	
	FROM IA_BIOSOURCE2ANNOT_AUDIT);

PROMPT UPDATE records IA_BIOSOURCE2ANNOT WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_BIOSOURCE2ANNOT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_BIOSOURCE2ANNOT_AUDIT a2
	        ,(SELECT
	            biosource_ac    a1_biosource_ac
				,annotation_ac  a1_annotation_ac
	            ,MIN(updated) a1_min_updated
	            FROM IA_BIOSOURCE2ANNOT_AUDIT a1
	            GROUP BY biosource_ac,annotation_ac)
        WHERE a2.biosource_ac  = a1_biosource_ac
		AND   a2.annotation_ac = a1_annotation_ac
        AND   a2.updated 	   = a1_min_updated
        AND base.biosource_ac  = a2.biosource_ac
		AND base.annotation_ac = a2.annotation_ac
		GROUP BY a2.biosource_ac,a2.annotation_ac
		)
WHERE (biosource_ac,annotation_ac) IN
 (SELECT biosource_ac,annotation_ac
    FROM IA_BIOSOURCE2ANNOT_AUDIT);

COMMIT;	
	

PROMPT UPDATE records IA_CV2CV 	without records IN AUDIT-TABLE	  
UPDATE IA_CV2CV 		  
SET created_user = userstamp 
WHERE (parent_ac,child_ac) NOT IN 
	  (SELECT parent_ac,child_ac			
	  FROM IA_CV2CV			
	  MINUS	
	  SELECT parent_ac,child_ac			
	  FROM IA_CV2CV_AUDIT);

PROMPT UPDATE records IA_CV2CV WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_CV2CV base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_CV2CV_AUDIT a2
	        ,(SELECT
	            parent_ac a1_parent_ac
				,child_ac a1_child_ac
	            ,MIN(updated) a1_min_updated
	            FROM IA_CV2CV_AUDIT a1
	            GROUP BY parent_ac,child_ac)
        WHERE a2.parent_ac = a1_parent_ac
		AND	  a2.child_ac  = a1_child_ac
        AND   a2.updated   = a1_min_updated
        AND base.parent_ac = a2.parent_ac
		AND base.child_ac  = a2.child_ac
		GROUP BY a2.parent_ac
			  	 ,a2.child_ac
		)
WHERE (parent_ac,child_ac)  IN
 (  SELECT parent_ac,child_ac
    FROM IA_CV2CV_AUDIT);

COMMIT;		  

PROMPT UPDATE records IA_CVOBJECT2ANNOT  without records IN AUDIT-TABLE
UPDATE IA_CVOBJECT2ANNOT  
SET created_user = userstamp 
WHERE (cvobject_ac,annotation_ac) NOT IN 
	  (SELECT cvobject_ac,annotation_ac	
	  FROM IA_CVOBJECT2ANNOT_AUDIT);
	  
PROMPT UPDATE records IA_CVOBJECT2ANNOT WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_CVOBJECT2ANNOT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_CVOBJECT2ANNOT_AUDIT a2
	        ,(SELECT
	            cvobject_ac    a1_cvobject_ac
				,annotation_ac a1_annotation_ac
	            ,MIN(updated)  a1_min_updated
	            FROM IA_CVOBJECT2ANNOT_AUDIT a1
	            GROUP BY cvobject_ac
				,annotation_ac)
        WHERE a2.cvobject_ac = a1_cvobject_ac
		AND   a2.annotation_ac 	= a1_annotation_ac
        AND   a2.updated 		= a1_min_updated
        AND   base.cvobject_ac	= a2.cvobject_ac
		AND   base.annotation_ac =a2.annotation_ac
		GROUP BY a2.cvobject_ac
			     ,a2.annotation_ac
		)
WHERE (cvobject_ac,annotation_ac)  IN
 (  SELECT cvobject_ac,annotation_ac
    FROM IA_CVOBJECT2ANNOT_AUDIT);

COMMIT;	  
	  

PROMPT UPDATE records IA_EXP2ANNOT 	without records IN AUDIT-TABLE  
UPDATE IA_EXP2ANNOT 	  
SET created_user = userstamp 
WHERE (experiment_ac,annotation_ac) NOT  IN 
	  (SELECT experiment_ac,annotation_ac	
	  FROM IA_EXP2ANNOT_AUDIT);

PROMPT UPDATE records IA_EXP2ANNOT WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_EXP2ANNOT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_EXP2ANNOT_AUDIT a2
	        ,(SELECT
	            experiment_ac	a1_experiment_ac
				,annotation_ac  a1_annotation_ac
	            ,MIN(updated) 	a1_min_updated
	            FROM IA_EXP2ANNOT_AUDIT a1
	            GROUP BY experiment_ac
				,annotation_ac)
        WHERE a2.experiment_ac = a1_experiment_ac
		AND   a2.annotation_ac = a1_annotation_ac
        AND   a2.updated 	   = a1_min_updated
        AND base.experiment_ac = a2.experiment_ac
		AND base.annotation_ac = a2.annotation_ac
		GROUP BY a2.experiment_ac
			  ,a2.annotation_ac
		)
WHERE (experiment_ac,annotation_ac)  IN
 ( SELECT experiment_ac,annotation_ac
    FROM IA_EXP2ANNOT_AUDIT);

COMMIT;	  

PROMPT UPDATE records IA_FEATURE2ANNOT without records IN AUDIT-TABLE  
UPDATE IA_FEATURE2ANNOT   
SET created_user = userstamp 
WHERE (feature_ac,annotation_ac) NOT IN 
	  (SELECT feature_ac,annotation_ac	
	  FROM IA_FEATURE2ANNOT_AUDIT);

PROMPT UPDATE records IA_FEATURE2ANNOT WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_FEATURE2ANNOT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_FEATURE2ANNOT_AUDIT a2
	        ,(SELECT
	            feature_ac	   a1_feature_ac
				,annotation_ac a1_annotation_ac
	            ,MIN(TIMESTAMP) a1_min
	            FROM IA_FEATURE2ANNOT_AUDIT a1
	            GROUP BY feature_ac
					  ,annotation_ac)
        WHERE a2.feature_ac    = a1_feature_ac
		AND	  a2.annotation_ac = a1_annotation_ac
        AND   a2.TIMESTAMP 	   = a1_min
        AND base.feature_ac    = a2.feature_ac
		AND base.annotation_ac = a2.annotation_ac
		GROUP BY a2.feature_ac
			  ,a2.annotation_ac
		)
WHERE (feature_ac,annotation_ac)  IN
 (  SELECT feature_ac,annotation_ac
    FROM IA_FEATURE2ANNOT_AUDIT);

COMMIT;
	  



PROMPT UPDATE records IA_INT2EXP without records IN AUDIT-TABLE		  
UPDATE IA_INT2EXP   
SET created_user = userstamp 
WHERE (interaction_ac,experiment_ac) NOT IN 
	  (SELECT interaction_ac,experiment_ac	
	  FROM IA_INT2EXP_AUDIT);


PROMPT UPDATE records IA_INT2EXP WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_INT2EXP base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_INT2EXP_AUDIT a2
	        ,(SELECT
	            interaction_ac 	 a1_interaction_ac
				,experiment_ac 	 a1_experiment_ac
	            ,MIN(updated) 	 a1_min_updated
	            FROM IA_INT2EXP_AUDIT a1
	            GROUP BY interaction_ac
				,experiment_ac)
        WHERE a2.interaction_ac = a1_interaction_ac
		AND   a2.experiment_ac 	= a1_experiment_ac 
        AND   a2.updated 		= a1_min_updated
        AND base.interaction_ac = a2.interaction_ac
		AND base.experiment_ac =  a2.experiment_ac
		GROUP BY a2.interaction_ac
			  ,a2.experiment_ac
		)
WHERE (interaction_ac,experiment_ac)  IN
 (  SELECT interaction_ac,experiment_ac
    FROM IA_INT2EXP_AUDIT);

COMMIT;


PROMPT UPDATE records IA_INT2ANNOT 	without records IN AUDIT-TABLE  
UPDATE IA_INT2ANNOT 	  
SET created_user = userstamp 
WHERE (interactor_ac,annotation_ac) NOT IN 
	  (SELECT interactor_ac,annotation_ac	
	  FROM IA_INT2ANNOT_AUDIT);

PROMPT UPDATE records IA_INT2ANNOT WITH records IN AUDIT-TABLE
SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  start_date FROM dual;

UPDATE IA_INT2ANNOT base
SET created_user =
        (SELECT MIN(userstamp) 
        FROM IA_INT2ANNOT_AUDIT a2
	        ,(SELECT
	            interactor_ac	a1_interactor_ac
				,annotation_ac 	a1_annotation_ac
	            ,MIN(updated) 	a1_min_updated
	            FROM IA_INT2ANNOT_AUDIT a1
	            GROUP BY interactor_ac
					  ,annotation_ac)
        WHERE a2.interactor_ac = a1_interactor_ac
		AND	  a2.annotation_ac = a1_annotation_ac
        AND   a2.updated 	   = a1_min_updated
        AND base.interactor_ac = a2.interactor_ac
		AND base.annotation_ac = a2.annotation_ac
		GROUP BY a2.interactor_ac
		,a2.annotation_ac
		)
WHERE (interactor_ac,annotation_ac) IN
 (  SELECT interactor_ac,annotation_ac
    FROM IA_INT2ANNOT_AUDIT);
 
COMMIT;	  




SELECT TO_CHAR(SYSDATE,'dd-mon-yyyy hh24:mi:ss')  end_date FROM dual;

spool OFF
