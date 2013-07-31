/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 *
 * @author arturzxc
 */
// a listener for the Twitter4JStreamer
public class TJStatusListener implements StatusListener {

    Twitter4JStreamer ts;

    public TJStatusListener(Twitter4JStreamer ts) {
        this.ts = ts;
    }

    public void onStatus(Status status) {
        ts.addTweet(status);
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        // should really remove deleted tweets here ...
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    }

    public void onScrubGeo(long userId, long upToStatusId) {
        // should really remove deleted location information here ...
    }

    public void onStallWarning(StallWarning stallWarning) {
        // should really do something about stalls here ...
    }

    public void onException(Exception ex) {
        ex.printStackTrace();
    }
}
