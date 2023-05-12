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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import static com.tezov.lib_java.toolbox.Clock.FormatDate.DAY_MONTH_YEAR;

import com.tezov.lib_java.toolbox.Clock;

public class ValidatorDate extends ValidatorBaseNoKey<String>{
private Clock.FormatDate formatDate = DAY_MONTH_YEAR;

@Override
public boolean isValid(String data){
    if(data == null){
        return false;
    } else {
        return Clock.StringTo.Date.with(data, formatDate) != null;
    }
}

public ValidatorDate setFormatDate(Clock.FormatDate formatDate){
    this.formatDate = formatDate;
    return this;
}

@Override
protected String prefix(){
    return "date";
}

@Override
public String getErrorMessage(String data){
    return super.getErrorMessage(data) + " " + formatDate.pattern().toLowerCase();
}

}
