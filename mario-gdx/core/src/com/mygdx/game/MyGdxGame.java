package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame extends ApplicationAdapter {
	// Screen and world variables
	World world;
	SpriteBatch batch;
	Texture img;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	MapLayer layer;
	MapObjects objects;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	BitmapFont font;
	float h;
	float w;
	final float pixelsToMeters = 100f;
	// Mario's variables
	Sprite marioSprite;
	Texture marioTexture;
	Body marioBody;
	String marioState;
	boolean onGround;
	// Other Variables
	ArrayList<Body> falls;
	ArrayList<Body> bodyTrash;
	ArrayList<Body> ground;

	@Override
	public void create() {
		world = new World(new Vector2(0, -9.8f), true);
		batch = new SpriteBatch();
		w = Gdx.graphics.getWidth(); // width of the screen
		h = Gdx.graphics.getHeight();// height of the screen
		camera = new OrthographicCamera();// camera
		camera.setToOrtho(false, w, h);
		camera.update();// update camera
		debugRenderer = new Box2DDebugRenderer();
		font = new BitmapFont();
		font.setColor(Color.ORANGE);
		marioState = "locked";
		onGround = false;

		PolygonShape shape = new PolygonShape();
		marioTexture = new Texture("marioIdle.png");
		marioSprite = new Sprite(marioTexture);
		marioSprite.setPosition(0, 225);
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
		marioBody.createFixture(fixtureMario);
		marioBody.setFixedRotation(true);
		tiledMap = new TmxMapLoader().load("world.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		falls = new ArrayList<Body>();
		bodyTrash = new ArrayList<Body>();
		ground = new ArrayList<Body>();
		createGround();
		createFalls();
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
			body.createFixture(fixtureDef);
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
			body.createFixture(fixtureDef);
			falls.add(body);

		}
	}

	public void setCollisions() {
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// Check to see if the collision is between the second sprite
				// and the bottom of the screen
				// If so apply a random amount of upward force to both
				// objects... just because
				for (Body b : falls) {
					if (contact.getFixtureA().getBody() == marioBody && contact.getFixtureB().getBody() == b) {
						bodyTrash.add(b);
						marioState = "locked";
					}
				}
				for (Body b : ground) {
					if (contact.getFixtureA().getBody() == marioBody && contact.getFixtureB().getBody() == b) {
						onGround = true;
						marioState = "idle";
					}
				}
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void keyBoard() {
		if(marioBody.getLinearVelocity().isZero() && marioState != "locked"){
        	marioState = "idle";
        }
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && marioState != "locked") {
			if (marioState == "jumping") {
				marioBody.applyForceToCenter(-250f, 0f, true);
			} else {
				marioState = "walking";
				marioBody.setLinearVelocity(-1.2f, 0f);
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && marioState != "locked") {
			if (marioState == "jumping") {
				marioBody.applyForceToCenter(250f, 0f, true);
			} else {
				marioState = "walking";
				marioBody.setLinearVelocity(1.2f, 0f);
			}
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && marioState != "locked" && marioState != "jumping") {			 
			if(marioState == "walking"){
				marioBody.applyForceToCenter(0f,28000f,true);
				marioState = "jumping";
				onGround = false;
			}
			if(marioState == "idle"){
				marioBody.applyLinearImpulse(new Vector2(0, 500f), marioBody.getPosition(), true);
				marioState = "jumping";
				onGround = false;
			}
		}
	}

	public void cleanBodies() {
		for (int i = 0; i < bodyTrash.size(); i++) {
			Body b = bodyTrash.remove(i);
			if (falls.contains(b)) {
				falls.remove(b);
			}
			if (!world.isLocked()) {
				world.destroyBody(b);
			}
		}
	}

	@Override
	public void render() {
		keyBoard();
		debug();
		camera.update();
		world.step(1f / 60f, 6, 2);
		cleanBodies();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		batch.setProjectionMatrix(camera.combined);
		debugMatrix = batch.getProjectionMatrix().cpy().scale(pixelsToMeters, pixelsToMeters, 0);
		marioSprite.setPosition((marioBody.getPosition().x * pixelsToMeters) - marioSprite.getWidth() / 2,
				(marioBody.getPosition().y * pixelsToMeters) - marioSprite.getHeight() / 2);
		batch.begin();
		batch.draw(marioSprite, marioSprite.getX(), marioSprite.getY());
		font.draw(batch, "Restitution: ", 5 / 2, 12 / 2);
		batch.end();
		debugRenderer.render(world, debugMatrix);
	}

	public void debug() {
		System.out.println("State: " + marioState);
		//System.out.println(marioBody.getLinearVelocity());
	}

}
