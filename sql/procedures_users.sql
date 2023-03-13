use restaurant_db;

-- drop procedure find_user_by_id;
delimiter &&
create procedure find_user_by_id (in p_id int)
begin
	select * from users where id = p_id;
end &&

-- drop procedure get_users_paged;
delimiter &&
create procedure get_users_paged (in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from users order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
    prepare statement from @query;
	execute statement;
end &&

-- drop function count_users;
delimiter &&
create function count_users () returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from users), -1);
	return @count;
end &&

-- drop procedure update_user;
delimiter &&
create procedure update_user (in p_id int, in p_name nvarchar(40),	in p_email varchar(40), in p_phone varchar(15))
begin
	update users set `name` = p_name, `email` = p_email, `phone` = p_phone where id = id;
end &&

-- -------------------------------------------

-- drop procedure find_user_by_login;
delimiter &&
create procedure find_user_by_login (in p_login nvarchar(30))
begin
	select * from users where login = p_login;
end &&

-- drop procedure find_users_by_filter_paged;
delimiter &&
create procedure find_users_by_filter_paged (in p_filter nvarchar(20), in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from users', concat(' where name like "%', p_filter,'%"'), ' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
    prepare statement from @query;
	execute statement;
end &&

-- drop function count_users_by_filter;
delimiter &&
create function count_users_by_filter (p_filter nvarchar(20)) returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from users where name like concat('%', p_filter, '%')), -1);
	return @count;
end &&

-- drop procedure register_user;
delimiter &&
create procedure register_user (in p_login varchar(20), in p_password varchar(200), in p_name nvarchar(40), in p_email varchar(40), in p_phone varchar(15))
begin
	declare v_role_id int;
	declare v_status_id int;
    declare v_user_id int;
    declare v_order_id int;
	select id into v_role_id from roles where name = 'CLIENT';
	select id into v_status_id from statuses where name = 'CREATED';
    insert into users(`login`, `password`, `name`, `email`, `phone`, `role_id`)
		values (p_login, p_password, p_name, p_email, p_phone, v_role_id);
	select id into v_user_id from users where login = p_login;
	insert into orders(`status_id`, `customer_id`) values (v_status_id, v_user_id);
	select id into v_order_id from orders where status_id = v_status_id and customer_id = v_user_id;
    update users set order_id = v_order_id where id = v_user_id;
end &&

-- drop procedure edit_user;
delimiter &&
create procedure edit_user (in p_id int, in p_blocked boolean, in p_role_id int)
begin
	update users set `blocked` = p_blocked, `role_id` = p_role_id where id = p_id;
end &&