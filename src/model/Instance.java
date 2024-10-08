package model;

//https://www.bc.edu/content/dam/files/schools/cas_sites/cs/pdf/academics/honors/06DanielScali.pdf

public class Instance {
    public final int GENOTYPE_LENGTH;
    private final int R, T, S, P;
    private final int rounds;

    public Instance(int rounds) {
        GENOTYPE_LENGTH = 71;                                                                                           //64 (3-game history) + 6 (first game) + 1 (first move)

        R = 3;                                                                                                          //payoffMatrix is hardcoded as agreed with the lecturer
        T = 5;
        S = 0;
        P = 1;

        this.rounds = rounds;                                                                                           //number of rounds isn't hardcoded as agreed with the lecturer
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int getRounds() {
        return rounds;
    }

    public int getR() {
        return R;
    }

    public int getT() {
        return T;
    }

    public int getS() {
        return S;
    }

    public int getP() {
        return P;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "main.model.model.Instance {\n\tpayoffMatrix = " + "\n\t\t" +
                "R = " + R + ", R = " + R + " | " +  "S = " + S + ", T = " + T +
                "\n\t\t" + "T = " + T + ", S = " + S + " | " + "P = " + P + ", P = " + P +
                "\n\trounds = " + rounds + "\n" + "}";
    }

}
