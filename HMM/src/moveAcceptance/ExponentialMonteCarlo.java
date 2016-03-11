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
public class ExponentialMonteCarlo extends AcceptanceCriterion {

    public ExponentialMonteCarlo(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
        //accept better
        if (newFitness < currentFitness) {
            acp = true;
            if (newFitness < bestFitness) {
                numberOfIterationsStuck = 0;
                Vars.isAtStuck = false;
            }

        } //accept equal
        else if (newFitness == currentFitness) {
            acp = true;
            numberOfIterationsStuck++;
        } //worsen
        else {
            double rnd = r.nextDouble();
            // System.out.println("Random: " + rnd);
            double a = Math.exp(((currentFitness - newFitness) / currentFitness));
            //System.out.println("Random: " + rnd + " P: " + a);
            if (rnd < a) {
                acp = true;
            }
        }
        return acp;
    }
}
