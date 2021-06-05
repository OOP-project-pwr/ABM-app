package agentSim.agent.man;

import agentSim.agent.Agent;
import agentSim.map.IMap;

public class Civil extends Agent {
    protected double contagious;
    protected int healthCondition;

    public Civil(IMap map, int health, int infDuration, int resDuration) {
        super(map, health, infDuration, resDuration);
    }

    @Override
    public String toString() {
        return switch (healthCondition) {
            case 0 -> TEXT_GREEN+"C "+TEXT_RESET;
            case 1 -> TEXT_RED+"C "+TEXT_RESET;
            case 2 -> TEXT_BLUE+"C "+TEXT_RESET;
            default -> TEXT_YELLOW+"?C "+TEXT_RESET;
        };
    }
}
