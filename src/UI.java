import java.util.Scanner;

public class UI {
    public static void main(String[] args) {
        int[] hidenSizes = {9,9};
        Evolution evolution = new Evolution(10,9,hidenSizes,9);
        Network n = evolution.run(100000);
        System.out.print("we have a winner with a lifetime of ");
        System.out.println(n.lifeTime);
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
}
