// State.java
package IA.Bicing;
import java.util.ArrayList;

/**
 * Created by bejar on 17/01/17.
 */
public class State {
    // originId, bikesTaken, destId1, numBikesLeftDest1, destId2, numBikesLeftDest2
    private int[][] fleet;
    private int[] isStationVisited; // if not visited, -1
                                    // else, contains the id of the van that visits it
    private int[] bikesNeeded;
    private boolean[] two_destinations; // if van has 2 destinations, true
                                        // else, false

    private double transportCost;
    private int suppliedDemand; //

    private static Estaciones stations;
    private static int F; // Max Furgonetas
    private static int E; // Number of stations
    private static int heuristicType;

    /* ------------------- Constructor ------------------- */

    public State(int n_van, Estaciones Est, int heuristicType) {
        State.stations = Est;
        State.F = n_van;
        State.E = Est.size();

        initializeFleet();
        initializeIsStationVisited();
        initializeBikesNeeded();
        intializeTwoDestinations();

        this.transportCost = 0;
        this.suppliedDemand = 0;
        State.heuristicType = heuristicType;
    }

    // Copy constructor
    public State(final State state) {
        this.setFleet(state.getFleet());
        this.setIsStationVisited(state.getIsStationVisited());
        this.setBikesNeeded(state.getBikesNeeded());

        this.setTransportCost(state.getTransportCost());
        this.setSuppliedDemand(state.getSuppliedDemand());
    }

    /* ------------------- Operators ------------------- */
    public void substractVan(int vanId, int count) {
        int originId = fleet[vanId][0];
        int bikesTaken = fleet[vanId][1] - count;
        int dest1Id = fleet[vanId][2];
        int numBikesLeftDest1 = fleet[vanId][3];
        int dest2Id = fleet[vanId][4];
        int numBikesLeftDest2 = fleet[vanId][5];

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
        if (originId != -1) bikesNeeded[originId] -= bikesTaken;
        if (dest1Id != -1) bikesNeeded[dest1Id] += numBikesLeftDest1;
        if (dest2Id != -1) bikesNeeded[dest2Id] += numBikesLeftDest2;

        // Update bikesTaken and fleet[vanId][1]
        if (originId != -1) bikesTaken = -bikesNeeded[originId] - count;

        // Update van's dest1 and bikesLeft at each dest
        if (dest1Id != -1) fleet[vanId][3] = numBikesLeftDest1 = Math.max(Math.min(bikesTaken, bikesNeeded[dest1Id]), 0);
        if (dest2Id != -1) fleet[vanId][5] = numBikesLeftDest2 = Math.max(Math.min(bikesTaken - numBikesLeftDest1, bikesNeeded[dest2Id]), 0);
        
        // Update bikesNeeded
        if (originId != -1) {
            bikesNeeded[originId] += numBikesLeftDest1;
            bikesTaken = fleet[vanId][1] = numBikesLeftDest1;
            if (dest2Id != -1) {
                bikesNeeded[originId] += numBikesLeftDest2;
                bikesTaken = fleet[vanId][1] += numBikesLeftDest2;
            }
        }
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
        if (dest1Id != -1) newTransportCost1 = getVanTransportCost(originId, dest1Id, bikesTaken);
        double newTransportCost2 = 0;
        if (dest2Id != -1) newTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost += (newTransportCost1 + newTransportCost2);

        // Update supplied demand
        if (dest1Id != -1) this.suppliedDemand += numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand += numBikesLeftDest2;
    }

    public Boolean isChangeDestination1ConditionOkay(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int dest1Id = fleet[vanId][2];
        int dest2Id = fleet[vanId][4];
        if (originId == -1) return false;
        if (newDestId == originId) return false;
        if (newDestId == dest1Id) return false;
        if (newDestId == -1 && dest2Id != -1) return false;
        if (newDestId != -1 && bikesNeeded[newDestId] <= 0) return false;
        return true;
    }

