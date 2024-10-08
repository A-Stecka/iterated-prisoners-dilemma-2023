package ea;

import model.Algorithm;
import model.Instance;
import model.Player;

import java.util.ArrayList;
import java.util.TreeMap;

public class EA extends Algorithm {
    private final ArrayList<Individual> bests, worsts, avgs;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EA(boolean debug, Instance rules, int refSetSize) {
        super(debug, rules, refSetSize);

        bests = new ArrayList<>();
        worsts = new ArrayList<>();
        avgs = new ArrayList<>();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //selectionMode = 0 ----> selekcja turniejowa
    //selectionMode = 1 ----> selekcja ruletkowa
    //crossoverMode = 0 ----> krzyzowanie Single Point
    //crossoverMode = 1 ----> krzyzowanie Two Point
    //crossoverMode = 2 ----> krzyżowanie Uniform
    //mutationMode = 0 -----> mutacja swap (single)
    //mutationMode = 1 -----> mutacja swap (multiple)
    //mutationMode = 2 -----> mutacja inversion
    public Individual run(int selectionMode, int crossoverMode, int mutationMode,
                          int noOfGens, int popSize, double tourSize, int pressureModifier, double eliteStrategy, double crossover, double mutation,
                          String filename, boolean loadSingleRun) {
        ArrayList<Individual> prevPop = this.initRandomPop(popSize);                                                    //initialise population
        evaluate(prevPop, referenceSet);                                                                                //evaluate initial population against reference set
        int currentGen = 0;

        while (currentGen < noOfGens) {
            ArrayList<Individual> newPop = new ArrayList<>(popSize);

            if (eliteStrategy != 0.0) {                                                                                 //elite strategy
                prevPop.sort((i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()) * (-1));
                for (int i = 0; i < eliteStrategy * popSize; i++)
                    newPop.add(new Individual(prevPop.get(i)));
            }

            while (newPop.size() < popSize - 1) {

                Individual fstParent, sndParent;
                if (selectionMode == 0) {
                    fstParent = tourSelect(prevPop, tourSize);
                    sndParent = tourSelect(prevPop, tourSize);
                } else {
                    fstParent = rouletteSelect(prevPop, pressureModifier);
                    sndParent = rouletteSelect(prevPop, pressureModifier);
                }

                if (Math.random() < crossover) {                                                                        //crossover
                    Individual[] children;

                    switch (crossoverMode) {
                        case 0 -> children = fstParent.crossoverSinglePoint(sndParent);
                        case 1 -> children = fstParent.crossoverTwoPoint(sndParent);
                        default -> children = fstParent.crossoverUniform(sndParent);
                    }

                    switch (mutationMode) {                                                                             //mutation
                        case 0 -> {
                            children[0].mutationBitFlipSingle(mutation);
                            children[1].mutationBitFlipSingle(mutation);
                        }
                        case 1 -> {
                            children[0].mutationBitFlipMultiple(mutation);
                            children[1].mutationBitFlipMultiple(mutation);
                        }
                        default -> {
                            children[0].mutationInversion(mutation);
                            children[1].mutationInversion(mutation);
                        }
                    }

                    newPop.add(children[0]);
                    newPop.add(children[1]);
                } else {                                                                                                //no crossover
                    Individual fstChild = new Individual(fstParent), sndChild = new Individual(sndParent);

                    switch (mutationMode) {                                                                             //mutation
                        case 0 -> {
                            fstChild.mutationBitFlipSingle(mutation);
                            sndChild.mutationBitFlipSingle(mutation);
                        }
                        case 1 -> {
                            fstChild.mutationBitFlipMultiple(mutation);
                            sndChild.mutationBitFlipMultiple(mutation);
                        }
                        default -> {
                            fstChild.mutationInversion(mutation);
                            sndChild.mutationInversion(mutation);
                        }
                    }
                    newPop.add(fstChild);
                    newPop.add(sndChild);
                }
            }

            evaluate(newPop, referenceSet);                                                                             //evaluate new population against reference set

            Individual[] stats = getStats(newPop, currentGen);                                                          //find best, worst and avg fitness for generation
            bests.add(stats[0]);
            worsts.add(stats[1]);
            avgs.add(stats[2]);

            prevPop = newPop;                                                                                           //replace old population
            currentGen++;                                                                                               //move to new generation
        }

        if (loadSingleRun)                                                                                              //load single run to file
            loadIntoFile(filename, bests, worsts, avgs);

        return bests.get(noOfGens - 1);                                                                                 //return best found strategy
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void evaluate(ArrayList<Individual> population, ArrayList<Player> referenceSet) {
        for (Individual player : population) {
            for (Player opponent : referenceSet)                                                                        //play a game against every opponent from the reference set - as agreed with the lecturer
                player.updateFitness(player.playAgainst(rules, opponent)[0]);
            player.setFitness(player.getFitness() / ((double) referenceSet.size()));                                    //get average game payoff for each player
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Individual[] getStats(ArrayList<Individual> population, int currentGen) {
        Individual best = population.get(0);
        Individual worst = population.get(0);
        Individual avg = new Individual(rules);
        double std = 0;

        for (Individual current : population) {
            if (current.getFitness() > best.getFitness())
                best = current;
            if (current.getFitness() < worst.getFitness())
                worst = current;
            avg.updateFitness(current.getFitness());
        }

        avg.setFitness(avg.getFitness() / ((double) population.size()));

        for (Individual current : population)
            std += Math.pow(avg.getFitness() - current.getFitness(), 2);
        std = std / ((double) population.size());
        std = Math.sqrt(std);

        if (debug && population.size() != 0) {
            System.out.print("gen " + currentGen + ": ");
            System.out.printf("%-10.2f", best.getFitness());
            System.out.printf("%-10.2f", worst.getFitness());
            System.out.printf("%-10.2f", avg.getFitness());
            System.out.printf("%-10.2f", std);
            System.out.println();
        }

        return new Individual[] {best, worst, avg};
    }

    public double getBest() {
        double best = Integer.MIN_VALUE;
        for (Individual player : bests)
            if (player.getFitness() > best)
                best = player.getFitness();
        return best;
    }

    public double getWorst() {
        double worst = Integer.MAX_VALUE;
        for (Individual player : worsts)
            if (player.getFitness() < worst)
                worst = player.getFitness();
        return worst;
    }

    private void loadIntoFile(String filename, ArrayList<Individual> bests, ArrayList<Individual> worsts, ArrayList<Individual> avgs) {
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

    private Individual tourSelect(ArrayList<Individual> population, double tourSize) {
        ArrayList<Integer> chosenIds = new ArrayList<>();
        Individual bestIndividual = null;
        for (int i = 0; i < tourSize * population.size(); i++) {                                                        //tournament selection without replacement
            int index = (int) (Math.random() * population.size());
            while (chosenIds.contains(index)) {
                index = (int) (Math.random() * population.size());
            }
            chosenIds.add(index);
            Individual current = population.get(index);
            if (bestIndividual == null || current.getFitness() > bestIndividual.getFitness())
                bestIndividual = current;
        }
        return bestIndividual;
    }

    private Individual rouletteSelect(ArrayList<Individual> population, int pressureModifier) {
        TreeMap<Double, Individual> probabilities = new TreeMap<>();

        double populationFitness = 0.0;
        for (Individual individual : population)
            populationFitness += Math.pow(individual.getFitness(), pressureModifier);                                   //exponentiation used to increase pressure

        for (Individual individual : population) {                                                                      //roulette selection
            double probability = Math.pow(individual.getFitness(), pressureModifier) / populationFitness;               //exponentiation used to increase pressure
            if (!probabilities.isEmpty())
                probability += probabilities.lastKey();
            probabilities.put(probability, individual);
        }

        return probabilities.ceilingEntry(Math.random()).getValue();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Individual> initRandomPop(int popSize) {
        ArrayList<Individual> population = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            Individual individual = new Individual(this.rules);
            individual.initIndividual();
            population.add(individual);
        }
        return population;
    }

}
