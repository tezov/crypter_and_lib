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
import com.tezov.lib_java.data.validator.ValidatorBaseNoKey;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;

public class ValidatorImportKeyPublic extends ValidatorBaseNoKey<String>{

private final ValidatorKeystoreLocked validatorKeystoreLocked;
private final ValidatorNotEmpty<String> validatorNotEmpty;

public ValidatorImportKeyPublic(){
    validatorKeystoreLocked = new ValidatorKeystoreLocked();
    validatorNotEmpty = new ValidatorNotEmpty<>();
}
@Override
protected String prefix(){
    return "import_key_public";
}
@Override
public boolean isValid(String data){
    return validatorNotEmpty.isValid(data) && validatorKeystoreLocked.isValid(data);
}
@Override
public String getErrorMessage(String data){
    if(!validatorNotEmpty.isValid(data)){
        return validatorNotEmpty.getErrorMessage(data);
    } else if(!validatorKeystoreLocked.isValid(data)){
        return validatorKeystoreLocked.getErrorMessage(data);
    } else {
        return super.getErrorMessage(data);
    }
}

}
