package com.tinkler.it;

import android.app.AlertDialog;
import android.app.DatePickerDialog;

import api.QCApi;
import api.QCApi.GetLocalTinklerTypesCallback;
import api.QCApi.GetOnlineTinklerTypesCallback;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.Calendar;

import api.QCApi.DeleteTinklerCallback;
import api.QCApi.EditTinklerCallback;
import api.Utils;
import model.Tinkler;
import model.TinklerType;
import utils.AttachImageActivity;

/**
 * Created by diogoguimaraes on 17/05/15.
 */
public class EditTinklerActivity extends AttachImageActivity implements GetOnlineTinklerTypesCallback, GetLocalTinklerTypesCallback, EditTinklerCallback, DeleteTinklerCallback, OnDateSetListener {

    private EditText tinklerNameEditText;
    private EditText tinklerAtr01EditText;
    private Spinner tinklerTypeSpinner;
    private EditText tinklerAtr02EditText;
    private ParseImageView tinklerImageView;
    private ParseImageView qrCodeImageView;
    private Button updButton;
    private Button regButton;
    private Button delButton;
    private DatePickerDialog datePickerDialog;

    private Tinkler mTinkler;
    private String[] mTinklerTypesArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tinkler);

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

        if (getIntent().hasExtra(ProfileFragmentActivity.TINKLER)) {
            String tinklerId = getIntent().getExtras().getString(ProfileFragmentActivity.TINKLER);

            mTinkler = QCApi.getTinkler(tinklerId);
        }

        tinklerNameEditText = (EditText) findViewById(R.id.editTinklerName);
        tinklerAtr01EditText = (EditText) findViewById(R.id.editTinklerAtr01);
        tinklerTypeSpinner = (Spinner) findViewById(R.id.editTinklerSpinner);
        tinklerAtr02EditText = (EditText) findViewById(R.id.editTinklerAtr02);
        tinklerImageView = (ParseImageView) findViewById(R.id.editTinklerImage);
        qrCodeImageView = (ParseImageView) findViewById(R.id.editTinklerQrCode);
        updButton = (Button) findViewById(R.id.editTinklerUpdBtn);
        regButton = (Button) findViewById(R.id.editTinklerRegBtn);
        delButton = (Button) findViewById(R.id.editTinklerDelBtn);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTinklerTypesArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tinklerTypeSpinner.setAdapter(dataAdapter);

        tinklerNameEditText.setText(mTinkler.getName());

        ParseFile tinklerImage = mTinkler.getImage();
        tinklerImageView.setParseFile(tinklerImage);
        tinklerImageView.loadInBackground();

        ParseFile qrCodeImage = mTinkler.getTinkler();
        qrCodeImageView.setParseFile(qrCodeImage);
        qrCodeImageView.loadInBackground();

        tinklerTypeSpinner.setSelection(3);

        //Set Tinkler's data depending on its typ
        String tinklerType = mTinkler.getType().get("typeName").toString();

        if(tinklerType.equals("Vehicle")){
            tinklerAtr01EditText.setText(mTinkler.getVehiclePlate());
            tinklerAtr02EditText.setText(Utils.dateToString(mTinkler.getVehicleYear(), "LLL yyyy"));
        }else if(tinklerType.equals("Pet")){

        }else if(tinklerType.equals("Realty or Location")){

        }else if(tinklerType.equals("Object") || tinklerType.equals("Bag or Suitcase")){

        }else if(tinklerType.equals("Advertisement")){

        }

        getSupportActionBar().setTitle(mTinkler.getName());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff00CEBA));
    }

    private void initListeners() {

        updButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validateForm();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteTinkler();
            }
        });

        tinklerAtr02EditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(EditTinklerActivity.this, EditTinklerActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void validateForm() {

        if (tinklerNameEditText.getText().toString().isEmpty() || tinklerAtr01EditText.getText().toString().isEmpty() || tinklerAtr02EditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
        } else {
            String name = tinklerNameEditText.getText().toString();
            String plate = tinklerAtr01EditText.getText().toString();
            String type = mTinklerTypesArray[tinklerTypeSpinner.getSelectedItemPosition()];
            String year = tinklerAtr02EditText.getText().toString();

            Tinkler tinkler = new Tinkler();
            tinkler.setName(name);
            tinkler.setVehiclePlate(plate);
            //tinkler.setType(type);
            tinkler.setVehicleYear(Utils.stringToDate(year, "LLLL yyyy"));

            tinkler.setId(mTinkler.getId());
            QCApi.editTinkler(tinkler, this);
        }
    }

    private void deleteTinkler() {
        QCApi.deleteTinkler(mTinkler, this);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        tinklerAtr02EditText.setText(Utils.dateToString(cal.getTime(), "LLL yyyy"));
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

        View.OnClickListener takePhotoListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(EditTinklerActivity.this).setTitle("").setMessage("Pick source").setCancelable(true)
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
