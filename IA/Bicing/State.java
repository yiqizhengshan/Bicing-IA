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

    /* ------------------- Constructor ------------------- */

    public State(int n_van, Estaciones Est) {
        State.stations = Est;
        State.F = n_van;
        State.E = Est.size();

        initializeFleet();
        initializeIsStationVisited();
        initializeBikesNeeded();

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
    }

    /* ------------------- Operators ------------------- */
    public void changeDestination1(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int bikesTaken = fleet[vanId][1];
        int dest1Id = fleet[vanId][2];
        int numBikesLeftDest1 = fleet[vanId][3];
        int dest2Id = fleet[vanId][4];
        int numBikesLeftDest2 = fleet[vanId][5];
        Boolean newDestNeedsBikes = bikesNeeded[newDestId] > 0; //it was <, now >. Is the only change in thos function

        if (newDestId == originId || !newDestNeedsBikes) return;
        // If we want to eliminate 1st destination, we can't have a
        // second destination
        if (newDestId == -1 && dest2Id != -1) return;
        System.out.print("!Applying changeDest1 operator!\n");

        // Reset transport cost
        double previousTransportCost1 = 0;
        double previousTransportCost2 = 0;
        if (dest1Id != -1) previousTransportCost1 = getVanTransportCost(originId, dest1Id, bikesTaken);
        if (dest2Id != -1 && dest1Id != -1) previousTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost -= (previousTransportCost1 + previousTransportCost2);

        // Reset supplied demand
        if (dest1Id != -1) this.suppliedDemand -= numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand -= numBikesLeftDest2;

        // Reset bikesNeeded
        if (dest1Id != -1) bikesNeeded[dest1Id] += numBikesLeftDest1;
        if (dest2Id != -1) bikesNeeded[dest2Id] += numBikesLeftDest2;

        // Update van's dest1 and bikesLeft at each dest
        fleet[vanId][2] = dest1Id = newDestId;
        if (dest1Id != -1) fleet[vanId][3] = numBikesLeftDest1 = Math.max(Math.min(bikesTaken, bikesNeeded[newDestId]), 0);
        if (dest2Id != -1) fleet[vanId][5] = numBikesLeftDest2 = Math.max(Math.min(bikesTaken - numBikesLeftDest1, bikesNeeded[dest2Id]), 0);
        
        // Update bikesNeeded
        if (dest1Id != -1) bikesNeeded[dest1Id] -= numBikesLeftDest1;
        if (dest2Id != -1) bikesNeeded[dest2Id] -= numBikesLeftDest2;

        if (fleet[vanId][3] == 0) {
            fleet[vanId][3] = numBikesLeftDest1 = -1; //convenio de no primer destino
            fleet[vanId][2] = dest1Id = -1;
        }
        if (fleet[vanId][5] == 0) {
            fleet[vanId][5] = numBikesLeftDest2 = -1; //convenio de no segundo destino
            fleet[vanId][4] = dest2Id = -1;
        }
        
        // Update transport cost
        double newTransportCost1 = 0;
        if (dest1Id != -1) newTransportCost1 = getVanTransportCost(originId, newDestId, bikesTaken);
        double newTransportCost2 = 0;
        if (dest2Id != -1) newTransportCost2 = getVanTransportCost(newDestId, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost += (newTransportCost1 + newTransportCost2);

        // Update supplied demand
        if (dest1Id != -1) this.suppliedDemand += numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand += numBikesLeftDest2;

        // Calculate new benefit
        this.benefit = (double) suppliedDemand - transportCost;

        System.out.print("!Operator changeDest1 applied!\n");
        printState();
    }

    public void changeDestination2(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int bikesTaken = fleet[vanId][1];
        int dest1Id = fleet[vanId][2];
        int numBikesLeftDest1 = fleet[vanId][3];
        int dest2Id = fleet[vanId][4];
        int numBikesLeftDest2 = fleet[vanId][5];
        Boolean newDestNeedsBikes = bikesNeeded[newDestId] > 0;

        System.out.print("!Applying changeDest2 operator!\n");
        // System.out.print("----Applying operator changeDestination: " + vanId + " " + newDestId + "----\n");
        // System.out.print("Operator not applied\n");
        // printVan(vanId);
        // printState();

        /*
            dest1: no  / dest2: yes ----> no se puede hacer DONE
            dest1: yes / dest2: no -----> meto dest2
            dest1: yes / dest2: yes ----> cambiarlo 
            dest1: yes / dest2: yes ---> eliminar dest2
         */
        if (newDestId == originId || !newDestNeedsBikes) return;
        if (newDestId == dest1Id || newDestId == dest2Id || dest1Id == -1) return;
        
        if (dest2Id != -1) {
            // Reset transport cost
            double previousTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
            this.transportCost -= previousTransportCost2;

            // Reset supplied demand
            this.suppliedDemand -= numBikesLeftDest2;
            
            // Reset bikesNeeded
            bikesNeeded[dest2Id] += numBikesLeftDest2;

            // Reset benefit
            this.benefit = (double) suppliedDemand - transportCost;
        }
        dest2Id = fleet[vanId][4] = newDestId;
        if (dest2Id != -1) {    // Cambiamos dest 2
            // Update van's dest2 and bikesLeft at dest2
            fleet[vanId][5] = numBikesLeftDest2 = Math.min(bikesTaken - numBikesLeftDest1, bikesNeeded[dest2Id]);
            // System.out.print(numBikesLeftDest2 + " " + (bikesTaken - numBikesLeftDest1) + " " + bikesNeeded[dest2Id] + "\n");
            if (fleet[vanId][5] == 0) { // If van has left all bikesTaken to dest1, don't go to dest2
                fleet[vanId][5] = -1; //convenio de no segundo destino
                fleet[vanId][4] = -1;
                // System.out.print("returned\n");
                return;
            }

            // Update bikesNeeded
            bikesNeeded[dest2Id] -= numBikesLeftDest2;

            // Update transport cost
            double newTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
            this.transportCost += newTransportCost2;

            // Update supplied demand
            this.suppliedDemand += numBikesLeftDest2;
            // eso es
            // Calculate new benefit
            this.benefit = (double) suppliedDemand - transportCost;
        }

        // Print fleet
        System.out.print("changeDest2 applied!\n");
        printState();
    }

    public void swapOrigin(int fleetId, int newOriginId) {
        // Condition: newOriginId != dest1Id && newOriginId != dest2Id and newOriginId has excess bikes
        // !Problem: how to handle the case where origin has no sufficient excess bikes for
        // both destinations?
        int originId = fleet[fleetId][0];
        int bikesTaken = fleet[fleetId][1];
        int dest1Id = fleet[fleetId][2];
        int numBikesLeftDest1 = fleet[fleetId][3];
        int dest2Id = fleet[fleetId][4];
        int numBikesLeftDest2 = fleet[fleetId][5];

        //can't swap if it has no fleet
        int fleet2Id = isStationVisited[newOriginId];
        if (fleet2Id == -1) return;

        //same origin
        if (newOriginId == originId) return;

        
        //fleet2Id data
        int originId2 = fleet[fleet2Id][0];
        int bikesTaken2 = fleet[fleet2Id][1];
        int dest1Id2 = fleet[fleet2Id][2];
        int numBikesLeftDest12 = fleet[fleet2Id][3];
        int dest2Id2 = fleet[fleet2Id][4];
        int numBikesLeftDest22 = fleet[fleet2Id][5];

        System.out.print("!Applying swapOrigin operator!\n");

        // Reset transport cost of both vans
        double previousTransportCost1 = 0;
        double previousTransportCost2 = 0;
        if (dest1Id != -1) previousTransportCost1 = getVanTransportCost(originId, dest1Id, bikesTaken);
        if (dest2Id != -1) previousTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost -= (previousTransportCost1 + previousTransportCost2);

        double previousTransportCost12 = 0;
        double previousTransportCost22 = 0;
        if (dest1Id2 != -1) previousTransportCost12 = getVanTransportCost(originId2, dest1Id2, bikesTaken2);
        if (dest2Id2 != -1) previousTransportCost22 = getVanTransportCost(dest1Id2, dest2Id2, bikesTaken2 - numBikesLeftDest12);
        this.transportCost -= (previousTransportCost12 + previousTransportCost22);

        // Reset supplied demand of both vans
        if (dest1Id != -1) this.suppliedDemand -= numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand -= numBikesLeftDest2;

        if (dest1Id2 != -1) this.suppliedDemand -= numBikesLeftDest12;
        if (dest2Id2 != -1) this.suppliedDemand -= numBikesLeftDest22;

        // Reset bikesNeeded of both vans
        if (dest1Id != -1) bikesNeeded[dest1Id] += numBikesLeftDest1;
        if (dest2Id != -1) bikesNeeded[dest2Id] += numBikesLeftDest2;

        if (dest1Id2 != -1) bikesNeeded[dest1Id2] += numBikesLeftDest12;
        if (dest2Id2 != -1) bikesNeeded[dest2Id2] += numBikesLeftDest22;

        //Reset bikesTaken of both vans
        bikesNeeded[originId] -= bikesTaken;
        bikesNeeded[originId2] -= bikesTaken2;
        bikesTaken = fleet[fleet2Id][1] = -bikesNeeded[originId2]; // it would be this or 0?
        bikesTaken2 = fleet[fleetId][1] = -bikesNeeded[originId];

        //Update isStationVisited of both vans
        isStationVisited[originId] = fleet2Id;
        isStationVisited[originId2] = fleetId;
        fleet[fleetId][0] = originId2;
        fleet[fleet2Id][0] = originId;

        // Update fleetId bikesLeft and bikesNeeded at each dest
        if (dest1Id != -1) {
            int temp1 =  Math.abs(bikesNeeded[newOriginId]);
            int temp2 = bikesNeeded[dest1Id];
            if (temp1 > temp2) { //sobran mas de las que faltan
                fleet[fleetId][1] = bikesTaken = temp2; //bicis que sale de origen
                fleet[fleetId][3] = numBikesLeftDest1 = temp2; //bicis que llegan a destino
                bikesNeeded[newOriginId] += temp2;
                bikesNeeded[dest1Id] -= temp2;
            }
            else { //faltan mas de las que sobran
                fleet[fleetId][1] = temp1; //bicis que sale de origen
                fleet[fleetId][3] = numBikesLeftDest1 = temp1; //bicis que llegan a destino
                bikesNeeded[newOriginId] += temp1;
                bikesNeeded[dest1Id] -= temp1;
            }
        }
        if (dest2Id != -1) {
            int temp1 = Math.abs(bikesNeeded[newOriginId]);
            int temp2 = bikesNeeded[dest2Id];
            if (temp1 > temp2) { //sobran mas de las que faltan
                fleet[fleetId][1] += temp1; //bicis que sale de origen
                bikesTaken += temp1;
                fleet[fleetId][5] = numBikesLeftDest2 = temp2; //bicis que sale de origen
                bikesNeeded[newOriginId] += temp1; //ponemos a cero porque ya ha hecho 2 destinos y aun le sobran
                bikesNeeded[dest2Id] -= temp2;
            }
            else {
                fleet[fleetId][1] += temp1; //bicis que sale de origen
                bikesTaken += temp1;
                fleet[fleetId][5] = numBikesLeftDest2 = temp1; //bicis que sale de origen
                bikesNeeded[newOriginId] += temp1;
                bikesNeeded[dest2Id] -= temp1;
            }
        }

        // Update fleet2Id bikesLeft and bikesNeeded at each dest
        if (dest1Id2 != -1) {
            int temp1 =  Math.abs(bikesNeeded[originId]);
            int temp2 = bikesNeeded[dest1Id2];
            if (temp1 > temp2) { //sobran mas de las que faltan
                fleet[fleet2Id][1] = bikesTaken2 = temp2; //bicis que sale de origen
                fleet[fleet2Id][3] = numBikesLeftDest12 = temp2; //bicis que llegan a destino
                bikesNeeded[originId] += temp2;
                bikesNeeded[dest1Id2] -= temp2;
            }
            else { //faltan mas de las que sobran
                fleet[fleet2Id][1] = temp1; //bicis que sale de origen
                fleet[fleet2Id][3] = numBikesLeftDest12 = temp1; //bicis que llegan a destino
                bikesNeeded[originId] += temp1;
                bikesNeeded[dest1Id2] -= temp1;
            }
        }
        if (dest2Id2 != -1) {
            int temp1 = Math.abs(bikesNeeded[originId]);
            int temp2 = bikesNeeded[dest2Id2];
            if (temp1 > temp2) { //sobran mas de las que faltan
                fleet[fleet2Id][1] += temp1; //bicis que sale de origen
                bikesTaken2 += temp1;
                fleet[fleet2Id][5] = numBikesLeftDest22 = temp2; //bicis que sale de origen
                bikesNeeded[originId] += temp1; //ponemos a cero porque ya ha hecho 2 destinos y aun le sobran
                bikesNeeded[dest2Id2] -= temp2;
            }
            else {
                fleet[fleet2Id][1] += temp1; //bicis que sale de origen
                bikesTaken2 += temp1;
                fleet[fleet2Id][5] = numBikesLeftDest22 = temp1; //bicis que sale de origen
                bikesNeeded[originId] += temp1;
                bikesNeeded[dest2Id2] -= temp1;
            }
        }

        if (fleet[fleetId][3] == 0) {
            fleet[fleetId][3] = numBikesLeftDest1 = -1; //convenio de no primer destino
            fleet[fleetId][2] = dest1Id = -1;
        }
        if (fleet[fleetId][5] == 0) {
            fleet[fleetId][5] = numBikesLeftDest2 = -1; //convenio de no segundo destino
            fleet[fleetId][4] = dest2Id = -1;
        }
        if (fleet[fleet2Id][3] == 0) {
            fleet[fleet2Id][3] = numBikesLeftDest12 = -1; //convenio de no primer destino
            fleet[fleet2Id][2] = dest1Id2 = -1;
        }
        if (fleet[fleet2Id][5] == 0) {
            fleet[fleet2Id][5] = numBikesLeftDest22 = -1; //convenio de no segundo destino
            fleet[fleet2Id][4] = dest2Id2 = -1;
        }
        
        // Update transport cost
        double newTransportCost1 = 0;
        if (dest1Id != -1) newTransportCost1 = getVanTransportCost(originId2, dest1Id, bikesTaken);
        double newTransportCost2 = 0;
        if (dest2Id != -1) newTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost += (newTransportCost1 + newTransportCost2);

        double newTransportCost12 = 0;
        if (dest1Id2 != -1) newTransportCost12 = getVanTransportCost(originId, dest1Id2, bikesTaken2);
        double newTransportCost22 = 0;
        if (dest2Id2 != -1) newTransportCost22 = getVanTransportCost(dest1Id2, dest2Id2, bikesTaken2 - numBikesLeftDest12);
        this.transportCost += (newTransportCost12 + newTransportCost22);

        // Update supplied demand
        if (dest1Id != -1) this.suppliedDemand += numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand += numBikesLeftDest2;
        
        if (dest1Id2 != -1) this.suppliedDemand += numBikesLeftDest12;
        if (dest2Id2 != -1) this.suppliedDemand += numBikesLeftDest22;

        // Calculate new benefit
        this.benefit = (double) suppliedDemand - transportCost;

        System.out.print("!Operator swapOrigin applied!\n");
        printState();
    }

    public void changeOrigin(int fleetId, int newOriginId) {
        // Condition: newOriginId != dest1Id && newOriginId != dest2Id and newOriginId has excess bikes
        // !Problem: how to handle the case where origin has no sufficient excess bikes for
        // both destinations?
        int originId = fleet[fleetId][0];
        int bikesTaken = fleet[fleetId][1];
        int dest1Id = fleet[fleetId][2];
        int numBikesLeftDest1 = fleet[fleetId][3];
        int dest2Id = fleet[fleetId][4];
        int numBikesLeftDest2 = fleet[fleetId][5];
        Boolean newDestNeedsBikes = bikesNeeded[newOriginId] > 0; //it was <, now >. Is the only change in thos function

        //can't change if newOriginId has fleet, or if it's the same origin, or if it's the same dest1, or if it's the same dest2, or it has no excess bikes
        if (newOriginId == originId || newDestNeedsBikes || newOriginId == -1 || newOriginId == dest1Id || newOriginId == dest2Id || isStationVisited[newOriginId] != -1) return;

        System.out.print("!Applying changeOrigin operator!\n");

        // Reset transport cost
        double previousTransportCost1 = 0;
        double previousTransportCost2 = 0;
        if (dest1Id != -1) previousTransportCost1 = getVanTransportCost(originId, dest1Id, bikesTaken);
        if (dest2Id != -1) previousTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost -= (previousTransportCost1 + previousTransportCost2);

        // Reset supplied demand
        if (dest1Id != -1) this.suppliedDemand -= numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand -= numBikesLeftDest2;

        // Reset bikesNeeded
        if (dest1Id != -1) bikesNeeded[dest1Id] += numBikesLeftDest1;
        if (dest2Id != -1) bikesNeeded[dest2Id] += numBikesLeftDest2;

        //Reset bikesTaken
        bikesNeeded[originId] -= bikesTaken;
        bikesTaken = fleet[fleetId][1] = -bikesNeeded[newOriginId]; // it would be this or 0?

        //Update isStationVisited and originId
        isStationVisited[originId] = -1;
        fleet[fleetId][0] = originId = newOriginId;
        isStationVisited[originId] = fleetId;

        // Update fleetId bikesLeft and bikesNeeded at each dest
        if (dest1Id != -1) {
            int temp1 =  Math.abs(bikesNeeded[newOriginId]);
            int temp2 = bikesNeeded[dest1Id];
            if (temp1 > temp2) { //sobran mas de las que faltan
                fleet[fleetId][1] = bikesTaken = temp2; //bicis que sale de origen
                fleet[fleetId][3] = numBikesLeftDest1 = temp2; //bicis que llegan a destino
                bikesNeeded[newOriginId] += temp2;
                bikesNeeded[dest1Id] -= temp2;
            }
            else { //faltan mas de las que sobran
                fleet[fleetId][1] = temp1; //bicis que sale de origen
                fleet[fleetId][3] = numBikesLeftDest1 = temp1; //bicis que llegan a destino
                bikesNeeded[newOriginId] += temp1;
                bikesNeeded[dest1Id] -= temp1;
            }
        }
        if (dest2Id != -1) {
            int temp1 = Math.abs(bikesNeeded[newOriginId]);
            int temp2 = bikesNeeded[dest2Id];
            if (temp1 > temp2) { //sobran mas de las que faltan
                fleet[fleetId][1] += temp1; //bicis que sale de origen
                bikesTaken += temp1;
                fleet[fleetId][5] = numBikesLeftDest2 = temp2; //bicis que sale de origen
                bikesNeeded[newOriginId] += temp1; //ponemos a cero porque ya ha hecho 2 destinos y aun le sobran
                bikesNeeded[dest2Id] -= temp2;
            }
            else {
                fleet[fleetId][1] += temp1; //bicis que sale de origen
                bikesTaken += temp1;
                fleet[fleetId][5] = numBikesLeftDest2 = temp1; //bicis que sale de origen
                bikesNeeded[newOriginId] += temp1;
                bikesNeeded[dest2Id] -= temp1;
            }
        }

        if (fleet[fleetId][3] == 0) {
            fleet[fleetId][3] = numBikesLeftDest1 = -1; //convenio de no primer destino
            fleet[fleetId][2] = dest1Id = -1;
        }
        if (fleet[fleetId][5] == 0) {
            fleet[fleetId][5] = numBikesLeftDest2 = -1; //convenio de no segundo destino
            fleet[fleetId][4] = dest2Id = -1;
        }
        
        // Update transport cost
        double newTransportCost1 = 0;
        if (dest1Id != -1) newTransportCost1 = getVanTransportCost(originId, dest1Id, bikesTaken);
        double newTransportCost2 = 0;
        if (dest2Id != -1) newTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost += (newTransportCost1 + newTransportCost2);

        // Update supplied demand
        if (dest1Id != -1) this.suppliedDemand += numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand += numBikesLeftDest2;

        // Calculate new benefit
        this.benefit = (double) suppliedDemand - transportCost;

        System.out.print("!Operator swapOrigin applied!\n");
        printState();
    }

    public void no_cojer_tantas_biciletas(int originid, int dest1Id, int dest2Id) {
        
    }
    

    /* ------------------- Heuristic function ------------------- */
    public double heuristic() {
        return -this.benefit;
    }

    /* ------------------- Goal test ------------------- */
    public boolean is_goal() {
        return false;
        // State does not have better successor states
    }

    /* ------------------- Getters ------------------- */

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

    /* ------------------- Setters ------------------- */
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

    /* ------------------- Initializers ------------------- */

    private void initializeFleet() {
        this.fleet = new int[State.F][6];
        for (int i = 0; i < State.F; ++i) {
            for (int j = 0; j < 6; ++j) {
                this.fleet[i][j] = -1;
            }
        }
    }

    private void initializeIsStationVisited() {
        this.isStationVisited = new int[State.E];
        for (int i = 0; i < State.E; ++i) {
            this.isStationVisited[i] = -1;
        }
    }

    private void initializeBikesNeeded() {
        this.bikesNeeded = new int[State.E]; //E = num estaciones
        for (int i = 0; i < State.E; i++) {
            int aux = State.stations.get(i).getDemanda() - State.stations.get(i).getNumBicicletasNext();
            if (aux > 0) { // no cumplo la demanda de la hora siguiente con bicis next
                int sobran = aux - State.stations.get(i).getNumBicicletasNoUsadas();
                bikesNeeded[i] = sobran;
            }
            else { //si cumplo la demanda de la hora siguiente con bicis next
                bikesNeeded[i] = Math.max(-State.stations.get(i).getNumBicicletasNoUsadas(), -30);
            }
        }
    }

    public void initialize_easy() {
        // Estrategia: Ordenar estaciones de más sobran bicis a menos
        // Origen: Assignar para cada furgoneta, un origen segun las estaciones ordenadas
        // Destino: Totalmente random, mientras no coincida con origen i el otro destino
        int[] increasingBikesNeededStationId = new int[State.E];
        for (int i = 0; i < E; ++i) increasingBikesNeededStationId[i] = i;

        // Sort
        for (int i = 0; i < E; ++i) {
            int minIndex = i;
            for (int j = i + 1; j < E; ++j) {
                if (bikesNeeded[increasingBikesNeededStationId[j]] < bikesNeeded[increasingBikesNeededStationId[minIndex]]) {
                    minIndex = j;
                }
            }
            int temp = increasingBikesNeededStationId[i];
            increasingBikesNeededStationId[i] = increasingBikesNeededStationId[minIndex];
            increasingBikesNeededStationId[minIndex] = temp;
        }
        int originId = 0;
        for (int vanId = 0; vanId < F; vanId++) {
            fleet[vanId][0] = originId;
            fleet[vanId][1] = Math.max(-bikesNeeded[originId], 0);
            ++originId;
        }
        for (int dest1Id = E - 1; dest1Id >= 0; --dest1Id) {
            // Math.min()
            // fleet[vanId][3]
        }
    }

    public void initialize_medium() {
        int size_fleet = F;
        int i = 0;
        int j = 0;

        while (i < E && j < E && size_fleet >= 0) {
            i = 0;
            j = 0;

            printBikesNeeded();

            //llegas al primer negativo
            while (i < E && bikesNeeded[i] >= 0) {
                ++i;
            }
            //llegas al primer positivo
            while (j < E && bikesNeeded[j] <= 0) {
                ++j;
            }
            
            if (i == E || j == E) continue;
            //System.out.println(E + " " + i + " " + j + " " + size_fleet);
            
            // System.out.println("i: " + i + " j: " + j + " size_fleet: " + size_fleet);
            

            //se usa de origen?
            if (isStationVisited[i] == -1 && size_fleet > 0) {
                int k = F - size_fleet;
                isStationVisited[i] = k;
                
                fleet[k][0] = i; //origen
                int temp1 =  Math.abs(bikesNeeded[i]);
                int temp2 = bikesNeeded[j];
                if (temp1 > temp2) { //sobran mas de las que faltan
                    fleet[k][1] = temp2; //bicis que sale de origen
                    fleet[k][3] = temp2; //bicis que llegan a destino
                    bikesNeeded[i] += temp2;
                    bikesNeeded[j] -= temp2;
                }
                else { //faltan mas de las que sobran
                    fleet[k][1] = temp1; //bicis que sale de origen
                    fleet[k][3] = temp1; //bicis que llegan a destino
                    bikesNeeded[i] += temp1;
                    bikesNeeded[j] -= temp1;
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
                    int temp1 = Math.abs(bikesNeeded[i]);
                    int temp2 = bikesNeeded[j];
                    if (temp1 > temp2) { //sobran mas de las que faltan
                        fleet[h][1] += temp1; //bicis que sale de origen
                        fleet[h][5] = temp2; //bicis que sale de origen
                        bikesNeeded[i] += temp1; //ponemos a cero porque ya ha hecho 2 destinos y aun le sobran
                        bikesNeeded[j] -= temp2;
                    }
                    else {
                        fleet[h][1] += temp1; //bicis que sale de origen
                        fleet[h][5] = temp1; //bicis que sale de origen
                        bikesNeeded[i] += temp1;
                        bikesNeeded[j] -= temp1;
                    }
                    //set suppliedDemand
                    suppliedDemand += fleet[h][5];
                    
                    if (size_fleet == 0) --size_fleet;
                }
            }
        }
        // printBikesNeeded();
        // System.out.println("i: " + i + " j: " + j + " size_fleet: " + size_fleet);

        //set transportCost
        int l = 0;
        while (l < F && fleet[l][1] != -1) {
            transportCost += calcula_cost(fleet[l][0], fleet[l][2], fleet[l][1]);
            if (fleet[l][4] != -1) transportCost += calcula_cost(fleet[l][2], fleet[l][4], fleet[l][1] - fleet[l][3]);
            ++l;
        }
        //set benefit
        benefit = suppliedDemand - transportCost;

        printState();
    }

    /* Auxiliary functions */

    public double calcula_cost(int i, int j, int num_bicis) {
        int cost_km = (num_bicis+9)/10;
        int distance = getEuclideanDistance(stations.get(i).getCoordX(), stations.get(i).getCoordY(), stations.get(j).getCoordX(), stations.get(j).getCoordY());
        distance = distance/1000;
        return cost_km*distance;
    }

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

    private void printVan(int vanId) {
        for (int j = 0; j < 6; ++j) {
            System.out.print(fleet[vanId][j] + " ");
        }
        System.out.println();
    }

    private void printBikesNeeded() {
        for (int i = 0; i < E; ++i) {
            System.out.print(bikesNeeded[i] + " ");
        }
        System.out.println();
    }
    public void printState() {
        System.out.println("benefit: " + benefit + " transportCost: " + transportCost + " suppliedDemand: " + suppliedDemand + "\n");
    }
}
