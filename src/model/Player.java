package model;

import java.util.BitSet;

public class Player {
    protected final Instance problem;
    private static MoveMapping mapping;
    protected BitSet strategy;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Player(Instance problem) {
        mapping = new MoveMapping();
        this.problem = problem;
        this.strategy = new BitSet(problem.GENOTYPE_LENGTH);
    }

    public Player(Instance problem, Player other) {
        mapping = new MoveMapping();
        this.problem = problem;
        this.strategy = new BitSet(problem.GENOTYPE_LENGTH);
        for (int i = 0; i < problem.GENOTYPE_LENGTH; i++)
            strategy.set(i, other.strategy.get(i));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void initRandom() {
        for (int i = 0; i < problem.GENOTYPE_LENGTH; i++)
            strategy.set(i, Math.random() > 0.5);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int[] playAgainst(Instance rules, Player opponent) {
        int scorePlayer = 0, scoreOpponent = 0;
        BitSet historyPlayer = new BitSet(), historyOpponent = new BitSet();
        boolean movePlayer, moveOpponent;

        for (int round = 0; round < rules.getRounds(); round ++) {
            movePlayer = this.move(round, historyPlayer);                                                               //player makes their move
            moveOpponent = opponent.move(round, historyOpponent);                                                       //opponent makes their move

            if (movePlayer && moveOpponent) {                                                                           //CC (both players collaborated)
                scorePlayer += rules.getR();
                scoreOpponent += rules.getR();
            } else
            if (movePlayer) {                                                                                           //CD (player collaborated, opponent defected)
                scorePlayer += rules.getS();
                scoreOpponent += rules.getT();
            } else
            if (moveOpponent) {                                                                                         //DC (player defected, opponent collaborated)
                scorePlayer += rules.getT();
                scoreOpponent += rules.getS();
            } else {                                                                                                    //DD (both players defected)
                scorePlayer += rules.getP();
                scoreOpponent += rules.getP();
            }

            if (movePlayer) {                                                                                           //update player history
                historyPlayer.set(round * 2);
                historyOpponent.set(round * 2 + 1);
            } else {
                historyPlayer.clear(round * 2);
                historyOpponent.clear(round * 2 + 1);
            }

            if (moveOpponent) {                                                                                         //update opponent history
                historyPlayer.set(round * 2 + 1);
                historyOpponent.set(round * 2);
            } else {
                historyPlayer.clear(round * 2 + 1);
                historyOpponent.clear(round * 2);
            }
        }

        return new int[]{scorePlayer, scoreOpponent};
    }

    public boolean move(int round, BitSet history) {
        switch (round) {
            case 0 -> {                                                                                                 //first move
                return strategy.get(0);
            }
            case 1 -> {                                                                                                 //second move
                if (history.get(1))                                                                                     //opponent C (cooperated)
                    return strategy.get(1);
                else                                                                                                    //opponent D (defected)
                    return strategy.get(2);
            }
            case 2 -> {                                                                                                 //third move
                if (history.get(1) && history.get(3))                                                                   //opponent CC (cooperated twice)
                    return strategy.get(3);
                else
                    if (history.get(1))                                                                                 //opponent CD (cooperated then defected)
                        return strategy.get(4);
                    else
                        if (history.get(3))                                                                             //opponent DC (defected ten cooperated)
                            return strategy.get(5);
                        else                                                                                            //opponent DD (defected twice)
                            return strategy.get(6);
            }
            default -> {
                BitSet lastThree = history.get(round * 2 - 6, round * 2);                                               //last three sets of moves (player's + opponent's)
                int id = mapping.get(lastThree);                                                                        //find the id for given move history
                return strategy.get(id + 7);                                                                            //adjust id to skip first game info
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("main.model.Player {\n\t");

        for (int i = 0; i < problem.GENOTYPE_LENGTH; i++) {
            if (strategy.get(i))
                builder.append("C");
            else
                builder.append("D");
        }

        builder.append("\n}");
        return builder.toString();
    }
}
