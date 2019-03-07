package com.example.team10.personalbest.fitness;

import com.example.team10.personalbest.PersonalBestUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    // DataSnapshot to extract from
    private static DataSnapshot snapshot;

    /**
     * getUserFromCloud
     *
     * Retrieves a PersonalBestUser object by looking up info from the
     * database using a uid. Return value may be null if user doesn't exist.
     *
     * @param uid The user id to lookup
     * @return PersonalBestUser A user from data in the database; null if not present
     */
    public static PersonalBestUser getUserFromCloud(String uid) {

        // Get database reference @ users directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR);

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
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
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

        // Return user if not null
        if (snapshot != null) {
            return snapshot.child(uid).getValue(PersonalBestUser.class);
        } else {
            return null;
        }
    }

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
    public static PersonalBestUser getUserByEmailFromCloud(String email) {

        // Get database reference @ root directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Delegate to helper function to get uid
        String uid = CloudProcessor.getUidFromEmail(email);

        // Delegate to other implemented function b/c we're lazy
        return CloudProcessor.getUserFromCloud(uid);
    }

    /**
     * uploadUserData
     *
     * Uploads fully comprehensive information regarding the user, including:
     *  - User email, mapped to user uid
     *  - The full user object
     *
     * @param user The user to upload
     */
    public static void uploadUserData(PersonalBestUser user) {

        // Get database reference @ user-specific directory, @ "users" directory
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child(USERS_DIR).child(user.getUid());

        // Link email to a uid
        CloudProcessor.linkIdToEmail(user.getUid(), user.getEmail());

        // Upload data to respective directories
        database.setValue(user);
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
        database.child(UID_EMAIL_MAP_DIR).child(email).setValue(uid);
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

        // Return the UID assumed to be associated with the email
        return snapshot.child(email).getValue(String.class);
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
        CloudProcessor.snapshot = snapshot;
    }
}
