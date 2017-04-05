package com.liebert.bmiCalc;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shorti1996 on 25.03.2017.
 */

@Singleton
@Component(modules = {SharedPreferenceModule.class})
public interface SharedPreferenceComponent {
    SharedPreferences getSharedPreference();
}