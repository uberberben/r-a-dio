package com.ben.ber;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.UIManager;

// Main frame with some additional stuff
public class GUI extends JFrame {
    private static JTextPane textArea;
    private static JButton msgSend;
    private static JScrollPane scrollPane;
    private static StyledDocument doc;
    private static JTextField msgField;
    private final static String newline = "\n";
    private String tempMsg;


    public static GUI gui;

    // GUI singleton getter
    public static GUI getGUIObject() {
        if (gui == null) {
            gui = new GUI();
        }
        return gui;

    }


    private GUI() {
        super("R/a/dio bot");

        try {
            // Windows theme, if not accessible fallback to default java look and feel
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException ex) {
            appendTextArea(ex.toString());

        } catch (InstantiationException ex) {
            appendTextArea(ex.toString());
        } catch (IllegalAccessException ex) {
            appendTextArea(ex.toString());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            appendTextArea(ex.toString());
        }


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(50, 50);
        setSize(500, 680);
        setLayout(new FlowLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Saving form data (nick, pass, channel)
                LoginPanel.setLoginData();
            }
        });
        // Labels, buttons, text areas

        JPanel labels = new Labels();
        add(labels);

        JPanel buttons = new Buttons();
        add(buttons);

        JPanel loginForm = new LoginPanel();
        add(loginForm);

        textArea = new JTextPane();
        textArea.setText("");
        textArea.setEditable(false);
        // Look at the WrapEditorKit class for explanation
        textArea.setEditorKit(new WrapEditorKit());
        scrollPane = new JScrollPane(textArea) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 500);
            }
        };

        doc = textArea.getStyledDocument();

        /* Smart scrolling
         * when at bottom it will scroll automatically
         * when somewhere else it will stay there until scrolled to bottom
         */
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            BoundedRangeModel brm = scrollPane.getVerticalScrollBar().getModel();
            boolean wasAtBottom = true;
            int temp;

            public void adjustmentValueChanged(AdjustmentEvent e) {


                System.out.println("Value: " + brm.getValue() + " Extent: " + brm.getExtent() + " Max: " + brm.getMaximum());
                System.out.println("O ile: " + scrollPane.getVerticalScrollBar().getBlockIncrement(1));
                scrollPane.getVerticalScrollBar().setUnitIncrement(17);

                if (brm.getValueIsAdjusting()) {

                    wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                    System.out.println("3");

                } else {
                    if (wasAtBottom == true) {
                        if (brm.getMaximum() - brm.getExtent() - brm.getValue() == scrollPane.getVerticalScrollBar().getUnitIncrement(1)) {
                            wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                            System.out.println("666");
                            return;
                        }
                        brm.setValue(brm.getMaximum());
                        System.out.println("1");

                    } else {
                        wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                        System.out.println("2");
                    }
                }
            }
        });
        // DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        // caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        add(scrollPane);

        // Custom IRC message field
        msgField = new JTextField();
        msgField.setPreferredSize(new Dimension(425, 22));
        add(msgField);

        msgSend = new JButton("Send");
        msgSend.setPreferredSize(new Dimension(50, 22));
        msgSend.setFont(new Font("Roboto", Font.PLAIN, 10));
        msgSend.setMargin(new Insets(1, 1, 1, 1));
        add(msgSend);

        // Custom IRC message button listener
        msgSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempMsg = msgField.getText();
                if (IRCClient.getConnection() != null && IRCClient.getConnection().isConnected()) {
                    try {
                        IRCClient.shipInput(tempMsg);
                        msgField.setText("");
                    } catch (Exception ex) {
                        appendTextArea(ex.toString());
                    }
                }
            }
        });

        // Send message on "Enter" press while typing message
        msgField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    msgSend.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(true);
                // Request focus on Nick text area on GUI start
                LoginPanel.requestFocusNick();
            }
        });



    }

    // Custom message field focus setter
    public static void requestFocusMsgField() {
        msgField.requestFocusInWindow();
    }

    // Now playing setter
    public static void setNowPlaying(String text) {
        Labels.setNP(text);
    }
    // Setting progress bar value and max value
    public static void setProgress(int current, int max){
        Labels.setProgressBarValue(current, max);
    }

    // Main chat appender with some text styles
    public static void appendTextArea(String text) {
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        SimpleAttributeSet keyWordBold = new SimpleAttributeSet();
        SimpleAttributeSet botMsg = new SimpleAttributeSet();
        SimpleAttributeSet botMsgBold = new SimpleAttributeSet();
        SimpleAttributeSet hanyuuMsg = new SimpleAttributeSet();
        SimpleAttributeSet hanyuuMsgBold = new SimpleAttributeSet();
        StyleConstants.setBackground(botMsg, Color.black);
        StyleConstants.setForeground(botMsg, Color.white);
        StyleConstants.setForeground(hanyuuMsg, Color.white);
        StyleConstants.setBackground(botMsgBold, Color.black);
        StyleConstants.setForeground(botMsgBold, Color.YELLOW);
        StyleConstants.setForeground(hanyuuMsgBold, Color.BLUE);
        StyleConstants.setBold(botMsgBold, true);
        StyleConstants.setBold(keyWordBold, true);
        StyleConstants.setBold(hanyuuMsgBold, true);

        int separator = text.indexOf(">");
        // doc.setCharacterAttributes(0,separator,keyWordBold,true);

        //  StyleConstants.setForeground(keyWord, Color.RED);
        //  StyleConstants.setBackground(keyWord, Color.YELLOW);

        // Style setting
        try {
            if (text.startsWith("Hanyuu-sama", LoginPanel.getNickname().length() + 2)) {
                doc.insertString(doc.getLength(), text.substring(0, separator + 1), botMsgBold);
                doc.insertString(doc.getLength(), text.substring(separator + 1) + "\n", botMsg);
            } else if (text.startsWith("Hanyuu-sama")) {
                doc.insertString(doc.getLength(), text.substring(0, separator + 1), hanyuuMsgBold);
                doc.insertString(doc.getLength(), text.substring(separator + 1) + "\n", keyWord);
            } else {
                doc.insertString(doc.getLength(), text.substring(0, separator + 1), keyWordBold);
                doc.insertString(doc.getLength(), text.substring(separator + 1) + "\n", keyWord);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

// Panel with login form
class LoginPanel extends JPanel implements ActionListener {
    private static JTextField nickField, channelField;
    private static JPasswordField nickservPass;
    private static JButton loginButton;
    private String[] loginData;

    // Nick field focus setter
    public static void requestFocusNick() {
        nickField.requestFocusInWindow();
    }

    public static String getNickname() {
        return nickField.getText();
    }

    /*
     * Saving form data
     * need to change password getter
     */
    public static void setLoginData() {
        SaveData.setPreference(nickField.getText(), nickservPass.getText(), channelField.getText());
    }

    public LoginPanel() {
        // Get saved credentials
        loginData = SaveData.getPreference();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        gridBag.setConstraints(this, constraints);
        setLayout(gridBag);

        channelField = new JTextField(loginData[2]);
        nickField = new JTextField(loginData[0]);
        nickservPass = new JPasswordField(loginData[1]);
        loginButton = new JButton("GO");
        nickField.setColumns(10);
        nickservPass.setColumns(10);
        channelField.setColumns(10);


        loginButton.addActionListener(this);
        add(nickField);
        add(nickservPass);
        add(channelField);
        add(loginButton);

        addListeners();
    }

    /*
    *  Keyboard and mouse listeners on text fields
    */
    public static void addListeners() {


        // Select text when focused
        nickField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                nickField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        nickservPass.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                nickservPass.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        // Select text on focus, but without '#' at the beginning
        channelField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                channelField.select(1, channelField.getText().length());

            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        // OK button clicking on "Enter" press
        nickservPass.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


                if (e.getKeyChar() == '\n') {
                    loginButton.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        nickField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    loginButton.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        channelField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


                if (e.getKeyChar() == '\n') {
                    loginButton.doClick();
                }

                // Insert '#' at the beginning when not there
                if (!channelField.getText().startsWith("#")) {
                    channelField.setText(new StringBuilder(channelField.getText()).insert(0, "#").toString());
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    // Login button listener, starts the whole connection
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        IRCClient.setNickservPass(nickservPass.getText());
        if (source == loginButton) {
            IRCClient.setChannel(channelField.getText());
            IRCClient.startLogin(new String[]{"-server", "irc.rizon.net:6660", "-nick", (String) nickField.getText()});
            GUI.requestFocusMsgField();
        }
    }
}

// Now playing label class

class Labels extends JPanel {
    private static JLabel np, time;
    private static JProgressBar progressBar;


    public Labels() {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        gridBag.setConstraints(this, constraints);
        setLayout(gridBag);

        constraints.gridy = 0;
        np = new JLabel("Now Playing");
        np.setPreferredSize(new Dimension(480, 20));
        np.setHorizontalAlignment(SwingConstants.CENTER);
        add(np, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        progressBar = new JProgressBar(0, 1);
        progressBar.setMaximum(100);
        progressBar.setValue(33);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(250,20));
        add(progressBar, constraints);

        constraints.gridy = 2;
       // constraints.anchor = GridBagConstraints.WEST;
        time = new JLabel("0:00/0:00");
       // time.setPreferredSize(new Dimension(20,20));
        add(time, constraints);


    }
    //Very simple method used to set progressbar and timer value
    public static void setProgressBarValue(int current, int max){
        if (current > max){
            return;
        }
        progressBar.setMaximum(max);
        progressBar.setValue(current);

        StringBuilder sb = new StringBuilder();
        int nowMin = current / 60;
        int nowSec = current % 60;
        if (nowMin < 10){
            sb.append("0");
        }
        sb.append(nowMin);
        sb.append(":");
        if (nowSec < 10){
            sb.append("0");
        }
        sb.append(nowSec);

        sb.append("/");

        int maxMin = max / 60;
        int maxSec = max % 60;

        if (maxMin < 10){
            sb.append("0");
        }
        sb.append(maxMin);
        sb.append(":");

        if (maxSec < 10){
            sb.append("0");
        }
        sb.append(maxSec);

        time.setText(sb.toString());

    }



    // Now playing setter
    public static void setNP(String text) {
        np.setText(text);
    }
}

// Fave, unfave etc buttons class

class Buttons extends JPanel implements ActionListener {
    private JButton fave;
    private JButton unfave;
    private JButton queue, play, pause, stop, search;
    SearchFrame searchFrame;


    public Buttons() {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        gridBag.setConstraints(this, constraints);
        setLayout(gridBag);

        //np = new JLabel("Now Playing");
        fave = new JButton("Fave");
        unfave = new JButton("Unfave");
        queue = new JButton("Queue");
        play = new JButton("Play");
        pause = new JButton("Pause");
        stop = new JButton("Stop");
        search = new JButton("Search");

        fave.addActionListener(this);
        unfave.addActionListener(this);
        queue.addActionListener(this);
        play.addActionListener(this);
        pause.addActionListener(this);
        stop.addActionListener(this);
        search.addActionListener(this);

        //  setLayout(new FlowLayout());
        //  setPreferredSize(new Dimension(300, 100));

        // Button positions, not really needed now but w/e
        constraints.fill = GridBagConstraints.CENTER;
        constraints.gridy = 1;
        constraints.gridx = 0;
        add(fave, constraints);
        constraints.gridx = 1;
        add(unfave, constraints);
        constraints.gridx = 2;
        add(queue, constraints);
        constraints.gridx = 3;
        add(play, constraints);
        constraints.gridx = 4;
        add(pause, constraints);
        constraints.gridx = 5;
        add(stop, constraints);
        constraints.gridx = 6;
        add(search, constraints);

    }

    // Button listeners
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == fave) {
            IRCClient.getConnection().doPrivmsg("Hanyuu-sama", ".fave");
            GUI.appendTextArea("Hanyuu-sama>" + ".fave");
        } else if (source == unfave) {
            IRCClient.getConnection().doPrivmsg("Hanyuu-sama", ".unfave");
            GUI.appendTextArea("Hanyuu-sama>" + ".unfave");
        } else if (source == queue) {
            IRCClient.getConnection().doPrivmsg(IRCClient.getTarget(), ".q");
            GUI.appendTextArea(IRCClient.getTarget() + " >" + ".q");
        } else if (source == play) {
            AudioPlayer.getPlayerObject().setPlay();
        } else if (source == pause) {
            AudioPlayer.getPlayerObject().setPause();
        } else if (source == stop) {
            AudioPlayer.getPlayerObject().setStop();
        } else if (source == search) {
            if (searchFrame != null){
                searchFrame = null;
            }
            searchFrame = new SearchFrame();
        }
    }
}


/*
* Custom class to help with word wrapping in main chat text area
* dunno why but with default settings it sometimes gets broken
* solution from https://forums.oracle.com/message/10692405
*
* */
class WrapEditorKit extends StyledEditorKit {
    ViewFactory defaultFactory=new WrapColumnFactory();
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

}

class WrapColumnFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new WrapLabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }

        // default to text display
        return new LabelView(elem);
    }
}

class WrapLabelView extends LabelView {
    public WrapLabelView(Element elem) {
        super(elem);
    }

    public float getMinimumSpan(int axis) {
        switch (axis) {
            case View.X_AXIS:
                return 0;
            case View.Y_AXIS:
                return super.getMinimumSpan(axis);
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

}










