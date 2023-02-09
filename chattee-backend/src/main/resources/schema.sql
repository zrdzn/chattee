create table if not exists users
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

alter table users
    owner to chattee;

create unique index if not exists users_id_uindex
    on users (id);

create unique index if not exists users_email_uindex
    on users (email);

create unique index if not exists users_username_uindex
    on users (username);

create table if not exists discussions
(
    id          bigserial
        constraint discussions_pk
            primary key,
    title       varchar(100)  not null,
    description varchar(2000) not null,
    author_id   bigserial
        constraint discussions_user_id_fk
            references users
);

alter table discussions
    owner to chattee;

create unique index if not exists discussions_id_uindex
    on discussions (id);

create table if not exists users_privileges
(
    user_id   bigint  not null
        constraint users_privileges_user_id_fk
            references users,
    privilege varchar not null,
    constraint users_privileges_pk
        primary key (user_id, privilege)
);

alter table users_privileges
    owner to chattee;

create table if not exists sessions
(
    token      varchar                 not null
        constraint sessions_pk
            primary key,
    user_id    bigint                  not null
        constraint sessions_user_id_fk
            references users,
    expire_at  timestamp               not null,
    created_at timestamp default now() not null,
    ip_address varchar                 not null
);

alter table sessions
    owner to chattee;

create unique index if not exists sessions_token_uindex
    on sessions (token);
