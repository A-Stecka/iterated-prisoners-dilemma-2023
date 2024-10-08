package model;

import utils.Logger;

import java.util.ArrayList;

public abstract class Algorithm {
    protected final boolean debug;
    protected final Instance rules;
    protected final Logger logger;
    protected ArrayList<Player> referenceSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Algorithm(boolean debug, Instance rules, int refSetSize) {
        this.debug = debug;
        this.rules = rules;
        logger = new Logger();
        referenceSet = initRefSet(refSetSize);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Player> initRefSet(int refSetSize) {
        ArrayList<Player> refSet = new ArrayList<>();
        for (int i = 0; i < refSetSize; i++) {                                                                          //create constant set of random opponents
            Player player = new Player(this.rules);
            player.initRandom();
            refSet.add(player);
        }
        return refSet;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadIntoFile(String filename, String params, double best, double worst, double avg, double std, double time) {
        logger.loadIntoFile(filename, params, best, worst, avg, std, time);
    }

}
