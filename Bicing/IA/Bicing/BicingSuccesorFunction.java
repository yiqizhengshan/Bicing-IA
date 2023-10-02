package IA.Bicing;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bejar on 17/01/17
 */
public class BicingSuccesorFunction implements SuccessorFunction{

    public List getSuccessors(Object state){
        ArrayList retval = new ArrayList();
        BicingBoard board = (BicingBoard) state;

        // Some code here
        // (flip all the consecutive pairs of coins and generate new states
        // Add the states to retval as Succesor("flip i j", new_state)
        // new_state has to be a copy of state

        for (int i = 0; i < board.getBoard().length; ++i) {
            BicingBoard new_board = new BicingBoard(board.getBoard(), board.getSolution());
            new_board.flip_it(i);
            retval.add(new Successor(new String("flip i j"), new_board));
        }

        return (retval);

    }

}
