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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;

import androidx.annotation.NonNull;

import static com.example.team10.personalbest.FormatHelper.reformatEmailForCloud;

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

    //private static final String USERS_DIR = "users";
    //private static final String UID_EMAIL_MAP_DIR = "emailToId";

    // Debug
    private static final String TAG = "CloudProcessor - ";

    // DataSnapshot to extract from
    private static DataSnapshot snapshot;
    private static WalkDay walkDay;


    //FIXME comment out all redundant methods, needs UID for firstime, need seperate Zone to store flags




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
            if (documentSnapshot == null){
                Log.d(TAG, "unsuccessful reading email");
                return false;
            }else{
                Log.d(TAG, "successful reading email");
                String mail = documentSnapshot.toObject(StringAsObject.class).getString1();Log.d(TAG,"converting email");
                if(mail == null) Log.d(TAG, "email is null");else if(mail.equals(email)) Log.d(TAG, "found revisiting user within cloud");
                return true;
            }
        }catch (Exception e){
            Log.d(TAG, "Error converting back user email");
            return true;
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
     * linkIdToEmail
     *
     * Links UIDs to emails in the database so we can look them up later.
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
                Log.d(TAG, "User successfully stored!");
            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error storing user", e);
                }
            });

    }


    //needs to write
    //FIXME
    public static boolean aInviteB(String A, String B){
        if(A==B)
        return true;
        else  return  false;
    }

    //actually change A's friend data
    public static void aAddB(String A, String B){

    }

    public static boolean checkAisBFriend(String A, String B){
        if(A==B)
            return true;
        else  return  false;
    }

    /**
     * getUidFromEmail
     *
     * Yields user UID from a given email by looking up the info
     * in the database.
     *
     * @param email The email to check.
     * @return String The associated UID.
     */
    private static String getUidFromEmail (String email) {

        // Get database reference @ email mapping directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(PERSONBEST_USER_COLLECTION_KEY)
                ;

        ValueEventListener m = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CloudProcessor.setSnapshot(snapshot);
                if (snapshot.hasChild(reformatEmailForCloud(email))) {
                    CloudProcessor.setSnapshot(snapshot);
                    Log.i(TAG,"Got snapshot with user email as child entry");

                } else {
                    Log.i(TAG, "snapshot doesn't have user email");
                    CloudProcessor.setSnapshot(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getCode());
                CloudProcessor.setSnapshot(null);
            }
        };
         // Read uid from email once.
        database.addListenerForSingleValueEvent(m);
        Log.i(TAG,"set up listener for one time reading");
        //code below this execute before listener respond
        //FIXME this whie would take forever

        while(snapshot==null) {
        //    Log.i(TAG,"waiting for aysn Data Listenr to update");

            try{
                Thread.sleep(20);
            }
            catch (Exception e){

            }
        }
        // Return the UID assumed to be associated with the email


        //FIXME
        // would trigger bug in AM.java 152 but won't trigger bug in 102 because the if-else branch there
        //handled the returned null
        /*
        if(snapshot!=null){
            Log.i(TAG, "Get UID of " + reformatEmailForUser(email) + " " + snapshot.child(reformatEmailForCloud(email)).getValue(String.class));
            return snapshot.child(reformatEmailForCloud(email)).getValue(String.class);
        }
        else{
            Log.d(TAG, "Unsuccessful geting UID, snapshot not set");
            return null;
        }
        */

        //FIXME
        //Program dies directly at AM's call to checkIsExistingUser
        String result = snapshot.child(FormatHelper.reformatEmailForCloud(email)).getValue(String.class);
        Log.i(TAG, "Get UID of " + FormatHelper.reformatEmailForUser(email) + " " + result);
        setSnapshot(null);
        return result;

    }

    /**
     * setSnapshot
     *
     * Sets the static DataSnapshot for the CloudProcessor so the class
     * can use it
     *
     * @param snapshot Data snapshot that reflects information from the database
     */
    private static void setSnapshot(DataSnapshot snapshot) {

        //FIXME this line never gets called meaning onDataChange never execute quick enough OR my implementation is wrong.
        if (snapshot != null) {
            Log.d(TAG, "Snapshot was set successfully with non-null.");
        }

        CloudProcessor.snapshot = snapshot;
    }

    private static void setWalkDay(WalkDay w){
        walkDay =w;
    }

    private static void resetWalkDay(){
        walkDay = null;
    }


}
