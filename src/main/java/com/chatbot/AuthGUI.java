package com.chatbot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AuthGUI extends JFrame {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BG =
            new Color(15, 15, 18);

    private static final Color LEFT_BG =
            new Color(20, 20, 24);

    private static final Color CARD =
            new Color(28, 28, 33);

    private static final Color INPUT_BG =
            new Color(39, 39, 45);

    private static final Color BORDER =
            new Color(62, 62, 70);

    private static final Color TEXT =
            new Color(240, 240, 245);

    private static final Color MUTED =
            new Color(155, 155, 165);

    private static final Color ACCENT =
            new Color(145, 105, 220);

    private static final Color SUCCESS =
            new Color(100, 200, 130);

    private static final Color ERROR =
            new Color(230, 100, 100);

    // =========================================================
    // FIREBASE
    // =========================================================

    private final FirebaseService firebaseService;

    // =========================================================
    // UI
    // =========================================================

    private JTextField emailField;
    private JPasswordField passwordField;

    private JButton actionButton;
    private JButton switchButton;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel messageLabel;
    private JLabel emailStatusLabel;

    private JLabel forgotPasswordLabel;

    private boolean loginMode = true;
    private boolean checkingEmail = false;

    private Timer emailCheckTimer;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AuthGUI() {

        firebaseService =
                new FirebaseService();

        setupLookAndFeel();

        setupWindow();

        buildUI();
    }

    // =========================================================
    // LOOK AND FEEL
    // =========================================================

    private void setupLookAndFeel() {

        try {

            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName()
            );

        } catch (Exception ignored) {
        }
    }

    // =========================================================
    // WINDOW
    // =========================================================

    private void setupWindow() {

        setTitle(
                "Java AI Chatbot"
        );

        setSize(
                900,
                650
        );

        setMinimumSize(
                new Dimension(
                        800,
                        600
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);
    }

    // =========================================================
    // MAIN UI
    // =========================================================

    private void buildUI() {

        JPanel root =
                new JPanel(
                        new GridLayout(
                                1,
                                2
                        )
                );

        root.setBackground(BG);

        // =====================================================
        // LEFT BRAND PANEL
        // =====================================================

        JPanel leftPanel =
                new JPanel();

        leftPanel.setLayout(
                new BoxLayout(
                        leftPanel,
                        BoxLayout.Y_AXIS
                )
        );

        leftPanel.setBackground(
                LEFT_BG
        );

        leftPanel.setBorder(
                new EmptyBorder(
                        80,
                        55,
                        60,
                        55
                )
        );

        JLabel logo =
                new JLabel(
                        "JAVA AI"
                );

        logo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        34
                )
        );

        logo.setForeground(TEXT);

        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        leftPanel.add(logo);

        JLabel line =
                new JLabel(
                        "Intelligent desktop assistant"
                );

        line.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        15
                )
        );

        line.setForeground(MUTED);

        line.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        leftPanel.add(
                Box.createVerticalStrut(8)
        );

        leftPanel.add(line);

        leftPanel.add(
                Box.createVerticalStrut(55)
        );

        JLabel description =
                new JLabel(
                        "<html>"
                        + "Ask questions.<br>"
                        + "Get intelligent answers.<br>"
                        + "Keep your conversations synced."
                        + "</html>"
                );

        description.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        17
                )
        );

        description.setForeground(TEXT);

        description.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        leftPanel.add(description);

        leftPanel.add(
                Box.createVerticalGlue()
        );

        JLabel powered =
                new JLabel(
                        "Powered by Gemini AI"
                );

        powered.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        powered.setForeground(MUTED);

        powered.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        leftPanel.add(powered);

        // =====================================================
        // RIGHT PANEL
        // =====================================================

        JPanel rightPanel =
                new JPanel(
                        new GridBagLayout()
                );

        rightPanel.setBackground(BG);

        JPanel card =
                new JPanel();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        card.setBackground(CARD);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                35,
                                40,
                                35,
                                40
                        )
                )
        );

        card.setPreferredSize(
                new Dimension(
                        390,
                        510
                )
        );

        // =====================================================
        // TITLE
        // =====================================================

        titleLabel =
                new JLabel(
                        "Welcome back"
                );

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        27
                )
        );

        titleLabel.setForeground(TEXT);

        titleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        card.add(titleLabel);

        subtitleLabel =
                new JLabel(
                        "Sign in to continue"
                );

        subtitleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        subtitleLabel.setForeground(MUTED);

        subtitleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        card.add(
                Box.createVerticalStrut(6)
        );

        card.add(subtitleLabel);

        card.add(
                Box.createVerticalStrut(28)
        );

        // =====================================================
        // EMAIL
        // =====================================================

        JLabel emailLabel =
                createLabel(
                        "Email"
                );

        card.add(emailLabel);

        card.add(
                Box.createVerticalStrut(7)
        );

        emailField =
                new JTextField();

        styleTextField(
                emailField
        );

        card.add(emailField);

        emailStatusLabel =
                new JLabel(
                        " "
                );

        emailStatusLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        emailStatusLabel.setForeground(
                MUTED
        );

        emailStatusLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        card.add(
                Box.createVerticalStrut(4)
        );

        card.add(emailStatusLabel);

        // =====================================================
        // EMAIL CHECK
        // =====================================================

        emailField.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(
                            KeyEvent e
                    ) {

                        scheduleEmailCheck();
                    }
                }
        );

        emailField.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusLost(
                            FocusEvent e
                    ) {

                        checkEmailNow();
                    }
                }
        );

        // =====================================================
        // PASSWORD
        // =====================================================

        card.add(
                Box.createVerticalStrut(15)
        );

        JLabel passwordLabel =
                createLabel(
                        "Password"
                );

        card.add(passwordLabel);

        card.add(
                Box.createVerticalStrut(7)
        );

        passwordField =
                new JPasswordField();

        stylePasswordField(
                passwordField
        );

        card.add(passwordField);

        // =====================================================
        // FORGOT PASSWORD
        // =====================================================

        forgotPasswordLabel =
                new JLabel(
                        "Forgot password?"
                );

        forgotPasswordLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        forgotPasswordLabel.setForeground(
                ACCENT
        );

        forgotPasswordLabel.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        forgotPasswordLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        card.add(
                Box.createVerticalStrut(9)
        );

        card.add(
                forgotPasswordLabel
        );

        forgotPasswordLabel.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        resetPassword();
                    }
                }
        );

        // =====================================================
        // MESSAGE
        // =====================================================

        messageLabel =
                new JLabel(
                        " "
                );

        messageLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        messageLabel.setForeground(
                ERROR
        );

        messageLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        card.add(
                Box.createVerticalStrut(12)
        );

        card.add(messageLabel);

        // =====================================================
        // ACTION BUTTON
        // =====================================================

        actionButton =
                new JButton(
                        "Sign In"
                );

        styleActionButton(
                actionButton
        );

        actionButton.addActionListener(
                e -> performAuthentication()
        );

        card.add(
                Box.createVerticalStrut(10)
        );

        card.add(actionButton);

        // =====================================================
        // SWITCH
        // =====================================================

        switchButton =
                new JButton(
                        "Create an account"
                );

        switchButton.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        switchButton.setForeground(
                ACCENT
        );

        switchButton.setBackground(
                CARD
        );

        switchButton.setBorder(null);

        switchButton.setFocusPainted(false);

        switchButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        switchButton.addActionListener(
                e -> toggleMode()
        );

        switchButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        card.add(
                Box.createVerticalStrut(17)
        );

        card.add(switchButton);

        rightPanel.add(card);

        root.add(leftPanel);
        root.add(rightPanel);

        setContentPane(root);
    }

    // =========================================================
    // LABEL
    // =========================================================

    private JLabel createLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        label.setForeground(TEXT);

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private void styleTextField(
            JTextField field
    ) {

        field.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        field.setForeground(TEXT);

        field.setBackground(INPUT_BG);

        field.setCaretColor(TEXT);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                11,
                                12,
                                11,
                                12
                        )
                )
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );
    }

    // =========================================================
    // PASSWORD FIELD
    // =========================================================

    private void stylePasswordField(
            JPasswordField field
    ) {

        field.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        field.setForeground(TEXT);

        field.setBackground(INPUT_BG);

        field.setCaretColor(TEXT);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                11,
                                12,
                                11,
                                12
                        )
                )
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private void styleActionButton(
            JButton button
    ) {

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        button.setForeground(Color.WHITE);

        button.setBackground(ACCENT);

        button.setFocusPainted(false);

        button.setBorder(
                new EmptyBorder(
                        12,
                        20,
                        12,
                        20
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );
    }

    // =========================================================
    // TOGGLE LOGIN / SIGN UP
    // =========================================================

    private void toggleMode() {

        loginMode =
                !loginMode;

        messageLabel.setText(" ");

        emailStatusLabel.setText(" ");

        passwordField.setText("");

        if (loginMode) {

            titleLabel.setText(
                    "Welcome back"
            );

            subtitleLabel.setText(
                    "Sign in to continue"
            );

            actionButton.setText(
                    "Sign In"
            );

            switchButton.setText(
                    "Create an account"
            );

            forgotPasswordLabel.setVisible(
                    true
            );

        } else {

            titleLabel.setText(
                    "Create account"
            );

            subtitleLabel.setText(
                    "Create your Java AI account"
            );

            actionButton.setText(
                    "Sign Up"
            );

            switchButton.setText(
                    "Already have an account?"
            );

            forgotPasswordLabel.setVisible(
                    false
            );
        }

        revalidate();

        repaint();
    }

    // =========================================================
    // EMAIL CHECK TIMER
    // =========================================================

    private void scheduleEmailCheck() {

        if (emailCheckTimer != null) {

            emailCheckTimer.stop();
        }

        String email =
                emailField
                        .getText()
                        .trim();

        if (
                email.isEmpty() ||
                !email.contains("@") ||
                !email.contains(".")
        ) {

            emailStatusLabel.setText(" ");

            return;
        }

        emailStatusLabel.setForeground(
                MUTED
        );

        emailStatusLabel.setText(
                "Checking email..."
        );

        emailCheckTimer =
                new Timer(
                        500,
                        e -> checkEmailNow()
                );

        emailCheckTimer.setRepeats(
                false
        );

        emailCheckTimer.start();
    }

    // =========================================================
    // CHECK EMAIL
    // =========================================================

    private void checkEmailNow() {

        String email =
                emailField
                        .getText()
                        .trim();

        if (
                email.isEmpty() ||
                !email.contains("@") ||
                !email.contains(".")
        ) {

            emailStatusLabel.setText(" ");

            return;
        }

        if (checkingEmail) {
            return;
        }

        checkingEmail = true;

        emailStatusLabel.setForeground(
                MUTED
        );

        emailStatusLabel.setText(
                "Checking email..."
        );

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        try {

                            /*
                             * Firebase Authentication does not expose
                             * a public "does this email exist?" endpoint
                             * through the normal client API.
                             *
                             * We therefore do not attempt a fake lookup.
                             * The actual sign-in/sign-up request below
                             * determines the account state securely.
                             */

                            Thread.sleep(120);

                            return "ready";

                        } catch (Exception e) {

                            return null;
                        }
                    }

                    @Override
                    protected void done() {

                        checkingEmail = false;

                        if (
                                email.equals(
                                        emailField
                                                .getText()
                                                .trim()
                                )
                        ) {

                            emailStatusLabel.setForeground(
                                    MUTED
                            );

                            emailStatusLabel.setText(
                                    loginMode
                                            ? "Enter your password to sign in."
                                            : "Use this email to create your account."
                            );
                        }
                    }
                };

        worker.execute();
    }

    // =========================================================
    // AUTHENTICATION
    // =========================================================

    private void performAuthentication() {

        String email =
                emailField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (email.isEmpty()) {

            showError(
                    "Please enter your email."
            );

            emailField.requestFocus();

            return;
        }

        if (!email.contains("@")) {

            showError(
                    "Please enter a valid email."
            );

            emailField.requestFocus();

            return;
        }

        if (password.isEmpty()) {

            showError(
                    "Please enter your password."
            );

            passwordField.requestFocus();

            return;
        }

        if (password.length() < 6) {

            showError(
                    "Password must be at least 6 characters."
            );

            passwordField.requestFocus();

            return;
        }

        setLoading(true);

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        try {

                            if (loginMode) {

    return firebaseService.signIn(email, password);

} else {

    return firebaseService.signUp(email, password);

}

                        } catch (Exception e) {

                            return "ERROR:" +
                                    e.getMessage();
                        }
                    }

                    @Override
                    protected void done() {

                        setLoading(false);

                        String result;

                        try {

                            result = get();

                        } catch (Exception e) {

                            showError(
                                    "Authentication failed."
                            );

                            return;
                        }

                        if (
                                result != null &&
                                result.startsWith("ERROR:")
                        ) {

                            showError(
                                    result.substring(6)
                            );

                            return;
                        }

                        if (
                                result == null ||
                                result.isBlank()
                        ) {

                            showError(
                                    "Authentication failed. Please try again."
                            );

                            return;
                        }

                        openChatbot();
                    }
                };

        worker.execute();
    }

    // =========================================================
    // PASSWORD RESET
    // =========================================================

    private void resetPassword() {

        String email =
                emailField
                        .getText()
                        .trim();

        if (email.isEmpty()) {

            showError(
                    "Enter your email first."
            );

            emailField.requestFocus();

            return;
        }

        setLoading(true);

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        try {

                            return firebaseService
                                    .sendPasswordReset(
                                            email
                                    );

                        } catch (Exception e) {

                            return "ERROR:" +
                                    e.getMessage();
                        }
                    }

                    @Override
                    protected void done() {

                        setLoading(false);

                        String result;

                        try {

                            result = get();

                        } catch (Exception e) {

                            showError(
                                    "Could not send reset email."
                            );

                            return;
                        }

                        if (
                                result != null &&
                                result.startsWith("ERROR:")
                        ) {

                            showError(
                                    result.substring(6)
                            );

                        } else {

                            showSuccess(
                                    "Password reset email sent."
                            );
                        }
                    }
                };

        worker.execute();
    }

    // =========================================================
    // OPEN CHATBOT
    // =========================================================

    private void openChatbot() {

        ChatbotGUI chatbot =
                new ChatbotGUI(
                        firebaseService
                );

        chatbot.setVisible(true);

        dispose();
    }

    // =========================================================
    // LOADING
    // =========================================================

    private void setLoading(
            boolean loading
    ) {

        actionButton.setEnabled(
                !loading
        );

        switchButton.setEnabled(
                !loading
        );

        emailField.setEnabled(
                !loading
        );

        passwordField.setEnabled(
                !loading
        );

        forgotPasswordLabel.setEnabled(
                !loading
        );

        if (loading) {

            actionButton.setText(
                    loginMode
                            ? "Signing in..."
                            : "Creating account..."
            );

        } else {

            actionButton.setText(
                    loginMode
                            ? "Sign In"
                            : "Sign Up"
            );
        }
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        messageLabel.setForeground(
                ERROR
        );

        messageLabel.setText(
                "<html>"
                + message
                + "</html>"
        );
    }

    // =========================================================
    // SUCCESS
    // =========================================================

    private void showSuccess(
            String message
    ) {

        messageLabel.setForeground(
                SUCCESS
        );

        messageLabel.setText(
                "<html>"
                + message
                + "</html>"
        );
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    AuthGUI gui =
                            new AuthGUI();

                    gui.setVisible(true);
                }
        );
    }
}