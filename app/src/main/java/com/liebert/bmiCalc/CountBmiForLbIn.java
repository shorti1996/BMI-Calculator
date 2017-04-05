package com.liebert.bmiCalc;

/**
 * Created by shorti1996 on 22.03.2017.
 */

public class CountBmiForLbIn implements ICountBmi {

    CountBmiForKgCm countBmiForKgM;

    private final static float METER_TO_INCH = 0.393701f;
    private final static float MIN_HEIGHT = 50f * METER_TO_INCH;
    private final static float MAX_HEIGHT = 250f * METER_TO_INCH;

    private final static float KG_TO_POUND = 2.204623f;
    private final static float MIN_MASS = 10.0f * KG_TO_POUND;
    private final static float MAX_MASS = 250.0f * KG_TO_POUND;

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
            return (mass / (height * height)) * 703.0f;
        }
    }
}
