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

    private final double D = 0.0003;

    public RecordToRecord(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
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
        else if (newFitness < (currentFitness + D)) {
            acp = true;
        }

        return acp;
    }

}
