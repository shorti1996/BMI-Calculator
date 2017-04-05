package com.liebert.bmiCalc;

import android.support.v4.media.MediaMetadataCompat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by shorti1996 on 15.03.2017.
 */

public class CountBmiTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void massUnderZeroKgInvalid() {
        //GIVEN - dane testowe
        float mass = -1.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForKgCm();
        //THEN - wynik
        assertFalse(iCountBmi.isValidMass(mass));
    }

    @Test
    public void mass100KgValid() {
        //GIVEN - dane testowe
        float mass = 100.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForKgCm();
        //THEN - wynik
        assertTrue(iCountBmi.isValidMass(mass));
    }

    @Test
    public void height150CmValid() {
        //GIVEN - dane testowe
        float height = 150f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForKgCm();
        //THEN - wynik
        assertTrue(iCountBmi.isValidHeight(height));
    }

    @Test
    public void Bmi90kg150cm() {
        //GIVEN - dane testowe
        float height = 150f;
        float mass = 90.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForKgCm();
        //THEN - wynik
        assertEquals(iCountBmi.countBmi(mass, height), 40, 0.001f);
    }

    @Test
    public void massUnderZeroLbInvalid() {
        //GIVEN - dane testowe
        float mass = -1.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForLbIn();
        //THEN - wynik
        assertFalse(iCountBmi.isValidMass(mass));
    }

    @Test
    public void mass143LbValid() {
        //GIVEN - dane testowe
        float mass = 143.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForLbIn();
        //THEN - wynik
        assertTrue(iCountBmi.isValidMass(mass));
    }

    @Test
    public void height68InValid() {
        //GIVEN - dane testowe
        float height = 68.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForLbIn();
        //THEN - wynik
        assertTrue(iCountBmi.isValidHeight(height));
    }

    @Test
    public void Bmi143Lb68In() {
        //GIVEN - dane testowe
        float height = 68.0f;
        float mass = 143.0f;
        //WHEN - inicjalizacja
        ICountBmi iCountBmi = new CountBmiForLbIn();
        //THEN - wynik
        assertEquals(iCountBmi.countBmi(mass, height), 21.74f, 0.001f);
    }

}
