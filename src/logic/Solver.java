package logic;

import structure.Field;
import structure.Region;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solver {

    private final int initialPopulationSize;
    private final int numberOfGenerations;
    private double mutationProbability;

    public Solver(int initialPopulationSize, int numberOfGenerations, double mutationProbability) {
        this.initialPopulationSize = initialPopulationSize;
        this.numberOfGenerations = numberOfGenerations;
        this.mutationProbability = mutationProbability;
    }

    // generic constructor sets default values
    public Solver() {
        this.initialPopulationSize = 1200;
        this.numberOfGenerations = 120;
        this.mutationProbability = 0.01;
    }

    public Field solvePuzzleGenetic(Field toSolve) {

        // initialize population
        ArrayList<Player> population = new ArrayList<Player>();
        for (int i = 0; i < initialPopulationSize; i++)
            population.add(new Player(toSolve.getWidth(), toSolve.getHeight(), true));

        // simulate generations
        HashMap<Integer, Integer> populationFitnessHistory = new HashMap<Integer, Integer>();
        for (int i = 0; i < numberOfGenerations; i++) {

            // calculate fitness for each individual
            int j = 1;
            int populationFitness = 0;
            for (Player p : population) {

                Field wrapper = new Field(toSolve.getWidth(), toSolve.getHeight(), toSolve.getRegions());
                wrapper.solution = p.getProposedSolution();
                p.fitness = checkSolution(wrapper);
                populationFitness += p.fitness;

                //System.out.printf("Gen. %d, Player %d: Fitness %d, Selection Probability: %f\n", i, j, p.fitness, p.fitness / (populationFitness * 1.0));

                j++;
            }

            //System.out.println("Generation " + i + " population fitness: " + populationFitness);

            // if there is no significant change in overall fitness, increase mutation rate
            if (i > 10 && Math.abs(populationFitnessHistory.get(i - 10) - populationFitness) <= population.size() / 100) {
                this.mutationProbability += 0.01;
                System.out.println("Increased mutation probability to " + this.mutationProbability);
            }

            // check if correct solution is present
            population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
            if (population.get(0).fitness > 1000) {
                toSolve.solution = population.get(0).getProposedSolution();
                return toSolve;
            }

            // perform selection based on fitness
            ArrayList<Player> matingPool = new ArrayList<Player>();
            population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
            for (int k = 0; k < initialPopulationSize / 1.5; k++) {
                //System.out.println("Individual fitness: " + population.get(k).fitness);
                matingPool.add(population.get(k));
            }
            /*Random rand = new Random();
            while (matingPool.size() < initialPopulationSize / 1.5) {
                Player player = population.get(rand.nextInt(population.size()));
                double playerSelectionProbability = (player.fitness * 1.0) / (populationFitness * 1.0);
                if (playerSelectionProbability >= Math.random()) {
                    matingPool.add(player);
                    population.remove(player);
                }
            }*/

            // randomize parents for next generation
            Collections.shuffle(matingPool);

            // reset population
            population = new ArrayList<Player>();

            // breed children and repopulate
            for (int k = 0; k < matingPool.size(); k += 2) {
                Player parentA = matingPool.get(k);
                Player parentB = matingPool.get(k + 1);
                Player child = performProbabilisticCrossover(parentA, parentB);
                if (Math.random() < this.mutationProbability)
                    child.mutate(1 + (int) (child.getSizeX() * this.mutationProbability));
                //if (Math.random() < this.mutationProbability) child.mutate((int)(child.getSizeX() * child.getSizeY() * this.mutationProbability));
                //child.mutate((int)(child.getSizeX() * child.getSizeY() * this.mutationProbability));
                population.addAll(Arrays.asList(parentA, parentB, child));
            }

            // lastly save last generation fitness
            populationFitnessHistory.put(i, populationFitness);

            // debug
            System.out.println(population.get(0).fitness);
            //System.out.println(populationFitness);
            if (population.get(0).fitness > 1000) System.out.println(population.get(0).toString());
        }

        population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        toSolve.solution = population.get(0).getProposedSolution();
        return toSolve;
    }

    // todo document algorithm
    public int checkSolution(Field solvedField) {

        // debug
        //System.out.println("Checking solution...");

        // use final int array for access from within lambda function
        final int[] score = {0};
        boolean allRegionsWithTargetCorrect = true;
        boolean correctSolution = true;

        // first: Check all the regions with defined target values.
        // Is the right number of cells blackened? If anything does not match, return false right away.
        for (Region r : solvedField.getRegions()) {
            if (r.targetNumber > 0) {
                AtomicInteger blackened = new AtomicInteger();
                r.cells.forEach(c -> {
                    if (solvedField.solution[c.getY()][c.getX()] == 'B') {
                        blackened.getAndIncrement();
                        // award points for correct field
                        score[0]++;
                    }
                });
                // debug
                //System.out.println("target evaluation; black cells: " + blackened.get() + "; target: " + r.targetNumber + "; region: " + r.toString());
                int differenceBlackened = Math.abs(blackened.get() - r.targetNumber);
                if (differenceBlackened != 0) {
                    allRegionsWithTargetCorrect = false;
                    score[0] -= differenceBlackened;
                } else {
                    // award points for each correct region
                    score[0] += 5;
                }
            }
        }
        // award points for getting all predefined regions right
        if (allRegionsWithTargetCorrect) score[0] += 10;

        // second: find rectangles in matrix

        // to save row evaluation status (initialized with zeroes)
        int[] rowEvaluation = new int[solvedField.solution[0].length];

        // iterate over entire solution array
        for (int y = 0; y < solvedField.solution.length; y++) {

            // replace B and W in row with 1 and 0 respectively
            int[] rowInts = translateToIntArray(solvedField.solution[y]);

            // debug
            //System.out.println("Current row: " + Arrays.toString(rowInts));

            // add current row to previous evaluation
            rowEvaluation = addIntArrayElementsIfNotZero(rowEvaluation, rowInts);

            // debug
            //System.out.println("Accumulated rectangles: " + Arrays.toString(rowEvaluation));

            if (!validateRectangleCountArray(rowEvaluation)) {
                correctSolution = false;
                score[0]--;
            }
            else score[0] += 2; // award some points for each row without error
        }

        // award lots of points for correct solution
        if (correctSolution && allRegionsWithTargetCorrect) score[0] += 1000;
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
     * @param base    The base array which is being added to.
     * @param compare The array to add. Zeroes in this array overwrite the addition.
     * @return An array representing a rectangle counter - hence the reset to zeroes.
     */
    private int[] addIntArrayElementsIfNotZero(int[] base, int[] compare) {
        int length = Math.min(base.length, compare.length);
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            if (i > 0 && i < length - 1) {
                if (compare[i] == 0 && !(compare[i-1] != 0 && base[i-1] != 0 && base[i] != 0) && !(compare[i+1] != 0 && base[i+1] != 0 && base[i] != 0)) result[i] = 0;
                else result[i] = base[i] + compare[i];
            } else if (i > 0) {
                if (compare[i] == 0 && !(compare[i-1] != 0 && base[i-1] != 0 && base[i] != 0)) result[i] = 0;
                else result[i] = base[i] + compare[i];
            } else if (i < length - 1) {
                if (compare[i] == 0 && !(compare[i+1] != 0 && base[i+1] != 0 && base[i] != 0)) result[i] = 0;
                else result[i] = base[i] + compare[i];
            } else {
                if (compare[i] == 0) result[i] = 0;
                else result[i] = base[i] + compare[i];
            }
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
    public boolean validateRectangleCountArray(int[] rectangleCounter) {

        // debug
        //System.out.print("Checking rectangle counter: " + Arrays.toString(rectangleCounter) + "; result: ");

        for (int i = 1; i < rectangleCounter.length; i++) {
            if (rectangleCounter[i] != rectangleCounter[i - 1]) {
                if (rectangleCounter[i] != 0 && rectangleCounter[i - 1] != 0) {
                    //System.out.println("false");
                    return false;
                }
            }
        }
        //System.out.println("true");
        return true;
    }

    public Player performProbabilisticCrossover(Player mum, Player dad) {

        Player child;

        if (mum.getProposedSolution().length == dad.getProposedSolution().length &&
                mum.getProposedSolution()[0].length == dad.getProposedSolution()[0].length) {
            child = new Player(mum.getProposedSolution()[0].length, mum.getProposedSolution().length, false);

            for (int row = 0; row < child.getProposedSolution().length; row++) {
                for (int column = 0; column < child.getProposedSolution()[0].length; column++) {
                    if (Math.random() > 0.5) child.setCell(row, column, dad.getProposedSolution()[row][column]);
                    else child.setCell(row, column, mum.getProposedSolution()[row][column]);
                }
            }

            return child;

        } else throw new IllegalArgumentException("Parent dimensions do not match!");
    }

    public Player performHorizontalOnePointCrossover(Player mum, Player dad) {

        Player child;

        if (mum.getProposedSolution().length == dad.getProposedSolution().length &&
                mum.getProposedSolution()[0].length == dad.getProposedSolution()[0].length) {

            child = new Player(mum.getSizeX(), mum.getSizeY(), false);

            // pick random crossover point within 2 rows from center
            //Random rand = new Random();
            int crossoverPoint = /*rand.nextInt(5) - 2 + */mum.getProposedSolution().length / 2;

            for (int row = 0; row < crossoverPoint; row++) child.setRow(row, dad.getProposedSolution()[row]);
            for (int row = crossoverPoint; row < mum.getSizeY(); row++) child.setRow(row, mum.getProposedSolution()[row]);

            return child;

        } else throw new IllegalArgumentException("Parent dimensions do not match!");

    }
}