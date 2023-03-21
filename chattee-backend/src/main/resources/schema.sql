create table if not exists accounts
(
    id         bigserial
        constraint users_pk
            primary key,
    email      varchar(50)             not null,
    password   varchar                 not null,
    username   varchar                 not null,
    created_at timestamp default now() not null,
    updated_at timestamp default now() not null
);

alter table accounts
    owner to chattee;

create unique index if not exists accounts_id_uindex
    on accounts (id);

create unique index if not exists accounts_email_uindex
    on accounts (email);

create unique index if not exists accounts_username_uindex
    on accounts (username);

create table if not exists discussions
(
    id          bigserial
        constraint discussions_pk
            primary key,
    title       varchar(100)  not null,
    description varchar(2000) not null,
    author_id   bigserial
        constraint discussions_account_id_fk
            references accounts
);

alter table discussions
    owner to chattee;

create unique index if not exists discussions_id_uindex
    on discussions (id);

create table if not exists accounts_privileges
(
    account_id   bigint  not null
        constraint accounts_privileges_account_id_fk
            references accounts,
    privilege varchar not null,
    constraint users_privileges_pk
        primary key (account_id, privilege)
);

alter table accounts_privileges
    owner to chattee;

create table if not exists auth_details
(
    token      varchar                 not null
        constraint auth_details_pk
            primary key,
    account_id    bigint                  not null
        constraint auth_details_account_id_fk
            references accounts,
    expire_at  timestamp               not null,
    created_at timestamp default now() not null,
    ip_address varchar                 not null
);

alter table auth_details
    owner to chattee;

create unique index if not exists auth_details_token_uindex
    on auth_details (token);
