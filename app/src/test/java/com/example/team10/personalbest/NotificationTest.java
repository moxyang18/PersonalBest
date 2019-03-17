package com.example.team10.personalbest;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

@RunWith(RobolectricTestRunner.class)
public class NotificationTest {

    @Before
    public void setUp() throws Exception {
        Context context = getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(context);
    }

    @Test
    public void subscribeToCorrectTopic() {
        Intent intent = TestUtils.getMessagePageIntent(TestUtils.getChatMessageService(new ArrayList<>()), TestUtils.getNotificationService("john123%test123"));
        intent.putExtra("email", "test123@gmail.com");
        intent.putExtra("from test", "John Doe");
        intent.putExtra("userEmail test", "johndoe123");
        MessagePage messagePage = Robolectric.buildActivity(MessagePage.class, intent).create().get();
        assertEquals("johndoe123%test123", messagePage.getTopicName());
        messagePage.finish();
    }


}