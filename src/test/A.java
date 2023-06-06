package test;

class A {
    private int anInt;
    protected short aShort;
    long aLong;
    public byte aByte;

    private final float aFloat = 0.0F;
    protected static double aDouble;
    static final char aChar = ' ';
    public static final boolean aBoolean = true;

    private int anIntFunction(int anIntParam) {
        return 0;
    }

    protected short aShortFunction(short aShortParam) {
        return 0;
    }

    long aLongFunction(long aLongParam) {
        return 0;
    }

    public byte aByteFunction(byte aByteParam) {
        return 0;
    }

    private static float aFloatFunction(float aFloatParam) {
        return 0;
    }

    protected static double aDoubleFunction(double aDoubleParam) {
        return 0;
    }

    static char aCharFunction(char aCharParam) {
        return ' ';
    }

    static public boolean aBooleanFunction(boolean aBooleanParam) {
        return true;
    }
}
