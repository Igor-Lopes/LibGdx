package com.mygdx.game;


import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.com.mygdx.Entities.CreateBodies;
import com.mygdx.game.com.mygdx.Entities.Mario;

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
    TextureRegion[] animationFrame;
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

    Mario mario;

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
        //Initializing Mario's variables
        mario = new Mario(new Vector2(100, 255), world);
        marioBody = mario.getMarioBody();

        //Platform Entities
        ground = new ArrayList<Body>();
        falls = new ArrayList<Body>();
        platforms = new ArrayList<Body>();
        bodyTrash = new ArrayList<Body>();
        ground = CreateBodies.createFromTiled("ground", tiledMap, world, BodyDef.BodyType.StaticBody, false);
        platforms = CreateBodies.createFromTiled("platforms", tiledMap, world, BodyDef.BodyType.StaticBody, false);
        falls = CreateBodies.createFromTiled("fall", tiledMap, world, BodyDef.BodyType.StaticBody, true);

        mario.setPlatformEntities(ground, platforms);
    }

    @Override
    public void render() {
        camera.update();

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        debugMatrix = batch.getProjectionMatrix().cpy().scale(pixelsToMeters, pixelsToMeters, 0);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        mario.update();
        batch.begin();
        mario.render(batch);
        batch.end();

        // debugRenderer.render(world, debugMatrix);
    }

}
