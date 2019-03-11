package com.example.team10.personalbest;

import java.util.ArrayList;
import java.util.List;

/**
 * PersonalBestUser class
 *
 * Serves to keep a user's information in main memory. Can be read/written to
 * via cloud functions or other runtime modifications. Keeps track of user
 * email, uid, table, and friend list.
 *
 * When using this class, please be sure to properly set the email and
 * the UID. Email and UID can be set after signin via the FirebaseUser object.
 */
public class PersonalBestUser {

    // Date-walkday table as json string
    private String table;

    // Email
    private String email;

    // UID (provided by Firebase)
    private String uid;

    // List of friends' emails
    private List<String> friends;

    /**
     * PersonalBestUser ctor
     *
     * Default constructor. To be passed to the database, Firebase
     * requires that this ctor has no params.
     */
    public PersonalBestUser() {
        friends = new ArrayList<>();
    }

    // GETTERS

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getTable() {
        return table;
    }

    public List<String> getFriends() {
        return friends;
    }

    // SETTERS

    public void setEmail(String email) { this.email = email; }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
