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
import com.tezov.lib_java.type.primitive.string.StringBase49To;
import com.tezov.lib_java.type.primitive.string.StringBase58To;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase64To;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.primitive.string.StringHexCharTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

public class DataStringDecoderAdapter extends DataStringAdapter{

DataStringDecoderAdapter(Format format){
    super(format);
}

@Override
public byte[] fromString(String data){
    switch(format){
        case HEX:return StringHexTo.Bytes(data);
        case HEX_CHAR:return StringHexCharTo.Bytes(data);
        case BASE49:return StringBase49To.Bytes(data);
        case BASE58:return StringBase58To.Bytes(data);
        case BASE64: return StringBase64To.Bytes(data);
        case CHAR:return StringCharTo.Bytes(data);
        default:{
DebugException.start().unknown("format", format).end();

            return null;
        }
    }
}
@Override
public String toString(byte[] bytes){
    return BytesTo.StringChar(bytes);
}


}
