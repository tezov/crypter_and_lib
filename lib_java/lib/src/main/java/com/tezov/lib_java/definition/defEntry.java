/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.definition;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
public interface defEntry<ENTRY_TYPE>{
Class<ENTRY_TYPE> getEntryType();
void attach(defEntry<?>  entry);
void link(defEntry<?>  entry);
<T> boolean isAcceptedType(Class<T> type);
boolean setValue(ENTRY_TYPE value);
<T> boolean setValue(Class<T> type, T value);
boolean setValueFrom(defEntry<?>  entry);
default <T> void onSetValue(Class<T> type){

}
<T> T getValue(Class<T> type);
ENTRY_TYPE getValue();
default boolean command(Object o){
    return true;
}

}
