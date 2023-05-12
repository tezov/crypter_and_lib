/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.currency.display;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowBinder;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowDescription;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowManager;

import java.util.List;

public class CurrenciesDisplayRecycler{
public static LabelRowManager<CurrencyDisplay> newRowManager(int layoutId, List<CurrencyDisplay> currencies){
    LabelRowBinder.AdapterItem<CurrencyDisplay> itemToString = new LabelRowBinder.AdapterItem<CurrencyDisplay>(){
        @Override
        public <T> T extractValue(int id, Class<T> type, CurrencyDisplay currency){
            if(id == R.id.lbl_label){
                return (T)currency.text;
            } else {
                if(id == R.id.img_icon){
                    return (T)currency.getFlag();
                }
            }
            return null;
        }
    };
    return new LabelRowManager<>(new CurrencyDisplayDataManager(currencies), LabelRowDescription.labelIcon(layoutId), itemToString);
}

}
