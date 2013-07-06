package com.ben.ber;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SearchFrame extends JFrame implements ActionListener {
    private static JList<String> searchList;
    private static JButton search, request;
    private static JTextField searchQuery;
    private static JLabel requestAvailable;
    private static DefaultListModel listModel;

    public SearchFrame() {
        super("Search");

        try {
            // Windows theme, if not accessible fallback to default java look and feel
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (InstantiationException ex) {
            System.out.println(ex.toString());
        } catch (IllegalAccessException ex) {
            System.out.println(ex.toString());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println(ex.toString());
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocation(50, 50);
        setSize(500, 680);

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        gridBag.setConstraints(this, constraints);
        setLayout(gridBag);


        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        requestAvailable = new JLabel("...");
        add(requestAvailable, constraints);

        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 0, 0, 0);
        searchQuery = new JTextField("...");

        searchQuery.setPreferredSize(new Dimension(330, 20));
        add(searchQuery, constraints);
        search = new JButton("Search");
        constraints.gridx = 1;
        search.setPreferredSize(new Dimension(70, 22));
        search.addActionListener(this);
        add(search, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        constraints.insets = new Insets(10, 0, 0, 0);
        listModel = new DefaultListModel();
        searchList = new JList<String>(listModel);
        //      searchList.setPreferredSize(new Dimension(400,500));
        searchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchList.setLayoutOrientation(JList.VERTICAL);
        searchList.setVisibleRowCount(1);


        JScrollPane listScroller = new JScrollPane(searchList);
        listScroller.setPreferredSize(new Dimension(400, 500));
        add(listScroller, constraints);

        request = new JButton("Request");
        constraints.gridy = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        constraints.insets = new Insets(10, 0, 0, 0);
        add(request, constraints);

        new SearchApiParser();
        addListeners();
        setVisible(true);
    }

    private void addListeners() {
        searchQuery.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                searchQuery.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        // OK button clicking on "Enter" press
        searchQuery.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    search.doClick();
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
        if (source == search) {
            SearchApiParser.doSearch(searchQuery.getText());
            System.out.println("yup");
        }  else if (source == request){
            System.out.println("nope for now");
        }
    }

    public static void addListElements(ArrayList<Track> tracks) {
        listModel.clear();
        for (Track track : tracks) {
            listModel.addElement(track.artistName + " - " + track.trackName);
        }
    }

    public static void setIsRequestPossible(boolean isRequestPossible) {
        if (isRequestPossible == false) {
            requestAvailable.setText("You can't make request at the moment");
        } else {
            requestAvailable.setText("You can make a request");
        }
    }
}
