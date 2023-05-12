/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.sqlLite.adapter;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import androidx.fragment.app.Fragment;

import android.database.Cursor;

import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java_android.definition.defCreatable;
import com.tezov.lib_java.type.primaire.Field;
import com.tezov.lib_java_android.type.android.wrapper.ParcelW;

import java.util.List;

public interface defCursorTo{
void setDecoderValue(defDecoder decryptValue);
<T> T valueFrom(Cursor cursor, Field field, int fieldOffset);
ParcelW parcel(Cursor cursor, List<Field> fields);
<ITEM> ITEM item(Cursor cursor, List<Field> fields, defCreatable<ITEM> factory);

}

