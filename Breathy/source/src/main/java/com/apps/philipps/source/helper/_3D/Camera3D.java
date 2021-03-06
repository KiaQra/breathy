package com.apps.philipps.source.helper._3D;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.apps.philipps.source.helper.Vector;

/**
 * Created by Jevgenij Huebert on 30.03.2017. Project Breathy
 */

public class Camera3D {


    private Vector direction = new Vector(0, 0, 0);
    private Vector position = new Vector(0, 0.2f, -3.5f);
    //private Vector position = new Vector(0, 0.2f, -10f);
    private Vector up = new Vector(0, 1, 0);
    private Vector rotation = new Vector(0, 0, 1, 0);

    public static float[] mMVPMatrix = new float[16];
    public static float[] mProjectionMatrix = new float[16];
    public static float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];

    public Camera3D(int width, int height, Vector... vectors) {
        if (vectors.length > 0)
            direction = vectors[0];
        if (vectors.length > 1)
            position = vectors[1];
        if (vectors.length > 2)
            up = vectors[2];
        if (vectors.length > 3)
            rotation = vectors[3];

        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 50000);
        updateMatrices();
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getProjectionMatrixMatrix() {
        return mProjectionMatrix;
    }

    public void set(Vector... vectors) {
        if (vectors.length > 0)
            direction = vectors[0];
        if (vectors.length > 1)
            position = vectors[1];
        if (vectors.length > 2)
            up = vectors[2];
        if (vectors.length > 3)
            rotation = vectors[3];
        updateMatrices();
    }

    public void move(Vector... vectors) {
        if (vectors.length > 0)
            direction.add(vectors[0]);
        if (vectors.length > 1)
            position.add(vectors[1]);
        if (vectors.length > 2)
            up.add(vectors[2]);
        if (vectors.length > 3) {
            vectors[3].add(new Vector(0, 0, 0, rotation.getF(3)));
            rotation.set(vectors[3]);
        }
        updateMatrices();
    }

    public void changeSurface(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 50000);
        updateMatrices();
    }

    private void updateMatrices() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        Matrix.setLookAtM(mViewMatrix, 0, position.getF(0), position.getF(1), position.getF(2),
                direction.getF(0), direction.getF(1), direction.getF(2),
                up.getF(0), up.getF(1), up.getF(2));

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        Matrix.setRotateM(mRotationMatrix, 0, rotation.getF(3), rotation.getF(0), rotation.getF(1), rotation.getF(2));

        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
    }

    public Vector getDirection() {
        return direction.clone();
    }

    public Vector getPosition() {
        return position.clone();
    }

    public Vector getUp() {
        return up.clone();
    }

    public Vector getRotation() {
        return rotation.clone();
    }
}
