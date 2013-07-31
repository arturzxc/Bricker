/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

import java.util.ArrayList;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.*;
import twitter4j.conf.*;

/**
 *
 * @author arturzxc
 */
// a tool for getting tweets from the Streaming API
public class Twitter4JStreamer {

    ArrayList tweets;
    private static final int MAX_TWEETS = 10;
    public boolean hasNew;

    public Twitter4JStreamer(Configuration config, AccessToken aToken, boolean london) {
        hasNew = false;
        tweets = new ArrayList();
        StatusListener listener = new TJStatusListener(this);
        TwitterStreamFactory tsf = new TwitterStreamFactory(config);
        TwitterStream twitterStream = tsf.getInstance(aToken);
        twitterStream.addListener(listener);
        if (london) {
            FilterQuery fq = new FilterQuery();
            fq.locations(new double[][]{
                        {
                            -0.4, 51.3
                        }, {
                            0.4, 51.6
                        }
                    });
            twitterStream.filter(fq);
        } else {
            // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
            twitterStream.sample();
        }
    }

    public void addTweet(Status tweet) {
        tweets.add(0, tweet);
        hasNew = true;
        if (tweets.size() > MAX_TWEETS) {
            tweets.remove(MAX_TWEETS);
        }
    }

    public Status getLatestTweet() {
        return (Status) tweets.get(0);
    }

    public ArrayList getLatestTweets() {
        return tweets;
    }
}
