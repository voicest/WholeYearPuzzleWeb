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
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBoardReturns63Cells() throws Exception {
        // 7 rows × 9 cols = 63 total cells (including OFF_BOARD)
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(63)));
    }

    @Test
    void getBoardCellsHaveRequiredFields() throws Exception {
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].row").exists())
                .andExpect(jsonPath("$[0].col").exists())
                .andExpect(jsonPath("$[0].state").exists());
    }

    @Test
    void getBoardContainsTargetCells() throws Exception {
        // Controller sets today's date as target on startup; there should be 2 TARGET cells
        MvcResult result = mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        // Count occurrences of "TARGET"
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
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.state == 'OFF_BOARD')]", hasSize(greaterThan(0))));
    }

    @Test
    void getBoardContainsFillableCells() throws Exception {
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.state == 'FILLABLE')]", hasSize(greaterThan(0))));
    }

    @Test
    void getBoardContainsMonthLabels() throws Exception {
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jan')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == 'Dec')]", hasSize(1)));
    }

    @Test
    void getBoardContainsDayLabels() throws Exception {
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == '1')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '31')]", hasSize(1)));
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

    // ─── POST /api/updateTargetDate ─────────────────────────────────────

    @Test
    void updateTargetDateReturns200() throws Exception {
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-06-15"))
                .andExpect(status().isOk());
    }

    @Test
    void updateTargetDateChangesBoardState() throws Exception {
        // Update to Jun 15
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-06-15"))
                .andExpect(status().isOk());

        // Verify the board now has Jun and 15 as TARGET cells
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jun' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '15' && @.state == 'TARGET')]", hasSize(1)));
    }

    @Test
    void updateTargetDateHandlesJan1() throws Exception {
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-01-01"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Jan' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '1' && @.state == 'TARGET')]", hasSize(1)));
    }

    @Test
    void updateTargetDateHandlesDec31() throws Exception {
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-12-31"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.label == 'Dec' && @.state == 'TARGET')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.label == '31' && @.state == 'TARGET')]", hasSize(1)));
    }

    // ─── POST /api/solve ────────────────────────────────────────────────

    @Test
    void solveReturns200() throws Exception {
        // First set a known date
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-01-01"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/solve"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void solveReturns9Placements() throws Exception {
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-01-01"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/solve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    void solvePlacementsHaveRequiredFields() throws Exception {
        mockMvc.perform(post("/api/updateTargetDate")
                        .param("date", "2024-01-01"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/solve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pieceId").exists())
                .andExpect(jsonPath("$[0].cells").exists())
                .andExpect(jsonPath("$[0].cells", hasSize(greaterThan(0))));
    }
}
