package test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

class C {
    private int[] anIntArray;
    protected Short[] aShortArray;
    long[][] anArrayOfLongArray;
    public Byte[][] anArrayOfByteArray;

    private final List<Float> aFloatList = null;
    protected static List<List<Double>> aListOfDoubleList;
    static final Set<Character> aSetOfCharacter = null;
    public static final Set<List<Boolean>> aSetOfBooleanList = null;

    private int[] anIntArrayFunction(int[] anIntArrayParam) {
        return null;
    }

    protected Short[] aShortArrayFunction(Short[] aShortArrayParam) {
        return null;
    }

    long[][][] aLong3DArrayFunction(long[][][][] someLongs) {
        return null;
    }

    public Byte[][][][][] aByte5DArrayFunction(Byte[][][][][][] someBytes) {
        return null;
    }

    private List<Float> aFloatListFunction(List<Float> aFloatListParam) {
        return null;
    }

    protected List<List<Double>> aListOfDoubleListFunction(List<List<List<Double>>> someDoubles) {
        return null;
    }

    Set<Character> aCharacterSetFunction(Set<Character> aCharacterSetParam) {
        return null;
    }

    public Set<List<Boolean>> aSetOfBooleanListFunction(Collection<Set<List<Boolean[][]>[]>> someBooleans) {
        return null;
    }
}
