/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.data.validator;

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
import com.tezov.lib_java.file.UtilsFile;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

public class ValidatorFileExtension extends ValidatorBaseNoKey<String>{
public final static String SEP = ",";
private final ValidatorNotEmpty<String> validatorNotEmpty;
private final Pattern pattern;
private final String extensions;
public ValidatorFileExtension(String extensions){
    validatorNotEmpty = new ValidatorNotEmpty<>();
    this.extensions = extensions;
    Iterator<String> it = Arrays.asList(extensions.split(SEP)).iterator();
    StringBuilder builder = new StringBuilder();
    while(it.hasNext()){
        String extension = it.next();
        builder.append("(?:#)|".replace("#", extension));
    }
    pattern = Pattern.compile(builder.substring(0, builder.length() - 1));
}
@Override
protected String prefix(){
    return "file_extension";
}
@Override
public boolean isValid(String data){
    if(data == null){
        return false;
    }
    String extension = UtilsFile.getExtension(data);
    return validatorNotEmpty.isValid(extension) && pattern.matcher(extension.toLowerCase()).matches();
}
@Override
public String getErrorMessage(String data){
    if(!validatorNotEmpty.isValid(data)){
        return validatorNotEmpty.getErrorMessage(data);
    } else {
        return super.getErrorMessage(data) + " " + extensions;
    }
}


}
