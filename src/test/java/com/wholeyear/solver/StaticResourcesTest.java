package com.wholeyear.solver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StaticResourcesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootServesIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void indexHtmlIsAccessibleDirectly() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<div id=\"root\">")));
    }

    @Test
    void manifestJsonIsServed() throws Exception {
        mockMvc.perform(get("/manifest.json"))
                .andExpect(status().isOk());
    }

    @Test
    void apiStillWorksAlongsideStaticResources() throws Exception {
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void unknownApiPathReturns404() throws Exception {
        // Non-existent API paths should return 404
        // Note: ErrorPage SPA fallback only works in a real servlet container, not MockMvc
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isNotFound());
    }
}
