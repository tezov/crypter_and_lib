/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase.adapter;

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
import com.tezov.lib_java_android.database.adapter.holder.AdapterHolder;

import static com.tezov.lib_java_android.database.adapter.holder.AdapterHolder.Mode.NONE;

public class AdapterHolderRemote extends AdapterHolder{
private defDataSnapshotTo dataSnapshotTo;

public AdapterHolderRemote(Mode mode, CipherHolderCrypto cipherHolder){
    super(mode, cipherHolder);
}

@Override
protected void init(Mode mode, CipherHolderCrypto cipherHolder){
    switch(mode){
        case NONE:{
            dataSnapshotTo = new DataSnapshotTo();
        }
        break;
        case ROW:{
            dataSnapshotTo = new DataSnapshotCryptRowTo();

        }
        break;
        case BLOCK:{
            dataSnapshotTo = new DataSnapshotCryptBlockTo();
        }
        break;
    }
    if(mode != NONE){
        dataSnapshotTo.setDecoderValue(cipherHolder.getDecoderValue());
    }
}

public defDataSnapshotTo getDataSnapshotTo(){
    return dataSnapshotTo;
}

}
