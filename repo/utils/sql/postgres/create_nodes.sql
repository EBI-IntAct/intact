-- insert nodes into database for testing synchron package

insert into ia_institution(shortlabel, fullname, postaladdress, url, ac) values ('EBI',
    'European Bioinformatics Institute',
    'European Bioinformatics Institute
    Wellcome Trust Genome Campus Hinxton, Cambridge
    CB10 1SD United Kingdom' ,
    'http://www.ebi.ac.uk',
    'EBI-11');

insert into ia_intactnode(ac, ftpaddress, ftplogin, ftppassword, ftpdirectory, ownerprefix,owner_ac)
    values ('EBI-11', 'brandt.ebi.ac.uk', 'amueller', 'iZ111sU+A', '/scratch/PseudoFtpServer/EBI', 'EBI','EBI-10');

