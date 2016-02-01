package se.walkercrou.composer;

import org.spongepowered.api.effect.sound.SoundType;

/**
 * Represents a pitch value for a {@link SoundType}. Minecraft's accepted pitches span two octaves from F#0 to F#2.
 */
public final class Pitch {
    public static final double FSHARP0 = 0;
    public static final double G0 = 0.53;
    public static final double GSHARP0 = 0.56;
    public static final double A0 = 0.6;
    public static final double ASHARP0 = 0.63;
    public static final double B0 = 0.67;
    public static final double C1 = 0.7;
    public static final double CSHARP1 = 0.76;
    public static final double D1 = 0.8;
    public static final double DSHARP1 = 0.84;
    public static final double E1 = 0.9;
    public static final double F1 = 0.94;
    public static final double FSHARP1 = 1;
    public static final double G1 = 1.06;
    public static final double GSHARP1 = 1.12;
    public static final double A1 = 1.18;
    public static final double ASHARP1 = 1.26;
    public static final double B1 = 1.34;
    public static final double C2 = 1.42;
    public static final double CSHARP2 = 1.5;
    public static final double D2 = 1.6;
    public static final double DSHARP2 = 1.68;
    public static final double E2 = 1.78;
    public static final double F2 = 1.88;
    public static final double FSHARP2 = 2;

    public static final double GFLAT0 = FSHARP0;
    public static final double AFLAT0 = GSHARP0;
    public static final double BFLAT0 = ASHARP0;
    public static final double CFLAT0 = B0;
    public static final double DFLAT1 = CSHARP1;
    public static final double EFLAT1 = DSHARP1;
    public static final double FFLAT1 = E1;
    public static final double GFLAT1 = FSHARP1;
    public static final double AFLAT1 = GSHARP1;
    public static final double BFLAT1 = ASHARP1;
    public static final double CFLAT1 = B1;
    public static final double DFLAT2 = CSHARP2;
    public static final double EFLAT2 = DSHARP2;
    public static final double FFLAT2 = E2;
    public static final double GFLAT2 = FSHARP2;

    public static final double[] TWO_OCTAVES = {
            FSHARP0, G0, GSHARP0, A0, ASHARP0, B0, C1, CSHARP1, D1, DSHARP1, E1, F1, FSHARP1, G1, GSHARP1, A1, ASHARP1,
            B1, C2, CSHARP2, D2, DSHARP2, E2, F2, FSHARP2
    };

    private Pitch() {
    }
}