    public void changeDestination1(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int bikesTaken = fleet[vanId][1];
        int dest1Id = fleet[vanId][2];
        int numBikesLeftDest1 = fleet[vanId][3];
        int dest2Id = fleet[vanId][4];
        int numBikesLeftDest2 = fleet[vanId][5];

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
        if (originId != -1) bikesNeeded[originId] -= bikesTaken;
        if (dest1Id != -1) bikesNeeded[dest1Id] += numBikesLeftDest1;
        if (dest2Id != -1) bikesNeeded[dest2Id] += numBikesLeftDest2;

        // Update bikesTaken and fleet[vanId][1]
        if (originId != -1) bikesTaken = -bikesNeeded[originId];

        // Update van's dest1 and bikesLeft at each dest
        fleet[vanId][2] = dest1Id = newDestId;
        if (dest1Id != -1) fleet[vanId][3] = numBikesLeftDest1 = Math.max(Math.min(bikesTaken, bikesNeeded[newDestId]), 0);
        if (dest2Id != -1) fleet[vanId][5] = numBikesLeftDest2 = Math.max(Math.min(bikesTaken - numBikesLeftDest1, bikesNeeded[dest2Id]), 0);
        
        // Update bikesNeeded
        if (originId != -1) {
            bikesNeeded[originId] += numBikesLeftDest1;
            bikesTaken = fleet[vanId][1] = numBikesLeftDest1;
            if (dest2Id != -1) {
                bikesNeeded[originId] += numBikesLeftDest2;
                bikesTaken = fleet[vanId][1] += numBikesLeftDest2;
            }
        }
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

        this.transportCost = 0.0;
        for (int vanid = 0; vanid < F; ++vanid) {
            int originid = fleet[vanid][0];
            int bikestaken = fleet[vanid][1];
            int dest1id = fleet[vanid][2];
            int numbikesLeftDest1 = fleet[vanid][3];
            int dest2id = fleet[vanid][4];
            int numbikesLeftDest2 = fleet[vanid][5];

            if (dest1id != -1)  this.transportCost += getVanTransportCost(originid, dest1id, bikestaken);
            if (dest1id != -1 && dest2id != -1) this.transportCost += getVanTransportCost(dest1id, dest2id, bikestaken - numbikesLeftDest1);
        }
    }

    public Boolean isChangeDestination2ConditionOkay(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int dest1Id = fleet[vanId][2];
        int dest2Id = fleet[vanId][4];
        if (originId == -1) return false;
        if (newDestId == originId) return false;
        if (newDestId == dest1Id) return false;
        if (newDestId == dest2Id) return false;
        if (dest1Id == -1) return false;
        if (newDestId != -1 && bikesNeeded[newDestId] <= 0) return false;
        return true;
    }

    public void changeDestination2(int vanId, int newDestId) {
        int originId = fleet[vanId][0];
        int bikesTaken = fleet[vanId][1];
        int dest1Id = fleet[vanId][2];
        int numBikesLeftDest1 = fleet[vanId][3];
        int dest2Id = fleet[vanId][4];
        int numBikesLeftDest2 = fleet[vanId][5];

        if (dest2Id != -1) {
            // Reset transport cost
            double previousTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
            this.transportCost -= previousTransportCost2;

            // Reset supplied demand
            this.suppliedDemand -= numBikesLeftDest2;
            
            // Reset bikesNeeded
            bikesNeeded[originId] -= numBikesLeftDest2;
            bikesNeeded[dest2Id] += numBikesLeftDest2;

            // Reset bikesTaken
            bikesTaken -= numBikesLeftDest2;
        }
        dest2Id = fleet[vanId][4] = newDestId;
        if (dest2Id != -1) {    // Cambiamos dest 2
            // Update bikesTaken and fleet[vanId][1]
            bikesTaken += -bikesNeeded[originId];

            // Update van's dest2 and bikesLeft at dest2
            fleet[vanId][5] = numBikesLeftDest2 = Math.min(bikesTaken - numBikesLeftDest1, bikesNeeded[dest2Id]);

            // Update bikesNeeded, bikesTaken, 
            bikesNeeded[originId] += numBikesLeftDest2;
            bikesTaken = fleet[vanId][1] = numBikesLeftDest1 + numBikesLeftDest2;

            if (fleet[vanId][5] == 0) { // If van has left all bikesTaken to dest1, don't go to dest2
                fleet[vanId][5] = -1; //convenio de no segundo destino
                fleet[vanId][4] = -1;
                return;
            }

            // Update bikesNeeded
            bikesNeeded[dest2Id] -= numBikesLeftDest2;

            // Update transport cost
            double newTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
            this.transportCost += newTransportCost2;

            // Update supplied demand
            this.suppliedDemand += numBikesLeftDest2;
        }

        // Calculate transport cost
        this.transportCost = 0.0;
        for (int vanid = 0; vanid < F; ++vanid) {
            int originid = fleet[vanid][0];
            int bikestaken = fleet[vanid][1];
            int dest1id = fleet[vanid][2];
            int numbikesLeftDest1 = fleet[vanid][3];
            int dest2id = fleet[vanid][4];

            if (dest1id != -1)  this.transportCost += getVanTransportCost(originid, dest1id, bikestaken);
            if (dest1id != -1 && dest2id != -1) this.transportCost += getVanTransportCost(dest1id, dest2id, bikestaken - numbikesLeftDest1);
        }
    }

