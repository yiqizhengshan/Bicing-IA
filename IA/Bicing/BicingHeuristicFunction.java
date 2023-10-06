package IA.Bicing;

import aima.search.framework.HeuristicFunction;

public class BicingHeuristicFunction implements HeuristicFunction {
    public double getHeuristicValue(Object n) {
        return ((State) n).heuristic();
    }
}
