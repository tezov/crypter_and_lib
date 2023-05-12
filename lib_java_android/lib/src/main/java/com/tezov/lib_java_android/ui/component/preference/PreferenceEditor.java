/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.preference;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.misc.AttributeReader;

import java.util.Set;

public class PreferenceEditor{
final static private int[] ATTR_INDEX = {android.R.attr.id};
protected androidx.preference.Preference pref;
protected SharedPreferences sp = null;
protected String key = null;

public PreferenceEditor(androidx.preference.Preference pref){
DebugTrack.start().create(this).end();
    this.pref = pref;
}
public void init(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    if(attrs == null){
        return;
    }
    AttributeReader attributes = new AttributeReader().setAttrsIndex(ATTR_INDEX).parse(context, attrs);
    int id = attributes.getReference(0);
    key = AppContext.getResources().getIdentifierName(id);
    sp = Application.sharedPreferences();
    pref.setKey(sp.encodeKey(key));
}

public boolean persistString(String value){
    if(!pref.isPersistent()){
        return false;
    }
    sp.put(key, value);
    return true;
}

public String getPersistedString(String defaultReturnValue){
    if(!pref.isPersistent()){
        return defaultReturnValue;
    }
    String value = sp.getString(key);
    if(value == null){
        return defaultReturnValue;
    } else {
        return value;
    }
}

public boolean persistInt(int value){
    if(!pref.isPersistent()){
        return false;
    }
    sp.put(key, value);
    return true;
}

public int getPersistedInt(int defaultReturnValue){
    if(!pref.isPersistent()){
        return defaultReturnValue;
    }
    Integer value = sp.getInt(key);
    if(value == null){
        return defaultReturnValue;
    } else {
        return value;
    }
}

public boolean persistFloat(float value){
    if(!pref.isPersistent()){
        return false;
    }
    sp.put(key, value);
    return true;
}

public float getPersistedFloat(float defaultReturnValue){
    if(!pref.isPersistent()){
        return defaultReturnValue;
    }
    Float value = sp.getFloat(key);
    if(value == null){
        return defaultReturnValue;
    } else {
        return value;
    }
}

public boolean persistLong(long value){
    if(!pref.isPersistent()){
        return false;
    }
    sp.put(key, value);
    return true;
}

public long getPersistedLong(long defaultReturnValue){
    if(!pref.isPersistent()){
        return defaultReturnValue;
    }
    Long value = sp.getLong(key);
    if(value == null){
        return defaultReturnValue;
    } else {
        return value;
    }
}

public boolean persistBoolean(boolean value){
    if(!pref.isPersistent()){
        return false;
    }
    sp.put(key, value);
    return true;
}
public boolean getPersistedBoolean(boolean defaultReturnValue){
    if(!pref.isPersistent()){
        return defaultReturnValue;
    }
    Boolean value = sp.getBoolean(key);
    if(value == null){
        return defaultReturnValue;
    } else {
        return value;
    }
}

public boolean persistStringSet(Set<String> values){
    if(!pref.isPersistent()){
        return false;
    }
    sp.put(key, values);
    return false;
}
public Set<String> getPersistedStringSet(Set<String> defaultReturnValue){
    if(!pref.isPersistent()){
        return defaultReturnValue;
    }
    Set<String> value = sp.getStringSet(key);
    if(value == null){
        return defaultReturnValue;
    } else {
        return value;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
