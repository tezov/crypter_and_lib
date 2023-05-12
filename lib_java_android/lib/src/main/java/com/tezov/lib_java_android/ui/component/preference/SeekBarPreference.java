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

import com.tezov.lib_java.debug.DebugTrack;

import java.util.Set;

public class SeekBarPreference extends androidx.preference.SeekBarPreference{
private PreferenceEditor editor = null;
public SeekBarPreference(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}
public SeekBarPreference(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}
public SeekBarPreference(android.content.Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}

public SeekBarPreference(android.content.Context context){
    super(context);
    init(context, null, 0, 0);
}
private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
    editor = new PreferenceEditor(this);
    editor.init(context, attrs, defStyleAttr, defStyleRes);
}

@Override
protected boolean persistString(String value){
    return editor.persistString(value);
}
@Override
protected String getPersistedString(String defaultReturnValue){
    return editor.getPersistedString(defaultReturnValue);
}
@Override
protected boolean persistInt(int value){
    return editor.persistInt(value);
}
@Override
protected int getPersistedInt(int defaultReturnValue){
    return editor.getPersistedInt(defaultReturnValue);
}
@Override
protected boolean persistFloat(float value){
    return editor.persistFloat(value);
}
@Override
protected float getPersistedFloat(float defaultReturnValue){
    return editor.getPersistedFloat(defaultReturnValue);
}
@Override
protected boolean persistLong(long value){
    return editor.persistLong(value);
}
@Override
protected long getPersistedLong(long defaultReturnValue){
    return editor.getPersistedLong(defaultReturnValue);
}
@Override
protected boolean persistBoolean(boolean value){
    return editor.persistBoolean(value);
}

@Override
protected boolean getPersistedBoolean(boolean defaultReturnValue){
    return editor.getPersistedBoolean(defaultReturnValue);
}
@Override
public boolean persistStringSet(Set<String> values){
    return editor.persistStringSet(values);
}
@Override
public Set<String> getPersistedStringSet(Set<String> defaultReturnValue){
    return defaultReturnValue;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
