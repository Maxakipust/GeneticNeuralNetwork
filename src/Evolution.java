

import sun.nio.ch.Net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class Evolution extends Observable{
    public double mutationAmmount = 0.001;
    public Network[] networks;
    public Random r = new Random(new Date().getTime());
    public int ID = 0;
    public static boolean shouldRun = true;

    /**
     * A class to manage the evolution of the neural networks. as it is it trains them to play tic tac toe
     * @param size the number of networks to train
     * @param inputSize the input size for the networks
     * @param hiddenSizes the hidden sizes for the networks
     * @param outputSize the output side for the network
     */
    public Evolution(int size, int inputSize, int[] hiddenSizes, int outputSize){
        networks = new Network[size];
        for(int i = 0; i<networks.length;i++){
            networks[i] = new Network(inputSize, hiddenSizes, outputSize,ID, r);
            ID++;
        }
    }

    /**
     * runs the evolution of the networks
     * @param itterations the number of times to run it. set to -1 if you want to run until user input
     * @return the network that has survived the longest and is still alive at the end
     */
    public Network run(int itterations){
        if(itterations==-1){
            new Thread(()->{
                Scanner s = new Scanner(System.in);
                s.nextLine();
                shouldRun = false;
            }).start();
            int life = 0;
            while(shouldRun){
                evolve();
                System.out.println(life);
                Network max = networks[0];
                for (int x = 1; x < networks.length; x++) {
                    if (max.lifeTime < networks[x].lifeTime) {
                        max = networks[x];
                    }
                }
                System.out.println("Max id: " + max.ID);
                System.out.println("Max was created on " + (life - max.lifeTime) + " and has been alive for " + max.lifeTime);
                setChanged();
                notifyObservers(networks);
                life++;
            }
        }else {
            for (int i = 0; i < itterations; i++) {
                evolve();
                System.out.println(i);
                Network max = networks[0];
                for (int x = 1; x < networks.length; x++) {
                    if (max.lifeTime < networks[x].lifeTime) {
                        max = networks[x];
                    }
                }
                System.out.println("Max id: " + max.ID);
                System.out.println("Max was created on " + (i - max.lifeTime) + " and has been alive for " + max.lifeTime);
                if (i % 10 == 0) {
                    setChanged();
                    notifyObservers(max);
                }
            }
        }
        Network max = networks[0];
        for(int i = 1; i< networks.length;i++){
            if(max.lifeTime<networks[i].lifeTime){
                max = networks[i];
            }
        }
        return max;
    }

    /**
     * manages the reproducing and mutating of the networks.
     */
    private void evolve(){
        for(int i = 0; i< networks.length-1; i+=2){
            int result = compete(networks[i],networks[i+1]);
            if(result==-1){
                networks[i+1].health-=1;
                if(networks[i+1].health<1) {
                    networks[i + 1] = copy(networks[i]);
                    networks[i + 1].mutate(mutationAmmount, r);
                    networks[i + 1].ID = ID;
                    ID++;
                }
                networks[i].lifeTime +=1;
            }else if(result ==-2) {
                networks[i + 1] = copy(networks[i]);
                networks[i + 1].mutate(mutationAmmount, r);
                networks[i + 1].ID = ID;
                ID++;
                networks[i].lifeTime +=1;
            }else if(result == 1){
                networks[i].health-=1;
                if(networks[i].health<1) {
                    networks[i] = copy(networks[i + 1]);
                    networks[i].mutate(mutationAmmount, r);
                    networks[i].ID = ID;
                    ID++;
                }
                networks[i+1].lifeTime +=1;
            }else if(result==2){
                networks[i] = copy(networks[i + 1]);
                networks[i].mutate(mutationAmmount, r);
                networks[i].ID = ID;
                ID++;
                networks[i+1].lifeTime +=1;
            }else if(result == 0){
                networks[i].lifeTime+=1;
                networks[i+1].lifeTime+=1;
            }
        }
        shuffle(50*networks.length);
    }

    /**
     * performs a deep copy of a network by serializing it and un-serializing it.
     * @param n the network to copy
     * @return a deep copy of the network
     */
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

    /**
     * This is the function where the magic happens. it is the Coliseum for neural networks, the two networks play an epic game of tic
     * tac toe agenst each other. if one of them makes an invalid move then it dies. if they somehow make it to the end of a game without
     * making an invalid move then the loser loses a health
     * @param a the first contestant
     * @param b the second contestant
     * @return -2 if b should be destroyed, -1 if b should lose a life, 0 if a tie, 1 if a should lose a life, 2 if a should be destroyed
     */
    //fill this out return -1 if the first one wins, 0 if tie, 1 if the second one wins
    public int compete(Network a, Network b){
        TicTacToe game = new TicTacToe();
        boolean Afirst = r.nextDouble()>0.5;
        while(game.getWinner().equals(".")){
            if(Afirst) {
                if (!game.makeMove(a.run(game.toInput()))) {
                    //System.out.println("Network "+a.ID+" made an invalid move");
                    return 2;
                }
                if (!game.makeMove(b.run(game.toInput()))) {
                    //System.out.println("Network "+b.ID+" made an invalid move");
                    return -2;
                }
            }else {
                if (!game.makeMove(b.run(game.toInput()))) {
                    //System.out.println("Network "+b.ID+" made an invalid move");
                    return -2;
                }
                if (!game.makeMove(a.run(game.toInput()))) {
                    //System.out.println("Network "+a.ID+" made an invalid move");
                    return 2;
                }
            }
        }
        if(game.getWinner().equals("o")){
            System.err.println("Network "+a.ID+" Wins");
            return -1;
        }else if(game.getWinner().equals("x")){
            System.err.println("Network "+b.ID+" Wins");
            return 1;
        }
        return 0;
    }

    /**
     * shuffles the networks randomly so they fight new networks each time
     * @param amount how much to shuffle it by
     */
    private void shuffle(int amount){
        /**
         * uncomment me and comment the rest of the function if you want it to sort by lifetime
        for(int i = 1; i<networks.length;i++){
            while(networks[i].lifeTime>networks[i-1].lifeTime){
                Network Temp = networks[i];
                networks[i]=networks[i-1];
                networks[i-1]=networks[i];
            }
        }**/
        for(int i = 0; i< amount;i++){
            int first = r.nextInt(networks.length);
            int second = r.nextInt(networks.length);
            while(first == second){
                first = r.nextInt(networks.length);
                second = r.nextInt(networks.length);
            }
            Network temp = networks[first];
            networks[first] = networks[second];
            networks[second] = temp;
        }
    }
}
