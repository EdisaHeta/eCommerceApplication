package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.InjectObjects(itemController, "itemRepository", itemRepository);
        Item item = new Item();
        item.setId(1L);
        item.setName("A Widget");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("A widget description");
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void testGetItems() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetItemById() {
        Item newItem = createItem(1L, "Fidget Spinner", "Toy", new BigDecimal("5"));
        when(itemRepository.findById(newItem.getId())).thenReturn(Optional.of(newItem));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item itemFound = response.getBody();
        assertNotNull(itemFound);
        assertEquals(Long.valueOf(1L), itemFound.getId());
        assertEquals("Fidget Spinner", itemFound.getName());
        assertEquals("Toy", itemFound.getDescription());
        assertEquals(BigDecimal.valueOf(5), itemFound.getPrice());
    }

    @Test
    public void testGetItemsByName() {
        Item newItem1 = createItem(1L, "Round Widget", "Round Widget First Description", new BigDecimal("6"));
        Item newItem2 = createItem(1L, "Round Widget", "Round Widget Second Description", new BigDecimal("7"));
        List<Item> items = new ArrayList<>();
        items.add(newItem1);
        items.add(newItem2);
        when(itemRepository.findByName("Round Widget")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> itemsFound = response.getBody();
        assertNotNull(itemsFound);
        assertEquals(Long.valueOf(1L), itemsFound.get(0).getId());
        assertEquals("Round Widget", itemsFound.get(0).getName());
        assertEquals("Round Widget First Description", itemsFound.get(0).getDescription());
        assertEquals(BigDecimal.valueOf(6), itemsFound.get(0).getPrice());
    }

    public Item createItem(Long id, String name, String description, BigDecimal price) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setDescription(description);
        newItem.setPrice(price);
        return newItem;
    }

//    @Before
//    public void setUp(){
//        itemController = new ItemController();
//        TestUtils.InjectObjects(itemController,"itemRepository",itemRepository);
//        Item item = new Item();
//        item.setId(1L);
//        item.setName("A Widget");
//        BigDecimal price = BigDecimal.valueOf(2.99);
//        item.setPrice(price);
//        item.setDescription("A widget description");
//        Item item2 = new Item();
//        item2.setName("Square Widget");
//        item2.setPrice(BigDecimal.valueOf(1.99));
//        item2.setDescription("A widget that is square");
//        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
//        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
//        when(itemRepository.findByName("A Widget")).thenReturn(Collections.singletonList(item));
//    }
//
//    @Test
//    public void testGetAllItems() {
//        ResponseEntity<List<Item>> response = itemController.getItems();
//        // assertNotEquals(response);
//        assertEquals(200, response.getStatusCodeValue());
//        List<Item> items = response.getBody();
//        assertNotNull(items);
//        assertEquals(1, items.size());
//    }
}
