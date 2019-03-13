package com.example.team10.personalbest.fitness;

import android.util.Log;

import com.example.team10.personalbest.PersonalBestUser;
import com.example.team10.personalbest.WalkDay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;

import java.time.LocalDate;

import androidx.annotation.NonNull;

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
    private static final String USERS_DIR = "users";
    private static final String UID_EMAIL_MAP_DIR = "emailToId";

    // Debug
    private static final String TAG = "CloudProcessor - ";

    // DataSnapshot to extract from
    private static DataSnapshot snapshot;



    //FIXME comment out all redundant methods, needs UID for firstime, need seperate Zone to store flags
    //and temporary friend invitation. (Each user pull this list/prompt every few seconds, and decide to accept or not.
    //lazy way: let the user to grab this info when they click new friend request.

    /**
     * getUserFromCloud
     *
     * Retrieves a PersonalBestUser object by looking up info from the
     * database using a uid. Return value may be null if user doesn't exist.
     *
     * @param uid The user id to lookup
     * @return PersonalBestUser A user from data in the database; null if not present
     */

    /*
    public static PersonalBestUser getUserFromCloud(String uid) {

        // Get database reference @ users directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR);

        Log.d(TAG, "Attempting to get user info...");

        /**
         * Read data once. Add a one-time listener for the database user directory.
         * Data is read immediately. If there is a directory specified by the uid,
         * then we'll read in the snapshot. Otherwise, the static variable of this
         * class, snapshot, will be set to null.
         *
         * See reference:
         *
         * https://firebase.google.com/docs/database/admin/retrieve-data
         */
    /*
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                    CloudProcessor.setSnapshot(snapshot);
                    Log.d(TAG, "Found user.");
                } else {
                    CloudProcessor.setSnapshot(null);
                    Log.d(TAG, "User not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getCode());
                CloudProcessor.setSnapshot(null);
            }
        });

        // Return user if not null
        if (snapshot != null) {
            PersonalBestUser user = snapshot.child(uid).getValue(PersonalBestUser.class);
            user.setEmail(reformatEmailForUser(user.getEmail()));
            return user;
        } else {
            return null;
        }
    }
    */

    /**
     * getUserByEmailFromCloud
     *
     * Constructs a PersonalBestUser object by looking up info from
     * the database using an email. May return null if user not present.
     *
     * Likely used to look up friend info.
     *
     * @param email The email to find the user of
     * @return PersonalBestUser A user retrieved from the database; return null if
     *                          not in database
     */
    /*
    public static PersonalBestUser getUserByEmailFromCloud(String email) {

        // Get database reference @ root directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Delegate to helper function to get uid
        String uid = CloudProcessor.getUidFromEmail(reformatEmailForCloud(email));

        // Delegate to other implemented function b/c we're lazy
        return CloudProcessor.getUserFromCloud(uid);
    }
    */


    /**
     * uploadUserData
     *
     * Uploads fully comprehensive information regarding the user, including:
     *  - User email, mapped to user uid
     *  - The full user object
     *
     * @param user The user to upload
     */
    /*
    public static void uploadUserData(PersonalBestUser user) {
        if(user == null)
            return;
        user.setEmail(reformatEmailForCloud(user.getEmail()));
        // Get database reference @ user-specific directory, @ "users" directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(user.getUid());

        // Link email to a uid
        CloudProcessor.linkIdToEmail(user.getUid(), reformatEmailForCloud(user.getEmail()));

        // Upload data to respective directories
        database.setValue(user);
}
*/

    /**
     * uploadWalkDay
     *
     * Uploads (overwrites) one walkday, implicitly call setLastUpLoadDate
     *
     * @param walkDay walkday to upload
     * @param email eamail used to distinguish user
     */
    public static void uploadWalkDay(WalkDay walkDay, String email){
        if(walkDay == null || email == null){
            Log.d(TAG,"null is input. Expect walkday and email");
        }
        String date = walkDay.getDate().toString();
        //long time = walkDay.getDate().
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(getUidFromEmail(reformatEmailForCloud(email)));
        database.child(date).setValue(walkDay);

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

        String uid = getUidFromEmail(reformatEmailForCloud(email));
        if (uid ==null) return null;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(uid);


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(date.toString())) {
                    CloudProcessor.setSnapshot(snapshot);
                    Log.d(TAG, "Found user's walkday data on "+ date.toString());
                } else {
                    CloudProcessor.setSnapshot(null);
                    Log.d(TAG, "User's walkday data on "+ date.toString()+" not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getCode());
                CloudProcessor.setSnapshot(null);
            }
        });

        // Return user if not null
        if (snapshot != null) {
            return snapshot.child(date.toString()).getValue(WalkDay.class);
        } else {
            return new WalkDay(date);
        }
    }



    public static void setLastUploadDate(LocalDate date, String email){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(getUidFromEmail(reformatEmailForCloud(email)));
        database.child("lastUploadDate").setValue(date.toEpochDay());
    }

    public static LocalDate getLastUploadDate(String email){
        String uid = getUidFromEmail(reformatEmailForCloud(email));
        if (uid ==null) return null;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(uid);


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("lastUploadDate")) {
                    CloudProcessor.setSnapshot(snapshot);
                    Log.d(TAG, "Found user's last upload date.");
                } else {
                    CloudProcessor.setSnapshot(null);
                    Log.d(TAG, "User's last upload date not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getCode());
                CloudProcessor.setSnapshot(null);
            }
        });

        // Return latest upload date if not null
        if (snapshot != null) {
            return snapshot.child("lastUploadDate").getValue(LocalDate.class);
        } else {
            return null;
        }
    }


    //By checking uid in firebase, we know if user data has been uploaded for at least once
    public static boolean checkExistingUserData(String email){
        String uid = getUidFromEmail(reformatEmailForCloud(email));
        if(uid == null) {
            return false;
        }
        else return true;
    }

    /**
     * linkIdToEmail
     *
     * Links UIDs to emails in the database so we can look them up later.
     *
     * @param uid The unique user id
     * @param email The email associated with the user
     */
    public static void linkIdToEmail (String uid, String email) {

        // Get database reference @ root directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Link
        database.child(UID_EMAIL_MAP_DIR).child(reformatEmailForCloud(email)).setValue(uid);
        database.child(USERS_DIR).child(uid).child("email").setValue(reformatEmailForCloud(email));
        database.child(USERS_DIR).child(uid).child("uid").setValue(uid);
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
                .child(UID_EMAIL_MAP_DIR);


         // Read uid from email once.
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(email)) {
                    CloudProcessor.setSnapshot(snapshot);
                } else {
                    CloudProcessor.setSnapshot(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getCode());
                CloudProcessor.setSnapshot(null);
            }
        });

        if(snapshot!=null)

        // Return the UID assumed to be associated with the email
            return snapshot.child(reformatEmailForCloud(email)).getValue(String.class);

        else
            return null;
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
        if (snapshot == null) {
            Log.d(TAG, "Snapshot was null - no data found at queried location.");
        }
        CloudProcessor.snapshot = snapshot;
    }

    /**
     * reformatEmailForCloud
     *
     * Periods (.) are prohibited in JSON strings. We'll reformat
     * the email string to replace periods with commas.
     *
     * @param email The email to reformat
     * @return A reformatted email with periods replaced with commas.
     */
    private static String reformatEmailForCloud (String email) {

        char[] emailChar = email.toCharArray();

        for (int i = 0; i < emailChar.length; i++) {
            if (emailChar[i] == '.') {
                emailChar[i] = ',';
            }
        }
        return new String(emailChar);
    }

    /**
     * reformatEmailForUser
     *
     * For main memory, we'll need strings to be in their original
     * format. We'll return email strings formatted with commas to
     * email strings with periods.
     *
     * @param email The email to reformat.
     * @return A reformatted email with commas replaced with periods.
     */
    private static String reformatEmailForUser (String email) {
        char[] emailChar = email.toCharArray();

        for (int i = 0; i < emailChar.length; i++) {
            if (emailChar[i] == ',') {
                emailChar[i] = '.';
            }
        }
        return new String(emailChar);
    }
}
