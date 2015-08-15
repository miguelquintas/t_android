package com.tinkler.it;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by diogoguimaraes on 15/08/15.
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageView mUserPhoto;
    private EditText mNameEditText;
    private TextView mTutorialText;
    private Switch mCustomMgsSwitch;
    private Button mResetPwdButton;
    private Button mLogoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff00CEBA));
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        initializeView();

    }

    public void initializeView() {
        mUserPhoto = (ImageView) findViewById(R.id.user_image);
        mNameEditText = (EditText) findViewById(R.id.user_name);
        mTutorialText = (TextView) findViewById(R.id.tutorial_textView);
        mCustomMgsSwitch = (Switch) findViewById(R.id.switch1);
        mResetPwdButton = (Button) findViewById(R.id.resetPwd_button);
        mLogoutButton = (Button) findViewById(R.id.logout_button);
    }


}
