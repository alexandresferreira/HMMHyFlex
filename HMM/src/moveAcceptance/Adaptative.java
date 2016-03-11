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
public class Adaptative extends AcceptanceCriterion {

    private double adaptativeRate = 0;
    private int nIterationsToUpdate = 50;
    
    public Adaptative(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
        //increase the acceptance percentage rate if the number of iteration is reacheds
        if(numberOfIterationsStuck == nIterationsToUpdate){
            adaptativeRate += 0.05;
            numberOfIterationsStuck = 0;
        }
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
            this.numberOfIterationsStuck++;
        } //worsen
        else {
             double rnd = r.nextDouble();
             if (rnd < adaptativeRate) {
                acp = true;
                //decreases rate
                adaptativeRate = 0;
                //if(adaptativeRate < 0){
                 //   adaptativeRate = 0;
                //}
            }
            //this.numberOfIterationsStuck++;
        }

        return acp;
    }

}
