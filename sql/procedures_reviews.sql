use restaurant_db;

-- drop procedure get_reviews_paged;
delimiter &&
create procedure get_reviews_paged (in p_start int, in p_size int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from reviews order by ', p_sort, ' ', p_direction, ' limit ', p_start, ', ', p_size);
	prepare statement from @query;
	execute statement;
end &&

-- drop function count_reviews;
delimiter &&
create function count_reviews () returns bigint
reads sql data
begin
	set @count = ifnull((select count(*) from reviews), -1);
	return @count;
end &&

-- drop procedure add_review;
delimiter &&
create procedure add_review (in p_grade tinyint, in p_comment nvarchar(200), in p_user_id int)
begin
	insert into reviews(`grade`, `comment`, `date`, `user_id`) values (p_grade, p_comment, now(), p_user_id);
end &&