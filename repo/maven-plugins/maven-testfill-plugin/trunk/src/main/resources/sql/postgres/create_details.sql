/* SET DOC OFF */
/*************************************************************

  Package:    IntAct

  Purpose:    Create additional detail dummy data for IntAct core

              Postgres version

  $Date: 2003-04-24 11:38:33 +0100 (dj, 24 abr 2003) $
  $Locker$

  *************************************************************/


INSERT INTO ia_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvInteractionType',
	      'aggregation',
	      'Formation of a complex by aggregation',
	      ac
         FROM ia_Institution
        WHERE shortLabel='EBI';

INSERT INTO ia_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvIdentification',
	      'mass-spec',
	      'Identification of interaction participants by mass spectrometry',
	      ac
         FROM ia_Institution
        WHERE shortLabel='EBI';

INSERT INTO ia_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvInteraction',
	      'tap',
	      'Tandem affinity purification',
	      ac
         FROM ia_Institution
        WHERE shortLabel='EBI';

INSERT INTO ia_BioSource (ac, shortLabel, fullName, taxid, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'Human',
	      'Homo Sapiens',
	      9606,
	      ac
         FROM ia_Institution
        WHERE shortLabel='EBI';

UPDATE ia_Experiment
   SET bioSource_Ac = (  SELECT ac 
         		   FROM ia_BioSource
                          WHERE shortLabel='Human' ),
    detectMethod_Ac = (  SELECT ac 
         		   FROM ia_ControlledVocab
                          WHERE shortLabel='tap' ),
     identMethod_Ac = (  SELECT ac 
         		   FROM ia_ControlledVocab
                          WHERE shortLabel='mass-spec' )
 WHERE shortLabel='gavin';

INSERT INTO ia_Annotation (ac, topic_ac, description, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      c.ac,
	      'A short comment for demo purposes.',	      
	      i.ac
         FROM ia_controlledVocab c,
              ia_Institution i
        WHERE c.shortlabel='comment'
          AND i.shortLabel='EBI';

INSERT INTO ia_Annotation (ac, topic_ac, description, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      c.ac,
	      'A rather long comment to test the display of long comments, and perhaps also to test how inventive I am in inventing rather long comments to test the display of long comments. This sentence might even be repeated, to show that even really long comments can occur if someone takes the time to type them in. This is only a few lines, a really long comment can have up to 4000 characters.',	      
	      i.ac
         FROM ia_controlledVocab c,
              ia_Institution i
        WHERE c.shortlabel='comment'
          AND i.shortLabel='EBI';

  INSERT INTO ia_ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'bind',
	      ac
         FROM ia_Institution
        WHERE shortLabel='EBI';

INSERT INTO ia_xref (ac, primaryid, secondaryid, database_ac, parent_ac, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'xx12',
	      'stes',
	      db.ac,
	      e.ac,
	      i.ac
         FROM ia_controlledvocab db, ia_experiment e, ia_Institution i
        WHERE db.shortlabel='bind' 
          AND e.shortlabel='gavin'
          AND i.shortLabel='EBI';

INSERT INTO ia_xref (ac, primaryid, secondaryid, database_ac, parent_ac, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'xx13',
	      'stess',
	      db.ac,
	      int.ac,
	      i.ac
         FROM ia_controlledvocab db, ia_interactor int, ia_Institution i
        WHERE db.shortlabel='bind' 
          AND int.shortlabel='ga-1'
          AND i.shortLabel='EBI';

UPDATE ia_interactor
   SET bioSource_Ac = (  SELECT ac 
         		   FROM ia_BioSource
                          WHERE shortLabel='Human' ),
 interactionType_Ac = (  SELECT ac 
         		   FROM ia_ControlledVocab
                          WHERE shortLabel='aggregation' ),
                 kd = 1
 WHERE shortLabel='ga-1';

UPDATE ia_interactor
   SET bioSource_Ac = (  SELECT ac 
         		   FROM ia_BioSource
                          WHERE shortLabel='Human' )
 WHERE shortLabel='ABD1';

UPDATE ia_component
   SET expressedin_ac = (  SELECT ac 
         		   FROM ia_BioSource
                          WHERE shortLabel='Human' ),
       stoichiometry = 1;

