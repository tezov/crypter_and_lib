/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController;

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

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerColorWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerDataWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerDate;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerRecyclerWheel;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerRecyclerWheelMultiple;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerStringUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerTime;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorDataWheelOrString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorPickers;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorRecyclerWheelDouble;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorRecyclerWheelOrString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.selectorPicker.SelectorRecyclerWheelOrStringOrConsume;

public interface PickerType{
//Single
Is DATE_YEAR = new Is("DATE_YEAR");
Is DATE_MONTH_YEAR = new Is("DATE_MONTH_YEAR");
Is DATE_DAY_MONTH_YEAR = new Is("DATE_DAY_MONTH_YEAR");
Is RECYCLER_WHEEL = new Is("RECYCLER_WHEEL");
Is RECYCLER_WHEEL_MULTIPLE = new Is("RECYCLER_WHEEL_MULTIPLE");
Is STRING = new Is("STRING");
Is STRING_UNIT = new Is("STRING_UNIT");
Is TIME = new Is("TIME");
Is DATA_WHEEL = new Is("DATA_WHEEL");
Is COLOR = new Is("COLOR");
//Compound x2
Is RECYCLER_WHEEL_DOUBLE = new Is("RECYCLER_WHEEL_DOUBLE");
Is RECYCLER_WHEEL_OR_STRING_OR_CONSUME = new Is("RECYCLER_WHEEL_OR_STRING_OR_CONSUME");
Is RECYCLER_WHEEL_OR_STRING = new Is("RECYCLER_WHEEL_OR_STRING");
Is DATA_WHEEL_OR_STRING = new Is("DATA_WHEEL_OR_STRING");

class Is extends EnumBase.Is{
    public Is(String name){
        super(name);
    }

    public DialogPickerController create(){
        //Single
        if(this == DATE_YEAR){
            return HELPER.DATE.create(DATE_YEAR);
        }
        if(this == DATE_MONTH_YEAR){
            return HELPER.DATE.create(DATE_MONTH_YEAR);
        }
        if(this == DATE_DAY_MONTH_YEAR){
            return HELPER.DATE.create(DATE_DAY_MONTH_YEAR);
        }
        if(this == RECYCLER_WHEEL){
            return HELPER.RECYCLER_WHEEL.create();
        }
        if(this == RECYCLER_WHEEL_MULTIPLE){
            return HELPER.RECYCLER_WHEEL_MULTIPLE.create();
        }
        if(this == STRING){
            return HELPER.STRING.create();
        }
        if(this == STRING_UNIT){
            return HELPER.STRING_UNIT.create();
        }
        if(this == TIME){
            return HELPER.TIME.create();
        }
        if(this == DATA_WHEEL){
            return HELPER.DATA_WHEEL.create();
        }
        if(this == COLOR){
            return HELPER.COLOR.create();
        }

        //Compound x2
        if(this == RECYCLER_WHEEL_DOUBLE){
            return HELPER.RECYCLER_WHEEL_DOUBLE.create();
        }
        if(this == RECYCLER_WHEEL_OR_STRING_OR_CONSUME){
            return HELPER.RECYCLER_WHEEL_OR_STRING_OR_CONSUME.create();
        }
        if(this == RECYCLER_WHEEL_OR_STRING){
            return HELPER.RECYCLER_WHEEL_OR_STRING.create();
        }
        if(this == DATA_WHEEL_OR_STRING){
            return HELPER.DATA_WHEEL_OR_STRING.create();
        }


DebugException.start().unknown("type", this).end();

        return null;
    }

}

class HELPER{
    //Single
    public static class DATE{
        public static DialogPickerController create(PickerType.Is type){
            return new DialogPickerController(new SelectorPickers(type));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static DialogPickerDate.Param obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    public static class RECYCLER_WHEEL{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(RECYCLER_WHEEL));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static <TYPE> DialogPickerRecyclerWheel.Param<TYPE> obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

        public static <TYPE> DialogPickerRecyclerWheel.Param<TYPE> obtainParam(PickerKey.Is key, DialogPickerController controller){
            return controller.obtainParamPicker(key);
        }

    }

    public static class RECYCLER_WHEEL_MULTIPLE{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(RECYCLER_WHEEL_MULTIPLE));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static <KEY> DialogPickerRecyclerWheelMultiple.Param<KEY> obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    public static class STRING{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(STRING));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static DialogPickerString.Param obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    public static class STRING_UNIT{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(STRING_UNIT));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static <TYPE> DialogPickerStringUnit.Param<TYPE> obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    public static class TIME{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(TIME));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static DialogPickerTime.Param obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    public static class DATA_WHEEL{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(DATA_WHEEL));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static DialogPickerDataWheel.Param obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    public static class COLOR{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorPickers(COLOR));
        }

        public static SelectorPickers.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

        public static DialogPickerColorWheel.Param obtainParam(DialogPickerController controller){
            return controller.obtainParamPicker(PickerKey.SINGLE);
        }

    }

    //Compound x2
    public static class RECYCLER_WHEEL_DOUBLE{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorRecyclerWheelDouble<>());
        }

        public static SelectorRecyclerWheelDouble.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

    }

    public static class RECYCLER_WHEEL_OR_STRING_OR_CONSUME{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorRecyclerWheelOrStringOrConsume<>());
        }

        public static SelectorRecyclerWheelOrStringOrConsume.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

    }

    public static class RECYCLER_WHEEL_OR_STRING{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorRecyclerWheelOrString<>());
        }

        public static SelectorRecyclerWheelOrString.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

    }

    public static class DATA_WHEEL_OR_STRING{
        public static DialogPickerController create(){
            return new DialogPickerController(new SelectorDataWheelOrString());
        }

        public static SelectorDataWheelOrString.Param getParamController(DialogPickerController controller){
            return controller.getParam();
        }

    }

}


}