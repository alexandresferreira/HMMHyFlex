/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveAcceptance;

import java.util.Random;

/**
 *
 * @author asferreira
 */
public class NaiveAcceptance extends AcceptanceCriterion {

    public NaiveAcceptance(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
        if (newFitness < currentFitness) {
            acp = true;
        } else {
            //if there is not an improvement in solution quality then we accept the solution with a 50% probability
            if (r.nextBoolean()) {
                acp = true;
            }
        }
        return acp;
    }
}
