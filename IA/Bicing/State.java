//BicingBoard.java
package IA.Bicing;
import java.util.ArrayList;

/**
 * Created by bejar on 17/01/17.
 */
public class State {
    // originId, bikesTaken, destId1, numBikesLeftDest2, destId2,
    // numBikesLeftDest2
    private int[][] fleet; // .size .insert
    private int[] isStationVisited; // if not visited, -1
                                    // else, contains the id of the van that visits it
    private int[] bikesNeeded;

    private double transportCost;
    private double benefit; // benefit = suppliedDemand - transportCost;
    private int suppliedDemand; //

    private static Estaciones stations;
    private static int F; // Max Furgonetas
    private static int E; // Number of stations

    /* Constructor */

    public State(int n_van, Estaciones Est) {
        State.stations = Est;
        State.F = n_van;
        State.E = Est.size();

        this.fleet = new int[n_van][6];
        this.isStationVisited = new int[Est.size()];

        for (int i = 0; i < State.E; ++i) {
            this.isStationVisited[i] = -1;
        }
        
        initialize_supply(Est);

        this.transportCost = 0;
        this.benefit = 0;
        this.suppliedDemand = 0;
        

    }

    // Copy constructor
    public State(final State state) {
        this.setFleet(state.getFleet());
        this.setIsStationVisited(state.getIsStationVisited());
        this.setBikesNeeded(state.getBikesNeeded());

        this.setTransportCost(state.getTransportCost());
        this.setBenefit(state.getBenefit());
        this.setSuppliedDemand(state.getSuppliedDemand());

        this.setStations(getStations());
        this.setBikesNeeded(state.getBikesNeeded());
        this.setCost(state.getCost());
        this.setDemandSupplied(state.getDemandSupplied());
    }

    /* Operators */
    public void changeDestination1(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int bikesTaken = fleet[vanId][1];
        int dest1Id = fleet[vanId][2];
        int numBikesLeftDest1 = fleet[vanId][3];
        int dest2Id = fleet[vanId][4];
        int numBikesLeftDest2 = fleet[vanId][5];
        Boolean newDestNeedsBikes = bikesNeeded[newDestId] < 0;


        if (newDestId == originId || !newDestNeedsBikes) return;
        // If we want to eliminate 1st destination, we can't have a
        // second destination
        if (newDestId == -1 && dest2Id != -1) return;

        // Update transport cost from origin to dest1 (bikesTaken is the same, bc we come from origin)
        // Calculate the transport cost of the previous route
        double previousTransportCost1 = getVanTransportCost(originId, dest1Id, bikesTaken);
        double previousTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        // 
        
        this.suppliedDemand -= numBikesLeftDest1 + numBikesLeftDest2;

        fleet[vanId][3] = numBikesLeftDest1 = Math.min(bikesTaken, bikesNeeded[newDestId]);
        fleet[vanId][5] = numBikesLeftDest2 = Math.min(bikesTaken - numBikesLeftDest1, bikesNeeded[dest2Id]);
        dest1Id = fleet[vanId][2] = newDestId;

        
        double newTransportCost1 = getVanTransportCost(originId, newDestId, bikesTaken);
        double newTransportCost2 = getVanTransportCost(newDestId, dest2Id, bikesTaken - numBikesLeftDest1);

        this.transportCost = transportCost - (previousTransportCost1 + previousTransportCost2) + (newTransportCost1 + newTransportCost2);

        this.suppliedDemand += numBikesLeftDest1 + numBikesLeftDest2;

        // Update transport cost from dest1 to dest2


        // Get previous and new supplied
        int previousBikesSupplied = numBikesLeftDest1;  // Supposing that all the bikes we leave, gives us benefit
        int newBikesSupplied = Math.min(bikesTaken, bikesNeeded[dest1Id]);
        
        // Calculate new benefit
        this.benefit = (float) suppliedDemand - transportCost;

        // Update van
        
    }

