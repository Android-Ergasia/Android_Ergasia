//package com.example.ergasiaandroid;
//
//import android.content.Context;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ParkingStorage {
//    private static final String PREFS = "parking_prefs";
//    private static final String KEY = "parking_data";
//
//    public static void save(Context context, List<ParkingLocation> list) {
//        List<JSONObject> jsonList = new ArrayList<>();
//        for (ParkingLocation p : list) {
//            jsonList.add(p.toJSON());
//        }
//
//        String data = jsonList.toString();
//        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//                .edit()
//                .putString(KEY, data)
//                .apply();
//    }
//
//    public static List<ParkingLocation> load(Context context) {
//        List<ParkingLocation> list = new ArrayList<>();
//        String data = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//                .getString(KEY, null);
//
//        if (data != null) {
//            try {
//                JSONArray array = new JSONArray(data);
//                for (int i = 0; i < array.length(); i++) {
//                    JSONObject obj = array.getJSONObject(i);
//                    list.add(ParkingLocation.fromJSON(obj));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return list;
//    }
//}
//
