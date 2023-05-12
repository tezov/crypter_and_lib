/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;

import android.text.Editable;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnFocusChangeListenerW;
import com.tezov.lib_java.data.validator.ValidatorPortDynamic;
import com.tezov.lib_java.data.validator.ValidatorPortUser;
import com.tezov.lib_java.util.UtilsString;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsTextWatcher{
private UtilsTextWatcher(){

}

public static class FloatPrecision extends EditTextOnTextChangedListenerW{
    private final Float minValue;
    private final int integerPrecision;
    private final int decimalPrecision;
    public FloatPrecision(Integer integerPrecision, Integer decimalPrecision){
        this(null, integerPrecision, decimalPrecision);
    }
    public FloatPrecision(Float minValue, Integer integerPrecision, Integer decimalPrecision){
        this.minValue = minValue;
        if(integerPrecision == null){
            integerPrecision = IntTo.MAX_DIGIT_POSITIVE;
        }
        this.integerPrecision = integerPrecision;
        if(decimalPrecision == null){
            decimalPrecision = FloatTo.MAX_DIGIT_DECIMAL;
        }
        this.decimalPrecision = decimalPrecision;
    }
    @Override
    public void onTextChanged(com.tezov.lib_java_android.ui.component.plain.EditText editText, Editable es){
        if(editText == null){
            return;
        }
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isOverflow()){
                es.clear();
                es.append(number.toString(false));
            }
            if(number.getDecimalPrecision() > decimalPrecision){
                es.replace(es.length() - (number.getDecimalPrecision() - decimalPrecision), es.length(), "");
            }
            if(number.getIntegerPrecision() > integerPrecision){
                int position = editText.getSelectionStart();
                int lr = number.getIntegerPrecision() - integerPrecision;
                if(lr == 1){
                    es.replace(position - 1, position, "");
                } else if(s.contains(UtilsString.NUMBER_SEPARATOR) || (position >= es.length())){
                    es.replace(0, lr, "");
                } else {
                    int st = position;
                    int en = position + lr;
                    if(en > es.length()){
                        st = st - (en - es.length());
                        if(st < 0){
                            st = 0;
                        }
                        en = es.length();
                    }
                    es.replace(st, en, "");
                }
            }
            if(minValue != null){
                float value = Float.parseFloat(es.toString());
                if(value < minValue){
                    String min = String.format(Locale.US, "%." + decimalPrecision + "f", minValue);
                    es.replace(0, es.length(), min);
                }
            }
        }
        setEnabled(true);
    }

}

public static class FloatRange extends EditTextOnTextChangedListenerW{
    private final Float minValue;
    private final Float maxValue;
    private final int decimalPrecision;

    public FloatRange(Float minValue, Float maxValue, Integer decimalPrecision){
        this.minValue = minValue;
        this.maxValue = maxValue;
        if(decimalPrecision == null){
            decimalPrecision = FloatTo.MAX_DIGIT_DECIMAL;
        }
        this.decimalPrecision = decimalPrecision;
    }
    private FloatRange me(){
        return this;
    }

    @Override
    public <T extends EditTextOnTextChangedListenerW> T attach(com.tezov.lib_java_android.ui.component.plain.EditText editText){
        editText.addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
            @Override
            public void onFocusChange(EditText textView, boolean hasFocus){
                Handler.PRIMARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        me().onFocusChange((com.tezov.lib_java_android.ui.component.plain.EditText)textView, hasFocus);
                    }
                });
            }
        });
        return super.attach(editText);
    }
    @Override
    public void onTextChanged(com.tezov.lib_java_android.ui.component.plain.EditText editText, Editable es){
        if(editText == null){
            return;
        }
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isOverflow()){
                es.clear();
                es.append(number.toString(false));
            }
            if(number.getDecimalPrecision() > decimalPrecision){
                es.replace(es.length() - (number.getDecimalPrecision() - decimalPrecision), es.length(), "");
            }
            if((maxValue != null && (Float.parseFloat(es.toString()) > maxValue))){
                String max;
                max = String.format(Locale.US, "%." + decimalPrecision + "f", maxValue);
                es.replace(0, es.length(), max);
            }
        }
        setEnabled(true);
    }
    void onFocusChange(com.tezov.lib_java_android.ui.component.plain.EditText editText, boolean hasFocus){
        if(editText == null){
            return;
        }
        Editable es = editText.getEditableText();
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isOverflow()){
                es.clear();
                es.append(number.toString(false));
            }
            if(number.getDecimalPrecision() > decimalPrecision){
                es.replace(es.length() - (number.getDecimalPrecision() - decimalPrecision), es.length(), "");
            }
            if((minValue != null) && (Float.parseFloat(es.toString()) < minValue)){
                String min;
                min = String.format(Locale.US, "%." + decimalPrecision + "f", minValue);
                es.replace(0, es.length(), min);
            }
        }
        setEnabled(true);
    }

}

