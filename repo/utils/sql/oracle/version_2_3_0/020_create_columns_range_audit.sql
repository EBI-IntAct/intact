PROMPT Adding full_sequence, upstream_sequence and downstream_sequence column for ia_range_audit
ALTER TABLE ia_range_audit ADD (full_sequence clob NULL);
ALTER TABLE ia_range_audit ADD (upstream_sequence VARCHAR2(40) NULL);
ALTER TABLE ia_range_audit ADD (downstream_sequence VARCHAR2(40) NULL);
commit;
