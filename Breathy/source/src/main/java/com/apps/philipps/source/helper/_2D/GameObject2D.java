package com.apps.philipps.source.helper._2D;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.apps.philipps.source.helper.Animated;
import com.apps.philipps.source.helper.Vector;
/**
 * Created by Jevgenij Huebert on 17.03.2017. Project Breathy
 */
public class GameObject2D extends Animated implements Cloneable {

    public boolean rotateToDirection = true;
    private Animated rotation = new Animated();
    public boolean intercectable = true;
    private View object;
    public final long created = System.currentTimeMillis();
    protected final String TAG = getClass().getSimpleName();

    public GameObject2D(Context context) {
        this(new ImageView(context));
    }

    public GameObject2D(@NonNull View object) {
        this(object, null, null);
    }

    public GameObject2D(@NonNull View object, @NonNull Vector position) {
        this(object, position, position.clone());
    }


    public GameObject2D(@NonNull View object, Vector position, Vector destination) {
        this(object, position, destination, 1, false);
    }

    public GameObject2D(@NonNull View object, Vector position, Vector destination, double speed, boolean active) {
        super(position, destination, speed, active);
        this.object = object;
        this.object.setPivotX(0);
        this.object.setPivotY(0);
        if (this.position == null)
            this.position = new Vector(this.object.getX(), this.object.getY());
        else {
            object.setX(this.position.getF(0));
            object.setY(this.position.getF(1));
        }
        if (this.destination == null)
            this.destination = this.position.clone();
    }

    public GameObject2D(@NonNull View object, @NonNull GameObject2D gameObject2D) {
        this(object, gameObject2D.getPosition().clone(), gameObject2D.getDestination().clone());
        setSpeed(gameObject2D.getSpeed());
        if (gameObject2D.isMoving())
            move();
        rotation = gameObject2D.rotation.clone();
    }

    public View getView() {
        return object;
    }


    @Override
    public void update(double deltaMilliseconds) {
        super.update(deltaMilliseconds);
        if(rotateToDirection)
            rotate();
        object.setX(position.getF(0));
        object.setY(position.getF(1));
    }

    @Override
    public void setPosition(Vector position) {
        super.setPosition(position);
        object.setX(position.getF(0));
        object.setY(position.getF(1));
    }

    public void setRotation(Vector rotation) {
        float angle = (float) Math.toDegrees(Math.atan2(rotation.get(1), rotation.get(0)));
        getView().setRotation(angle);
    }
    public void rotate(){
        float angle = (float) Math.toDegrees(Math.atan2(destination.get(1)-position.get(1), destination.get(0)-position.get(0)));
        getView().setRotation(angle);
    }

    /**
     * Sets the View object
     *
     * @param object the View represented by this class
     */
    public void setObject(View object) {
        this.object = object;
    }

//    private void calcNewTarget(double alpha) {
//
//        float x = destination.getF(0);
//        float y = destination.getF(1);
//        float xD = position.getF(0);
//        float yD = position.getF(1);
//        float newX = (float) (xD + (x - xD) * Math.cos(alpha) - (y - yD) * Math.sin(alpha));
//        float newY = (float) (yD + (x - xD) * Math.sin(alpha) + (y - yD) * Math.cos(alpha));
//
//        //ToDo dafür sorgen das Bild unter bestimmten umständen aus dem Screen verschwindet
//
//        destination = new Vector(x, y);
//    }

    /**
     * Get Boundaries in the form:<br>
     * [x1 x2 y1 y2] This Coordinates describes edges in this kind:
     * <pre>
     * x1,y1   x2,y1
     * x1,y2   x2,y2
     * </pre>
     * <p>
     * x1,y1 is the Position of this Game Object
     *
     * @return 4 Values that describes edges of a box
     */
    public Vector getBoundaries() {
        double[] pos = position.get();
        return new Vector(pos[0], pos[0] + object.getMeasuredWidth(), pos[1], pos[1] + object.getMeasuredHeight());
    }

    public boolean intersect(GameObject2D gameObject2D) {
        if (intercectable) {
            Rect r1 = new Rect();
            Rect r2 = new Rect();
            object.getDrawingRect(r1);
            gameObject2D.object.getDrawingRect(r2);
            return r1.intersect(r2);
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + super.toString();
    }

    @Override
    public GameObject2D clone() {
        return new GameObject2D(object, this);
    }
}
