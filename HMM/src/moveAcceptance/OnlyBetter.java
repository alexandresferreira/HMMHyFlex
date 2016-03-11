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
public class OnlyBetter extends AcceptanceCriterion {

    public OnlyBetter(Random r) {
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
        else{
            //acp = true;
            numberOfIterationsStuck++;
        }
        //accept worse if 120 iterations been passed
        if(numberOfIterationsStuck == 120){
            acp = true;
            System.out.println("Aceitou Pior. Resetou!");
            numberOfIterationsStuck = 0;
        }
        return acp;
    }
}

