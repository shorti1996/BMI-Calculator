package com.liebert.bmiCalc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.function.Predicate;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    //dagger
    //private SharedPreferences mSharedPreferences;

    @BindView(R.id.mass_tiet) TextInputEditText mMassTiet;
    @BindView(R.id.mass_til) TextInputLayout mMassTil;
    @BindView(R.id.height_tiet)TextInputEditText mHeightTiet;
    @BindView(R.id.height_til) TextInputLayout mHeightTil;
    @BindView(R.id.bmi_tv) TextView mBmiTv;

    private boolean mMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getSavedValues(sharedPreferences);
        loadUnitPreferences(sharedPreferences);
        setParamsHint(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        //setTextChangedListeners();
        setupTextInputEditTextListeners();

        //dagger
        //fail XD
//        mSharedPreferences = DaggerSharedPrerenceComponent.builder()
//                .sharedPreferenceModule(new SharedPreferenceModule(getApplication()))
//                .build().getSharedPreference();
    }

    private void setupTextInputEditTextListeners() {
        //TODO (3) Czy takie lambdy (predykaty) sa ok
        setupRxListener(mMassTil, mMassTiet, getString(R.string.pref_saved_mass_key), this::validateMass);
        setupRxListener(mHeightTil, mHeightTiet, getString(R.string.pref_saved_height_key), this::validateHeight);
    }

    //TODO (4) Czy bez implementowania tego sie da jakos dalej przekazac validateMass/validateHeight
    /*moze byc moj predykat
    @FunctionalInterface
    public interface MyPredicate<T> {

        /**
         * Tests the value for satisfying predicate.
         *
         * @param value the value to be tests
         * @return {@code true} if the value matches the predicate, otherwise {@code false}
         */
        /*boolean test(T value);
    }*/

    /**
     * get text, parse float, check if valid, save prefs, set errors
     * @param textInputLayout
     * @param textInputEditText
     * @param key sharedPrefs key
     * @param validator
     *
     */
    private void setupRxListener(TextInputLayout textInputLayout, TextInputEditText textInputEditText, String key, Predicate<Float> validator) {
        RxTextView.textChangeEvents(textInputEditText)
            .debounce(400, TimeUnit.MILLISECONDS)
            .map(event -> event.view().getText().toString())
            .map(text -> {
                return text == null || text.equals("") ? 0.0f : Float.parseFloat(text);
            }).map(value -> {
                boolean paramValid = value == 0.0f || validator.test(value);
                if (paramValid) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putFloat(key, value);
                    editor.apply();
                }
                return paramValid;
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(paramValid -> {
                if (paramValid) {
                    textInputLayout.setError(null);
                } else {
                    textInputLayout.setError(getString(R.string.invalid_args_input));
                }
        });
    }

    /**
     * Bez sensu troche ta metoda, wolalem zapisywac automatycznie
     */
    @OnClick (R.id.save_args_btn)
    public void saveHeightAndMass(){
        ICountBmi bmiCounter = getICountBmi();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        String massString = mMassTiet.getText().toString();
        String heightString = mHeightTiet.getText().toString();
        try {
            float mass = Float.parseFloat(massString);
            float height = Float.parseFloat(heightString);
            if (bmiCounter.isValidHeight(height) && bmiCounter.isValidMass(mass)) {
                editor.putFloat(getString(R.string.pref_saved_mass_key), mass);
                editor.putFloat(getString(R.string.pref_saved_height_key), height);
                editor.apply();
                Toast.makeText(this, getString(R.string.saved_args_toast), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.invalid_args_toast), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //przed tym, jak Pan powiedział, że mam zapisywać tylko dobre wartości
/*    private void setupRxListener(TextInputLayout textInputLayout, TextInputEditText textInputEditText, String key, Predicate<Float> validator) {
        RxTextView.textChangeEvents(textInputEditText)
                .debounce(400, TimeUnit.MILLISECONDS)
                .map(event -> {
                    String text = event.view().getText().toString();
                    if (text.equals("")) {
                        return true;
                    }
                    float value = Float.parseFloat(text);
                    return validator.test(value);
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(validParam -> {
                    if (validParam) {
                        textInputLayout.setError(null);
                    } else {
                        textInputLayout.setError(getString(R.string.invalid_args_input));
                    }
                });
    }*/

    private boolean validateMass(float mass) {
        ICountBmi countBmi = getICountBmi();
        return countBmi.isValidMass(mass);
    }

    private boolean validateHeight(float height) {
        ICountBmi countBmi = getICountBmi();
        return countBmi.isValidHeight(height);
    }

    //TODO (5) Czy DI mialoby sens tutaj i czy mialoby sens, gdyby ICountBmi bylo polem tej klasy
    @NonNull
    private ICountBmi getICountBmi() {
        ICountBmi countBmi;
        if (mMetric) {
            countBmi = new CountBmiForKgCm();
        } else {
            countBmi = new CountBmiForLbIn();
        }
        return countBmi;
    }

    private void loadUnitPreferences(SharedPreferences sharedPreferences) {
        String metric = getString(R.string.pref_units_metric);
        String units = sharedPreferences.getString(getString(R.string.pref_units_key), metric);
        mMetric = units.equals(metric);
    }

    /**
     * Load values from sharedPrefs and put them to views
     * @param sharedPreferences sharedPreferences to load values from
     */
    private void getSavedValues(SharedPreferences sharedPreferences) {
        float savedMass = sharedPreferences.getFloat( getString(R.string.pref_saved_mass_key), 0.0f);
        float savedHeight = sharedPreferences.getFloat( getString(R.string.pref_saved_height_key), 0.0f);

        String heightText = savedHeight == 0.0f ? "" : String.valueOf(savedHeight);
        mHeightTiet.setText(heightText);

        String massText = savedMass == 0.0f ? "" : String.valueOf(savedMass);
        mMassTiet.setText(massText);

        updateBmiTextView();
    }

    private void setParamsHint(SharedPreferences sharedPreferences) {
        if (mMetric) {
            mHeightTil.setHint(getString(R.string.height_hint_m));
            mMassTil.setHint(getString(R.string.mass_hint_kg));
        } else {
            mHeightTil.setHint(getString(R.string.height_hint_in));
            mMassTil.setHint(getString(R.string.mass_hint_lb));
        }
    }

    /**
     * I don't use it anymore
     * RxJava is cooler
     */
    private class TextInputEditTextWatcher implements TextWatcher{
        // TODO (2) czy to ok, że to są pola
        private String key;
        private TextInputEditText textInputEditText;
        private TextInputLayout textInputLayout;

        public TextInputEditTextWatcher(String key, TextInputLayout textInputLayout, TextInputEditText textInputEditText) {
            this.key = key;
            this.textInputLayout = textInputLayout;
            this.textInputEditText = textInputEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            if (s.toString().equals("")) {
                s = "0";
            }
            ICountBmi countBmi = getICountBmi();


            if (!textInputEditText.getText().toString().equals("")) {
                if (textInputEditText == mMassTiet) {
                    if (!countBmi.isValidMass(Float.parseFloat(s.toString()))) {
//                    textInputEditText.setError(getString(R.string.wrong_args_input));
                        textInputLayout.setError(getString(R.string.invalid_args_input));
                        return;
                    }
                } else if (textInputEditText == mHeightTiet) {
                    if (!countBmi.isValidHeight(Float.parseFloat(s.toString()))) {
//                    textInputEditText.setError(getString(R.string.wrong_args_input));
                        textInputLayout.setError(getString(R.string.invalid_args_input));
                        return;
                    }

                }
            }

            textInputLayout.setError(null);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(key, Float.parseFloat(s.toString()));
            editor.apply();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void setTextChangedListeners() {
        mMassTiet.addTextChangedListener(new TextInputEditTextWatcher(getString(R.string.pref_saved_mass_key), mMassTil, mMassTiet));

        mHeightTiet.addTextChangedListener(new TextInputEditTextWatcher(getString(R.string.pref_saved_height_key), mHeightTil, mHeightTiet));
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @OnClick (R.id.calcuate_bmi_btn)
    public void calculateBmi(){
        float mass = 0;
        float height = 0;
        float bmi = 0;
        ICountBmi bmiCounter = getICountBmi();

        try {
            mass = Float.parseFloat(mMassTiet.getText().toString());
            height = Float.parseFloat(mHeightTiet.getText().toString());
//            bmiCounter = new CountBmiForKgCm();
            bmi = bmiCounter.countBmi(mass, height);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.invalid_args_toast), Toast.LENGTH_SHORT).show();
            return;
        }

        hideKeyboard();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(getString(R.string.pref_saved_bmi_key), bmi);
        editor.apply();

        updateBmiTextView();
    }

    /**
     * Get saved BMI, update the corresponding view and its color
     */
    private void updateBmiTextView() {
        float bmi = getBmiFromSharedPrefs();
        String bmiString = getBmiString();
        mBmiTv.setText(bmiString);

        if (bmi < Bmi.UNDERWEIGHT_MAX || bmi > Bmi.OVERWEIGHT_MIN) {
            mBmiTv.setTextColor(Color.RED);
        }
        else {
            mBmiTv.setTextColor(Color.GREEN);
        }
    }

    /**
     * Load Bmi from sharedPrefs and get readable string
     * @return BMI with two decimal places
     */
    private String getBmiString() {
        float bmi = getBmiFromSharedPrefs();
        return new DecimalFormat("#.##").format(bmi);
    }

    private float getBmiFromSharedPrefs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getFloat(getString(R.string.pref_saved_bmi_key), 0.0f);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_about_activity:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_share_bmi:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String message = String.format(getString(R.string.share_message)+ " " + getBmiString());
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_saved_bmi_key))) {

        } else if (key.equals(getString(R.string.pref_units_key))) {
            loadUnitPreferences(sharedPreferences);
            setParamsHint(sharedPreferences);

            //TODO (1) tutaj
            clearParams();
        }
    }

    private void clearParams() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.pref_saved_bmi_key));
        editor.remove(getString(R.string.pref_saved_height_key));
        editor.remove(getString(R.string.pref_saved_mass_key));
/*        editor.putFloat(getString(R.string.pref_saved_bmi_key), 0.0f);
        editor.putFloat(getString(R.string.pref_saved_mass_key), 0.0f);
        editor.putFloat(getString(R.string.pref_saved_height_key), 0.0f);*/

        editor.apply();
        getSavedValues(sharedPreferences);
/*        mHeightTiet.setText("");
        mMassTiet.setText("");
        mBmiTv.setText("0.0");*/
    }
}

