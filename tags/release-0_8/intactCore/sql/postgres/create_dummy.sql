/* SET DOC OFF */
/*************************************************************

  Package:    IntAct

  Purpose:    Create dummy data for IntAct core

              Postgres version

  $Date$
  $Locker$

  *************************************************************/


-- IA_Institution
INSERT INTO IA_Institution (shortLabel, fullName, postalAddress, url, ac)
     VALUES ( 'EBI',
	      'European Bioinformatics Institute',
	      'European Bioinformatics Institute\n' ||
	      'Wellcome Trust Genome Campus\n' ||
              'Hinxton, Cambridge CB10 1SD\n' ||
              'United Kingdom',
	      'http://www.ebi.ac.uk',
	      'EBI-' || nextval('Intact_ac'));

-- node
INSERT INTO IA_intactnode ( ac, ownerprefix, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'EBI',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';


INSERT INTO IA_Experiment (ac, shortLabel, fullname, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'gavin',
              'Analysis of the yeast interactome by tandem affinity purification and subsequent further analysis.',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_Experiment(ac, shortLabel, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'ho',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'definition',
	      'Definition of the controlled vocabulary term',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

INSERT INTO IA_ControlledVocab (ac, objClass, shortLabel, fullName, owner_ac)
       SELECT 'EBI-' || nextval('Intact_ac'),
	      'uk.ac.ebi.intact.model.CvTopic',
	      'comment',
	      'Comment for public view',
	      ac
         FROM IA_Institution
        WHERE shortLabel='EBI';

