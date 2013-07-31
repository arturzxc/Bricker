/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package twitter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;

/**
 *
 * @author arturzxc
 */
// a class for accessing the search method of the Twitter Search API
public class Twitter4JSearcher {

    private Twitter twitter;

    public Twitter4JSearcher(Configuration config, AccessToken aToken) {
        twitter = new TwitterFactory(config).getInstance(aToken);
    }

    public QueryResult search(String searchTerm, int page) {
        Query query = new Query(searchTerm);
        // query.page(page); // Twitter4J now requires nextPage() for further pages
        try {
            return twitter.search(query);
        } catch (TwitterException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}