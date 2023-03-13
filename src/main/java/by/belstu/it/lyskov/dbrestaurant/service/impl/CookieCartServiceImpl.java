package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.dto.CookieCartItemDto;
import by.belstu.it.lyskov.dbrestaurant.exception.*;
import by.belstu.it.lyskov.dbrestaurant.repository.DishRepository;
import by.belstu.it.lyskov.dbrestaurant.service.CookieCartService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CookieCartServiceImpl implements CookieCartService {

    private final DishRepository dishRepository;

    public CookieCartServiceImpl(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public List<CookieCartItemDto> getCartItems(List<CookieCartItemDto> cart, Pageable pageable) {
        List<CookieCartItemDto> list = new ArrayList<>();
        for (long i = 0; i < pageable.getPageSize(); i++) {
            if (i == cart.size())
                break;
            list.add(cart.get((int) (pageable.getOffset() + i)));
        }
        return list;
    }

    @Override
    public CookieCartItemDto findCartItemByDish(List<CookieCartItemDto> cart, Long dish) throws CartItemNotFoundException {
        return cart.stream().filter(o -> o.getDish() == dish).findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cookie doesn't contain cart item with dish id \"dishId\""));
    }

    @Override
    public void addCartItem(List<CookieCartItemDto> cart, CookieCartItemDto cartItemDto) throws BadReferenceException, ServiceException {
        try {
            if (!dishRepository.existsById(cartItemDto.getDish()))
                throw new BadReferenceException("Dish with id \"" + cartItemDto.getDish() + "\" doesn't exist!");
            Optional<CookieCartItemDto> orderItem = cart.stream().filter(o -> o.getDish() == cartItemDto.getDish()).findFirst();
            if (orderItem.isPresent())
                orderItem.get().setQuantity(orderItem.get().getQuantity() + cartItemDto.getQuantity());
            else
                cart.add(cartItemDto);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to add cart item", e);
        }
    }

    @Override
    public void updateCartItemByDish(List<CookieCartItemDto> cart, Long dish, CookieCartItemDto cartItemDto) throws CartItemNotFoundException {
        CookieCartItemDto orderItem = cart.stream().filter(o -> o.getDish() == dish).findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cookie doesn't contain cart item with dish id \"dishId\""));
        orderItem.setQuantity(orderItem.getQuantity() + cartItemDto.getQuantity());
    }

    @Override
    public void deleteCartItemByDish(List<CookieCartItemDto> cart, Long dish) throws CartItemNotFoundException {
        CookieCartItemDto orderItem = cart.stream().filter(o -> o.getDish() == dish).findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cookie doesn't contain cart item with dish id \"dishId\""));
        cart.remove(orderItem);
    }
}
