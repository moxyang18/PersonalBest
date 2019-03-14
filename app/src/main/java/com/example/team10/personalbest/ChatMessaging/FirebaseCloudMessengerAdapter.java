package com.example.team10.personalbest.ChatMessaging;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.MessagePage;
import com.example.team10.personalbest.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseCloudMessengerAdapter implements MessageService {
    private static String TAG = "FirebaseCloudMessengerAdapter:";
    String MESSAGES_KEY = "messages";
    String COLLECTION_KEY = "chats";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";


    CollectionReference chat;
    MessagePage activity;

    public FirebaseCloudMessengerAdapter(MessagePage homeActivity, String topic) {
        this.activity = homeActivity;

        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(topic)
                .collection(MESSAGES_KEY);


    }
    public String subscribeToNotificationsTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                        }
                );
        return topic;
    }

    public void sendMessage(String from) {
        EditText messageView = activity.findViewById(R.id.text_message);
        String textMessage = messageView.getText().toString();

        Map<String, String> newMessage = new HashMap<>();

        Log.d(TAG, textMessage + "is the text message");
        if(textMessage.equals("") || textMessage.equals("\n")) {
            return;
        }
        else {
            newMessage.put(FROM_KEY, from);
            newMessage.put(TEXT_KEY, messageView.getText().toString());
        }


        Log.d(TAG, "before addOnSuccessListener");
        chat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
            Log.d(TAG, "new message added");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
            Log.d(TAG, "no message added");
        });
        Log.d(TAG, "send button being called");
    }

    public void initMessageUpdateListener() {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {
            if (error != null) {
                Log.e(TAG, error.getLocalizedMessage());
                return;
            }

            if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                documentChanges.forEach(change -> {
                    QueryDocumentSnapshot document = change.getDocument();
                    sb.append(document.get(FROM_KEY));
                    sb.append(":\n");
                    sb.append(document.get(TEXT_KEY));
                    sb.append("\n\n");
                    sb.append("Sent at: " + document.getDate(TIMESTAMP_KEY));
                    sb.append("\n");
                    sb.append("---\n");
                });

                TextView chatView = activity.findViewById(R.id.chat);
                chatView.append(sb.toString());
                Log.d(TAG, "message update listener initialized");
            }
        });
    }
}



/*
package com.example.team10.personalbest.ChatMessaging;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team10.personalbest.MessagePage;
import com.example.team10.personalbest.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseCloudMessengerAdapter {
    private static final String TAG = "FirebaseCloudMessengerAdapter :: ";

    String COLLECTION_KEY = "chats";
    String DOCUMENT_KEY = "chat1";
    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    MessagePage activity;

    public FirebaseCloudMessengerAdapter(MessagePage homeActivity, String topic ) {
        this.activity = homeActivity;

        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(topic)
                .collection(MESSAGES_KEY);
    }

    public void sendMessage(String from) {
        if (from == null || from.isEmpty()) {
            Toast.makeText(activity, "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText messageView = activity.findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, from);
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        Log.d(TAG, "Before adding listeners");
        chat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
            Log.d(TAG, "Setting Success Listener");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
            Log.d(TAG, "Setting Failure Listener");
        }).addOnCanceledListener(() -> {
            Log.d(TAG, "Listener is canceled");
        }).addOnCompleteListener(complete -> {
            Log.d(TAG, "Listener is completed");
        });
        Log.d(TAG, "Finshed adding Listeners");
    }

    public void initMessageUpdateListener() {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING)
                .addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                        documentChanges.forEach(change -> {
                            QueryDocumentSnapshot document = change.getDocument();
                            sb.append(document.get(FROM_KEY));
                            sb.append(":\n");
                            sb.append(document.get(TEXT_KEY));
                            sb.append("\n");
                            sb.append("---\n");
                        });


                        TextView chatView = activity.findViewById(R.id.chat);
                        chatView.append(sb.toString());
                    }
                });
    }

    public void subscribeToNotificationsTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                        }
                );
    }
}
*/