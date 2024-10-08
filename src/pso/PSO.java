package pso;

import model.Algorithm;
import model.Instance;
import model.Player;

import java.util.ArrayList;
import java.util.BitSet;

public class PSO extends Algorithm {
    private final ArrayList<Particle> bests, worsts, avgs;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PSO(boolean debug, Instance rules, int refSetSize) {
        super(debug, rules, refSetSize);

        bests = new ArrayList<>();
        worsts = new ArrayList<>();
        avgs = new ArrayList<>();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Particle run(int noOfIters, int swarmSize, double inertiaParam, double cognitiveParam, double socialParam,
                        double maxV, String filename, boolean loadSingleRun) {
        ArrayList<Particle> swarm = this.initRandomSwarm(swarmSize);                                                    //initialise swarm
        evaluate(swarm, referenceSet);                                                                                  //evaluate particle positions against reference set
        int currentIter = -1;
        Particle globalBest = getStats(swarm, currentIter, true)[0];                                              //find global best
        double globalBestFitness = globalBest.getFitness();
        BitSet globalBestPosition = new BitSet(rules.GENOTYPE_LENGTH);
        for (int i = 0; i < rules.GENOTYPE_LENGTH; i++)
            globalBestPosition.set(i, globalBest.getPosition().get(i));
        currentIter++;

        while (currentIter < noOfIters) {
            for (int i = 0; i < swarmSize; i++) {
                swarm.get(i).updateVelocity(inertiaParam, cognitiveParam, socialParam, globalBestPosition, maxV);       //update velocity for every particle
                swarm.get(i).updatePosition();                                                                          //update position of every particle
                evaluate(i, swarm, referenceSet);                                                                       //evaluate new position of particle

                if (swarm.get(i).getFitness() > globalBestFitness) {                                                    //update global best
                    globalBestFitness = swarm.get(i).getFitness();
                    for (int j = 0; j < rules.GENOTYPE_LENGTH; j++)
                        globalBestPosition.set(j, swarm.get(i).getPosition().get(j));
                }
            }

            Particle[] stats = getStats(swarm, currentIter, false);                                               //find best, worst and avg fitness for iteration
            bests.add(new Particle(stats[0]));
            worsts.add(new Particle(stats[1]));
            avgs.add(new Particle(stats[2]));

            currentIter++;
        }

        if (loadSingleRun)                                                                                              //load single run to file
            loadIntoFile(filename, bests, worsts, avgs);

        return bests.get(noOfIters - 1);                                                                                //return best found strategy
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void evaluate(ArrayList<Particle> swarm, ArrayList<Player> referenceSet) {
        for (int i = 0; i < swarm.size(); i++)                                                                          //evaluate entire swarm
            evaluate(i, swarm, referenceSet);
    }

    private void evaluate(int playerId, ArrayList<Particle> swarm, ArrayList<Player> referenceSet) {
        swarm.get(playerId).setFitness(0.0);
        for (Player opponent : referenceSet)                                                                            //play a game against every opponent from the reference set - as agreed with the lecturer
            swarm.get(playerId).updateFitness(swarm.get(playerId).playAgainst(rules, opponent)[0]);
        swarm.get(playerId).setFitness(swarm.get(playerId).getFitness() / ((double) referenceSet.size()));              //get average game payoff
        swarm.get(playerId).updatePersonalBest();                                                                       //update personal best
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Particle[] getStats(ArrayList<Particle> swarm, int currentIter, boolean silent) {
        Particle best = swarm.get(0);
        Particle worst = swarm.get(0);
        Particle avg = new Particle(rules);
        double std = 0;

        for (Particle current : swarm) {
            if (current.getFitness() > best.getFitness())
                best = current;
            if (current.getFitness() < worst.getFitness())
                worst = current;
            avg.updateFitness(current.getFitness());
        }

        avg.setFitness(avg.getFitness() / ((double) swarm.size()));

        for (Particle current : swarm)
            std += Math.pow(avg.getFitness() - current.getFitness(), 2);
        std = std / ((double) swarm.size());
        std = Math.sqrt(std);

        if (debug && !silent && swarm.size() != 0) {
            System.out.print("iter " + currentIter + ": ");
            System.out.printf("%-10.2f", best.getFitness());
            System.out.printf("%-10.2f", worst.getFitness());
            System.out.printf("%-10.2f", avg.getFitness());
            System.out.printf("%-10.2f", std);
            System.out.println();
        }

        return new Particle[] {best, worst, avg};
    }

    public double getBest() {
        double best = Integer.MIN_VALUE;
        for (Particle player : bests)
            if (player.getFitness() > best)
                best = player.getFitness();
        return best;
    }

    public double getWorst() {
        double worst = Integer.MAX_VALUE;
        for (Particle player : worsts)
            if (player.getFitness() < worst)
                worst = player.getFitness();
        return worst;
    }

    private void loadIntoFile(String filename, ArrayList<Particle> bests, ArrayList<Particle> worsts, ArrayList<Particle> avgs) {
        int noOfGenerations = bests.size();
        double[] bestsArray = new double[noOfGenerations], worstsArray = new double[noOfGenerations], avgsArray = new double[noOfGenerations];
        for (int i = 0; i < noOfGenerations; i++) {
            bestsArray[i] = bests.get(i).getFitness();
            worstsArray[i] = worsts.get(i).getFitness();
            avgsArray[i] = avgs.get(i).getFitness();
        }
        logger.loadIntoFile(filename, bestsArray, worstsArray, avgsArray);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Particle> initRandomSwarm(int swarmSize) {
        ArrayList<Particle> swarm = new ArrayList<>();
        for (int i = 0; i < swarmSize; i++) {
            Particle particle = new Particle(this.rules);
            particle.initParticle();
            swarm.add(particle);
        }
        return swarm;
    }

}
