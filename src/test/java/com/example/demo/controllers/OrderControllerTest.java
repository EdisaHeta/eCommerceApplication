package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.InjectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.InjectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void testSubmit() {
        Item item = createItem(1L, "Fidget Spinner", BigDecimal.valueOf(5), "Metal Toy");
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createCart(1L, items, null);
        User user = createUser(1L, "Edisa", "12345678", cart);
        cart.setUser(user);

        when(userRepository.findByUsername("Edisa")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("Edisa");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertEquals(user, order.getUser());
        assertEquals(items, order.getItems());
        assertEquals(BigDecimal.valueOf(5), order.getTotal());
    }

    @Test
    public void testGetOrdersForUser() {
        Item item = createItem(1L, "Fidget Spinner", BigDecimal.valueOf(5), "Metal Toy");
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createCart(1L, new ArrayList<>(), null);
        User user = createUser(1L, "Edisa", "12345678", null);
        cart.setUser(user);
        cart.setItems(items);
        user.setCart(cart);

        orderController.submit("Edisa");
        when(userRepository.findByUsername("Edisa")).thenReturn(user);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Edisa");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void testSubmitFail() {
        when(userRepository.findByUsername("Edisa")).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit("Edisa");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetOrdersForUserFail() {
        when(userRepository.findByUsername("Edisa")).thenReturn(null);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Edisa");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Helper method to create a User instance
     *
     * @param userId
     * @param username
     * @param password
     * @param cart
     * @return User object
     */
    public User createUser(long userId, String username, String password, Cart cart) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setCart(cart);
        return newUser;
    }

    /**
     * Helper method to create an Item instance
     *
     * @param id
     * @param name
     * @param price
     * @param description
     * @return Item object.
     */
    public Item createItem(Long id, String name, BigDecimal price, String description) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setPrice(price);
        newItem.setDescription(description);
        return newItem;
    }

    /**
     * Helper method to create a Cart instance.
     *
     * @param cartId
     * @param items
     * @param user
     * @return Cart object
     */
    public Cart createCart(long cartId, ArrayList<Item> items, User user) {
        Cart newCart = new Cart();
        newCart.setId(cartId);
        newCart.setItems(items);
        newCart.setUser(user);
        newCart.setTotal(BigDecimal.valueOf(5));
        return newCart;
    }
}
