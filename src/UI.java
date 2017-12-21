import javafx.application.Application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class UI extends Application implements Observer {
    /**
     * the main entry point of the program. it starts the application if there arent any args, if there is one then it will try to load a
     * network from the file and let you play it
     * @param args (optional) the location of the network
     */
    public static void main(String[] args) {
        if(args.length == 1){
            try {
                FileInputStream fis = new FileInputStream(args[0]);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object obj = ois.readObject();
                if(obj instanceof Network){
                    Network n = (Network)obj;

                    TicTacToe game = new TicTacToe();
                    Scanner s = new Scanner(System.in);
                    System.out.println(game.toString());
                    while(game.getWinner().equals(".")){
                        System.out.print("row col: ");
                        int row = s.nextInt();
                        int col = s.nextInt();
                        game.makeMove(row,col);

                        game.makeMove(n.run(game.toInput()));

                        System.out.println(game.toString());
                    }
                    System.out.println("And the winner is...");
                    System.out.println(game.getWinner());
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else {
            Application.launch();
        }
    }
    private Network n;
    private BorderPane bp;
    private GridPane gp;
    private Boolean started = false;

    /**
     * initalizes the Evolution
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        super.init();
        int[] hidenSizes = {9,9,9};
        Evolution evolution = new Evolution(18,9,hidenSizes,9);
        evolution.addObserver(this);
        new Thread(()->{
            Network n = evolution.run(-1);
            System.out.print("we have a winner with a lifetime of ");
            System.out.println(n.lifeTime);
            TicTacToe game = new TicTacToe();
            Scanner s = new Scanner(System.in);
            System.out.println(game.toString());
            this.n = n;
            while(game.getWinner().equals(".")){
                System.out.print("row col: ");
                int row = s.nextInt();
                int col = s.nextInt();
                game.makeMove(row,col);

                game.makeMove(n.run(game.toInput()));

                System.out.println(game.toString());
            }
            System.out.println("And the winner is...");
            System.out.println(game.getWinner());
            System.out.println("Enter location to save the network");
            String loc = s.next();
            while(loc==""){
                loc = s.next();
            }
            try {
                FileOutputStream fos = new FileOutputStream(loc);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(n);
                oos.flush();
                oos.close();
            }catch (Exception ex){

            }
        }).start();

    }

    /**
     * Sets up the GUI
     * @param primaryStage the stage to put everything in
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        bp = new BorderPane();
        gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        bp.setCenter(gp);
        Scene myScene = new Scene(bp);
        primaryStage.setScene(myScene);
        primaryStage.show();
        started = true;
    }

    /**
     * runs whenever the evolution completes one round it updates the GUI with pretty pictures
     * @param o the observable object
     * @param arg the arg given. this should be the list of networks
     */
    @Override
    public void update(Observable o, Object arg) {
        if(bp!=null){
            if(arg instanceof Network[]) {
                if(started) {
                    Platform.runLater(() -> {
                        Network[] networks = (Network[])arg;
                        gp.getChildren().clear();
                        int max = 0;
                        for(int i = 1; i< networks.length;i++){
                            if(networks[i].lifeTime>networks[max].lifeTime){
                                max = i;
                            }
                        }
                        for(int i = 0; i< networks.length;i++) {
                            gp.add(networks[i].draw(200,200,5,(i==max)),i%6,(int)Math.floor(i/6));
                        }
                    });
                }
            }
        }
    }
}
