use restaurant_db;

-- drop procedure find_category_by_id;
delimiter &&
create procedure find_category_by_id (in p_id int)
begin
	select * from categories where id = p_id;
end &&

-- drop procedure get_categories_sorted;
delimiter &&
create procedure get_categories_sorted (in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from categories order by ', p_sort, ' ', p_direction);
	prepare statement from @query;
	execute statement;
end &&

-- drop procedure get_categories_paged;
delimiter &&
create procedure get_categories_paged (in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from categories order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
	prepare statement from @query;
	execute statement;
end &&

-- drop function count_categories;
delimiter &&
create function count_categories () returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from categories), -1);
	return @count;
end &&

-- drop procedure add_category;
delimiter &&
create procedure add_category (in p_name nvarchar(30))
begin
	insert into categories(`name`) values (p_name);
end &&

-- drop procedure update_category;
delimiter &&
create procedure update_category (in p_id int, in p_name nvarchar(30))
begin
	update categories set `name` = p_name where id = p_id;
end &&

-- drop procedure delete_category;
delimiter &&
create procedure delete_category (in p_id int)
begin
	delete from categories where id = p_id;
end &&

-- -------------------------------------------

-- drop procedure find_categories_by_filter_paged;
delimiter &&
create procedure find_categories_by_filter_paged (in p_filter nvarchar(20), in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from categories', concat(' where name like "%', p_filter,'%"'), ' order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
    prepare statement from @query;
	execute statement;
end &&

-- drop function count_categories_by_filter;
delimiter &&
create function count_categories_by_filter (p_filter nvarchar(20)) returns bigint
reads sql data
begin
set @count = ifnull((select count(*) from categories where name like concat('%', p_filter, '%')), -1);
return @count;
end &&