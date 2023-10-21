
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
        // Using Scanner for Getting Input from User

        Scanner in = new Scanner(System.in);
        
        int initializationMode, mode, E, B, seed, F;

        System.out.println("Modo del input: 0 automático, 1 manual");
        int modoInput = in.nextInt();
        if (modoInput == 0) {
            initializationMode = 3;
            mode = 2;
            E = 25;
            B = 1250;
            seed = 1234;
            F = 20;
        }
        else {
            System.out.println("Modo de inicialización: 1 (EASY) o 2 (MEDIUM) o 3 (HARD)");
            initializationMode = in.nextInt();
            System.out.println("Modo: 1 (EQUILIBRIUM) o 2 (RUSH_HOUR)");
            mode = in.nextInt();
            System.out.println("Dame el número de estaciones:");
            E = in.nextInt();
            System.out.println("Dame el número de bicicletas:");
            B = in.nextInt();
            System.out.println("Dame la semilla:");
            seed = in.nextInt();
            System.out.println("Dame el número de furgonetas:");
            F = in.nextInt();
        }

        Estaciones stations;
        if (mode == 1) stations = new Estaciones(E, B, Estaciones.EQUILIBRIUM, seed);
        else stations = new Estaciones(E, B, Estaciones.RUSH_HOUR, seed);

        State initialState = new State(F, stations);
        
        //counter initialization
        long tiempoInicio = System.currentTimeMillis();


        if (initializationMode == 1) initialState.initialize_easy();
        else if (initializationMode == 2) initialState.initialize_medium();
        else initialState.initialize_hard();

        in.close();

        // Create the Problem object
        Problem p = new Problem(initialState,
                new IA.Bicing.BicingSuccesorFunction(),
                new IA.Bicing.BicingGoalTest(),
                new IA.Bicing.BicingHeuristicFunction());

        // Instantiate the search algorithm
        // AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search alg = new HillClimbingSearch();
        // int iterations = 10000;
        // int step = 
        // int k = 125;
        // float lambda = 0.0001;
        // Search algSA = new SimulatedAnnealingSearch(iterations, k, lambda);

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        // Print initial state things
        String results = new String("benefit: " + initialState.getBenefit() + " suppliedDemand: " + initialState.getSuppliedDemand() + " transportCost: " + initialState.getTransportCost() + " totalLength: " + initialState.getTotalLength());
        String fleetState = initialState.getFleetState();
            System.out.println(results + "\n" + fleetState);
        //System.out.println("suppliedDemand: " + initialState.getSuppliedDemand());

        // We print the results of the search
        System.out.println();
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        long tiempoFin = System.currentTimeMillis();
        long totalTiempo = tiempoFin - tiempoInicio;
        System.out.println("Tiempo de ejecución: " + totalTiempo + " milisegundos");
        System.out.println("----------------------------------");
        // You can access also to the goal state using the
        // method getGoalState of class Search
    }
}