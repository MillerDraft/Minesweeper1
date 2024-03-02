import javax.swing.*;
import java.awt.*;

public class Minesweeper {

    // Attributes

    private JFrame frame;
    private JLabel textLabel;
    BackEnd logic;
    private JPanel textPanel, panelForReStart, panelForLevel;
    private JButton reStart, level;
    private JMenuItem easy;
    private JMenuItem medium;
    private JMenuItem hard;

    private int boardWidth, boardHeight;

    Font buttonFont = new Font("Arial", Font.PLAIN, 25);

    // Constructor
    public Minesweeper(){
        // Initialize textLabel
        textLabel = new JLabel();
        textLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER); // Text at center position
        textLabel.setOpaque(true);

        // Initialize BackEnd with textLabel
        logic = new BackEnd(textLabel);

        boardWidth = logic.cols * logic.tileSize;
        boardHeight = logic.rows * logic.tileSize;

        // Text Panel and textLabel initialization
        textPanel = new JPanel(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER); // Label position middle of the panel
    }
    // Methods

    // Method to initialize the program
    public void init(){
        setTextLabel();
        setButtons();
        setButtonPanel();
        logic.setBoardPanel();

        frameConfig();

    }

    // Frame method
    public void frameConfig(){
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(textPanel, BorderLayout.NORTH); //Add panel and place in top position
        frame.add(logic.boardPanel);

        frame.setVisible(true);
        logic.setMines();

    }

    // Method to update textLabel text for totalMines amount
    public void setTextLabel(){
        textLabel.setText("⚠ Clear: ️" + logic.totalMines + " mines" );

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
            if (logic.boardCleared > 0){ // Disable difficulty menu after first tile has been checked
                return;
            }

            // Menu configuration, Change game difficulty
            // menu bar
            JPopupMenu popupMenu = new JPopupMenu();

            // menu items: easy, medium, hard
            easy = new JMenuItem("Easy");
            easy.setFont(buttonFont);
            easy.addActionListener(easyEvent -> {
                logic.setTotalMines(5); // Set amount of mine for the easy level
                setTextLabel();   // Update textLabel
                logic.setMines();       // Update mines amount
            });

            medium = new JMenuItem("Medium");
            medium.setFont(buttonFont);
            medium.addActionListener(mediumEvent ->{
                logic.setTotalMines(17);
                setTextLabel();
                logic.setMines();
            });

            hard = new JMenuItem(("Hard"));
            hard.setFont(buttonFont);
            hard.addActionListener(hardEvent -> {
                logic.setTotalMines(25);
                setTextLabel();
                logic.setMines();
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

}
