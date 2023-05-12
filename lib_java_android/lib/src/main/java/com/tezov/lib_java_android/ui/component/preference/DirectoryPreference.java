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

import com.tezov.lib_java_android.ui.component.preference.custom.UtilsPreference;
import com.tezov.lib_java_android.R;

import androidx.preference.DialogPreference;

import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.tezov.lib_java.debug.DebugTrack;

import java.util.Set;

public class DirectoryPreference extends DialogPreference{
private String text;
private PreferenceEditor editor = null;

public DirectoryPreference(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}
public DirectoryPreference(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}
public DirectoryPreference(android.content.Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}
public DirectoryPreference(android.content.Context context){
    super(context);
    init(context, null, 0, 0);
}
private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
    TypedArray a = context.obtainStyledAttributes(attrs, androidx.preference.R.styleable.EditTextPreference, defStyleAttr, defStyleRes);
    if(UtilsPreference.getBoolean(a,
            androidx.preference.R.styleable.EditTextPreference_useSimpleSummaryProvider,
            androidx.preference.R.styleable.EditTextPreference_useSimpleSummaryProvider, false)){
        setSummaryProvider(DirectoryPreference.SimpleSummaryProvider.getInstance());
    }
    a.recycle();
    editor = new PreferenceEditor(this);
    editor.init(context, attrs, defStyleAttr, defStyleRes);
}

@Override
protected Object onGetDefaultValue(TypedArray a, int index) {
    return a.getString(index);
}
@Override
public int getDialogLayoutResource(){
    return R.layout.preference_directory;
}

public String getText(){
    return text;
}
public void setText(String text){
    final boolean wasBlocking = shouldDisableDependents();
    this.text = text;
    persistString(text);
    final boolean isBlocking = shouldDisableDependents();
    if(isBlocking != wasBlocking){
        notifyDependencyChange(isBlocking);
    }
    notifyChanged();
}

@Override
protected void onSetInitialValue(Object defaultValue){
    setText(getPersistedString((String)defaultValue));
}
@Override
public boolean shouldDisableDependents(){
    return TextUtils.isEmpty(text) || super.shouldDisableDependents();
}
@Override
protected Parcelable onSaveInstanceState(){
    final Parcelable superState = super.onSaveInstanceState();
    if(isPersistent()){
        return superState;
    }
    final SavedState myState = new SavedState(superState);
    myState.mText = getText();
    return myState;
}
@Override
protected void onRestoreInstanceState(Parcelable state){
    if(state == null || !state.getClass().equals(SavedState.class)){
        super.onRestoreInstanceState(state);
        return;
    }
    SavedState myState = (SavedState)state;
    super.onRestoreInstanceState(myState.getSuperState());
    setText(myState.mText);
}

private static class SavedState extends BaseSavedState{
    public static final Creator<SavedState> CREATOR = new Creator<SavedState>(){
        @Override
        public SavedState createFromParcel(Parcel in){
            return new SavedState(in);
        }

        @Override
        public SavedState[] newArray(int size){
            return new SavedState[size];
        }
    };
    String mText;
    SavedState(Parcel source){
        super(source);
        mText = source.readString();
    }
    SavedState(Parcelable superState){
        super(superState);
    }
    @Override
    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest, flags);
        dest.writeString(mText);
    }
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

public static final class SimpleSummaryProvider implements SummaryProvider<DirectoryPreference>{
    private static SimpleSummaryProvider sSimpleSummaryProvider;
    private SimpleSummaryProvider(){}
    public static SimpleSummaryProvider getInstance(){
        if(sSimpleSummaryProvider == null){
            sSimpleSummaryProvider = new SimpleSummaryProvider();
        }
        return sSimpleSummaryProvider;
    }
    @Override
    public CharSequence provideSummary(DirectoryPreference preference){
        if(TextUtils.isEmpty(preference.getText())){
            return (preference.getContext().getString(androidx.preference.R.string.not_set));
        } else {
            return preference.getText();
        }
    }
}


@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
