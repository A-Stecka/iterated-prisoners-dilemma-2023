import ea.EA;
import ea.Individual;
import model.Instance;
import pso.PSO;
import pso.Particle;

public class Research {

    @SuppressWarnings("unused")
    public static void Z1_testAllRounds(boolean debug) {
        int[] noOfRounds = {5, 10, 15};

        for (int noOfRound : noOfRounds)
            Z1_testAll(debug, noOfRound, "results\\rounds_" + noOfRound + ".csv");
    }

    private static void Z1_testAll(boolean debug, int rounds, String filename) {
        int[] noOfGenerations = {50, 100, 250};
        int[] populationSizes = {25, 50, 100};
        double[] tourSizes = {0.05, 0.1, 0.25};
        double[] crossoverProbabilities = {0.5, 0.7, 0.9};
        double[] mutationProbabilities = {0.05, 0.1, 0.3};

        Instance instance = new Instance(rounds);

        for (int noOfGens : noOfGenerations)
            for (int popSize : populationSizes)
                for (double tourSize : tourSizes)
                    for (double crossover : crossoverProbabilities)
                        for (double mutation : mutationProbabilities) {
                            String params = noOfGens + ";" + popSize + ";" + tourSize + ";" + crossover + ";" + mutation;
                            if (debug)
                                System.out.println("---------------------------------------------------- " + params);
                            testEA(debug, filename, params, instance, 0, 0, 0,
                                    noOfGens, popSize, tourSize, 1, 0, crossover, mutation);
                        }
    }

    @SuppressWarnings("unused")
    public static void Z2_testAllRounds(boolean debug) {
        int[] noOfRounds = {5, 10, 15};

        for (int noOfRound : noOfRounds)
            Z2_testAll(debug, noOfRound, "results\\Z2_rounds_" + noOfRound + ".csv");
    }

    private static void Z2_testAll(boolean debug, int rounds, String filename) {
        int[] selectionModes = {0, 1};
        int[] crossoverModes = {0, 1, 2};
        int[] mutationModes = {0, 1, 2};
        double[] tourSizes = {0.05, 0.1, 0.25};
        double[] crossoverProbabilities = {0.5, 0.7, 0.9};
        double[] mutationProbabilities = {0.05, 0.1, 0.3};
        double[] eliteStrategies = {0, 0.05, 0.1, 0.3};

        Instance instance = new Instance(rounds);

        for (int selectionMode : selectionModes)
            for (int crossoverMode : crossoverModes)
                for (int mutationMode : mutationModes)
                    for (double tourSize : tourSizes)
                        for (double crossover : crossoverProbabilities)
                            for (double mutation : mutationProbabilities)
                                for (double eliteStrategy : eliteStrategies) {
                                    String selectionParam, crossoverParam, mutationParam;
                                    if (selectionMode == 0)
                                        selectionParam = "tournament";
                                    else
                                        selectionParam = "roulette";
                                    switch (crossoverMode) {
                                        case 0 -> crossoverParam = "single point";
                                        case 1 -> crossoverParam = "two point";
                                        default -> crossoverParam = "uniform";
                                    }
                                    switch (mutationMode) {
                                        case 0 -> mutationParam = "bit flip (single)";
                                        case 1 -> mutationParam = "bit flip (multiple)";
                                        default -> mutationParam = "inversion";
                                    }
                                    String params = selectionParam + ";" + crossoverParam + ";" + mutationParam + ";" + tourSize + ";" + eliteStrategy + ";" + crossover + ";" + mutation;
                                    if (debug)
                                        System.out.println("---------------------------------------------------- " + params);
                                    testEA(debug, filename, params, instance, selectionMode, crossoverMode, mutationMode,
                                            100, 50, tourSize, 7, eliteStrategy, crossover, mutation);
                                }
    }

    private static void testEA(boolean debug, String filename, String params, Instance instance,
                              int selectionMode, int crossoverMode, int mutationMode,
                               int noOfGens, int popSize, double tourSize, int pressureModifier, double eliteStrategy,
                              double crossover, double mutation) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[10];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;

        EA ea = new EA(false, instance, 100);

