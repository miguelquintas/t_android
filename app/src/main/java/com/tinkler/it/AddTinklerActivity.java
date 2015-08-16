package com.tinkler.it;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


import api.QCApi;
import api.QCApi.GetLocalTinklerTypesCallback;
import api.QCApi.GetOnlineTinklerTypesCallback;
import api.QCApi.AddTinklerCallback;
import model.Tinkler;
import model.TinklerType;
import utils.AttachImageActivity;

public class AddTinklerActivity extends AttachImageActivity implements GetOnlineTinklerTypesCallback, GetLocalTinklerTypesCallback, AddTinklerCallback {

	private EditText tinklerNameEditText;
	private Spinner tinklerTypeSpinner;
	private ImageView tinklerImageView;
	private Button nextButton;

	private Tinkler mTinkler;
	private String[] mTinklerTypesArray;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_tinkler);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff00CEBA));

		//Get TinklerTypes from Parse
		//Check internet connection
		if(QCApi.isOnline(this)){
			QCApi.getOnlineTinklerTypes(this);
		}else{
			QCApi.getLocalTinklerTypes(this);
		}

		initViews();
		initListeners();
	}

	private void initViews() {

        tinklerNameEditText = (EditText) findViewById(R.id.tinkler_name);
        tinklerTypeSpinner = (Spinner) findViewById(R.id.tinkler_type_spinner);
        tinklerImageView = (ImageView) findViewById(R.id.tinkler_image);
        nextButton = (Button) findViewById(R.id.next_button);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTinklerTypesArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tinklerTypeSpinner.setAdapter(dataAdapter);

		getSupportActionBar().setTitle("New Tinkler");
	}

	private void initListeners() {

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				validateForm();
			}
		});

	}

	private void validateForm() {

		if (tinklerNameEditText.getText().toString().isEmpty()) {
			Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
		} else {
            //Go to the submit screen
            Fragment fragment = null;
            fragment = new SubmitNewTinklerFragmentActivity();
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.add_new_tinkler_frame, fragment).commit();
		}
	}

	private int reverseTypeArray(String type) {
		for (int i = 0; i < mTinklerTypesArray.length; i++) {
			if (type.equals(mTinklerTypesArray[i])) {
				return i;
			}
		}

		return 0;
	}

	@Override
	public void onCompleteAdd(boolean success) {
		if (success) {
			Toast.makeText(this, "Sucess", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Oops", Toast.LENGTH_LONG).show();
		}
	}
	
	// upload image stuff
	
	private void initCameraListeners(){
		final DialogInterface.OnClickListener cameraClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startCamera();
			}
		};

		final DialogInterface.OnClickListener galleryClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startGallery();
			}
		};

		OnClickListener takePhotoListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(AddTinklerActivity.this).setTitle("").setMessage("Pick source").setCancelable(true)
						.setPositiveButton("Camera", cameraClickListener)
						.setNegativeButton("Galeery", galleryClickListener).create();
				dialog.show();
			}
		};
	}

	@Override
	public void onCompleteGetOnlineTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success) {
		if (success) {
			mTinklerTypesArray = new String[tinklerTypes.size()];

			//populate the string array of types
			for(int i =0; i<tinklerTypes.size(); i++) {
				mTinklerTypesArray[i] = tinklerTypes.get(i).getName();
			}

		} else {
			Toast.makeText(this, "Oops..", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onCompleteGetLocalTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success) {
		if (success) {
			mTinklerTypesArray = new String[tinklerTypes.size()];

			//populate the string array of types
			for(int i =0; i<tinklerTypes.size(); i++) {
				mTinklerTypesArray[i] = tinklerTypes.get(i).getName();
			}

		} else {
			Toast.makeText(this, "Oops..", Toast.LENGTH_LONG).show();
		}

	}
}
