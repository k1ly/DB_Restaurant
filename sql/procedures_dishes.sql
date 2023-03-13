use restaurant_db;

-- drop procedure find_dish_by_id;
delimiter &&
create procedure find_dish_by_id (in p_id int)
begin
	select * from dishes where id = p_id;
end &&

-- drop procedure get_dishes_paged;
delimiter &&
create procedure get_dishes_paged (in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from dishes order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
    prepare statement from @query;
	execute statement;
end &&

-- drop function count_dishes;
delimiter &&
create function count_dishes () returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from dishes), -1);
	return @count;
end &&

-- drop procedure add_dish;
delimiter &&
create procedure add_dish (in p_name nvarchar(40), in p_description nvarchar(200), in p_image_url varchar(100),
	in p_weight smallint, in p_price double(5,2), in p_discount tinyint, in p_category_id int)
begin
	insert into dishes(`name`, `description`, `image_url`, `weight`, `price`, `discount`, `category_id`)
		values (p_name, p_description, p_image_url, p_weight, p_price, p_discount, p_category_id);
end &&

-- drop procedure update_dish;
delimiter &&
create procedure update_dish (in p_id int, in p_name nvarchar(40), in p_description nvarchar(200), in p_image_url varchar(100),
	in p_weight smallint, in p_price double(5,2), in p_discount tinyint, in p_category_id int)
begin
	update dishes set `name` = p_name, `description` = p_description, `image_url` = p_image_url,
		`weight` = p_weight, `price` = p_price, `discount` = discount, `category_id` = p_category_id where id = p_id;
end &&

-- drop procedure delete_dish;
delimiter &&
create procedure delete_dish (in p_id int)
begin
	delete from dishes where id = p_id;
end &&

-- -------------------------------------------

-- drop procedure find_dishes_by_filter_paged;
delimiter &&
create procedure find_dishes_by_filter_paged (in p_filter nvarchar(20), in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from dishes', concat(' where name like "%', p_filter,'%"'), ' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
    prepare statement from @query;
	execute statement;
end &&

-- drop function count_dishes_by_filter;
delimiter &&
create function count_dishes_by_filter (p_filter nvarchar(20)) returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from dishes where name like concat('%', p_filter, '%')), -1);
	return @count;
end &&

-- drop procedure find_dishes_by_category_paged;
delimiter &&
create procedure find_dishes_by_category_paged (in p_category_id int, in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from dishes where category_id = ', p_category_id, ' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
	prepare statement from @query;
	execute statement;
end &&

-- drop function count_dishes_by_category;
delimiter &&
create function count_dishes_by_category (p_category_id int) returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from dishes where category_id = p_category_id), -1);
	return @count;
end &&