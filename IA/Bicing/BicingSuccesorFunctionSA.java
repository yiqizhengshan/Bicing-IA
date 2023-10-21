package IA.Bicing;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BicingSuccesorFunctionSA implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        State currentState = (State) state;

        int F = State.getF();
        int E = State.getE();

        Random myRandom = new Random();
        int vanId = myRandom.nextInt(F);
        int newOriginId = myRandom.nextInt(E);
        int newDestId = myRandom.nextInt(E);
        int operatorId = myRandom.nextInt(4);

        State newState = (State) currentState;
        String operator, results, fleetState;
        // Devolver un sucesor random
        if (operatorId == 0) {
            // Change Destination 1
            
            newState.changeDestination1(vanId, newDestId);

            operator = new String("changeDestination1 " + vanId + " " + newDestId);
            results = new String("benefit: " + newState.getBenefit() + " suppliedDemand: " + newState.getSuppliedDemand() + " transportCost: " + newState.getTransportCost() + " totalLength: " + copy.getTotalLength());
            fleetState = newState.getFleetState();
        } else if (operatorId == 1) {
            // Change Destination 2
            newState.changeDestination2(vanId, newDestId);

            operator = new String("changeDestination2 " + vanId + " " + newDestId);
            results = new String("benefit: " + newState.getBenefit() + " suppliedDemand: " + newState.getSuppliedDemand() + " transportCost: " + newState.getTransportCost() + " totalLength: " + copy.getTotalLength());
            fleetState = newState.getFleetState();
        }
        else {
            // Change Origin
            newState.changeOrigin(vanId, newOriginId);

            operator = new String("changeOrigin " + vanId + " " + newOriginId);
            results = new String("benefit: " + newState.getBenefit() + " suppliedDemand: " + newState.getSuppliedDemand() + " transportCost: " + newState.getTransportCost() + " totalLength: " + copy.getTotalLength());
            fleetState = newState.getFleetState();
        }
        retval.add(new Successor(new String(operator + "\n" + results + "\n" + fleetState), newState));
        return retval;
    }
}