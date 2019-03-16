package com.example.team10.personalbest.fitness;

import android.util.Log;

import com.example.team10.personalbest.ActivityMediator;
import com.example.team10.personalbest.FormatHelper;
import com.example.team10.personalbest.WalkDay;
import com.example.team10.personalbest.friend.StringAsObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

import static com.example.team10.personalbest.FormatHelper.reformatEmailForCloud;
import static com.example.team10.personalbest.FormatHelper.reformatEmailForUser;

/**
 * CloudProcessor class
 *
 * Used to upload/read data from Firebase cloud. All functions
 * in this class are static - no instantiation of a CloudProcessor
 * required. Can write user objects to the cloud. Can read in these
 * user objects via email or uid.
 *
 * Can also link UIDs to emails for later lookups, but currently that
 * is an operation only used by CloudProcessor itself.
 */
public class CloudProcessor {

    // Constant strings to indicate directories in database
    private static final String USER_DIR_KEY = "users";
    private static final String PERSONBEST_USER_COLLECTION_KEY = "users_stored";
    private static final String STORED_USERS_KEY = "activated_users";
    private static final String WALKDAY_COLLECTION_KEY ="walkdays";
    private static final String UPDATE_TIME_COLLECTION_KEY = "updatedsince";
    private static final String UPDATE_TIME_KEY = "lastUploadDate";
    private static final String FRIEND_DIR_KEY = "friends";
    private static final String FRIEND_LIST_KEY = "friendlist";
    private static final String MUTUAL = "mutual_friend";
    private static final String ONEDIRECTION = "attempt_friend";


    //private static final String USERS_DIR = "users";
    //private static final String UID_EMAIL_MAP_DIR = "emailToId";

    // Debug
    private static final String TAG = "CloudProcessor - ";

    // DataSnapshot to extract from
    private static DataSnapshot snapshot;
    private static WalkDay walkDay;

    /**
     * uploadWalkDay
     *
     * Uploads (overwrites) one walkday, implicitly call setLastUpLoadDate
     *
     * @param walkDay walkday to upload
     * @param email eamail used to distinguish user
     */
    public static void uploadWalkDay(WalkDay walkDay, String email){
        if(walkDay == null)
            Log.d(TAG,"null is input. Expect walkday");
        if(email == null){
            Log.d(TAG,"null is input. Expect email");
        }
        String date = walkDay.getDate().toString();

        //long time = walkDay.getString1().

        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(email))
                .collection(WALKDAY_COLLECTION_KEY)
                .document(date);

