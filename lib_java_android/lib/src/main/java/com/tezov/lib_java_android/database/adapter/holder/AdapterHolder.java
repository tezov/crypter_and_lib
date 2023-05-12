/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.adapter.holder;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.cipher.holder.CipherHolderCrypto;
import com.tezov.lib_java_android.database.adapter.contentValues.ContentValuesCryptBlockTo;
import com.tezov.lib_java_android.database.adapter.contentValues.ContentValuesCryptRowTo;
import com.tezov.lib_java_android.database.adapter.contentValues.ContentValuesTo;
import com.tezov.lib_java_android.database.adapter.definition.defContentValuesTo;
import com.tezov.lib_java_android.database.adapter.definition.defParcelTo;
import com.tezov.lib_java_android.database.adapter.parcel.ParcelTo;
import com.tezov.lib_java_android.database.adapter.parcel.ParcelToCryptBlock;
import com.tezov.lib_java_android.database.adapter.parcel.ParcelToCryptRow;

import static com.tezov.lib_java_android.database.adapter.holder.AdapterHolder.Mode.NONE;

public class AdapterHolder{
private defParcelTo parcelTo;
private defContentValuesTo contentValuesTo;

public AdapterHolder(Mode mode, CipherHolderCrypto cipherHolder){
    switch(mode){
        case NONE:{
            parcelTo = new ParcelTo();
            contentValuesTo = new ContentValuesTo();
        }
        break;
        case ROW:{
            parcelTo = new ParcelToCryptRow();
            contentValuesTo = new ContentValuesCryptRowTo();
        }
        break;
        case BLOCK:{
            parcelTo = new ParcelToCryptBlock();
            contentValuesTo = new ContentValuesCryptBlockTo();
        }
        break;
    }
    if(mode != NONE){
        parcelTo.setEncoderValue(cipherHolder.getEncoderValue());
        contentValuesTo.setDecoderValue(cipherHolder.getDecoderValue());
    }
    init(mode, cipherHolder);
}
protected void init(Mode mode, CipherHolderCrypto cipherHolder){
}

public defParcelTo getParcelTo(){
    return parcelTo;
}
public defContentValuesTo getContentValuesTo(){
    return contentValuesTo;
}


public enum Mode{
    NONE, ROW, BLOCK,
}

}
