-- insert nodes into database for testing synchron package

insert into institution(ac,fullName) values ('EBI-10','EBI Outstation');


-- you can add here informations about ftp server you want to use (e.g. your computer, your login and your password)
-- if you want to use the collector

-- node for the EBI, you can add others node on the same exemple
insert into intactnode(ac, ftpaddress, ftplogin, ftppassword, ftpdirectory, ownerprefix,owner_ac)
    values ('EBI-node1', '', '', '', '', 'EBI','EBI-10');


--don't forget to specify your id (e.g. EBI) in config/Properties.pro

