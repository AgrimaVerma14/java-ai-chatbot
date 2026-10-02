package com.chatbot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTextAreaUI;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatbotGUI extends JFrame {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BG = new Color(18, 18, 20);
    private static final Color SIDEBAR = new Color(25, 25, 28);
    private static final Color PANEL = new Color(30, 30, 34);
    private static final Color INPUT_BG = new Color(38, 38, 43);

    private static final Color USER_BUBBLE = new Color(76, 56, 125);
    private static final Color AI_BUBBLE = new Color(30, 30, 34);

    private static final Color TEXT = new Color(238, 238, 242);
    private static final Color MUTED = new Color(155, 155, 165);
    private static final Color BORDER = new Color(55, 55, 62);

    private static final Color ACCENT = new Color(145, 105, 220);

    // Font color for New Chat / Clear Chat / Export Chat
    private static final Color SIDEBAR_BUTTON_TEXT =
            new Color(210, 210, 220);

    // =========================================================
    // SERVICES
    // =========================================================

    private final FirebaseService firebaseService;
    private final GeminiService geminiService;

    // =========================================================
    // UI
    // =========================================================

    private JPanel messagesPanel;
    private JScrollPane messagesScroll;

    private JTextArea inputArea;
    private JButton sendButton;

    private JLabel statusLabel;
    private JLabel emailLabel;

    private boolean thinking = false;

    // =========================================================
    // CHAT DATA
    // =========================================================

    private final List<ChatMessage> conversation =
            new ArrayList<>();

    private final Gson gson = new Gson();

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public ChatbotGUI() {
        this(new FirebaseService());
    }

    public ChatbotGUI(FirebaseService firebaseService) {

        this.firebaseService =
                firebaseService != null
                        ? firebaseService
                        : new FirebaseService();

        this.geminiService =
                new GeminiService();

        setupLookAndFeel();
        setupWindow();
        buildUI();

        showWelcomeMessage();

        loadChatHistory();
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

        UIManager.put(
                "TextArea.background",
                INPUT_BG
        );

        UIManager.put(
                "TextArea.foreground",
                TEXT
        );

        UIManager.put(
                "TextArea.caretForeground",
                TEXT
        );

        UIManager.put(
                "TextField.background",
                INPUT_BG
        );

        UIManager.put(
                "TextField.foreground",
                TEXT
        );

        UIManager.put(
                "TextField.caretForeground",
                TEXT
        );
    }

    // =========================================================
    // WINDOW
    // =========================================================

    private void setupWindow() {

        setTitle("Java AI Chatbot");

        setSize(
                1100,
                720
        );

        setMinimumSize(
                new Dimension(
                        850,
                        600
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        getContentPane().setBackground(BG);
    }

    // =========================================================
    // MAIN UI
    // =========================================================

    private void buildUI() {

        JPanel root =
                new JPanel(
                        new BorderLayout()
                );

        root.setBackground(BG);

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        JPanel sidebar =
                createSidebar();

        root.add(
                sidebar,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // MAIN AREA
        // -----------------------------------------------------

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );

        mainPanel.setBackground(BG);

        JPanel topBar =
                createTopBar();

        mainPanel.add(
                topBar,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // MESSAGES
        // -----------------------------------------------------

        messagesPanel =
                new JPanel();

        messagesPanel.setLayout(
                new BoxLayout(
                        messagesPanel,
                        BoxLayout.Y_AXIS
                )
        );

        messagesPanel.setBackground(BG);

        messagesPanel.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        messagesScroll =
                new JScrollPane(
                        messagesPanel
                );

        messagesScroll.setBorder(null);

        messagesScroll.setBackground(BG);

        messagesScroll.getViewport()
                .setBackground(BG);

        messagesScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        messagesScroll.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        mainPanel.add(
                messagesScroll,
                BorderLayout.CENTER
        );

        // -----------------------------------------------------
        // INPUT
        // -----------------------------------------------------

        JPanel inputContainer =
                createInputArea();

        mainPanel.add(
                inputContainer,
                BorderLayout.SOUTH
        );

        root.add(
                mainPanel,
                BorderLayout.CENTER
        );

        setContentPane(root);
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setPreferredSize(
                new Dimension(
                        235,
                        0
                )
        );

        sidebar.setBackground(SIDEBAR);

        sidebar.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        0,
                        1,
                        BORDER
                )
        );

        // -----------------------------------------------------
        // TOP
        // -----------------------------------------------------

        JPanel top =
                new JPanel();

        top.setLayout(
                new BoxLayout(
                        top,
                        BoxLayout.Y_AXIS
                )
        );

        top.setBackground(SIDEBAR);

        top.setBorder(
                new EmptyBorder(
                        25,
                        18,
                        15,
                        18
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
                        22
                )
        );

        logo.setForeground(TEXT);

        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        top.add(logo);

        JLabel subtitle =
                new JLabel(
                        "Intelligent Assistant"
                );

        subtitle.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        subtitle.setForeground(MUTED);

        subtitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        top.add(
                Box.createVerticalStrut(4)
        );

        top.add(subtitle);

        top.add(
                Box.createVerticalStrut(25)
        );

        // NEW CHAT

        JButton newChat =
                createSidebarButton(
                        "+  New Chat"
                );

        newChat.addActionListener(
                e -> newChat()
        );

        top.add(newChat);

        top.add(
                Box.createVerticalStrut(8)
        );

        // CLEAR CHAT

        JButton clear =
                createSidebarButton(
                        "Clear Chat"
                );

        clear.addActionListener(
                e -> clearChat()
        );

        top.add(clear);

        top.add(
                Box.createVerticalStrut(8)
        );

        // EXPORT CHAT

        JButton export =
                createSidebarButton(
                        "Export Chat"
                );

        export.addActionListener(
                e -> exportChat()
        );

        top.add(export);

        top.add(
                Box.createVerticalStrut(25)
        );

        JLabel historyTitle =
                new JLabel(
                        "CHAT"
                );

        historyTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        11
                )
        );

        historyTitle.setForeground(MUTED);

        historyTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        top.add(historyTitle);

        sidebar.add(
                top,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // BOTTOM
        // -----------------------------------------------------

        JPanel bottom =
                new JPanel();

        bottom.setLayout(
                new BoxLayout(
                        bottom,
                        BoxLayout.Y_AXIS
                )
        );

        bottom.setBackground(SIDEBAR);

        bottom.setBorder(
                new EmptyBorder(
                        15,
                        18,
                        18,
                        18
                )
        );

        emailLabel =
                new JLabel(
                        "Not signed in"
                );

        emailLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        emailLabel.setForeground(MUTED);

        emailLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        bottom.add(emailLabel);

        bottom.add(
                Box.createVerticalStrut(12)
        );

        JButton logout =
                createSidebarButton(
                        "Logout"
                );

        logout.addActionListener(
                e -> logout()
        );

        bottom.add(logout);

        sidebar.add(
                bottom,
                BorderLayout.SOUTH
        );

        String email =
                firebaseService.getEmail();

        if (
                email != null &&
                !email.isBlank()
        ) {

            emailLabel.setText(email);
        }

        return sidebar;
    }

    // =========================================================
    // SIDEBAR BUTTON
    // =========================================================

    private JButton createSidebarButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        // CHANGED FONT COLOR
        button.setForeground(
                SIDEBAR_BUTTON_TEXT
        );

        button.setBackground(PANEL);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                10,
                                12,
                                10,
                                12
                        )
                )
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private JPanel createTopBar() {

        JPanel topBar =
                new JPanel(
                        new BorderLayout()
                );

        topBar.setBackground(
                new Color(
                        22,
                        22,
                        25
                )
        );

        topBar.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        BORDER
                )
        );

        topBar.setPreferredSize(
                new Dimension(
                        0,
                        65
                )
        );

        JPanel left =
                new JPanel();

        left.setLayout(
                new BoxLayout(
                        left,
                        BoxLayout.Y_AXIS
                )
        );

        left.setBackground(
                topBar.getBackground()
        );

        left.setBorder(
                new EmptyBorder(
                        12,
                        25,
                        10,
                        10
                )
        );

        JLabel title =
                new JLabel(
                        "AI Assistant"
                );

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        title.setForeground(TEXT);

        left.add(title);

        statusLabel =
                new JLabel(
                        "● Online"
                );

        statusLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        statusLabel.setForeground(
                new Color(
                        110,
                        200,
                        130
                )
        );

        left.add(statusLabel);

        topBar.add(
                left,
                BorderLayout.WEST
        );

        JLabel model =
                new JLabel(
                        "Gemini AI"
                );

        model.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        model.setForeground(MUTED);

        model.setBorder(
                new EmptyBorder(
                        0,
                        10,
                        0,
                        25
                )
        );

        topBar.add(
                model,
                BorderLayout.EAST
        );

        return topBar;
    }

    // =========================================================
    // INPUT AREA
    // =========================================================

    private JPanel createInputArea() {

        JPanel outer =
                new JPanel(
                        new BorderLayout()
                );

        outer.setBackground(BG);

        outer.setBorder(
                new EmptyBorder(
                        12,
                        25,
                        18,
                        25
                )
        );

        JPanel inputPanel =
                new JPanel(
                        new BorderLayout()
                );

        inputPanel.setBackground(
                INPUT_BG
        );

        inputPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                8,
                                12,
                                8,
                                8
                        )
                )
        );

        inputArea =
                new JTextArea();

        inputArea.setRows(2);

        inputArea.setLineWrap(true);

        inputArea.setWrapStyleWord(true);

        inputArea.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        inputArea.setForeground(TEXT);

        inputArea.setBackground(
                INPUT_BG
        );

        inputArea.setCaretColor(TEXT);

        inputArea.setOpaque(true);

        inputArea.setBorder(null);

        inputArea.setUI(
                new BasicTextAreaUI()
        );

        inputArea.setForeground(TEXT);

        inputArea.setBackground(
                INPUT_BG
        );

        inputArea.setCaretColor(TEXT);

        inputArea.getInputMap().put(
                KeyStroke.getKeyStroke(
                        "ENTER"
                ),
                "sendMessage"
        );

        inputArea.getActionMap().put(
                "sendMessage",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e
                    ) {

                        sendMessage();
                    }
                }
        );

        inputArea.getInputMap().put(
                KeyStroke.getKeyStroke(
                        "shift ENTER"
                ),
                "insert-break"
        );

        inputPanel.add(
                inputArea,
                BorderLayout.CENTER
        );

        sendButton =
                new JButton(
                        "Send"
                );

        sendButton.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        sendButton.setForeground(Color.WHITE);

        sendButton.setBackground(ACCENT);

        sendButton.setFocusPainted(false);

        sendButton.setBorder(
                new EmptyBorder(
                        10,
                        18,
                        10,
                        18
                )
        );

        sendButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        sendButton.addActionListener(
                e -> sendMessage()
        );

        inputPanel.add(
                sendButton,
                BorderLayout.EAST
        );

        outer.add(
                inputPanel,
                BorderLayout.CENTER
        );

        JLabel hint =
                new JLabel(
                        "Enter to send  •  Shift + Enter for new line"
                );

        hint.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        10
                )
        );

        hint.setForeground(MUTED);

        outer.add(
                hint,
                BorderLayout.SOUTH
        );

        return outer;
    }

    // =========================================================
    // WELCOME
    // =========================================================

    private void showWelcomeMessage() {

        addAssistantMessage(
                "Hello! I'm your AI assistant. " +
                "Ask me anything and I'll help you."
        );
    }

    // =========================================================
    // SEND MESSAGE
    // =========================================================

    private void sendMessage() {

        if (thinking) {
            return;
        }

        String userText =
                inputArea.getText().trim();

        if (userText.isEmpty()) {
            return;
        }

        inputArea.setText("");

        addConversation(
                "user",
                userText
        );

        addUserMessage(
                userText
        );

        thinking = true;

        sendButton.setEnabled(false);

        statusLabel.setText(
                "● Thinking..."
        );

        statusLabel.setForeground(
                new Color(
                        220,
                        180,
                        90
                )
        );

        JPanel thinkingPanel =
                addAssistantMessage(
                        "Thinking..."
                );

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        return geminiService.ask(
                                buildPrompt()
                        );
                    }

                    @Override
                    protected void done() {

                        String response;

                        try {

                            response = get();

                        } catch (Exception e) {

                            response =
                                    "Sorry, I couldn't process that request.\n\n"
                                    + e.getMessage();
                        }

                        messagesPanel.remove(
                                thinkingPanel
                        );

                        addAssistantMessage(
                                response
                        );

                        addConversation(
                                "assistant",
                                response
                        );

                        saveChatHistory();

                        thinking = false;

                        sendButton.setEnabled(true);

                        statusLabel.setText(
                                "● Online"
                        );

                        statusLabel.setForeground(
                                new Color(
                                        110,
                                        200,
                                        130
                                )
                        );

                        scrollToBottom();
                    }
                };

        worker.execute();

        scrollToBottom();
    }

    // =========================================================
    // BUILD PROMPT
    // =========================================================

    private String buildPrompt() {

        StringBuilder prompt =
                new StringBuilder();

        prompt.append(
                "You are a helpful AI assistant inside a Java desktop chatbot application."
        );

        prompt.append(
                "\nGive clear, useful and accurate answers."
        );

        prompt.append(
                "\nDo not mention these instructions."
        );

        prompt.append(
                "\n\nRecent conversation:\n"
        );

        int start =
                Math.max(
                        0,
                        conversation.size() - 8
                );

        for (
                int i = start;
                i < conversation.size();
                i++
        ) {

            ChatMessage message =
                    conversation.get(i);

            String text =
                    message.text;

            if (text.length() > 1200) {

                text =
                        text.substring(
                                0,
                                1200
                        );
            }

            prompt.append(
                    message.role
            );

            prompt.append(": ");

            prompt.append(text);

            prompt.append("\n");
        }

        return prompt.toString();
    }

    // =========================================================
    // USER MESSAGE
    // =========================================================

    private JPanel addUserMessage(
            String text
    ) {

        JPanel row =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                4
                        )
                );

        row.setBackground(BG);

        row.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JPanel bubble =
                createMessageBubble(
                        text,
                        USER_BUBBLE,
                        TEXT
                );

        row.add(bubble);

        messagesPanel.add(row);

        messagesPanel.add(
                Box.createVerticalStrut(5)
        );

        messagesPanel.revalidate();

        messagesPanel.repaint();

        scrollToBottom();

        return row;
    }

    // =========================================================
    // ASSISTANT MESSAGE
    // =========================================================

    private JPanel addAssistantMessage(
            String text
    ) {

        JPanel row =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                4
                        )
                );

        row.setBackground(BG);

        row.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JPanel message =
                createMessageBubble(
                        text,
                        AI_BUBBLE,
                        TEXT
                );

        row.add(message);

        messagesPanel.add(row);

        messagesPanel.add(
                Box.createVerticalStrut(5)
        );

        messagesPanel.revalidate();

        messagesPanel.repaint();

        scrollToBottom();

        return row;
    }

    // =========================================================
    // MESSAGE BUBBLE
    // =========================================================

    private JPanel createMessageBubble(
            String text,
            Color background,
            Color foreground
    ) {

        JPanel bubble =
                new JPanel(
                        new BorderLayout()
                );

        bubble.setBackground(
                background
        );

        bubble.setBorder(
                new EmptyBorder(
                        10,
                        13,
                        10,
                        13
                )
        );

        JTextArea textArea =
                new JTextArea();

        textArea.setText(text);

        textArea.setEditable(false);

        textArea.setFocusable(true);

        textArea.setLineWrap(true);

        textArea.setWrapStyleWord(true);

        textArea.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        textArea.setForeground(
                foreground
        );

        textArea.setBackground(
                background
        );

        textArea.setOpaque(true);

        textArea.setBorder(null);

        textArea.setMargin(
                new Insets(
                        0,
                        0,
                        0,
                        0
                )
        );

        textArea.setUI(
                new BasicTextAreaUI()
        );

        textArea.setForeground(
                foreground
        );

        textArea.setBackground(
                background
        );

        textArea.setCaretColor(
                foreground
        );

        textArea.setOpaque(true);

        int maxWidth = 650;

        int minWidth = 120;

        FontMetrics metrics =
                textArea.getFontMetrics(
                        textArea.getFont()
                );

        String[] lines =
                text.split(
                        "\n",
                        -1
                );

        int longest = 0;

        for (String line : lines) {

            longest =
                    Math.max(
                            longest,
                            metrics.stringWidth(line)
                    );
        }

        int width =
                Math.min(
                        maxWidth,
                        Math.max(
                                minWidth,
                                longest + 5
                        )
                );

        if (longest > maxWidth) {
            width = maxWidth;
        }

        textArea.setSize(
                new Dimension(
                        width,
                        Short.MAX_VALUE
                )
        );

        Dimension preferred =
                textArea.getPreferredSize();

        int height =
                Math.max(
                        25,
                        preferred.height
                );

        textArea.setPreferredSize(
                new Dimension(
                        width,
                        height
                )
        );

        textArea.setMaximumSize(
                new Dimension(
                        width,
                        height
                )
        );

        bubble.add(
                textArea,
                BorderLayout.CENTER
        );

        bubble.setPreferredSize(
                new Dimension(
                        width + 26,
                        height + 20
                )
        );

        bubble.setMaximumSize(
                new Dimension(
                        width + 26,
                        height + 20
                )
        );

        return bubble;
    }

    // =========================================================
    // CONVERSATION
    // =========================================================

    private void addConversation(
            String role,
            String text
    ) {

        conversation.add(
                new ChatMessage(
                        role,
                        text
                )
        );
    }

    // =========================================================
    // NEW CHAT
    // =========================================================

    private void newChat() {

        if (thinking) {
            return;
        }

        conversation.clear();

        messagesPanel.removeAll();

        showWelcomeMessage();

        saveChatHistory();

        scrollToBottom();
    }

    // =========================================================
    // CLEAR CHAT
    // =========================================================

    private void clearChat() {

        if (thinking) {
            return;
        }

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Clear the current conversation?",
                        "Clear Chat",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                result !=
                JOptionPane.YES_OPTION
        ) {
            return;
        }

        conversation.clear();

        messagesPanel.removeAll();

        showWelcomeMessage();

        saveChatHistory();

        scrollToBottom();
    }

    // =========================================================
    // EXPORT CHAT
    // =========================================================

    private void exportChat() {

        if (conversation.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "There is no conversation to export.",
                    "Export Chat",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        JFileChooser chooser =
                new JFileChooser();

        chooser.setDialogTitle(
                "Export Chat"
        );

        chooser.setSelectedFile(
                new File(
                        "chat.txt"
                )
        );

        int result =
                chooser.showSaveDialog(
                        this
                );

        if (
                result !=
                JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File file =
                chooser.getSelectedFile();

        try (
                FileWriter writer =
                        new FileWriter(file)
        ) {

            for (
                    ChatMessage message :
                    conversation
            ) {

                String role =
                        message.role.equals("user")
                                ? "You"
                                : "AI";

                writer.write(
                        role
                        + ":\n"
                        + message.text
                        + "\n\n"
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Chat exported successfully.",
                    "Export Chat",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not export chat:\n"
                    + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // FIREBASE PATH
    // =========================================================

    private String getChatPath() {

        String uid =
                firebaseService.getUserId();

        if (
                uid == null ||
                uid.isBlank()
        ) {

            return null;
        }

        return "users/"
                + uid
                + "/chatHistory";
    }

    // =========================================================
    // SAVE CHAT HISTORY
    // =========================================================

    private void saveChatHistory() {

        String path =
                getChatPath();

        if (path == null) {
            return;
        }

        JsonArray array =
                new JsonArray();

        for (
                ChatMessage message :
                conversation
        ) {

            JsonObject object =
                    new JsonObject();

            object.addProperty(
                    "role",
                    message.role
            );

            object.addProperty(
                    "text",
                    message.text
            );

            array.add(object);
        }

        SwingWorker<Void, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected Void doInBackground() {

                        try {

                            firebaseService.write(
                                    path,
                                    gson.toJson(array)
                            );

                        } catch (Exception ignored) {
                        }

                        return null;
                    }
                };

        worker.execute();
    }

    // =========================================================
    // LOAD CHAT HISTORY
    // =========================================================

    private void loadChatHistory() {

        String path =
                getChatPath();

        if (path == null) {
            return;
        }

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        try {

                            return firebaseService.read(
                                    path
                            );

                        } catch (Exception e) {

                            return null;
                        }
                    }

                    @Override
                    protected void done() {

                        String json;

                        try {

                            json = get();

                        } catch (Exception e) {

                            return;
                        }

                        if (
                                json == null ||
                                json.isBlank() ||
                                json.equals("null")
                        ) {
                            return;
                        }

                        try {

                            JsonElement element =
                                    JsonParser.parseString(
                                            json
                                    );

                            if (
                                    !element.isJsonArray()
                            ) {
                                return;
                            }

                            JsonArray array =
                                    element.getAsJsonArray();

                            if (array.isEmpty()) {
                                return;
                            }

                            conversation.clear();

                            messagesPanel.removeAll();

                            for (
                                    JsonElement item :
                                    array
                            ) {

                                if (
                                        !item.isJsonObject()
                                ) {
                                    continue;
                                }

                                JsonObject object =
                                        item.getAsJsonObject();

                                String role =
                                        object.has("role")
                                                ? object.get("role")
                                                        .getAsString()
                                                : "";

                                String text =
                                        object.has("text")
                                                ? object.get("text")
                                                        .getAsString()
                                                : "";

                                if (
                                        text.isBlank()
                                ) {
                                    continue;
                                }

                                conversation.add(
                                        new ChatMessage(
                                                role,
                                                text
                                        )
                                );

                                if (
                                        role.equals("user")
                                ) {

                                    addUserMessage(
                                            text
                                    );

                                } else {

                                    addAssistantMessage(
                                            text
                                    );
                                }
                            }

                            scrollToBottom();

                        } catch (Exception ignored) {
                        }
                    }
                };

        worker.execute();
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        if (thinking) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please wait for the current response to finish.",
                    "Please Wait",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        firebaseService.logout();

        dispose();

        SwingUtilities.invokeLater(
                () -> {

                    AuthGUI authGUI =
                            new AuthGUI();

                    authGUI.setVisible(true);
                }
        );
    }

    // =========================================================
    // SCROLL
    // =========================================================

    private void scrollToBottom() {

        SwingUtilities.invokeLater(
                () -> {

                    JScrollBar vertical =
                            messagesScroll
                                    .getVerticalScrollBar();

                    vertical.setValue(
                            vertical.getMaximum()
                    );
                }
        );
    }

    // =========================================================
    // CHAT MESSAGE
    // =========================================================

    private static class ChatMessage {

        private final String role;
        private final String text;

        private ChatMessage(
                String role,
                String text
        ) {

            this.role = role;
            this.text = text;
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    try {

                        UIManager.setLookAndFeel(
                                UIManager.getCrossPlatformLookAndFeelClassName()
                        );

                    } catch (Exception ignored) {
                    }

                    AuthGUI authGUI =
                            new AuthGUI();

                    authGUI.setVisible(true);
                }
        );
    }
}