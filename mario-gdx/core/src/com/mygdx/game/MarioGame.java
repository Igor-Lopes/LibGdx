package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by igor on 2/14/16.
 */
public class MarioGame extends Game{

    public SpriteBatch batch;

    public SpriteBatch getBatch(){
        return  batch;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        setScreen(new MyGdxGame(this));

    }
     @Override
    public void render() {
  //
        super.render(); //<-----

   //
    }

}
