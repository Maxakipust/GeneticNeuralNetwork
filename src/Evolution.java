import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Evolution {
    public double mutationAmmount = 0.5;
    public Network[] networks;
    public Random r = new Random();
    public Evolution(int size, int inputSize, int[] hiddenSizes, int outputSize){
        networks = new Network[size];
        for(int i = 0; i<networks.length;i++){
            networks[i] = new Network(inputSize, hiddenSizes, outputSize);
        }
    }

    public Network run(int itterations){
        for(int i = 0; i< itterations;i++){
            evolve();
            System.out.println(i);
        }
        Network max = networks[0];
        for(int i = 1; i< networks.length;i++){
            if(max.lifeTime<networks[i].lifeTime){
                max = networks[i];
            }
        }
        return max;
    }


    private void evolve(){
        for(int i = 0; i< networks.length; i+=2){
            int result = compete(networks[i],networks[i+1]);
            if(result==-1){
                networks[i+1] = copy(networks[i]);
                networks[i+1].mutate(mutationAmmount);
                networks[i].lifeTime +=1;
            }else if(result == 1){
                networks[i] = copy(networks[i+1]);
                networks[i].mutate(mutationAmmount);
                networks[i+1].lifeTime +=1;
            }else if(result == 0){
                networks[i].lifeTime+=1;
                networks[i+1].lifeTime+=1;
            }
        }
        shuffle(2*networks.length);
    }

    public Network copy(Network n){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(n);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            Network network = (Network)in.readObject();
            network.lifeTime = 0;
            return network;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return(null);
    }

    //fill this out return -1 if the first one wins, 0 if tie, 1 if the second one wins
    public int compete(Network a, Network b){
        TicTacToe game = new TicTacToe();
        while(game.getWinner().equals(".")){
            if(!game.makeMove(a.run(game.toInput()))){
                return 1;
            }
            if(!game.makeMove(b.run(game.toInput()))){
                return -1;
            }
        }
        if(game.getWinner().equals("o")){
            return -1;
        }else if(game.getWinner().equals("x")){
            return 1;
        }
        return 0;
    }

    private void shuffle(int amount){
        for(int i = 0; i< amount;i++){
            int first = r.nextInt(networks.length);
            int second = r.nextInt(networks.length);
            while(first != second){
                first = r.nextInt(networks.length);
                second = r.nextInt(networks.length);
            }
            Network temp = networks[first];
            networks[first] = networks[second];
            networks[second] = temp;
        }
    }
}
