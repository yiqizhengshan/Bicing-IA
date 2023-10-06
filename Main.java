
//Main.java
import IA.Bicing.*;

import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;

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

        System.out.println("Escribe EASY o MEDIUM");
        String s2 = in.nextLine();
        System.out.println("Escribe EQUILIBRIUM o RUSH_HOUR");
        String mode = in.nextLine();
        System.out.println("Dame el número de estaciones:");
        int s = in.nextInt();
        System.out.println("Dame el número de bicicletas:");
        int b = in.nextInt();
        System.out.println("Dame la semilla:");
        int seed = in.nextInt();

        Estaciones Est;
        if (mode == "EQUILIBRIUM") {
            Est = new Estaciones(s, b, Estaciones.EQUILIBRIUM, seed);
        } else {
            Est = new Estaciones(s, b, Estaciones.RUSH_HOUR, seed);
        }
        System.out.println("Dame el número de furgonetas:");
        int f = in.nextInt();

        State initialState = new State(f, Est);

        switch (s2) {
            case "EASY":
                initialState.initialize_easy();
            case "MEDIUM":
                initialState.initialize_medium();
        }

        // close the Scanner object
        in.close();

        // Create the Problem object
        Problem p = new Problem(initialState,
                new IA.Bicing.BicingSuccesorFunction(),
                new IA.Bicing.BicingGoalTest(),
                new IA.Bicing.BicingHeuristicFunction());

        // Instantiate the search algorithm
        // AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search alg = new AStarSearch(new GraphSearch());

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
