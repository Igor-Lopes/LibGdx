package com.mygdx.game.com.mygdx.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

/**
 * Created by igor on 1/27/16.
 */
public class Mario {
    private World world;
    private Sprite marioSprite;
    private Vector2 marioPosition;
    private float width;
    private float height;
    private Body marioBody;
    private ArrayList<Body> platforms;
    private ArrayList<Body> falls;
    private ArrayList<Body> ground;
    private Fixture mainSensor;
    private Fixture leftSensor;
    private Fixture rightSensor;
    private Fixture bottomSensor;
   // Boolean variables for sensors collision
    private boolean mainCollision;
    private boolean leftCollision;
    private boolean rightCollision;
    private boolean bottomCollision;
    //Animation Variables
    private Animation walkingLeft;
    private Animation walkingRight;
    private Animation currAnimation;
    private AssetManager assetManager = new AssetManager();
    private TextureAtlas atlas = new TextureAtlas();
    private final String TEXTURE_PATH = "mario_sheet.png";
    private float elapsedTime;
    private boolean playAnimation;
    //Mario's States and Directions
    private enum State {
        spawn, dead, idle, walking, jumping
    }
    private enum Direction {left, right}
    private State marioState;
    private Direction marioDir;

    //Constants
    private final float PPM = 100f;
    //private final float MAX_VELOCITY = 2f;


    public Mario(Vector2 spawnLocation, World w) {

        assetManager.load("mario.pack", TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get("mario.pack");
        marioSprite = new Sprite(atlas.findRegion("mario1"));
        marioSprite.setPosition(spawnLocation.x, spawnLocation.y);

        Texture test = new Texture("m.png");
        test.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);


        marioSprite = new Sprite(test);
        marioSprite.setPosition(spawnLocation.x, spawnLocation.y);


        width = 32;
        height = 52;
        setAnimations();
        world = w;
        marioPosition = spawnLocation;

        initStates();

        marioBody = CreateBodies.createBody(spawnLocation, marioSprite.getWidth(), marioSprite.getHeight() , 700f, 0, 0.90f,
                BodyDef.BodyType.DynamicBody, world, true);
        mainSensor = (Fixture) marioBody.getUserData();
        leftSensor = CreateBodies.createBoxSensor(marioBody, marioSprite.getWidth() / 16 / PPM,
                marioSprite.getHeight() * 0.45f / PPM, new Vector2(-0.20f, 0), 0);
        rightSensor = CreateBodies.createBoxSensor(marioBody, marioSprite.getWidth() / 16 / PPM,
                marioSprite.getHeight() * 0.45f / PPM, new Vector2(0.20f, 0), 0);
        bottomSensor = CreateBodies.createBoxSensor(marioBody, marioSprite.getWidth() / 4f / PPM,
                marioSprite.getHeight() / 4 / PPM, new Vector2(0, -0.20f), 0);

        handleCollisions();

    }

    private void initStates() {
        marioState = State.jumping;
        marioDir = Direction.right;

        mainCollision = false;
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;
    }

    private void setAnimations() {

        walkingRight =  new Animation(1/12f, atlas.getRegions());

        assetManager.load("mario_left.pack", TextureAtlas.class);
        assetManager.finishLoading();
        atlas = assetManager.get("mario_left.pack");
        walkingLeft =  new Animation(1/12f, atlas.getRegions());


        currAnimation = walkingRight;
    }

    public void refreshAnimation() {
        if (marioState == State.walking && marioDir == Direction.left) {

            currAnimation.setFrameDuration(1 / 12f);
            playAnimation = true;
            currAnimation = walkingLeft;
        }
        if (marioState == State.walking && marioDir == Direction.right) {
            currAnimation.setFrameDuration(1 / 12f);
            playAnimation = true;
            currAnimation = walkingRight;
        }
        if (marioState == State.idle && marioDir == Direction.left) {
            currAnimation = walkingLeft;
            playAnimation = false;
        }
        if (marioState == State.idle && marioDir == Direction.right) {
            currAnimation = walkingRight;
            playAnimation = false;
        }
        if (marioState == State.jumping && marioDir == Direction.left) {
            currAnimation = walkingLeft;
            playAnimation = true;
            currAnimation.setFrameDuration(0f);
        }
        if (marioState == State.jumping && marioDir == Direction.right) {
            currAnimation = walkingRight;
            playAnimation = true;
            currAnimation.setFrameDuration(0f);
        }

    }

