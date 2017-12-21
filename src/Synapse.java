import java.io.Serializable;
import java.util.Random;

public class Synapse implements Serializable{
    public Neuron inputNeuron;
    public Neuron outputNeuron;
    public double weight;
    //private Random r;

    /**
     * a synapse or connecter between two neurons.
     * @param inputNeuron the input neuron
     * @param outputNeuron the output neuron
     * @param r a random to generate random numbers
     */
    public Synapse(Neuron inputNeuron, Neuron outputNeuron, Random r) {
        this.inputNeuron = inputNeuron;
        //this.r = r;
        this.outputNeuron = outputNeuron;
        this.weight = (r.nextDouble()*2)-1;
    }

    /**
     * mutates the synapse
     * @param amount the amount to mutate by
     * @param r a random to generate random numbers
     */
    public void mutate(double amount, Random r){
        this.weight+=(r.nextDouble()*2*amount)-amount;
    }
}
