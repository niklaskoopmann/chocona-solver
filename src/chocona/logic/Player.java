package chocona.logic;

import java.util.Arrays;
import java.util.Random;

public class Player {

    private final char[][] proposedSolution;
    public int fitness;
    private final int sizeX;
    private final int sizeY;

    public Player(int sizeX, int sizeY, boolean randomize) {

        this.proposedSolution = new char[sizeX][sizeY];
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        if (randomize) {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    this.proposedSolution[x][y] = Math.random() > 0.5 ? 'B' : 'W';
                }
            }
        }
    }

    public Player(char[][] proposedSolution) {
        this.proposedSolution = proposedSolution;
        this.sizeX = proposedSolution[0].length;
        this.sizeY = proposedSolution.length;
    }

    public void mutate(int numberOfMutations) {

        Random rand = new Random();
        for (int i = 0; i < numberOfMutations; i++) {
            int x = rand.nextInt(this.sizeX);
            int y = rand.nextInt(this.sizeY);
            if (this.proposedSolution[y][x] == 'B') this.proposedSolution[y][x] = 'W';
            else if (this.proposedSolution[y][x] == 'W') this.proposedSolution[y][x] = 'B';
        }
    }

    public char[][] getProposedSolution() {
        return proposedSolution;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void setCell(int x, int y, char value) {
        this.proposedSolution[y][x] = value;
    }

    public void setRow(int row, char[] values) {
        this.proposedSolution[row] = values;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Player\n");
        stringBuilder.append("  fitness=").append(fitness).append("\n");
        stringBuilder.append("  proposedSolution=\n");
        for (char[] chars : proposedSolution) {
            stringBuilder.append("    ").append(Arrays.toString(chars)).append("\n");
        }
        return stringBuilder.toString();
    }
}
