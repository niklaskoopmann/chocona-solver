package logic;

import structure.Field;
import structure.Region;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solver {

    private final int initialPopulationSize;
    private final int numberOfGenerations;
    private final Field toSolve;
    private final HashMap<Integer, Integer> populationFitnessHistory;
    private double mutationProbability;
    private ArrayList<Player> population;

    public Solver(int initialPopulationSize, int numberOfGenerations, double mutationProbability, Field toSolve) {
        this.initialPopulationSize = initialPopulationSize;
        this.numberOfGenerations = numberOfGenerations;
        this.mutationProbability = mutationProbability;
        this.population = new ArrayList<Player>();
        this.populationFitnessHistory = new HashMap<Integer, Integer>();
        this.toSolve = toSolve;
        initializePopulation();
    }

    // generic constructor sets default values
    public Solver(Field toSolve) {
        this.initialPopulationSize = 1200;
        this.numberOfGenerations = 600;
        this.mutationProbability = 0.01;
        this.population = new ArrayList<Player>();
        this.populationFitnessHistory = new HashMap<Integer, Integer>();
        this.toSolve = toSolve;
        initializePopulation();
    }

    public void initializePopulation() {
        this.population = new ArrayList<Player>();
        for (int i = 0; i < this.initialPopulationSize; i++)
            this.population.add(new Player(this.toSolve.getWidth(), this.toSolve.getHeight(), true));
    }

    public void stepGeneration(int currentGeneration, ArrayList<Player> currentPopulation) {

        // calculate fitness for each individual
        int j = 1;
        int populationFitness = 0;
        for (Player p : currentPopulation) {

            Field wrapper = new Field(this.toSolve.getWidth(), this.toSolve.getHeight(), toSolve.getRegions());
            wrapper.solution = p.getProposedSolution();
            p.fitness = checkSolution(wrapper);
            populationFitness += p.fitness;

            //System.out.printf("Gen. %d, Player %d: Fitness %d, Selection Probability: %f\n", currentGeneration, j, p.fitness, p.fitness / (populationFitness * 1.0));

            j++;
        }

        // if there is no significant change in overall fitness, increase mutation rate
        if (currentGeneration > 10 && Math.abs(populationFitnessHistory.get(currentGeneration - 10) - populationFitness) <= currentPopulation.size() / 100) {
            this.mutationProbability += 0.01;
            System.out.println("Increased mutation probability to " + this.mutationProbability);
        } else this.mutationProbability = 0.01;

        // check if correct solution is present
        currentPopulation.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        if (currentPopulation.get(0).fitness > 1000) {
            this.toSolve.solution = currentPopulation.get(0).getProposedSolution();
            this.population = currentPopulation;
            return;
        }

        // perform selection based on fitness
        ArrayList<Player> matingPool = new ArrayList<Player>();
        /*currentPopulation.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        for (int k = 0; k < initialPopulationSize / 1.5; k++) {
            //System.out.println("Individual fitness: " + population.get(k).fitness);
            matingPool.add(currentPopulation.get(k));
        }*/
        Random rand = new Random();
        while (matingPool.size() < initialPopulationSize / 1.5) {
            Player player = currentPopulation.get(rand.nextInt(currentPopulation.size()));
            double playerSelectionProbability = (player.fitness * 1.0) / (populationFitness * 1.0);
            //System.out.println("Player fitness: " + player.fitness + "; selection probability: " + playerSelectionProbability);
            if (playerSelectionProbability >= Math.random()) {
                matingPool.add(player);
                currentPopulation.remove(player);
            }
        }

        // randomize parents for next generation
        Collections.shuffle(matingPool);

        // setup next generation
        ArrayList<Player> nextGeneration = new ArrayList<Player>();

        // breed children and repopulate
        for (int k = 0; k < matingPool.size(); k += 2) {
            Player parentA = matingPool.get(k);
            Player parentB = matingPool.get(k + 1);
            Player child = performProbabilisticCrossover(parentA, parentB);
            if (Math.random() < this.mutationProbability)
                child.mutate(1 + (int) (child.getSizeX() * this.mutationProbability));
            //if (Math.random() < this.mutationProbability) child.mutate((int)(child.getSizeX() * child.getSizeY() * this.mutationProbability));
            //child.mutate((int)(child.getSizeX() * child.getSizeY() * this.mutationProbability));
            nextGeneration.addAll(Arrays.asList(parentA, parentB, child));
        }

        // lastly save last generation fitness
        this.populationFitnessHistory.put(currentGeneration, populationFitness);

        // debug
        System.out.println("Pop fitness: " + populationFitness + "; fittest: " + currentPopulation.get(0).fitness + "; least fit: " + currentPopulation.get(currentPopulation.size() - 1).fitness + "; average: " + (populationFitness / nextGeneration.size() * 1.0));
        //System.out.println(populationFitness);
        if (currentPopulation.get(0).fitness > 1000) System.out.println(currentPopulation.get(0).toString());

        this.population = nextGeneration;
    }

    public Field solvePuzzleGenetic() {

        // simulate generations
        for (int i = 0; i < numberOfGenerations; i++) {
            stepGeneration(i, this.population);
        }

        this.population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        this.toSolve.solution = this.population.get(0).getProposedSolution();
        return this.toSolve;
    }

    public Field getCurrentBestPlayerSolution() {
        this.population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        return new Field(this.population.get(0).getProposedSolution());
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
                    }
                });
                // debug
                //System.out.println("target evaluation; black cells: " + blackened.get() + "; target: " + r.targetNumber + "; region: " + r.toString());

                // penalty per wrong cell
                int differenceBlackened = Math.abs(blackened.get() - r.targetNumber);
                if (differenceBlackened != 0) {
                    allRegionsWithTargetCorrect = false;
                    score[0]--;
                } else {
                    // award points for each correct region with target number based on the field structure
                    score[0] += r.totalCells;
                }
            }
        }
        // award points for getting all predefined regions right
        if (allRegionsWithTargetCorrect) score[0] += (solvedField.getWidth() + solvedField.getHeight());

        // second: find rectangles in matrix

        // to save row evaluation status (initialized with zeroes)
        int[] rowEvaluation = new int[solvedField.getWidth()];

        // iterate over entire solution array
        for (int y = 0; y < solvedField.getHeight(); y++) {

            // replace B and W in row with 1 and 0 respectively
            int[] rowInts = translateToIntArray(solvedField.solution[y]);

            // debug
            //System.out.println("Current row: " + Arrays.toString(rowInts));

            // add current row to previous evaluation
            rowEvaluation = addIntArrayElementsIfNotZero(rowEvaluation, rowInts);

            // debug
            //System.out.println("Accumulated rectangles: " + Arrays.toString(rowEvaluation));

            // calculate the number of errors in the current row
            int errorsInRow = validateRectangleCountArray(rowEvaluation);
            if (errorsInRow > 0) {
                correctSolution = false;
                score[0]--;
            } else score[0] += solvedField.getWidth(); // award some points for each row without error
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
                if (compare[i] == 0 && !(compare[i - 1] != 0 && base[i - 1] != 0 && base[i] != 0) && !(compare[i + 1] != 0 && base[i + 1] != 0 && base[i] != 0))
                    result[i] = 0;
                else result[i] = base[i] + compare[i];
            } else if (i > 0) {
                if (compare[i] == 0 && !(compare[i - 1] != 0 && base[i - 1] != 0 && base[i] != 0)) result[i] = 0;
                else result[i] = base[i] + compare[i];
            } else if (i < length - 1) {
                if (compare[i] == 0 && !(compare[i + 1] != 0 && base[i + 1] != 0 && base[i] != 0)) result[i] = 0;
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
     * @return the number of errors in the counter array, ergo 0 if rectangleCounter is valid.
     */
    public int validateRectangleCountArray(int[] rectangleCounter) {

        // debug
        //System.out.print("Checking rectangle counter: " + Arrays.toString(rectangleCounter) + "; result: ");

        int penalty = 0;

        for (int i = 1; i < rectangleCounter.length; i++) {
            if (rectangleCounter[i] != rectangleCounter[i - 1]) {
                if (rectangleCounter[i] != 0 && rectangleCounter[i - 1] != 0) {
                    //System.out.println("false");
                    penalty++;
                }
            }
        }
        //System.out.println("true");
        return penalty;
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
            for (int row = crossoverPoint; row < mum.getSizeY(); row++)
                child.setRow(row, mum.getProposedSolution()[row]);

            return child;

        } else throw new IllegalArgumentException("Parent dimensions do not match!");

    }

    public ArrayList<Player> getPopulation() {
        return population;
    }

    public int getInitialPopulationSize() {
        return initialPopulationSize;
    }
}