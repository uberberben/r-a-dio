package com.ben.ber;


import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import javax.swing.UIManager;


public class GUI extends JFrame implements Runnable {
    private static JTextPane textArea;
    private static JButton msgSend;
    private static JScrollPane scrollPane;
    private static StyledDocument doc;
    private static JTextField msgField;
    private final static String newline = "\n";
    private String tempMsg;


    public static GUI gui;


    public void run() {

    }

    public static GUI getGUIObject() {
        if (gui == null) {
            gui = new GUI();
        }
        return gui;

    }


    private GUI() {
        super("R/a/dio bot");

        try {

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
        setSize(500, 650);
        setLayout(new FlowLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LoginPanel.setLoginData();
            }
        });
        /* przyciski */

        JPanel labels = new Labels();
        add(labels);

        JPanel buttons = new Buttons();
        add(buttons);

        JPanel loginForm = new LoginPanel();
        add(loginForm);

        textArea = new JTextPane();
        textArea.setText("");
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 500);
            }
        };

        doc = textArea.getStyledDocument();


        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            BoundedRangeModel brm = scrollPane.getVerticalScrollBar().getModel();
            boolean wasAtBottom = true;
            int temp;

            public void adjustmentValueChanged(AdjustmentEvent e) {


                System.out.println("Value: " + brm.getValue() + " Extent: " + brm.getExtent() + " Max: " + brm.getMaximum());
                System.out.println("O ile: " + scrollPane.getVerticalScrollBar().getBlockIncrement(1));
                scrollPane.getVerticalScrollBar().setUnitIncrement(17);



                /*

                if (!brm.getValueIsAdjusting()) {

                    if (wasAtBottom == true){

                        brm.setValue(brm.getMaximum());
                        System.out.println("1");

                    } else {
                        wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                        System.out.println("2");
                    }
                } else {
                    wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                System.out.println("3");
                }   */

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

        msgField = new JTextField();
        msgField.setPreferredSize(new Dimension(425, 22));
        add(msgField);

        msgSend = new JButton("Send");
        msgSend.setPreferredSize(new Dimension(50, 22));
        msgSend.setFont(new Font("Roboto", Font.PLAIN, 10));
        msgSend.setMargin(new Insets(1, 1, 1, 1));
        add(msgSend);

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

        msgField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    msgSend.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });


        setVisible(true);
        LoginPanel.requestFocusNick();
    }

    public static void requestFocusMsgField() {
        msgField.requestFocusInWindow();
    }

    public static void changeNowPlaying(String text) {
        Labels.setNP(text);
    }

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


class LoginPanel extends JPanel implements ActionListener {
    private static JTextField nickField, channelField;
    private static JPasswordField nickservPass;
    private static JButton loginButton;
    private String[] loginData;

    public static void requestFocusNick() {
        nickField.requestFocusInWindow();
    }

    public static String getNickname() {
        return nickField.getText();
    }

    public static void setLoginData() {
        SaveData.setPreference(nickField.getText(), nickservPass.getText(), channelField.getText());
    }

    public LoginPanel() {
        /* get saved credentials */
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

    public static void addListeners() {
                /* listenery myszy u klawiatury do pol tekstowych */
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
        channelField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                channelField.select(1, channelField.getText().length());

            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        /* nickField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nickField.setText("");
            }
        });

        nickservPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                nickservPass.setText("");
            }
        });  */

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

     /*   channelField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                channelField.setText("");
            }
        });      */

        channelField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


                if (e.getKeyChar() == '\n') {
                    loginButton.doClick();
                }

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

class Labels extends JPanel {
    private static JLabel np;


    public Labels(){
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        gridBag.setConstraints(this, constraints);
        setLayout(gridBag);

        np = new JLabel("Now Playing");
        np.setPreferredSize(new Dimension(480,20));
        np.setHorizontalAlignment(SwingConstants.CENTER);
        add(np);


    }

    public static void setNP(String text){
        np.setText(text);
    }
}

class Buttons extends JPanel implements ActionListener {
    private JButton fave;
    private JButton unfave;
    private JButton queue, play, pause, stop;
    //  private static JLabel np;

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

        fave.addActionListener(this);
        unfave.addActionListener(this);
        queue.addActionListener(this);
        play.addActionListener(this);
        pause.addActionListener(this);
        stop.addActionListener(this);

        //  setLayout(new FlowLayout());
        //  setPreferredSize(new Dimension(300, 100));


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

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == fave) {
            IRCClient.getConnection().doPrivmsg("Hanyuu-sama", ".fave");
            GUI.appendTextArea("Hanyuu-sama>" + ".fave");
            //GUI.appendTextArea("asdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\nasdasdasdasdadqqqq\n");
        } else if (source == unfave) {
            IRCClient.getConnection().doPrivmsg("Hanyuu-sama", ".unfave");
            GUI.appendTextArea("Hanyuu-sama>" + ".unfave");
            // GUI.appendTextArea(("asd"));
        } else if (source == queue) {
            IRCClient.getConnection().doPrivmsg(IRCClient.getTarget(), ".q");
            GUI.appendTextArea(IRCClient.getTarget() + " >" + ".q");
        }  else if (source == play) {
            AudioPlayer.getPlayerObject().setPlay();
        } else if (source == pause) {
            AudioPlayer.getPlayerObject().setPause();
        } else if (source == stop) {
            AudioPlayer.getPlayerObject().setStop();
        }
    }
}










