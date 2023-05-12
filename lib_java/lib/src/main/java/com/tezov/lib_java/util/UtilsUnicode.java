/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.util;

import com.tezov.lib_java.toolbox.CompareType;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.RangeInt;
import com.tezov.lib_java.type.primitive.IntTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UtilsUnicode{
    public static class Latin{
        final List<RangeInt> ranges;
        int length;
        public Latin(){
            ranges = new ArrayList<>();
            ranges.add(new RangeInt(0x0020, 0x007E)); //ASCII
            ranges.add(new RangeInt(0x00A0, 0x00FF)); //supplement
            ranges.add(new RangeInt(0x0100, 0x0148)); //extended-A1
            ranges.add(new RangeInt(0x0149, 0x017F)); //extended-A2
            ranges.add(new RangeInt(0x0180, 0x024F)); //extended-B
            ranges.add(new RangeInt(0x1E02, 0x1EF3)); //additional
            ranges.add(new RangeInt(0x0259, 0x0259)); //IPA extension
            ranges.add(new RangeInt(0x027C, 0x027C)); //IPA extension
            ranges.add(new RangeInt(0x0292, 0x0292)); //IPA extension
            ranges.add(new RangeInt(0x02B0, 0x02FF)); //Spacing modifier
            length = 0;
            for(RangeInt r:ranges){
                length+= r.length();
            }
        }
        public String random(){
            int value = AppRandomNumber.nextInt(length);
            int charCode = 0;
            for(RangeInt r:ranges){
                if(value < r.length()){
                    charCode = r.getMin() + value;
                    break;
                }
                value -= r.length();
            }
            return Character.toString((char)charCode);
        }
        public String random(int length){
            StringBuilder builder = new StringBuilder();
            for(int i=0; i<length; i++){
                builder.append(random());
            }
            return builder.toString();
        }
    }
}
