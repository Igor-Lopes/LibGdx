package com.mygdx.game.com.mygdx.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private Body marioBody;
    private ArrayList<Body> platforms;
    private ArrayList<Body> falls;
    private ArrayList<Body> ground;
    private Fixture mainSensor;
    private Fixture leftSensor;
    private Fixture rightSensor;
    private Fixture bottomSensor;

    private boolean mainCollision;
    private boolean leftCollision;
    private boolean rightCollision;
    private boolean bottomCollision;

    //Animation Variables
    private Animation walkingLeft;
    private Animation walkingRight;
    private Animation currAnimation;
    private Texture marioTexture;
    private final String TEXTURE_PATH = "mario_sheet.png";
    private TextureRegion[] animationFrame;
    private boolean play;

    //Mario's States
    private enum State {
        spawn, dead, idle, walking, jumping
    }

    private enum Direction {left, right}

    private State marioState;
    private Direction marioDir;

    private final float PPM = 100f;
    private float elapsedTime;

    public Mario(Vector2 spawnLocation, World w) {
        world = w;
        marioPosition = spawnLocation;

        setAnimations();
        refreshAnimation();
        play = true;
        initStates();

        marioBody = CreateBodies.createBody(spawnLocation, marioSprite.getWidth(), marioSprite.getHeight(), 700f, 0, 1,
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
        marioTexture = new Texture("mario_sheet.png");
        TextureRegion[][] tmpFrames = TextureRegion.split(marioTexture, marioTexture.getWidth() / 3,
                marioTexture.getHeight() / 2);
        animationFrame = new TextureRegion[3];

        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 3; j++) {
                animationFrame[index++] = tmpFrames[i][j];
            }
        }

        marioSprite = new Sprite(tmpFrames[0][0]);
        marioSprite.setPosition(100, 225);
        walkingRight = new Animation(1 / 10f, animationFrame);

        animationFrame = new TextureRegion[3];

        index = 0;
        for (int i = 1; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                animationFrame[index++] = tmpFrames[i][j];
            }
        }

        walkingLeft = new Animation(1 / 10f, animationFrame);

        currAnimation = walkingRight;
        play = true;
        currAnimation = walkingRight;

    }

    public void refreshAnimation() {
        if (marioState == State.walking && marioDir == Direction.left) {

            currAnimation.setFrameDuration(1 / 10f);
            play = true;
            currAnimation = walkingLeft;
        }
        if (marioState == State.walking && marioDir == Direction.right) {
            currAnimation.setFrameDuration(1 / 10f);
            play = true;
            currAnimation = walkingRight;
        }
        if (marioState == State.idle && marioDir == Direction.left) {
            currAnimation = walkingLeft;
            play = false;
        }
        if (marioState == State.idle && marioDir == Direction.right) {
            currAnimation = walkingRight;
            play = false;
        }
        if (marioState == State.jumping && marioDir == Direction.left) {
            currAnimation = walkingLeft;
            play = true;
            currAnimation.setFrameDuration(0f);
        }
        if (marioState == State.jumping && marioDir == Direction.right) {
            currAnimation = walkingRight;
            play = true;
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

    public void update() {
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

        marioSprite.setPosition((marioBody.getPosition().x * PPM) - marioSprite.getWidth() / 2,
                (marioBody.getPosition().y * PPM) - marioSprite.getHeight() / 2);
        batch.draw(currAnimation.getKeyFrame(elapsedTime, play), marioSprite.getX(), marioSprite.getY());

    }

    public Body getMarioBody() {

        return marioBody;
    }

    public Fixture getMainSensor() {

        return mainSensor;
    }

    public Fixture getLeftSensor() {

        return leftSensor;
    }

    public Fixture getRightSensor() {

        return rightSensor;
    }

    public Fixture getBottomSensor() {

        return bottomSensor;
    }

    public Animation getAnimation() {

        return currAnimation;
    }

    public Vector2 getMarioPos() {

        return marioPosition;
    }

}
