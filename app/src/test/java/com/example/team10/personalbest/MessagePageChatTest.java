package com.example.team10.personalbest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.example.team10.personalbest.ChatMessaging.ChatMessage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

@RunWith(RobolectricTestRunner.class)
public class MessagePageChatTest {
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    CollectionReference cr;

    @Before
    public void setUp() throws Exception {
        Context context = getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(context);
    }

    @Test
    public void messagesDisplayedInOrder() {
        List<ChatMessage> m = new ArrayList<>();
        m.add(new ChatMessage("User1", "Hi there"));
        m.add(new ChatMessage("User1", "How are you doing?"));
        m.add(new ChatMessage("User2", "Good, how are you?"));

        Intent intent = TestUtils.getMessagePageIntent(TestUtils.getChatMessageService(m), TestUtils.getNotificationService("chat1"));
        intent.putExtra("email", "test123@gmail.com");
        intent.putExtra("from test", "John Doe");
        intent.putExtra("userEmail test", "johndoe123");
        MessagePage activity = Robolectric.buildActivity(MessagePage.class, intent).create().get();

        TextView chat = activity.findViewById(R.id.chat);

        StringBuilder sb = new StringBuilder();
        m.forEach(message -> sb.append(message.toString()));
        chat.append(sb.toString());
        assertEquals(sb.toString(), chat.getText().toString());
    }

}
