package model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Tinkler implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private ParseUser owner;
	private ParseFile image;
	private ParseObject type;

	private String vehiclePlate;
	private Date vehicleYear;

	private String petBreed;
	private Date petAge;

	private String brand;
	private String color;

	private String locationCity;

	private Date eventDate;
	private String adType;

	private ParseFile tinkler;
	private int tinklerQRCodeKey;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParseUser getOwner() {
		return owner;
	}

	public void setOwner(ParseUser owner) {
		this.owner = owner;
	}

	public ParseFile getImage() {
		return image;
	}

	public void setImage(ParseFile image) {
		this.image = image;
	}

	public ParseObject getType() {
		return type;
	}

	public void setType(ParseObject type) {
		this.type = type;
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}

	public Date getVehicleYear() {
		return vehicleYear;
	}

	public void setVehicleYear(Date vehicleYear) {
		this.vehicleYear = vehicleYear;
	}

	public String getPetBreed() {
		return petBreed;
	}

	public void setPetBreed(String petBreed) {
		this.petBreed = petBreed;
	}

	public Date getPetAge() {
		return petAge;
	}

	public void setPetAge(Date petAge) {
		this.petAge = petAge;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public ParseFile getTinkler() {
		return tinkler;
	}

	public void setTinkler(ParseFile tinkler) {
		this.tinkler = tinkler;
	}

	public int getTinklerQRCodeKey() {
		return tinklerQRCodeKey;
	}

	public void setTinklerQRCodeKey(int tinklerQRCodeKey) {
		this.tinklerQRCodeKey = tinklerQRCodeKey;
	}

	public String getLocationCity() { return locationCity; }

	public void setLocationCity(String locationCity) { this.locationCity = locationCity;}

	public Date getEventDate() {return eventDate; }

	public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

	public String getAdType() { return adType;}

	public void setAdType(String adType) { this.adType = adType;}

}
