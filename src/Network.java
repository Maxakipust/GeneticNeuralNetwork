import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Network implements Serializable{
    //static stuff
    public static Random r = new Random();
    static double getRandom(){
        return r.nextDouble();
    }

    //non-static stuff
    LinkedList<Neuron> inputLayer = new LinkedList<>();
    LinkedList<LinkedList<Neuron>> hiddenLayers = new LinkedList<>();
    LinkedList<Neuron> outputLayer = new LinkedList<>();
    int lifeTime = 0;


    public Network(int inputSize, int[] hiddenSizes, int outputSize){
        //input layer
        for(int i = 0; i< inputSize;i++){
            inputLayer.add(new Neuron());
        }
        //first hidden layer
        LinkedList<Neuron> firstHidden = new LinkedList<>();
        for(int i = 0; i<hiddenSizes[0];i++){
            firstHidden.add(new Neuron(inputLayer));
        }
        hiddenLayers.add(firstHidden);

        //rest of the hidden layers
        for(int i = 1; i<hiddenSizes.length; i++){
            LinkedList<Neuron> hiddenLayer = new LinkedList<>();
            for(int j = 0; j<hiddenSizes[i]; j++){
                hiddenLayer.add(new Neuron(hiddenLayers.get(i-1)));
            }
            hiddenLayers.add(hiddenLayer);
        }

        //output layers
        for(int i = 0; i<outputSize; i++){
            outputLayer.add(new Neuron(hiddenLayers.getLast()));
        }
    }

    public void mutate(double amount){
        inputLayer.forEach((Neuron n)->{n.mutate(amount);});
        hiddenLayers.forEach((LinkedList<Neuron> ns)->{ns.forEach((Neuron n) -> {n.mutate(amount);});});
        outputLayer.forEach((Neuron n)->{n.mutate(amount);});
    }

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

    public Canvas draw(int w, int h, int size){
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
        for(Neuron n:locations.keySet()){
            double xloc = locations.get(n).getX();
            double yloc = locations.get(n).getY();
            cg.fillOval(xloc,yloc,size,size);
        }
        return ret;
    }

    private double getSpacing(double h, double size, double num){
        return (h-(size*num))/(num+1);
    }
    private double getXGap(double w, double size, double num){
        return (w-(size*num))/(num-1);
    }
    private double getY(double gap, double size, double i){
        return (i*gap+(i-0.5)*size);
    }
}
