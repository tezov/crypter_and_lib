/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.currency.local;

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
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowBinder;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowDescription;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowManager;


public class CurrenciesRecycler{
public static LabelRowManager<Currency> newRowManager(int layoutId, Type type){
    LabelRowBinder.AdapterItem<Currency> itemToString;
    if (type == Type.CODE) {
        itemToString = new LabelRowBinder.AdapterItem<Currency>() {
            @Override
            public <T> T extractValue(int id, Class<T> type, Currency currency) {
                return (T) currency.code;
            }
        };
    } else {
        DebugException.start().unknown("type", type).end();

        itemToString = null;
    }
    return new LabelRowManager<>(new CurrencyDataManager(), LabelRowDescription.label(layoutId), itemToString);
}

public static CurrencyDataManager.Filter newFilter(){
    return new CurrencyDataManager.Filter();
}


public enum Type{
    CODE
}

}
