/* SET DOC OFF */
/*************************************************************

  Package:    IntAct

  Purpose:    Create dummy data for IntAct core 

              Postgres version
 
  $Date$
  $Locker$

  *************************************************************/


-- Institution
INSERT INTO Institution (shortLabel, fullName, postalAddress, url, ac) 
     VALUES ( 'EBI',
	      'European Bioinformatics Institute',
	      'European Bioinformatics Institute\n' ||
	      'Wellcome Trust Genome Campus\n' || 
              'Hinxton, Cambridge CB10 1SD\n' ||
              'United Kingdom',
	      'http://www.ebi.ac.uk',
	      'EBI-' || nextval('Intact_ac'));

INSERT INTO ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'SPTR',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'PubMed',
	      'PubMed',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'SGD',
	      'Saccharomyces Genome Database',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'GO',
	      'Gene Ontology',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvComponentRole',
	      'bait',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvComponentRole',
	      'prey',
	      ac 
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO Experiment (ac, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'gavin',
	      ac
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO Experiment(ac, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'ho',
	      ac
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Function',
	      'Biological Function of the Object',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Remark',
	      'Internal remark',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Note',
	      'Public note',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Definition',
	      'Definition of an annotation topic',
	      ac 
         FROM Institution 
        WHERE shortLabel='EBI';

INSERT INTO Annotation (ac, topic_ac, owner_ac, description)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      cv.ac,	
	      i.ac,
	      'Describes the biological function of the Object.'
         FROM Institution i, ControlledVocab cv
        WHERE i.shortLabel='EBI' 
          AND cv.shortLabel='Definition';

INSERT INTO obj2annot (annotation_ac, cvobject_ac)
       SELECT a.ac,	
	      cv.ac
         FROM Annotation a, ControlledVocab cv
        WHERE a.description='Describes the biological function of the Object.'
          AND cv.shortLabel='Function';

