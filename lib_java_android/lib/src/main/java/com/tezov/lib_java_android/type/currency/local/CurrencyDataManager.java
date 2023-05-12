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

import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java.util.UtilsList;

import java.util.List;

public class CurrencyDataManager extends RecyclerListDataManager<Currency>{
private List<Currency> currencyAll;
private List<Currency> currencyFiltered = null;

public CurrencyDataManager(){
    super(Currency.class);
    Currencies.observeOnLoaded(new LoadCurrenciesObserver(this));
}

private void updateFilteredList(String s){
    if((currencyAll == null) || (s == null)){
        currencyFiltered = currencyAll;
        return;
    }
    currencyFiltered = UtilsList.filter(currencyAll, new PredicateW<Currency>(){
        @Override
        public boolean test(Currency currency){
            return currency.getName().toLowerCase().contains(s.toLowerCase());
        }
    });
}

@Override
public <T> void filter(RecyclerListDataManager.Filter<T> filter){
    filter((Filter)filter);
}

public void filter(Filter filter){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            String stringValue = filter.getValue();
            updateFilteredList(stringValue);
            getRowManager().postUpdatedAll(false);
        }
    };
    if(!postToHandler(runnable)){
        runnable.run();
    }
}

@Override
public Integer indexOf(Currency data){
    if(currencyFiltered == null){
        return null;
    }
    return currencyFiltered.indexOf(data);
}

@Override
public int size(){
    if(currencyFiltered == null){
        return 0;
    }
    return currencyFiltered.size();
}

@Override
public Currency get(int index){
    if(currencyFiltered == null){
        return null;
    }
    return currencyFiltered.get(index);
}

private static class LoadCurrenciesObserver extends ObserverValue<Boolean>{
    LoadCurrenciesObserver(Object owner){
        super(owner);
    }

    @Override
    public CurrencyDataManager getOwner(){
        return super.getOwner();
    }

    @Override
    public void onComplete(Boolean loaded){
        if(!loaded){
            return;
        }
        unsubscribe();
        CurrencyDataManager me = getOwner();
        me.currencyAll = Currencies.list();
        me.updateFilteredList(null);

    }

}

public static class Filter extends RecyclerListDataManager.Filter<String>{}

}
