package com.liebert.bmiCalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.liebert.lab01.R;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.about_me_title_tv) TextView mAboutMeTitleTv;
    @BindView(R.id.about_me_tv) TextView mAboutMeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        mAboutMeTitleTv.setText(getString(R.string.about_me_title));
        mAboutMeTv.setText(getString(R.string.about_me_text));
    }


}
