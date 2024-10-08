package ea;

import model.Instance;
import model.Player;

import java.util.BitSet;

public class Individual extends Player {
    private double fitness;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Individual(Instance problem) {
        super(problem);
        fitness = 0.0;
    }

    public Individual(Individual other) {
        super(other.problem, other);
        fitness = 0;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Individual[] crossoverSinglePoint(Individual sndParent) {
        int index = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
        while (index == 0 || index == this.problem.GENOTYPE_LENGTH)
            index = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
        return new Individual[]{this.crossoverSinglePointSingleChild(sndParent, index), sndParent.crossoverSinglePointSingleChild(this, index)};
    }

    private Individual crossoverSinglePointSingleChild(Individual sndParent, int index) {                               //copy genes from first parent up to a certain point
        BitSet childGenotype = new BitSet(this.problem.GENOTYPE_LENGTH);                                                //copy genes from second parent from a certain point
        for (int i = 0; i < this.problem.GENOTYPE_LENGTH; i++) {
            if (i < index)
                childGenotype.set(i, this.strategy.get(i));
            if (i >= index)
                childGenotype.set(i, sndParent.strategy.get(i));
        }
        Individual child = new Individual(this.problem);
        child.strategy = childGenotype;
        return child;
    }

    public Individual[] crossoverTwoPoint(Individual sndParent) {
        int fstIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH), sndIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
        while (fstIndex == 0 || fstIndex == this.problem.GENOTYPE_LENGTH)
            fstIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
        while (fstIndex == sndIndex || sndIndex == 0 || sndIndex == this.problem.GENOTYPE_LENGTH)
            sndIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
        if (fstIndex > sndIndex) {
            int tmp = fstIndex;
            fstIndex = sndIndex;
            sndIndex = tmp;
        }
        return new Individual[]{this.crossoverTwoPointSingleChild(sndParent, fstIndex, sndIndex), sndParent.crossoverTwoPointSingleChild(this, fstIndex, sndIndex)};
    }

    private Individual crossoverTwoPointSingleChild(Individual sndParent, int fstIndex, int sndIndex) {                 //copy genes from first parent up to fst index
        BitSet childGenotype = new BitSet(this.problem.GENOTYPE_LENGTH);                                                //copy genes from second parent between fst and snd index
        for (int i = 0; i < this.problem.GENOTYPE_LENGTH; i++) {                                                        //copy genes from first parent after snd index
            if (i < fstIndex || i >= sndIndex)
                childGenotype.set(i, this.strategy.get(i));
            if (i >= fstIndex && i < sndIndex)
                childGenotype.set(i, sndParent.strategy.get(i));
        }
        Individual child = new Individual(this.problem);
        child.strategy = childGenotype;
        return child;
    }

    public Individual[] crossoverUniform(Individual sndParent) {                                                        //each gene can come from one parent or the other
        BitSet fstChildGenotype = new BitSet(this.problem.GENOTYPE_LENGTH),                                             //"flip a coin" for each gene
                sndChildGenotype = new BitSet(this.problem.GENOTYPE_LENGTH);
        for (int i = 0; i < this.problem.GENOTYPE_LENGTH; i++)
            if (Math.random() < 0.5) {
                fstChildGenotype.set(i, this.strategy.get(i));
                sndChildGenotype.set(i, sndParent.strategy.get(i));
            } else {
                fstChildGenotype.set(i, sndParent.strategy.get(i));
                sndChildGenotype.set(i, this.strategy.get(i));
            }
        Individual[] children = new Individual[]{new Individual(this.problem), new Individual(this.problem)};
        children[0].strategy = fstChildGenotype;
        children[1].strategy = sndChildGenotype;
        return children;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void mutationBitFlipSingle(double mutation) {
        if (Math.random() < mutation)
            this.strategy.flip((int) (Math.random() * this.problem.GENOTYPE_LENGTH));
    }

    public void mutationBitFlipMultiple(double mutation) {
        for (int i = 0; i < this.problem.GENOTYPE_LENGTH; i++)
            if (Math.random() < mutation)
                this.strategy.flip(i);
    }

    public void mutationInversion(double mutation) {
        if (Math.random() < mutation) {
            int fstIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH), sndIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
            while (fstIndex == sndIndex)
                sndIndex = (int) (Math.random() * this.problem.GENOTYPE_LENGTH);
            if (fstIndex > sndIndex) {
                int tmp = fstIndex;
                fstIndex = sndIndex;
                sndIndex = tmp;
            }
            for (int i = 0; i < (sndIndex - fstIndex) / 2; i++) {
                boolean tmp = strategy.get(fstIndex + i);
                strategy.set(fstIndex + i, strategy.get(sndIndex - i));
                strategy.set(sndIndex - i, tmp);
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initIndividual() {
        initRandom();
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

}
