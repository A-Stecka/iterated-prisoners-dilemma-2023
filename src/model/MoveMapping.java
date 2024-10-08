package model;

import java.util.BitSet;
import java.util.Hashtable;

public class MoveMapping {
    private final Hashtable<BitSet, Integer> moves;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MoveMapping() {
        moves = new Hashtable<>(64);                                                                         //64 permutations represented as consecutive bin numbers
        StringBuilder builder;

        for (int n = 0; n < 64; n++) {                                                                                  //build table of all possible moves (3-game history)
            builder = new StringBuilder(Integer.toString(n, 2));                                                   //convert number to binary, write as string
            while (builder.length() < 6)
                builder.insert(0, '0');                                                                        //pad to length 6 (3-game history)

            BitSet temp = new BitSet(6);
            for (int i = 0; i < 6; i++)
                if (builder.charAt(i) == '0')
                    temp.set(i);
            moves.put(temp, n);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int get(BitSet h) {
        return moves.get(h);
    }

}
