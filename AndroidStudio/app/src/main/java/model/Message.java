package model;

import java.io.Serializable;
import java.util.Date;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Message implements Serializable{

	private static final long serialVersionUID = 2L;
	
	private String id;
	private ParseObject type;
	private String text;
	private ParseUser from;
	private ParseUser to;
	private ParseObject targetTinkler;
	private Date sentDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ParseObject getType() {
		return type;
	}

	public void setType(ParseObject type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ParseUser getFrom() {
		return from;
	}

	public void setFrom(ParseUser from) {
		this.from = from;
	}

	public ParseUser getTo() {
		return to;
	}

	public void setTo(ParseUser to) {
		this.to = to;
	}

	public ParseObject getTargetTinkler() {
		return targetTinkler;
	}

	public void setTargetTinkler(ParseObject targetTinkler) {
		this.targetTinkler = targetTinkler;
	}
	
	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

}
