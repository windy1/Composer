package se.walkercrou.composer;

import org.spongepowered.api.effect.sound.SoundType;

/**
 * Represents a pitch value for a {@link SoundType}. Minecraft's accepted pitches span two octaves from F#0 to F#2.
 */
public final class Pitch {
    public static double FSHARP0 = 0;
    public static double G0 = 0.55;
    public static double GSHARP0 = 0.56;
    public static double A0 = 0.6;
    public static double ASHARP0 = 0.65;
    public static double B0 = 0.67;
    public static double C1 = 0.7;
    public static double CSHARP1 = 0.75;
    public static double D1 = 0.8;
    public static double DSHARP1 = 0.85;
    public static double E1 = 0.9;
    public static double F1 = 0.95;
    public static double FSHARP1 = 1;
    public static double G1 = 1.05;
    public static double GSHARP1 = 1.1;
    public static double A1 = 1.2;
    public static double ASHARP1 = 1.3;
    public static double B1 = 1.35;
    public static double C2 = 1.4;
    public static double CSHARP2 = 1.5;
    public static double D2 = 1.6;
    public static double DSHARP2 = 1.7;
    public static double E2 = 1.8;
    public static double F2 = 1.9;
    public static double FSHARP2 = 2;

    public static double GFLAT0 = FSHARP0;
    public static double AFLAT0 = GSHARP0;
    public static double BFLAT0 = ASHARP0;
    public static double CFLAT0 = B0;
    public static double DFLAT1 = CSHARP1;
    public static double EFLAT1 = DSHARP1;
    public static double FFLAT1 = E1;
    public static double GFLAT1 = FSHARP1;
    public static double AFLAT1 = GSHARP1;
    public static double BFLAT1 = ASHARP1;
    public static double CFLAT1 = B1;
    public static double DFLAT2 = CSHARP2;
    public static double EFLAT2 = DSHARP2;
    public static double FFLAT2 = E2;
    public static double GFLAT2 = FSHARP2;

    private Pitch() {
    }
}
