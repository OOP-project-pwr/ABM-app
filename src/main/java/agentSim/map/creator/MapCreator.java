package agentSim.map.creator;

import agentSim.map.IMap;
import agentSim.map.SimulationMap;

public class MapCreator implements IMapCreator {
    int width;
    int height;

    public MapCreator(int width, int height) {
        this.width = height;
        this.height = width;
    }

    @Override
    public IMap createMap() {
        return new SimulationMap(width, height);
    }
}
