PROMPT Adding full_sequence, upstream_sequence and downstream_sequence column for ia_range
ALTER TABLE ia_range ADD (full_sequence clob NULL);
ALTER TABLE ia_range ADD (upstream_sequence VARCHAR2(40) NULL);
ALTER TABLE ia_range ADD (downstream_sequence VARCHAR2(40) NULL);
commit;