public static class IntPrecision extends EditTextOnTextChangedListenerW{
    private final Integer minPrecision;
    private final Integer maxPrecision;

    public IntPrecision(Integer minPrecision, Integer maxPrecision){
        this.minPrecision = minPrecision;
        if(maxPrecision == null){
            maxPrecision = IntTo.MAX_DIGIT_POSITIVE;
        }
        this.maxPrecision = maxPrecision;
    }
    private IntPrecision me(){
        return this;
    }

    @Override
    public <T extends EditTextOnTextChangedListenerW> T attach(com.tezov.lib_java_android.ui.component.plain.EditText editText){
        editText.addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
            @Override
            public void onFocusChange(EditText textView, boolean hasFocus){
                Handler.PRIMARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        me().onFocusChange((com.tezov.lib_java_android.ui.component.plain.EditText)textView, hasFocus);
                    }
                });
            }
        });
        return super.attach(editText);
    }

    @Override
    public void onTextChanged(com.tezov.lib_java_android.ui.component.plain.EditText editText, Editable es){
        if(editText == null){
            return;
        }
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isIntegerOverflow() || !number.isInteger()){
                es.clear();
                es.append(number.toString(false));
            }
            if(number.getIntegerPrecision() > maxPrecision){
                es.replace(maxPrecision, es.length(), "");
            }
        }
        setEnabled(true);
    }
    void onFocusChange(com.tezov.lib_java_android.ui.component.plain.EditText editText, boolean hasFocus){
        if(editText == null){
            return;
        }
        Editable es = editText.getEditableText();
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isIntegerOverflow() || !number.isInteger()){
                es.clear();
                es.append(number.toString(false));
            }
            if((minPrecision != null) && (number.getIntegerPrecision() < minPrecision)){
                es.clear();
            }
        }
        setEnabled(true);
    }

}

public static class IntRange extends EditTextOnTextChangedListenerW{
    private final Integer minValue;
    private final Integer maxValue;

    public IntRange(Integer minValue, Integer maxValue){
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    private IntRange me(){
        return this;
    }
    @Override
    public <T extends EditTextOnTextChangedListenerW> T attach(com.tezov.lib_java_android.ui.component.plain.EditText editText){
        editText.addFocusChangeListener(new TextViewOnFocusChangeListenerW(){
            @Override
            public void onFocusChange(EditText textView, boolean hasFocus){
                Handler.PRIMARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        me().onFocusChange((com.tezov.lib_java_android.ui.component.plain.EditText)textView, hasFocus);
                    }
                });
            }
        });
        return super.attach(editText);
    }
    @Override
    public void onTextChanged(com.tezov.lib_java_android.ui.component.plain.EditText editText, Editable es){
        if(editText == null){
            return;
        }
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isIntegerOverflow() || !number.isInteger()){
                es.clear();
                es.append(Integer.toString(number.getDecimal()));
            }
            if((maxValue != null) && (Integer.parseInt(es.toString()) > maxValue)){
                es.replace(0, es.length(), Integer.toString(maxValue));
            }
        }
        setEnabled(true);
    }
    void onFocusChange(com.tezov.lib_java_android.ui.component.plain.EditText editText, boolean hasFocus){
        if(editText == null){
            return;
        }
        Editable es = editText.getEditableText();
        if(es.length() <= 0){
            return;
        }
        String s = es.toString();
        setEnabled(false);
        if(!NumberUtils.isCreatable(s)){
            es.clear();
        } else {
            UtilsString.Number number = UtilsString.parseNumber(s);
            if(number.isIntegerOverflow() || !number.isInteger()){
                es.clear();
                es.append(Integer.toString(number.getDecimal()));
            }
            if((minValue != null) && (Integer.parseInt(es.toString()) < minValue)){
                es.replace(0, es.length(), Integer.toString(minValue));
            }
        }
        setEnabled(true);
    }

}

