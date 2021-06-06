package agentSim.agent;

import agentSim.IColors;
import agentSim.map.IMap;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import java.util.Random;

import static com.google.common.primitives.Ints.max;
import static com.google.common.primitives.Ints.min;

public abstract class Agent extends AAgent implements IColors {

    protected Random rnd;
    protected long seed;
    static long seedInfections;
    protected int healthCondition;
    protected int infectionDuration;
    protected int resistanceDuration;

    public Agent(IMap map, int health, int infDuration, int resDuration) {
        super(map);
//        !!! Current seed isn't the same as the one being passed in Simulation class
        this.seed = 0;
        rnd = new Random(seed);
        this.healthCondition = health;
//        If generated agent is ill then set duration for infection, if it isn't then keep it at 0
        this.infectionDuration = infDuration;
//        Same as above but for resistant agents
        this.resistanceDuration = resDuration;
        seedInfections = 0;
        //    Uncomment this to get completely random series of probabilities for infections
//        seedInfections = System.currentTimeMillis();
    }

    public void move(int distance) {
        int initialRow =map.getAgentPosition(this)[0];
        int initialColumn =map.getAgentPosition(this)[1];
        int currR = initialRow;
        int currC = initialColumn;

        int rowLimit = map.getXDim()-1;
        int colLimit = map.getYDim()-1;

        do {
//        Exclusive range - number between 0-7 and +1 to fulfill condition of 8 neighbour directions
//            Currently the numbers are an even distribution however it can be easily changed
//            with swapping the generated num variable to double and adjusting case conditions to match desired probabilities
            int num = rnd.nextInt(9)+1;
            switch (num) {
//            Move down
                case 1:
                    currR = Math.max(0, currR- distance);
                    break;
//            Move up
                case 2:
                    currR = Math.min(currR+ distance, rowLimit);
                    break;
//            Move left
                case 3:
                    currC = Math.max(0, currC- distance);
                    break;
//            Move right
                case 4:
                    currC = Math.min(currC+ distance, colLimit);
                    break;
//            Move down right
                case 5:
                    currR = Math.max(0, currR- distance);
                    currC = Math.min(currC+ distance, colLimit);
                    break;
//            Move down left
                case 6:
                    currR = Math.max(0, currR- distance);
                    currC = Math.max(0, currC- distance);
                    break;
//            Move up right
                case 7:
                    currR = Math.min(currR+ distance, rowLimit);
                    currC = Math.min(currC+ distance, colLimit);
                    break;
//            Move up left
                case 8:
                    currR = Math.min(currR+ distance, rowLimit);
                    currC = Math.max(0, currC- distance);
                    break;
//            Stay in place - keep the initial values and move to the placement part of the loop
                case 9:
                    currR = initialRow;
                    currC = initialColumn;
                    break;
            }
//            Break loop if the neighbour cell is empty or is itself (that way infinite loop will be avoided in case all neighbours are taken)
            if (map.getAgent(currR,currC) == null || map.getAgent(currR,currC) == this) {
//                New function to place agents was required in order to account for case when agent is surrounded only by agents and is unable to move
//                In such case the function should keep looping until the moment it generates number 9 and keeps the agent in its place
                map.placeAgentInclusive(this, currR, currC);
                break;
            }
        } while (true);

//            Old function for moving the agent
//        do {
//            int position_x = rnd.nextInt(map.getXDim());
//            int position_y = rnd.nextInt(map.getYDim());
//            if (map.getAgent(position_x, position_y) == null) {
//                map.placeAgent(this, position_x, position_y);
//                break;
//            }
//        } while(true);

    }

