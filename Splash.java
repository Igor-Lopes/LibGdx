package com.mygdx.alienswarm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Splash implements Screen {
	private static final int CUR_SPEED = 5;
	private Texture texture = new Texture(Gdx.files.internal("bkg_menu.png"));
	private Texture alien_texture = new Texture(Gdx.files.internal("alien.png"));
	private Sprite alien = new Sprite(alien_texture);
	private Image splashImage = new Image(texture);
	private Stage stage = new Stage();
	private SpriteBatch batch = new SpriteBatch();
	private Texture spacemarine_texture = new Texture(
			Gdx.files.internal("spacemarine.png"));
	private Sprite spacemarine = new Sprite(spacemarine_texture);
	private World world = new World(new Vector2(0, 0), true);
	private float alienrotation;
	private Body Marinebody;
	private BodyDef MarinebodyDef = new BodyDef();
	private PolygonShape Marineshape = new PolygonShape();
	private FixtureDef MarinefixtureDef = new FixtureDef();

	private Body Alienbody;
	private int AlienSpeed = 0;
	private BodyDef AlienbodyDef = new BodyDef();
	private PolygonShape Alienshape = new PolygonShape();
	private FixtureDef AlienfixtureDef = new FixtureDef();
	Vector2 vAlien = new Vector2();

	Splash() {
		spacemarine.setPosition(100, 100);
		alien.setPosition(400, 400);
		AlienbodyDef.type = BodyDef.BodyType.KinematicBody;
		AlienbodyDef.position.set(alien.getX(), alien.getY());
		Alienbody = world.createBody(AlienbodyDef);
		Alienshape.setAsBox(alien.getWidth() / 2, alien.getHeight() / 2);
		AlienfixtureDef.shape = Alienshape;
		AlienfixtureDef.density = 1f;

		MarinebodyDef.type = BodyDef.BodyType.DynamicBody;
		MarinebodyDef.position.set(spacemarine.getX(), spacemarine.getY());
		Marinebody = world.createBody(MarinebodyDef);
		Marineshape.setAsBox(spacemarine.getWidth() / 2,
				spacemarine.getHeight() / 2);
		MarinefixtureDef.shape = Marineshape;
		MarinefixtureDef.density = 1f;
	}

	public void moveAlien() {
		float mX = 0;
		float mY = 0;
		int velocity = 50;
		vAlien = new Vector2(-1 * (float) Math.sin(Alienbody.getAngle()) * velocity,
				(float) Math.cos(Alienbody.getAngle() * velocity));
		
		mX = (float) Math.cos(Math.toRadians(spacemarine.getRotation()));
		mY = (float) Math.sin(Math.toRadians(spacemarine.getRotation()));
		
		vAlien.x = mX;
		vAlien.y = mY;
		if (vAlien.len() > 0) {
			vAlien = vAlien.nor();
		}
		vAlien.x = vAlien.x * velocity;
		vAlien.y = vAlien.y * velocity;
		vAlien.x += vAlien.x * Gdx.graphics.getDeltaTime();
		vAlien.y += vAlien.x * Gdx.graphics.getDeltaTime();
	}

	public float rotateMarine() {
		float angle = 0;
		float mouseX = 0;
		float mouseY = 0;
		mouseX = Gdx.input.getX();
		mouseY = 677 - Gdx.input.getY();
		angle = (float) Math.toDegrees(Math.atan2(mouseX - spacemarine.getX(),
				mouseY - spacemarine.getY()));
		if (angle < 0)
			angle += 360;
		spacemarine.setRotation(angle * -1);
		return angle;
	}

	public float rotateAlien(Sprite s, float posX, float posY) {
		float angle = 0;
		float mouseX = 0;
		float mouseY = 0;
		mouseX = posX;
		mouseY = posY;
		angle = (float) Math.toDegrees(Math.atan2(mouseX - s.getX(), mouseY - s.getY()));
		if (angle < 0)
			angle += 360;
		s.setRotation(angle * -1);
		return angle;
	}

	@Override
	public void render(float delta) {
		float angle;
		moveAlien();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		angle = rotateMarine();
		rotateAlien(alien, spacemarine.getX(),
				spacemarine.getY());
		if (Gdx.input.isKeyPressed(Input.Keys.W) == true
				&& spacemarine.getY() < 560) {
			Marinebody.setTransform(spacemarine.getX(), spacemarine.getY() + 4,
					angle);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S) == true
				&& spacemarine.getY() > 0) {
			Marinebody.setTransform(spacemarine.getX(), spacemarine.getY() - 4,
					angle);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D) == true
				&& spacemarine.getX() < 920) {
			Marinebody.setTransform(spacemarine.getX() + 4, spacemarine.getY(),
					angle);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A) == true
				& spacemarine.getX() > 8) {
			Marinebody.setTransform(spacemarine.getX() - 4, spacemarine.getY(),
					angle);
		}
		alien.setPosition(Alienbody.getPosition().x, Alienbody.getPosition().y);
		spacemarine.setPosition(Marinebody.getPosition().x, Marinebody.getPosition().y);
		batch.begin();
		spacemarine.draw(batch);
		alien.draw(batch);
		batch.end();
		// stage.act();
		// stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		stage.addActor(splashImage);

		splashImage.addAction(Actions.sequence(Actions.alpha(0),
				Actions.fadeIn(0.5f), Actions.delay(2),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						((Game) Gdx.app.getApplicationListener())
								.setScreen(new Splash());
					}
				})));
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		texture.dispose();
		stage.dispose();
	}
}
