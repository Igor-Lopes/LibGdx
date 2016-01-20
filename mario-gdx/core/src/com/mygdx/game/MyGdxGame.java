package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MyGdxGame extends ApplicationAdapter {
	World world;
	SpriteBatch batch;
	Texture img;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	MapLayer  layer;
	MapObjects objects;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	BitmapFont font;
	float h;
	float w;
	final float pixelsToMeters = 100f;
	//Mario
	Sprite marioSprite;
	Texture marioTexture;
	Body marioBody;
	
	
	
	ArrayList<Rectangle> ground;
	
	@Override
	public void create () {
		world = new World(new Vector2(0, -9.8f),true);
		batch = new SpriteBatch();
		w = Gdx.graphics.getWidth(); //width of the screen
        h = Gdx.graphics.getHeight();//height of the screen
        camera = new OrthographicCamera();//camera
        camera.setToOrtho(false, w, h);
        camera.update();//update camera
        debugRenderer = new Box2DDebugRenderer();
        
      
        PolygonShape shape = new PolygonShape();
        marioTexture = new Texture("mario.png");
        marioSprite = new Sprite(marioTexture);
        marioSprite.setPosition(0, 225);
        BodyDef bodyDefMario = new BodyDef();
        bodyDefMario.type = BodyDef.BodyType.DynamicBody;
       // bodyDefMario.position.set(marioSprite.getX() / pixelsToMeters, marioSprite.getY() / pixelsToMeters);
        bodyDefMario.position.set((marioSprite.getX() + marioSprite.getWidth()/2) / 
        		pixelsToMeters, 
   (marioSprite.getY() + marioSprite.getHeight()/2) / pixelsToMeters);
        marioBody = world.createBody(bodyDefMario);
        shape.setAsBox(marioSprite.getWidth()/2 / pixelsToMeters, marioSprite.getHeight()
                /2 / pixelsToMeters);
        FixtureDef fixtureMario = new FixtureDef();
        fixtureMario.shape = shape;
        fixtureMario.density = 100f;
        fixtureMario.restitution = 0;
        marioBody.createFixture(fixtureMario);
        
		
		tiledMap = new TmxMapLoader().load("world.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		layer = tiledMap.getLayers().get("ground");
        objects = layer.getObjects();	
       
        
       createBodies();
        
	}
	
	public void createBodies(){
		Body body;
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		for(int i = 0; i < objects.getCount(); i++){
			RectangleMapObject rmp = (RectangleMapObject) objects.get(i);
			Rectangle r = rmp.getRectangle();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			//Box2D uses x,y to place it in the center, so it needs to add the w and h to x,y 
			//and divide by 2 so it can center it properly
			bodyDef.position.set(
				    (r.x + r.width * 0.5f) / pixelsToMeters,
				    (r.y + r.height * 0.5f) / pixelsToMeters
				  );
			body = world.createBody(bodyDef);
			shape.setAsBox(r.getWidth() * 0.5f /pixelsToMeters , r.getHeight() * 0.5f /pixelsToMeters );
			
			fixtureDef.shape = shape;
			body.createFixture(fixtureDef);
			
		}	
	}
	
	public void keyBoard(){
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			marioBody.setLinearVelocity(-1f, 0f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			marioBody.setLinearVelocity(1f, 0f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            //body.applyForceToCenter(0f,16000,true);
        	marioBody.applyLinearImpulse(new Vector2(0,50),marioBody.getPosition(), true);
		}
	}

	@Override
	public void render () {
		System.out.println(Gdx.input.getX());

		keyBoard();
		//debug();
		camera.update();
		world.step(1f/60f, 6, 2);
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(pixelsToMeters,
        		pixelsToMeters, 0);
        marioSprite.setPosition((marioBody.getPosition().x * pixelsToMeters) - marioSprite.
                getWidth()/2 ,
        (marioBody.getPosition().y * pixelsToMeters) - marioSprite.getHeight()/2 )
;
		batch.begin();
		batch.draw(marioSprite, marioSprite.getX(), marioSprite.getY());
		batch.end();
		debugRenderer.render(world, debugMatrix);
	}
	
	public void debug(){
		System.out.println("Mario's mass [" + marioBody.getMass() + "kg]");
	}
}
