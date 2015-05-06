package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Conversation implements Serializable{

	private static final long serialVersionUID = 3L;

	private String conversationId;
	private ParseUser toUser;
	private ParseUser starterUser;
	private ParseObject toTinkler;
	private ArrayList<Message> conversationMsgs;
	private Date lastSentDate;
	private Boolean hasUnreadMsg;
	private Boolean hasSentMsg;
	private Boolean wasDeleted;
	private Boolean isLocked;

	public String getConversationId(){return conversationId;}

	public void setConversationId(String conversationId){ this.conversationId = conversationId;}

	public ParseUser getToUser() {
		return toUser;
	}

	public void setToUser(ParseUser toUser) {
		this.toUser = toUser;
	}

	public ParseUser getStarterUser() {
		return starterUser;
	}

	public void setStarterUser(ParseUser starterUser) {
		this.starterUser = starterUser;
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

	public Boolean getHasUnreadMsg() {return hasUnreadMsg;}

	public void setHasUnreadMsg(Boolean hasUnreadMsg) { this.hasUnreadMsg = hasUnreadMsg;}

	public Boolean getHasSentMsg() {return hasSentMsg;}

	public void setHasSentMsg(Boolean hasSentMsg) { this.hasSentMsg = hasSentMsg;}

	public Boolean getWasDeleted() {return wasDeleted;}

	public void setWasDeleted(Boolean wasDeleted) { this.wasDeleted = wasDeleted;}

	public Boolean getIsLocked() {return isLocked;}

	public void setIsLocked(Boolean isLocked){ this.isLocked = isLocked;}
}
