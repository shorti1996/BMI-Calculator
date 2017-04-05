package com.liebert.bmiCalc;

/**
 * Created by shorti1996 on 15.03.2017.
 */

public class CountBmiForKgCm implements ICountBmi {

    private final static float MIN_HEIGHT = 50f;
    private final static float MAX_HEIGHT = 250f;
    private final static float MIN_MASS = 10.0f;
    private final static float MAX_MASS = 250.0f;

    @Override
    public Boolean isValidMass(float mass) {
        return mass >= MIN_MASS && mass <= MAX_MASS;
    }

    @Override
    public Boolean isValidHeight(float height) {
        return height >= MIN_HEIGHT && height <= MAX_HEIGHT;
    }

    @Override
    public float countBmi(float mass, float height) {
        if (!isValidHeight(height) || !isValidMass(mass)) {
            throw new IllegalArgumentException("Wrong arguments");
        }
        else {
            float heightM = height / 100.0f;
            return mass / (heightM * heightM);
        }
    }
}
