
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Neuron implements Serializable{
    public List<Synapse> inputSynapses = new LinkedList<>();
    public List<Synapse> outputSynapses = new LinkedList<>();
    private double bias;
    public double value;

    public Neuron(){
        this.bias = Network.getRandom();
    }

    public Neuron(LinkedList<Neuron> inputNeurons){
        for(Neuron n:inputNeurons){
            Synapse s = new Synapse(n,this);
            n.outputSynapses.add(s);
            inputSynapses.add(s);
        }
    }

    public void mutate(double amount){
        this.bias += (Network.getRandom()*2*amount)-amount;
        outputSynapses.forEach((Synapse s) ->s.mutate(amount));
    }

    public double calcValue(){
        double ret = 0;
        for(Synapse s:inputSynapses){
            ret+=s.weight*s.inputNeuron.value;
        }
        ret = Sigmoid.function(ret);
        value = ret;
        return(ret);
    }

    public double calcError(double target){
        return(target - value);
    }
}
