package util;

import java.io.Serializable;

/**
 * This class consists of a number of parameter used within HMM.
 */
public class Vars implements Serializable {

    public static long totalExecutionTime = 480000;
    
    public static boolean breakpoint1 = true;
    public static boolean breakpoint2 = true;
    public static int numberOfTrials = 30;

    public static long limitOfIterationsStuck = 90000;
    public static long iterMax;
    public static int numberOfRestarts = 0;

    //SimulatedAnnealing parameter
    public static double temperature = 1;
    public static boolean reduceTemperature = false;
    public static int numberOfParameters = 9;
    public static int numberOfAcceptances = 2;
    public static boolean isAtStuck = false;
}
