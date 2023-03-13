alter session set NLS_LANGUAGE = AMERICAN;

insert into roles(name)
values ('ADMIN');
insert into roles(name)
values ('MANAGER');
insert into roles(name)
values ('CLIENT');
insert into roles(name)
values ('GUEST');

insert into statuses(name)
values ('CREATED');
insert into statuses(name)
values ('AWAITING');
insert into statuses(name)
values ('CANCELED');
insert into statuses(name)
values ('PREPARING');
insert into statuses(name)
values ('READY');
insert into statuses(name)
values ('NOT_PAID');
insert into statuses(name)
values ('FINISHED');
call guest_package.register_user('admin', '$2a$10$Oa7ihAgjBHyUhYleJI/81edhrzCAlzGe2IaSwxN5AfNCFqEkxDyG.', 'admin', 'admin@gmail.com',
                   null);
call admin_package.edit_user(1, 0, 1);

select *
from users;