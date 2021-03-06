package hyperheuristic;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
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
    private DecimalFormat fmt;
    // number of heuristics withtou crossover ones
    private int realNumberOfHeuristics;
    private long numberOfIterations;
    private int acceptanceType;
    private long totalExecTime;
    private double currentFitness;
    private double newFitness;
    private double bestFitness;
    private long startTime;
    private long currentTime;
    private int totalNumOfNewBestFound;
    private long lastIterationBest;
    private int[] local_search_heuristics;
    private ArrayList<Integer> heuristics;
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
    private int prevHeuristic;
    private int currHeuristic;
    private int p;
    private int a;

    public HMM(long seed, int numberOfHeuristics, long totalExecTime, int acc) {
        super(seed);
        this.numberOfHeuristics = numberOfHeuristics;
        this.numberOfIterations = 0;
        this.totalExecTime = totalExecTime;
        heuristics = new ArrayList<Integer>();
        Vars.totalExecutionTime = totalExecTime;
        this.acceptanceType = acc;
        acceptance = new RecordToRecord(rng);
        fmt = new DecimalFormat("0.00");
        heuristicTypeList = new HeuristicType[numberOfHeuristics];
        timesUsed = new double[numberOfHeuristics];
        totalTime = new long[numberOfHeuristics];
        heuristicClassTypeList = new HeuristicClassType[numberOfHeuristics];
    }

    private void initializeValues(ProblemDomain problem) {
        setHeuristicTypes(problem);
        realNumberOfHeuristics = numberOfHeuristics - crossover_heuristics.length;
        initializeNonCrossoverHeuristics();
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
        initializeValues(problem);
        //printDoubleMatrix(transHeuristics);
        problem.setMemorySize(12);
        long endTime;
        int auxNumberIt = 0;
        long startHeur, endHeur;
        problem.initialiseSolution(0);
        problem.copySolution(0, 10);
        System.out.println("Heuristicas:");
        for (int i = 0; i < realNumberOfHeuristics; i++) {
            System.out.print(heuristics.get(i) + " ");
        }
        System.out.println("");
        //System.out.println("Tamanho da Window: " + selection.getWindowSize());
        System.out.println("Solução inicial: " + problem.getFunctionValue(0));
        bestFitness = currentFitness = newFitness = problem.getFunctionValue(0);
        prevHeuristic = 0;
        while (!hasTimeExpired()) {
            //lastCalledHeuristic = selection.selectHeuristic();
            currHeuristic = select(transHeuristics, prevHeuristic);
            //System.out.print("Heuristica escolhida: " + heuristics.get(currHeuristic) + " ");
            p = select(transParameters, heuristics.get(currHeuristic));
            //System.out.print("Parâmetro: " + parameters[p] + " ");
            a = select(transAcceptances, heuristics.get(currHeuristic));
            //System.out.println("Accep: " + acceptances[a]);
            problem.setIntensityOfMutation(parameters[p]);
            problem.setDepthOfSearch(parameters[p]);

            startHeur = System.nanoTime();
            newFitness = problem.applyHeuristic(heuristics.get(currHeuristic), 0, 1);
            endHeur = System.nanoTime();

            //double delta = Math.max(0, ((currentFitness - newFitness) / currentFitness));
            if (acceptances[a] == 1) {
                if (acceptance.accept(newFitness, currentFitness, bestFitness)) {
                    problem.copySolution(1, 0);
                    if (newFitness < bestFitness) {
                        totalNumOfNewBestFound++;
                        /* Update the best fitness value */
                        bestFitness = newFitness;
                        lastIterationBest = numberOfIterations;
                        /* Copy the new best solution to the solution memory */
                        problem.copySolution(0, 10);
                        updateScore(scoresHeuristics, prevHeuristic, currHeuristic);
                        updateScore(scoresParameters, currHeuristic, p);
                        updateScore(scoresAcceptances, currHeuristic, a);
                        updateProbabilities(scoresHeuristics, transHeuristics, prevHeuristic, currHeuristic);
                        //printDoubleMatrix(transHeuristics);
                        updateProbabilities(scoresParameters, transParameters, currHeuristic, p);
                        //printDoubleMatrix(transParameters);
                        updateProbabilities(scoresAcceptances, transAcceptances, currHeuristic, a);
                        //printDoubleMatrix(transAcceptances);

                    }
                    currentFitness = newFitness;
                }
            } else {
               problem.copySolution(1, 0);
               currentFitness = newFitness;
            }

            numberOfIterations++;
            auxNumberIt++;
            /* Increment the phase iteration counter */
            //currentTime = System.currentTimeMillis() - startTime;
            timesUsed[heuristics.get(currHeuristic)]++;
            totalTime[heuristics.get(currHeuristic)] += (endHeur - startHeur);
            prevHeuristic = heuristics.get(currHeuristic);
            //System.out.println(problem.getFunctionValue(0) + " " + lastCalledHeuristic + improv);
            currentTime = System.currentTimeMillis() - startTime;
            //break;
        }

        System.out.println("Número de iterações executadas: " + numberOfIterations);
        System.out.println("Ultima iteração onde um ótimo foi encontrado: " + lastIterationBest);
        //System.out.println("Parâmetros finais para o HyFlex: ");
        //double x[] = selection.getLevelOfChangeList();
        System.out.println("Matriz Heuristicas:");
        printDoubleMatrix(transHeuristics);
        System.out.println("");
        System.out.println("Matriz Parâmetros:");
        printDoubleMatrix(transParameters);
        System.out.println("");
        System.out.println("Matriz Acceptances:");
        printDoubleMatrix(transAcceptances);
        System.out.println("");
        System.out.println("Vezes usada: ");
        for (int i = 0; i < realNumberOfHeuristics; i++) {
            System.out.println("Heurística: " + heuristics.get(i) + " " + timesUsed[heuristics.get(i)] + " " + fmt.format(timesUsed[heuristics.get(i)] * 100.0 / numberOfIterations) + "% Tempo "
                    + TimeUnit.NANOSECONDS.toMillis(totalTime[heuristics.get(i)]));
        }
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

    //roulette wheel selection
    private int select(double[][] matrix, int llh) {
        double rand = rng.nextDouble();
        int index = 0;
        //System.out.println("Número sorteado: " + rand);
        double sum = matrix[llh][index];
        while (rand > sum) {
            index++;
            sum += matrix[llh][index];
        }
        //System.out.println("Index escolhido: "  + index);
        return index;
    }

    private void initializeNonCrossoverHeuristics() {
        for (int i = 0; i < local_search_heuristics.length; i++) {
            heuristics.add(local_search_heuristics[i]);
        }
        for (int i = 0; i < ruin_recreate_heuristics.length; i++) {
            heuristics.add(ruin_recreate_heuristics[i]);
        }
        for (int i = 0; i < mutation_heuristics.length; i++) {
            heuristics.add(mutation_heuristics[i]);
        }
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
        double prob = 1.0 / (double) tam;
        for (int i = 0; i < realNumberOfHeuristics; i++) {
            for (int j = 0; j < tam; j++) {
                matrix[i][j] = prob;
            }
        }
    }

    private void updateScore(int[][] matrix, int heuristic, int selectedIndex) {
        matrix[heuristic][selectedIndex] += 1;
    }

    private void updateProbabilities(int[][] scores, double[][] matrix, int heuristic, int selectedIndex) {
        double sum = 0.0;
        for (int j = 0; j < scores[heuristic].length; j++) {
            sum += scores[heuristic][j];
        }
        for (int j = 0; j < matrix[heuristic].length; j++) {
            matrix[heuristic][j] = (double) scores[heuristic][j] / (double) sum;
        }
    }

    private int[] getAcceptances() {
        int[] aux = new int[Vars.numberOfAcceptances];
        for (int i = 0; i < Vars.numberOfAcceptances; i++) {
            if (i == 0) {
                aux[i] = 0;
            } else {
                aux[i] = 1 + aux[i - 1];
            }
        }
        return aux;
    }

    private void printDoubleMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(fmt.format(matrix[i][j]) + " ");
            }
            System.out.println("");
        }
    }

    private void printIntMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println("");
        }
    }

}