public static class IntRepetition extends EditTextOnTextChangedListenerW{
    protected int minValue;
    protected int maxValue;
    protected Integer repetition = null;
    protected String sep = ".";
    private String previousData = null;

    public IntRepetition(int minValue, int maxValue){
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    public IntRepetition setMinValue(Integer minValue){
        this.minValue = minValue;
        return this;
    }
    public IntRepetition setMaxValue(Integer maxValue){
        this.maxValue = maxValue;
        return this;
    }
    public IntRepetition setRepetition(Integer repetition){
        this.repetition = repetition;
        return this;
    }
    public IntRepetition setSep(String sep){
        this.sep = sep;
        return this;
    }
    @Override
    public <T extends EditTextOnTextChangedListenerW> T attach(com.tezov.lib_java_android.ui.component.plain.EditText editText){
        if(editText.getKeyListener().getInputType() != InputType.TYPE_CLASS_NUMBER){

DebugException.start().log("android:inputType=\"...\" must be number").end();

            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        Editable e = editText.getText();
        if(e != null){
            previousData = e.toString();
        }
        return super.attach(editText);
    }
    @Override
    public void onTextChanged(com.tezov.lib_java_android.ui.component.plain.EditText editText, Editable es){
        if(editText == null){
            return;
        }
        if(es.length() <= 0){
            return;
        }
        final String currentData = es.toString();
        if(previousData.length() <= es.length()){
            setEnabled(false);
            Pattern pattern = Pattern.compile("([0-9]+)|(" + Pattern.quote(sep) + ")");
            Matcher matcher = pattern.matcher(currentData);
            int countSegment = 0;
            boolean canAppendSep = false;
            StringBuilder finalData = new StringBuilder();
            while(matcher.find()){
                String data = matcher.group();
                if(sep.equals(data)){
                    if(canAppendSep){
                        finalData.append(sep);
                        canAppendSep = false;
                    }
                } else if(NumberUtils.isCreatable(data)){
                    countSegment++;
                    int value = Integer.parseInt(data);
                    if(value < minValue){
                        value = minValue;
                    } else {
                        value = Math.min(value, maxValue);
                    }
                    finalData.append(value);
                    if((repetition == null) || (repetition > countSegment)){
                        if((data.length() >= String.valueOf(maxValue).length())){
                            finalData.append(sep);
                            canAppendSep = false;
                        } else if((value == 0) && (data.length() > 1)){
                            finalData.append(sep);
                            canAppendSep = false;
                        } else {
                            canAppendSep = true;
                        }
                    }
                }
            }
            String finalDataString = finalData.toString();
            if(!currentData.equals(finalDataString)){
                es.clear();
                es.append(finalDataString);
                previousData = finalDataString;
            }
            setEnabled(true);
        } else {
            previousData = currentData;
        }
    }

}

public static class IPv4 extends IntRepetition{
    public IPv4(){
        super(0, 255);
        repetition = 4;
        sep = ".";
    }

}

public static class PortDynamic extends IntRange{
    public PortDynamic(){
        super(ValidatorPortDynamic.MIN, ValidatorPortDynamic.MAX);
    }

}

public static class PortUser extends IntRange{
    public PortUser(){
        super(ValidatorPortUser.MIN, ValidatorPortUser.MAX);
    }

}

}

