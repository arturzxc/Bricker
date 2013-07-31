package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JOptionPane;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter.*;
import twitter4j.Status;
import twitter4j.User;

public class Main extends SimpleApplication {

    //Counts updates. It is used if tweets are to be received
    //in intervals
    int counter = 0;
    //Location of creation of tweets
    private static Vector3f loc1;
    //Gravity of physics engine.
    private static Vector3f gravity;
    /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;
    private Geometry floor_geo;
    /** Prepare Materials */
    private Material tweetMaterial;
    private Material tweetStartPointMaterial;
    /** Prepare geometries and physical nodes for bricks and cannon balls. */
    private RigidBodyControl tweet_phy;
    private RigidBodyControl tweet_floor_phy;
    private static final Box floor;
    //Twitter vars
    private static int tweetGetDelay; //delay between tweets
    private Twitter4JStreamer streamer;
    private AccessToken aToken;
    private Configuration config;

    static {//set static variables
        tweetGetDelay = 1;//the gr
        loc1 = new Vector3f(0, 0.0f, 0);
        gravity = new Vector3f(0, 1f, 0);
        floor = new Box(Vector3f.ZERO, 2f, 0.05f, 2f);
        floor.scaleTextureCoordinates(new Vector2f(1, 1));

    }

    @Override
    public void simpleInitApp() {
        guiNode.detachAllChildren();//remove debugging info
        
        viewPort.setBackgroundColor(ColorRGBA.White);
        /** Set up Physics  */
        bulletAppState = new BulletAppState();
        //add physics to state manager
        stateManager.attach(bulletAppState);
        //set gravity which is a little up so it looks like fountain of tweets
        bulletAppState.getPhysicsSpace().setGravity(gravity);

        //Camera location
        cam.setLocation(new Vector3f(10, 10, 20));
        //location that camera looks at
        cam.lookAt(new Vector3f(0, 3, 0), Vector3f.UNIT_Y);
        //speed of moving camera around
        flyCam.setMoveSpeed(15f);

        
        setupTweeter();
        initMaterials();
        //botom floor
        initTwitterStartPoint(new Vector3f(0, -0.1f, 0));//This is something like "floor"
        //top floor
        initTwitterStartPoint(new Vector3f(0, 15.1f, 0));//This is something like "floor"
    }

    //Materials required for our objects
    public void initMaterials() {
        tweetMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
        TextureKey key = new TextureKey("Textures/tweetbird.png");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        //ColorMap is one of maps that a material can have
        tweetMaterial.setTexture("ColorMap", tex);

        tweetStartPointMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/twitter.png");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        tweetStartPointMaterial.setTexture("ColorMap", tex3);
    }

    //This will make a sort of floor with mass 0 so it
    //it is not affected by gravity
    public void initTwitterStartPoint(Vector3f loc) {
        floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(tweetStartPointMaterial);
        floor_geo.setLocalTranslation(loc);
        this.rootNode.attachChild(floor_geo);//attach to scene graph
        //Mass 0 so gravity doesnt drag it down
        tweet_floor_phy = new RigidBodyControl(0.0f);
        //add it to physics so other physical objects will collide with it
        floor_geo.addControl(tweet_floor_phy);
        bulletAppState.getPhysicsSpace().add(tweet_floor_phy);

    }

    //Create cubes that represent tweets
    //The bigger the cube the more follower the user has
    public void makeTweetBrick(Vector3f loc, Status status) {
        //cube size is based on the user followers
        float size = (status.getUser().getFollowersCount() / 1500 + 0.15f);
        Box box2 = new Box(Vector3f.ZERO, size, size, size);        
        Geometry brick_geo = new Geometry("brick", box2);
        //add meterial togeometry of tweet cube
        brick_geo.setMaterial(tweetMaterial);
        rootNode.attachChild(brick_geo);
        //position the tweet cube
        brick_geo.setLocalTranslation(loc);
        //set up physical body of tweet cube with mass 2.0
        tweet_phy = new RigidBodyControl(2.0f);
        // Add physical tweet cube to physics space. 
        brick_geo.addControl(tweet_phy);
        bulletAppState.getPhysicsSpace().add(tweet_phy);
    }

    public void setupTweeter() {
        // set up & authenticate to Twitter
        config = Twitter4JTools.init();
        aToken = Twitter4JTools.getAccessToken("pathisharrdcoded", config);
        // this will get a live stream (random sample)
        streamer = new Twitter4JStreamer(config, aToken, true);
    }

    /*
     * This method prints tweets and returns status if there is one
     */
    public Status printNewTweets() {
        // get latest tweet 
        if (streamer.hasNew) {
            Status status = streamer.getLatestTweet();
            User user = status.getUser();

            // clear screen and print the tweet
            String toPrint = "\nTweet ID: " + status.getId()
                    + "\nText: " + status.getText()
                    + "\nTimestamp: " + status.getCreatedAt()
                    + "\nLocation (GPS): " + status.getGeoLocation();
            if (user != null) {
                toPrint += "\nUsername: " + user.getScreenName()
                        + "\nUser ID: " + user.getId()
                        + "\nFull name " + user.getName()
                        + "\nLocation: " + user.getLocation();
                System.out.println(toPrint);
            }
            return status;
        }
        return null;
    }

    /*
     * This method is getting called by the framework on every tick 
     * of the program. 
     */
    @Override
    public void simpleUpdate(float fl) {
        //this will fetch status once per couple cycles
        if (counter > tweetGetDelay) {
            Status status = printNewTweets();
            if (status != null) {
                //try to make tweet cube that encorporates some of
                //the information that we got from tweeter
                makeTweetBrick(loc1, status);

            }
            counter = 0;
        }
        counter++;

    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public static void main(String[] args) {
        Main app = new Main();
        
        //Info about how to move around the world
        JOptionPane.showMessageDialog(null, "User mouse to look around and WSAD buttons to move around. "
                + "\n When Application exits it will crush. This is knows to developers of JME3"
                + "\n\"Its a known issue with lwjgl 2.8.1 on windows 64bit, as soon as 2.8.2 is out we add that.\"");
        app.setShowSettings(false);//Do not show settings window
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(true);//set application to be full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        settings.put("Width", width);
        settings.put("Height", height);
        settings.put("Title", "Tweeter Bricks Fountain");
        settings.put("VSync", true);
        app.setSettings(settings);
        
        app.start();

    }
}