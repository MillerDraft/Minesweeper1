import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper {
    // Inner class to create objects(buttons) with JButton properties
    // , and it positions on the board (r)row, (c)column
    public class MineTile extends JButton {
        private int r;
        private int c;
        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }

    }
    // Attributes
    private JFrame frame;
    private JLabel textLabel;
    private JPanel textPanel, boardPanel, panelForReStart, panelForLevel;
    private JButton reStart, level;
    private JMenuItem easy;
    private JMenuItem medium;
    private JMenuItem hard;

    private int tileSize, rows, cols, boardWidth, boardHeight, boardCleared;
    private int totalMines, defaultMines;
    Font buttonFont = new Font("Arial", Font.PLAIN, 25);
    private MineTile[][] board;
    private ArrayList<MineTile> mineList;
    private boolean gameOver;
    private Sound sound;

    // Constructor
    public Minesweeper(){
        tileSize = 80;
        rows = 8;
        cols = rows;
        boardWidth = cols * tileSize;
        boardHeight = rows * tileSize;
        defaultMines = 10;
        totalMines = defaultMines;
        gameOver = false;
        boardCleared = 0;
        sound = new Sound();

        // Text Panel and textLabel initialization
        textPanel = new JPanel(new BorderLayout());
        textLabel = new JLabel();
        textLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER); // Text at center position
        textLabel.setOpaque(true);
        textPanel.add(textLabel, BorderLayout.CENTER); // Label position middle of the panel
    }
    // Methods

    // Method to initialize the program
    public void init(){
        setTextLabel();
        setButtons();
        setButtonPanel();
        setBoardPanel();

        frameConfig();

    }
    // Set totalMines to a different value
    public void setTotalMines(int mines){
        totalMines = mines;
    }

    // Frame method
    public void frameConfig(){
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(textPanel, BorderLayout.NORTH); //Add panel and place in top position
        frame.add(boardPanel);

        frame.setVisible(true);
        setMines();

    }

    // Method to update textLabel text for totalMines amount
    public void setTextLabel(){
        textLabel.setText("Clear: " + totalMines + " mines");

    }

    // Button method
    public void setButtons(){
        // Re-Start button
        reStart = new JButton("ReStart");
        reStart.setFocusable(false);
        reStart.setFont(buttonFont);

        // Use lambda expression to avoid implement ActionListener class
        reStart.addActionListener(reStartEvent ->{
            frame.dispose(); // Close frame
            Minesweeper playAgain = new Minesweeper(); // new Object
            playAgain.init(); // initialization
        });

        // Level Button
        level = new JButton("Select level");
        level.setFont(buttonFont);
        level.setFocusable(false);

        level.addActionListener( e ->  {
            if (boardCleared > 0){ // Disable difficulty menu after first tile has been checked
                return;
            }

            // Menu configuration, Change game difficulty
            // menu bar
            JPopupMenu popupMenu = new JPopupMenu();

            // menu items: easy, medium, hard
            easy = new JMenuItem("Easy");
            easy.setFont(buttonFont);
            easy.addActionListener(easyEvent -> {
                setTotalMines(5); // Set amount of mine for the easy level
                setTextLabel();   // Update textLabel
                setMines();       // Update mines amount
            });

            medium = new JMenuItem("Medium");
            medium.setFont(buttonFont);
            medium.addActionListener(mediumEvent ->{
                setTotalMines(17);
                setTextLabel();
                setMines();
            });

            hard = new JMenuItem(("Hard"));
            hard.setFont(buttonFont);
            hard.addActionListener(hardEvent -> {
                setTotalMines(25);
                setTextLabel();
                setMines();
            });

            // add menu items to menu bar
            popupMenu.add(easy);
            popupMenu.add(medium);
            popupMenu.add(hard);
            popupMenu.show(level, 0, level.getHeight());

        });
    }

    // Panel method for reStart button
    public void setButtonPanel(){
        panelForReStart = new JPanel();
        // reStart button position on the panel
        panelForReStart.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelForReStart.add(reStart);

        // panelForReStart position on textPanel
        textPanel.add(panelForReStart, BorderLayout.EAST);

        panelForLevel = new JPanel();
        panelForLevel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelForLevel.add(level);

        textPanel.add(panelForLevel, BorderLayout.WEST);
    }
    // Board Panel method
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
        textLabel.setText("💥💥💥 Game Over");
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
            textLabel.setText("Mines Cleared");
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
