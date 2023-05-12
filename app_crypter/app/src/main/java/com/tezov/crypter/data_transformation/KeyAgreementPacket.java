/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.crypter.application.AppInfo;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringBase58To;

public class KeyAgreementPacket{
public final static int KEY_PUBLIC_REQUESTER = 1;
public final static int KEY_SHARE_SENDER = 2;
public final static String DATA_INVALID = "????????";
private Integer codeOperation = null;
private String signatureApp = null;
private defUid uid = null;
private String payload = null;
public KeyAgreementPacket(){

}
public boolean isValid(){
    return (uid != null) && (codeOperation != null) && (signatureApp != null) && (payload != null);
}
public String getSignatureApp(){
    return signatureApp;
}
public Integer getCodeOperation(){
    return codeOperation;
}
public KeyAgreementPacket setCodeOperation(int codeOperation){
    this.codeOperation = codeOperation;
    return this;
}
public String getPayload(){
    if(isValid()){
        return payload;
    } else {
        return null;
    }

}
public KeyAgreementPacket setPayload(String payload){
    this.payload = payload;
    return this;
}
public defUid getUid(){
    return uid;
}
public KeyAgreementPacket setUid(defUid uid){
    this.uid = uid;
    return this;
}
public String toString(boolean setSignatureLocal){
    if(setSignatureLocal){
        signatureApp = AppInfo.getSignature();
    }
    if(!isValid()){
DebugException.start().log("not valid").end();
    }
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(codeOperation);
    buffer.put(uid.toBytes());
    buffer.put(signatureApp);
    buffer.put(payload);
    return BytesTo.StringBase58(buffer.arrayPacked());
}
public void fromString(String data, int codeOperationExpected){
    byte[] bytes = StringBase58To.Bytes(data);
    if(bytes == null){
        return;
    }
    ByteBuffer buffer = ByteBuffer.wrapPacked(bytes);
    codeOperation = buffer.getInt();
    if(!Compare.equals(codeOperation, codeOperationExpected)){
        return;
    }
    uid = UidBase.fromBytes(buffer.getBytes());
    if(uid == null){
        return;
    }
    signatureApp = buffer.getString();
    if(signatureApp == null){
        return;
    }
    payload = buffer.getString();
}

}
