package com.mygdx.game.com.mygdx.Entities;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

/**
 * Created by igor on 1/28/16.
 */
public class CreateBodies {

    private final static float PPM = 100f;

    public static Body createBody(Vector2 position, float w, float h, float d, float r, float f,
                                  BodyDef.BodyType bType, World world, boolean isFixedRotation) {
        Body body;
        Fixture fixture;
        BodyDef bdef = new BodyDef();
        bdef.type = bType;
        bdef.position.set((position.x + w / 2) / PPM,
                (position.y + h / 2) / PPM);
        body = world.createBody(bdef);
        fixture = body.createFixture(createFixture(body, w, h, d, r, f));
        body.setFixedRotation(isFixedRotation);
        body.setUserData(fixture);
        return body;
    }

    private static FixtureDef createFixture(Body b, float w, float h, float d, float r, float f) {
        FixtureDef fDef = new FixtureDef();
        fDef.shape = createShape(w, h);
        fDef.density = d;
        fDef.restitution = r;
        fDef.friction = f;
        // Fixture fixture = b.createFixture(fDef);

        return fDef;
    }

    private static PolygonShape createShape(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        return shape;
    }

    public static ArrayList createFromTiled(String layerName, TiledMap tiledMap, World world, BodyDef.BodyType bType, boolean sensor) {
        ArrayList array = new ArrayList();
        MapLayer layer;
        MapObjects objects;
        layer = tiledMap.getLayers().get(layerName);
        objects = layer.getObjects();
        Body body;
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        for (int i = 0; i < objects.getCount(); i++) {
            RectangleMapObject rmp = (RectangleMapObject) objects.get(i);
            Rectangle r = rmp.getRectangle();
            bodyDef.type = bType;
            // Box2D uses x,y to place it in the center, so it needs to add the
            // w and h to x,y
            // and divide by 2 so it can center it properly
            bodyDef.position.set((r.x + r.width * 0.5f) / PPM, (r.y + r.height * 0.5f) / PPM);
            body = world.createBody(bodyDef);

            shape.setAsBox(r.getWidth() * 0.5f / PPM, r.getHeight() * 0.5f / PPM);
            fixtureDef.shape = shape;
            fixtureDef.friction = 1;
            fixtureDef.isSensor = sensor;
            Fixture f = body.createFixture(fixtureDef);

            body.setUserData(f);
            array.add(body);
        }
        return array;
    }

    public static Fixture createBoxSensor(Body body, float w, float h, Vector2 position, float angle) {
        FixtureDef fDef = new FixtureDef();
        Fixture fixture;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w, h, position, angle);
        fDef.isSensor = true;
        fDef.shape = shape;
        fixture = body.createFixture(fDef);
        shape.dispose();
        return fixture;
    }
}
