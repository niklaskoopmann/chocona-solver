package logic;

import structure.Field;
import structure.Region;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Solver {

    private final int initialPopulationSize;
    private final int numberOfGenerations;

    public Solver(int initialPopulationSize, int numberOfGenerations) {
        this.initialPopulationSize = initialPopulationSize;
        this.numberOfGenerations = numberOfGenerations;
    }

    // generic constructor sets default values
    public Solver() {
        this.initialPopulationSize = 200;
        this.numberOfGenerations = 25;
    }

    public Field solvePuzzleGenetic(Field toSolve){

        // initialize population
        ArrayList<Player> population = new ArrayList<Player>();
        for (int i = 0; i < initialPopulationSize; i++) population.add(new Player(toSolve.getWidth(), toSolve.getHeight()));

        // simulate generations
        int previousGenerationPopulationFitness = Integer.MIN_VALUE;
        for (int i = 0; i < numberOfGenerations; i++) {

            // calculate fitness for each individual
            int j = 1;
            int populationFitness = 0;
            for (Player p : population) {

                Field wrapper = new Field(toSolve.getWidth(), toSolve.getHeight(), toSolve.getRegions());
                wrapper.solution = p.getProposedSolution();
                p.fitness = checkSolution(wrapper);
                populationFitness += p.fitness;

                System.out.printf("Gen. %d, Player %d: Fitness %d", i, j, p.fitness);

                j++;
            }

            // sort population list based on fitness
            population.sort(Comparator.comparingInt(Player::getFitness).reversed());

            // if there is no significant change in overall fitness, get best solution and break
            if (Math.abs(previousGenerationPopulationFitness - populationFitness) <= 1) {
                toSolve.solution = population.get(0).getProposedSolution();
                return toSolve;
            }



        }

        throw new UnsupportedOperationException();
    }

    // todo document algorithm
    public int checkSolution(Field solvedField) {

        // use final int array for access from within lambda function
        final int[] score = {0};

        // first: Check all the regions with defined target values.
        // Is the right number of cells blackened? If anything does not match, return false right away.
        for (Region r : solvedField.getRegions()) {
            if (r.targetNumber > 0) {
                AtomicInteger blackened = new AtomicInteger();
                r.cells.forEach(c -> {
                    if (solvedField.solution[c.getX()][c.getY()] == 'B') {
                        blackened.getAndIncrement();
                        // award points for correct field
                        score[0]++;
                    }
                });
                if (blackened.get() != r.targetNumber) {
                    return score[0];
                } else {
                    // award points for each correct region
                    score[0] += 2;
                }
            }
        }
        // award points for getting all predefined regions right
        score[0] += 10;

        // second: find rectangles in matrix

        // to save row evaluation status (initialized with zeroes)
        int[] rowEvaluation = new int[solvedField.solution[0].length];

        // iterate over entire solution array
        for (int y = 0; y < solvedField.solution.length; y++) {

            // replace B and W in row with 1 and 0 respectively
            int[] rowInts = translateToIntArray(solvedField.solution[y]);

            // add current row to previous evaluation
            rowEvaluation = addIntArrayElementsIfNotZero(rowEvaluation, rowInts);

            if (!validateRectangleCountArray(rowEvaluation)) return score[0];
            else score[0]+=3; // award some points for each row without error
        }

        // award lots of points for correct solution
        score[0] += 100;
        return score[0];
    }

    // todo Might change the types for solution array to int to skip this step
    private int[] translateToIntArray(char[] blackWhiteCharArray) {
        int[] outIntArray = new int[blackWhiteCharArray.length];
        for (int i = 0; i < blackWhiteCharArray.length; i++) {
            if (blackWhiteCharArray[i] == 'B') outIntArray[i] = 1;
            else if (blackWhiteCharArray[i] == 'W') outIntArray[i] = 0;
            else throw new IllegalArgumentException("Only char[] with values B and W allowed!");
        }
        return outIntArray;
    }

    /**
     * Adds two int arrays element by element. Zeroes in compare lead to zeroes in result!
     *
     * @param base The base array which is being added to.
     * @param compare The array to add. Zeroes in this array overwrite the addition.
     * @return An array representing a rectangle counter - hence the reset on zeroes.
     */
    private int[] addIntArrayElementsIfNotZero(int[] base, int[] compare) {
        int length = Math.min(base.length, compare.length);
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            if (compare[i] == 0) result[i] = 0;
            else result[i] = base[i] + compare[i];
        }

        return result;
    }

    /**
     * Validation method for rectangles in Chocona solutions. If there are two horizontally adjacent rectangles of
     * different height, the solution is invalid and the method returns false for the current rectangleCounter.
     *
     * @param rectangleCounter A rectangle counter array of type int with length > 1.
     * @return True if rectangleCounter is valid, false otherwise.
     */
    private boolean validateRectangleCountArray(int[] rectangleCounter) {

        for (int i = 1; i < rectangleCounter.length; i++) {
            if (rectangleCounter[i] != rectangleCounter[i - 1]) {
                if (rectangleCounter[i] != 0 || rectangleCounter[i - 1] != 0) return false;
            }
        }

        return true;
    }
}
