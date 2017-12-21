
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Neuron implements Serializable{
    public List<Synapse> inputSynapses = new LinkedList<>();
    public List<Synapse> outputSynapses = new LinkedList<>();
    private double bias;
    public double value;
    //private Random r;

    /**
     * a single neuron without any inputs in the network this one should be called to create the input neurons
     * @param r a random to start out with
     */
    public Neuron(Random r){
        //this.r = r;
        this.bias = r.nextDouble();
    }

    /**
     * a single neuron with outputs. this should be used for all neurons not in the input layer
     * @param inputNeurons a list of the input neurons for this neuron
     * @param r a random to start out with
     */
    public Neuron(LinkedList<Neuron> inputNeurons, Random r){
        //this.r = r;
        for(Neuron n:inputNeurons){
            Synapse s = new Synapse(n,this, r);
            n.outputSynapses.add(s);
            inputSynapses.add(s);
        }
    }

    /**
     * mutates this neuron and its children synapses
     * @param amount the amount to mutate it by
     * @param r the random to use to get a random number
     */
    public void mutate(double amount, Random r){
        this.bias += (r.nextDouble()*2*amount)-amount;
        outputSynapses.forEach((Synapse s) ->s.mutate(amount, r));
    }

    /**
     * calculates the value of the neuron.
     * @return the value of the neuron
     */
    public double calcValue(){
        double ret = 0;
        for(Synapse s:inputSynapses){
            ret+=s.weight*s.inputNeuron.value;
        }
        ret = Sigmoid.function(ret);
        value = ret;
        return(ret);
    }
}
