package com.liebert.bmiCalc;

/**
 * Created by shorti1996 on 15.03.2017.
 */

public interface ICountBmi {

    public Boolean isValidMass(float mass);
    public Boolean isValidHeight(float height);
    public float countBmi(float mass, float height);

}
