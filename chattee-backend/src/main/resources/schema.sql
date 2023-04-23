create table if not exists accounts
(
    id         bigserial
        constraint users_pk
            primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    email      varchar(50)             not null,
    password   varchar                 not null,
    username   varchar                 not null,
    avatar_url varchar
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
    id bigserial constraint discussions_pk primary key,
    created_at timestamp not null,
    title       varchar(100)  not null,
    description varchar(200) not null,
    author_id   bigserial
        constraint discussions_account_id_fk
            references accounts
);

alter table discussions
    owner to chattee;

create unique index if not exists discussions_id_uindex
    on discussions (id);

create table if not exists discussions_posts (
    id bigserial constraint discussions_posts_pk primary key,
    created_at timestamp not null,
    content varchar(2048),
    author_id bigserial constraint discussions_posts_account_id_fk references accounts,
    discussion_id bigserial constraint discussions_posts_discussions_id_fk references discussions
);

alter table discussions_posts owner to chattee;

create unique index if not exists discussions_posts_uindex on discussions_posts (id);

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
    created_at timestamp not null,
    account_id    bigint                  not null
        constraint auth_details_account_id_fk
            references accounts,
    expire_at  timestamp               not null,
    ip_address varchar                 not null
);

alter table auth_details
    owner to chattee;

create unique index if not exists auth_details_token_uindex
    on auth_details (token);
