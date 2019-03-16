package com.example.team10.personalbest;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MediatorFactory {

    private static final String TAG = "[MediatorFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static Mediator create(String key, HomePage homePage) {
        Log.i(TAG, String.format("creating Mediator with key %s", key));
        return blueprints.get(key).create(homePage);
    }

    public interface BluePrint {
        Mediator create(HomePage homePage);
    }
}
