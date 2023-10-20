
//Main.java
import IA.Bicing.*;

import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

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
        // Using Scanner for Getting Input from User

        Scanner in = new Scanner(System.in);

        System.out.println("Escribe 1 (EASY) o 2 (MEDIUM)");
        int difficulty = in.nextInt();
        System.out.println("Escribe 1 (EQUILIBRIUM) o 2 (RUSH_HOUR)");
        int mode = in.nextInt();
        System.out.println("Dame el número de estaciones:");
        int E = in.nextInt();
        System.out.println("Dame el número de bicicletas:");
        int B = in.nextInt();
        System.out.println("Dame la semilla:");
        int seed = in.nextInt();

        Estaciones Est;
        if (mode == 1) {
            Est = new Estaciones(E, B, Estaciones.EQUILIBRIUM, seed);
        } else {
            Est = new Estaciones(E, B, Estaciones.RUSH_HOUR, seed);
        }

        System.out.println("Dame el número de furgonetas:");
        int F = in.nextInt();

        State initialState = new State(F, Est);

        //print bikesNeeded
        for (int i = 0; i < initialState.getBikesNeeded().length; ++i) {
            System.out.println("bikesNeeded[" + i + "]: " + initialState.getBikesNeeded()[i]);
        }

        if (difficulty == 1) {
            initialState.initialize_easy();
        } else {
            initialState.initialize_medium();
        }

        //print bikesNeeded after initialize
        String FleetState = initialState.getFleetState();
        System.out.println(FleetState);

        // close the Scanner object
        in.close();

        // Create the Problem object
        Problem p = new Problem(initialState,
                new IA.Bicing.BicingSuccesorFunction(),
                new IA.Bicing.BicingGoalTest(),
                new IA.Bicing.BicingHeuristicFunction());

        // Instantiate the search algorithm
        // AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search alg = new HillClimbingSearch();


        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        // We print the results of the search
        System.out.println();
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        // You can access also to the goal state using the
        // method getGoalState of class Search

    }
}