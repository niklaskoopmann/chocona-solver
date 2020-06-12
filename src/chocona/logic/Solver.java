package chocona.logic;

import chocona.Configuration;
import chocona.structure.Field;
import chocona.structure.Region;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solver {

    private final int initialPopulationSize;
    private final int numberOfGenerations;
    private final Field toSolve;
    private final HashMap<Integer, Integer> populationFitnessHistory;
    private double mutationProbability;
    private ArrayList<Player> population;
    private boolean solutionFound;
    private int currentGenerationIndex;

    public Solver(int initialPopulationSize, int numberOfGenerations, double mutationProbability, Field toSolve) {
        this.initialPopulationSize = initialPopulationSize;
        this.numberOfGenerations = numberOfGenerations;
        this.mutationProbability = mutationProbability;
        this.solutionFound = false;
        this.currentGenerationIndex = 0;
        this.population = new ArrayList<Player>();
        this.populationFitnessHistory = new HashMap<Integer, Integer>();
        this.toSolve = toSolve;
        initializePopulation();
    }

    // generic constructor sets default values
    public Solver(Field toSolve) {
        this.initialPopulationSize = Configuration.initialPopulationSize;
        this.numberOfGenerations = Configuration.numberOfGenerations;
        this.mutationProbability = Configuration.mutationProbability;
        this.population = new ArrayList<Player>();
        this.solutionFound = false;
        this.currentGenerationIndex = 0;
        this.populationFitnessHistory = new HashMap<Integer, Integer>();
        this.toSolve = toSolve;
        initializePopulation();
    }

    /**
     * Initialize the Solver's population with initialPopulationSize individuals with random genomes.
     */
    public void initializePopulation() {
        this.population = new ArrayList<Player>();
        for (int i = 0; i < this.initialPopulationSize; i++) {
            Player initialPlayer = new Player(this.toSolve.getWidth(), this.toSolve.getHeight(), true);

            // calculate initial fitness
            Field wrapper = new Field(this.toSolve.getWidth(), this.toSolve.getHeight(), toSolve.getRegions());
            wrapper.solution = initialPlayer.getProposedSolution();
            initialPlayer.setFitness(checkSolution(wrapper));
            this.population.add(initialPlayer);
        }
    }

    /**
     * Simulate exactly one generation of individuals and save the result to the Solver.population attribute. Based on
     * the index currentGeneration, stalling can be detected and additional measures like a higher mutation rate can be
     * taken.
     *
     * @param currentGeneration the current generation index
     * @param currentPopulation the current population of Players to select, reproduce and mutate
     */
    public void stepGeneration(int currentGeneration, ArrayList<Player> currentPopulation) {

        // check if correct solution is present
        currentPopulation.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        if (currentPopulation.get(0).fitness == Integer.MAX_VALUE) {
            this.toSolve.solution = currentPopulation.get(0).getProposedSolution();
            this.population = currentPopulation;
            this.solutionFound = true;

            System.out.println("solution found: " + currentPopulation.get(0).toString());

            return;
        }

        // perform selection based on fitness
        ArrayList<Player> matingPool = performSelection(currentPopulation);

        // randomize parents for next generation
        Collections.shuffle(matingPool);

        // breed children and populate next generation
        ArrayList<Player> nextGeneration = mate(matingPool);

        // add next generation's children to population
        currentPopulation.addAll(nextGeneration);

        // calculate fitness for each individual
        int populationFitness = 0;
        for (Player p : currentPopulation) {
            Field wrapper = new Field(this.toSolve.getWidth(), this.toSolve.getHeight(), toSolve.getRegions());
            wrapper.solution = p.getProposedSolution();
            p.fitness = checkSolution(wrapper);
            populationFitness += p.fitness;
        }

        // if there is no significant change in overall fitness, increase mutation rate
        if (currentGeneration > 10 && Math.abs(populationFitnessHistory.get(currentGeneration - 10) - populationFitness) <= currentPopulation.size() / 100) {
            this.mutationProbability *= 2;
            System.out.println("Increased mutation probability to " + this.mutationProbability);
        } else this.mutationProbability = Configuration.mutationProbability;

        // kill off weakest individuals until base population size is reached
        currentPopulation.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        currentPopulation.subList(this.initialPopulationSize, currentPopulation.size()).clear();

        // lastly save last generation fitness
        this.populationFitnessHistory.put(currentGeneration, populationFitness);

        System.out.printf("Generation %d; population fitness: %d; fittest: %d; least fit: %d; average: %s%n",
                currentGeneration,
                populationFitness,
                currentPopulation.get(0).fitness,
                currentPopulation.get(currentPopulation.size() - 1).fitness,
                populationFitness / nextGeneration.size() * 1.0);

        this.population = currentPopulation;
    }

    /**
     * Selects 1/2 of the input population based directly on their fitness (roulette wheel).
     *
     * @param currentPopulation the current full population to select from
     * @return the selected individuals
     */
    public ArrayList<Player> performSelection(ArrayList<Player> currentPopulation) {

        // calculate population fitness
        int populationFitness = currentPopulation
                .stream()
                .mapToInt(Player::getFitness)
                .sum();

        ArrayList<Player> matingPool = new ArrayList<Player>();
        Random rand = new Random();

        // fill mating pool with individuals (half of total population based on their fitness)
        while (matingPool.size() < this.initialPopulationSize / 2.0) {
            Player player = currentPopulation.get(rand.nextInt(currentPopulation.size()));
            double playerSelectionProbability = (player.fitness * 1.0) / (populationFitness * 1.0);
            if (playerSelectionProbability >= Math.random()) {
                matingPool.add(player);
            }
        }
        return matingPool;
    }

    /**
     * Combine all the parent Players in matingPool to produce the next generation of individuals based on the
     * probabilistic crossover algorithm. Two players produce up to two children with a predefined probability.
     *
     * @param matingPool the individuals selected for mating
     * @return the next generation's population
     */
    public ArrayList<Player> mate(ArrayList<Player> matingPool) {

        // randomize mating pool
        Collections.shuffle(matingPool);

        // setup next generation
        ArrayList<Player> nextGeneration = new ArrayList<Player>();

        // iterate over all selected Players
        for (int k = 0; k < matingPool.size(); k += 2) {
            Player parentA = matingPool.get(k);
            Player parentB = matingPool.get(k + 1);

            // produce up to two children based on procreation probability
            ArrayList<Player> children = new ArrayList<Player>();
            for (int i = 0; i < 2; i++) {
                if (Math.random() < Configuration.procreationProbability) {
                    Player child = performProbabilisticCrossover(parentA, parentB);
                    if (Math.random() < this.mutationProbability)
                        child.mutate(1 + (int) (child.getSizeX() * this.mutationProbability));
                    children.add(child);
                }
            }
            nextGeneration.addAll(children);
        }
        return nextGeneration;
    }

    /**
     * Simulate a predefined number of generations (attribute numberOfGenerations of the Solver)
     *
     * @return the proposed solution of the fittest individual after numberOfGenerations generations
     */
    public Field solvePuzzleGenetic() {

        // simulate generations until solution is found or limit is reached
        this.currentGenerationIndex = 0;
        initializePopulation();
        while (!solutionFound && this.currentGenerationIndex < this.numberOfGenerations) {
            stepGeneration(this.currentGenerationIndex, this.population);
            this.currentGenerationIndex++;
        }

        this.population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        this.toSolve.solution = this.population.get(0).getProposedSolution();
        return this.toSolve;
    }

    /**
     * Get the proposed solution of the current generation's fittest player.
     *
     * @return a Field containing the population's current best solution
     */
    public Field getCurrentBestPlayerSolution() {
        this.population.sort(Comparator.comparingInt((Player p) -> p.fitness).reversed());
        return new Field(this.population.get(0).getProposedSolution());
    }

    /**
     * Checks and scores a proposed solution in a Field object to calculate individual fitness. The score is based on
     * the number of rule conformities/violations - the correct solution is awarded 1000 points.
     *
     * @param solvedField the proposed solution to check
     * @return an integer score
     */
    public int checkSolution(Field solvedField) {

        // use final int array for access from within lambda functions
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

                // penalty per wrong cell or reward based on cells in region
                int differenceBlackened = Math.abs(blackened.get() - r.targetNumber);
                if (differenceBlackened != 0) {
                    allRegionsWithTargetCorrect = false;
                    score[0] -= differenceBlackened;
                } else {
                    // award points for each correct region with target number based on the field chocona.structure
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

            // add current row to previous evaluation
            rowEvaluation = addIntArrayElementsIfNotZero(rowEvaluation, rowInts);

            // calculate the number of errors in the current row
            int errorsInRow = validateRectangleCountArray(rowEvaluation);
            if (errorsInRow > 0) {
                correctSolution = false;
                score[0] -= errorsInRow;
            } else score[0] += solvedField.getWidth(); // award some points for each row without error
        }

        // highest possible score for correct solution
        if (correctSolution && allRegionsWithTargetCorrect) return Integer.MAX_VALUE;

        // otherwise return current score
        return score[0];
    }

    /**
     * Converts a char array of 'B' and 'W' to an integer array of 1 and 0 respectively.
     *
     * @param blackWhiteCharArray the char array to convert, consisting of only 'B' and 'W'
     * @return an integer array with 0s or 1s
     * @throws IllegalArgumentException if the input contains anything other than 'B' and 'W'
     */
    public int[] translateToIntArray(char[] blackWhiteCharArray) {
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

        int penalty = 0;

        for (int i = 1; i < rectangleCounter.length; i++) {
            if (rectangleCounter[i] != rectangleCounter[i - 1]) {
                if (rectangleCounter[i] != 0 && rectangleCounter[i - 1] != 0) {
                    penalty++;
                }
            }
        }

        return penalty;
    }

    /**
     * Perform a probabilistic crossover for two individuals to produce a child individual. Decides whether to include
     * the father's or mother's gene with a probability of 50 % each.
     *
     * @param mum the mother Player
     * @param dad the father Player
     * @return a child Player based on the genomes of its parents
     */
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

    /**
     * Perform a one point crossover for two individuals to produce a child individual. Uses the top half of genes from
     * the father's genome and the bottom half from the mother's.
     *
     * @param mum the mother Player
     * @param dad the father Player
     * @return a child Player based on the genomes of its parents
     */
    public Player performHorizontalOnePointCrossover(Player mum, Player dad) {

        Player child;

        if (mum.getProposedSolution().length == dad.getProposedSolution().length &&
                mum.getProposedSolution()[0].length == dad.getProposedSolution()[0].length) {

            child = new Player(mum.getSizeX(), mum.getSizeY(), false);

            // cross over at half the height
            int crossoverPoint = mum.getProposedSolution().length / 2;

            for (int row = 0; row < crossoverPoint; row++) child.setRow(row, dad.getProposedSolution()[row]);
            for (int row = crossoverPoint; row < mum.getSizeY(); row++)
                child.setRow(row, mum.getProposedSolution()[row]);

            return child;

        } else throw new IllegalArgumentException("Parent dimensions do not match!");

    }

    public ArrayList<Player> getPopulation() {
        return population;
    }

    public void setPopulation(ArrayList<Player> population) {
        this.population = population;
    }

    public int getInitialPopulationSize() {
        return initialPopulationSize;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }

    public int getCurrentGenerationIndex() {
        return currentGenerationIndex;
    }
}