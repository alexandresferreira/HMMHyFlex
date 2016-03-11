package hyperheuristic;


import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;
import moveAcceptance.AcceptanceCriterion;
import moveAcceptance.Adaptative;
import moveAcceptance.AdaptiveIterationLimitedListBasedTA;
import moveAcceptance.AllMoves;
import moveAcceptance.BetterEqual;
import moveAcceptance.ExponentialMonteCarlo;
import moveAcceptance.NaiveAcceptance;
import moveAcceptance.OnlyBetter;
import moveAcceptance.RecordToRecord;
import moveAcceptance.SimulatedAnnealing;
import util.Vars;
/**
 *
 * @author alexandre
 */
public class HMM extends HyperHeuristic{
    
    private int numberOfHeuristics;
    private long numberOfIterations;
    private int acceptanceType;
    private long totalExecTime;
    private int lastCalledHeuristic;
    private int pastHeuristic;
    private double currentFitness;
    private double newFitness;
    private long startTime;
    private long currentTime;
    private int totalNumOfNewBestFound;
    private int[] local_search_heuristics;
    private int[] mutation_heuristics;
    private double[] timesUsed;
    private long[] totalTime;
    private int[] crossover_heuristics;
    private int[] ruin_recreate_heuristics;
    private HeuristicType[] heuristicTypeList;
    private HeuristicClassType[] heuristicClassTypeList;

    public HMM() {
    }
    
    public HMM(long seed, int numberOfHeuristics, long totalExecTime, int acc) {
        super(seed);
        this.numberOfHeuristics = numberOfHeuristics;
        this.numberOfIterations = 0;
        this.totalExecTime = totalExecTime;
        Vars.totalExecutionTime = totalExecTime;
        heuristicTypeList = new HeuristicType[numberOfHeuristics];
        timesUsed = new double[numberOfHeuristics];
        totalTime = new long[numberOfHeuristics];
        heuristicClassTypeList = new HeuristicClassType[numberOfHeuristics];
        this.acceptanceType = acc;
    }

    @Override
    protected void solve(ProblemDomain pd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
     private void setHeuristicTypes(ProblemDomain problem) {
        local_search_heuristics = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.LOCAL_SEARCH);
        mutation_heuristics = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.MUTATION);
        ruin_recreate_heuristics = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.RUIN_RECREATE);
        crossover_heuristics = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.CROSSOVER);

        for (int i = 0; i < local_search_heuristics.length; i++) {
            heuristicTypeList[local_search_heuristics[i]] = HeuristicType.LOCAL_SEARCH;
            heuristicClassTypeList[local_search_heuristics[i]] = HeuristicClassType.ImprovingMoreOrEqual;
        }
        for (int i = 0; i < mutation_heuristics.length; i++) {
            heuristicTypeList[mutation_heuristics[i]] = HeuristicType.MUTATION;
            heuristicClassTypeList[mutation_heuristics[i]] = HeuristicClassType.ImprovingMoreOrEqual;
        }
        for (int i = 0; i < ruin_recreate_heuristics.length; i++) {
            heuristicTypeList[ruin_recreate_heuristics[i]] = HeuristicType.RUIN_RECREATE;
            heuristicClassTypeList[ruin_recreate_heuristics[i]] = HeuristicClassType.ImprovingMoreOrEqual;
        }
        for (int i = 0; i < crossover_heuristics.length; i++) {
            heuristicTypeList[crossover_heuristics[i]] = HeuristicType.CROSSOVER;
            heuristicClassTypeList[crossover_heuristics[i]] = HeuristicClassType.ImprovingMoreOrEqual;
        }
    }
     
    private boolean isCrossover(int heurIndex) {
        boolean isCrossover = false;
        for (int cr = 0; cr < crossover_heuristics.length; cr++) {
            if (crossover_heuristics[cr] == heurIndex) {
                isCrossover = true;
                break;
            }
        }
        return isCrossover;
    }
    
    @Override
    public String toString() {
        return "HMM"; //To change body of generated methods, choose Tools | Templates.
    }

    
}
