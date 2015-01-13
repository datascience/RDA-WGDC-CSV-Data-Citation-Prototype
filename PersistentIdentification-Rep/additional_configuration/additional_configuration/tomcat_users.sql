truncate table users;	

create table users (
  user_name         varchar(15) not null primary key,
  user_pass         varchar(15) not null
);

create table user_roles (
  user_name         varchar(15) not null,
  role_name         varchar(15) not null,
  primary key (user_name, role_name)
);

insert into users values('admin','admin123');
insert into user_roles values('admin','manager-gui');
insert into user_roles values('admin','manager-script');