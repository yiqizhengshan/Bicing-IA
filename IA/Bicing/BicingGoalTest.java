package IA.Bicing;

import aima.search.framework.GoalTest;

public class BicingGoalTest implements GoalTest {
    public boolean isGoalState(Object state) {
        return ((State) state).is_goal();
    }
}
