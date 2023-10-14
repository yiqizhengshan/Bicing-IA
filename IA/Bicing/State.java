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
        initialize_supply(Est);

        this.transportCost = 0;
        this.benefit = 0;
        this.suppliedDemand = 0;

        State.stations = Est;
        State.F = n_van;
        State.E = Est.size();
        
        for (int i = 0; i < E; ++i) {
            if (stations.get(i).getNumBicicletasNext() - stations.get(i).getDemanda() > 0) {
                estacionesPositivas.add(stations.get(i));
            }
            else if (stations.get(i).getNumBicicletasNext() - stations.get(i).getDemanda() < 0) {
                estacionesNegativas.add(stations.get(i));
            }
        }
        
        this.isStationVisited = new Boolean[estacionesPositivas.size()];
        
        sort_positivo();
        sort_negativo();
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
    
    public void changeDestination1(int fleetId, int newDestId) {
        int originId = fleet[fleetId][0];
        int bikesTaken = fleet[fleetId][1];
        int dest1Id = fleet[fleetId][2];
        int numBikesLeftDest1 = fleet[fleetId][3];
        int dest2Id = fleet[fleetId][4];
        int numBikesLeftDest2 = fleet[fleetId][5];
        Boolean newDestNeedsBikes = demandMinusSupply[newDestId] < 0;


        if (newDestId == originId || !newDestNeedsBikes) return;
        // If we want to eliminate 1st destination, we can't have a
        // second destination
        if (newDestId == -1 && dest2Id != -1) return;

        // int previousCost = transportCost
    }

    public void changeDestination2(int fleetId, int newDestId) {
        int originId = fleet[fleetId][0];
        int bikesTaken = fleet[fleetId][1];
        int dest1Id = fleet[fleetId][2];
        int numBikesLeftDest1 = fleet[fleetId][3];
        int dest2Id = fleet[fleetId][4];
        int numBikesLeftDest2 = fleet[fleetId][5];
        Boolean newDestNeedsBikes = demandMinusSupply[newDestId] < 0;


        if (newDestId == originId || !newDestNeedsBikes) return;
        if (newDestId == dest1Id) return;
        

    }

    public void swapOrigin(int fleetId, int newOriginId) {
        // Condition: newOriginId != dest1Id && newOriginId != dest2Id and newOriginId has excess bikes
        // !Problem: how to handle the case where origin has no sufficient excess bikes for
        // both destinations?
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
        int size_pos = estacionesPositivas.size();
        int size_neg = estacionesNegativas.size();
        int size_fleet = F;
        int i = 0;

        while (size_pos > i && size_neg > i && size_fleet > 0) {
            fleet[i][0] = i;
            fleet[i][1] = fleet[i][3] = (estacionesPositivas.get(i).getDemanda() - estacionesPositivas.get(i).getNumBicicletasNext())%30;
            fleet[i][2] = i;
            fleet[i][4] = fleet[i][5] = -1;

            isStationVisited[i] = true;

            //set numBicicletasNoUsadas
            estacionesPositivas.get(i).setNumBicicletasNoUsadas(estacionesPositivas.get(i).getDemanda() - estacionesPositivas.get(i).getNumBicicletasNext() - fleet[i][1]);
            int not_used_bikes = estacionesNegativas.get(i).getDemanda() - estacionesNegativas.get(i).getNumBicicletasNext() + fleet[i][1];
            if (not_used_bikes < 0) estacionesNegativas.get(i).setNumBicicletasNoUsadas(0);
            else estacionesNegativas.get(i).setNumBicicletasNoUsadas(not_used_bikes);

            //set transportCost
            transportCost += calcula_cost(i, i, fleet[i][1]);
            
            //set numBicicletasNext
            estacionesPositivas.get(i).setNumBicicletasNext(estacionesPositivas.get(i).getDemanda() - fleet[i][1]);
            estacionesNegativas.get(i).setNumBicicletasNext(estacionesNegativas.get(i).getDemanda() + fleet[i][1]);
            --size_fleet;
            --size_neg;
            --size_pos;
            ++i;
        }
    }

    public double calcula_cost(int i, int j, int num_bicis) {
        int cost_km = (num_bicis+9)/10;
        int distance = getEuclideanDistance(estacionesPositivas.get(i).getCoordX(), estacionesPositivas.get(i).getCoordY()estacionesNegativas.get(i).getCoordX(), estacionesNegativas.get(i).getCoordY());
        distance /= 1000;
        return cost_km*distance;
    }

    /* Sorting functions */

    //sort de mayor a menor la clase estacionesPositivas
    private void sort_positivo() {
        for (int i = 0; i < estacionesPositivas.size(); ++i) {
            for (int j = i + 1; j < estacionesPositivas.size(); ++j) {
                if (estacionesPositivas.get(i).getNumBicicletasNext() < estacionesPositivas.get(j).getNumBicicletasNext()) {
                    Estacion aux = estacionesPositivas.get(i);
                    estacionesPositivas.set(i, estacionesPositivas.get(j));
                    estacionesPositivas.set(j, aux);
                }
            }
        }
    }

    //sort de menor a mayor la clase estacionesNegativas
    private void sort_negativo() {
        for (int i = 0; i < estacionesNegativas.size(); ++i) {
            for (int j = i + 1; j < estacionesNegativas.size(); ++j) {
                if (estacionesNegativas.get(i).getNumBicicletasNext() > estacionesNegativas.get(j).getNumBicicletasNext()) {
                    Estacion aux = estacionesNegativas.get(i);
                    estacionesNegativas.set(i, estacionesNegativas.get(j));
                    estacionesNegativas.set(j, aux);
                }
            }
        }
    }

    // Auxiliary functions
    private int getEuclideanDistance(int originX, int originY, int destX, int destY) {
        return Math.abs(destX - originX) + Math.abs(destY - originY);
    }

    private double getVanTransportCost() {
        int dist
nce = getEuclideanDistance    }
}
