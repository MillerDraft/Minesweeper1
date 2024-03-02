import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class BackEnd {
    // Inner class to create objects(buttons) with JButton properties
    // , and it positions on the board (r)row, (c)column
    public static class MineTile extends JButton {
        private int r;
        private int c;
        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }

    }
    private JLabel textLabel;

    private ArrayList<MineTile> mineList;
    protected JPanel boardPanel;
    private MineTile[][] board;
    protected int tileSize, rows, cols,  defaultMines, totalMines;
    protected int boardCleared;
    protected boolean gameOver;
    private Sound sound;
    public BackEnd(JLabel textLabel){
        this.textLabel = textLabel;
        boardPanel = new JPanel(new GridLayout());
        tileSize = 80;
        rows = 8;
        cols = rows;
        defaultMines = 10;
        totalMines = defaultMines;
        gameOver = false;
        boardCleared = 0;
        sound = new Sound();

    }

    public void setTotalMines(int mines){
        totalMines = mines;
    }

    public void setBoardPanel(){
        boardPanel = new JPanel(new GridLayout(rows, cols)); // 8x8
        board = new MineTile[rows][cols]; // 2D array to store each tile
        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c++){
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                // Font that allow to use emojis
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0,0,0)); // space between tiles

                // Depending on mouse button will be the action
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver){  // Disable if game over
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        // left click
                        if (e.getButton() == MouseEvent.BUTTON1){
                            if (tile.getText().isEmpty() && tile.isEnabled()){
                                if (mineList.contains(tile)){
                                    revealMines(); // Show all mines
                                }
                                else{
                                    // Show number of surrounding mines,
                                    // or all empty tile that are next to each other,
                                    // starting for this one.
                                    checkMines(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3){
                            if (tile.getText().isEmpty()){
                                tile.setText("🚩");
                            }
                            else if (tile.getText().equals("🚩")){
                                tile.setText("");
                            }
                        }

                    }
                });

                boardPanel.add(tile);


            }
        }


    }
    // Set mine location method
    public void setMines(){
        mineList = new ArrayList<MineTile>();

        /*mineList.add(board[4][1]);  testing purpose
        mineList.add(board[0][0]);
        mineList.add(board[3][7]);
         */

        int mines = totalMines;
        Random random = new Random();
        // Random mines position
        while(mines > 0){
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if(!mineList.contains(board[r][c])){
                mineList.add(board[r][c]);
                mines --;
            }
        }
    }
    // Reveal mine method end game by loss
    public void revealMines(){
        gameOver = true;
        for (MineTile tile : mineList){
            tile.setText("💣");
            playSound(0);
        }
        updateLabel(gameOver);
    }

    // Check mines around
    public void checkMines(int r, int c){

        // base case out of the board limit
        if (r < 0 || r >= rows || c < 0 || c >= cols){
            return;
        }
        MineTile tile = board[r][c];

        // base case, tile was already checked
        if (!tile.isEnabled()){
            return;
        }
        tile.setEnabled(false);
        boardCleared ++;

        int minesFound = 0;

        // Check neighbours
        minesFound += mineCount(r-1, c-1); // top left
        minesFound += mineCount(r-1, c);     // up
        minesFound += mineCount(r-1, c+1); // top right

        minesFound += mineCount(r, c-1);    // left
        minesFound += mineCount(r, c+1);    // right

        minesFound += mineCount(r+1, c-1); // bottom left
        minesFound += mineCount(r+1, c);     // down
        minesFound += mineCount(r+1, c+1);// bottom right

        if (minesFound > 0){
            tile.setText(Integer.toString(minesFound)); // Update textLabel
            playSound(1); // call sound method
        }
        else{
            tile.setText("");
            playSound(1);
            // Recursively call method
            checkMines(r-1, c-1); // top left
            checkMines(r-1, c);     // up
            checkMines(r-1, c+1); // top right

            checkMines(r, c-1);    // left
            checkMines(r, c+1);    // right

            checkMines(r+1, c-1); // bottom left
            checkMines(r+1, c);     // down
            checkMines(r+1, c+1);// bottom right
        }

        // Game over by Win
        if (boardCleared == rows * cols - mineList.size()){
            gameOver = true;
            updateLabel(gameOver);

        }
    }

    public void updateLabel(boolean gameOver){
        if (gameOver && boardCleared == rows * cols - mineList.size()){
            textLabel.setText("Mines Cleared 👏");
        }
        else{
            textLabel.setText("💥💥💥 Game Over");
        }

    }

    // Count mines around method
    public int mineCount(int r, int c) {
        // base case out of the board limit
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return 0;
        }
        return mineList.contains(board[r][c]) ? 1 : 0;

    }
    // Music effect method
    public void playSound(int i){
        sound.setFile(i); // load file
        sound.play();     // make sound effect
    }
}
