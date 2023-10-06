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

        for (int vanId = 0; vanId < F; ++vanId) {
            for (int originId = 0; originId < E; ++originId) {
                // van can go to same originId OR not visited station

                for (int destId1 = 0; destId1 < E; ++destId1) {
                    if (destId1 == originId)
                        continue;
                    // single move
                    for (int destId2 = 0; destId2 < E; ++destId2) {
                        if (destId2 == originId || destId2 == destId1)
                            continue;
                        // double move
                    }
                }
            }
        }
        return (retval);
    }
}
