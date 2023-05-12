/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.misc;

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
import com.tezov.lib_java.data.validator.Validator;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;

public class ValidatorCredential extends Validator<String, String>{
public final static String STRING_WRONG_CREDENTIAL = "_wrong";

private final ValidatorNotEmpty<String> validatorNotEmpty;
public ValidatorCredential(){
    validatorNotEmpty = new ValidatorNotEmpty<>();
}
@Override
protected String prefix(){
    return "credential";
}
@Override
public boolean isValid(String data){
    return validatorNotEmpty.isValid(data);
}
@Override
public String getErrorMessage(String data, String key, String extra){
    if(key != null){
        return super.getErrorMessage(data, key, extra);
    } else if(!validatorNotEmpty.isValid(data)){
        return validatorNotEmpty.getErrorMessage(data);
    } else {
        return super.getErrorMessage(data, STRING_WRONG_CREDENTIAL, extra);
    }

}


}
