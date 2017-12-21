import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Network implements Serializable{
    //static stuff


    //non-static stuff
    LinkedList<Neuron> inputLayer = new LinkedList<>();
    LinkedList<LinkedList<Neuron>> hiddenLayers = new LinkedList<>();
    LinkedList<Neuron> outputLayer = new LinkedList<>();
    public int lifeTime = 0;
    public int ID = 0;
    public int health = 5;
    //private Random r;

    /**
     * A neural network
     * @param inputSize the number of input nodes
     * @param hiddenSizes the number of hidden nodes in each hidden layer
     * @param outputSize the number of output nodes
     * @param ID the id for this network
     * @param r a random to be used to create the network
     */
    public Network(int inputSize, int[] hiddenSizes, int outputSize, int ID, Random r){
        this.ID = ID;
        //this.r = r;
        //input layer
        for(int i = 0; i< inputSize;i++){
            inputLayer.add(new Neuron(r));
        }
        //first hidden layer
        LinkedList<Neuron> firstHidden = new LinkedList<>();
        for(int i = 0; i<hiddenSizes[0];i++){
            firstHidden.add(new Neuron(inputLayer, r));
        }
        hiddenLayers.add(firstHidden);

        //rest of the hidden layers
        for(int i = 1; i<hiddenSizes.length; i++){
            LinkedList<Neuron> hiddenLayer = new LinkedList<>();
            for(int j = 0; j<hiddenSizes[i]; j++){
                hiddenLayer.add(new Neuron(hiddenLayers.get(i-1),r));
            }
            hiddenLayers.add(hiddenLayer);
        }

        //output layers
        for(int i = 0; i<outputSize; i++){
            outputLayer.add(new Neuron(hiddenLayers.getLast(),r));
        }
    }

    /**
     * mutates the network up to an amount
     * @param amount the amount to mutate the network by
     * @param r a random to be used to see how much it should mutate
     */
    public void mutate(double amount, Random r){
        inputLayer.forEach((Neuron n)->{n.mutate(amount,r);});
        hiddenLayers.forEach((LinkedList<Neuron> ns)->{ns.forEach((Neuron n) -> {n.mutate(amount,r);});});
        outputLayer.forEach((Neuron n)->{n.mutate(amount,r);});
    }

    /**
     * runs the network with a given set of inputs
     * @param input the inputs for the network
     * @return the outputs for the network, these are raw data
     */
    public double[] run(double[] input){
        for(int i = 0; i<inputLayer.size();i++){
            inputLayer.get(i).value = input[i];
        }
        for(int i = 0; i<hiddenLayers.size();i++){
            for(int j=0; j<hiddenLayers.get(i).size();j++){
                hiddenLayers.get(i).get(j).calcValue();
            }
        }
        double[] ret = new double[outputLayer.size()];
        for(int i = 0; i<outputLayer.size();i++){
            ret[i] = outputLayer.get(i).calcValue();
        }
        return ret;
    }

    /**
     * draws the network onto a canvas. green is positive, red is negative, transparent is 0
     * @param w the width of the canvas
     * @param h the height of the canvas
     * @param size the size of each circle
     * @param winner if it is the winner
     * @return a canvas with the network drawn on it
     */
    public Canvas draw(int w, int h, int size, boolean winner){
        HashMap<Neuron, Point2D> locations = new HashMap<>();
        double inputSpacing = getSpacing(h,size,inputLayer.size());
        double Xgap = getXGap(w,size,hiddenLayers.size()+2);
        int x = 0;
        for(int i = 0; i< inputLayer.size();i++){
            locations.put(inputLayer.get(i),new Point2D(x,getY(inputSpacing,size,i)));
        }
        for(int i = 0; i< hiddenLayers.size();i++){
            double ySpacing = getSpacing(h,size,hiddenLayers.get(i).size());
            x+=Xgap+size;
            for(int j = 0; j<hiddenLayers.get(i).size();j++){
                locations.put(hiddenLayers.get(i).get(j),new Point2D(x,getY(ySpacing,size,j)));
            }
        }
        double ySpacing = getSpacing(h,size,outputLayer.size());
        x+=Xgap+size;
        for(int i =0; i< outputLayer.size();i++){
            locations.put(outputLayer.get(i),new Point2D(x,getY(ySpacing,size,i)));
        }

        Canvas ret = new Canvas(w,h);
        GraphicsContext cg = ret.getGraphicsContext2D();

        if(winner){
            cg.setFill(Color.YELLOW);
            cg.fillRect(0,0,w,h);
            cg.setFill(Color.BLACK);
        }
        for(int i = 0; i<inputLayer.size();i++){
            for(int j = 0; j<inputLayer.get(i).outputSynapses.size();j++){
                Point2D start = locations.get(inputLayer.get(i));
                Point2D end = locations.get(inputLayer.get(i).outputSynapses.get(j).outputNeuron);
                double weight = inputLayer.get(i).outputSynapses.get(j).weight;
                double sigW =Sigmoid.function(weight);
                cg.setStroke(weight>0?Color.color(0,1,0,1-sigW):Color.color(1,0,0,1-sigW));
                //cg.setLineWidth(weight);
                cg.strokeLine(start.getX()+(size/2), start.getY()+(size/2), end.getX()+(size/2), end.getY()+(size/2));
            }
        }
        for(LinkedList<Neuron> ll:hiddenLayers){
            for(Neuron n:ll){
                for(Synapse s: n.outputSynapses){
                    Point2D start = locations.get(n);
                    Point2D end = locations.get(s.outputNeuron);
                    double weight = s.weight;
                    double sigW =Sigmoid.function(weight);
                    cg.setStroke(weight>0?Color.color(0,1,0,1-sigW):Color.color(1,0,0,1-sigW));
                    //cg.setLineWidth(weight);
                    cg.strokeLine(start.getX()+(size/2), start.getY()+(size/2), end.getX()+(size/2), end.getY()+(size/2));
                }
            }
        }
        for(Neuron n:locations.keySet()){
            double xloc = locations.get(n).getX();
            double yloc = locations.get(n).getY();
            cg.fillOval(xloc,yloc,size,size);
        }
        cg.fillText("HP:"+health+"  ID:"+ID+"  LT:"+lifeTime,0,h);
        return ret;
    }

    /**
     * gets the vertical spacing for a given row of nodes
     * @param h the height of the canvas
     * @param size the size of a circle
     * @param num the number of nodes
     * @return the number of vertical pixels between node
     */
    private double getSpacing(double h, double size, double num){
        return (h-(size*num))/(num+1);
    }

    /**
     * gets the horizontal spacing for a given set of cols
     * @param w the width of the canvas
     * @param size the size of a circle
     * @param num the number of cols
     * @return the number of pixels between each col
     */
    private double getXGap(double w, double size, double num){
        return (w-(size*num))/(num-1);
    }

    /**
     * gets the y position of a circle
     * @param gap the ygap size
     * @param size the size of a circle
     * @param i the index it is in the row
     * @return the y position in pixels
     */
    private double getY(double gap, double size, double i){
        return (i*gap+(i-0.5)*size);
    }

    /**
     * the toString function. if you need help with this... god help you
     * @return the id
     */
    @Override
    public String toString() {
        return ""+ID;
    }
}
