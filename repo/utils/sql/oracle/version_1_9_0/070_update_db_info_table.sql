PROMPT "Increase field length of ia_db_info.dbi_key to 255"
ALTER TABLE ia_db_info
MODIFY dbi_key VARCHAR2(255) NOT NULL;


PROMPT "Increase field length of ia_db_info.value to 255"
ALTER TABLE ia_db_info
MODIFY value VARCHAR2(255);