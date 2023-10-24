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
            // substractVan
            // for (int count = 0; count < 10; ++count) {
            //     State copy = new State(board);
            //     if (copy.getFleet()[vanId][1] - count > 0) copy.substractVan(vanId, count);

            //     String operator = new String("substractVan " + vanId + " " + count);
            //     String results = new String("benefit: " + copy.getBenefit() + " suppliedDemand: " + copy.getSuppliedDemand() + " transportCost: " + copy.getTransportCost() + " totalLength: " + copy.getTotalLength());
            //     //String fleetState = copy.getFleetState();
            //     String fleetState = "";
            //     retval.add(new Successor(new String(operator + "\n" + results + "\n" + fleetState), copy));
            // }

            // changeDestination2
            for (int destId2 = 0; destId2 < E; ++destId2) {
                State copy = new State(board);
                copy.changeDestination2(vanId, destId2);

                String operator = new String("changeDestination2 " + vanId + " " + destId2);
                String results = new String("benefit: " + copy.getBenefit() + " suppliedDemand: " + copy.getSuppliedDemand() + " transportCost: " + copy.getTransportCost() + " totalLength: " + copy.getTotalLength());
                //String fleetState = copy.getFleetState();
                String fleetState = "";
                retval.add(new Successor(new String(operator + "\n" + results + "\n" + fleetState), copy));
            }

            // changeDestination1
            for (int destId1 = 0; destId1 < E; ++destId1) {
                State copy = new State(board);
                if (!copy.isChangeDestination1ConditionOkay(vanId, destId1)) continue;
                copy.changeDestination1(vanId, destId1);

                String operator = new String("changeDestination1 " + vanId + " " + destId1);
                String results = new String("benefit: " + copy.getBenefit() + " suppliedDemand: " + copy.getSuppliedDemand() + " transportCost: " + copy.getTransportCost() + " totalLength: " + copy.getTotalLength());
                //String fleetState = copy.getFleetState();
                String fleetState = "";
                retval.add(new Successor(new String(operator + "\n" + results + "\n" + fleetState), copy));
            }

            // changeOrigin
            for (int originId = 0; originId < E; ++originId) {
                State copy = new State(board);
                if (!copy.isChangeOriginConditionOkay(vanId, originId)) continue;
                copy.changeOrigin(vanId, originId);

                String operator = new String("changeOrigin " + vanId + " " + originId);
                String results = new String("benefit: " + copy.getBenefit() + " suppliedDemand: " + copy.getSuppliedDemand() + " transportCost: " + copy.getTransportCost() + " totalLength: " + copy.getTotalLength());
                //String fleetState = copy.getFleetState();
                String fleetState = "";
                retval.add(new Successor(new String(operator + "\n" + results + "\n" + fleetState), copy));
            }


            // //swapOrigin with changeDestination1 with changeDestination2
            // for (int originId2 = 0; originId2 < F; ++originId2) {
            //     for (int destId1 = 0; destId1 < E; ++destId1) {
            //         for (int destId2 = 0; destId2 < E; ++destId2) {
            //             for (int count = 0; count < 10; ++count) {
            //                 State copy = new State(board);
            //                 if (copy.getFleet()[vanId][1] - count > 0) copy.substractVan(vanId, count);
            //                 copy.changeOrigin(vanId, originId2);
            //                 copy.changeDestination1(vanId, destId1);
            //                 copy.changeDestination2(vanId, destId2);
            //                 double benefit = copy.getBenefit();
            //                 double transportCost = copy.getTransportCost();
            //                 //String FleetState = copy.getFleetState();
            //                 String FleetState = "";
            //                 retval.add(new Successor(new String("substractVan, changeOrigin, changeDestination1, changeDestination2 " + vanId + " " + count + " " + originId2 + " " + destId1  + " " + destId2 + " benefit: " + benefit + " transportCost: " + transportCost + " totalLength: " + copy.getTotalLength() + "\n" + FleetState), copy));
            //             }
            //         }
            //     }
            // }
        }
        return (retval);
    }
}
