package api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Conversation;
import model.MessageType;
import model.Tinkler;
import model.TinklerType;

public class QCApi {

    public static ArrayList<Tinkler> createTinklerObj(List<ParseObject> objects) {
        final ArrayList<Tinkler> tinklers = new ArrayList<Tinkler>();

        for (ParseObject object : objects) {
            Tinkler tinkler = new Tinkler();
            tinkler.setId(object.getObjectId());
            tinkler.setName(object.getString("name"));
            tinkler.setType(object.getParseObject("type"));
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

        return tinklers;
    }

    public static Tinkler getTinkler(String tinklerId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");
        query.fromPin("pinnedTinklers");
        Tinkler tinkler = new Tinkler();
        try {
            ParseObject object = query.get(tinklerId);

            tinkler.setId(object.getObjectId());
            tinkler.setName(object.getString("name"));
            tinkler.setType(object.getParseObject("type"));
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

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tinkler;
    }


    public static void getOnlineTinklers(final GetOnlineTinklersCallback callback) {

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.include("type");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // The find succeeded.
                    System.out.println("Successfully retrieved" + objects.size() + " tinklers");

                    //Unpin previous objects and then pin new collected ones
                    final List<ParseObject> localobjects = objects;
                    ParseObject.unpinAllInBackground("pinnedTinklers", new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject.pinAllInBackground("pinnedTinklers", localobjects);
                            } else {
                                System.out.println("Error pinning tinklers: " + e.getMessage());
                            }
                        }
                    });

