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

-- node
INSERT INTO intactnode ( ac, ownerprefix, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'EBI',
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

INSERT INTO Experiment (ac, shortLabel, fullname, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'gavin',
              'Analysis of the yeast interactome by tandem affinity purification and subsequent further analysis.',
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
	      'definition',
	      'Definition of the controlled vocabulary term',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

INSERT INTO ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'comment',
	      'Comment for public view',
	      ac
         FROM Institution
        WHERE shortLabel='EBI';

