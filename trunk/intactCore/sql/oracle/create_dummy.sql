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
INSERT INTO IA_Institution (shortLabel, fullName, postalAddress, url, ac)
     VALUES ( 'EBI',
	      'European Bioinformatics Institute',
	      'European Bioinformatics Institute\n' ||
	      'Wellcome Trust Genome Campus\n' ||
              'Hinxton, Cambridge CB10 1SD\n' ||
              'United Kingdom',
	      'http://www.ebi.ac.uk',
	      'EBI-' || Intact_ac.nextval);

-- Node
INSERT INTO IA_intactnode (ac, ownerprefix, owner_ac)
    SELECT  'EBI-' || Intact_ac.nextval,
          'EBI',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_Experiment (ac, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'gavin',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_Experiment (ac, shortLabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'ho',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'definition',
	      'Definition of the controlled vocabulary term',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
	      'uk.ac.ebi.intact.model.CvTopic',
	      'comment',
	      'Comment for public view',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_biosource (ac, taxid, shortlabel, owner_ac)
       SELECT 'EBI-' || Intact_ac.nextval,
              '-1',
              'in vitro',
	          ac
       FROM IA_Institution
       WHERE shortLabel='EBI';

commit;
exit;

