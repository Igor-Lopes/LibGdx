package com.mygdx.game.com.mygdx.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by igor on 1/27/16.
 */
public class Goomba {

    private Texture goombaTexture;
    private Sprite goombaSprite;
    private Body goombaBody;
    private World world;
    private Body marioBody;

    private enum State{walkingLeft, walkingRight};

    private final float PPM  = 100f;


    public Goomba(Vector2 spawnLocation, World w, Body mario){
        goombaTexture = new Texture("gmb.png");
        goombaSprite = new Sprite(goombaTexture);
        goombaSprite.setPosition(spawnLocation.x, spawnLocation.y);
        world = w;
        marioBody = mario;

        goombaBody = CreateBodies.createBody(spawnLocation, goombaSprite.getWidth(), goombaSprite.getHeight() , 300f, 0, 1,
                BodyDef.BodyType.DynamicBody, world, true);


    }

    public void update(){
        goombaSprite.setPosition((goombaBody.getWorldCenter().x * PPM) - goombaSprite.getWidth() / 2,
                (goombaBody.getPosition().y * PPM ) - goombaSprite.getHeight() / 2);
    }

    public void render(SpriteBatch batch){
        update();
        batch.draw(goombaSprite, goombaSprite.getX(), goombaSprite.getY());

    }


    public void dispose(){

    }
}
