package com.apps.philipps.opengltest;

import android.content.Context;

import com.apps.philipps.source.helper.Vector;
import com.apps.philipps.source.helper._3D.GameObject3D;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine {
    ArrayList<GameObject3D> street = new ArrayList<>();
    private float angle = 0f;
    private float SPEED = 0.01f;

    public Car car;
    public ArrayList<Enemy> enemies;
    private float zOffset = -0.2f;
    private int numberOfEnemies = 5;
    private float relativeDistanceOfEnemies = 20.0f;
    private float safeDistance = 1.0f;

    Context mActivityContext;

    public GameEngine(Context mActivityContext) {
        this.mActivityContext = mActivityContext;
        createCar();
        createEnemies();
        createStreet();
    }

    public void runSimulation(long deltaTime) {
        car.runs();
        car.draw(deltaTime);
        enemiesRun(deltaTime);
    }

    private void createCar() {
        car = new Car(mActivityContext, R.raw.newcar, R.drawable.cartexture);
        car.setPosition(new Vector(0, -1f, zOffset));
    }

    private void createEnemies() {
        enemies = new ArrayList<>();
        for (int i = 0; i < numberOfEnemies; i++) {
            enemies.add(createNewEnemy());
        }
    }

    private void enemiesRun(long deltaTime) {
        //generate random turn
        Random random = new Random();
        for (int i = 0; i < enemies.size(); i++) {
            float r = random.nextFloat();
            Enemy enemy = enemies.get(i);
            if (enemy.getCounter() > 120) {
                if (r > 0.5f && !enemy.isTurningLeft() && !enemy.isTurningRight()) {
                    enemy.setTurningLeft(true);
                } else if (r <= 0.5f && !enemy.isTurningLeft() && !enemy.isTurningRight()) {
                    enemy.setTurningRight(true);
                } else if (enemy.isTurningLeft()) {
                    enemy.turnLeft(30f * r);
                    if (enemy.getCounter() > 180) {
                        enemy.setCounter(0);
                        enemy.setTurningLeft(false);
                    }
                } else if (enemy.isTurningRight()) {
                    enemy.turnRight(30f * r);
                    if (enemy.getCounter() > 180) {
                        enemy.setCounter(0);
                        enemy.setTurningRight(false);
                    }
                }
            }
            //runs
            enemy.runsWithSpeed(SPEED / 2.0f);
            enemy.runs();
            enemy.draw(deltaTime);

            //remove all create new enemy
            if (enemy.getObject3D().getPosition().get(1) < -2.0f) {
                enemies.remove(i);
                enemies.add(createNewEnemy());
            }
        }
    }

    private Enemy createNewEnemy() {
        Random random = new Random();
        Enemy newEnemy = new Enemy(mActivityContext, R.raw.enemycar, R.drawable.enemytexture);
        newEnemy.setPosition(new Vector(0, random.nextFloat() * relativeDistanceOfEnemies + 1f, zOffset));
        while (enemesOverlapped(newEnemy)) {
            newEnemy = createNewEnemy();
        }
        return newEnemy;
    }

    private boolean enemesOverlapped(Enemy newEnemy) {
        for (int i = 0; i < enemies.size(); i++) {
            if (Math.abs(newEnemy.getObject3D().getPosition().get(1) - enemies.get(i).getObject3D().getPosition().get(1)) < safeDistance) {
                return true;
            }
        }
        return false;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }


    //########## SIMULATION STRET #################
    public void drawStreet(long deltaTime) {
        if (street.get(street.size() / 2).getPosition().get(1) > -1.0) {
            moveStreet();
        } else {
            moveStreet();
            refreshStreet();
        }
        drawSquares(deltaTime);
    }

    private void createStreet() {
        for (int i = -9; i < 10; i++) {
            GameObject3D square = new GameObject3D(new Shapes.Square(mActivityContext));
            square.setPosition(new Vector(0, i * 1.0f, 0));
            street.add(square);
        }
    }

    public void refreshStreet() {
        GameObject3D square = street.get(0);
        float f = street.get(street.size() - 1).getPosition().get(1);
        square.setPosition(new Vector(0, f + 1.0f, 0));
        street.add(square);
        street.remove(0);
    }

    public void moveStreet() {
        for (int i = 0; i < street.size(); i++) {
            street.get(i).move(new Vector(0, -SPEED, 0));
        }
    }

    public void drawSquares(long deltaTime) {
        for (int i = 0; i < street.size(); i++) {
            street.get(i).update(deltaTime);
        }
    }
}