//BicingBoard.java
package IA.Bicing;

/**
 * Created by bejar on 17/01/17.
 */
public class State {
    // originId, bikesTaken, destId1, numBikesLeftDest2, destId2,
    // numBikesLeftDest2
    private int[][] fleet; // .size .insert
    private Boolean[] isStationVisited;
    private int[] demandMinusSupply; // preguntarle al profe

    private double transportCost;
    private double benefit; // benefit == demandSupplied - cost;
    private int suppliedDemand; //

    private static Estaciones stations;
    private static int F; // Max Furgonetas
    private static int E; // Number of stations

    /* Constructor */

    //Hay que separar positivos con negativos con 2 vectores static
    private static Estaciones estacionesPositivas;
    private static Estaciones estacionesNegativas;


    public State(int n_van, Estaciones Est) {
        this.isStationVisited = new Boolean[Est.size()];
        initialize_supply(Est);

        this.transportCost = 0;
        this.benefit = 0;
        this.suppliedDemand = 0;

        State.stations = Est;
        State.F = n_van;
        State.E = Est.size();
    }

    // Copy constructor
    public State(final State state) {
        this.setFleet(state.getFleet());
        this.setIsStationVisited(state.getIsStationVisited());
        this.setDemandMinusSupply(state.getDemandMinusSupply());

        this.setTransportCost(state.getTransportCost());
        this.setBenefit(state.getBenefit());
        this.setSuppliedDemand(state.getSuppliedDemand());

        this.setStations(getStations());
        this.setDemandMinusSupply(state.getDemandMinusSupply());
        this.setCost(state.getCost());
        this.setBenefits(state.getBenefits());
        this.setDemandSupplied(state.getDemandSupplied());
    }

    /* Operators */
    public void put_and_move_one(int origin_id, int dest_id, int n_bicis) {
        Estacion ori = stations.get(origin_id);
        Estacion dest = stations.get(dest_id);

        int surplus = Math.min(ori.getNumBicicletasNext() - ori.getDemanda(),
                Math.min(30, ori.getNumBicicletasNoUsadas()));
        int next_demand = dest.getDemanda() - dest.getNumBicicletasNext();
    }

    public void put_and_move_two(int origin_id, int dest_id1, int dest_id2, int n_bicis) {
        Estacion ori = stations.get(origin_id);
        Estacion dest1 = stations.get(dest_id1);
        Estacion dest2 = stations.get(dest_id2);

        int surplus = Math.min(ori.getNumBicicletasNext() - ori.getDemanda(),
                Math.min(30, ori.getNumBicicletasNoUsadas()));
        int next_demand1 = dest1.getDemanda() - dest1.getNumBicicletasNext();
        int next_demand2 = dest2.getDemanda() - dest2.getNumBicicletasNext();
    }

    /* Heuristic function */
    public double heuristic() {
        return this.benefit - this.transportCost;
    }

    /* Goal test */
    public boolean is_goal() {
        return false;
        // State does not have better successor states
    }

    /* Getters */

    public int[][] getFleet() {
        return this.fleet;
    }

    public Boolean[] getIsStationVisited() {
        return this.isStationVisited;
    }

    public int[] getDemandMinusSupply() {
        return this.demandMinusSupply;
    }

    public double getTransportCost() {
        return this.transportCost;
    }

    public double getBenefit() {
        return this.benefit;
    }

    public int getSuppliedDemand() {
        return this.suppliedDemand;
    }

    public static Estaciones getStations() {
        return stations;
    }

    public double getCost() {
        return this.transportCost;
    }

    public double getBenefits() {
        return this.benefit;
    }

    public int getDemandSupplied() {
        return this.suppliedDemand;
    }

    public static int getE() {
        return E;
    }

    public static int getF() {
        return F;
    }

    /* Setters */
    public void setFleet(final int[][] fleet) {
        int F = fleet.length;
        int numAttrib = fleet[0].length;
        this.fleet = new int[F][numAttrib];
        for (int i = 0; i < F; ++i) {
            for (int j = 0; j < numAttrib; ++j) {
                this.fleet[i][j] = fleet[i][j];
            }
        }
    }

    public void setIsStationVisited(final Boolean[] isStationVisited) {
        int E = isStationVisited.length;
        this.isStationVisited = new Boolean[E];
        for (int i = 0; i < E; ++i) {
            this.isStationVisited[i] = isStationVisited[i];
        }
    }

    public void setDemandMinusSupply(final int[] demandMinusSupply) {
        int E = demandMinusSupply.length;
        this.demandMinusSupply = new int[E];
        for (int i = 0; i < E; ++i) {
            this.demandMinusSupply[i] = demandMinusSupply[i];
        }
    }

    public void setTransportCost(final double transportCost) {
        this.transportCost = transportCost;
    }

    public void setBenefit(final double benefit) {
        this.benefit = benefit;
    }

    public void setSuppliedDemand(final int suppliedDemand) {
        this.suppliedDemand = suppliedDemand;
    }

    public void setStations(final Estaciones estacions) {
        stations = estacions;
    }

    public void setCost(final double cost) {
        this.transportCost = cost;
    }

    public void setBenefits(final double benefits) {
        this.benefit = benefits;
    }

    public void setDemandSupplied(final int demandSupplied) {
        this.suppliedDemand = demandSupplied;
    }

    /* Initializers */

    private void initialize_supply(Estaciones Est) {
        this.demandMinusSupply = new int[Est.size()];
        for (int i = 0; i < Est.size(); i++) {
            demandMinusSupply[i] = Est.get(i).getDemanda() - Est.get(i).getNumBicicletasNext();
        }
    }

    public void initialize_easy() {
        fleet = new int[F][6];
        for (int i = 0; i < F; i++) {
            for (int j = 0; j < 6; j++) {
                this.fleet[i][j] = -1;
            }
        }
    }

    public void initialize_medium() {
        fleet = new int[F][6];
        for (int i = 0; i < F; i++) {
            for (int j = 0; j < 6; j++) {
                this.fleet[i][j] = -1;
            }
        }
    }

    // Some functions will be needed for creating a copy of the state
}
