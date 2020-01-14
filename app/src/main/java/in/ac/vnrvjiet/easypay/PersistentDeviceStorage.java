package in.ac.vnrvjiet.easypay;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PersistentDeviceStorage {

    private static PersistentDeviceStorage persistStorage = null;
    private SharedPreferences sharedPreferences;

    public synchronized static PersistentDeviceStorage getInstance(Context context) {
        if (persistStorage == null) {
            persistStorage = new PersistentDeviceStorage(context);
        }
        return persistStorage;
    }

    private PersistentDeviceStorage(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getEmail() {
        return sharedPreferences.getString(Constants.EMAIL_ID, "null");
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL_ID, email);
        editor.apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(Constants.PHONE_NUMBER, "null");
    }

    public void setPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PHONE_NUMBER, phoneNumber);
        editor.apply();
    }

    public void setName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.NAME, name);
        editor.apply();
    }

    public String getName() {
        return sharedPreferences.getString(Constants.NAME, "NoName");
    }

}
