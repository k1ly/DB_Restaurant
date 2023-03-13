package by.belstu.it.lyskov.dbrestaurant.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    void givenURIReturnsJsonObjectAndTripIdExists_MockMVC() throws Exception {
        mockMvc.perform(get("/trips/{id}", "1")).andExpect(status().isOk())
                .andDo(print()).andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1)).andReturn();
    }
}