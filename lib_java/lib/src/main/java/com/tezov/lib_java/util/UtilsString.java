/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.util;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;

import org.threeten.bp.LocalDateTime;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java9.util.StringJoiner;

public class UtilsString{

public final static String NUMBER_SEPARATOR = ".";
public final static String NUMBER_NEGATIVE_SIGN = "-";
public final static int NUMBER_OVERFLOW = -1;

public static <T> StringJoiner join(CharSequence delimiter, Iterator<T> it){
    return join(new StringJoiner(delimiter), it, new FunctionW<T, String>(){
        @Override
        public String apply(T t){
            return t.toString();
        }
    });
}
public static <T> StringJoiner join(CharSequence delimiter, Collection<T> values){
    return join(new StringJoiner(delimiter), values, new FunctionW<T, String>(){
        @Override
        public String apply(T t){
            return t.toString();
        }
    });
}

public static <T> StringJoiner join(CharSequence prefix, CharSequence delimiter, Iterator<T> it){
    return join(new StringJoiner(delimiter, prefix, ""), it, new FunctionW<T, String>(){
        @Override
        public String apply(T t){
            return t.toString();
        }
    });
}
public static <T> StringJoiner join(CharSequence prefix, CharSequence delimiter, Collection<T> values){
    return join(new StringJoiner(delimiter, prefix, ""), values, new FunctionW<T, String>(){
        @Override
        public String apply(T t){
            return t.toString();
        }
    });
}

public static <T> StringJoiner join(CharSequence delimiter, Iterator<T> it, FunctionW<T, String> toString){
    return join(new StringJoiner(delimiter), it, toString);
}
public static <T> StringJoiner join(CharSequence delimiter, Collection<T> values, FunctionW<T, String> toString){
    return join(new StringJoiner(delimiter), values, toString);
}

public static <T> StringJoiner join(CharSequence prefix, CharSequence delimiter, Iterator<T> it, FunctionW<T, String> toString){
    return join(new StringJoiner(delimiter, prefix, ""), it, toString);
}
public static <T> StringJoiner join(CharSequence prefix, CharSequence delimiter, Collection<T> values, FunctionW<T, String> toString){
    return join(new StringJoiner(delimiter, prefix, ""), values, toString);
}

public static <T> StringJoiner join(StringJoiner joiner, Iterator<T> it){
    return join(joiner, it, new FunctionW<T, String>(){
        @Override
        public String apply(T t){
            return t.toString();
        }
    });
}
public static <T> StringJoiner join(StringJoiner joiner, Collection<T> values){
    return join(joiner, values, new FunctionW<T, String>(){
        @Override
        public String apply(T t){
            return t.toString();
        }
    });
}
public static <T> StringJoiner join(StringJoiner joiner, Collection<T> values, FunctionW<T, String> toString){
    return join(joiner, values != null ? values.iterator() : null, toString);
}
public static <T> StringJoiner join(StringJoiner joiner, Iterator<T> it, FunctionW<T, String> toString){
    if(it == null){
        return joiner.add("null");
    } else if(!it.hasNext()){
        return joiner.add("empty");
    } else {
        while(it.hasNext()){
            joiner.add(toString.apply(it.next()));
        }
        return joiner;
    }
}

public static String[] split(String s, int interval){
    int arrayLength = (int)Math.ceil(((s.length() / (double)interval)));
    String[] result = new String[arrayLength];
    int j = 0;
    int lastIndex = result.length - 1;
    for(int i = 0; i < lastIndex; i++){
        result[i] = s.substring(j, j + interval);
        j += interval;
    }
    result[lastIndex] = s.substring(j);
    return result;
}

public static Number parseNumber(String s){
    return parseNumber(s, NUMBER_SEPARATOR);
}
public static Number parseNumber(String s, String separator){
    if(s == null){
        return null;
    } else {
        Pattern pattern = Pattern.compile("^(" + Pattern.quote(NUMBER_NEGATIVE_SIGN) + ")?([0-9" + "]+)(" + Pattern.quote(separator) + ")?([0-9]+)?" + "([a-zA-Z]+)?$");
        Matcher matcher = pattern.matcher(s);
        if(matcher.matches()){
            return new Number(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(5));
        } else {
            return null;
        }
    }
}
public static String removeLeadingZero(String s){
    return s.replaceFirst("^0+(?!$)", "");
}
public static String removeSeparatorIfLast(String s){
    if(NUMBER_SEPARATOR.equals(String.valueOf(s.charAt(s.length() - 1)))){
        return s.substring(0, s.length() - 1);
    } else {
        return s;
    }
}
public static String randomHex(int length){
    return BytesTo.StringHex(UtilsBytes.random(length));
}
public static String randomBase49(int length){
    return BytesTo.StringBase49(UtilsBytes.random(length));
}
public static boolean containsAtLeastOne(String text, String[] separators){
    for(String sep: separators){
        if(text.contains(sep)){
            return true;
        }
    }
    return false;
}
public static String[] splitAndKeepSeparator(String text, String[] separators){
    StringBuilder patternBuilder = new StringBuilder();
    for(String sep: separators){
        patternBuilder.append("(?<=").append(sep).append(")|");
    }
    String pattern;
    if(patternBuilder.length() > 0){
        pattern = patternBuilder.substring(0, patternBuilder.length() - 1);
    } else {
        pattern = patternBuilder.toString();
    }
    return text.split(pattern);
}
public static String capitalize(String text, String separator){
    return capitalize(text, new String[]{separator});
}
public static String capitalize(String text, String[] separators){
    if(text == null){
        return null;
    } else if(!containsAtLeastOne(text, separators)){
        return capitalizeFirst(text);
    } else if(text.length() == 1){
        return text;
    } else {
        StringBuilder out = new StringBuilder();
        String[] textSplit = splitAndKeepSeparator(text, separators);
        for(String t: textSplit){
            out.append(capitalizeFirst(t));
        }
        return out.toString();
    }
}
//IMPROVE marche pas avec
// Côte D'ivoire
// Blabla (sdjlsd)
// Balala (jklsdj Mmslss)
public static String capitalizeFirst(String text){
    if(text == null){
        return null;
    } else if(text.length() == 1){
        return text.toUpperCase();
    } else {
        return text.trim().substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}

public static String appendDateAndTime(String name){
    LocalDateTime date = Clock.DateAndTime.now();
    return name + "_" + Clock.DateAndTimeTo.string(date, Clock.FormatDateAndTime.FULL_FILE_NAME);
}
public static StringBuilder appendDateAndTime(StringBuilder builder){
    LocalDateTime date = Clock.DateAndTime.now();
    return builder.append("_").append(Clock.DateAndTimeTo.string(date, Clock.FormatDateAndTime.FULL_FILE_NAME));
}
public static String insert(String s, char c, int every){
    return s.replaceAll(".{" + every + "}(?!$)", "$0" + c);
}

public static class Number{
    private final boolean negative;
    private final int integerPrecision;
    private final int decimalPrecision;
    private final String unit;
    private int integer;
    private int decimal;

    public Number(String prefix, String integer, String decimal, String unit){
        negative = Nullify.string(prefix) != null;
        if(Nullify.string(integer) != null){
            if((!negative && (integer.length() <= IntTo.MAX_DIGIT_POSITIVE)) || ((negative && (integer.length() <= IntTo.MAX_DIGIT_NEGATIVE)))){
                try{
                    this.integer = Integer.parseInt(integer);
                } catch(Throwable e){
                    this.integer = NUMBER_OVERFLOW;
                }
            } else {
                this.integer = NUMBER_OVERFLOW;
            }
            this.integerPrecision = integer.length();
        } else {
            this.integer = 0;
            this.integerPrecision = 0;
        }
        if(Nullify.string(decimal) != null){
            if(decimal.length() <= FloatTo.MAX_DIGIT_DECIMAL){
                try{
                    this.decimal = Integer.parseInt(decimal);
                } catch(Throwable e){
                    this.decimal = NUMBER_OVERFLOW;
                }
            } else {
                this.decimal = NUMBER_OVERFLOW;
            }
            this.decimalPrecision = decimal.length();
        } else {
            this.decimal = 0;
            this.decimalPrecision = 0;
        }
        this.unit = Nullify.string(unit);
    }

    public int getInteger(){
        return integer;
    }

    public int getIntegerPrecision(){
        return integerPrecision;
    }

    public int getDecimal(){
        return decimal;
    }

    public int getDecimalPrecision(){
        return decimalPrecision;
    }

    public String getUnit(){
        return unit;
    }

    public boolean isNegative(){
        return negative;
    }
    public boolean isInteger(){
        return decimalPrecision == 0;
    }
    public boolean isFloat(){
        return decimalPrecision != 0;
    }
    public boolean isOverflow(){
        return isIntegerOverflow() || isDecimalOverflow();
    }
    public boolean isDecimalOverflow(){
        return (decimal == NUMBER_OVERFLOW);
    }
    public boolean isIntegerOverflow(){
        return (integer == NUMBER_OVERFLOW);
    }

    public String toString(boolean withUnit){
        StringBuilder data = new StringBuilder();
        if(negative){
            data.append(NUMBER_NEGATIVE_SIGN);
        }
        if(integer != NUMBER_OVERFLOW){
            data.append(integer);
        } else {
            data.append(Integer.MAX_VALUE);
        }
        if(decimalPrecision > 0){
            data.append(NUMBER_SEPARATOR);
            if(decimal != NUMBER_OVERFLOW){
                data.append(String.format(Locale.US, "%0" + decimalPrecision + "d", decimal));
            } else {
                for(int end = Math.min(IntTo.MAX_DIGIT_POSITIVE, FloatTo.MAX_DIGIT_DECIMAL), i = 0; i < end; i++){
                    data.append("9");
                }
            }
        }
        if(withUnit && (unit != null)){
            data.append(unit);
        }
        return data.toString();
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("isNegative", negative);
        data.append("integerPrecision", (integerPrecision != NUMBER_OVERFLOW) ? integerPrecision : "overflow");
        data.append("decimal", decimal);
        data.append("isNegative", negative);
        data.append("decimalPrecision", (decimalPrecision != NUMBER_OVERFLOW) ? decimalPrecision : "overflow");
        data.append("unit", unit);
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

}

}
