package pso;

import model.Instance;
import model.Player;

import java.util.Arrays;
import java.util.BitSet;

public class Particle extends Player {
    private double fitness;
    private double personalBestFitness;
    private final BitSet personalBestPosition;
    private final double[] velocity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Particle(Instance problem) {
        super(problem);
        fitness = 0.0;
        personalBestFitness = 0.0;
        personalBestPosition = new BitSet(problem.GENOTYPE_LENGTH);
        velocity = new double[problem.GENOTYPE_LENGTH];
    }

    public Particle(Particle other) {
        super(other.problem, other);
        fitness = other.fitness;
        personalBestFitness = other.personalBestFitness;
        personalBestPosition = new BitSet(problem.GENOTYPE_LENGTH);
        for (int i = 0; i < problem.GENOTYPE_LENGTH; i++)
            personalBestPosition.set(i, other.personalBestPosition.get(i));
        velocity = new double[problem.GENOTYPE_LENGTH];
        System.arraycopy(other.velocity, 0, velocity, 0, problem.GENOTYPE_LENGTH);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateVelocity(double inertiaParam, double cognitiveParam, double socialParam, BitSet globalBestPosition, double maxV) {
        for (int i = 0; i < problem.GENOTYPE_LENGTH; i++) {                                                             //calculate new velocity
            double r1 = Math.random(), r2 = Math.random();

            if (globalBestPosition.get(i) && personalBestPosition.get(i))                                               //https://doi.org/10.1016/j.asoc.2013.08.004
                velocity[i] = inertiaParam * velocity[i] + r1 * cognitiveParam + r2 * socialParam;
            else
                if (!globalBestPosition.get(i) && !personalBestPosition.get(i))
                    velocity[i] = inertiaParam * velocity[i] - r1 * cognitiveParam - r2 * socialParam;
                else
                    velocity[i] = inertiaParam * velocity[i];

            if (velocity[i] < (-1) * maxV)                                                                              //clip velocity values to [-maxV, maxV]
                velocity[i] = (-1) * maxV;
            if (velocity[i] > maxV)
                velocity[i] = maxV;
        }
    }

    public void updatePosition() {
        for (int i = 0; i < problem.GENOTYPE_LENGTH; i++)                                                               //calculate new position
            strategy.set(i, Math.random() < sigmoid(velocity[i]));
    }

    private static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initParticle() {
        initRandom();
        Arrays.fill(velocity, 0);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double value) {
        fitness = value;
    }

    public void updateFitness(double value) {
        fitness += value;
    }

    public BitSet getPosition() {
        return strategy;
    }

    public void updatePersonalBest() {
        if (fitness > personalBestFitness) {
            personalBestFitness = fitness;
            for (int i = 0; i < problem.GENOTYPE_LENGTH; i++)
                personalBestPosition.set(i, strategy.get(i));
        }
    }

}
