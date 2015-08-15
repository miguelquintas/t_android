package com.tinkler.it;

import java.util.ArrayList;

import model.Tinkler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;

import api.QCApi;
import api.QCApi.GetOnlineTinklersCallback;
import api.QCApi.GetLocalTinklersCallback;
import api.Utils;

public class ProfileFragmentActivity extends Fragment implements GetOnlineTinklersCallback, GetLocalTinklersCallback {

	public static String TINKLER = "TINKLER";
    public static String POS = "POS";
	static final int PICK_TINKLER_EDIT = 1;  // The request code
    static final int PICK_TINKLER_ADD = 2;  // The request code
	private ListView mTinklersListView;
	private Button mAddTinklerButton;

	private ArrayList<Tinkler> mTinklers;
	private TinklersAdapter mAdapter;

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		//Check internet connection
		if(QCApi.isOnline(getActivity())){
			QCApi.getOnlineTinklers(this);
		}else{
			QCApi.getLocalTinklers(this);
		}

		initViews(view);
		initListeners();

		return view;
	}

	private void initViews(View view) {

		mTinklersListView = (ListView) view.findViewById(R.id.vehicles_list_view);
		mAddTinklerButton = (Button) view.findViewById(R.id.add_vehicle_button);

	}

	private void initListeners() {

		mTinklersListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Tinkler tinkler = mTinklers.get(position);

                Intent intent = new Intent(getActivity(), EditTinklerActivity.class);
				intent.putExtra(TINKLER, tinkler.getId());
                intent.putExtra(POS, position);
                startActivityForResult(intent, PICK_TINKLER_EDIT);
			}
		});

		mAddTinklerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AddTinklerActivity.class);
				startActivityForResult(intent, PICK_TINKLER_ADD);
			}
		});
	}

	@SuppressLint("ViewHolder")
	public class TinklersAdapter extends ArrayAdapter<Tinkler> {

		private final Context context;
		private final ArrayList<Tinkler> tinklers;

		public TinklersAdapter(Context context, ArrayList<Tinkler> tinklers) {
			super(context, R.layout.tinkler_row, tinklers);
			this.context = context;
			this.tinklers = tinklers;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView name;
			final ParseImageView picture;

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.tinkler_row, parent, false);

			name = (TextView) rowView.findViewById(R.id.name);
			picture = (ParseImageView) rowView.findViewById(R.id.image);

			Tinkler tinkler = tinklers.get(position);
			name.setText(tinkler.getName());
			ParseFile image = tinkler.getImage();
			picture.setParseFile(image);
			picture.loadInBackground();

			return rowView;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//If the results come from the EDIT menu
		if (requestCode == PICK_TINKLER_EDIT) {
			// Make sure the request was successful
			if (resultCode == Activity.RESULT_OK) {
                Bundle receivedData = data.getExtras();
                //Set the updated Tinkler Name
                mTinklers.get(receivedData.getInt("POS")).setName(receivedData.getString("TINKLER_NAME"));

                mAdapter.notifyDataSetChanged();
                Log.d("Received Data", "Updated Name:" + receivedData.getString("TINKLER_NAME"));
                Log.d("Received Data", "Tinkler Position:" + receivedData.getInt("POS"));
			}
		}else if(requestCode == PICK_TINKLER_ADD) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {}
        }

	}

	@Override
	public void onCompleteGetOnlineTinklers(ArrayList<Tinkler> tinklers, boolean success) {
		if (success) {
			mTinklers = tinklers;

			mAdapter = new TinklersAdapter(getActivity(), tinklers);
			mTinklersListView.setAdapter(mAdapter);
		} else {
			Toast.makeText(getActivity(), "Oops..", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onCompleteGetLocalTinklers(ArrayList<Tinkler> tinklers, boolean success) {
		if (success) {
			mTinklers = tinklers;

			mAdapter = new TinklersAdapter(getActivity(), tinklers);
			mTinklersListView.setAdapter(mAdapter);
		} else {
			Toast.makeText(getActivity(), "Oops..", Toast.LENGTH_LONG).show();
		}

	}
}
