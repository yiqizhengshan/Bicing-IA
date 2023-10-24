
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
            mode = 1;
            E = 25;
            B = 1250;
            seed = 1234;
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

        int[] seeds = { 4655, 1234, 66583, 98511, 4656, 4651, 22153, 38421, 77751, 99999, 55236 };
        //int[] seeds = {1234, 1234+1, 1234+2, 1234+3, 1234+4, 1234+5, 1234+6, 1234+7, 1234+8, 1234+9};
        int[] K = { 1, 5, 25, 50, 125 };
        double[] LAMBDA = { 1.0, 0.1, 0.01, 0.001, 0.0001};
        //5 25 50 125, 0.001 y 0.0005
        //25 y 0.01
        int index = 0;
        long[] executionTimes = new long[25];
        double[] benefits = new double[25];

        System.out.println("k lambda benefit");

        // Seeds
        // for (int i = 0; i < 5; ++i) {
        //     for (int j = 0; j < 5; ++j) {
        //         double media = 0.0;
        //         for (int l = 0; l < 10; ++l) {
        //             int newSeed = seeds[l];
        //             Estaciones stations;
        //             if (mode == 1)
        //                 stations = new Estaciones(E, B, Estaciones.EQUILIBRIUM, newSeed);
        //             else
        //                 stations = new Estaciones(E, B, Estaciones.RUSH_HOUR, newSeed);

        //             State initialState = new State(F, stations);

        //             // counter initialization
        //             long tiempoInicio = System.currentTimeMillis();

        //             if (initializationMode == 1)
        //                 initialState.initialize_easy();
        //             else if (initializationMode == 2)
        //                 initialState.initialize_medium();
        //             else
        //                 initialState.initialize_hard();

        //             // Create the Problem object
        //             Problem p = new Problem(initialState,
        //                     new IA.Bicing.BicingSuccesorFunctionSA(),
        //                     new IA.Bicing.BicingGoalTest(),
        //                     new IA.Bicing.BicingHeuristicFunction());

        //             // Simulated Annealing
        //             int iterations = 100000;
        //             int step = 20;
        //             int k = K[i];
        //             double lambda = LAMBDA[j];
        //             Search alg = new SimulatedAnnealingSearch(iterations, step, k, lambda);

        //             // Instantiate the SearchAgent object
        //             SearchAgent agent = new SearchAgent(p, alg);
        //             long tiempoFin = System.currentTimeMillis();
        //             long totalTiempo = tiempoFin - tiempoInicio;

        //             // Print the results of the search
        //             State finalState = (State) alg.getGoalState();
        //             // finalState.printState();
        //             // finalState.printTotalTransportCost();
        //             media += finalState.getBenefit();
        //             // if (k == 125 && lambda == 0.01)
        //             //     System.out.println(K[i] + " " + LAMBDA[j] + " " + finalState.getBenefit());
        //         }
        //         media /= 10.0;
        //         benefits[index] = media;
        //         System.out.println(benefits[index]);
        //         ++index;
        //     }
        // }

        int num_pruebas = 11;
        int[] Nestaciones = new int[num_pruebas];
        int[] Nfurgonetas = new int[num_pruebas];
        int[] Nbicicletas = new int[num_pruebas];
        Nestaciones[0] = 25;
        Nfurgonetas[0] = 5;
        Nbicicletas[0] = 1250;
        for (int i = 1; i < num_pruebas; ++i) {
            Nestaciones[i] = 25*i;
            Nfurgonetas[i] = 5*i;
            Nbicicletas[i] = 1250*i;
        }

        for (int j = 0; j < 11; ++j) {
        //for (int i = 0; i < num_pruebas; ++i) {
            // if (i != 1) continue;
            int newSeed = seeds[j];
            Estaciones stations;
            // E = Nestaciones[i];
            // B = Nbicicletas[i];
            if (mode == 1)
                stations = new Estaciones(E, B, Estaciones.EQUILIBRIUM, newSeed);
            else
                stations = new Estaciones(E, B, Estaciones.RUSH_HOUR, newSeed);

            // F = Nfurgonetas[i];
            
            State initialState = new State(F, stations);

            // counter initialization
            long tiempoInicio = System.currentTimeMillis();

            if (initializationMode == 1)
                initialState.initialize_easy();
            else if (initializationMode == 2)
                initialState.initialize_medium();
            else
                initialState.initialize_hard();

            // Create the Problem object
            Problem p = new Problem(initialState,
                    new IA.Bicing.BicingSuccesorFunctionSA(),
                    new IA.Bicing.BicingGoalTest(),
                    new IA.Bicing.BicingHeuristicFunction());

            // Hill Climbing
            // Search alg = new HillClimbingSearch();

            // Simulated Annealing
            int iterations = 100000;
            int step = 20;
            int k = K[4];
            double lambda = LAMBDA[4];
            Search alg = new SimulatedAnnealingSearch(iterations, step, k, lambda);

            // Instantiate the SearchAgent object
            SearchAgent agent = new SearchAgent(p, alg);
            long tiempoFin = System.currentTimeMillis();
            long totalTiempo = tiempoFin - tiempoInicio;

            // Print the results of the search
            State finalState = (State) alg.getGoalState();

            // printActions(agent.getActions());
            // printInstrumentation(agent.getInstrumentation());
            // if (i == 1) {
            // finalState.printFleet();
            // finalState.printBikesNeeded();
            // finalState.printState();
            // }
            // if (i != 0) {
                executionTimes[j] = totalTiempo;
                benefits[j] = finalState.getBenefit();
            //}
        //}
        System.out.println(finalState.getDistanceTotal());
    }
        for (int i = 0; i < 11; ++i) { 
             System.out.println(benefits[i]);
        }
        for (int i = 0; i < 11; ++i) { 
            System.out.println(executionTimes[i]);
        }
    }
}