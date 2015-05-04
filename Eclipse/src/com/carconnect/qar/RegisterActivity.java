package com.carconnect.qar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {

	private EditText mUsernameEditText;
	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordRepeatEditText;
	private Button mRegisterButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initializeView();
		initListeners();
	}

	public void initializeView() {
		mUsernameEditText = (EditText) findViewById(R.id.username_edit_text);
		mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
		mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
		mPasswordRepeatEditText = (EditText) findViewById(R.id.password_repeat_edit_text);
		mRegisterButton = (Button) findViewById(R.id.register_button);
	}

	public void initListeners() {

		mRegisterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				registerButtonClicked();
			}
		});
	}

	private void registerButtonClicked() {
		// validate fields
		if (mUsernameEditText.getText().length() == 0 || mEmailEditText.getText().length() == 0 || 
				mPasswordEditText.getText().length() == 0 || mPasswordRepeatEditText.getText().length() == 0) {
			Toast.makeText(getApplicationContext(), "Fill in all fields", Toast.LENGTH_LONG).show();
		} else if (!mPasswordEditText.getText().toString().equals(mPasswordRepeatEditText.getText().toString())) {
			Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
		} else {
			parseRegister();
		}
	}

	private void parseRegister() {
		ParseUser user = new ParseUser();
		user.setUsername(mUsernameEditText.getText().toString());
		user.setEmail(mEmailEditText.getText().toString());
		user.setPassword(mPasswordEditText.getText().toString());

		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					// back to Login
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Sign up Error", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