    public Boolean isChangeOriginConditionOkay(int fleetId, int newOriginId) {
        int originId = fleet[fleetId][0];
        int dest1Id = fleet[fleetId][2];
        int dest2Id = fleet[fleetId][4];
        Boolean newDestNeedsBikes = bikesNeeded[newOriginId] >= 0;
        if (newOriginId == originId || newDestNeedsBikes || newOriginId == -1 || newOriginId == dest1Id || newOriginId == dest2Id || isStationVisited[newOriginId] != -1) return false;
        return true;
    }

    public void changeOrigin(int fleetId, int newOriginId) {
        int originId = fleet[fleetId][0];
        int bikesTaken = fleet[fleetId][1];
        int dest1Id = fleet[fleetId][2];
        int numBikesLeftDest1 = fleet[fleetId][3];
        int dest2Id = fleet[fleetId][4];
        int numBikesLeftDest2 = fleet[fleetId][5];

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
                fleet[fleetId][1] += temp2; //bicis que sale de origen
                bikesTaken += temp2;
                fleet[fleetId][5] = numBikesLeftDest2 = temp2; //bicis que sale de origen
                bikesNeeded[newOriginId] += temp2; //ponemos a cero porque ya ha hecho 2 destinos y aun le sobran
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
        if (dest2Id != -1 && dest1Id != -1) newTransportCost2 = getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        this.transportCost += (newTransportCost1 + newTransportCost2);

        // Update supplied demand
        if (dest1Id != -1) this.suppliedDemand += numBikesLeftDest1;
        if (dest2Id != -1) this.suppliedDemand += numBikesLeftDest2;

        this.transportCost = 0.0;
        for (int vanid = 0; vanid < F; ++vanid) {
            int originid = fleet[vanid][0];
            int bikestaken = fleet[vanid][1];
            int dest1id = fleet[vanid][2];
            int numbikesLeftDest1 = fleet[vanid][3];
            int dest2id = fleet[vanid][4];

            if (dest1id != -1)  this.transportCost += getVanTransportCost(originid, dest1id, bikestaken);
            if (dest1id != -1 && dest2id != -1) this.transportCost += getVanTransportCost(dest1id, dest2id, bikestaken - numbikesLeftDest1);
        }
    }

