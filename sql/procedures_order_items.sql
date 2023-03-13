use restaurant_db;

-- drop procedure find_order_item_by_id;
delimiter &&
create procedure find_order_item_by_id (in p_id int)
begin
	select * from order_items where id = p_id;
end &&

-- drop procedure add_order_item;
delimiter &&
create procedure add_order_item (in p_quantity int, in p_dish_id int, in p_order_id int)
begin
	insert into order_items(`quantity`, `dish_id`, `order_id`) values (p_quantity, p_dish_id, p_order_id);
end &&

-- drop procedure update_order_item;
delimiter &&
create procedure update_order_item (in p_id int, in p_quantity int)
begin
	update order_items set `quantity` = p_quantity where id = p_id;
end &&

-- drop procedure delete_order_item;
delimiter &&
create procedure delete_order_item (in p_id int)
begin
	delete from order_items where id = p_id;
end &&

-- -------------------------------------------

-- drop procedure find_order_item_by_order_and_dish;
delimiter &&
create procedure find_order_item_by_order_and_dish (in p_order_id int, in p_dish_id int)
begin
	select * from order_items where order_id = p_order_id and dish_id = p_dish_id;
end &&

-- drop procedure find_order_items_by_order_paged;
delimiter &&
create procedure find_order_items_by_order_paged (in p_order_id int, in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from order_items where order_id = ', p_order_id, ' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
	prepare statement from @query;
	execute statement;
end &&

-- drop function count_order_items_by_order;
delimiter &&
create function count_order_items_by_order (p_order_id int) returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from order_items where order_id = p_order_id), -1);
	return @count;
end &&

-- drop function count_order_item_total_price_by_order;
delimiter &&
create function count_order_item_total_price_by_order (p_order_id int) returns double(10,2)
reads sql data
begin
	set @count = ifnull((select sum((price * (100 - discount) / 100) * quantity) from order_items_dishes_view where order_id = p_order_id), -1);
	return @count;
end &&