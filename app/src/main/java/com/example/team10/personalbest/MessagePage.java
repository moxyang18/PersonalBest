package com.example.team10.personalbest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.ChatMessaging.FirebaseCloudMessengerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagePage extends AppCompatActivity {
    private static String TAG = MessagePage.class.getSimpleName();

    String COLLECTION_KEY = "chats";
    String DOCUMENT_KEY = "chat1";
    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";
    String friendEmail;

    FirebaseCloudMessengerAdapter fcmAdapter;
    SharedPreferences sharedPreferences;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);
        SharedPreferences sharedpreferences = getSharedPreferences("FirebaseLabApp", Context.MODE_PRIVATE);

        from = sharedpreferences.getString(FROM_KEY, null);

        //Initialize Firebase Store
        FirebaseApp.initializeApp(this);
        fcmAdapter = new FirebaseCloudMessengerAdapter(this, DOCUMENT_KEY);
        fcmAdapter.initMessageUpdateListener();
        fcmAdapter.subscribeToNotificationsTopic();


        //Set up buttons/UI
        TextView friendName = findViewById(R.id.friend_name);

        //Get the friend's name and email from intent (just email)
        friendName.setText(getIntent().getExtras().getString("name"));
        friendEmail = getIntent().getExtras().getString("name");

        Button friend_homepage_button = findViewById(R.id.friend_homepage_button);
        friend_homepage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                launchFriendHomepage();
            }
        });

        Button message_back_button = findViewById(R.id.message_back_button);
        message_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(view -> fcmAdapter.sendMessage(from));

        EditText nameView = findViewById((R.id.user_name));
        nameView.setText(from);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                from = s.toString();
                sharedpreferences.edit().putString(FROM_KEY, from).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public void launchFriendHomepage(){
        Intent intent = new Intent(this, FriendHomePage.class);
        intent.putExtra("name", friendEmail); //pass in name
        startActivity(intent);
    }
}
