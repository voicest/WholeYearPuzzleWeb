package com.wholeyear.solver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SolverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ─── GET /api/board ─────────────────────────────────────────────────

    @Test
    void getBoardReturns200() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBoardReturns63Cells() throws Exception {
        // 7 rows × 9 cols = 63 total cells (including OFF_BOARD)
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(63)));
    }

    @Test
    void getBoardCellsHaveRequiredFields() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].row").exists())
                .andExpect(jsonPath("$[0].col").exists())
                .andExpect(jsonPath("$[0].state").exists());
    }

    @Test
    void getBoardContainsCorrectTargetCells() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2026-03-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Mar' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '11' && @.state == 'TARGET')]", hasSize(1)));
    }

    @Test
    void getBoardHasExactly2TargetCells() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        int count = 0;
        int idx = 0;
        while ((idx = json.indexOf("\"TARGET\"", idx)) != -1) {
            count++;
            idx++;
        }
        org.junit.jupiter.api.Assertions.assertEquals(2, count,
                "Board should have exactly 2 TARGET cells");
    }

    @Test
    void getBoardContainsOffBoardCells() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.state == 'OFF_BOARD')]", hasSize(greaterThan(0))));
    }

    @Test
    void getBoardContainsFillableCells() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.state == 'FILLABLE')]", hasSize(greaterThan(0))));
    }

    @Test
    void getBoardContainsMonthLabels() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jan')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == 'Dec')]", hasSize(1)));
    }

    @Test
    void getBoardContainsDayLabels() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == '1')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '31')]", hasSize(1)));
    }

    @Test
    void getBoardWithoutDateDefaultsToToday() throws Exception {
        // Calling without a date param should still return 200 with 2 TARGET cells
        MvcResult result = mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(63)))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        int count = 0;
        int idx = 0;
        while ((idx = json.indexOf("\"TARGET\"", idx)) != -1) {
            count++;
            idx++;
        }
        org.junit.jupiter.api.Assertions.assertEquals(2, count,
                "Board without date param should still have 2 TARGET cells (today's date)");
    }

    @Test
    void getBoardWithInvalidDateReturns400() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBoardWithInvalidMonthReturns400() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-13-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBoardWithInvalidDayReturns400() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-01-32"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBoardJan1HasCorrectTargets() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jan' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '1' && @.state == 'TARGET')]", hasSize(1)));
    }

    @Test
    void getBoardDec31HasCorrectTargets() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Dec' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '31' && @.state == 'TARGET')]", hasSize(1)));
    }

    // ─── GET /api/pieces ────────────────────────────────────────────────

    @Test
    void getPiecesReturns200() throws Exception {
        mockMvc.perform(get("/api/pieces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPiecesReturns9Pieces() throws Exception {
        mockMvc.perform(get("/api/pieces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    void getPieceHasRequiredFields() throws Exception {
        mockMvc.perform(get("/api/pieces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].shape").exists());
    }

    @Test
    void getPieceIdsAreSequential() throws Exception {
        mockMvc.perform(get("/api/pieces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[8].id").value(8));
    }

    @Test
    void getPieceShapesAreNonEmpty() throws Exception {
        mockMvc.perform(get("/api/pieces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shape", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[8].shape", hasSize(greaterThan(0))));
    }

    // ─── POST /api/updateTargetDate (removed) ───────────────────────────

    @Test
    void updateTargetDateEndpointIsRemoved() throws Exception {
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-06-15"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /api/solve ────────────────────────────────────────────────

    @Test
    void solveReturns200() throws Exception {
        mockMvc.perform(post("/api/solve").param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void solveReturns9Placements() throws Exception {
        mockMvc.perform(post("/api/solve").param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    void solvePlacementsHaveRequiredFields() throws Exception {
        mockMvc.perform(post("/api/solve").param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pieceId").exists())
                .andExpect(jsonPath("$[0].cells").exists())
                .andExpect(jsonPath("$[0].cells", hasSize(greaterThan(0))));
    }

    @Test
    void solveWithoutDateDefaultsToToday() throws Exception {
        mockMvc.perform(post("/api/solve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    void solveWithInvalidDateReturns400() throws Exception {
        mockMvc.perform(post("/api/solve").param("date", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    // ─── Statelessness: concurrent dates don't interfere ────────────────

    @Test
    void boardForDifferentDatesReturnsDifferentTargets() throws Exception {
        mockMvc.perform(get("/api/board").param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jan' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '1' && @.state == 'TARGET')]", hasSize(1)));

        mockMvc.perform(get("/api/board").param("date", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Dec' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '31' && @.state == 'TARGET')]", hasSize(1)));

        // Verify first date still works (no shared state corruption)
        mockMvc.perform(get("/api/board").param("date", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jan' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '1' && @.state == 'TARGET')]", hasSize(1)));
    }
}
