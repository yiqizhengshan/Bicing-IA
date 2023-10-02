package IA.Bicing;

/**
 * Created by bejar on 17/01/17.
 */
public class BicingBoard {
    /* Class independent from AIMA classes
       - It has to implement the state of the problem and its operators
     *

    /* State data structure
        vector with the parity of the coins (we can assume 0 = heads, 1 = tails
     */

    private int [] board;
    private static int [] solution;

    /* Constructor */
    public BicingBoard(int []init, int[] goal) {

        board = new int[init.length];
        solution = new int[init.length];

        for (int i = 0; i< init.length; i++) {
            board[i] = init[i];
            solution[i] = goal[i];
        }

    }

    /* vvvvv TO COMPLETE vvvvv */
    public void flip_it(int i){
        // flip the coins i and i + 1
        board[i] = (board[i] + 1)%2;
        if (i == board.length - 1) board[0] = (board[0] + 1)%2;
        else board[i + 1] = (board[i + 1] + 1)%2;
    }

    /* Heuristic function */
    public double heuristic(){
        // compute the number of coins out of place respect to solution
        int count = 0;
        int tam = board.length;

        for (int i = 0; i < tam; ++i) {
            if (board[i] != solution[i]) ++count;
        }

        return count;
    }

    /* Goal test */
    public boolean is_goal(){
        // compute if board = solution
        if (heuristic() == 0) return true;
        else return false;
    }

    /* auxiliary functions */

    // Some functions will be needed for creating a copy of the state

    /* ^^^^^ TO COMPLETE ^^^^^ */
    public int[] getBoard() {
        return board;
    }

    public int[] getSolution() {
        return solution;
    }
}