    /* ------------------- Heuristic function ------------------- */
    public double heuristic() {
        return -(this.getBenefit());
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
        if (State.heuristicType == 1) {
            return this.suppliedDemand;
        }
        return this.suppliedDemand - this.transportCost;
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

    public boolean [] getTwoDestinations() {
        return this.two_destinations;
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

    public void setSuppliedDemand(final int suppliedDemand) {
        this.suppliedDemand = suppliedDemand;
    }

    public void setTwoDestinations(final boolean[] two_destinations) {
        int E = two_destinations.length;
        this.two_destinations = new boolean[E];
        for (int i = 0; i < E; ++i) {
            this.two_destinations[i] = two_destinations[i];
        }
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

    private void intializeTwoDestinations() {
        this.two_destinations = new boolean[State.E];
        for (int i = 0; i < State.E; ++i) {
            this.two_destinations[i] = false;
        }
    }

    private void initializeBikesNeeded() {
        this.bikesNeeded = new int[State.E]; //E = num estaciones
        for (int i = 0; i < State.E; i++) {
            // int aux = State.stations.get(i).getDemanda() - State.stations.get(i).getNumBicicletasNext();
            // if (aux > 0) { // no cumplo la demanda de la hora siguiente con bicis next
            //     int sobran = aux - State.stations.get(i).getNumBicicletasNoUsadas();
            //     bikesNeeded[i] = sobran;
            // }
            // else { //si cumplo la demanda de la hora siguiente con bicis next
            //     bikesNeeded[i] = Math.max(-State.stations.get(i).getNumBicicletasNoUsadas(), -30);
            // }

            //positivo si sobran, negativo si faltan
            int bicis_sobran = State.stations.get(i).getNumBicicletasNext() - State.stations.get(i).getDemanda();
            if (bicis_sobran > 0) {
                bikesNeeded[i] = -Math.min(Math.min(State.stations.get(i).getNumBicicletasNoUsadas(), bicis_sobran), 30);
            }
            else {
                bikesNeeded[i] = -bicis_sobran;
            }
        }
    }

    public void initialize_easy() {
        // Cojo las bicis no usadas F primeras estaciones
        // Las dejo en las F siguientes estaciones
        
        for (int i = 0; i < F; ++i) { //cojo bicis de las F primeras estaciones
            fleet[i][0] = i;
            fleet[i][1] = stations.get(i).getNumBicicletasNoUsadas();
            bikesNeeded[i] += fleet[i][1];
            if (bikesNeeded[i] > 0) this.suppliedDemand -= fleet[i][1];
            else this.suppliedDemand -= fleet[i][1]-bikesNeeded[i];
        }

        // assigno des de 0 a E-F
        int j = 0;
        for (int i = F; i < E && i < 2*F; ++i) { //las dejo en las F siguientes estaciones 2*F > E;
            fleet[i-F][2] = i;
            fleet[i-F][3] = fleet[i-F][1];

            if (bikesNeeded[i] > 0) this.suppliedDemand += Math.min(bikesNeeded[i], fleet[i-F][1]);
            bikesNeeded[i] -= fleet[i-F][1];
            this.transportCost += getVanTransportCost(fleet[i-F][0], fleet[i-F][2], fleet[i-F][1]);
            
            ++j;
        }
        // asgino des de F-j a F
        for(int i = 0; i < F - j; ++i) {
            fleet[j+i][2] = i;
            fleet[j+i][3] =  fleet[j+i][1];

            if (bikesNeeded[i] > 0) this.suppliedDemand += Math.min(bikesNeeded[i], fleet[j+i][1]);
            bikesNeeded[j+i] -= fleet[j+i][1];
            this.transportCost += getVanTransportCost(fleet[j+i][0], fleet[j+i][2], fleet[j+i][1]);
        }
    }

    public void initialize_hard() {
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

        //Assignar furgonetas a las estaciones que les sobran más bicis
        //La furgoneta coje todas las que sobran
        for (int i = 0; i < F; i++) {
            int originId = increasingBikesNeededStationId[i];
            fleet[i][0] = originId;
            fleet[i][1] = Math.max(-bikesNeeded[originId], 0); // en el caso de que coje 0 es que no hay mas de F estaciones con bicis sobrantes
            if (fleet[i][1] > 0) bikesNeeded[originId] = 0;
        }

        for (int vanId = 0; vanId < F; ++vanId) {
            for (int j = E - 1; j >= 0 && fleet[vanId][2] == -1; --j) {
                int dest1Id = increasingBikesNeededStationId[j];
                if (bikesNeeded[dest1Id] <= 0) continue;

                fleet[vanId][2] = dest1Id;
                fleet[vanId][3] = Math.min(bikesNeeded[dest1Id], fleet[vanId][1]);
                bikesNeeded[dest1Id] -= fleet[vanId][3];
                this.suppliedDemand += fleet[vanId][3];
                this.transportCost += getVanTransportCost(fleet[vanId][0], dest1Id, fleet[vanId][1]);
            }
        }

        //le tienen que sobrar bicis a la van
        //no puede ir a 
        for (int vanId = 0; vanId < F; ++vanId) {
            if (fleet[vanId][1] - fleet[vanId][3] > 0) {
                for (int j = E - 1; j >= 0 && fleet[vanId][4] == -1; --j) {
                    int dest2Id = increasingBikesNeededStationId[j];
                    if (bikesNeeded[dest2Id] > 0) {
                        fleet[vanId][4] = dest2Id;
                        fleet[vanId][5] = Math.min(bikesNeeded[dest2Id], fleet[vanId][1] - fleet[vanId][3]);
                        bikesNeeded[dest2Id] -= fleet[vanId][5];
                        this.suppliedDemand += fleet[vanId][5];
                        this.transportCost += getVanTransportCost(fleet[vanId][2], dest2Id, fleet[vanId][1] - fleet[vanId][3]);
                    }
                }
            }
        }
    }

    public void initialize_medium() {
        int size_fleet = F;
        int i = 0;
        int j = 0;

        while (i < E && j < E && size_fleet >= 0) {
            i = 0;
            j = 0;
            
            //llegas al primer negativo/bicis sobran y si ya tiene 2 destinos paso al siguiente
            while (i < E && (bikesNeeded[i] >= 0 || two_destinations[i] == true)) {
                ++i;
            }

            //while (i < E && bikesNeeded[i] >= 0) ++i;

            // //en caso que i tiene ya primer destino, la id de la estacion no puede ser j
            int jRepe = -2;
            if (i < E && two_destinations[i] == false && isStationVisited[i] != -1) {
                jRepe = fleet[isStationVisited[i]][2];
            }

            //llegas al primer positivo/bicis faltan
            while (j < E && (bikesNeeded[j] <= 0 || j == jRepe)) {
                ++j;
            }

            //while (j < E && bikesNeeded[j] <= 0) ++j;
            
            if (i == E || j == E) continue;
            //System.out.println(E + " " + i + " " + j + " " + size_fleet);
            
            //System.out.println("i: " + i + " j: " + j + " size_fleet: " + size_fleet);
            
            //System.out.println("after while");

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
                        fleet[h][1] += temp2; //bicis que sale de origen
                        fleet[h][5] = temp2; //bicis que sale de origen
                        bikesNeeded[i] += temp2; //bikesNeeded[i] tendra las que le sobran
                        bikesNeeded[j] -= temp2;
                    }
                    else {
                        fleet[h][1] += temp1; //bicis que sale de origen
                        fleet[h][5] = temp1; //bicis que sale de origen
                        bikesNeeded[i] += temp1;
                        bikesNeeded[j] -= temp1;
                    }
                    two_destinations[i] = true;
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
    }

    /* Auxiliary functions */

    public double calcula_cost(int i, int j, int num_bicis) {
        double cost_km = ((double)num_bicis+9.0)/10.0;
        double distance = getEuclideanDistance(stations.get(i).getCoordX(), stations.get(i).getCoordY(), stations.get(j).getCoordX(), stations.get(j).getCoordY());
        distance = distance/1000.0;
        return cost_km*distance;
    }

    private int getEuclideanDistance(int originX, int originY, int destX, int destY) {
        return Math.abs(destX - originX) + Math.abs(destY - originY);
    }

    private double getVanTransportCost(int originId, int destId, int bikesTaken) {
        Estacion origin = stations.get(originId);
        Estacion dest = stations.get(destId);
        double distance = getEuclideanDistance(origin.getCoordX(), origin.getCoordY(), dest.getCoordX(), dest.getCoordY());
        double cost_km = ((double)bikesTaken+9.0)/10.0;
        distance /= 1000.0;
        return cost_km*distance;
    }

    public double getTotalLength() {
        double total_length = 0;
        for (int i = 0; i < F; ++i) {
            if (fleet[i][0] != -1 && fleet[i][2] != -1) {
                Estacion origin = stations.get(fleet[i][0]);
                Estacion dest1 = stations.get(fleet[i][2]);
                total_length += getEuclideanDistance(origin.getCoordX(), origin.getCoordY(), dest1.getCoordX(), dest1.getCoordY());
                if (fleet[i][4] != -1) {
                    Estacion dest2 = stations.get(fleet[i][4]);
                    total_length += getEuclideanDistance(dest1.getCoordX(), dest1.getCoordY(), dest2.getCoordX(), dest2.getCoordY());
                }
            }
        }
        return total_length;
    }

    private void printVan(int vanId) {
        System.out.print("Van " + vanId + ": ");
        for (int j = 0; j < 6; ++j) {
            System.out.print(fleet[vanId][j] + "  ");
        }
        System.out.println();
    }

    public void printFleet() {
        for (int i = 0; i < F; ++i) {
            printVan(i);
        }
        System.out.println();
    }

    public void printBikesNeeded() {
        System.out.println("Bikes needed: ");
        for (int i = 0; i < E; ++i) {
            System.out.print(i + ": " + bikesNeeded[i] + "  ");
        }
        System.out.println();
    }

    public void printState() {
        if (heuristicType == 1) {
            System.out.println("benefit: " + this.getBenefit());
        }
        else {
            System.out.println("benefit: " + this.getBenefit() + " transportCost: " + transportCost + " suppliedDemand: " + suppliedDemand);
        }
    }

    public String getFleetState() {
        String fleetState = "";
        for (int i = 0; i < F; ++i) {
            fleetState += "Van " + i + ": ";
            for (int j = 0; j < 6; ++j) {
                fleetState += fleet[i][j] + " ";
            }
            fleetState += "\n";
        }
        return fleetState;
    }

    public void printStationsInfo() {
        System.out.println("Stations info: ");
        for (int i = 0; i < E; ++i) {
            Estacion s = stations.get(i);
            System.out.println(i + ": D:" + s.getDemanda() + " N:" + s.getNumBicicletasNext() + " BNU:" + s.getNumBicicletasNoUsadas());
        }
    }

    public void printTotalTransportCost() {
        double cost = 0.0;
        for (int vanId = 0; vanId < F; ++vanId) {
            int originId = fleet[vanId][0];
            int bikesTaken = fleet[vanId][1];
            int dest1Id = fleet[vanId][2];
            int numBikesLeftDest1 = fleet[vanId][3];
            int dest2Id = fleet[vanId][4];
            int numBikesLeftDest2 = fleet[vanId][5];

            if (dest1Id != -1) cost += getVanTransportCost(originId, dest1Id, bikesTaken);
            if (dest1Id != -1 && dest2Id != -1) cost += getVanTransportCost(dest1Id, dest2Id, bikesTaken - numBikesLeftDest1);
        }
        System.out.println("Transport cost: " + cost);
    }

    public double getDistanceTotal() {
        double dist = 0.0;
        for (int vanId = 0; vanId < F; ++vanId) {
            int originId = fleet[vanId][0];
            int bikesTaken = fleet[vanId][1];
            int dest1Id = fleet[vanId][2];
            int numBikesLeftDest1 = fleet[vanId][3];
            int dest2Id = fleet[vanId][4];
            int numBikesLeftDest2 = fleet[vanId][5];    
            Estacion origin = State.stations.get(originId);
            if (dest1Id != -1) dist += getEuclideanDistance(origin.getCoordX(), origin.getCoordY(), State.stations.get(dest1Id).getCoordX(), State.stations.get(dest1Id).getCoordY());
            if (dest1Id != -1 && dest2Id != -1) dist += getEuclideanDistance(State.stations.get(dest1Id).getCoordX(), State.stations.get(dest1Id).getCoordY(), State.stations.get(dest2Id).getCoordX(), State.stations.get(dest2Id).getCoordY());

        }
        return dist;
    }
}
