import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class DecodeLabs_Java_P1 extends JFrame {

    // Game State 
    private int targetNumber;
    private int attemptsLeft;
    private int totalScore;
    private int roundsPlayed;
    private boolean gameActive;

    private static final int MAX_ATTEMPTS = 7;
    private static final int MIN_NUM      = 1;
    private static final int MAX_NUM      = 100;
    private static final Random random    = new Random();

    //  UI Components 
    private JTextField guessField;
    private JButton    guessBtn;
    private JButton    newGameBtn;

    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JLabel attemptsLabel;
    private JLabel scoreLabel;
    private JLabel roundLabel;
    private JLabel feedbackLabel;
    private JLabel emojiLabel;

    private JProgressBar attemptsBar;
    private JTextArea    historyArea;

    // Color Palette 
    private static final Color BG_DARK      = new Color(18,  18,  28);
    private static final Color BG_CARD      = new Color(30,  30,  46);
    private static final Color ACCENT_GREEN = new Color(100, 220, 140);
    private static final Color ACCENT_PURPLE= new Color(180, 130, 255);
    private static final Color ACCENT_GOLD  = new Color(255, 200,  60);
    private static final Color TEXT_WHITE   = new Color(230, 230, 240);
    private static final Color TEXT_MUTED   = new Color(140, 140, 160);
    private static final Color TOO_HIGH     = new Color(255,  90,  90);
    private static final Color TOO_LOW      = new Color( 90, 180, 255);
    private static final Color SUCCESS      = new Color(100, 220, 140);

    // Constructor
    public DecodeLabs_Java_P1() {
        setTitle("Number Guessing Game - Honey");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        buildUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        startNewRound();
    }

    // Build all UI panels 
    private void buildUI() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // Header 
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new EmptyBorder(20, 30, 16, 30));

        titleLabel = new JLabel("🎯 Number Guessing Game", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_GREEN);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setBackground(BG_CARD);

        roundLabel  = statLabel("Round: 1");
        scoreLabel  = statLabel("Score: 0");
        statsPanel.add(roundLabel);
        statsPanel.add(statSep());
        statsPanel.add(scoreLabel);

        header.add(titleLabel,  BorderLayout.WEST);
        header.add(statsPanel,  BorderLayout.EAST);

        // thin accent line at bottom
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(1, 2));
        line.setBackground(ACCENT_PURPLE);
        header.add(line, BorderLayout.SOUTH);

        return header;
    }

    // Center 
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(16, 0));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(20, 24, 20, 24));

        center.add(buildGameCard(), BorderLayout.CENTER);
        center.add(buildHistoryPanel(), BorderLayout.EAST);
        return center;
    }

    private JPanel buildGameCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 60, 80), 1, true),
            new EmptyBorder(24, 28, 24, 28)
        ));

        // Instruction
        instructionLabel = centeredLabel("Guess a number between 1 and 100", 14, TEXT_MUTED);
        instructionLabel.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Emoji feedback
        emojiLabel = centeredLabel("🤔", 48, Color.WHITE);
        emojiLabel.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Feedback text
        feedbackLabel = centeredLabel("Enter your first guess!", 16, ACCENT_PURPLE);
        feedbackLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Attempts progress
        attemptsLabel = centeredLabel("Attempts Left: " + MAX_ATTEMPTS, 13, TEXT_MUTED);

        attemptsBar = new JProgressBar(0, MAX_ATTEMPTS);
        attemptsBar.setValue(MAX_ATTEMPTS);
        attemptsBar.setStringPainted(false);
        attemptsBar.setPreferredSize(new Dimension(320, 8));
        attemptsBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        attemptsBar.setBackground(new Color(50, 50, 65));
        attemptsBar.setForeground(ACCENT_GREEN);
        attemptsBar.setBorderPainted(false);
        attemptsBar.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Input row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputRow.setBackground(BG_CARD);

        guessField = new JTextField(8);
        guessField.setFont(new Font("Segoe UI", Font.BOLD, 20));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setBackground(new Color(40, 40, 58));
        guessField.setForeground(TEXT_WHITE);
        guessField.setCaretColor(ACCENT_GREEN);
        guessField.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_PURPLE, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        // Allow Enter key to submit
        guessField.addActionListener(e -> handleGuess());

        guessBtn = styledButton("Guess →", ACCENT_GREEN, BG_DARK);
        guessBtn.addActionListener(e -> handleGuess());

        inputRow.add(guessField);
        inputRow.add(guessBtn);

        // New Game button
        newGameBtn = styledButton("New Round ↺", BG_CARD, ACCENT_GOLD);
        newGameBtn.setForeground(ACCENT_GOLD);
        newGameBtn.addActionListener(e -> startNewRound());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(BG_CARD);
        btnPanel.setBorder(new EmptyBorder(14, 0, 0, 0));
        btnPanel.add(newGameBtn);

        card.add(instructionLabel);
        card.add(emojiLabel);
        card.add(feedbackLabel);
        card.add(attemptsLabel);
        card.add(attemptsBar);
        card.add(inputRow);
        card.add(btnPanel);

        return card;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(180, 0));
        panel.setBackground(BG_CARD);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 60, 80), 1, true),
            new EmptyBorder(14, 12, 14, 12)
        ));

        JLabel histTitle = new JLabel("Guess History");
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        histTitle.setForeground(ACCENT_PURPLE);
        histTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setBackground(new Color(24, 24, 38));
        historyArea.setForeground(TEXT_MUTED);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historyArea.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(null);
        scroll.setBackground(new Color(24, 24, 38));

        panel.add(histTitle, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        return panel;
    }

    // Footer 
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_CARD);
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel credit = new JLabel("Developed by Honey | First Internship Project at DecodeLabs");
        credit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        credit.setForeground(TEXT_MUTED);
        footer.add(credit);
        return footer;
    }

    //  Game Logic 
    private void startNewRound() {
        targetNumber  = random.nextInt(MAX_NUM) + 1;   // 1 – 100
        attemptsLeft  = MAX_ATTEMPTS;
        gameActive    = true;
        roundsPlayed++;

        guessField.setText("");
        guessField.setEnabled(true);
        guessBtn.setEnabled(true);
        guessField.requestFocus();

        historyArea.setText("");
        feedbackLabel.setText("Enter your first guess!");
        feedbackLabel.setForeground(ACCENT_PURPLE);
        emojiLabel.setText("🤔");
        instructionLabel.setText("Guess a number between 1 and 100");
        attemptsBar.setValue(MAX_ATTEMPTS);
        attemptsBar.setForeground(ACCENT_GREEN);
        attemptsLabel.setText("Attempts Left: " + MAX_ATTEMPTS);
        roundLabel.setText("Round: " + roundsPlayed);
        scoreLabel.setText("Score: " + totalScore);
    }

    private void handleGuess() {
        if (!gameActive) return;

        String input = guessField.getText().trim();

        //  Exception Handling 
        if (input.isEmpty()) {
            showInputError("Please enter a number!");
            return;
        }

        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            showInputError("Invalid input! Enter digits only (e.g. 42)");
            guessField.selectAll();
            return;
        }

        if (guess < MIN_NUM || guess > MAX_NUM) {
            showInputError("Out of range! Enter a number between 1 and 100.");
            guessField.selectAll();
            return;
        }

        // Valid guess — process it
        attemptsLeft--;
        String arrow;

        if (guess == targetNumber) {
            // Win 
            int points = attemptsLeft * 10 + 10;
            totalScore += points;
            gameActive = false;

            feedbackLabel.setText("🎉 Correct! It was " + targetNumber + "! +" + points + " pts");
            feedbackLabel.setForeground(SUCCESS);
            emojiLabel.setText("🏆");
            attemptsBar.setValue(attemptsLeft + 1);

            logHistory(guess, "✅ WIN");
            guessField.setEnabled(false);
            guessBtn.setEnabled(false);
            scoreLabel.setText("Score: " + totalScore);

            SwingUtilities.invokeLater(() ->
                showPlayAgainDialog("🎉 You got it in " + (MAX_ATTEMPTS - attemptsLeft) +
                    " guess" + ((MAX_ATTEMPTS - attemptsLeft) == 1 ? "" : "es") +
                    "!\nPoints earned: +" + points +
                    "\nTotal Score: " + totalScore)
            );
            return;
        }

        // Wrong guess
        if (guess > targetNumber) {
            feedbackLabel.setText("📉 Too High! Try lower.");
            feedbackLabel.setForeground(TOO_HIGH);
            emojiLabel.setText("🔻");
            arrow = "▼ Too High";
        } else {
            feedbackLabel.setText("📈 Too Low! Try higher.");
            feedbackLabel.setForeground(TOO_LOW);
            emojiLabel.setText("🔺");
            arrow = "▲ Too Low";
        }

        logHistory(guess, arrow);
        updateAttemptsUI();
        guessField.selectAll();
        guessField.requestFocus();

        //  Out of Attempts 
        if (attemptsLeft == 0) {
            gameActive = false;
            feedbackLabel.setText("💀 Game Over! The number was " + targetNumber);
            feedbackLabel.setForeground(TOO_HIGH);
            emojiLabel.setText("😵");
            guessField.setEnabled(false);
            guessBtn.setEnabled(false);

            SwingUtilities.invokeLater(() ->
                showPlayAgainDialog("😔 Out of attempts!\nThe number was: " + targetNumber +
                    "\nTotal Score: " + totalScore)
            );
        }
    }

    private void updateAttemptsUI() {
        attemptsLabel.setText("Attempts Left: " + attemptsLeft);
        attemptsBar.setValue(attemptsLeft);

        // Bar colour shifts red as attempts dwindle
        if      (attemptsLeft > 4) attemptsBar.setForeground(ACCENT_GREEN);
        else if (attemptsLeft > 2) attemptsBar.setForeground(ACCENT_GOLD);
        else                        attemptsBar.setForeground(TOO_HIGH);
    }

    private void logHistory(int guess, String tag) {
        historyArea.append(String.format("%-4d  %s%n", guess, tag));
    }

    //  Dialogs 
    private void showInputError(String msg) {
        feedbackLabel.setText("⚠ " + msg);
        feedbackLabel.setForeground(ACCENT_GOLD);
        emojiLabel.setText("⚠️");
        guessField.requestFocus();
    }

    private void showPlayAgainDialog(String message) {
        String[] options = {"Play Again", "Quit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "Round Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (choice == 0) {
            startNewRound();
        } else {
            showFinalScore();
        }
    }

    private void showFinalScore() {
        JOptionPane.showMessageDialog(
            this,
            "Thanks for playing!\n\nRounds Played: " + roundsPlayed +
            "\nFinal Score: " + totalScore +
            "\n\n— DecodeLabs Batch 2026 —",
            "Final Score",
            JOptionPane.INFORMATION_MESSAGE
        );
        System.exit(0);
    }

    //  UI Helpers 
    private JLabel centeredLabel(String text, int size, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ACCENT_GOLD);
        return lbl;
    }

    private JLabel statSep() {
        JLabel sep = new JLabel("|");
        sep.setForeground(TEXT_MUTED);
        return sep;
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setText(text);
        btn.setBackground(bg);
        btn.setForeground(fg.equals(BG_DARK) ? Color.WHITE : fg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        // For Guess button, make bg visible
        if (bg.equals(ACCENT_GREEN)) {
            btn.setForeground(BG_DARK);
            btn.setOpaque(true);
            btn.setBackground(ACCENT_GREEN);
        }
        return btn;
    }

    // Entry Point
    public static void main(String[] args) {
        // Set system look for native decorations, then override colours
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Style JOptionPane dialogs
            UIManager.put("OptionPane.background",       new Color(30, 30, 46));
            UIManager.put("Panel.background",            new Color(30, 30, 46));
            UIManager.put("OptionPane.messageForeground",new Color(230, 230, 240));
            UIManager.put("Button.background",           new Color(100, 220, 140));
            UIManager.put("Button.foreground",           Color.BLACK);
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(DecodeLabs_Java_P1::new);
    }
}