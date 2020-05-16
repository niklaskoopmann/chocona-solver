package logic;

import structure.Field;
import structure.Region;

import java.util.concurrent.atomic.AtomicInteger;

public class Solver {

    // todo implement method
    public void solvePuzzle(){
        throw new UnsupportedOperationException();
    }

    // todo implement method
    public int calculateScore() {
        throw new UnsupportedOperationException();
    }

    // todo split into separate methods
    public boolean checkSolution(Field solvedField) {

        // first: Check all the regions with defined target values.
        // Is the right number of cells blackened? If anything does not match, return false right away.
        for (Region r : solvedField.getRegions()) {
            if (r.targetNumber > 0) {
                AtomicInteger blackened = new AtomicInteger();
                r.cells.forEach(c -> {
                    if (solvedField.solution[c.getX()][c.getY()] == 'B') blackened.getAndIncrement();
                });
                if (blackened.get() != r.targetNumber) {
                    return false;
                }
            }
        }
        // for scoring: add some score value for getting defined regions right

        // second: find rectangles in matrix

        // to save row evaluation status (initialized with zeroes)
        int[] rowEvaluation = new int[solvedField.solution[0].length];

        // iterate over entire solution array
        for (int y = 0; y < solvedField.solution.length; y++) {

            // replace B and W in row with 1 and 0 respectively
            int[] rowInts = translateToIntArray(solvedField.solution[y]);

            // add current row to previous evaluation
            rowEvaluation = addIntArrayElementsIfNotZero(rowEvaluation, rowInts);

            if (!validateRectangleCountArray(rowEvaluation)) return false;
        }

        return true;
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
            if(rectangleCounter[i] != rectangleCounter[i - 1]) {
                if (rectangleCounter[i] != 0 || rectangleCounter[i - 1] != 0) return false;
            }
        }

        return true;
    }
}
