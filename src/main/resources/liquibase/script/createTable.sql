-- liquibase formatted sql
create table message (
    id bigint primary key ,
    chatId bigint not null,
    txt character varying(255) not null ,
    data timestamp not null
);