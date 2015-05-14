package com.tinkler.it;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import api.QCApi;
import api.QCApi.GetLocalTinklerTypesCallback;
import api.QCApi.GetOnlineTinklerTypesCallback;
import api.QCApi.AddTinklerCallback;
import api.QCApi.DeleteTinklerCallback;
import api.QCApi.EditTinklerCallback;
import api.Utils;
import model.Tinkler;
import model.TinklerType;
import utils.AttachImageActivity;

public class AddTinklerActivity extends AttachImageActivity implements GetOnlineTinklerTypesCallback, GetLocalTinklerTypesCallback, AddTinklerCallback, EditTinklerCallback, DeleteTinklerCallback, OnDateSetListener {

	private EditText tinklerNameEditText;
	private EditText tinklerPlateEditText;
	private Spinner tinklerTypeSpinner;
	private EditText tinklerYearEditText;
	private ImageView tinklerImageView;
	private Button saveButton;
	private Button deleteButton;
	private DatePickerDialog datePickerDialog;

	private String mState;
	private Tinkler mTinkler;
	private String[] mTinklerTypesArray;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_tinkler);

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

		if (getIntent().hasExtra(ProfileFragmentActivity.STATE)) {
			mState = getIntent().getExtras().getString(ProfileFragmentActivity.STATE);
		}

		if (getIntent().hasExtra(ProfileFragmentActivity.TINKLER)) {
			String tinklerId = getIntent().getExtras().getString(ProfileFragmentActivity.TINKLER);

			mTinkler = QCApi.getTinkler(tinklerId);
		}

		tinklerNameEditText = (EditText) findViewById(R.id.tinkler_name);
		tinklerPlateEditText = (EditText) findViewById(R.id.tinkler_plate);
		tinklerTypeSpinner = (Spinner) findViewById(R.id.tinkler_type_spinner);
		tinklerYearEditText = (EditText) findViewById(R.id.tinkler_year);
		tinklerImageView = (ImageView) findViewById(R.id.tinkler_image);
		saveButton = (Button) findViewById(R.id.save_button);
		deleteButton = (Button) findViewById(R.id.delete_button);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTinklerTypesArray);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tinklerTypeSpinner.setAdapter(dataAdapter);

		if (mState.equals(ProfileFragmentActivity.EDIT_TINKLER)) {
			tinklerNameEditText.setText(mTinkler.getName());
			tinklerTypeSpinner.setSelection(3);

			//Set Tinkler's data depending on its typ
			String tinklerType = mTinkler.getType().get("typeName").toString();

			if(tinklerType.equals("Vehicle")){
				tinklerPlateEditText.setText(mTinkler.getVehiclePlate());
				tinklerYearEditText.setText(Utils.dateToString(mTinkler.getVehicleYear(), "LLL yyyy"));
			}else if(tinklerType.equals("Pet")){

			}else if(tinklerType.equals("Realty or Location")){

			}else if(tinklerType.equals("Object") || tinklerType.equals("Bag or Suitcase")){

			}else if(tinklerType.equals("Advertisement")){

			}


			saveButton.setText("Edit Vehicle");
			deleteButton.setVisibility(View.VISIBLE);

			getSupportActionBar().setTitle("Edit Vehicle");
		} else {
			getSupportActionBar().setTitle("Add Vehicle");
		}
	}

	private void initListeners() {

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				validateForm();
			}
		});

		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteTinkler();
			}
		});

		tinklerYearEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH);
				int day = c.get(Calendar.DAY_OF_MONTH);

				datePickerDialog = new DatePickerDialog(AddTinklerActivity.this, AddTinklerActivity.this, year, month, day);
				datePickerDialog.show();
			}
		});
	}

	private void validateForm() {

		if (tinklerNameEditText.getText().toString().isEmpty() || tinklerPlateEditText.getText().toString().isEmpty() || tinklerYearEditText.getText().toString().isEmpty()) {
			Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
		} else {
			String name = tinklerNameEditText.getText().toString();
			String plate = tinklerPlateEditText.getText().toString();
			String type = mTinklerTypesArray[tinklerTypeSpinner.getSelectedItemPosition()];
			String year = tinklerYearEditText.getText().toString();

			Tinkler tinkler = new Tinkler();
			tinkler.setName(name);
			tinkler.setVehiclePlate(plate);
			//tinkler.setType(type);
			tinkler.setVehicleYear(Utils.stringToDate(year, "LLLL yyyy"));

			if (mState.equals(ProfileFragmentActivity.ADD_TINKLER)) {
				QCApi.addTinkler(tinkler, this);
			} else {
				tinkler.setId(mTinkler.getId());
				QCApi.addTinkler(tinkler, this);
			}
		}
	}

	private void deleteTinkler() {
		QCApi.deleteTinkler(mTinkler, this);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);

		tinklerYearEditText.setText(Utils.dateToString(cal.getTime(), "LLL yyyy"));
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

	@Override
	public void onCompleteEdit(boolean success) {
		if (success) {
			Toast.makeText(this, "Sucess", Toast.LENGTH_LONG).show();
			finish();
		} else {
			Toast.makeText(this, "Oops", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onCompleteDelete(boolean success) {
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