                    callback.onCompleteGetOnlineTinklers(createTinklerObj(objects), true);

                } else {
                    System.out.println("Error getting online tinklers: " + e.getMessage());
                    callback.onCompleteGetOnlineTinklers(null, false);
                }
            }
        });
    }

    public static void getLocalTinklers(final GetLocalTinklersCallback callback) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.include("type");
        query.orderByDescending("createdAt");
        query.fromPin("pinnedTinklers");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // The find succeeded.
                    System.out.println("Successfully retrieved" + objects.size() + " tinklers");

                    callback.onCompleteGetLocalTinklers(createTinklerObj(objects), true);
                } else {
                    System.out.println("Error getting online tinklers: " + e.getMessage());
                    callback.onCompleteGetLocalTinklers(null, false);
                }
            }
        });
    }

    public static ArrayList<Conversation> createConversationObj(List<ParseObject> objects) {
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
            if (conversation.getStarterUser().getObjectId() == ParseUser.getCurrentUser().getObjectId()) {
                conversation.setToUser(object.getParseUser("talkingToUser"));
                conversation.setWasDeleted(object.getBoolean("wasDeletedByStarter"));
                conversation.setIsLocked(object.getBoolean("isLockedByStarter"));
                conversation.setHasUnreadMsg(object.getBoolean("starterHasUnreadMsgs"));
                conversation.setHasSentMsg(object.getBoolean("starterHasSentMsg"));
            } else {
                conversation.setToUser(object.getParseUser("starterUser"));
                conversation.setWasDeleted(object.getBoolean("wasDeletedByTo"));
                conversation.setIsLocked(object.getBoolean("isLockedByTo"));
                conversation.setHasUnreadMsg(object.getBoolean("toHasUnreadMsgs"));
                conversation.setHasSentMsg(object.getBoolean("toHasSentMsg"));
            }

            //Check blocked conversations and deleted conversations
            if (!object.getBoolean("isBlocked") && !conversation.getWasDeleted()) {
                conversations.add(conversation);
            } else {
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
                            } else {
                                System.out.println("Error pinning conversations: " + e.getMessage());
                            }
                        }
                    });

                    callback.onCompleteGetOnlineConversations(createConversationObj(objects), true);
                } else {
                    System.out.println("Error getting online conversations: " + e.getMessage());
                    callback.onCompleteGetOnlineConversations(null, false);
                }
            }
        });
    }

    public static void getLocalConversations(final GetLocalConversationsCallback callback) {

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
        myConv.fromPin("pinnedConversations");
        myConv.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //The find succeeded.
                    System.out.println("Successfully retrieved" + objects.size() + " conversations");

                    callback.onCompleteGetLocalConversations(createConversationObj(objects), true);
                } else {
                    System.out.println("Error getting local conversations: " + e.getMessage());
                    callback.onCompleteGetLocalConversations(null, false);
                }
            }
        });
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

            if (tinkler.getTinkler() != null){
                tinklerObject.put("qrCode", tinkler.getTinkler());
            }

            if (tinkler.getTinklerQRCodeKey() != 0) {
                tinklerObject.put("qrCodeKey", tinkler.getTinklerQRCodeKey());
            }

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
        myMsgTypes.include("type");
        myMsgTypes.orderByAscending("createdAt");
        myMsgTypes.setLimit(30);
        myMsgTypes.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground("pinnedMsgTypes", objects, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject.pinAllInBackground("pinnedMsgTypes", objects);
                            } else {
                                System.out.println(e);
                            }
                        }
                    });

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

    public static void getLocalMessageTypes(final GetAllLocalMessageTypesCallback callback) {
        final ArrayList<MessageType> msgTypes = new ArrayList<MessageType>();

        final ParseQuery<ParseObject> myMsgTypes = ParseQuery.getQuery("MessageType");
        myMsgTypes.include("type");
        myMsgTypes.orderByAscending("createdAt");
        myMsgTypes.fromPin("pinnedMsgTypes");
        myMsgTypes.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    for (ParseObject object : objects) {
                        MessageType msgType = new MessageType();
                        msgType.setId(object.getObjectId());
                        msgType.setName(object.getString("text"));
                        msgType.setTinklerType(object.getParseObject("type"));

                        msgTypes.add(msgType);
                    }

                    callback.onCompleteGetAllLocalMessageTypes(msgTypes, true);
                } else {
                    callback.onCompleteGetAllLocalMessageTypes(null, true);
                }
            }
        });
    }

    public static void getOnlineTinklerTypes(final GetOnlineTinklerTypesCallback callback) {
        final ParseQuery<ParseObject> myTinklerTypes = ParseQuery.getQuery("TinklerType");
        myTinklerTypes.orderByAscending("createdAt");

        try {
            //Unpin previous objects and then pin new collected ones
            final List<ParseObject> onlineobjects = myTinklerTypes.find();
            ParseObject.unpinAll("pinnedTinklerTypes");
            ParseObject.pinAll("pinnedTinklerTypes", onlineobjects);

            callback.onCompleteGetOnlineTinklerTypes(createTinklerTypeObj(onlineobjects),true);
        } catch (ParseException e) {
            e.printStackTrace();
            callback.onCompleteGetOnlineTinklerTypes(null, false);
        }
    }

    public static void getLocalTinklerTypes(final GetLocalTinklerTypesCallback callback){
        final ParseQuery<ParseObject> myTinklerTypes = ParseQuery.getQuery("TinklerType");
        myTinklerTypes.orderByAscending("createdAt");
        myTinklerTypes.fromPin("pinnedTinklerTypes");

        try {
            final List<ParseObject> localobjects = myTinklerTypes.find();
            callback.onCompleteGetLocalTinklerTypes(createTinklerTypeObj(localobjects), true);
        } catch (ParseException e) {
            e.printStackTrace();
            callback.onCompleteGetLocalTinklerTypes(null, false);
        }
    }

    public static ArrayList<TinklerType> createTinklerTypeObj(List<ParseObject> objects){
        final ArrayList<TinklerType> typeNames = new ArrayList<TinklerType>();

        for (ParseObject object : objects) {
            TinklerType tinklerType = new TinklerType();
            tinklerType.setId(object.getObjectId());
            tinklerType.setName(object.getString("typeName"));
            typeNames.add(tinklerType);
        }

        return typeNames;
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

    public static void validateQrCode(final String objectId, final int objectKey, final ValidateQRCodeCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tinkler");
        query.include("owner");

        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseRelation relation = parseObject.getRelation("ban");
                    ParseQuery<ParseObject> blockedQuery = relation.getQuery();

                    blockedQuery.whereEqualTo("email", ParseUser.getCurrentUser().getUsername());
                    blockedQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> banObjects, ParseException e) {
                            if (e == null) {
                                ParseUser user = (ParseUser) parseObject.get("owner");
                                if (ParseUser.getCurrentUser().getUsername().equals(user.getUsername())) {
                                    callback.onValidateQRCode(true, false, false, false, true);
                                } else if (banObjects.size() > 0) {
                                    callback.onValidateQRCode(true, false, false, true, false);
                                } else if ((int) parseObject.get("qrCodeKey") != objectKey) {
                                    callback.onValidateQRCode(true, false, false, false, false);
                                } else if ((int) parseObject.get("qrCodeKey") == objectKey && (boolean) user.get("allowCustomMsg") == true) {
                                    callback.onValidateQRCode(true, true, true, false, false);
                                } else if ((int) parseObject.get("qrCodeKey") == objectKey && (boolean) user.get("allowCustomMsg") == false) {
                                    callback.onValidateQRCode(true, true, false, false, false);
                                }
                            } else {
                                callback.onValidateQRCode(true, false, false, false, false);
                            }
                        }
                    });
                } else {
                    callback.onValidateQRCode(true, false, false, false, false);
                }
            }
        });
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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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

    public interface ValidateQRCodeCallback {
        void onValidateQRCode(boolean success, boolean isValidated, boolean allowCustom, boolean isBlocked, boolean isSelfTinkler);
    }

    public interface GetOnlineTinklersCallback {
        void onCompleteGetOnlineTinklers(ArrayList<Tinkler> tinklers, boolean success);
    }

    public interface GetLocalTinklersCallback {
        void onCompleteGetLocalTinklers(ArrayList<Tinkler> tinklers, boolean success);
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

    public interface GetAllLocalMessageTypesCallback {
        void onCompleteGetAllLocalMessageTypes(ArrayList<MessageType> messageTypes, boolean success);
    }

    public interface GetAllTinklerTypesCallback {
        void onCompleteGetAllTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success);
    }

    public interface GetLocalTinklerTypesCallback {
        void onCompleteGetLocalTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success);
    }

    public interface GetOnlineTinklerTypesCallback {
        void onCompleteGetOnlineTinklerTypes(ArrayList<TinklerType> tinklerTypes, boolean success);
    }

}
