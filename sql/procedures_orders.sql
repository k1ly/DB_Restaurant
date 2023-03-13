use restaurant_db;

-- drop procedure find_order_by_id;
delimiter &&
create procedure find_order_by_id (in p_id int)
begin
	select * from orders where id = p_id;
end &&

-- drop procedure update_order;
delimiter &&
create procedure update_order (in p_id int, in p_status_id int, in p_manager_id int)
begin
	declare v_status_name varchar(30);
	select name into v_status_name from statuses where id = p_status_id;
    set @delivery_date = if(v_status_name = 'FINISHED', concat(' delivery_date = ', now(), ','), '');
    set @manager_id = ifnull(concat(', manager_id = ', p_manager_id), '');
	set @query = concat('update orders set', @delivery_date, ' status_id = ', p_status_id, @manager_id, ' where id = ', p_id);
    prepare statement from @query;
    execute statement;
end &&

-- -------------------------------------------

-- drop procedure find_order_by_customer;
delimiter &&
create procedure find_order_by_customer (in p_customer_id int)
begin
	select * from orders where customer_id = p_customer_id;
end &&

-- drop procedure find_orders_by_status_paged;
delimiter &&
create procedure find_orders_by_status_paged (in p_status_id int, in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from orders where status_id = ', p_status_id, ' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
	prepare statement from @query;
	execute statement;
end &&

-- drop function count_orders_by_status;
delimiter &&
create function count_orders_by_status (p_status_id int) returns bigint
reads sql data
begin
set @count = ifnull((select count(*) from orders where status_id =  status_id), -1);
return @count;
end &&

-- drop procedure find_orders_by_status_and_customer_paged;
delimiter &&
create procedure find_orders_by_status_and_customer_paged (in p_status_id int, in p_customer_id int, in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from orders where status_id = ', p_status_id, ' and customer_id = ', p_customer_id,
		' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
	prepare statement from @query;
	execute statement;
end &&

-- drop function count_orders_by_status_and_customer;
delimiter &&
create function count_orders_by_status_and_customer (p_status_id int, p_customer_id int) returns bigint
reads sql data
begin
set @count = ifnull((select count(*) from orders where status_id =  p_status_id and customer_id = p_customer_id), -1);
return @count;
end &&

-- drop procedure confirm_order;
delimiter &&
create procedure confirm_order (in p_id int, in p_price double(10,2), in p_specified_date datetime, in p_address_id int)
begin
	declare v_status_id int;
	declare v_user_id int;
	declare v_order_id int;
	select id into v_status_id from statuses where name = 'AWAITING';
	update orders set `price` = p_price, `specified_date` = p_specified_date, `order_date` = now(), `status_id` = v_status_id, `address_id` = p_address_id where id = p_id;
	select id into v_status_id from statuses where name = 'CREATED';
    select customer_id into v_user_id from orders where id = p_id;
    insert into orders(`status_id`, `customer_id`) values (v_status_id, v_user_id);
	select id into v_order_id from orders where status_id = v_status_id and customer_id = v_user_id;
    update users set order_id = v_order_id where id = v_user_id;
end &&

-- drop procedure cancel_order;
delimiter &&
create procedure cancel_order (in p_id int)
begin
	declare v_status_id int;
	select id into v_status_id from statuses where name = 'CANCELED';
	update orders set `status_id` = v_status_id where id = p_id;
end &&