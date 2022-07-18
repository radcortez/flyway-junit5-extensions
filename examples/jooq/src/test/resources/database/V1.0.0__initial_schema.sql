create table users (
    id varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    age integer not null,
    primary key (id)
);

insert into users values ('3df5eeff-f93d-4036-b1aa-9e96a7a8820d', 'Naruto', 'Uzumaki', 17);
