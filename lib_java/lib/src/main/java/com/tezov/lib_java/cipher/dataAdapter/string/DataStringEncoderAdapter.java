/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.dataAdapter.string;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;

public class DataStringEncoderAdapter extends DataStringAdapter{

DataStringEncoderAdapter(Format format){
    super(format);
}

@Override
public byte[] fromString(String data){
    return StringCharTo.Bytes(data);
}
@Override
public String toString(byte[] bytes){
    switch(format){
        case HEX:return BytesTo.StringHex(bytes);
        case HEX_CHAR:return BytesTo.StringHexChar(bytes);
        case BASE49:return BytesTo.StringBase49(bytes);
        case BASE58:return BytesTo.StringBase58(bytes);
        case BASE64:return BytesTo.StringBase64(bytes);
        case CHAR:return BytesTo.StringChar(bytes);
        default:{
DebugException.start().unknown("format", format).end();
            return null;
        }
    }
}


}
