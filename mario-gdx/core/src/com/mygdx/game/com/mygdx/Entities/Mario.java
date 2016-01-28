package com.mygdx.game.com.mygdx.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by igor on 1/27/16.
 */
public class Mario {
    private Sprite marioSprite;
    private Body marioBody;
    private Fixture mainSensor;
    private Fixture leftSensor;
    private Fixture rightSensor;
    private Fixture bottomSensor;
    //Animation Variables
    private Animation walkLeft;
    private Animation walkRight;
    private Animation jump;
    private Animation idle;
    private Animation currAnimation;
    private Texture marioTexture;
    private TextureRegion [] animationFrame;
    //Mario's States
    private enum State {spawn, dead, idle, walking, jumping}
    private enum Direction {left, right}
    private State marioState;
    private Direction marioDir;



    public Mario(){
        setAnimations();
        setStates();

    }

    private void setAnimations(){
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
        walkRight = new Animation(1/10f, animationFrame);

        animationFrame = new TextureRegion[3];

        index = 0;
        for(int i=1; i < 2; i++){
            for(int j=0; j < 3; j++) {
                animationFrame[index++] = tmpFrames[i][j];
            }
        }
        walkLeft = new Animation(1/10f, animationFrame);

    }

    private void setStates(){
        marioState = State.idle;
        marioDir = Direction.right;
    }

    private Body createBody(Vector2 position, BodyDef.BodyType bType, World world){
        Body body;
        BodyDef bdef = new BodyDef();
        bdef.type = bType;
        bdef.position.set(position.x, position.y);
        body = world.createBody(bdef);
        return body;
    }

    private Fixture createFixture(Body b, float d, float r, float f ){
        FixtureDef fdef = new FixtureDef();
        fdef.density = d;
        fdef.restitution = r;
        fdef.friction = f;
        Fixture fixture = b.createFixture(fdef);

        return fixture;
    }

    private PolygonShape createShape(float w, float h){
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w,h);

        return shape;
    }

    private void setSpritePos(float x, float y) { marioSprite.setPosition(x,y); }

    public Animation getAnimation(){ return currAnimation; }

    public float getSpriteX(){ return marioSprite.getX(); }

    public float getSpriteY(){ return marioSprite.getY(); }

}