        for (int i = 0; i < 10; i++) {
            if (debug)
                System.out.println("------------------------ RUN " + i);
            ea.run(selectionMode, crossoverMode, mutationMode, noOfGens, popSize, tourSize, pressureModifier, eliteStrategy, crossover, mutation, filename, false);
            double currentBest = ea.getBest(), currentWorst = ea.getWorst();
            bests[i] = currentBest;
            if (currentBest > best)
                best = currentBest;
            if (currentWorst < worst)
                worst = currentWorst;
            avg += currentBest;

            ea.initRefSet(100);
        }
        avg = avg / ((double) 10);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) 10);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        ea.loadIntoFile(filename, params, best, worst, avg, std, ((double) elapsedTime) / 10.0);
    }

    @SuppressWarnings("unused")
    public static void runOnceEA(boolean loadSingleRun) {
        Instance instance = new Instance(5);
        EA ea = new EA(true, instance, 100);
        Individual result = ea.run(0, 0, 0, 100, 50, 0.05, 7, 0, 0.5, 0.05, "results\\test.csv", loadSingleRun);
        System.out.println(result);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    public static void Z3_testAllRounds(boolean debug) {
        int[] noOfRounds = {5, 10, 15};

        for (int noOfRound : noOfRounds)
            Z3_testAll(debug, noOfRound, "results\\pso_rounds_" + noOfRound + ".csv");
    }

    private static void Z3_testAll(boolean debug, int rounds, String filename) {
        int[] noOfIterations = {50, 100, 250};
        int[] swarmSizes = {25, 50, 100};
        double[] inertiaParams = {0.25, 0.5, 0.75};
        double[] cognitiveParams = {1, 1.5, 3};
        double[] socialParams = {1, 1.5, 3};
        int[] maxVs = {1, 5, 10};

        Instance instance = new Instance(rounds);

        for (int noOfIters : noOfIterations)
            for (int swarmSize : swarmSizes)
                for (double inertiaParam : inertiaParams)
                    for (double cognitiveParam : cognitiveParams)
                        for (double socialParam : socialParams)
                            for (int maxV : maxVs) {
                                String params = noOfIters + ";" + swarmSize + ";" + inertiaParam + ";" + cognitiveParam + ";" + socialParam + ";" + maxV;
                                if (debug)
                                    System.out.println("---------------------------------------------------- " + params);
                                testPSO(debug, filename, params, instance, noOfIters, swarmSize, inertiaParam, cognitiveParam, socialParam, maxV);
                            }
    }

    private static void testPSO(boolean debug, String filename, String params, Instance instance,
                               int noOfIters, int swarmSize, double inertiaParam, double cognitiveParam, double socialParam,
                               double maxV) {
        long startTime = System.currentTimeMillis();
        double[] bests = new double[10];
        double best = Integer.MIN_VALUE;
        double worst = Integer.MAX_VALUE;
        double avg = 0.0;
        double std = 0.0;

        PSO pso = new PSO(false, instance, 100);

        for (int i = 0; i < 10; i++) {
            if (debug)
                System.out.println("------------------------ RUN " + i);
            pso.run(noOfIters, swarmSize, inertiaParam, cognitiveParam, socialParam, maxV, filename, false);
            double currentBest = pso.getBest(), currentWorst = pso.getWorst();
            bests[i] = currentBest;
            if (currentBest > best)
                best = currentBest;
            if (currentWorst < worst)
                worst = currentWorst;
            avg += currentBest;

            pso.initRefSet(100);
        }
        avg = avg / ((double) 10);
        for (double value : bests)
            std += Math.pow(avg - value, 2);
        std = std / ((double) 10);
        std = Math.sqrt(std);
        long elapsedTime = System.currentTimeMillis() - startTime;
        pso.loadIntoFile(filename, params, best, worst, avg, std, ((double) elapsedTime) / 10.0);
    }

    @SuppressWarnings("unused")
    public static void runOncePSO(boolean loadSingleRun) {
        Instance instance = new Instance(5);
        PSO pso = new PSO(true, instance, 100);
        Particle result = pso.run(250, 25, 0.25, 1.5, 1.5, 1, "results\\pso_test.csv", loadSingleRun);
        System.out.println(result);
    }

}
