public class Sigmoid {
    public static double function(double x){
        return 1/(1+Math.exp(-x));
    }
    public static double derivitive(double x){
        return function(x)*(1-function(x));
    }
}
