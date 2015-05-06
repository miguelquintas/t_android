package model;

import com.parse.ParseObject;

public class MessageType {

	private String id;
	private String name;
	private ParseObject tinklerType;

	public String getId() { return id;}

	public void setId(String id) { this.id = id; }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParseObject getTinklerType() {
		return tinklerType;
	}

	public void setTinklerType(ParseObject tinklerType) { this.tinklerType = tinklerType;}
}
