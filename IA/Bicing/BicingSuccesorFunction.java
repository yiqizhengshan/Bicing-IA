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
            //swapOrigin
            for (int originId = 0; originId < F; ++originId) {
                State copy = new State(board);
                copy.swapOrigin(vanId, originId);
                double benefit = copy.getBenefit();
                double transportCost = copy.getTransportCost();
                retval.add(new Successor(new String("swapOrigin " + vanId + " " + originId + " benefit: " + benefit + " transportCost: " + transportCost), copy));
            }

            //changeDestination2
            for (int destId2 = 0; destId2 < E; ++destId2) {
                State copy = new State(board);
                copy.changeDestination2(vanId, destId2);
                double benefit = copy.getBenefit();
                double transportCost = copy.getTransportCost();
                retval.add(new Successor(new String("changeDestination2 " + vanId + " " + destId2 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
            }
            //changeDestination1
            for (int destId1 = 0; destId1 < E; ++destId1) {
                State copy = new State(board);
                copy.changeDestination1(vanId, destId1);
                double benefit = copy.getBenefit();
                double transportCost = copy.getTransportCost();
                retval.add(new Successor(new String("changeDestination1 " + vanId + " " + destId1 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
            }

            //changeOrigin
            for (int originId = 0; originId < E; ++originId) {
                State copy = new State(board);
                copy.changeOrigin(vanId, originId);
                double benefit = copy.getBenefit();
                double transportCost = copy.getTransportCost();
                retval.add(new Successor(new String("changeOrigin " + vanId + " " + originId + " benefit: " + benefit + " transportCost: " + transportCost), copy));
            }

            //changeOrigin with swapOrigin
            for (int originId = 0; originId < E; ++originId) {
                for (int originId2 = 0; originId2 < F; ++originId2) {
                    State copy = new State(board);
                    copy.changeOrigin(vanId, originId);
                    copy.swapOrigin(vanId, originId2);
                    double benefit = copy.getBenefit();
                    double transportCost = copy.getTransportCost();
                    retval.add(new Successor(new String("changeOrigin with swapOrigin " + vanId + " " + originId + " " + originId2 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
                }
            }

            //changeOrigin with swapOrigin with changeDestination1
            for (int originId = 0; originId < E; ++originId) {
                for (int originId2 = 0; originId2 < F; ++originId2) {
                    for (int destId1 = 0; destId1 < E; ++destId1) {
                        State copy = new State(board);
                        copy.changeOrigin(vanId, originId);
                        copy.swapOrigin(vanId, originId2);
                        copy.changeDestination1(vanId, destId1);
                        double benefit = copy.getBenefit();
                        double transportCost = copy.getTransportCost();
                        retval.add(new Successor(new String("changeOrigin with swapOrigin with changeDestination1 " + vanId + " " + originId + " " + originId2 + " " + destId1 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
                    }
                }
            }

            //changeOrigin with swapOrigin with changeDestination2
            for (int originId = 0; originId < E; ++originId) {
                for (int originId2 = 0; originId2 < F; ++originId2) {
                    for (int destId2 = 0; destId2 < E; ++destId2) {
                        State copy = new State(board);
                        copy.changeOrigin(vanId, originId);
                        copy.swapOrigin(vanId, originId2);
                        copy.changeDestination2(vanId, destId2);
                        double benefit = copy.getBenefit();
                        double transportCost = copy.getTransportCost();
                        retval.add(new Successor(new String("changeOrigin with swapOrigin with changeDestination2 " + vanId + " " + originId + " " + originId2 + " " + destId2 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
                    }
                }
            }

            //changeOrigin with swapOrigin with changeDestination1 with changeDestination2
            for (int originId = 0; originId < E; ++originId) {
                for (int originId2 = 0; originId2 < F; ++originId2) {
                    for (int destId1 = 0; destId1 < E; ++destId1) {
                        for (int destId2 = 0; destId2 < E; ++destId2) {
                            State copy = new State(board);
                            copy.changeOrigin(vanId, originId);
                            copy.swapOrigin(vanId, originId2);
                            copy.changeDestination1(vanId, destId1);
                            copy.changeDestination2(vanId, destId2);
                            double benefit = copy.getBenefit();
                            double transportCost = copy.getTransportCost();
                            retval.add(new Successor(new String("changeOrigin with swapOrigin with changeDestination1 with changeDestination2 " + vanId + " " + originId + " " + originId2 + " " + destId1 + " " + destId2 + " benefit: " + benefit + " transportCost: " + transportCost), copy));
                        }
                    }
                }
            }
        }
    
        return (retval);
    }
}
