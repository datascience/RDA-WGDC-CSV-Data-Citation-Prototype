
    alter table PERSISTENTIDENTIFIER 
        drop 
        foreign key FK_47i0dassc7sa5atqife225luu;

    drop table if exists PERSISTENTIDENTIFIER;

    drop table if exists organization;

    create table PERSISTENTIDENTIFIER (
        IdentifierTypes varchar(31) not null,
        persistent_identifier_id bigint not null auto_increment,
        uri varchar(255),
        created datetime,
        identifier varchar(255),
        updated datetime,
        organization_id integer not null,
        primary key (persistent_identifier_id)
    );

    create table organization (
        organization_id integer not null auto_increment,
        alphaPIDlength integer not null,
        alphanumericPIDlength integer not null,
        numericPIDlength integer not null,
        organization_name varchar(255) not null,
        organization_prefix integer not null,
        primary key (organization_id)
    );

    alter table PERSISTENTIDENTIFIER 
        add constraint UK_iw5b1xsmmc7oygrvc6r2bso9r  unique (organization_id, identifier);

    alter table organization 
        add constraint UK_s98gga7r9eag3re0lnp2dobxo  unique (organization_prefix);

    alter table PERSISTENTIDENTIFIER 
        add constraint FK_47i0dassc7sa5atqife225luu 
        foreign key (organization_id) 
        references organization (organization_id);
