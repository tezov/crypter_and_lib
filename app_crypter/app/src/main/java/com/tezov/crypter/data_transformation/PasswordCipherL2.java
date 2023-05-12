/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.data_transformation;

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
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;

public class PasswordCipherL2 extends com.tezov.lib_java.cipher.misc.PasswordCipher{
private char[] cipheredL2 = null;
protected PasswordCipherL2(){}
public static PasswordCipherL2 fromClear(char[] password){
    PasswordCipherL2 pc = new PasswordCipherL2();
    pc.ciphered = pc.scramble(password);
    return pc;
}
public static PasswordCipherL2 fromCiphered(char[] password){
    PasswordCipherL2 pc = new PasswordCipherL2();
    pc.ciphered = password;
    return pc;
}
public PasswordCipherL2 scramble(byte[] salt){
    cipheredL2 = scramble(ciphered, salt);
    return this;
}

public char[] get(){
    if(cipheredL2 == null){
DebugException.start().explode("l2 is null").end();
    }
    return cipheredL2;
}
public char[] getL0(){
    return super.get();
}

@Override
public boolean equals(Object obj){
    return super.equals(obj) && (obj instanceof PasswordCipherL2) && Compare.equals(((PasswordCipherL2)obj).cipheredL2, cipheredL2);
}

@Override
protected void finalize() throws Throwable{
    Nullify.array(cipheredL2);
    super.finalize();
}

}
