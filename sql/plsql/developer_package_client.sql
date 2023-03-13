alter session set NLS_LANGUAGE = AMERICAN;

create or replace package client_package as
    function find_address_by_id(p_id in addresses.id%type) return sys_refcursor;
    procedure add_address(p_country in addresses.country%type, p_locality in addresses.locality%type,
                          p_street in addresses.street%type, p_house in addresses.house%type,
                          p_apartment in addresses.apartment%type,
                          p_user_id in addresses.user_id%type);
    function find_addresses_by_user(p_user_id in addresses.user_id%type, p_sort in nvarchar2,
                                           p_direction in varchar2) return sys_refcursor;
    function find_order_item_by_id(p_id in order_items.id%type) return sys_refcursor;
    procedure add_order_item(p_quantity in order_items.quantity%type, p_dish_id in order_items.dish_id%type,
                             p_order_id in order_items.order_id%type);
    procedure update_order_item(p_id in order_items.id%type, p_quantity in order_items.quantity%type);
    procedure delete_order_item(p_id in order_items.id%type);
    function find_order_item_by_dish(p_order_id in order_items.order_id%type,
                                               p_dish_id in order_items.dish_id%type) return sys_refcursor;
    function find_order_items_by_order(p_order_id in order_items.order_id%type, p_start in number,
                                             p_size in number,
                                             p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor;
    function count_order_items_by_order(p_order_id order_items.order_id%type) return number;
    function count_order_item_total_price(p_order_id order_items.order_id%type) return binary_double;
    function find_order_by_id(p_id in orders.id%type) return sys_refcursor;
    function find_order_by_customer(p_customer_id in orders.customer_id%type) return sys_refcursor;
    function find_orders_by_status(p_status_id in orders.status_id%type,
                                                      p_customer_id in orders.customer_id%type,
                                                      p_start in number, p_size in number,
                                                      p_sort in nvarchar2,
                                                      p_direction in varchar2) return sys_refcursor;
    function count_orders_by_status(p_status_id orders.status_id%type,
                                    p_customer_id orders.customer_id%type) return number;
    procedure confirm_order(p_id in orders.id%type, p_price in orders.price%type,
                            p_specified_date in orders.specified_date%type,
                            p_address_id in number);
    procedure cancel_order(p_id in orders.id%type);
    procedure add_review(p_grade in reviews.grade%type, p_comment in reviews."comment"%type,
                         p_user_id in reviews.user_id%type);
    function find_status_by_id(p_id in statuses.id%type) return sys_refcursor;
    function find_status_by_name(p_name in statuses.name%type) return sys_refcursor;
    procedure update_user(p_id in users.id%type, p_name in users.name%type, p_email in users.email%type,
                          p_phone in users.phone%type);
end client_package;

create or replace package body client_package as

    function find_address_by_id(p_id in addresses.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from addresses where id = p_id;
        return c_cursor;
    end;

    procedure add_address(p_country in addresses.country%type, p_locality in addresses.locality%type,
                          p_street in addresses.street%type, p_house in addresses.house%type,
                          p_apartment in addresses.apartment%type,
                          p_user_id in addresses.user_id%type)
        is
    begin
        insert into addresses(country, locality, street, house, apartment, user_id)
        values (p_country, p_locality, p_street, p_house, p_apartment, p_user_id);
    end;

    function find_addresses_by_user(p_user_id in addresses.user_id%type, p_sort in nvarchar2,
                                           p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from addresses where user_id = ' || p_user_id || ' order by ' || p_sort || ' ' ||
                   p_direction;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function find_order_item_by_id(p_id in order_items.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from order_items where id = p_id;
        return c_cursor;
    end;

    procedure add_order_item(p_quantity in order_items.quantity%type, p_dish_id in order_items.dish_id%type,
                             p_order_id in order_items.order_id%type)
        is
    begin
        insert into order_items(quantity, dish_id, order_id) values (p_quantity, p_dish_id, p_order_id);
    end;

    procedure update_order_item(p_id in order_items.id%type, p_quantity in order_items.quantity%type)
        is
    begin
        update order_items set quantity = p_quantity where id = p_id;
    end;

    procedure delete_order_item(p_id in order_items.id%type)
        is
    begin
        delete from order_items where id = p_id;
    end;

    function find_order_item_by_dish(p_order_id in order_items.order_id%type,
                                               p_dish_id in order_items.dish_id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from order_items where order_id = p_order_id and dish_id = p_dish_id;
        return c_cursor;
    end;

    function find_order_items_by_order(p_order_id in order_items.order_id%type, p_start in number,
                                             p_size in number,
                                             p_sort in nvarchar2, p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from order_items where order_id = ' || p_order_id || ' order by ' ||
                   p_sort || ' ' ||
                   p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_order_items_by_order(p_order_id order_items.order_id%type) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from order_items where order_id = p_order_id), -1) into v_count from dual;
        return v_count;
    end;

    function count_order_item_total_price(p_order_id order_items.order_id%type) return binary_double
        is
        v_count number;
    begin
        select nvl((select sum((price * (100 - discount) / 100) * quantity)
                    from order_items_dishes_view
                    where order_id = p_order_id), -1)
        into v_count
        from dual;
        return v_count;
    end;

    function find_order_by_id(p_id in orders.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from orders where id = p_id;
        return c_cursor;
    end;

    function find_order_by_customer(p_customer_id in orders.customer_id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from orders where customer_id = p_customer_id;
        return c_cursor;
    end;

    function find_orders_by_status(p_status_id in orders.status_id%type,
                                                      p_customer_id in orders.customer_id%type,
                                                      p_start in number, p_size in number,
                                                      p_sort in nvarchar2,
                                                      p_direction in varchar2) return sys_refcursor
        is
        q_query  varchar2(300);
        c_cursor sys_refcursor;
    begin
        q_query := 'select * from (select * from orders where status_id = ' || p_status_id || ' and customer_id = ' ||
                   p_customer_id ||
                   ' order by ' || p_sort || ' ' || p_direction || ') where rownum <= ' || p_start || ', ' || p_size;
        open c_cursor for q_query;
        return c_cursor;
    end;

    function count_orders_by_status(p_status_id orders.status_id%type,
                                                 p_customer_id orders.customer_id%type) return number
        is
        v_count number;
    begin
        select nvl((select count(*) from orders where status_id = p_status_id and customer_id = p_customer_id), -1)
        into v_count
        from dual;
        return v_count;
    end;

    procedure confirm_order(p_id in orders.id%type, p_price in orders.price%type,
                            p_specified_date in orders.specified_date%type,
                            p_address_id in number)
        is
        v_status_id statuses.id%type;
        v_user_id   users.id%type;
        v_order_id  orders.id%type;
    begin
        select id into v_status_id from statuses where name = 'AWAITING';
        update orders
        set price          = p_price,
            specified_date = p_specified_date,
            order_date     = systimestamp,
            status_id      = v_status_id,
            address_id     = p_address_id
        where id = p_id;
        select id into v_status_id from statuses where name = 'CREATED';
        select customer_id into v_user_id from orders where id = p_id;
        insert into orders(status_id, customer_id) values (v_status_id, v_user_id);
        select id into v_order_id from orders where status_id = v_status_id and customer_id = v_user_id;
        update users set order_id = v_order_id where id = v_user_id;
    end;

    procedure cancel_order(p_id in orders.id%type)
        is
        v_status_id statuses.id%type;
    begin
        select id into v_status_id from statuses where name = 'CANCELED';
        update orders set status_id = v_status_id where id = p_id;
    end;

    procedure add_review(p_grade in reviews.grade%type, p_comment in reviews."comment"%type,
                         p_user_id in reviews.user_id%type)
        is
    begin
        insert into reviews(grade, "comment", "date", user_id) values (p_grade, p_comment, systimestamp, p_user_id);
    end;

    function find_status_by_id(p_id in statuses.id%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from statuses where id = p_id;
        return c_cursor;
    end;

    function find_status_by_name(p_name in statuses.name%type) return sys_refcursor
        is
        c_cursor sys_refcursor;
    begin
        open c_cursor for select * from statuses where name = p_name;
        return c_cursor;
    end;

    procedure update_user(p_id in users.id%type, p_name in users.name%type, p_email in users.email%type,
                          p_phone in users.phone%type)
        is
    begin
        update users set name = p_name, email = p_email, phone = p_phone where id = p_id;
    end;

end client_package;
