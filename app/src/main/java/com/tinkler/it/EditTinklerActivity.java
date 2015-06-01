package com.tinkler.it;

import android.app.AlertDialog;
import android.app.DatePickerDialog;

import api.QCApi;
import api.QCApi.GetLocalTinklerTypesCallback;
import api.QCApi.GetOnlineTinklerTypesCallback;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;

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

    private RelativeLayout parentLayout;
    private EditText tinklerNameEditText;
    private EditText tinklerAtr01EditText;
    private Spinner tinklerTypeSpinner;
    private EditText tinklerAtr02EditText;
    private ParseImageView tinklerImageView;
    private Button updButton;
    private Button regButton;
    private Button delButton;
    private DatePickerDialog datePickerDialog;

    private int tinklerPos;

    private Tinkler mTinkler;
    private String[] mTinklerTypeNamesArray;
    private ArrayList<TinklerType> mTinklerTypes;

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
            tinklerPos = getIntent().getExtras().getInt(ProfileFragmentActivity.POS);
        }

        //set layout vars
        parentLayout = (RelativeLayout) findViewById(R.id.editTinklerLayout);
        tinklerNameEditText = (EditText) findViewById(R.id.editTinklerName);
        tinklerAtr01EditText = (EditText) findViewById(R.id.editTinklerAtr01);
        tinklerTypeSpinner = (Spinner) findViewById(R.id.editTinklerSpinner);
        tinklerAtr02EditText = (EditText) findViewById(R.id.editTinklerAtr02);
        tinklerImageView = (ParseImageView) findViewById(R.id.editTinklerImage);
        updButton = (Button) findViewById(R.id.editTinklerUpdBtn);
        regButton = (Button) findViewById(R.id.editTinklerRegBtn);
        delButton = (Button) findViewById(R.id.editTinklerDelBtn);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTinklerTypeNamesArray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tinklerTypeSpinner.setAdapter(dataAdapter);

        //Hide keyboard when pressing outside of it
        parentLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

        //Set TextField Adapters to Hide Keyboard
        tinklerNameEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(tinklerNameEditText.getWindowToken(), 0);
                }
                return false;
            }
        });
        tinklerAtr01EditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(tinklerAtr01EditText.getWindowToken(), 0);
                }
                return false;
            }
        });
        tinklerAtr02EditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(tinklerAtr02EditText.getWindowToken(), 0);
                }
                return false;
            }
        });

        tinklerNameEditText.setText(mTinkler.getName());
        //Set the Tinkler Image
        ParseFile tinklerImage = mTinkler.getImage();
        tinklerImageView.setParseFile(tinklerImage);
        tinklerImageView.loadInBackground();

        //Set the Tinkler Type
        tinklerTypeSpinner.setSelection(getTinklerTypePos(mTinkler.getType()));

        //Set Tinkler's data depending on its type
        String tinklerType = mTinklerTypeNamesArray[getTinklerTypePos(mTinkler.getType())];

        if(tinklerType.equals("Vehicle")){
            if(mTinkler.getVehiclePlate().isEmpty())
                tinklerAtr01EditText.setHint("Vehicle Plate");
            else
                tinklerAtr01EditText.setText(mTinkler.getVehiclePlate());

            if(mTinkler.getVehicleYear() == null)
                tinklerAtr02EditText.setHint("Vehicle Year");
            else
                tinklerAtr02EditText.setText(Utils.dateToString(mTinkler.getVehicleYear(), "LLL yyyy"));
        }else if(tinklerType.equals("Pet")){
            if(mTinkler.getPetBreed().isEmpty())
                tinklerAtr01EditText.setHint("Pet Breed");
            else
                tinklerAtr01EditText.setText(mTinkler.getPetBreed());

            if(mTinkler.getPetAge() == null)
                tinklerAtr02EditText.setHint("Pet Age");
            else
                tinklerAtr02EditText.setText(Utils.dateToString(mTinkler.getPetAge(), "LLL yyyy"));

        }else if(tinklerType.equals("Realty or Location")){
            if(mTinkler.getLocationCity().isEmpty())
                tinklerAtr01EditText.setHint("Location");
            else
                tinklerAtr01EditText.setText(mTinkler.getLocationCity());

            tinklerAtr02EditText.setVisibility(View.INVISIBLE);
        }else if(tinklerType.equals("Object") || tinklerType.equals("Bag or Suitcase")){
            if(mTinkler.getBrand().isEmpty())
                tinklerAtr01EditText.setHint("Brand");
            else
                tinklerAtr01EditText.setText(mTinkler.getBrand());

            if(mTinkler.getColor().isEmpty())
                tinklerAtr02EditText.setHint("Color");
            else
                tinklerAtr02EditText.setText(mTinkler.getColor());

        }else if(tinklerType.equals("Advertisement")){
            if(mTinkler.getAdType().isEmpty())
                tinklerAtr01EditText.setHint("Type");
            else
                tinklerAtr01EditText.setText(mTinkler.getAdType());

            if(mTinkler.getEventDate() == null)
                tinklerAtr02EditText.setHint("Event Date");
            else
                tinklerAtr02EditText.setText(Utils.dateToString(mTinkler.getEventDate(), "LLL yyyy"));
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

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void validateForm() {

        if (tinklerNameEditText.getText().toString().isEmpty() || tinklerAtr01EditText.getText().toString().isEmpty() || tinklerAtr02EditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
        } else {
            String name = tinklerNameEditText.getText().toString();
            String plate = tinklerAtr01EditText.getText().toString();
            String type = mTinklerTypeNamesArray[tinklerTypeSpinner.getSelectedItemPosition()];
            String year = tinklerAtr02EditText.getText().toString();

            Tinkler tinkler = new Tinkler();
            tinkler.setName(name);
            tinkler.setVehiclePlate(plate);
            //get the Tinkler type
            tinkler.setType(mTinkler.getType());
            tinkler.setVehicleYear(Utils.stringToDate(year, "LLLL yyyy"));

            tinkler.setId(mTinkler.getId());
            QCApi.editTinkler(tinkler, this);
        }
    }

    private void deleteTinkler() {
        QCApi.deleteTinkler(mTinkler, this);
    }

    private int getTinklerTypePos(ParseObject tinklerType){
        for(int i =0; i<mTinklerTypes.size(); i++) {
            if(mTinklerTypes.get(i).getId().equals(tinklerType.getObjectId()))
                return i;
        }
        return 0;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        tinklerAtr02EditText.setText(Utils.dateToString(cal.getTime(), "LLL yyyy"));
    }

    private int reverseTypeArray(String type) {
        for (int i = 0; i < mTinklerTypeNamesArray.length; i++) {
            if (type.equals(mTinklerTypeNamesArray[i])) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onCompleteEdit(boolean success) {
        if (success) {
            Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show();
            //Set the data to be updated when headed back to the fragment
            Intent intent = new Intent();
            Bundle changedTinkler = new Bundle();
            changedTinkler.putString("TINKLER_NAME", tinklerNameEditText.getText().toString());
            changedTinkler.putInt("POS", tinklerPos);
            changedTinkler.putParcelable("TINKLER_IMAGE", tinklerImageView.getDrawingCache());
            intent.putExtras(changedTinkler);
            setResult(RESULT_OK , intent);
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
            mTinklerTypeNamesArray = new String[tinklerTypes.size()];
            mTinklerTypes = tinklerTypes;

            //populate the string array of types
            for(int i =0; i<tinklerTypes.size(); i++) {
                mTinklerTypeNamesArray[i] = tinklerTypes.get(i).getName();
            }

        } else {
            Toast.makeText(this, "Oops..", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCompleteGetLocalTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success) {
        if (success) {
            mTinklerTypeNamesArray = new String[tinklerTypes.size()];
            mTinklerTypes = tinklerTypes;
            //populate the string array of types
            for(int i =0; i<tinklerTypes.size(); i++) {
                mTinklerTypeNamesArray[i] = tinklerTypes.get(i).getName();
            }

        } else {
            Toast.makeText(this, "Oops..", Toast.LENGTH_LONG).show();
        }

    }
}
