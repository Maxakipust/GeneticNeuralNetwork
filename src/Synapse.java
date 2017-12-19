import java.io.Serializable;

public class Synapse implements Serializable{
    public Neuron inputNeuron;
    public Neuron outputNeuron;
    public double weight;

    public Synapse(Neuron inputNeuron, Neuron outputNeuron) {
        this.inputNeuron = inputNeuron;
        this.outputNeuron = outputNeuron;
        this.weight = Network.getRandom();
    }

    public void mutate(double amount){
        this.weight+=(Network.getRandom()*2*amount)-amount;
    }
}
