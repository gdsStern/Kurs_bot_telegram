-- liquibase formatted sql
create table message (
    id bigint primary key,
    chat_id bigint not null,
    text character varying(255) not null ,
    date timestamp not null
);