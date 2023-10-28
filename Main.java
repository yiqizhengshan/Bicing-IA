
//Main.java
import IA.Bicing.*;

import aima.search.framework.GraphSearch;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.framework.SuccessorFunction;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;


public class Main {
    private static void printInstrumentation(Properties properties) {
        
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

    public static void main(String[] args) throws Exception {
        String algorithmMode = args[0]; // hc, sa
        String initializationMode = args[1];    // easy, medium, hard
        String demandMode = args[2];    // equi, rush
        int heuristicType = Integer.parseInt(args[3]); // 1, 2
        int E = Integer.parseInt(args[4]);
        int F = Integer.parseInt(args[5]);
        int B = Integer.parseInt(args[6]);
        int seed = Integer.parseInt(args[7]);
        
        SuccessorFunction succesorFunc;
        Search search = null;
        if (algorithmMode.equals("hc")) {
            search = new HillClimbingSearch();
            succesorFunc = new IA.Bicing.BicingSuccesorFunction();
        }
        else if (algorithmMode.equals("sa")) {
            int iterations = Integer.parseInt(args[8]);
            int step = Integer.parseInt(args[9]);
            int k = Integer.parseInt(args[10]);
            double lambda = Double.parseDouble(args[11]);
            search = new SimulatedAnnealingSearch(iterations, step, k, lambda);
            succesorFunc = new IA.Bicing.BicingSuccesorFunctionSA();
        }
        else {
            System.out.println("Incorrect algorithmMode format: choose hc or sa");
            return;
        }

        Estaciones stations;
        if (demandMode.equals("equi"))
            stations = new Estaciones(E, B, Estaciones.EQUILIBRIUM, seed);
        else if (demandMode.equals("rush"))
            stations = new Estaciones(E, B, Estaciones.RUSH_HOUR, seed);
        else {
            System.out.println("Incorrect demandMode format: equi or rush");
            return;
        }

        if (heuristicType != 1 && heuristicType != 2) {
            System.out.println("Incorrect heuristic function format: 1 (no transport cost) or 2 (with transport cost)");
            return;
        }
        
        State initialState = new State(F, stations, heuristicType);
        long tiempoInicio = System.currentTimeMillis();
        if (initializationMode.equals("easy"))
            initialState.initialize_easy();
        else if (initializationMode.equals("medium"))
            initialState.initialize_medium();
        else if (initializationMode.equals("hard"))
            initialState.initialize_hard();
        else {
            System.out.println("Incorrect initializationMode format: easy, medium or hard");
            return;
        }
        
        Problem p = new Problem(initialState,
            succesorFunc,
            new IA.Bicing.BicingGoalTest(),
            new IA.Bicing.BicingHeuristicFunction());
        SearchAgent agent = new SearchAgent(p, search);

        long tiempoFin = System.currentTimeMillis();
        long totalTiempo = tiempoFin - tiempoInicio;

        
        if (algorithmMode.equals("hc")) {
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        }
        // Print the results of the search
        State finalState = (State) search.getGoalState();
        System.out.print("time: " + totalTiempo + "ms ");
        finalState.printState();
    }
}