        database.set(walkDay)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        //Log.d(TAG, "Walkday successfully uploaded!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error uploading walkday", e);
            }
        });

    }


    /**
     * It is used for preloading, listener would assign appropriate value to it
     * @param date
     * @param email
     */
    public static void requestDay(LocalDate date, String email, boolean isUser){
        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(email))
                .collection(WALKDAY_COLLECTION_KEY)
                .document(date.toString());

        Task<DocumentSnapshot> task_documentSnapshot= database.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Log.d(TAG, "requesting walkDay of "+date.toString()+" snapshot doesn't exist, going to return new walkday");
                    if(isUser){
                        ActivityMediator.getUserWalkDays().put(date.toString(),new WalkDay(date.toString()));
                    }else{
                        ActivityMediator.getFriendWalkDays().put(date.toString(), new WalkDay(date.toString()));
                    }
                }

                WalkDay result = documentSnapshot.toObject(WalkDay.class);
                if(result == null) {
                    Log.d(TAG, "requesting WalkDay of "+date.toString()+" but got null object");
                    if(isUser){
                        ActivityMediator.getUserWalkDays().put(date.toString(),new WalkDay(date.toString()));
                    }else{
                        ActivityMediator.getFriendWalkDays().put(date.toString(), new WalkDay(date.toString()));
                    }
                    return;
                }
                else{
                    try{
                        int step = result.getStepCountDailyTotal();
                        if(isUser){
                            ActivityMediator.getUserWalkDays().put(date.toString(),result);
                        }else{
                            ActivityMediator.getFriendWalkDays().put(date.toString(), result);
                        }
                    }catch (Exception e){
                        Log.d(TAG, "after request, caught error, going to return new walkday");
                        if(isUser){
                            ActivityMediator.getUserWalkDays().put(date.toString(),new WalkDay(date.toString()));
                        }else{
                            ActivityMediator.getFriendWalkDays().put(date.toString(), new WalkDay(date.toString()));
                        }
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Log.d(TAG, "requesting WalkDay of "+date.toString()+" but FAIL THE TASK");
                if(isUser){
                    ActivityMediator.getUserWalkDays().put(date.toString(),new WalkDay(date.toString()));
                }else{
                    ActivityMediator.getFriendWalkDays().put(date.toString(), new WalkDay(date.toString()));
                }
            }
        });
    }

    /**
     * retrieveDay
     *
     * retrieve (but doesn't change) one walkday
     * should only been used for direct reads or assign to mediator's walkday
     *
     * @param date Local date of the specific date wanted
     * @param email eamail used to distinguish user
     */
    public static WalkDay retrieveDay(LocalDate date, String email){

        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(email))
                .collection(WALKDAY_COLLECTION_KEY)
                .document(date.toString());

        Task<DocumentSnapshot> task_documentSnapshot= database.get();
        /*
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setWalkDay(documentSnapshot.toObject(WalkDay.class));
            }
        });
        */
        while (!task_documentSnapshot.isComplete()){
            try{
                Thread.sleep(20);
            }catch (Exception e){

            }
        }
        if(task_documentSnapshot.isSuccessful())
            Log.d(TAG, "Task retrieving walkday is successful");
        else Log.d(TAG, "Task retrieving walkday is unsuccessful");
        try {
            DocumentSnapshot documentSnapshot = task_documentSnapshot.getResult();
            if (documentSnapshot == null) {
                Log.d(TAG, "unsuccessful reading walkDay");
                return null;
            } else {
                //Log.d(TAG, "successful reading walkDay");
                return documentSnapshot.toObject(WalkDay.class);
            }
        }catch (Exception e){
            Log.d(TAG, "retrieve Day TASK has error");
            return null;
        }

    }

    /*
    public static void helperWrite(String email){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(reformatEmailForCloud(email));
        database.child("tempArea").setValue("Write");
        Log.i(TAG, "helper write 1");
    }
    public static void helperWrite2(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(UID_EMAIL_MAP_DIR);
        database.child("tempArea").setValue("Write");
        Log.i(TAG, "helper write 2");
    }

    */

    public static void setUpdateInfo(LocalDate date, LocalTime time, String email){

        if(date == null)
            Log.d(TAG,"null is input. Expect date");
        if(email == null){
            Log.d(TAG,"null is input. Expect email");
        }

        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(email))
                .collection(UPDATE_TIME_COLLECTION_KEY)
                .document(UPDATE_TIME_KEY);


        database.set(new StringAsObject(date.toString(),time.toString())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                //Log.d(TAG, "LastUploadDate successfully uploaded!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error uploading LastUploadDate", e);
            }
        });
        //Log.i(TAG, "setUpdateInfo of "+date.toString()+ " as " + date.toEpochDay());

    }

    public static StringAsObject getUpdateInfo(String email){

        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(email))
                .collection(UPDATE_TIME_COLLECTION_KEY)
                .document(UPDATE_TIME_KEY);

        Task<DocumentSnapshot> task_documentSnapshot = database.get();


        while (!task_documentSnapshot.isComplete()){
            try{
                Thread.sleep(20);
            }catch (Exception e){

            }
        }

        try {DocumentSnapshot documentSnapshot = task_documentSnapshot.getResult();
            if (documentSnapshot == null){
                Log.d(TAG, "unsuccessful reading lastuploaddate");
                return null;
            }else{
                //Log.d(TAG, "successful reading lastuploaddate");

                return documentSnapshot.toObject(StringAsObject.class);
            }
        }catch (Exception e){
            Log.d(TAG, "TASK hasn't finished");
            return null;
        }


    }


    //By checking uid in firebase, we know if user data has been uploaded for at least once
    public static boolean checkExistingUserData(String email){
        return checkAccount(email);
    }


    //return true if found account
    public static boolean checkAccount(String email){


        //database.
        Task<DocumentSnapshot> task_documentSnapshot = FirebaseFirestore.getInstance()
                .collection(PERSONBEST_USER_COLLECTION_KEY)
                .document(reformatEmailForCloud(email)).get();;



        while (!task_documentSnapshot.isComplete()){
            try{
                Thread.sleep(20);
            }catch (Exception e){

            }
        }
        if(!task_documentSnapshot.isSuccessful())
            Log.d(TAG, "task check account unsuccessful");



        try {
            DocumentSnapshot documentSnapshot = task_documentSnapshot.getResult();
            if (documentSnapshot == null ||!documentSnapshot.exists()){
                Log.d(TAG, "unsuccessful reading email");
                return false;
            }else{
                Log.d(TAG, "successful reading email");
                String mail = documentSnapshot.toObject(StringAsObject.class).getString1();
                Log.d(TAG,"converting email");
                if(mail == null) {
                    Log.d(TAG, "email is null");
                    return  false;
                }
                else if(mail.equals(reformatEmailForCloud(email))) {
                    Log.d(TAG, "found revisiting user within cloud");
                    return true;
                }else
                    return  false;
            }
        }catch (Exception e){
            Log.d(TAG, "Error converting back user email");
            return false;
        }
        /*
        CollectionReference database = FirebaseFirestore.getInstance()
                .collection(PERSONBEST_USER_COLLECTION_KEY);
        Query query = database.whereEqualTo(reformatEmailForCloud(email),reformatEmailForCloud(email));
        Task<QuerySnapshot> task_querySnapshot = query.get();

        while (!task_querySnapshot.isComplete()){
            try{
                Thread.sleep(20);
            }catch (Exception e){

            }
        }
        if(!task_querySnapshot.isSuccessful())
            Log.d(TAG, "task for query is not successful");
        try {
            QuerySnapshot querySnapshot =task_querySnapshot.getResult();
            if (querySnapshot.isEmpty())
                Log.d(TAG, querySnapshot.toString());
                Log.d(TAG, "query doesn't find account satisfying requirement");
            return ! querySnapshot.isEmpty();
        }catch (Exception e){
            Log.d(TAG, "check Account query unsuccessful");
            return false;
        }
         */

    }

    /**
     * activateAccount
     *
     * activate PersonalBest user in cloud
     *
     *
     * @param email The email associated with the user
     */
    public static void activateAccount (String email) {
        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(PERSONBEST_USER_COLLECTION_KEY)
                .document(reformatEmailForCloud(email));
        // Get database reference @ root directory
        database.set(new StringAsObject(reformatEmailForCloud(email),reformatEmailForCloud(email))).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User account successfully stored!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error storing user", e);
                    }
                });

        DocumentReference database2 = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(email))
                .collection(FRIEND_DIR_KEY)
                .document(FRIEND_LIST_KEY);
                //.collection("No usage")
                //.document("No usage");
        // Get database reference @ root directory
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(reformatEmailForCloud(email),ONEDIRECTION);
        database2.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User friendlist successfully setup!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error storing user", e);
                    }
                });

    }


    //FIXME Untested
    //called by aInviteB ( which called by AM's addFriend())
    //handles all cases of adding friends
    public static void updateFriendInfo (String userEmail,String friendEmail) {

        //user A's dir
        DocumentReference database1 = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(userEmail))
                .collection(FRIEND_DIR_KEY)
                .document(FRIEND_LIST_KEY);

        //friend candidate B's dir
        DocumentReference database2 = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(friendEmail))
                .collection(FRIEND_DIR_KEY)
                .document(FRIEND_LIST_KEY);

        //check candidate B's dir
        /*
        Task<DocumentSnapshot> task_database2= database2.get();
        while (!task_database2.isComplete()){
            try{
                Thread.sleep(20);
            }catch (Exception e){

            }
        }
        if(!task_database2.isSuccessful())
            Log.d(TAG, "task check account unsuccessful");

        try {
            DocumentSnapshot documentSnapshot = task_database2.getResult();
            if (documentSnapshot == null){
                Log.d(TAG, ("user "+reformatEmailForUser(userEmail)+" doesn't know "+reformatEmailForUser(friendEmail))+" or otherwise");
                //not friend, first time
                //FIXME ADD ONE DIRECTION
                database1.update(reformatEmailForCloud(friendEmail),ONEDIRECTION).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"user "+reformatEmailForUser(userEmail)+"adding B as candiate" );
                    }
                });
                return;
            }else{
                Log.d(TAG, "successful reading B's friendlist");
                String s1 = (String) documentSnapshot.get(reformatEmailForCloud(userEmail));

                if(s1 ==null ){
                    //friend B added A before;
                    Log.d(TAG, ("user "+reformatEmailForUser(userEmail)+" doesn't know "+reformatEmailForUser(friendEmail))+" or otherwise");
                    //FIXME ADD ONE DIRECTION
                    database1.update(reformatEmailForCloud(friendEmail),ONEDIRECTION).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"user "+reformatEmailForUser(userEmail)+"adding B as candiate" );
                        }
                    });

                }else if(s1.equals(MUTUAL)){
                    //already friend
                    Log.d(TAG, ("user "+reformatEmailForUser(userEmail)+" and "+reformatEmailForUser(friendEmail))+" already friends");

                    //Do nothing
                    return;

                }else if(s1.equals(ONEDIRECTION)){
                    //Friend B added A before
                    Log.d(TAG, "B added A or otherwise");
                    database1.update(reformatEmailForCloud(friendEmail),MUTUAL).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"user "+reformatEmailForUser(userEmail)+"and B are now friends" );
                        }
                    });
                    database2.update(reformatEmailForCloud(userEmail),MUTUAL).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"user "+reformatEmailForUser(friendEmail)+"and A are now friends" );
                        }
                    });

                }else {
                    //error since last write
                    Log.d(TAG, "error since last write of friend relation");
                }
            }

        }catch (Exception e){
            Log.d(TAG, "Error updating friendList info");

        }
        */

        //FIXME Untested
        //Async version
        database2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String s1 = (String) documentSnapshot.get(reformatEmailForCloud(userEmail));

                if(s1 ==null ){
                    //friend B added A before;
                    Log.d(TAG, ("user "+reformatEmailForUser(userEmail)+" doesn't know "+reformatEmailForUser(friendEmail))+" or otherwise");
                    //FIXME Untested
                    database1.update(reformatEmailForCloud(friendEmail),ONEDIRECTION).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"user "+reformatEmailForUser(userEmail)+"adding B as candiate" );
                        }
                    });

                }else if(s1.equals(MUTUAL)){
                    //already friend
                    Log.d(TAG, ("user "+reformatEmailForUser(userEmail)+" and "+reformatEmailForUser(friendEmail))+" already friends");

                    //Do nothing




                }else if(s1.equals(ONEDIRECTION)){
                    //Friend B added A before
                    Log.d(TAG, "B added A or otherwise");
                    database1.update(reformatEmailForCloud(friendEmail),MUTUAL).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"user "+reformatEmailForUser(userEmail)+"and "+reformatEmailForUser(friendEmail)+" are now friends" );
                        }
                    });
                    database2.update(reformatEmailForCloud(userEmail),MUTUAL).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"user "+reformatEmailForUser(friendEmail)+"and" +reformatEmailForUser(userEmail)+" are now friends" );
                        }
                    });

                    //BUT HAVE TO PUSH FRIENDLIST CHANGE
                    //FIXME

                }else {
                    //error since last write
                    Log.d(TAG, "error since last write of friend relation");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Log.d(TAG, ("user "+reformatEmailForUser(userEmail)+" doesn't know "+reformatEmailForUser(friendEmail))+" or otherwise");
                //not friend, first time
                //FIXME ADD ONE DIRECTION
                database1.update(reformatEmailForCloud(friendEmail),ONEDIRECTION).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"user "+reformatEmailForUser(userEmail)+"adding B as candiate" );
                    }
                });

            }
        });
    }

    //FIXME Untested
    //TODO need to be called inside AM's sync
    //add a listener to auto add new mutal friend to static friendList of current device
    //would call refresh of FriendListPage or notify it.(FLP would be a observer of AM, and would be notified
    //when have new friend, (we directly call AM's notifyObservers here)
    public static void checkFriendList(String userEmail){
        DocumentReference database1 = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(userEmail))
                .collection(FRIEND_DIR_KEY)
                .document(FRIEND_LIST_KEY);
        database1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());
                    Map<String,Object> map =snapshot.getData();
                    Set<String> candidates = map.keySet();
                    for (String candidate : candidates) {
                        Log.d(TAG, "found snapshot changes! candidate : "+ candidate+" is "+map.get(candidate)+" with user");
                        if(((String)(map.get(candidate))).equals(MUTUAL) ){
                            if(!ActivityMediator.getFriendList().contains(candidate)){
                                Log.d(TAG, "we got new friends need to be pushed");
                                ActivityMediator.addInFriendList(reformatEmailForUser(candidate));
                                Log.d(TAG, "added user "+reformatEmailForUser(candidate)+ " to friendList");
                                //FIXME prompt to refresh friendListPage;
                            }
                        }


                    }

                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });
    }

    //FIXME Untested
    //TODO need to be called inside AM's sync
    //used when initialize the app, should be called by AM in sync();
    //it basically check the cloud storage of friend candidates and add to friendList(static local)
    // if it's mutual friend.
    public static void loadFriendList(String userEmail){
        DocumentReference database = FirebaseFirestore.getInstance()
                .collection(USER_DIR_KEY)
                .document(reformatEmailForCloud(userEmail))
                .collection(FRIEND_DIR_KEY)
                .document(FRIEND_LIST_KEY);
        Task<DocumentSnapshot> task_doc = database.get();
        while (!task_doc.isComplete()){
            try{
                Thread.sleep(20);
            }catch (Exception e){

            }
        }
        if(!task_doc.isSuccessful())
            Log.d(TAG, "task check account unsuccessful");

        try {
            DocumentSnapshot documentSnapshot = task_doc.getResult();
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Map<String,Object> map = documentSnapshot.getData();
                Set<String> candidates = map.keySet();
                for (String candidate : candidates) {
                    Log.d(TAG, "candidate : "+ candidate+" is "+map.get(candidate)+" with user");
                    if(((String)(map.get(candidate))).equals(MUTUAL) ) {
                        Log.d(TAG, "added user "+reformatEmailForUser(candidate)+ " to friendList");
                        ActivityMediator.addInFriendList(reformatEmailForUser(candidate));
                    }else{
                        Log.d(TAG, (String)map.get(candidate));
                    }
                }
            }
        }catch (Exception e){

        }

    }

    //needs to write
    //FIXME
    public static void aInviteB(String A, String B){
        updateFriendInfo(A,B);
    }


    //removed because we should manipulate both's friendlist in the cloud when attempt to add;
    //actually change A's friend data
    //public static void aAddB(String A, String B)



    //removed because we should already checked when attempt to add;
    //public static boolean checkAisBFriend(String A, String B)



}
