/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct

  Purpose:    Create dummy data for IntAct core

  Usage:      sqlplus username/password @create_dummy.sql

  $Date$
  $Locker$

  *************************************************************/

SET DOC OFF

-- Institution
INSERT INTO Institution (shortLabel, fullName, postalAddress, url, ac)
     VALUES ( 'EBI',
	      'European Bioinformatics Institute',
	      'European Bioinformatics Institute\n' ||
	      'Wellcome Trust Genome Campus\n' ||
              'Hinxton, Cambridge CB10 1SD\n' ||
              'United Kingdom',
	      'http://www.ebi.ac.uk',
	      'EBI-' || Intact_ac.nextval);

-- Node
INSERT INTO intactnode (ac, ownerprefix, owner_ac)
    SELECT  'EBI-' || Intact_ac.nextval,
          'EBI',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'SPTR',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'PubMed',
	      'PubMed',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'SGD',
	      'Saccharomyces Genome Database',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvDatabase',
	      'GO',
	      'Gene Ontology',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvComponentRole',
	      'bait',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvComponentRole',
	      'prey',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO Experiment (ac, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'gavin',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO Experiment (ac, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'ho',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Function',
	      'Biological Function of the Object',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Remark',
	      'Internal remark',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Note',
	      'Public note',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Definition',
	      'Definition of the controlled vocabulary term',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'Comment',
	      'Comment for public view',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO Annotation (ac, topic_ac, owner_ac, description)
       SELECT 'EBI-' || Intact_ac.nextval,
	      cv.ac,
	      i.ac,
	      'Describes the biological function of the Object.'
         FROM Institution i, ControlledVocab cv
        WHERE i.shortLabel='EBI'
          AND cv.shortLabel='Function';

INSERT INTO cvobject2annot (annotation_ac, cvobject_ac)
       SELECT a.ac,
	      cv.ac
         FROM Annotation a, ControlledVocab cv
        WHERE a.description='Describes the biological function of the Object.'
          AND cv.shortLabel='Function';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvXrefQualifier',
	      'GO-definition-ref',
	      'GO definition reference',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvXrefQualifier',
	      'Identical',
	      'Reference to the identical object in another database',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

commit;
exit;

