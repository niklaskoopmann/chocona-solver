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

        // second: find rectangles in matrix
        for (int y = 0; y < solvedField.solution.length; y++) {
            for (int x = 0; x < solvedField.solution[y].length; x++) {
                if(solvedField.solution[x][y] == 'B') {

                }
            }
        }

        return true;
    }
}
