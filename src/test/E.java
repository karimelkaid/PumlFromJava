package test;

import java.util.*;

abstract class E {
    private Map<Character, Integer> mapOfInt;
    Map<Short, Double[]> mapOfDoubleArray;
    protected Map<Integer, A> mapOfA;
    public Map<Double, A[]> mapOfArrayOfA;

    private Map<Character, Integer> mapOfIntFunction(Map<Character, Integer> a0, HashMap<Character, Integer[]> a1,
                                                     TreeMap<Character, Integer[][]> a2, Map<Character, Integer[][][]> a3) {
        return null;
    }

    abstract Map<Short, Double[]> mapOfDoubleArrayFunction(Map<Short, Double[]> a0, Map<Short, List<Double>> a1,
                                                           HashMap<Short, ArrayList<Double[]>> a2,
                                                           TreeMap<Short, Map<Short, TreeSet<Double[]>>> a3);

    protected Map<Short, A> mapOfAFunction(Map<Integer, A> a0, Map<Short, List<A>> a1,
                                           Map<Short, List<A>[]> a2, Map<Short, HashMap<Short, A>> a3) {
        return null;
    }

    public static Map<Double, A[]> mapOfArrayOfAFunction(Map<Integer, A[]> a0, Map<Double, List<A[]>> a1,
                                                         Map<Double, List<A[]>[]> a2, TreeMap<Double, Map<Double, A[]>> a3) {
        return null;
    }
}
