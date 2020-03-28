package com.androidavanzado.minitwitter.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String APP_SETTINGS_FILE = "APP_SETTINGS";

    private SharedPreferencesManager(){}

    // Método que señala el contexto y el fichero donde se guardaran los datos
    private static SharedPreferences getSharedPreferences(){
        return MyApp.getContext()
                .getSharedPreferences(APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    // Métodos que se encarga de guardar la clave y el valor
    public static void setSomeStringValue(String dataLabel, String dataValue){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(dataLabel, dataValue);
        editor.commit();
    }

    public static void setSomeBooleanValue(String dataLabel, boolean dataValue){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(dataLabel, dataValue);
        editor.commit();
    }

    // Métodos para rescatar el valor
    public static String getSomeStringValue(String dataLabel){
        return getSharedPreferences().getString(dataLabel, null);
    }

    public static boolean getSomeBooleanValue(String dataLabel){
        return getSharedPreferences().getBoolean(dataLabel, false);
    }

}
