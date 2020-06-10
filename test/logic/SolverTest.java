package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.Cell;
import structure.Field;
import structure.Region;

import java.util.ArrayList;
import java.util.Arrays;

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
    void stepGeneration() {
        solver.stepGeneration(0, solver.getPopulation());
    }

    @Test
    void solvePuzzleGenetic() {
        //solver.solvePuzzleGenetic();
    }

    @Test
    void performSelection() {
        solver = new Solver(120, 60, 0.1, field);
        ArrayList<Player> beforeSelection = new ArrayList<Player>();
        for (int i = 0; i < 120; i++) {
            Player player = new Player(4, 4, true);
            player.setFitness(i + 1);
            beforeSelection.add(player);
        }
        ArrayList<Player> afterSelection = solver.performSelection(beforeSelection, 7260);
        assertEquals(80, afterSelection.size());
    }

    @Test
    void mate() {
        solver = new Solver(120, 60, 0.1, field);
        ArrayList<Player> matingPool = new ArrayList<Player>();
        for (int i = 0; i < 40; i++) {
            char[][] black = new char[][]{
                    {'B', 'B', 'B', 'B'},
                    {'B', 'B', 'B', 'B'},
                    {'B', 'B', 'B', 'B'},
                    {'B', 'B', 'B', 'B'},
            };
            matingPool.add(new Player(black));
        }
        for (int i = 0; i < 40; i++) {
            char[][] white = new char[][]{
                    {'W', 'W', 'W', 'W'},
                    {'W', 'W', 'W', 'W'},
                    {'W', 'W', 'W', 'W'},
                    {'W', 'W', 'W', 'W'},
            };
            matingPool.add(new Player(white));
        }
        ArrayList<Player> nextGeneration = solver.mate(matingPool);
        assertEquals(120, nextGeneration.size());
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

        ArrayList<Region> regions1 = new ArrayList<Region>();
        ArrayList<Cell> cellsInRegion1 = new ArrayList<Cell>();
        cellsInRegion1.add(new Cell(0,0));
        cellsInRegion1.add(new Cell(1,0));
        cellsInRegion1.add(new Cell(0,1));
        cellsInRegion1.add(new Cell(1,1));
        regions1.add(new Region(3, 4, cellsInRegion1));
        Field field1 = new Field(10, 10, regions1);
        field1.setSolution(input1);

        ArrayList<Region> regions2 = new ArrayList<Region>();
        ArrayList<Cell> cellsInRegion2 = new ArrayList<Cell>();
        cellsInRegion2.add(new Cell(5,2));
        cellsInRegion2.add(new Cell(6,2));
        cellsInRegion2.add(new Cell(5,3));
        cellsInRegion2.add(new Cell(6,3));
        cellsInRegion2.add(new Cell(5,4));
        cellsInRegion2.add(new Cell(5,5));
        regions2.add(new Region(6, 6, cellsInRegion2));
        Field field2 = new Field(10, 10, regions2);
        field2.setSolution(input2);

        assert (solver.checkSolution(field1) < 1000);
        assert (solver.checkSolution(field2) < 1000);
    }

    @Test
    void performHorizontalOnePointCrossoverPositive() {
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
    void performHorizontalOnePointCrossoverNegative() {
        char[][] solutionParentA = new char[][]{
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'}
        };

        char[][] solutionParentB = new char[][]{
                {'W', 'B', 'W'},
                {'B', 'W', 'B'},
                {'W', 'B', 'W'}
        };

        Player parentA = new Player(solutionParentA);
        Player parentB = new Player(solutionParentB);

        Exception e = assertThrows(IllegalArgumentException.class, () -> solver.performHorizontalOnePointCrossover(parentA, parentB));

        assertEquals("Parent dimensions do not match!", e.getMessage());
    }

    @Test
    void validateRectangleCountArray() {

        int[][] inputCounts = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {2, 2, 1, 1, 2, 2, 1, 1, 2, 2},
                {2, 2, 0, 0, 2, 2, 0, 0, 2, 2},
                {8, 0, 8, 0, 8, 0, 8, 0, 8, 0},
                {8, 0, 8, 0, 8, 0, 8, 1, 8, 0},
                {-1, 0, -1, -1, -1, 0, 0, -2, -2, -2},
                {0, 0, -1, -2, -3, 0, 1, -4, -4, 0}
        };

        int[] expectation = new int[]{
                0,
                0,
                0,
                4,
                0,
                0,
                2,
                0,
                3
        };

        for (int i = 0; i < inputCounts.length; i++) {
            assertEquals(expectation[i], solver.validateRectangleCountArray(inputCounts[i]));
        }
    }

    @Test
    void getCurrentBestPlayerSolution() {
        char[][] solutionA = new char[][]{
                {'h', 'e', 'l', 'l', 'o'},
                {'w', 'o', 'r', 'l', 'd'}
        };

        char[][] solutionB = new char[][]{
                {'l', 'o', 'r', 'e', 'm'},
                {'i', 'p', 's', 'u', 'm'}
        };

        Player playerA = new Player(solutionA);
        Player playerB = new Player(solutionB);

        playerA.setFitness(1337);
        playerB.setFitness(4711);

        ArrayList<Player> testPlayers = new ArrayList<>(Arrays.asList(playerA, playerB));

        solver.setPopulation(testPlayers);

        assertEquals(solutionB, solver.getCurrentBestPlayerSolution().getSolution());
    }

    @Test
    void performProbabilisticCrossoverPositive() {
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

        Player parentA = new Player(solutionParentA);
        Player parentB = new Player(solutionParentB);

        Player actualChild = solver.performProbabilisticCrossover(parentA, parentB);

        assertEquals(6, actualChild.getProposedSolution().length);
        assertEquals(6, actualChild.getProposedSolution()[0].length);
    }

    @Test
    void performProbabilisticCrossoverNegative() {
        char[][] solutionParentA = new char[][]{
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'},
                {'B', 'B', 'B', 'B', 'B', 'B'}
        };

        char[][] solutionParentB = new char[][]{
                {'W', 'B', 'W'},
                {'B', 'W', 'B'},
                {'W', 'B', 'W'}
        };

        Player parentA = new Player(solutionParentA);
        Player parentB = new Player(solutionParentB);

        Exception e = assertThrows(IllegalArgumentException.class, () -> solver.performProbabilisticCrossover(parentA, parentB));

        assertEquals("Parent dimensions do not match!", e.getMessage());
    }

    @Test
    void translateToIntArrayPositive() {
        char[] validB = new char[]{'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B'};
        char[] validW = new char[]{'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'};
        char[] validBW = new char[]{'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W', 'B', 'W'};

        int[] allOnes = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] allZeroes = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] oneZero = new int[]{1, 0, 1, 0, 1, 0, 1, 0, 1, 0};

        assertArrayEquals(allOnes, solver.translateToIntArray(validB));
        assertArrayEquals(allZeroes, solver.translateToIntArray(validW));
        assertArrayEquals(oneZero, solver.translateToIntArray(validBW));
    }

    @Test
    void translateToIntArrayNegative() {
        char[] invalid = new char[]{'B', 'W', 'S', 'P', 'A', 'M', 'W', 'B'};

        Exception e = assertThrows(IllegalArgumentException.class, () -> solver.translateToIntArray(invalid));

        assertEquals("Only char[] with values B and W allowed!", e.getMessage());
    }
}