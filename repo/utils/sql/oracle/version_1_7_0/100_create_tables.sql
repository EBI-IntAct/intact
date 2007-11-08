drop table ia_imex_import_pub;
drop table ia_imex_import;
drop table ia_imex_import_pub_audit;
drop table ia_imex_import_audit;
drop trigger trgAud_ia_imex_import_pub;
drop trigger trgAud_ia_imex_import;
drop sequence imex_sequence;


commit;

PROMPT Creating table "IA_IMEX_IMPORT"

CREATE TABLE IA_IMEX_IMPORT (
    id              number(19,0) not null,
    created         timestamp,
    created_user    varchar2(30 char),
    updated         timestamp,
    userstamp       varchar2(30 char),
    activationType  varchar2(255 char),
    count_total     number(10,0),
    count_not_found number(10,0),
    count_failed  number(10,0),
    import_date     timestamp,
    repository      varchar2(255 char),
    primary key (id))
    TABLESPACE &&intactIndexTablespace;

PROMPT Creating table "IA_IMEX_IMPORT_PUB"

CREATE TABLE IA_IMEX_IMPORT_PUB (
    pmid                varchar2(50 char) not null,
    created             timestamp not null,
    created_user        varchar2(30 char) not null,
    updated             timestamp not null,
    userstamp           varchar2(30 char) not null,
    message             clob,
    original_filename   varchar2(255 char),
    release_date        timestamp,
    status              varchar2(255 char),
    imexImport_id       number(19,0),
    provider_ac         varchar2(30 char),
    primary key (imexImport_id, pmid))
    TABLESPACE &&intactIndexTablespace;

ALTER TABLE IA_IMEX_IMPORT_PUB add constraint fk_ImexImport_imexImportPub foreign key (imexImport_id) references ia_imex_import;
ALTER TABLE IA_IMEX_IMPORT_PUB add constraint fk_Institution_provider foreign key (provider_ac) references ia_institution;

set term off
    COMMENT ON TABLE IA_IMEX_IMPORT IS 'Represents an IMEx import action, which may contain many publications.';

    COMMENT ON COLUMN IA_IMEX_IMPORT.activationType IS      'Type of activation of the import.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.count_total IS         'Count of total publications processed.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.count_not_found IS     'Count of failed publications in the database which were not found in the repository.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.count_failed IS        'Count of publications failed during import.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.created IS             'Date of the creation of the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.created_user IS        'Database user who has created the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.updated IS             'Date of the last update of the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT.userstamp IS           'Database user who has performed the last update of the row.';

    COMMENT ON TABLE IA_IMEX_IMPORT_PUB IS 'Table used to track the IMEx imported publications';

    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.original_filename IS   'Filename from which the data were imported.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.status IS              'Status of the data import.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.pmid IS                'Publication identifier reflecting the data imported.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.release_date IS        'Release date of the publication.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.message IS             'Message attached to this operation.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.provider_ac IS         'Institution from which this publication originates.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.created IS             'Date of the creation of the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.created_user IS        'Database user who has created the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.updated IS             'Date of the last update of the row.';
    COMMENT ON COLUMN IA_IMEX_IMPORT_PUB.userstamp IS           'Database user who has performed the last update of the row.';
set term on