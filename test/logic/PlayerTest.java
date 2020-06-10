package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player playerRandom;
    Player playerEmpty;

    @BeforeEach
    void setUp() {
        playerRandom = new Player(10, 10, true);
        playerEmpty = new Player(10, 10, false);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void mutate() {
        //char[][] beforeMutation = Arrays.copyOf(playerRandom.getProposedSolution());
        String beforeMutation = playerRandom.toString();
        playerRandom.mutate(3);
        assertNotEquals(beforeMutation, playerRandom.toString());
    }

    @Test
    void mutateNothing() {
        String beforeMutation = playerRandom.toString();
        playerRandom.mutate(0);
        assertEquals(beforeMutation, playerRandom.toString());
    }

    @Test
    void getProposedSolution() {
    }

    @Test
    void setGetFitness() {
        int fitness = 1337;
        playerEmpty.setFitness(fitness);
        assertEquals(fitness, playerEmpty.getFitness());
    }

    @Test
    void setCell() {
    }

    @Test
    void setRow() {
        char[] newRow = new char[]{'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'};
        int position = 3;
        playerRandom.setRow(position, newRow);
        assertArrayEquals(newRow, playerRandom.getProposedSolution()[position]);
    }

    @Test
    void getSizeX() {
    }

    @Test
    void getSizeY() {
        assertEquals(10, playerRandom.getSizeY());
    }

    @Test
    void testToString() {
        playerRandom.setFitness(1337);

        assertTrue(playerRandom.toString().contains("Player"));
        assertTrue(playerRandom.toString().contains("fitness=1337"));
        assertTrue(playerRandom.toString().contains("proposedSolution="));
    }
}