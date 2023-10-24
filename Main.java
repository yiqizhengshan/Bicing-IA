
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
            initializationMode = 1;
            mode = 1;
            E = 25;
            B = 1250;
            seed = 1233;
            F = 5;
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
        in.close();

        long[] executionTimes = new long[10];
        double[] benefits = new double[10];

        // Seeds
        for (int i = 0; i < 11; ++i) {
            int newSeed = seed + i;
            Estaciones stations;
            if (mode == 1) stations = new Estaciones(E, B, Estaciones.EQUILIBRIUM, newSeed);
            else stations = new Estaciones(E, B, Estaciones.RUSH_HOUR, newSeed);

            State initialState = new State(F, stations);
            
            //counter initialization
            long tiempoInicio = System.currentTimeMillis();
            
            if (initializationMode == 1) initialState.initialize_easy();
            else if (initializationMode == 2) initialState.initialize_medium();
            else initialState.initialize_hard();

            // Create the Problem object
            Problem p = new Problem(initialState,
                    new IA.Bicing.BicingSuccesorFunction(),
                    new IA.Bicing.BicingGoalTest(),
                    new IA.Bicing.BicingHeuristicFunction());

            // Hill Climbing
            Search alg = new HillClimbingSearch();

            // Simulated Annealing
            // int iterations = 100000;
            // int step = 20;
            // int k = 1;
            // double lambda = 0.0001;
            // Search alg = new SimulatedAnnealingSearch(step, iterations, k, lambda);

            // Instantiate the SearchAgent object
            SearchAgent agent = new SearchAgent(p, alg);
            long tiempoFin = System.currentTimeMillis();

            long totalTiempo = tiempoFin - tiempoInicio;
            
            // We print the results of the search
            State finalState = (State) alg.getGoalState();

            // printActions(agent.getActions());
            // printInstrumentation(agent.getInstrumentation());

            if (i != 0) {
                executionTimes[i - 1] = totalTiempo;
                benefits[i - 1] = finalState.getBenefit();
            }
        }
        for (int i = 0; i < 10; ++i) {
            System.out.println("Beneficio: " + benefits[i] + " Tiempo de ejecución: " + executionTimes[i] + " milisegun");
        }
        
    }
}