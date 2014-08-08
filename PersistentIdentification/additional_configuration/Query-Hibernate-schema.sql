
    alter table filter 
        drop 
        foreign key FK_keqv4t6jkmnup4d91i4kixowq;

    alter table filter_AUD 
        drop 
        foreign key FK_2fcsq0fjiaugtsdr5rofhh4ec;

    alter table query_AUD 
        drop 
        foreign key FK_r8gtm2j9iyiwjev42lgpw905c;

    alter table sorting 
        drop 
        foreign key FK_6brwul2vbh1oe64pclaed73r4;

    alter table sorting_AUD 
        drop 
        foreign key FK_ofb80oyrsaj3w7h5y8jfia4ge;

    drop table if exists REVINFO;

    drop table if exists filter;

    drop table if exists filter_AUD;

    drop table if exists query;

    drop table if exists query_AUD;

    drop table if exists sorting;

    drop table if exists sorting_AUD;

    create table REVINFO (
        REV integer not null auto_increment,
        REVTSTMP bigint,
        primary key (REV)
    );

    create table filter (
        filter_id bigint not null auto_increment,
        filterName varchar(255),
        filter_sequence integer,
        filterValue varchar(255),
        query_id bigint,
        primary key (filter_id)
    );

    create table filter_AUD (
        filter_id bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        filterName varchar(255),
        filter_sequence integer,
        filterValue varchar(255),
        query_id bigint,
        primary key (filter_id, REV)
    );

    create table query (
        query_id bigint not null auto_increment,
        PID varchar(255) not null,
        createdDate datetime,
        data_source varchar(255) not null,
        execution_timestamp datetime,
        hash varchar(255) not null,
        lastUpdated datetime,
        query_text varchar(255),
        user_name varchar(255) not null,
        primary key (query_id)
    );

    create table query_AUD (
        query_id bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        PID varchar(255),
        createdDate datetime,
        data_source varchar(255),
        execution_timestamp datetime,
        hash varchar(255),
        lastUpdated datetime,
        query_text varchar(255),
        user_name varchar(255),
        primary key (query_id, REV)
    );

    create table sorting (
        sorting_id bigint not null auto_increment,
        direction varchar(255),
        sorting_column varchar(255),
        query_id bigint,
        primary key (sorting_id)
    );

    create table sorting_AUD (
        sorting_id bigint not null,
        REV integer not null,
        REVTYPE tinyint,
        direction varchar(255),
        sorting_column varchar(255),
        query_id bigint,
        primary key (sorting_id, REV)
    );

    alter table query 
        add constraint UK_5otantpaph5arao1ulacu9kmm  unique (PID);

    alter table query 
        add constraint UK_c06lvi02pc5tgsmagub2vqp2u  unique (hash);

    alter table filter 
        add constraint FK_keqv4t6jkmnup4d91i4kixowq 
        foreign key (query_id) 
        references query (query_id);

    alter table filter_AUD 
        add constraint FK_2fcsq0fjiaugtsdr5rofhh4ec 
        foreign key (REV) 
        references REVINFO (REV);

    alter table query_AUD 
        add constraint FK_r8gtm2j9iyiwjev42lgpw905c 
        foreign key (REV) 
        references REVINFO (REV);

    alter table sorting 
        add constraint FK_6brwul2vbh1oe64pclaed73r4 
        foreign key (query_id) 
        references query (query_id);

    alter table sorting_AUD 
        add constraint FK_ofb80oyrsaj3w7h5y8jfia4ge 
        foreign key (REV) 
        references REVINFO (REV);
