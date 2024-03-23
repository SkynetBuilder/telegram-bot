-- liquibase formatted sql

-- changeset vgulenkov:2
create table if not exists notification_task (
id UUID primary key,
chat_id bigint not null,
message_text text not null,
date_time timestamp not null
)