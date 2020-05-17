package logic;

public class Player {

    private char[][] proposedSolution;
    public int fitness;

    public char[][] getProposedSolution() {
        return proposedSolution;
    }

    public Player(int sizeX, int sizeY) {

        this.proposedSolution = new char[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                this.proposedSolution[x][y] = Math.random() > 0.5 ? 'B' : 'W';
            }
        }
    }

    public int getFitness() {
        return fitness;
    }
}
