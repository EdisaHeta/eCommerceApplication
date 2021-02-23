package com.example.demo.security;

import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {

    @Autowired
    private MockMvc mvc;


    @Test
    public void testUnauthenticatedUsers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/cart")).andExpect(status().isUnauthorized());
    }

    @Test
    public void testNonExistentUser() throws Exception {
        String username = "Edisa1";
        String password = "password";

        String body = "{\"username\":\"" + username + "\", \"password\":\" " + password + "\"}";

        mvc.perform(MockMvcRequestBuilders.post("/api/user/Edisa")
                .content(body))
                .andExpect(status().isUnauthorized()).andReturn();
    }


    @Test
    public void testGetUserWithToken() throws Exception {
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJFZGlzYSIsImV4cCI6MTYxNDkwMDA3MH0.8R8qs-CcXDAjyajThH84rC2xV8eNJaXwQq9A_kzNazTeM6yUAtRH2zJCIc_BYiD-x2dzJo2PSSO9ptsjGQpT_w";

        assertNotNull(token);

        mvc.perform(MockMvcRequestBuilders.get("/api/user/Edisa").header("Authorization", token)).andExpect(status().isOk());
    }

    @Test
    public void testGetUserFailedAttempt() throws Exception {
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJFZGlzYSIsImV4cCI6MTYxNDkwMDA3MH0.8R8qs-CcXDAjyajThH84rC2xV8eNJaXwQq9A_kzNazTeM6yUAtRH2zJCIc_BYiD-x2dzJo2PSSO9ptsjGQpT_1";

        assertNotNull(token);

        mvc.perform(MockMvcRequestBuilders.get("/api/user/Edisa2").header("Authorization", token)).andExpect(status().isNotFound());
    }

}
