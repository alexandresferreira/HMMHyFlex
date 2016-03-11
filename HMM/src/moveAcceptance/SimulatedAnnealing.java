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
 * @author asferreira
 */
public class SimulatedAnnealing extends AcceptanceCriterion {
    
    private double cooling;
    
    public SimulatedAnnealing(Random r) {
        super(r);
    }

    @Override
    public boolean accept(double newFitness, double currentFitness, double bestFitness) {
        boolean acp = false;
        //accept better
        if(newFitness < currentFitness){
            acp = true;
            if(newFitness < bestFitness){
                numberOfIterationsStuck = 0;
                Vars.isAtStuck = false;
            }
            
        }
        //accept equal
       else if(newFitness == currentFitness){
            acp = true;
            numberOfIterationsStuck++;
       }
        //accept with probability
        else{
            double rnd = r.nextDouble();   
           // System.out.println("Random: " + rnd);
            double a = Math.exp(((currentFitness - newFitness) / currentFitness)/Vars.temperature);
            //System.out.println("Random: " + rnd + " P: " + a);
            if(rnd < a){
                acp = true;
            }
        }
        if(Vars.reduceTemperature){
            reduceTemperature();
        }
        if(numberOfIterationsStuck == Vars.iterMax){
            System.out.println("Resetou!");
            resetCoolingSchedule();
            numberOfIterationsStuck = 0;
            Vars.numberOfRestarts++;
        }
        return acp;
    }
    
    @Override
    public void updateCooling(){
        this.cooling = 1.0 - (1.0/ (double) Vars.iterMax);
    }

    private void reduceTemperature(){
        Vars.temperature = Vars.temperature * this.cooling;
        if(Vars.temperature <= 0.0){
            Vars.temperature = 0.0001;
        }
    }
    
    private void resetCoolingSchedule(){
        Vars.temperature = 1;
    }
    
}
