package api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Conversation;
import model.Message;
import model.MessageType;
import model.Tinkler;
import model.TinklerType;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class QCApi {

	public static void getAllTinklers(final GetAllTinklersCallback callback) {

		final ArrayList<Tinkler> tinklers = new ArrayList<Tinkler>();

		final ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");
		query.whereEqualTo("owner", ParseUser.getCurrentUser());
		query.include("type");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() == 0) {
						query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
					} else {
						query.setCachePolicy(CachePolicy.NETWORK_ONLY);
					}

					for (ParseObject object : objects) {
						Tinkler tinkler = new Tinkler();
						tinkler.setId(object.getObjectId());
						tinkler.setName(object.getString("name"));
						tinkler.setType(object.getParseObject("vehicleType"));
						tinkler.setOwner(object.getParseUser("owner"));
						tinkler.setVehiclePlate(object.getString("vehiclePlate"));
						tinkler.setVehicleYear(object.getDate("vehicleYear"));
						tinkler.setPetAge(object.getDate("petAge"));
						tinkler.setPetBreed(object.getString("petBreed"));
						tinkler.setColor(object.getString("color"));
						tinkler.setBrand(object.getString("brand"));
						tinkler.setLocationCity(object.getString("locationCity"));
						tinkler.setEventDate(object.getDate("eventDate"));
						tinkler.setAdType(object.getString("adType"));
						tinkler.setImage(object.getParseFile("picture"));
						tinkler.setTinkler(object.getParseFile("qrCode"));
						tinkler.setTinklerQRCodeKey(object.getInt("qrCodeKey"));
						tinklers.add(tinkler);
					}

					callback.onCompleteGetAllTinklers(tinklers, true);
				} else {
					callback.onCompleteGetAllTinklers(null, false);
				}
			}
		});
	}

	public static ArrayList<Conversation> createConversationObj(List<ParseObject> objects){
		final ArrayList<Conversation> conversations = new ArrayList<Conversation>();

		for (ParseObject object : objects) {
			//Create Message Object
			Conversation conversation = new Conversation();

			conversation.setConversationId(object.getObjectId());
			conversation.setStarterUser(object.getParseUser("starterUser"));
			conversation.setToTinkler(object.getParseObject("talkingToTinkler"));
			conversation.setLastSentDate(object.getDate("lastMessageDate"));

			//If the current user started this conversation then set the talkingToUser, wasDeleted, isLocked, hasUnreadMsgs
			// and hasSentMsgs values accordingly
			if(conversation.getStarterUser().getObjectId() == ParseUser.getCurrentUser().getObjectId()){
				conversation.setToUser(object.getParseUser("talkingToUser"));
				conversation.setWasDeleted(object.getBoolean("wasDeletedByStarter"));
				conversation.setIsLocked(object.getBoolean("isLockedByStarter"));
				conversation.setHasUnreadMsg(object.getBoolean("starterHasUnreadMsgs"));
				conversation.setHasSentMsg(object.getBoolean("starterHasSentMsg"));
			}else{
				conversation.setToUser(object.getParseUser("starterUser"));
				conversation.setWasDeleted(object.getBoolean("wasDeletedByTo"));
				conversation.setIsLocked(object.getBoolean("isLockedByTo"));
				conversation.setHasUnreadMsg(object.getBoolean("toHasUnreadMsgs"));
				conversation.setHasSentMsg(object.getBoolean("toHasSentMsg"));
			}

			//Check blocked conversations and deleted conversations
			if(!object.getBoolean("isBlocked") && !conversation.getWasDeleted()){
				conversations.add(conversation);
			}else{
				System.out.println("This conversation is blocked");
			}
		}

		return conversations;
	}

	public static void getOnlineConversations(final GetOnlineConversationsCallback callback) {
		//Query to get the started conversations by the current user
		ParseQuery<ParseObject> startedConv = ParseQuery.getQuery("Conversation");
		startedConv.whereEqualTo("starterUser", ParseUser.getCurrentUser());

		//Query to get the conversations started by another user
		ParseQuery<ParseObject> toConv = ParseQuery.getQuery("Conversation");
		toConv.whereEqualTo("talkingToUser", ParseUser.getCurrentUser());

		//Or query to get all this user's conversations
		List<ParseQuery<ParseObject>> q = new ArrayList<ParseQuery<ParseObject>>();
		q.add(startedConv);
		q.add(toConv);

		final ParseQuery<ParseObject> myConv = ParseQuery.or(q);

		myConv.orderByDescending("lastMessageDate");
		myConv.include("talkingToTinkler");
		myConv.include("starterUser");
		myConv.include("talkingToUser");
		myConv.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					//The find succeeded.
					System.out.println("Successfully retrieved" + objects.size() + " conversations");

					//Unpin previous objects and then pin new collected ones
					final List<ParseObject> localobjects = objects;
					ParseObject.unpinAllInBackground("pinnedConversations", new DeleteCallback() {
						public void done(ParseException e) {
							if (e == null) {
								ParseObject.pinAllInBackground("pinnedConversations", localobjects);
							}else {
								System.out.println("Error pinning conversations: " + e.getMessage());
							}
						}
					});

					callback.onCompleteGetOnlineConversations(createConversationObj(objects), true);
				} else {
					System.out.println("Error getting conversations: " + e.getMessage());
					callback.onCompleteGetOnlineConversations(null, false);
				}
			}
		});
	}

	public static void getAllConversations(final GetAllConversationsCallback callback) {
		final ArrayList<Conversation> conversations = new ArrayList<Conversation>();

		ParseQuery<ParseObject> sentMsgs = ParseQuery.getQuery("Message");
		sentMsgs.whereEqualTo("from", ParseUser.getCurrentUser());
		
		ParseQuery<ParseObject> receivedMsgs = ParseQuery.getQuery("Message");
		receivedMsgs.whereEqualTo("to", ParseUser.getCurrentUser());

		List<ParseQuery<ParseObject>> q = new ArrayList<ParseQuery<ParseObject>>();
		q.add(sentMsgs);
		q.add(receivedMsgs);

		final ParseQuery<ParseObject> myMsgs = ParseQuery.or(q);
		myMsgs.whereEqualTo("deletedByUser", false);
		myMsgs.orderByAscending("createdAt");
		myMsgs.include("type");
		myMsgs.include("tinkler");
		myMsgs.include("from");
		myMsgs.include("to");
		
		myMsgs.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() == 0) {
						myMsgs.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
					} else {
						myMsgs.setCachePolicy(CachePolicy.NETWORK_ONLY);
					}

					for (ParseObject object : objects) {
						Message message = new Message();
						message.setId(object.getObjectId());
						message.setType(object.getParseObject("type"));
						message.setText(object.getString("customText"));
						message.setFrom(object.getParseUser("from"));
						message.setTo(object.getParseUser("to"));
						message.setSentDate(object.getCreatedAt());
						message.setTargetTinkler(object.getParseObject("tinkler"));

						boolean isNew;

						if (conversations.size() == 0) {
							conversations.add(createNewConversation(message));
						} else {
							isNew = false;

							for (Conversation conversation : conversations) {
								if (message.getFrom().getUsername().equals(conversation.getToUser().getUsername()) || message.getTo().getUsername().equals(conversation.getToUser().getUsername())) {
									if (message.getTargetTinkler().getObjectId().equals(conversation.getToTinkler().getObjectId())) {
										conversation.getConversationMsgs().add(message);
										isNew = false;
									} else {
										isNew = true;
									}
								} else {
									isNew = true;
								}
							}

							if (isNew) {
								conversations.add(createNewConversation(message));
							}
						}

					}

					callback.onCompleteGetAllConversations(conversations, true);
				} else {
					callback.onCompleteGetAllConversations(null, false);
				}
			}
		});
	}

	public static Conversation createNewConversation(Message message) {
		Conversation newConversation = new Conversation();
		ArrayList<Message> messages = new ArrayList<Message>();

		if (ParseUser.getCurrentUser().getUsername().equals(message.getFrom().getUsername())) {
			newConversation.setToUser(message.getTo());
		} else {
			newConversation.setToUser(message.getFrom());
		}

		newConversation.setToTinkler(message.getTargetTinkler());
		messages.add(message);
		newConversation.setConversationMsgs(messages);
		newConversation.setLastSentDate(message.getSentDate());

		return newConversation;
	}

	public static void addTinkler(final Tinkler tinkler, final AddTinklerCallback callback) {

		final int qrCodeKey = 1;

		ParseObject tinklerObject = new ParseObject("Tinkler");
		tinklerObject.put("owner", ParseUser.getCurrentUser());
		tinklerObject.put("name", tinkler.getName());
		tinklerObject.put("type", tinkler.getType());

		if (tinkler.getVehiclePlate() != null) {
			tinklerObject.put("vehiclePlate", tinkler.getVehiclePlate());
		}

		if (tinkler.getVehicleYear() != null) {
			tinklerObject.put("vehicleYear", tinkler.getVehicleYear());
		}

		if (tinkler.getPetBreed() != null) {
			tinklerObject.put("petBreed", tinkler.getPetBreed());
		}

		if (tinkler.getPetAge() != null) {
			tinklerObject.put("petAge", tinkler.getPetAge());
		}

		if (tinkler.getBrand() != null) {
			tinklerObject.put("brand", tinkler.getBrand());
		}

		if (tinkler.getColor() != null) {
			tinklerObject.put("color", tinkler.getColor());
		}

		if (tinkler.getLocationCity() != null) {
			tinklerObject.put("locationCity", tinkler.getLocationCity());
		}

		if (tinkler.getEventDate() != null) {
			tinklerObject.put("eventDate", tinkler.getEventDate());
		}

		if (tinkler.getAdType() != null) {
			tinklerObject.put("adType", tinkler.getAdType());
		}

		if (tinkler.getImage() != null) {
			tinklerObject.put("picture", tinkler.getImage());
		}

		tinklerObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {

					ParseQuery<ParseObject> addedTinkler = ParseQuery.getQuery("Tinkler");
					addedTinkler.whereEqualTo("owner", ParseUser.getCurrentUser());
					addedTinkler.whereEqualTo("name", tinkler.getName());
					addedTinkler.getFirstInBackground(new GetCallback<ParseObject>() {

						@Override
						public void done(final ParseObject newTinkler, ParseException e) {
							if (e == null) {

								// TODO Save QRCode image in gallery
								/*
								 * UIImage *qrCodeImage = [QCQrCode generateQRCode:newTinkler.objectId :qrCodeKey]; UIImageWriteToSavedPhotosAlbum(qrCodeImage, nil, nil, nil);
								 * 
								 * // TODO Save the QR-Code and QR-Code Key in the DB and send email NSData *qrCodeimageData = UIImageJPEGRepresentation(qrCodeImage, 0.05f); PFFile *qrCodeimageFile =
								 * [PFFile fileWithName:@"qrCode.jpg" data:qrCodeimageData];
								 */
								ParseFile qrCodeImageFile = new ParseFile(null);
								newTinkler.add("qrCode", qrCodeImageFile);
								newTinkler.add("qrCodeKey", qrCodeKey);
								newTinkler.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										if (e == null) {
											sendEmail(newTinkler.getObjectId());
											callback.onCompleteAdd(true);
										} else {
											callback.onCompleteAdd(false);
										}
									}
								});
							} else {
								callback.onCompleteAdd(false);
							}
						}
					});
				} else {
					callback.onCompleteAdd(false);
				}
			}
		});
	}

	public static void editTinkler(final Tinkler tinkler, final EditTinklerCallback callback) {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");
		ParseObject tinklerObject;

		try {
			tinklerObject = query.get(tinkler.getId());
			tinklerObject.put("name", tinkler.getName());
			tinklerObject.put("type", tinkler.getType());

			if (tinkler.getVehiclePlate() != null) {
				tinklerObject.put("vehiclePlate", tinkler.getVehiclePlate());
			}

			if (tinkler.getVehicleYear() != null) {
				tinklerObject.put("vehicleYear", tinkler.getVehicleYear());
			}

			if (tinkler.getPetBreed() != null) {
				tinklerObject.put("petBreed", tinkler.getPetBreed());
			}

			if (tinkler.getPetAge() != null) {
				tinklerObject.put("petAge", tinkler.getPetAge());
			}

			if (tinkler.getBrand() != null) {
				tinklerObject.put("brand", tinkler.getBrand());
			}

			if (tinkler.getColor() != null) {
				tinklerObject.put("color", tinkler.getColor());
			}

			if (tinkler.getLocationCity() != null) {
				tinklerObject.put("locationCity", tinkler.getLocationCity());
			}

			if (tinkler.getEventDate() != null) {
				tinklerObject.put("eventDate", tinkler.getEventDate());
			}

			if (tinkler.getAdType() != null) {
				tinklerObject.put("adType", tinkler.getAdType());
			}

			if (tinkler.getImage() != null) {
				tinklerObject.put("picture", tinkler.getImage());
			}

			tinklerObject.put("qrCode", tinkler.getTinkler());
			tinklerObject.put("qrCodeKey", tinkler.getTinklerQRCodeKey());

			tinklerObject.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						callback.onCompleteEdit(true);
					} else {
						callback.onCompleteEdit(false);
					}
				}
			});
		} catch (ParseException e2) {
			callback.onCompleteEdit(false);
		}
	}

	public static void deleteTinkler(final Tinkler tinkler, final DeleteTinklerCallback callback) {

		final ParseObject tinklerToDelete = ParseObject.createWithoutData("Tinkler", tinkler.getId());

		// TODO
		ParseQuery<ParseObject> sentMsgs = ParseQuery.getQuery("Message");
		sentMsgs.whereEqualTo("from", ParseUser.getCurrentUser());
		sentMsgs.whereExists("customText");
		sentMsgs.whereEqualTo("tinkler", tinklerToDelete);

		ParseQuery<ParseObject> receivedMsgs = ParseQuery.getQuery("Message");
		receivedMsgs.whereEqualTo("to", ParseUser.getCurrentUser());
		sentMsgs.whereEqualTo("tinkler", tinklerToDelete);

		List<ParseQuery<ParseObject>> q = new ArrayList<ParseQuery<ParseObject>>();
		q.add(sentMsgs);
		q.add(receivedMsgs);

		ParseQuery<ParseObject> tinklerMsgs = ParseQuery.or(q);
		tinklerMsgs.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject object : objects) {
						object.deleteEventually();
					}

					tinklerToDelete.deleteEventually();

					callback.onCompleteDelete(true);
				} else {
					callback.onCompleteDelete(false);
				}
			}
		});
	}

	public static void getMessageTypes(final GetAllMessageTypesCallback callback) {
		final ArrayList<MessageType> msgTypes = new ArrayList<MessageType>();

		final ParseQuery<ParseObject> myMsgTypes = ParseQuery.getQuery("MessageType");
		myMsgTypes.orderByAscending("createdAt");
		myMsgTypes.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() == 0) {
						myMsgTypes.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
					} else {
						myMsgTypes.setCachePolicy(CachePolicy.NETWORK_ONLY);
					}

					for (ParseObject object : objects) {
						MessageType msgType = new MessageType();
						msgType.setId(object.getObjectId());
						msgType.setName(object.getString("text"));
						msgType.setTinklerType(object.getParseObject("type"));

						msgTypes.add(msgType);
					}

					callback.onCompleteGetAllMessageTypes(msgTypes, true);
				} else {
					callback.onCompleteGetAllMessageTypes(null, false);
				}
			}
		});
	}

	public static void getAllTinklerTypes(final GetAllTinklerTypesCallback callback) {
		final ArrayList<TinklerType> typeNames = new ArrayList<TinklerType>();

		final ParseQuery<ParseObject> myTinklerTypes = ParseQuery.getQuery("TinklerType");
		myTinklerTypes.orderByAscending("createdAt");
		myTinklerTypes.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() == 0) {
						myTinklerTypes.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
					} else {
						myTinklerTypes.setCachePolicy(CachePolicy.NETWORK_ONLY);
					}

					for (ParseObject object : objects) {
						TinklerType tinklerType = new TinklerType();
						tinklerType.setId(object.getObjectId());
						tinklerType.setName(object.getString("typeName"));

						typeNames.add(tinklerType);
					}

					callback.onCompleteGetAllTinklerTypes(typeNames, true);
				} else {
					callback.onCompleteGetAllTinklerTypes(null, false);
				}
			}
		});
	}

	public static void confirmEmail(final String email, final VerifyEmailCallback callback) {

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("email", email);
		query.getFirstInBackground(new GetCallback<ParseUser>() {

			@Override
			public void done(ParseUser object, ParseException e) {
				try {
					object.refresh();
					if (object.getBoolean("emailVerified") == true) {
						callback.onCompleteVerify(true);
					} else {
						callback.onCompleteVerify(false);
					}
				} catch (ParseException pe) {
					callback.onCompleteVerify(false);
				}
			}
		});
	}

	public static void editProfile(String name, boolean customMsg, EditProfileCallback callback) {
		ParseUser.getCurrentUser().add("name", name);
		ParseUser.getCurrentUser().add("allowCustomMsg", customMsg); // TODO
		ParseUser.getCurrentUser().saveInBackground();

		callback.onCompleteEditProfile(true);
	}

	public static boolean validateQrCode(String objectId, int objectKey) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");

		try {
			ParseObject objectToValidate = query.get(objectId);
			int key = objectToValidate.getInt("qrCodeKey");

			if (key == objectKey) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void sendEmail(String objectId) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("objectId", objectId);
		ParseCloud.callFunctionInBackground("sendEmailQRCode", params, new FunctionCallback<String>() {

			@Override
			public void done(String object, ParseException e) {
				if (e == null) {
					// success
				} else {
					// error
				}
			}
		});
	}

	// Callbacks da vida
	public interface AddTinklerCallback {
		void onCompleteAdd(boolean success);
	}

	public interface EditTinklerCallback {
		void onCompleteEdit(boolean success);
	}

	public interface DeleteTinklerCallback {
		void onCompleteDelete(boolean success);
	}

	public interface VerifyEmailCallback {
		void onCompleteVerify(boolean success);
	}

	public interface EditProfileCallback {
		void onCompleteEditProfile(boolean success);
	}

	public interface GetAllTinklersCallback {
		void onCompleteGetAllTinklers(ArrayList<Tinkler> tinklers, boolean success);
	}

	public interface GetAllConversationsCallback {
		void onCompleteGetAllConversations(ArrayList<Conversation> conversations, boolean success);
	}

	public interface GetOnlineConversationsCallback {
		void onCompleteGetOnlineConversations(ArrayList<Conversation> conversations, boolean success);
	}

	public interface GetLocalConversationsCallback {
		void onCompleteGetLocalConversations(ArrayList<Conversation> conversations, boolean success);
	}

	public interface GetAllMessageTypesCallback {
		void onCompleteGetAllMessageTypes(ArrayList<MessageType> messageTypes, boolean success);
	}

	public interface GetAllTinklerTypesCallback {
		void onCompleteGetAllTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success);
	}
}
