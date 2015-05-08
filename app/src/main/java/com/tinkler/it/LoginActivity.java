package com.tinkler.it;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import api.QCApi;
import api.QCApi.VerifyEmailCallback;

public class LoginActivity extends AppCompatActivity implements VerifyEmailCallback{

	private EditText mUsernameEditText;
	private EditText mPassswordEditText;
	private Button mLoginButton;
	private TextView mSignupTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(R.layout.activity_login);

		//Enable Local Datastore
		Parse.enableLocalDatastore(getApplicationContext());
		Parse.initialize(this, "cw3jgrLq6MFIDoaYln4DEKDsJeUIF3GACepXNiMN", "zqfETbeQXwLqDvcZeW0OHYnbPkpcmFt7VTRG6Roh");
		//Enable Push Notifications
		ParsePush.subscribeInBackground("", new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
				} else {
					Log.e("com.parse.push", "failed to subscribe for push", e);
				}
			}
		});


		ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null){
        	userIsLoggedIn();
        }
			
		initializeView();
		initListeners();
	}

	public void initializeView() {
		mUsernameEditText = (EditText) findViewById(R.id.username_edit_text);
		mPassswordEditText = (EditText) findViewById(R.id.password_edit_text);
		mLoginButton = (Button) findViewById(R.id.login_button);
		mSignupTextView = (TextView) findViewById(R.id.signup_text_view);
	}

	public void initListeners() {

		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginButtonClicked();
			}
		});
		
		mSignupTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void loginButtonClicked(){
		if (mUsernameEditText.getText().length() == 0 || mPassswordEditText.getText().length() == 0){
			Toast.makeText(getApplicationContext(), "Fill in all fields", Toast.LENGTH_LONG).show();
		} else {
			parseLogin();
		}
	}

	private void parseLogin(){
		ParseUser.logInInBackground(mUsernameEditText.getText().toString(), mPassswordEditText.getText().toString(), new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null){
					QCApi.confirmEmail(user.getEmail(), LoginActivity.this);
				} else {
					Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void userIsLoggedIn(){
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onCompleteVerify(boolean success) {
		if (success){
			userIsLoggedIn();
		} else {
			Toast.makeText(this, "Activate your account first", Toast.LENGTH_LONG).show();
		}
		
	}
}
