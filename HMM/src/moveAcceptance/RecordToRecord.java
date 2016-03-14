/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveAcceptance;

import java.util.Random;
import util.Vars;

/**
 *
 * @author Alexandre
 */
public class RecordToRecord extends AcceptanceCriterion {

    private final double T = 30;

    public RecordToRecord(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
        double threshold;
        //threshold
        if (bestFitness < 1) {
            //System.out.println("Best Fitness: " + bestFitness);
            double x = T / bestFitness;
            double aux = 10;
            //int i = 0;
            while ((Math.floor(x - (x % aux)) != aux) && ((Math.floor(x - (x % aux))/aux) > 10.0)) {
                //System.out.println("aux: " + Math.floor(x - (x % aux)));
                //System.out.println("resultado" + Math.floor(x - (x % aux))/aux);
                aux *= 10;
                //System.out.print("i: " + i + " ");
                //i++;
            }
            threshold = T / (aux);
            System.out.println("Treshold: " + threshold);
        } else {
            threshold = bestFitness + T;
        }
        //acceptance
        if (newFitness < currentFitness) {
            acp = true;
            if (newFitness < bestFitness) {
                numberOfIterationsStuck = 0;
                Vars.isAtStuck = false;
            }
        }
        //worsen
        else if (newFitness  == currentFitness) {
            acp = true;
        }
        else if (newFitness < threshold) {
            acp = true;
        }
        return acp;
    }

}
