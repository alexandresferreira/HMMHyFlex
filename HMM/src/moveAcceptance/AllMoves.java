/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moveAcceptance;

import java.util.Random;
/**
 *
 * @author Alexandre
 */
public class AllMoves extends AcceptanceCriterion {
    
    public AllMoves(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        return true;
    }
    
}
