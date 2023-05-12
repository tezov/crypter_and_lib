/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.keyMaker.keySim;

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
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataBytesToStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.definition.defDataAdapterDecoder;
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;

public class KeySimMaker_BytesToString extends KeySimMaker<byte[], String>{
private final DataStringAdapter.Format format;
public KeySimMaker_BytesToString(PasswordCipher password, DataStringAdapter.Format format){
    super(password);
    this.format = format;
}
@Override
protected defDataAdapterEncoder<byte[], String> newAdapterEncoder(){
    return DataBytesToStringAdapter.forEncoder(format);
}
@Override
protected defDataAdapterDecoder<byte[], String> newAdapterDecoder(){
    return DataBytesToStringAdapter.forDecoder(format);
}

}
