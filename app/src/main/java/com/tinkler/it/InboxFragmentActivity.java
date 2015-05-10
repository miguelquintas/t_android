package com.tinkler.it;

import java.util.ArrayList;

import model.Conversation;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.QCApi;
import api.QCApi.GetOnlineConversationsCallback;
import api.QCApi.GetLocalConversationsCallback;
import api.Utils;

public class InboxFragmentActivity extends Fragment implements GetOnlineConversationsCallback, GetLocalConversationsCallback {

	public static String MESSAGE = "MESSAGE";

	private ListView mMessagesListView;
	private ArrayList<Conversation> mConversations;
	private ConversationsAdapter mAdapter;

	@Override
	public void onStart() {
		super.onStart();

		//Check internet connection
		if(QCApi.isOnline(getActivity())){
			QCApi.getOnlineConversations(this);
		}else{
			QCApi.getLocalConversations(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inbox, container, false);

		initViews(view);
		initListeners();

		return view;
	}

	private void initViews(View view) {

		mMessagesListView = (ListView) view.findViewById(R.id.messages_list_view);
	}

	private void initListeners() {

		mMessagesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Conversation conversation = mConversations.get(position);

				Intent intent = new Intent(getActivity(), MessagesActivity.class);
				intent.putExtra(MESSAGE, conversation);
				startActivity(intent);
			}
		});
	}

	@SuppressLint("ViewHolder")
	public class ConversationsAdapter extends ArrayAdapter<Conversation> {

		private final Context context;
		private final ArrayList<Conversation> conversations;

		public ConversationsAdapter(Context context, ArrayList<Conversation> conversations) {
			super(context, R.layout.message_row, conversations);
			this.context = context;
			this.conversations = conversations;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView type;
			TextView date;

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.message_row, parent, false);

			type = (TextView) rowView.findViewById(R.id.type);
			date = (TextView) rowView.findViewById(R.id.date);

			Conversation conversation = conversations.get(position);

			type.setText(conversation.getToTinkler().get("name").toString());
			date.setText(Utils.dateToString(conversation.getLastSentDate(), " HH:mm dd/MM/yy"));

			return rowView;
		}
	}

	@Override
	public void onCompleteGetOnlineConversations(ArrayList<Conversation> conversations, boolean success) {
		if (success) {
			mConversations = conversations;

			mAdapter = new ConversationsAdapter(getActivity(), conversations);
			mMessagesListView.setAdapter(mAdapter);
		} else {
			Toast.makeText(getActivity(), "Oops..", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onCompleteGetLocalConversations(ArrayList<Conversation> conversations, boolean success) {
		if (success) {
			mConversations = conversations;

			mAdapter = new ConversationsAdapter(getActivity(), conversations);
			mMessagesListView.setAdapter(mAdapter);
		} else {
			Toast.makeText(getActivity(), "Oops..", Toast.LENGTH_LONG).show();
		}
	}

}
