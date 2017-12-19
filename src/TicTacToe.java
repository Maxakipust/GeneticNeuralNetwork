public class TicTacToe {
    String [][] board = new String[3][3];
    String turn = "o";
    public TicTacToe(){
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j< board[i].length; j++){
                board[i][j] = ".";
            }
        }
    }
    public boolean makeMove(int row, int col){
        if(board[row][col].equals(".")) {
            board[row][col] = turn;
        }else{
            turn = turn.equals("o")?"x":"o";
            return false;
        }
        turn = turn.equals("o")?"x":"o";
        return true;
    }

    public boolean makeMove(double[] input){
        int max = 0;
        for(int i = 1; i<input.length;i++){
            if(input[max]<input[i]){
                max = i;
            }
        }
        int row = (int)Math.floor(max/board.length);
        int col = (int)(max%board.length);
        return makeMove(row,col);

    }

    public String getWinner(){
        if(board[0][0]==board[0][1]&&board[0][1]==board[0][2]){
            if(!board[0][0].equals(".")) {
                return board[0][0];
            }
        }
        if(board[1][0]==board[1][1]&&board[1][1]==board[1][2]){
            if(!board[1][0].equals(".")) {
                return board[1][0];
            }
        }
        if(board[2][0]==board[2][1]&&board[2][1]==board[2][2]){
            if(!board[2][0].equals(".")) {
                return board[2][0];
            }
        }

        if(board[0][0]==board[1][0]&&board[1][0]==board[2][0]){
            if(!board[0][0].equals(".")) {
                return board[0][0];
            }
        }
        if(board[0][1]==board[1][1]&&board[1][1]==board[2][1]){
            if(!board[0][1].equals(".")) {
                return board[0][1];
            }
        }
        if(board[0][2]==board[1][2]&&board[1][2]==board[2][2]){
            if(!board[0][2].equals(".")) {
                return board[0][2];
            }
        }

        if(board[0][0]==board[1][1]&&board[1][1]==board[2][2]){
            if(!board[0][0].equals(".")) {
                return board[0][0];
            }
        }
        if(board[0][2]==board[1][1]&&board[1][1]==board[2][0]){
            if(!board[0][2].equals(".")) {
                return board[0][2];
            }
        }
        return ".";
    }
    public double[] toInput(){
        double[] ret = new double[9];
        int k =0;
        for(int i = 0; i< board.length;i++){
            for(int j = 0; j<board[i].length;j++){
                if(board[i][j].equals(turn)){
                    ret[k] = 1;
                }else if(board[i][j].equals(".")){
                    ret[k] = 0;
                }else{
                    ret[k] = -1;
                }
                k++;
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        for(int i = 0; i<board.length; i++){
            for(int j = 0; j<board[i].length;j++){
                ret+=board[i][j];
            }
            ret+="\n";
        }
        return ret;
    }
}
