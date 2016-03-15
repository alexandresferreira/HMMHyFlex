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

    private final double T = 0.20;

    public RecordToRecord(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
        double threshold;
        if (newFitness < currentFitness) {
            acp = true;
            if (newFitness < bestFitness) {
                numberOfIterationsStuck = 0;
                Vars.isAtStuck = false;
            }
        }
        else if (newFitness  == currentFitness) {
            acp = true;
        }
        else if (newFitness < (bestFitness+(bestFitness*T))) {
            acp = true;
        }
        return acp;
    }

}
