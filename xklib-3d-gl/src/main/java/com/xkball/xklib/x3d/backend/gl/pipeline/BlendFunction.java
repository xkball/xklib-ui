package com.xkball.xklib.x3d.backend.gl.pipeline;

public record BlendFunction(SourceFactor sourceColor, DestFactor destColor, SourceFactor sourceAlpha, DestFactor destAlpha) {
    public static final BlendFunction LIGHTNING = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
    public static final BlendFunction GLINT = new BlendFunction(SourceFactor.SRC_COLOR, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
    public static final BlendFunction OVERLAY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
    public static final BlendFunction TRANSLUCENT = new BlendFunction(
        SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA
    );
    public static final BlendFunction TRANSLUCENT_PREMULTIPLIED_ALPHA = new BlendFunction(
        SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA
    );
    public static final BlendFunction ADDITIVE = new BlendFunction(SourceFactor.ONE, DestFactor.ONE);
    public static final BlendFunction ENTITY_OUTLINE_BLIT = new BlendFunction(
        SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE
    );
    public static final BlendFunction INVERT = new BlendFunction(
        SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO
    );

    public BlendFunction(SourceFactor sourceFactor, DestFactor destFactor) {
        this(sourceFactor, destFactor, sourceFactor, destFactor);
    }
    
    public static int toGl(DestFactor destFactor) {
        return switch (destFactor) {
            case CONSTANT_ALPHA -> 32771;
            case CONSTANT_COLOR -> 32769;
            case DST_ALPHA -> 772;
            case DST_COLOR -> 774;
            case ONE -> 1;
            case ONE_MINUS_CONSTANT_ALPHA -> 32772;
            case ONE_MINUS_CONSTANT_COLOR -> 32770;
            case ONE_MINUS_DST_ALPHA -> 773;
            case ONE_MINUS_DST_COLOR -> 775;
            case ONE_MINUS_SRC_ALPHA -> 771;
            case ONE_MINUS_SRC_COLOR -> 769;
            case SRC_ALPHA -> 770;
            case SRC_COLOR -> 768;
            case ZERO -> 0;
        };
    }
    
    public static int toGl(SourceFactor sourceFactor) {
        return switch (sourceFactor) {
            case CONSTANT_ALPHA -> 32771;
            case CONSTANT_COLOR -> 32769;
            case DST_ALPHA -> 772;
            case DST_COLOR -> 774;
            case ONE -> 1;
            case ONE_MINUS_CONSTANT_ALPHA -> 32772;
            case ONE_MINUS_CONSTANT_COLOR -> 32770;
            case ONE_MINUS_DST_ALPHA -> 773;
            case ONE_MINUS_DST_COLOR -> 775;
            case ONE_MINUS_SRC_ALPHA -> 771;
            case ONE_MINUS_SRC_COLOR -> 769;
            case SRC_ALPHA -> 770;
            case SRC_ALPHA_SATURATE -> 776;
            case SRC_COLOR -> 768;
            case ZERO -> 0;
        };
    }
    
    public enum SourceFactor {
        CONSTANT_ALPHA,
        CONSTANT_COLOR,
        DST_ALPHA,
        DST_COLOR,
        ONE,
        ONE_MINUS_CONSTANT_ALPHA,
        ONE_MINUS_CONSTANT_COLOR,
        ONE_MINUS_DST_ALPHA,
        ONE_MINUS_DST_COLOR,
        ONE_MINUS_SRC_ALPHA,
        ONE_MINUS_SRC_COLOR,
        SRC_ALPHA,
        SRC_ALPHA_SATURATE,
        SRC_COLOR,
        ZERO;
    }
    
    public enum DestFactor {
        CONSTANT_ALPHA,
        CONSTANT_COLOR,
        DST_ALPHA,
        DST_COLOR,
        ONE,
        ONE_MINUS_CONSTANT_ALPHA,
        ONE_MINUS_CONSTANT_COLOR,
        ONE_MINUS_DST_ALPHA,
        ONE_MINUS_DST_COLOR,
        ONE_MINUS_SRC_ALPHA,
        ONE_MINUS_SRC_COLOR,
        SRC_ALPHA,
        SRC_COLOR,
        ZERO;
    }
}