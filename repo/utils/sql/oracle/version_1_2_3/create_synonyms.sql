-- creates synonims and grants permissions for the table
grant select on IA_DB_INFO to INTACT_SELECT ;
grant select, insert,update,delete on IA_DB_INFO to INTACT_CURATOR ;

create public synonym IA_DB_INFO for INTACT.IA_BioSource_Xref;