package com.apps.philipps.source.helper._3D;

import android.opengl.Matrix;

import com.apps.philipps.source.helper.Vector;

import java.util.ArrayList;

/**
 * This class generates a bounding box for 3D object
 */
public class BoundingBox {
    private Vector[] points = new Vector[8];
    private float[] max_min_value;
    public float maxX;
    public float maxY;
    public float maxZ;
    public float minX;
    public float minY;
    public float minZ;
    public float start_maxX;
    public float start_maxY;
    public float start_maxZ;
    public float start_minX;
    public float start_minY;
    public float start_minZ;
    private ArrayList<Line> lines = new ArrayList<>();
    private boolean needToCalculate = true;

    /**
     * actual position of bounding box
     */
    Vector position = new Vector(0, 0, 0);

    /**
     * Constructor
     */
    public BoundingBox(Vector[] vertex) {
        if (needToCalculate) {
            calculateMaxMinValue(vertex);
            start_maxX = maxX;
            start_maxY = maxY;
            start_maxZ = maxZ;
            start_minX = minX;
            start_minY = minY;
            start_minZ = minZ;
            max_min_value = new float[]{maxX, maxY, maxZ, minX, minY, minZ};
            rotate(new Vector(1, 0, 0, -90));
            generateLines();
        }
    }

    /**
     * @return returns extreme values from bounding box
     */
    public Vector getMax_min_value() {
        if (needToCalculate)
            return new Vector(max_min_value);
        else
            return new Vector();
    }

    /**
     * translates bounding box to position <code>vector</code>
     *
     * @param vector
     */

    public void translate(Vector vector) {
        if (needToCalculate) {
            float[] temp = new float[16];
            Matrix.setIdentityM(temp, 0);
            Matrix.translateM(temp, 0, vector.getF(0), vector.getF(1), vector.getF(2));
            multipleMatrix(temp);
            position.add(vector);
        }
    }

    /**
     * rotates bounding box to position <code>vector</code>
     *
     * @param vector
     */
    public void rotate(Vector vector) {
        if (needToCalculate) {
            float[] temp = new float[16];
            Matrix.setIdentityM(temp, 0);
            Matrix.rotateM(temp, 0, temp, 0, vector.getF(3), vector.getF(0), vector.getF(1), vector.getF(2));
            multipleMatrix(temp);
        }
    }

    private void multipleMatrix(float[] transformMatrix) {
        for (int i = 0; i < points.length; i++) {
            float[] vector = new float[4];
            vector[0] = points[i].getF(0);
            vector[1] = points[i].getF(1);
            vector[2] = points[i].getF(2);
            vector[3] = 1;
            Matrix.multiplyMV(vector, 0, transformMatrix, 0, vector, 0);
            points[i].set(new Vector(vector[0], vector[1], vector[2]));
        }
    }

    private void calculateMaxMinValue(Vector[] vectors) {
        this.maxX = -Float.MAX_VALUE;
        this.maxY = -Float.MAX_VALUE;
        this.maxZ = -Float.MAX_VALUE;
        this.minX = Float.MAX_VALUE;
        this.minY = Float.MAX_VALUE;
        this.minZ = Float.MAX_VALUE;
        Vector[] temp = new Vector[vectors.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = vectors[i].clone();
        }

        for (int i = 0; i < temp.length; i++) {
            Vector v = temp[i];
            if (v.getF(0) > this.maxX)
                this.maxX = v.getF(0);
            if (v.getF(0) < this.minX)
                this.minX = v.getF(0);

            if (v.getF(1) > this.maxY)
                this.maxY = v.getF(1);
            if (v.getF(1) < this.minY)
                this.minY = v.getF(1);

            if (v.getF(2) > this.maxZ)
                this.maxZ = v.getF(2);
            if (v.getF(2) < this.minZ)
                this.minZ = v.getF(2);
        }
        this.points[0] = new Vector(maxX, maxY, maxZ);
        this.points[1] = new Vector(minX, maxY, maxZ);
        this.points[2] = new Vector(minX, minY, maxZ);
        this.points[3] = new Vector(maxX, minY, maxZ);
        this.points[4] = new Vector(maxX, maxY, minZ);
        this.points[5] = new Vector(minX, maxY, minZ);
        this.points[6] = new Vector(minX, minY, minZ);
        this.points[7] = new Vector(maxX, minY, minZ);
    }

    /**
     * check collision between 2 bounding boxes
     *
     * @param box
     * @return returns <code>true</code> if the boxes are intersected otherwise <code>false</code>
     */
    public boolean collision(BoundingBox box) {
        if (needToCalculate)
            return (minX <= box.maxX && maxX >= box.minX) &&
                    (minY <= box.maxY && maxY >= box.minY) &&
                    (minZ <= box.maxZ && maxZ >= box.minZ);
        else
            return false;
    }

    private void generateLines() {
        if (needToCalculate) {
            for (int i = 0; i < points.length - 1; i++)
                for (int j = i + 1; j < points.length; j++) {
                    Line l = new Line();
                    l.setVertices(points[i], points[j]);
                    lines.add(l);
                }
        }
    }

    /**
     * draws bounding box
     */
    public void drawLines() {
        if (needToCalculate) {
            renewLinesPosition();
            for (Line line : lines) {
                line.draw();
            }
        }
    }

    /**
     * recalculate the actual position of bounding box
     */
    public void renewLinesPosition() {
        if (needToCalculate) {
            int counter = 0;
            for (int i = 0; i < points.length - 1; i++)
                for (int j = i + 1; j < points.length; j++) {
                    lines.get(counter).setVertices(points[i], points[j]);
                    counter++;
                }
            calculateMaxMinValue(points);
        }
    }

    public void setNeedToCalculateBB(boolean bool){
        needToCalculate = bool;
    }
    public boolean isNeedToCalculateBB(){
        return needToCalculate;
    }
}

