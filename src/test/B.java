package test;

abstract class B {
    private Integer anInteger;
    protected Short aShort;
    Long aLong;
    public Byte aByte;

    private final Float aFloat = 0.0F;
    protected static Double aDouble;
    static final Character aCharacter = ' ';
    public static final Boolean aBoolean = true;

    private Integer anIntFunction(Integer anIntParam) {
        return 0;
    }

    protected Short aShortFunction(Short aShortParam) {
        return 0;
    }

    Long aLongFunction(Long aLongParam) {
        return 0L;
    }

    public Byte aByteFunction(Byte aByteParam) {
        return 0;
    }

    private Float aFloatFunction(Float aFloatParam) {
        return Float.NaN;
    }

    protected abstract Double aDoubleFunction(Double aDoubleParam);

    static Character aCharacterFunction(Character aCharacterParam) {
        return ' ';
    }

    public static Boolean aBooleanFunction(Boolean aBooleanParam) {
        return true;
    }
}