    public void handleCollisions() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                for (Body b : ground) {
                    if (contact.getFixtureA() == mainSensor && contact.getFixtureB() == b.getUserData()) {
                        mainCollision = true;
                    }
                    if (contact.getFixtureA() == leftSensor && contact.getFixtureB() == b.getUserData()) {
                        leftCollision = true;
                    }
                    if (contact.getFixtureA() == rightSensor && contact.getFixtureB() == b.getUserData()) {
                        rightCollision = true;
                    }
                    if (contact.getFixtureA() == bottomSensor && contact.getFixtureB() == b.getUserData()) {
                        bottomCollision = true;
                        marioState = State.idle;
                    }
                }
                for (Body b : platforms) {
                    if (contact.getFixtureA() == mainSensor && contact.getFixtureB() == b.getUserData()) {
                        mainCollision = true;
                    }
                    if (contact.getFixtureA() == leftSensor && contact.getFixtureB() == b.getUserData()) {
                        leftCollision = true;
                    }
                    if (contact.getFixtureA() == rightSensor && contact.getFixtureB() == b.getUserData()) {
                        rightCollision = true;
                    }
                    if (contact.getFixtureA() == bottomSensor && contact.getFixtureB() == b.getUserData()) {
                        bottomCollision = true;
                        marioState = State.idle;
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

                for (Body b : ground) {
                    if (contact.getFixtureA() == mainSensor && contact.getFixtureB() == b.getUserData()) {
                        mainCollision = false;
                    }
                    if (contact.getFixtureA() == leftSensor && contact.getFixtureB() == b.getUserData()) {
                        leftCollision = false;
                    }
                    if (contact.getFixtureA() == rightSensor && contact.getFixtureB() == b.getUserData()) {
                        rightCollision = false;
                    }
                    if (contact.getFixtureA() == bottomSensor && contact.getFixtureB() == b.getUserData()) {
                        bottomCollision = false;
                        marioState = State.jumping;
                    }
                }
                for (Body b : platforms) {
                    if (contact.getFixtureA() == mainSensor && contact.getFixtureB() == b.getUserData()) {
                        mainCollision = false;
                    }
                    if (contact.getFixtureA() == leftSensor && contact.getFixtureB() == b.getUserData()) {
                        leftCollision = false;
                    }
                    if (contact.getFixtureA() == rightSensor && contact.getFixtureB() == b.getUserData()) {
                        rightCollision = false;
                    }
                    if (contact.getFixtureA() == bottomSensor && contact.getFixtureB() == b.getUserData()) {
                        bottomCollision = false;
                        marioState = State.jumping;
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    public void updateSprite(){
        marioSprite.setPosition((marioBody.getWorldCenter().x * PPM) - marioSprite.getWidth() / 2,
                (marioBody.getWorldCenter().y * PPM ) - marioSprite.getHeight() / 2);
    }

    public void update(float delta) {

        if (mainCollision && !bottomCollision && marioState != State.spawn && marioDir == Direction.right
                && marioBody.getLinearVelocity().isZero()) {
            marioBody.setLinearVelocity(1.2f, 0f);
        }
        if (mainCollision && !bottomCollision && marioState != State.spawn && marioDir == Direction.left
                && marioBody.getLinearVelocity().isZero()) {
            marioBody.setLinearVelocity(-1.2f, 0f);
        }
        if (marioBody.getLinearVelocity().isZero() && marioState != State.spawn && marioState != State.dead) {
            marioState = State.idle;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !rightCollision) {
            marioDir = Direction.right;
            if (marioState == State.jumping) {
                marioBody.applyForceToCenter(250f, 0f, true);
            } else {
                marioState = State.walking;
                marioBody.setLinearVelocity(1.2f, 0f);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !leftCollision) {
            marioDir = Direction.left;
            if (marioState == State.jumping) {
                marioBody.applyForceToCenter(-250f, 0f, true);

            } else {
                marioState = State.walking;
                marioBody.setLinearVelocity(-1.2f, 0f);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && marioState != State.jumping) {
            marioBody.applyLinearImpulse(new Vector2(0, 500f), marioBody.getWorldCenter(), true);
            marioState = State.jumping;
        }
    }

    public void setPlatformEntities(ArrayList<Body> groundList, ArrayList<Body> platformList) {
        ground = groundList;
        platforms = platformList;
    }

    public void render(SpriteBatch batch) {


        elapsedTime += Gdx.graphics.getDeltaTime();

        refreshAnimation();

        updateSprite();
        batch.draw(currAnimation.getKeyFrame(elapsedTime, playAnimation), marioSprite.getX(), marioSprite.getY());
        //batch.draw(marioSprite, Math.round(marioSprite.getX()),Math.round(marioSprite.getY()) );
       // marioSprite.draw(batch);

    }

    public void setLeftCollision(boolean status){
        leftCollision = status;
    }

    public void setRightCollision(boolean status){
        rightCollision = status;
    }

    public void setBottomCollision(boolean status){
        bottomCollision = status;
    }


    public void dispose(){
        atlas.dispose();
        assetManager.dispose();

    }

    public Body getMarioBody() {

        return marioBody;
    }
}
