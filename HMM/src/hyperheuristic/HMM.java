package hyperheuristic;


import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;
import java.text.DecimalFormat;
import moveAcceptance.AcceptanceCriterion;
import moveAcceptance.Adaptative;
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
    // number of heuristics withtou crossover ones
    private int realNumberOfHeuristics;
    private long numberOfIterations;
    private int acceptanceType;
    private long totalExecTime;
    private int lastCalledHeuristic;
    private int pastHeuristic;
    private double currentFitness;
    private double newFitness;
    private double bestFitness;
    private long startTime;
    private long currentTime;
    private int totalNumOfNewBestFound;
    private int[] local_search_heuristics;
    private int[] heuristics;
    private int[] acceptances;
    private double[] parameters;
    private int[] mutation_heuristics;
    private double[] timesUsed;
    private long[] totalTime;
    private int[] crossover_heuristics;
    private int[] ruin_recreate_heuristics;
    private HeuristicType[] heuristicTypeList;
    private HeuristicClassType[] heuristicClassTypeList;
    // trasition and score matrix for llhs
    private double [][][] transHeuristics;
    private int [][][] scoresHeuristics;
    // trasition and score matrix for parameters
    private double [][][] transParameters;
    private int [][][] scoresParameters;
    // trasition and score matrix for acceptance
    private double [][][] transAcceptance;
    private int [][][] scoreAcceptance;
    
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
    
    private void initializeValues(ProblemDomain problem) {
        setHeuristicTypes(problem);
        realNumberOfHeuristics  = numberOfHeuristics - crossover_heuristics.length;
        heuristics = getNonCrossoverHeuristics();
        parameters = getParameters();
        acceptances = new int[2];
        acceptances[0]=0; acceptances[1]=1;
        transHeuristics = new double [realNumberOfHeuristics][realNumberOfHeuristics][realNumberOfHeuristics];
        scoresHeuristics = new int [realNumberOfHeuristics][realNumberOfHeuristics][realNumberOfHeuristics];
        transParameters = new double[realNumberOfHeuristics][realNumberOfHeuristics][9];
        scoresParameters = new int[realNumberOfHeuristics][realNumberOfHeuristics][9];
        transParameters = new double[realNumberOfHeuristics][realNumberOfHeuristics][2];
        scoresParameters = new int[realNumberOfHeuristics][realNumberOfHeuristics][2];
    }


    @Override
    protected void solve(ProblemDomain problem) {
        DecimalFormat fmt = new DecimalFormat("0.00");
        initializeValues(problem);
        problem.setMemorySize(12);
        long endTime;
        int auxNumberIt = 0;
        long startHeur, endHeur;
        problem.initialiseSolution(0);
        problem.copySolution(0, 10);
        System.out.println("Heuristicas:");
        for (int i = 0; i < realNumberOfHeuristics; i++) {
            System.out.print(heuristics[i]+" ");
        }
        System.out.println("");
        //System.out.println("Tamanho da Window: " + selection.getWindowSize());
        System.out.println("Solução inicial: " + problem.getFunctionValue(0));
        bestFitness = currentFitness = newFitness = problem.getFunctionValue(0);
    
    }

    
     private void setHeuristicTypes(ProblemDomain problem) {
       
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

    private int[] getNonCrossoverHeuristics() {
        int aux[] = new int[realNumberOfHeuristics];
        for (int i = 0; i < local_search_heuristics.length; i++) {
            aux[i] = local_search_heuristics[i];
        }
        for (int i = 0; i < ruin_recreate_heuristics.length; i++) {
            aux[local_search_heuristics.length+i] = ruin_recreate_heuristics[i];
        }
        for (int i = 0; i < mutation_heuristics.length; i++) {
            aux[ruin_recreate_heuristics.length+i] = mutation_heuristics[i];
        }
        return aux;
    }

    private double[] getParameters() {
        double [] aux = new double[9];
        for (int i = 0; i < 9; i++) {
            if(i == 0){
                aux[i] = 0.1;
            }else
                aux[i] = 0.1 + aux[i-1];
        }
        return aux;
    }

  
    
}
