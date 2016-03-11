package hyperheuristic;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
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
public class HMM extends HyperHeuristic {

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
    private long lastIterationBest;
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
    private double[][] transHeuristics;
    private int[][] scoresHeuristics;
    // trasition and score matrix for parameters
    private double[][] transParameters;
    private int[][] scoresParameters;
    // trasition and score matrix for acceptance
    private double[][] transAcceptances;
    private int[][] scoresAcceptances;
    private AcceptanceCriterion acceptance;

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
        realNumberOfHeuristics = numberOfHeuristics - crossover_heuristics.length;
        heuristics = getNonCrossoverHeuristics();
        parameters = getParameters();
        acceptances = getAcceptances();
        transHeuristics = new double[realNumberOfHeuristics][realNumberOfHeuristics];
        scoresHeuristics = new int[realNumberOfHeuristics][realNumberOfHeuristics];
        transParameters = new double[realNumberOfHeuristics][Vars.numberOfParameters];
        scoresParameters = new int[realNumberOfHeuristics][Vars.numberOfParameters];
        transAcceptances = new double[realNumberOfHeuristics][Vars.numberOfAcceptances];
        scoresAcceptances = new int[realNumberOfHeuristics][Vars.numberOfAcceptances];
        initializeScores(scoresHeuristics, realNumberOfHeuristics);
        initializeScores(scoresParameters, Vars.numberOfParameters);
        initializeScores(scoresAcceptances, Vars.numberOfAcceptances);
        initializeProbabilities(transHeuristics, realNumberOfHeuristics);
        initializeProbabilities(transParameters, Vars.numberOfParameters);
        initializeProbabilities(transAcceptances, Vars.numberOfAcceptances);
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
            System.out.print(heuristics[i] + " ");
        }
        System.out.println("");
        //System.out.println("Tamanho da Window: " + selection.getWindowSize());
        System.out.println("Solução inicial: " + problem.getFunctionValue(0));
        bestFitness = currentFitness = newFitness = problem.getFunctionValue(0);

        while (!hasTimeExpired()) {
            //lastCalledHeuristic = selection.selectHeuristic();
            //problem.setIntensityOfMutation(selection.getLevelOfChangeList()[lastCalledHeuristic]);
            //problem.setDepthOfSearch(selection.getLevelOfChangeList()[lastCalledHeuristic]);

            startHeur = System.nanoTime();
            //if (isCrossover(lastCalledHeuristic)) {
            //System.out.println("Enoutr aqui");
            // int slnInxForCrossover = rng.nextInt(5) + 5;
            //newFitness = problem.applyHeuristic(lastCalledHeuristic, 0, slnInxForCrossover, 1);
            //} else {
            newFitness = problem.applyHeuristic(lastCalledHeuristic, 0, 1);
            //}
            endHeur = System.nanoTime();

            double delta = Math.max(0, ((currentFitness - newFitness) / currentFitness));

            //acceptance
            if (acceptance.accept(newFitness, currentFitness, bestFitness)) {
                problem.copySolution(1, 0);
                if (newFitness < bestFitness) {
                    totalNumOfNewBestFound++;
                    /* Update the best fitness value */
                    bestFitness = newFitness;
                    lastIterationBest = numberOfIterations;
                    /* Change randomly one of the solution used for crossovers (or any heuristic requiring two solutions) */
                    int randMemIndex = rng.nextInt(5) + 5;
                    problem.copySolution(1, randMemIndex);
                    /* Copy the new best solution to the solution memory */
                    problem.copySolution(0, 10);

                }
                currentFitness = newFitness;
            }

            numberOfIterations++;
            auxNumberIt++;
            /* Increment the phase iteration counter */
            //currentTime = System.currentTimeMillis() - startTime;
            timesUsed[lastCalledHeuristic]++;
            totalTime[lastCalledHeuristic] += (endHeur - startHeur);
            pastHeuristic = lastCalledHeuristic;
            //System.out.println(problem.getFunctionValue(0) + " " + lastCalledHeuristic + improv);
            currentTime = System.currentTimeMillis() - startTime;
        }

        System.out.println("Número de iterações executadas: " + numberOfIterations);
        System.out.println("Ultima iteração onde um ótimo foi encontrado: " + lastIterationBest);
        //System.out.println("Parâmetros finais para o HyFlex: ");
        //double x[] = selection.getLevelOfChangeList();
        System.out.println("Vezes usada: ");
        for (int i = 0; i < numberOfHeuristics; i++) {
            System.out.println("Heurística: " + i + " " + timesUsed[i] + " " + fmt.format(timesUsed[i] * 100.0 / numberOfIterations) + "% Tempo "
                    + TimeUnit.NANOSECONDS.toMillis(totalTime[i]));
        }
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
            aux[local_search_heuristics.length + i] = ruin_recreate_heuristics[i];
        }
        for (int i = 0; i < mutation_heuristics.length; i++) {
            aux[ruin_recreate_heuristics.length + i] = mutation_heuristics[i];
        }
        return aux;
    }

    private double[] getParameters() {
        double[] aux = new double[Vars.numberOfParameters];
        for (int i = 0; i < Vars.numberOfParameters; i++) {
            if (i == 0) {
                aux[i] = 0.1;
            } else {
                aux[i] = 0.1 + aux[i - 1];
            }
        }
        return aux;
    }

    private void initializeScores(int[][] matrix, int tam) {
        for (int i = 0; i < realNumberOfHeuristics; i++) {
            for (int j = 0; j < tam; j++) {
                matrix[i][j] = 1;
            }
        }
    }

    private void initializeProbabilities(double[][] matrix, int tam) {
        double prob = 1/tam;
        for (int i = 0; i < realNumberOfHeuristics; i++) {
            for (int j = 0; j < tam; j++) {
                matrix[i][j] = prob;
            }
        }
    }

    private void updateScore(double[][] matrix, int pastheuristic, int current, int element) {
        matrix[current][element] += 1;
    }

    private int[] getAcceptances() {
        int[] aux = new int[Vars.numberOfAcceptances];
        for (int i = 0; i < Vars.numberOfParameters; i++) {
            if (i == 0) {
                aux[i] = 0;
            } else {
                aux[i] = 1 + aux[i - 1];
            }
        }
        return aux;
    }

}
