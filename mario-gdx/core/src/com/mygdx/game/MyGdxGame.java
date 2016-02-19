package com.mygdx.game;


import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
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
import com.mygdx.game.com.mygdx.Entities.Goomba;
import com.mygdx.game.com.mygdx.Entities.Mario;

public class MyGdxGame implements Screen {

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
    Goomba goomba;

    final MarioGame game;


    public MyGdxGame(final MarioGame g) {
        game = g;
        game.batch = new SpriteBatch();
        //Initializing World and screen variables
        world = new World(new Vector2(0, -9.8f), true);

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

       // music.play();
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
        //

     //   goomba = new Goomba(new Vector2(1172,255), world, marioBody);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 1, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(pixelsToMeters, pixelsToMeters, 0);
        game.batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        game.batch.begin();
        mario.update(delta);
        mario.render(game.batch);
        game.batch.end();
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose(){
        marioTexture.dispose();
        batch.dispose();
        music.dispose();
        mario.dispose();
    }
}
