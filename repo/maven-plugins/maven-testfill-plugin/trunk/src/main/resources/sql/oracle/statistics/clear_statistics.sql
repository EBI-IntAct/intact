-- Clear the content of the statistics tables.
-- author: Samuel Kerrien (skerrien@ebi.ac.uk)
-- version: $Id: clear_statistics.sql 4093 2005-07-04 08:14:31Z skerrien $

DELETE FROM IA_BioSourceStatistics;
DELETE FROM IA_DetectionMethodsStatistics;
DELETE FROM IA_Statistics;

exit