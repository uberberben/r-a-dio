package com.ben.ber;

import java.util.prefs.Preferences;

public class SaveData {
    private static Preferences prefs;

    // Save form data
    public static void setPreference(String nick, String pass, String channel) {
        if (prefs == null) {
            prefs = Preferences.userRoot().node("SaveData");
        }
        prefs.put("nick", nick);
        prefs.put("pass", pass);
        prefs.put("channel", channel);
    }

    // Form data getter
    public static String[] getPreference() {
        if (prefs == null) {
            prefs = Preferences.userRoot().node("SaveData");
        }
        return new String[]{prefs.get("nick", "Nick"), prefs.get("pass", "Password"), prefs.get("channel", "#r/a/dio")};
    }

}
