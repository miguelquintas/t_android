package com.tinkler.it;

import java.util.ArrayList;

import model.Tinkler;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.QCApi;
import api.QCApi.GetOnlineTinklersCallback;
import api.QCApi.GetLocalTinklersCallback;
import api.Utils;

public class ProfileFragmentActivity extends Fragment implements GetOnlineTinklersCallback, GetLocalTinklersCallback {

	public static String STATE = "STATE";

	public static String ADD_TINKLER = "ADD";
	public static String EDIT_TINKLER = "EDIT";
	public static String TINKLER = "TINKLER";

	private TextView mUserNameTextView;
	private ListView mTinklersListView;
	private Button mAddTinklerButton;

	private ArrayList<Tinkler> mTinklers;
	private TinklersAdapter mAdapter;

	@Override
	public void onStart() {
		super.onStart();

		//Check internet connection
		if(QCApi.isOnline()){
			System.out.println("Estou Online");
			QCApi.getOnlineTinklers(this);
		}else{
			System.out.println("Estou Offline");
			QCApi.getLocalTinklers(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		initViews(view);
		initListeners();

		return view;
	}

	private void initViews(View view) {

		mUserNameTextView = (TextView) view.findViewById(R.id.user_name_text_view);
		mTinklersListView = (ListView) view.findViewById(R.id.vehicles_list_view);
		mAddTinklerButton = (Button) view.findViewById(R.id.add_vehicle_button);

	}

	private void initListeners() {

		mTinklersListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Tinkler tinkler = mTinklers.get(position);

				Intent intent = new Intent(getActivity(), AddTinklerActivity.class);
				intent.putExtra(STATE, EDIT_TINKLER);
				intent.putExtra(TINKLER, tinkler);
				startActivity(intent);
			}
		});

		mAddTinklerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AddTinklerActivity.class);
				intent.putExtra(STATE, ADD_TINKLER);
				startActivity(intent);
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

			TextView brand;
			TextView plate;
			TextView date;
			ImageView picture;

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.tinkler_row, parent, false);

			brand = (TextView) rowView.findViewById(R.id.brand);
			plate = (TextView) rowView.findViewById(R.id.plate);
			date = (TextView) rowView.findViewById(R.id.date);
			picture = (ImageView) rowView.findViewById(R.id.image);

			Tinkler tinkler = tinklers.get(position);

			brand.setText(tinkler.getName());
			plate.setText(tinkler.getVehiclePlate());

			if(tinkler.getVehicleYear() != null)
				date.setText(Utils.dateToString(tinkler.getVehicleYear(), "LLL yyyy"));

			return rowView;
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
