/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package main;

import hyperheuristic.HMM;
import AbstractClasses.ProblemDomain;
import BinPacking.BinPacking;
import SAT.SAT;
import VRP.VRP;
import FlowShop.FlowShop;
import PersonnelScheduling.PersonnelScheduling;
import java.text.DecimalFormat;
import travelingSalesmanProblem.TSP;
import util.Vars;

/**
 *
 * @author asferreira
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IllegalArgumentException {
        DecimalFormat fmt = new DecimalFormat("0.00");
        ProblemDomain problem;
        long timeLimit = 480000;
        int instance = 7;
        //long seed = 1234;
        long seed = System.currentTimeMillis();
        int acc = 0;
        //double scalingFactor = 10.0;
        problem = new BinPacking(seed);
        if (args.length == 1) {
            // problem = getProblem(args[0], seed)1
        } else if (args.length == 3) {
            problem = getProblem(args[0], seed);
            instance = Integer.parseInt(args[1]);
            timeLimit = Long.parseLong(args[2]);
        } 
        else if(args.length == 4){
            problem = getProblem(args[0], seed);
            instance = Integer.parseInt(args[1]);
            timeLimit = Long.parseLong(args[2]);
            acc = Integer.parseInt(args[3]);
        }
        else if(args.length == 5){
	    problem = getProblem(args[0], seed);
            instance = Integer.parseInt(args[1]);
            timeLimit = Long.parseLong(args[2]);
            acc = Integer.parseInt(args[3]);
            Vars.scalingFactor = Double.parseDouble(args[4]); 
        }
	else if(args.length == 6){
	    problem = getProblem(args[0], seed);
            instance = Integer.parseInt(args[1]);
            timeLimit = Long.parseLong(args[2]);
            acc = Integer.parseInt(args[3]);
            Vars.scalingFactor = Double.parseDouble(args[4]);
            Vars.windowSize = Double.parseDouble(args[5]);
	}

        problem.loadInstance(instance);
        System.out.println("Seed: " + seed);
        System.out.println("Scaling Factor: " + Vars.scalingFactor + " Tamanho da Window: " + Vars.windowSize);
        System.out.println("Problema: " + problem.toString() + " instancia: " + instance + " timeLimit: " + timeLimit);
        Vars.totalExecutionTime  = timeLimit;
        double results[] = new double[problem.getNumberOfHeuristics()];
        //for (int i = 0; i < runs; i++) {
            //MABHH mab = new MABHH(seed, problem.getNumberOfHeuristics(), timeLimit, acc);
            //mab.setTimeLimit(timeLimit);
            //mab.loadProblemDomain(problem);
            //mab.run();
            //System.out.println("Melhor Solução: " + mab.getBestSolutionValue());
    }

    public static ProblemDomain getProblem(String problem, long seed) {
        if (problem.equals("BinPacking")) {
            return new BinPacking(seed);
        } else if (problem.equals("TSP")) {
            return new TSP(seed);
        } else if (problem.equals("MAXSAT")) {
            return new SAT(seed);
        } else if (problem.equals("PersonnelScheduling")) {
            return new PersonnelScheduling(seed);
        } else if (problem.equals("VRP")) {
            return new VRP(seed);
        } else if (problem.equals("FlowShop")) {
            return new FlowShop(seed);
        }
        return null;
    }

}