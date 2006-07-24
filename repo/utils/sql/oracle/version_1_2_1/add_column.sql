-- add new column in table ia_xref
-- that column will be used to determinate the type of an Xref's parent.
ALTER TABLE ia_xref
ADD parentClass VARCHAR2(255);