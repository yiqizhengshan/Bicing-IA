package IA.Bicing;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class BicingSuccesorFunction implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        State board = (State) state;

        int F = State.getF();
        int E = State.getE();

        board.printState();
        for (int vanId = 0; vanId < F; ++vanId) {
            for (int destId2 = 0; destId2 < E; ++destId2) {
                State copy = new State(board);
                copy.changeDestination2(vanId, destId2);
                double benefit = copy.getBenefit();
                double transportCost = copy.getTransportCost();
                retval.add(new Successor(new String("changeDestination2 " + vanId + " " + destId2 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
            }
            for (int destId1 = 0; destId1 < E; ++destId1) {
                State copy = new State(board);
                copy.changeDestination1(vanId, destId1);
                double benefit = copy.getBenefit();
                double transportCost = copy.getTransportCost();
                retval.add(new Successor(new String("changeDestination1 " + vanId + " " + destId1 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
            }
        }
    
        return (retval);
    }
}
