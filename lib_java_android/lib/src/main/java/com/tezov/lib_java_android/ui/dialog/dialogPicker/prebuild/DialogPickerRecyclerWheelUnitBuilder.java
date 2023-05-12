/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.prebuild;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.unit.defEnumUnit;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.DialogPickerRecyclerWheelMultiple;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController.PickerType;
import com.tezov.lib_java_android.ui.form.component.dialog.defFormComponentDialog;
import com.tezov.lib_java_android.ui.recycler.prebuild.adapter.DataManagerFromList;
import com.tezov.lib_java_android.ui.recycler.prebuild.adapter.DataManagerNumber;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowBinder;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowManager;

public class DialogPickerRecyclerWheelUnitBuilder<E extends Enum<E> & defEnumUnit<E>>{
public final static Integer UNIT_KEY = Integer.MIN_VALUE;
public final static Integer SEP_KEY = 0;

private final defFormComponentDialog picker;
private int integerPrecision = 0;
private int decimalPrecision = 0;
private E initialUnit = null;
private String sep = null;
private Integer modulo = 10;

private DialogPickerRecyclerWheelUnitBuilder(defFormComponentDialog picker){
DebugTrack.start().create(this).end();
    this.picker = picker;
}

public static <E extends Enum<E> & defEnumUnit<E>> DialogPickerRecyclerWheelUnitBuilder<E> from(defFormComponentDialog picker, Class<E> type){
    return new DialogPickerRecyclerWheelUnitBuilder(picker);
}

public static <E extends Enum<E> & defEnumUnit<E>> LabelRowManager<E> newUnitRowManager(Class<E> type, int layoutID){
    return DataManagerFromList.newRowManager(type, layoutID, new LabelRowBinder.AdapterItem<E>(){
        @Override
        public <T> T extractValue(int id, Class<T> type, E item){
            return (T)item.name();
        }
    });
}

public DialogPickerRecyclerWheelUnitBuilder<E> setIntegerPrecision(int integerPrecision){
    this.integerPrecision = integerPrecision;
    return this;
}

public DialogPickerRecyclerWheelUnitBuilder<E> setDecimalPrecision(int decimalPrecision){
    this.decimalPrecision = decimalPrecision;
    return this;
}

public DialogPickerRecyclerWheelUnitBuilder<E> setInitialUnit(E unit){
    this.initialUnit = unit;
    this.sep = unit.getSeparator();
    return this;
}

public DialogPickerRecyclerWheelUnitBuilder<E> setModulo(Integer modulo){
    this.modulo = modulo;
    return this;
}

public void build(){
    ListEntry<Integer, Object> initialValue = new ListEntry<Integer, Object>();
    DialogPickerRecyclerWheelMultiple.Param<Integer> param = PickerType.HELPER.RECYCLER_WHEEL_MULTIPLE.obtainParam(picker.getController());
    for(int i = integerPrecision; i > 0; i--){
        LabelRowManager<String> rowManagerNumber = DataManagerNumber.newRowManager(R.layout.dialog_picker_recycler_wheel_multiple_row);
        if(modulo != null){
            rowManagerNumber.<DataManagerNumber>getDataManager().setModulo(modulo);
        }
        param.setRowManager(i, rowManagerNumber);
        initialValue.add(i, "0");
    }
    if(sep != null){
        param.setSep(SEP_KEY, sep);
    }
    for(int i = 1; i <= decimalPrecision; i++){
        LabelRowManager<String> rowManagerNumber = DataManagerNumber.newRowManager(R.layout.dialog_picker_recycler_wheel_multiple_row);
        if(modulo != null){
            rowManagerNumber.<DataManagerNumber>getDataManager().setModulo(modulo);
        }
        param.setRowManager(-i, rowManagerNumber);
        initialValue.add(-i, "0");
    }
    if(initialUnit != null){
        LabelRowManager<E> rowManagerNumber = newUnitRowManager((Class<E>)initialUnit.getClass(), R.layout.dialog_picker_recycler_wheel_multiple_row);
        param.setRowManager(UNIT_KEY, rowManagerNumber);
        initialValue.add(UNIT_KEY, initialUnit);
    }
    param.setInitialValue(initialValue);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


}
