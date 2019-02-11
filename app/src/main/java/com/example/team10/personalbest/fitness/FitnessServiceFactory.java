package com.example.team10.personalbest.fitness;

import android.util.Log;

import com.example.team10.personalbest.HomePage;

import java.util.HashMap;
import java.util.Map;

public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static FitnessService create(String key, HomePage homePage) {
        Log.i(TAG, String.format("creating FitnessService with key %s", key));
        return blueprints.get(key).create(homePage);
    }

    public interface BluePrint {
        FitnessService create(HomePage homePage);
    }
}
