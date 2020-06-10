package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.Field;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    Solver solver;
    Field field;

    @BeforeEach
    void setUp() {
        field = new Field(new char[4][4]);
        solver = new Solver(field);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void solvePuzzleGenetic() {
    }

    @Test
    void initializePopulation() {
        int targetPopulationSize = 1337;
        solver = new Solver(targetPopulationSize, 4711, 0.666, field);
        solver.initializePopulation();
        assertEquals(targetPopulationSize, solver.getPopulation().size());
    }

    @Test
    void checkSolution() {

        char[][] input1 = new char[][]{
                {'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'B', 'W', 'W', 'W', 'B', 'B'},
                {'B', 'B', 'B', 'W', 'W', 'B', 'B', 'W', 'B', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'W'},
                {'B', 'B', 'B', 'W', 'B', 'B', 'B', 'W', 'W', 'B'},
                {'B', 'B', 'B', 'W', 'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'B', 'B', 'B', 'W', 'W', 'B'},
                {'B', 'W', 'B', 'W', 'W', 'W', 'W', 'B', 'B', 'W'},
                {'W', 'B', 'W', 'W', 'W', 'W', 'B', 'W', 'W', 'B'},
                {'B', 'W', 'B', 'W', 'B', 'W', 'W', 'W', 'W', 'W'}
        };

        char[][] input2 = new char[][]{
                {'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'B', 'W'},
                {'B', 'B', 'B', 'W', 'W', 'B', 'B', 'W', 'B', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'B', 'B', 'W', 'W', 'W'},
                {'B', 'W', 'B', 'B', 'W', 'B', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'B', 'W', 'W', 'B', 'W', 'W', 'W', 'W'},
                {'B', 'W', 'W', 'W', 'B', 'W', 'W', 'W', 'B', 'W'},
                {'B', 'W', 'W', 'W', 'W', 'B', 'W', 'W', 'B', 'W'},
                {'W', 'B', 'W', 'B', 'W', 'W', 'B', 'W', 'W', 'B'},
                {'B', 'W', 'W', 'W', 'W', 'B', 'W', 'W', 'B', 'W'}
        };

        //assert(solver.checkSolution(new Field(input1)) < 1000);
        assert(solver.checkSolution(new Field(input2)) < 1000);
    }

    @Test
    void performHorizontalOnePointCrossover() {
        char[][] solutionParentA = new char[][]{
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'}
        };
        char[][] solutionParentB = new char[][]{
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'}
        };
        char[][] expectedChildSolution = new char[][]{
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'W', 'W', 'W', 'W', 'W', 'W'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'}
        };

        Player parentA = new Player(solutionParentA);
        Player parentB = new Player(solutionParentB);

        Player actualChild = solver.performHorizontalOnePointCrossover(parentA, parentB);

        assertArrayEquals(expectedChildSolution, actualChild.getProposedSolution());
    }

    @Test
    void validateRectangleCountArray() {

        int[][] inputCounts = new int[][]{
                {0,0,0,0,0,0,0,0,0,0},
                {1,0,1,0,1,0,1,0,1,0},
                {1,1,1,1,1,1,1,1,1,1},
                {2,2,1,1,2,2,1,1,2,2},
                {2,2,0,0,2,2,0,0,2,2},
                {8,0,8,0,8,0,8,0,8,0},
                {8,0,8,0,8,0,8,1,8,0}
        };

        boolean[] expectation = new boolean[]{
                true,
                true,
                true,
                false,
                true,
                true,
                false
        };

        for (int i = 0; i < inputCounts.length; i++) {
            assertEquals(expectation[i], solver.validateRectangleCountArray(inputCounts[i]));
        }
    }
}