    public void infect(int fieldOfView, int duration, double probability) {
        Multimap<IAgent, Integer> neighbours = this.getNeighbours(fieldOfView);
        int row = 0;
        int col;
        int i = 0;
//        New random object for generating infection probabilities
        Random ran = new Random(seedInfections);
//        Change the seed to generate different numbers for another object
        seedInfections++;
        IAgent toBeInfected;

//        for (int i = 0; )

//        Two coordinates are stored repetitively so just iterate over even and uneven values from neighbours
        for (Integer value : neighbours.values()) {
    //            When i is uneven both row and col for one agent are known
                if ( i % 2 != 0) {
//                    rand.nextInt((max - min) + 1) + min;
//                    Max being 100*field of view and min being 0
//                    Multiplied by field of view to lower the chance of infection over long distances
                    double genProb = ran.nextInt((100*fieldOfView)+1);
//                    System.out.println("genProb: " + genProb + " prob: " + probability);
                    col = value;
                    toBeInfected = map.getAgent(row, col);
//                    If target agent is healthy then infect and prob is right
//                    Quick prob description 100 - being 100% 0 - being 0 %
//                    But that's only for fov of 1, for bigger fov to calculate 100% of infection the prob number must be multiplied by fov
                    if (toBeInfected.getHealth() == 0 && genProb <= probability) {
                        toBeInfected.setHealth(1);
//                        Set infection duration for infected agents
                        toBeInfected.setInfectionDuration(duration);
                    }
                } else {
                    row = value;
                }
                i++;
            }
    }

    public void recover(int resistanceDurationAfterDisease) {
//        Part for losing resistance
        if (resistanceDuration > 0 ) {
            resistanceDuration--;
//            Once resistance duration is 0 then set the agent to be healthy
//            healthCondition == 2 is there to ensure only resistant agents can recover
            if (resistanceDuration == 0 && healthCondition == 2) {
                healthCondition = 0;
            }
        } else if (resistanceDuration < 0) {
            resistanceDuration = 0;
        }

//        Part for recovering from infection
        if (infectionDuration > 0 ) {
            infectionDuration--;
//            Once infection duration is 0 then set the agent to be immune
//            healthCondition == 1 is there to ensure only ill agents can recover
            if (infectionDuration == 0 && healthCondition == 1) {
                healthCondition = 2;
                resistanceDuration = resistanceDurationAfterDisease;
            }
//            Prevents infectionDuration from having negative values (just in case)
        } else if (infectionDuration < 0) {
            infectionDuration = 0;
        }
    }

    public Multimap<IAgent, Integer> getNeighbours(int fieldOfView) {
        int currR = map.getAgentPosition(this)[0];
        int currC = map.getAgentPosition(this)[1];
        int rowLimit = map.getXDim()-1;
        int colLimit = map.getYDim()-1;

//        Multimap to store agent and their neighbour locations
        Multimap<IAgent, Integer> positions = ArrayListMultimap.create();

        for (int x = max(0, currR-fieldOfView); x <= Math.min(currR+fieldOfView, rowLimit); x++) {
            for (int y = max(0, currC-fieldOfView); y <= min(currC+fieldOfView, colLimit); y++) {
//                    Check if the x and y aren't coordinates of the agent
                if (x != currR || y != currC) {
//                            Check if neighbour is not empty
                    if (map.getAgent(x,y) != null) {
//                                Accumulate positions in multiMap
                        positions.putAll(this, Ints.asList(x,y));
                    }
                }
            }
        }
        return positions;
    }

    public void setHealth(int healthStatus) {
        this.healthCondition = healthStatus;
    }

    public int getHealth() {
        return this.healthCondition;
    }

    public void setInfectionDuration(int infectionDuration) {
        this.infectionDuration = infectionDuration;
    }

    public void setResistanceDuration(int resistanceDuration) {
        this.resistanceDuration = resistanceDuration;
    }


    @Override
    public String toString() {
        return switch (healthCondition) {
            case 0 -> TEXT_GREEN+"h "+TEXT_RESET;
            case 1 -> TEXT_RED+"i "+TEXT_RESET;
            case 2 -> TEXT_BLUE+"r "+TEXT_RESET;
            default -> TEXT_YELLOW+"? "+TEXT_RESET;
        };
    }
}
