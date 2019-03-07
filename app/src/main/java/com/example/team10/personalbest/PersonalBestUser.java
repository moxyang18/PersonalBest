package com.example.team10.personalbest;

import java.util.ArrayList;
import java.util.Hashtable;
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

    // Store dates mapping to days of walks
    private Hashtable<String, WalkDay> table;

    // Email
    private String email;

    // UID (provided by Firebase)
    private String uid;

    // List of friends' emails
    private ArrayList<String> friends;

    /**
     * PersonalBestUser ctor
     *
     * Default constructor. To be passed to the database, Firebase
     * requires that this ctor has no params.
     */
    public PersonalBestUser() {
        friends = new ArrayList<>();
        table = new Hashtable<>();
    }

    // GETTERS

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public Hashtable<String, WalkDay> getTable() {
        return table;
    }

    public List<String> getFriends() {
        return friends;
    }

    // SETTERS

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void setTable(Hashtable<String, WalkDay> table) {
        this.table = table;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
