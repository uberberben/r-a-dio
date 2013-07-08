package com.ben.ber;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class SearchFrame extends JFrame implements ActionListener {
    private static JList<String> searchList;
    private static JButton search, request;
    private static JTextField searchQuery;
    private static JLabel requestAvailable, requestResult;
    private static DefaultListModel listModel;
    private static String postBody;
    private static String resultText;

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
        request.addActionListener(this);
        add(request, constraints);

        requestResult = new JLabel("");
        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        constraints.insets = new Insets(10, 0, 0, 0);
        add(requestResult, constraints);
        requestResult.setVisible(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Saving form data (nick, pass, channel)
                SearchApiParser.clearStuff();
            }
        });

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
        }
        if (source == request) {
            System.out.println("test");
            int index = searchList.getSelectedIndex();
            Track selectedTrack = (Track) listModel.getElementAt(index);
            System.out.println(selectedTrack.trackName);
            String requestResult = requestTrack(selectedTrack);
            setRequestResult(requestResult);
            SearchApiParser.parseIsRequestPossible("");
        }
    }

    public static void addListElements(ArrayList<Track> tracks) {
        listModel.clear();
        for (Track track : tracks) {
            listModel.addElement(track);
        }
    }

    public static void setIsRequestPossible(boolean isRequestPossible) {
        if (isRequestPossible == false) {
            requestAvailable.setText("You can't make request at the moment");
        } else {
            requestAvailable.setText("You can make a request");
        }
    }

    private static String requestTrack(Track track) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://r-a-d.io/request/index.py");
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("songid", Integer.toString(track.trackId)));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream in = entity.getContent();
                try {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String str;
                    while ((str = buf.readLine()) != null) {
                        sb.append(str);
                    }
                    postBody = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postBody;
    }

    private static void setRequestResult(String result) {


        if (result.matches(".*You need to wait longer before requesting again.*")) {
            resultText = "You need to wait longer before requesting again.";
        } else if (result.matches(".*You need to wait longer before requesting this song.*")) {
            resultText = "You need to wait longer before requesting this song.";
        } else if (result.matches(".*Thank you for making your request!.*")) {
            resultText = "Thank you for making your request!";
        } else if (result.matches(".*Invalid parameter.*")) {
            resultText = "Invalid parameter.";
        } else if (result.matches(".*You can't request songs at the moment.*")) {
            resultText = "You can't request songs at the moment.";
        } else {
            resultText = "Unknown error";
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestResult.setText(resultText);
                    requestResult.setVisible(true);
                    Thread.sleep(5000);
                    requestResult.setVisible(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
