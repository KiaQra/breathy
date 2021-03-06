package com.apps.philipps.opengltest;

import android.content.Context;

import com.apps.philipps.source.helper.Vector;
import com.apps.philipps.source.helper._3D.BoundingBox;
import com.apps.philipps.source.helper._3D.GameObject3D;


public class Tire {
    private GameObject3D tire;
    public float speed = 0f;
    float angle = 0;
    float constant = 0f;
    float count = 0f;
    Car car;
    boolean isFrontTire;
    private Vector centerOfObject = new Vector();

    public Tire(Context mActivityContext, Car car, int modelID, int textureId, boolean isFrontTire) {
        this.car = car;
        this.isFrontTire = isFrontTire;
        tire = new GameObject3D(GameObject3D.loadObject(mActivityContext, modelID, textureId));
        tire.getBoundingBox().setNeedToCalculateBB(false);
        calculateCenterOfObject();
    }

    public void setPosition(Vector position) {
        tire.setPosition(position);
    }

    public void setRotation(Vector rotation) {
        tire.setRotation(rotation);
    }

    public void turnRight(float speed) {
        this.speed = speed;
        tire.getPosition().add(new Vector(-speed, 0, 0));
        angle -= speed;
    }

    public void turnLeft(float speed) {
        this.speed = speed;
        tire.getPosition().add(new Vector(+speed, 0, 0));
        angle += speed;
    }


    public void runs(float speed) {
        tire.move(new Vector(0, 0, 0));
        Vector centerOfObject = getCenterOfObject();
        tire.rotate(new Vector(1, 0, 0, -90));
        float turnAngel = angle * Backend.rotateSpeed;
        tire.rotate(new Vector(0, 1, 0, turnAngel));
        resetRotation();

        tire.setPosition(new Vector(0, 0, 0));
        tire.move(centerOfObject);
        count = count + 5 + speed * 360f;
        if (isFrontTire)
            tire.rotate(new Vector(0, 1, 0, angle * Backend.rotateSpeed / 2.0f));
        tire.rotate(new Vector(1, 0, 0, count));
        tire.setPosition(new Vector(0, 0, 0));
        tire.move(new Vector().sub(centerOfObject));
        constant = 0.0f;
        tire.setPosition(car.getCarBodyObject3D().getPosition().clone());
    }

    public void crashes() {
        constant += 5.0f;
        tire.move(new Vector(0, 0, 0));
        tire.rotate(new Vector(1, 0, 0, -90));
        tire.rotate(new Vector(0, 1, 0, constant));
    }


    public void resetRotation() {
        if (Math.abs(angle) < speed)
            angle = 0f;
        if (angle > 0)
            angle -= speed;
        else if (angle < 0)
            angle += speed;
    }


    public void draw(long deltaTime) {
        tire.update(deltaTime);
    }

    public GameObject3D getObject3D() {
        return tire;
    }

    private void calculateCenterOfObject() {
        BoundingBox boundingBox = getObject3D().getBoundingBox();
        centerOfObject = new Vector(
                (boundingBox.start_maxX - boundingBox.start_minX) / 2.0f + boundingBox.start_minX,
                (boundingBox.start_maxY - boundingBox.start_minY) / 2.0f + boundingBox.start_minY,
                (boundingBox.start_maxZ - boundingBox.start_minZ) / 2.0f + boundingBox.start_minZ);
    }

    private Vector getCenterOfObject() {
        return centerOfObject;
    }

}