    public void changeDestination2(int fleetId, int newDestId) {
        int originId = fleet[fleetId][0];
        int bikesTaken = fleet[fleetId][1];
        int dest1Id = fleet[fleetId][2];
        int numBikesLeftDest1 = fleet[fleetId][3];
        int dest2Id = fleet[fleetId][4];
        int numBikesLeftDest2 = fleet[fleetId][5];
        Boolean newDestNeedsBikes = bikesNeeded[newDestId] < 0;


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

    public int [] getIsStationVisited() {
        return this.isStationVisited;
    }

    public int[] getBikesNeeded() {
        return this.bikesNeeded;
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

    public void setIsStationVisited(final int[] isStationVisited) {
        int E = isStationVisited.length;
        this.isStationVisited = new int[E];
        for (int i = 0; i < E; ++i) {
            this.isStationVisited[i] = isStationVisited[i];
        }
    }

    public void setBikesNeeded(final int[] bikesNeeded) {
        int E = bikesNeeded.length;
        this.bikesNeeded = new int[E];
        for (int i = 0; i < E; ++i) {
            this.bikesNeeded[i] = bikesNeeded[i];
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
        this.bikesNeeded = new int[Est.size()];
        for (int i = 0; i < Est.size(); i++) {
            bikesNeeded[i] = (Est.get(i).getDemanda() - Est.get(i).getNumBicicletasNext());
            if (bikesNeeded[i] > 0) bikesNeeded[i] = bikesNeeded[i]%30;
        }
    }

    public void initialize_easy() {
        fleet = new int[F][6];
        // Estrategia: 
        for (int i = 0; i < F; i++) {
            for (int j = 0; j < 6; j++) {
                this.fleet[i][j] = -1;
            }
        }
    }

    public void initialize_medium() {
        int size_fleet = F;
        int i = 0;
        int j = 0;
        
        while (i < E && j < E && size_fleet > 0) {
            i = 0;
            j = 0;
            //print bikes Needed
            for (int k = 0; k < E; ++k) {
                System.out.print(bikesNeeded[k] + " ");
            }
            //llegas al primer positivo
            while (bikesNeeded[i] <= 0 && i < E) {
                ++i;
            }
            //llegas al primer negativo
            while (bikesNeeded[j] >= 0 && j < E) {
                ++j;
            }
            
            System.out.println("i: " + i + " j: " + j + " size_fleet: " + size_fleet);
            
            /*-15 -1 2 -26 -22 -10 -12 -8 3 19 10 -2 -10 22 -17 4 -23 4 9 0 28 -22 -16 27 -9 i: 2 j: 0 size_fleet: 5
              -13 -1 0 -26 -22 -10 -12 -8 3 19 10 -2 -10 22 -17 4 -23 4 9 0 28 -22 -16 27 -9 i: 8 j: 0 size_fleet: 4
              -10 -1 0 -26 -22 -10 -12 -8 0 19 10 -2 -10
              0 -1 0 -26 -22 -10 -12 -8 0 9 10 -2 -10
              0 0 0 -26 -22 -10 -12 -8 0 0 10 -2 -10
              0 0 0 -16 -22 -10 -12 -8 0 0 0 -2 -10*/

            //se usa de origen?
            if (isStationVisited[i] == -1) {
                int k = F - size_fleet;
                isStationVisited[i] = k;
                
                fleet[k][0] = i; //origen
                int temp1 = bikesNeeded[i];
                int temp2 = Math.abs(bikesNeeded[j]);
                if (temp1 > temp2) {
                    fleet[k][1] = temp2; //bicis que sale de origen
                    fleet[k][3] = temp2; //bicis que llegan a destino
                    bikesNeeded[i] += temp2;
                    bikesNeeded[j] -= temp2;
                }
                else {
                    fleet[k][1] = temp1; //bicis que sale de origen
                    fleet[k][3] = temp1; //bicis que llegan a destino
                    bikesNeeded[i] = bikesNeeded[i] - temp1;
                    bikesNeeded[j] += temp1;
                }

                fleet[k][2] = j; //destino
                fleet[k][4] = -1;
                fleet[k][5] = -1; //no hay segundo destino

                //set suppliedDemand
                suppliedDemand += fleet[k][3];
                //update size_fleet
                --size_fleet;
            }
            else {
                int h = isStationVisited[i];
                //tiene 2o destino?
                if (fleet[h][4] == -1) {
                    fleet[h][4] = j;
                    int temp1 = bikesNeeded[i];
                    int temp2 = Math.abs(bikesNeeded[j]);
                    if (temp1 > temp2) {
                        fleet[h][1] += temp2; //bicis que sale de origen
                        fleet[h][5] = temp2; //bicis que sale de origen
                        bikesNeeded[i] = 0; //ponemos a cero porque ya ha hecho 2 destinos y aun le sobran
                        bikesNeeded[j] -= temp2;
                    }
                    else {
                        fleet[h][1] += temp1; //bicis que sale de origen
                        fleet[h][5] = temp1; //bicis que sale de origen
                        bikesNeeded[i] -= temp1;
                        bikesNeeded[j] += temp1;
                    }
                    //set suppliedDemand
                    suppliedDemand += Math.abs(fleet[h][5]);
                }
            }
        }
        //set transportCost
        int l = 0;
        while (l < F && fleet[l][1] != -1) {
            transportCost += calcula_cost(fleet[l][0], fleet[l][2], fleet[l][1]);
            if (fleet[l][4] != -1)
                transportCost += calcula_cost(fleet[l][2], fleet[l][4], fleet[l][3]);
            ++l;
        }
        //set benefit
        benefit = suppliedDemand - transportCost;

        System.out.println("benefit: " + benefit + " transportCost: " + transportCost + " suppliedDemand: " + suppliedDemand);
    }

    public double calcula_cost(int i, int j, int num_bicis) {
        int cost_km = (num_bicis+9)/10;
        int distance = getEuclideanDistance(stations.get(i).getCoordX(), stations.get(i).getCoordY(), stations.get(j).getCoordX(), stations.get(j).getCoordY());
        distance = distance/1000;
        return cost_km*distance;
    }

    // Auxiliary functions
    private int getEuclideanDistance(int originX, int originY, int destX, int destY) {
        return Math.abs(destX - originX) + Math.abs(destY - originY);
    }

    private double getVanTransportCost(int originId, int destId, int bikesTaken) {
        Estacion origin = stations.get(originId);
        Estacion dest = stations.get(destId);
        int distance = getEuclideanDistance(origin.getCoordX(), origin.getCoordY(), dest.getCoordX(), dest.getCoordY());
        int cost_km = (bikesTaken+9)/10;
        distance /= 1000;
        return cost_km*distance;
    }
}
