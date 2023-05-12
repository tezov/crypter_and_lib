/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.activity;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.ui.component.preference.DirectoryPreference;
import com.tezov.lib_java_android.ui.component.preference.custom.DialogPreferenceDirectory;
import com.tezov.lib_java_android.ui.component.preference.custom.DialogPreferenceEditText;
import com.tezov.lib_java_android.ui.component.preference.EditTextPreference;
import com.tezov.lib_java.toolbox.Reflection;

import androidx.fragment.app.DialogFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.PreferenceOnClickListenerW;

import java.util.ArrayList;
import java.util.List;

public abstract class ActivityPreference extends ActivityToolbar{

protected abstract int getPreferenceContainerId();
protected abstract FragmentPreference createFragmentPreference();

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
    tr.add(getPreferenceContainerId(), createFragmentPreference()).commit();
}

public static abstract class FragmentPreference extends androidx.preference.PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final static String KEY_XML_ID = "KEY_XML_ID";

    public FragmentPreference(){
        trackClassCreate();
    }
    public FragmentPreference(int xmlId){
        trackClassCreate();
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        bundle.putInt(KEY_XML_ID, xmlId);
        setArguments(bundle);
    }
    private void trackClassCreate(){
DebugTrack.start().create(this).end();
    }

    public abstract String getSharedPreferencesName();
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(getSharedPreferencesName());
        preferenceManager.setSharedPreferencesMode(android.content.Context.MODE_PRIVATE);
        Bundle bundle = getArguments();
        if(bundle == null){
            bundle = savedInstanceState;
        }
        addPreferencesFromResource(bundle.getInt(KEY_XML_ID));
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_XML_ID, getArguments().getInt(KEY_XML_ID));
    }

    public List<String> getKeys(){
        List<String> keys = new ArrayList<>();
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        return Nullify.collection(getKeys(preferenceScreen, keys));
    }
    private List<String> getKeys(PreferenceGroup preferenceGroup, List<String> keys){
        for(int i = 0; i < preferenceGroup.getPreferenceCount(); i++){
            androidx.preference.Preference preference = preferenceGroup.getPreference(i);
            if(preference instanceof PreferenceGroup){
                keys = getKeys((PreferenceGroup)preference, keys);
            } else {
                String key = preference.getKey();
                if(key != null){
                    keys.add(key);
                }
            }
        }
        return keys;
    }
    public boolean hasKey(String key){
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        return hasKey(preferenceScreen, key);
    }
    private boolean hasKey(PreferenceGroup preferenceGroup, String key){
        for(int i = 0; i < preferenceGroup.getPreferenceCount(); i++){
            androidx.preference.Preference preference = preferenceGroup.getPreference(i);
            if(preference instanceof PreferenceGroup){
                if(hasKey((PreferenceGroup)preference, key)){
                    return true;
                } else if(key.equals(preference.getKey())){
                    return true;
                }
            }
        }
        return false;
    }

    private String getKey(int id){
        return AppContext.getResources().getIdentifierName(id);
    }
    protected Preference findPreference(int id){
        String key = getKey(id);
        return findPreference(key);
    }
    protected void setOnClickListener(int id, PreferenceOnClickListenerW listener){
        Preference pref = findPreference(id);
        pref.setOnPreferenceClickListener(listener);
    }
    protected void setOnClickListenerEnabled(int id, boolean flag){
        setOnClickListenerEnabled(findPreference(id), flag);
    }
    protected void setOnClickListenerEnabled(Preference preference, boolean flag){
        ((PreferenceOnClickListenerW)preference.getOnPreferenceClickListener()).setEnabled(flag);
    }

    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    final public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        com.tezov.lib_java_android.application.SharedPreferences sp = Application.sharedPreferences();
        onSharedPreferenceChanged(sp, key, sp.decodeKey(key));
    }
    public void onSharedPreferenceChanged(com.tezov.lib_java_android.application.SharedPreferences sp, String key, String keyDecoded){

    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}
public static abstract class FragmentSharePreference extends FragmentPreference{
    final protected static String DIALOG_FRAGMENT_CUSTOM_TAG = "androidx.preference.PreferenceFragment.DIALOG.custom";
    final private static String ARG_KEY = "key";
    protected com.tezov.lib_java_android.application.SharedPreferences sp;

    public FragmentSharePreference(){

    }
    public FragmentSharePreference(int xmlId){
        super(xmlId);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        super.onCreatePreferences(savedInstanceState, rootKey);
        sp = Application.sharedPreferences();
    }
    public String getEncodedKey(int id){
        return getEncodedKey(AppContext.getResources().getIdentifierName(id));
    }
    public String getEncodedKey(String key){
        return sp.encodeKey(key);
    }
    @Override
    protected Preference findPreference(int id){
        String key = getEncodedKey(id);
        return findPreference(key);
    }

    @Override
    final public void onDisplayPreferenceDialog(Preference preference){
        Class<? extends PreferenceDialogFragmentCompat> type = onDisplayPreferenceDialogCustom(preference);
        if(type == null){
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        if (getParentFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_CUSTOM_TAG) == null) {
            DialogFragment dialogFragment = newInstance(type, preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), DIALOG_FRAGMENT_CUSTOM_TAG);
        }
    }
    protected Class<? extends PreferenceDialogFragmentCompat> onDisplayPreferenceDialogCustom(Preference preference){
        if(preference instanceof EditTextPreference){
            return DialogPreferenceEditText.class;
        }
        if(preference instanceof DirectoryPreference){
            return DialogPreferenceDirectory.class;
        }
        return null;
    }

    protected static <T extends PreferenceDialogFragmentCompat> T newInstance(Class<T> type, String key){
        T fragment = Reflection.newInstance(type);
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

}

}
