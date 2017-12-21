public class Sigmoid {
    /**
     * a sigmoid function. squshes the real numberline between 0 and 1
     * @param x the number
     * @return the squshed number
     */
    public static double function(double x){
        return 1/(1+Math.exp(-x));
    }
}
