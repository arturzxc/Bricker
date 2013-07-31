package twitter;

import twitter4j.conf.*;
import twitter4j.auth.*;
import twitter4j.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// general tools
public class Twitter4JTools {

    // OAuth key & secret for the application "Interaction Design Coursework"
    private static final String consumerKey = "XWqjCqBE4AAnj1kNFQIeoA";
    private static final String consumerSecret = "Oik9MZ6gyWMMvEsjq6MDgSMr1qCXqWC9IsAKEac";
    // variables for private user OAuth data
    private static final String accessTokenFile = "access-token.txt";
    private static String pin = null;

    // initialise configuration
    public static Configuration init() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        Configuration config = cb.build();
        return config;
    }

    public static void setPin(String pin) {
        System.out.println("Set PIN " + pin);
        Twitter4JTools.pin = pin;
    }

    //HARDCODED THE TOKEN DETAILS AS ONLY I WILL USE IT. APP IS NOT FOR PRODUCTION
    public static AccessToken getAccessToken(String path, Configuration config) {
        AccessToken at = null;

        String token = "325878645-W8s04eclPdOy1QOjyh4WTcdHkeCNttPYTiUIDlAZ";
        String tokenSecret = "3M0lrV45DRqZCd5BxCrDHEik5wThOLmkrhc7yqWZYjs";

        at = new AccessToken(token, tokenSecret);

        if (at == null) {
            try {
                Twitter tw = new TwitterFactory(config).getInstance();
                RequestToken requestToken = tw.getOAuthRequestToken();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (at == null) {


                    // pop up a dialog box to give the URL and wait for the PIN
                    JFrame frame = new JFrame("Twitter Authorisation");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLayout(new GridLayout(5, 1));
                    JTextField textField = new JTextField(10);
                    textField.setText(requestToken.getAuthorizationURL());
                    JLabel l1 = new JLabel("Open the following URL and grant access to your account:");
                    l1.setLabelFor(textField);
                    frame.add(l1);
                    frame.add(textField);
                    JTextField textField2 = new JTextField(10);
                    JLabel l2 = new JLabel("Enter the PIN here and press RETURN:");
                    l2.setLabelFor(textField2);
                    frame.add(l2);
                    frame.add(textField2);
                    textField2.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            Twitter4JTools.setPin(e.getActionCommand());
                        }
                    });
                    // Display the window.
                    frame.pack();
                    frame.setVisible(true);

                    while (pin == null) {
                        // wait ...
                        Thread.sleep(1);
                    }

                    try {
                        if (pin.length() > 0) {
                            at = tw.getOAuthAccessToken(requestToken, pin);
                        } else {
                            at = tw.getOAuthAccessToken();
                        }
                    } catch (TwitterException te) {
                        if (401 == te.getStatusCode()) {
                            System.out.println("Unable to get the access token.");
                        } else {
                            te.printStackTrace();
                        }
                    }
                }
                /*
                // write to file
                BufferedWriter bw = new BufferedWriter(new FileWriter(tokenFile));
                bw.write(at.getToken() + "\n");
                bw.write(at.getTokenSecret() + "\n");
                bw.close();*/
            } catch (Exception e) {
                // couldn't write file? die
                e.printStackTrace();
                System.exit(0);
            }
        }
        return at;
    }

    // convenience method for getting an API instance
    public static Twitter getTwitter(Configuration config, AccessToken aToken) {
        return new TwitterFactory(config).getInstance(aToken);
    }
}




