/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.data.validator;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.application.AppContext;
import com.tezov.lib_java.debug.DebugTrack;

public abstract class Validator<TYPE, KEY>{
private final static String ATTRIBUTE = "validator_";

public Validator(){
DebugTrack.start().create(this).end();
}
public abstract boolean isValid(TYPE data);

protected abstract String prefix();
private String getValidatorName(){
    return ATTRIBUTE + prefix();
}

public String getErrorMessageResourceName(String suffix){
    String resourceName = getValidatorName();
    if(suffix != null){
        resourceName += suffix;
    }
    return resourceName;
}
protected String getErrorMessageResourceSuffix(TYPE data, KEY k){
    return (String)k;
}

public String getErrorMessage(TYPE data, KEY k, String extra){
    String message = getString(getErrorMessageResourceSuffix(data, k));
    if(extra != null){
        message += extra;
    }
    return message;
}

final protected String getString(String suffix){
    return AppContext.getResources().getString(getErrorMessageResourceName(suffix));
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
