truncate table users restart identity cascade;

alter sequence user_id_seq restart with 1;
