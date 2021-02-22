package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.InjectObjects(userController, "userRepository", userRepository);
        TestUtils.InjectObjects(userController, "cartRepository", cartRepository);
        TestUtils.InjectObjects(userController, "bCryptPasswordEncoder", encoder);
    }


    @Test
    public void testCreateUser() throws Exception {
        when(encoder.encode("12345678")).thenReturn("12345678Hashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Edisa");
        r.setPassword("12345678");
        r.setConfirmPassword("12345678");

        final ResponseEntity<User> createdUserResponse = userController.createUser(r);
        assertNotNull(createdUserResponse);
        assertEquals(200, createdUserResponse.getStatusCodeValue());

        User u = createdUserResponse.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("Edisa", u.getUsername());
        assertEquals("12345678Hashed", u.getPassword());
    }

    @Test
    public void testFindUserById() throws Exception {
        //Create new user
        final ResponseEntity<User> createdUserResponse = createTestingUser();
        assertNotNull(createdUserResponse);
        assertEquals(200, createdUserResponse.getStatusCodeValue());

        User createdUser = createdUserResponse.getBody();
        assertNotNull(createdUser);
        assertEquals(0, createdUser.getId());
        assertEquals("Edisa", createdUser.getUsername());
        assertEquals("12345678Hashed", createdUser.getPassword());

        // Find User By Id
        when(userRepository.findById(createdUser.getId())).thenReturn(Optional.of(createdUser));
        final ResponseEntity<User> findUserById = userController.findById(createdUser.getId());
        User userFound = findUserById.getBody();
        assertEquals(200, findUserById.getStatusCodeValue());
        assertEquals(0, userFound.getId());
        assertEquals("Edisa", userFound.getUsername());
        assertEquals("12345678Hashed", userFound.getPassword());
    }

    @Test
    public void testFindUserByUsername() throws Exception {
        // Create new User
        final ResponseEntity<User> createdUserResponse = createTestingUser();
        assertNotNull(createdUserResponse);
        assertEquals(200, createdUserResponse.getStatusCodeValue());

        User createdUser = createdUserResponse.getBody();
        assertNotNull(createdUser);
        assertEquals(0, createdUser.getId());
        assertEquals("Edisa", createdUser.getUsername());
        assertEquals("12345678Hashed", createdUser.getPassword());

        // Find User by Username "Edisa"
        when(userRepository.findByUsername(createdUser.getUsername())).thenReturn(createdUser);
        final ResponseEntity<User> findByUserName = userController.findByUserName(createdUser.getUsername());
        User userFound = findByUserName.getBody();
        assertEquals(200, findByUserName.getStatusCodeValue());
        assertEquals(0, userFound.getId());
        assertEquals("Edisa", userFound.getUsername());
        assertEquals("12345678Hashed", userFound.getPassword());
    }

    @Test
    public void testCreateUserFail() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Edisa");
        r.setPassword("1234");
        r.setConfirmPassword("1234");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User u = response.getBody();
        assertNull(u);
    }

    @Test
    public void testFindByIdNotFound() {
        final ResponseEntity<User> response = userController.findByUserName("Edisa2");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUsernameNotFound() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    /**
     * Create testing user
     * @return
     */
    public ResponseEntity<User> createTestingUser() {
        when(encoder.encode("12345678")).thenReturn("12345678Hashed");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Edisa");
        userRequest.setPassword("12345678");
        userRequest.setConfirmPassword("12345678");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        return response;
    }
}
