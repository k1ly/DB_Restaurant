package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.dto.CookieCartItemDto;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.CartItemNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

import java.util.List;

public interface CookieCartService {

    List<CookieCartItemDto> getCartItems(List<CookieCartItemDto> cart, Pageable pageable);

    CookieCartItemDto findCartItemByDish(List<CookieCartItemDto> cart, Long dish) throws CartItemNotFoundException;

    void addCartItem(List<CookieCartItemDto> cart, CookieCartItemDto cartItemDto) throws BadReferenceException, ServiceException;

    void updateCartItemByDish(List<CookieCartItemDto> cart, Long dish, CookieCartItemDto cartItemDto) throws CartItemNotFoundException;

    void deleteCartItemByDish(List<CookieCartItemDto> cart, Long dish) throws CartItemNotFoundException;
}
