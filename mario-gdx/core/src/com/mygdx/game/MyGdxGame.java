package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


public class MyGdxGame extends ApplicationAdapter {
    // Screen and world variables
    World world;
    SpriteBatch batch;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    MapLayer layer;
    MapObjects objects;
    OrthographicCamera camera;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    Music music;
    BitmapFont font;
    float h;
    float w;
    final float pixelsToMeters = 100f;
    float stateTime;
    // Mario's variables
    Sprite marioSprite;
    Texture marioTexture;
    TextureRegion [] animationFrame;
    Animation walkingLeft;
    Animation walkingRight;
    Animation currAnimation;
    boolean play;
    float elapsedTime;
    Body marioBody;
    enum State {spawn, dead, idle, walking, jumping}
    enum Direction {left, right}
    State marioState;
    Direction marioDir;
    boolean moveToRight;
    boolean moveToLeft;
  //  boolean isGrounded;
    // Other Variables
    ArrayList<Body> falls;
    ArrayList<Body> platforms;
    ArrayList<Body> bodyTrash;
    ArrayList<Body> ground;
    Vector2 test = new Vector2();
    boolean main = true;
    boolean bottom = false;
    @Override
    public void create() {
        //Initializing World and screen variables
        world = new World(new Vector2(0, -9.8f), true);
        batch = new SpriteBatch();
        w = Gdx.graphics.getWidth(); // width of the screen
        h = Gdx.graphics.getHeight();// height of the screen
        tiledMap = new TmxMapLoader().load("world_new.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        camera = new OrthographicCamera();// camera
        camera.setToOrtho(false, w, h);
        camera.update();// update camera
        debugRenderer = new Box2DDebugRenderer();
        music = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3"));
        music.setVolume(0.2f);
        music.setLooping(true);
        //music.play();
        font = new BitmapFont();
        font.setColor(Color.ORANGE);
        stateTime = 0;
        //Initializing Mario's variables
        marioTexture = new Texture ("mario_sheet.png");
        TextureRegion [][] tmpFrames = TextureRegion.split(marioTexture, marioTexture.getWidth()/3,
                marioTexture.getHeight()/2);
        animationFrame = new TextureRegion[3];

        int index = 0;
        for(int i=0; i < 1; i++){
            for(int j=0; j < 3; j++) {
                animationFrame[index++] = tmpFrames[i][j];
            }
        }

        marioSprite = new Sprite(tmpFrames[0][0]);
        marioSprite.setPosition(100, 225);
        walkingRight = new Animation(1/10f, animationFrame);

        animationFrame = new TextureRegion[3];

        index = 0;
        for(int i=1; i < 2; i++){
            for(int j=0; j < 3; j++) {
                animationFrame[index++] = tmpFrames[i][j];
            }
        }

        walkingLeft = new Animation(1/10f, animationFrame);

        currAnimation = walkingRight;
        play = true;
        marioState = State.idle;
        marioDir = Direction.right;
        moveToRight = true;
        moveToLeft = true;

        PolygonShape shape = new PolygonShape();
        BodyDef bodyDefMario = new BodyDef();
        bodyDefMario.type = BodyDef.BodyType.DynamicBody;

        bodyDefMario.position.set((marioSprite.getX() + marioSprite.getWidth() / 2) / pixelsToMeters,
                (marioSprite.getY() + marioSprite.getHeight() / 2) / pixelsToMeters);
        marioBody = world.createBody(bodyDefMario);

        shape.setAsBox(marioSprite.getWidth() / 2 / pixelsToMeters, marioSprite.getHeight() / 2 / pixelsToMeters);
        FixtureDef fixtureMario = new FixtureDef();
        fixtureMario.shape = shape;
        fixtureMario.density = 700f;
        fixtureMario.restitution = 0;
        fixtureMario.friction = 1;
        Fixture [] sides = new Fixture[4];
        sides[0] = marioBody.createFixture(fixtureMario);
        marioBody.setFixedRotation(true);

        //Right Side Sensor
        fixtureMario = new FixtureDef();
        shape = new PolygonShape();
        //shape.setAsBox(marioSprite.getWidth() / 16 / pixelsToMeters,marioSprite.getHeight() * 0.4f / pixelsToMeters,
        //   new Vector2(0.20f,0), 0);

        shape.setAsBox(marioSprite.getWidth() / 16 / pixelsToMeters,marioSprite.getHeight() * 0.45f / pixelsToMeters,
                new Vector2(0.20f,0), 0);
        fixtureMario.isSensor = true;
        fixtureMario.shape = shape;


        sides[1] = marioBody.createFixture(fixtureMario);
        //Left Side Sensor
        fixtureMario = new FixtureDef();
        shape = new PolygonShape();
        //shape.setAsBox(marioSprite.getWidth() / 16 / pixelsToMeters,marioSprite.getHeight() * 0.4f / pixelsToMeters,
        //   new Vector2(0.20f,0), 0);
        shape.setAsBox(marioSprite.getWidth() / 16 / pixelsToMeters,marioSprite.getHeight() * 0.45f / pixelsToMeters,
                new Vector2(-0.20f,0), 0);
        fixtureMario.isSensor = true;
        fixtureMario.shape = shape;
        sides[2] = marioBody.createFixture(fixtureMario);
        // Bottom
        fixtureMario = new FixtureDef();
        shape = new PolygonShape();
        //shape.setAsBox(marioSprite.getWidth() / 16 / pixelsToMeters,marioSprite.getHeight() * 0.4f / pixelsToMeters,
        //   new Vector2(0.20f,0), 0);
        shape.setAsBox(marioSprite.getWidth() /4f / pixelsToMeters,marioSprite.getHeight() /4 / pixelsToMeters,
                new Vector2(0,-0.20f), 0);
        fixtureMario.isSensor = true;
        fixtureMario.shape = shape;
        sides[3] = marioBody.createFixture(fixtureMario);

        marioBody.setUserData(sides);

        //Other
        falls = new ArrayList<Body>();
        platforms = new ArrayList<Body>();
        bodyTrash = new ArrayList<Body>();
        ground = new ArrayList<Body>();
        createGround();
        createFalls();
        createPlatforms();
        setCollisions();
    }

    public void createGround() { // Creates Ground bodies
        layer = tiledMap.getLayers().get("ground");
        objects = layer.getObjects();
        Body body;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        for (int i = 0; i < objects.getCount(); i++) {
            RectangleMapObject rmp = (RectangleMapObject) objects.get(i);
            Rectangle r = rmp.getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            // Box2D uses x,y to place it in the center, so it needs to add the
            // w and h to x,y
            // and divide by 2 so it can center it properly
            bodyDef.position.set((r.x + r.width * 0.5f) / pixelsToMeters, (r.y + r.height * 0.5f) / pixelsToMeters);
            body = world.createBody(bodyDef);

            shape.setAsBox(r.getWidth() * 0.5f / pixelsToMeters, r.getHeight() * 0.5f / pixelsToMeters);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1;
            Fixture f = body.createFixture(fixtureDef);

            body.setUserData(f);
            ground.add(body);
        }
    }

    public void createFalls() { // Createsfalls
        layer = tiledMap.getLayers().get("fall");
        objects = layer.getObjects();
        Body body;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        for (int i = 0; i < objects.getCount(); i++) {
            RectangleMapObject rmp = (RectangleMapObject) objects.get(i);
            Rectangle r = rmp.getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((r.x + r.width * 0.5f) / pixelsToMeters, (r.y + r.height * 0.5f) / pixelsToMeters);
            body = world.createBody(bodyDef);
            shape.setAsBox(r.getWidth() * 0.5f / pixelsToMeters, r.getHeight() * 0.5f / pixelsToMeters);
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            body.createFixture(fixtureDef);
            falls.add(body);
        }
    }

    public void createPlatforms() { // Creates Platforms
        layer = tiledMap.getLayers().get("platforms");
        objects = layer.getObjects();
        Body body;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        for (int i = 0; i < objects.getCount(); i++) {
            RectangleMapObject rmp = (RectangleMapObject) objects.get(i);
            Rectangle r = rmp.getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((r.x + r.width * 0.5f) / pixelsToMeters, (r.y + r.height * 0.5f) / pixelsToMeters);
            body = world.createBody(bodyDef);
            shape.setAsBox(r.getWidth() * 0.5f / pixelsToMeters, r.getHeight() * 0.5f / pixelsToMeters);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1;
            Fixture f = body.createFixture(fixtureDef);
            body.setUserData(f);
            platforms.add(body);
        }
    }

    public void setCollisions() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture [] sides = (Fixture[]) marioBody.getUserData();
                for (Body b : platforms) {
                    if(contact.getFixtureA() == sides[0] && contact.getFixtureB() == b.getUserData()){
                        main = true;
                    }
                    if(contact.getFixtureA() == sides[3] && contact.getFixtureB() == b.getUserData()){
                        bottom = true;
                        marioState = State.idle;
                    }
                    if(contact.getFixtureA() == sides[1] && contact.getFixtureB() == b.getUserData()){
                        moveToRight = false;
                    }
                    if(contact.getFixtureA() == sides[2] && contact.getFixtureB() == b.getUserData()){
                        moveToLeft = false;
                    }
                }
                for (Body b : ground) {
                    if(contact.getFixtureA() == sides[0] && contact.getFixtureB() == b.getUserData()){
                        main = true;
                    }
                    if(contact.getFixtureA() == sides[3] && contact.getFixtureB() == b.getUserData()){
                        bottom = true;
                        marioState = State.idle;
                    }
                    if(contact.getFixtureA() == sides[1] && contact.getFixtureB() == b.getUserData()){
                        moveToRight = false;
                    }
                    if(contact.getFixtureA() == sides[2] && contact.getFixtureB() == b.getUserData()){
                        moveToLeft = false;
                    }
                }
            }
            @Override
            public void endContact(Contact contact) {
                Fixture [] sides = (Fixture[]) marioBody.getUserData();

                for (Body b : ground) {
                    if(contact.getFixtureA() == sides[0] && contact.getFixtureB() == b.getUserData()){
                        main = false;
                    }
                    if(contact.getFixtureA() == sides[3] && contact.getFixtureB() == b.getUserData() && moveToLeft && moveToRight){
                        bottom = false;
                        marioState = State.jumping;
                    }
                    if(contact.getFixtureA() == sides[1] && contact.getFixtureB() == b.getUserData()){
                        moveToRight = true;
                    }
                    if(contact.getFixtureA() == sides[2] && contact.getFixtureB() == b.getUserData()){
                        moveToLeft = true;
                    }
                }
                for (Body b : platforms) {
                    if(contact.getFixtureA() == sides[0] && contact.getFixtureB() == b.getUserData()){
                        main = false;
                    }
                    if(contact.getFixtureA() == sides[3] && contact.getFixtureB() == b.getUserData() && moveToLeft && moveToRight){
                        bottom = false;
                        marioState = State.jumping;
                    }
                    if(contact.getFixtureA() == sides[1] && contact.getFixtureB() == b.getUserData()){
                        moveToRight = true;
                    }
                    if(contact.getFixtureA() == sides[2] && contact.getFixtureB() == b.getUserData()){
                        moveToLeft = true;
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

    public void keyBoard() {
        if(main && !bottom && marioState != State.spawn && marioDir == Direction.right
                && marioBody.getLinearVelocity().isZero()){
            marioBody.setLinearVelocity(1.2f, 0f);
        }
        if(main && !bottom && marioState != State.spawn  && marioDir == Direction.left
                && marioBody.getLinearVelocity().isZero()){
            marioBody.setLinearVelocity(-1.2f, 0f);
        }

        if(marioBody.getLinearVelocity().isZero() && marioState != State.spawn  && marioState != State.dead){
            marioState = State.idle;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && moveToLeft) {
            marioDir = Direction.left;
            if (marioState == State.jumping) {
                marioBody.applyForceToCenter(-250f, 0f, true);

            } else {
                if(bottom)
                marioState = State.walking;
                marioBody.setLinearVelocity(-1.2f, 0f);
               //  marioBody.applyLinearImpulse(new Vector2(2f, 0f), marioBody.getWorldCenter(), true);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && moveToRight) {
            marioDir = Direction.right;
            if (marioState == State.jumping) {
                marioBody.applyForceToCenter(250f , 0f, true);

            } else {
                if(bottom)
                marioState = State.walking;
                marioBody.setLinearVelocity(1.2f, 0f);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && marioState != State.jumping) {
            marioBody.applyLinearImpulse(new Vector2(0, 500f), marioBody.getWorldCenter(), true);
            marioState = State.jumping;
        }
    }

    public void refreshAnimation(){
        if(marioState == State.walking && marioDir == Direction.left){

            currAnimation.setFrameDuration(1/10f);
            play = true;
            currAnimation = walkingLeft;
        }
        if(marioState == State.walking && marioDir == Direction.right){
            currAnimation.setFrameDuration(1/10f);
            play = true;
            currAnimation = walkingRight;
        }
        if(marioState == State.idle && marioDir == Direction.left){
            currAnimation = walkingLeft;
            play = false;
        }
        if(marioState == State.idle && marioDir == Direction.right){
            currAnimation = walkingRight;
            play = false;
        }
        if(marioState == State.jumping && marioDir == Direction.left){
            currAnimation = walkingLeft;
            play = true;
            currAnimation.setFrameDuration(0f);
        }
        if(marioState == State.jumping && marioDir == Direction.right){
            currAnimation = walkingRight;
            play = true;
            currAnimation.setFrameDuration(0f);
        }

    }

    @Override
    public void render() {
        camera.update();

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        elapsedTime += Gdx.graphics.getDeltaTime();

        keyBoard();
        refreshAnimation();
        debug();

        debugMatrix = batch.getProjectionMatrix().cpy().scale(pixelsToMeters, pixelsToMeters, 0);
        test = new Vector2((marioSprite.getX()/ pixelsToMeters) + marioSprite.getWidth() * 2,
                (marioSprite.getY() + marioSprite.getHeight() * 2));

        marioSprite.setPosition((marioBody.getPosition().x * pixelsToMeters) - marioSprite.getWidth() / 2,
                (marioBody.getPosition().y * pixelsToMeters) - marioSprite.getHeight() / 2);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.begin();
        batch.draw(currAnimation.getKeyFrame(elapsedTime,play), marioSprite.getX(), marioSprite.getY());

        font.draw(batch, "Debug: ", 0, 0);
        batch.end();

        debugRenderer.render(world, debugMatrix);
    }

    public void debug() {
        // System.out.println("State: " + marioState + " Direction: " + marioDir);
    }

}
