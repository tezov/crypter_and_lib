/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.country;

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
import com.tezov.lib_java_android.type.currency.local.Currency;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowBinder;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowDescription;
import com.tezov.lib_java_android.ui.recycler.prebuild.row.label.LabelRowManager;

public class CountriesRecycler{
public static LabelRowManager<Country> newRowManager(int layoutId, Type type){
    LabelRowBinder.AdapterItem<Country> itemToString;
    switch(type){
        case FLAG_AND_NAME:
            itemToString = new LabelRowBinder.AdapterItem<Country>(){
                @Override
                public <T> T extractValue(int id, Class<T> type, Country country){
                    return (T)(country.getFlag() + "  " + country.name);
                }
            };
            break;
        case FLAG_AND_CURRENCY_CODE:
            itemToString = new LabelRowBinder.AdapterItem<Country>(){
                @Override
                public <T> T extractValue(int id, Class<T> type, Country country){
                    Currency currency = country.getCurrency();
                    if(currency != null){
                        return (T)(country.getFlag() + "  " + currency.getCode());
                    } else {
                        return (T)(country.getFlag() + " ...");
                    }
                }
            };
            break;
        default:{

DebugException.start().unknown("type", type).end();

            itemToString = null;
        }
    }
    return new LabelRowManager<Country>(new CountriesDataManager(), LabelRowDescription.label(layoutId), itemToString);
}

public static CountriesDataManager.Filter newFilter(){
    return new CountriesDataManager.Filter();
}


public enum Type{
    FLAG_AND_NAME, FLAG_AND_CURRENCY_CODE,
}

}
