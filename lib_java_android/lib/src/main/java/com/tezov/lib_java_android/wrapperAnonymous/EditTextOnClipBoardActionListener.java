/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.wrapperAnonymous;

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
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.component.plain.EditText;

public abstract class EditTextOnClipBoardActionListener{
private boolean enabled = true;
public EditTextOnClipBoardActionListener(){
DebugTrack.start().create(this).end();
}
public boolean isEnabled(){
    return enabled;
}
public void setEnabled(boolean enabled){
    this.enabled = enabled;
}

public void onPaste(EditText editText){}
public void onCut(EditText editText){}
public void onCopy(EditText editText){}
public void onShare(EditText editText){}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
