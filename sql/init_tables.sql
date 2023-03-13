use restaurant_db;

insert into roles(name) values('ADMIN');
insert into roles(name) values('MANAGER');
insert into roles(name) values('CLIENT');
insert into roles(name) values('GUEST');

insert into statuses(name) values('CREATED');
insert into statuses(name) values('AWAITING');
insert into statuses(name) values('CANCELED');
insert into statuses(name) values('PREPARING');
insert into statuses(name) values('READY');
insert into statuses(name) values('NOT_PAID');
insert into statuses(name) values('FINISHED');

call register_user('admin', '$2a$10$Oa7ihAgjBHyUhYleJI/81edhrzCAlzGe2IaSwxN5AfNCFqEkxDyG.', 'admin', 'admin@gmail.com', null);
call edit_user(1, false, 1);
select * from users;

-- call insert_category_proc('Завтрак');
-- call insert_category_proc('Салаты');
-- call insert_category_proc('Супы');
-- call insert_category_proc('Блюда из курицы');
-- call insert_category_proc('Блюда из свинины');
-- call insert_category_proc('Блюда из говядины');
-- call insert_category_proc('Гарниры');
-- call insert_category_proc('Десерты');
-- call insert_category_proc('Напитки');

-- call insert_dish_proc('Яичница', 'Яичница', '//', 70, 8, 1),
-- call insert_dish_proc('Яичница1', 'Яичница', '//', 70, 8, 1),
-- call insert_dish_proc('Яичница2', 'Яичница', '//', 70, 8, 1),
-- call insert_dish_proc('Борщ', 'Борщ', '//', 150, 15, 3),
-- call insert_dish_proc('Цезарь', 'Цезарь', '//', 120, 12, 2),
-- call insert_dish_proc('Шашлык', 'Шашлык', '//', 200, 21, 5);