package agentSim.agent.animal;

import agentSim.agent.Agent;
import agentSim.map.IMap;

public class Animal extends Agent {

    public Animal(IMap map, int health, int infDuration, int resDuration) {
        super(map, health, infDuration, resDuration);
    }

//    Specific recover implementation for Animal agents
    @Override
    public void recover() {
        recover(2);
    }

//    Specific infection implementation for Animal agents
    @Override
    public void infect() {
        infect(1,4, 100);
    }
//    Specific move implementation for Animal agents
    @Override
    public void move(){
        move(2);
    }

    @Override
    public String toString() {
        return switch (healthCondition) {
            case 0 -> TEXT_GREEN+"A "+TEXT_RESET;
            case 1 -> TEXT_RED+"A "+TEXT_RESET;
            case 2 -> TEXT_BLUE+"A "+TEXT_RESET;
            default -> TEXT_YELLOW+"?A "+TEXT_RESET;
        };
    }
}
