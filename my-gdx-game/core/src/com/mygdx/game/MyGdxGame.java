/* @author: Igor Lopes
 * January 19th, 2016
 * Basic physics in LibGdx
 */

package com.mygdx.game;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	
    SpriteBatch batch;
    Sprite sprite;
    Texture img;
    World world;
    Body body;
    Body bodyEdgeScreen;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    BitmapFont font;
    // Pipe for Collision
    Sprite pipe;
    Texture textpipe;
    Body bodypipe;
    

    float torque = 0.0f;
    boolean drawSprite = true;

    final float PIXELS_TO_METERS = 100f; // 1 meter = 100 pixels

    @Override
    public void create() {

        batch = new SpriteBatch();
        img = new Texture("mario.png");
        sprite = new Sprite(img);
        sprite.setPosition(-100,100);
        world = new World(new Vector2(0, -9.8f),true); // Earth Gravity
        //Creation of the main body
        BodyDef bodyDef = new BodyDef(); // Object type
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Dynamic
        /* To box2d:
         *
           bodyDef.position.set((sprite.getX() + sprite.getWidth()/2) / 
                             PIXELS_TO_METERS, 
                (sprite.getY() + sprite.getHeight()/2) / PIXELS_TO_METERS);
         * From box2d:
         * 
           sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - sprite.
                           getWidth()/2 , 
                (body.getPosition().y * PIXELS_TO_METERS) -sprite.getHeight()/2 )
                 ;
         *
         */
        bodyDef.position.set((sprite.getX()) /
                        PIXELS_TO_METERS,
                (sprite.getY()) / PIXELS_TO_METERS);

        body = world.createBody(bodyDef); // Adds the body in the world

        PolygonShape shape = new PolygonShape();
        // Box for the body. It can be seen in the debug
        // Larger area implies in a bigger mass
        shape.setAsBox(sprite.getWidth()/2 / PIXELS_TO_METERS, sprite.getHeight()
                /2 / PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef(); // Fixture: size, shape, and material properties of an object in the physics scene 
        fixtureDef.shape = shape;
        fixtureDef.density = 200f; // Density: kg/ms2 is 1f, Density - how heavy it is in relation to its area
        //fixtureDef.restitution = 0.5f; // Restitution - how bouncy the fixture is
        fixtureDef.restitution = 0.0f;
        body.createFixture(fixtureDef); //Adds Fixture to the body
       // shape.dispose();
        
        //Creation of the pipe
        textpipe = new Texture("Pipe.png");
        pipe = new Sprite(textpipe);
        pipe.setPosition(0, -215);
        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.type = BodyDef.BodyType.StaticBody;
        bodyDef3.position.set((pipe.getX() + 50) /
                PIXELS_TO_METERS,
        (pipe.getY()) / PIXELS_TO_METERS );
        bodypipe = world.createBody(bodyDef3);
        shape =  new PolygonShape();
        shape.setAsBox(pipe.getWidth() / PIXELS_TO_METERS /2 , pipe.getHeight()
                 / PIXELS_TO_METERS );
        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = shape;
        fixtureDef3.density = 10000f;
        fixtureDef3.restitution = 0f;
        bodypipe.createFixture(fixtureDef3);
        shape.dispose();
        
        //Creation of the edge on the screen
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody; //Static 
        float w = Gdx.graphics.getWidth()/PIXELS_TO_METERS;
        // Set the height to just 50 pixels above the bottom of the screen so we can see the edge in the
        // debug renderer
        float h = Gdx.graphics.getHeight()/PIXELS_TO_METERS- 50/PIXELS_TO_METERS; // -50 to be visible here
        //bodyDef2.position.set(0,
//                h-10/PIXELS_TO_METERS);
        System.out.println(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
        bodyDef2.position.set(0,0);
        FixtureDef fixtureDef2 = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape(); // Creates an edge
        // Change the values for larger or shorter edge
        
        edgeShape.set(-w/2,-h/2,w/2,-h/2);
        fixtureDef2.shape = edgeShape;

        bodyEdgeScreen = world.createBody(bodyDef2);
        bodyEdgeScreen.createFixture(fixtureDef2);
        edgeShape.dispose();

        Gdx.input.setInputProcessor(this);      
        // For debug
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
        
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // Check to see if the collision is between the second sprite and the bottom of the screen
                // If so apply a random amount of upward force to both objects... just because
                if(contact.getFixtureA().getBody() == body && 
                		contact.getFixtureB().getBody() == bodypipe){
                	System.out.println("Collision with pipe");

                    
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

    private float elapsed = 0;
    @Override
    public void render() {
    	
    	//System.out.println("Body's mass: " + body.getMass() + " kg");
        camera.update();
        // Step the physics simulation forward at a rate of 60hz
        world.step(1f/60f, 6, 2);
        // Torque needs to be update each frame
        body.applyTorque(torque,true);
        // From Box2D
        sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - sprite.
                        getWidth()/2 ,
                (body.getPosition().y * PIXELS_TO_METERS) -sprite.getHeight()/2 )
        ;
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                PIXELS_TO_METERS, 0);
        batch.begin();

        if(drawSprite)
            batch.draw(sprite, sprite.getX(), sprite.getY(),sprite.getOriginX(),
                    sprite.getOriginY(),
                    sprite.getWidth(),sprite.getHeight(),sprite.getScaleX(),sprite.
                            getScaleY(),sprite.getRotation());
        batch.draw(pipe, pipe.getX(), pipe.getY(),pipe.getOriginX(),
        		pipe.getOriginY(),
        		pipe.getWidth(),pipe.getHeight(),pipe.getScaleX(),pipe.
                        getScaleY(),pipe.getRotation());

        font.draw(batch,
                "Restitution: " + body.getFixtureList().first().getRestitution(),
                -Gdx.graphics.getWidth()/2,
               Gdx.graphics.getHeight()/2 );
        batch.end();

        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void dispose() {
        img.dispose();
        world.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {


        if(keycode == Input.Keys.RIGHT)
            body.setLinearVelocity(1f, 0f);
        if(keycode == Input.Keys.LEFT)
            body.setLinearVelocity(-1f,0f);

        if(keycode == Input.Keys.UP)
            //body.applyForceToCenter(0f,16000,true);
        	body.applyLinearImpulse(new Vector2(0,600),body.getPosition(), true);
        if(keycode == Input.Keys.DOWN)
            body.applyForceToCenter(0f, -10f, true);

        // On brackets ( [ ] ) apply torque, either clock or counterclockwise
        if(keycode == Input.Keys.RIGHT_BRACKET)
            torque += 0.1f;
        if(keycode == Input.Keys.LEFT_BRACKET)
            torque -= 0.1f;

        // Remove the torque using backslash /
        if(keycode == Input.Keys.BACKSLASH)
            torque = 0.0f;

        // If user hits spacebar, reset everything back to normal
        if(keycode == Input.Keys.SPACE|| keycode == Input.Keys.NUM_2) {
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            torque = 0f;
            sprite.setPosition(0f,0f);
            body.setTransform(0f,0f,0f);
        }

        if(keycode == Input.Keys.COMMA) {
            body.getFixtureList().first().setRestitution(body.getFixtureList().first().getRestitution()-0.1f);
        }
        if(keycode == Input.Keys.PERIOD) {
            body.getFixtureList().first().setRestitution(body.getFixtureList().first().getRestitution()+0.1f);
        }
        if(keycode == Input.Keys.ESCAPE || keycode == Input.Keys.NUM_1)
            drawSprite = !drawSprite;

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    // On touch we apply force from the direction of the users touch.
    // This could result in the object "spinning"
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        body.applyForce(50f,50f,screenX,screenY,true);
        //body.applyTorque(0.4f,true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}