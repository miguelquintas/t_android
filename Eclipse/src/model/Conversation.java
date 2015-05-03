package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Conversation implements Serializable{

	private static final long serialVersionUID = 3L;
	
	private ParseUser toUser;
	private ParseObject toTinkler;
	private ArrayList<Message> conversationMsgs;
	private Date lastSentDate;

	public ParseUser getToUser() {
		return toUser;
	}

	public void setToUser(ParseUser toUser) {
		this.toUser = toUser;
	}

	public ParseObject getToTinkler() {
		return toTinkler;
	}

	public void setToTinkler(ParseObject toTinkler) {
		this.toTinkler = toTinkler;
	}

	public ArrayList<Message> getConversationMsgs() {
		return conversationMsgs;
	}

	public void setConversationMsgs(ArrayList<Message> conversationMsgs) {
		this.conversationMsgs = conversationMsgs;
	}

	public Date getLastSentDate() {
		return lastSentDate;
	}

	public void setLastSentDate(Date lastSentDate) {
		this.lastSentDate = lastSentDate;
	}
}
