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

public class CountriesDataManager extends RecyclerListDataManager<Country>{
private List<Country> countriesAll;
private List<Country> countriesFiltered = null;

public CountriesDataManager(){
    super(Country.class);
    Countries.observeOnLoaded(new LoadCountriesObserver(this));
}

private void updateFilteredList(String s){
    if((countriesAll == null) || (s == null)){
        countriesFiltered = countriesAll;
        return;
    }
    countriesFiltered = UtilsList.filter(countriesAll, new PredicateW<Country>(){
        @Override
        public boolean test(Country country){
            return country.getName().toLowerCase().contains(s.toLowerCase());
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
public Integer indexOf(Country data){
    if(countriesFiltered == null){
        return null;
    }
    return countriesFiltered.indexOf(data);
}

@Override
public int size(){
    if(countriesFiltered == null){
        return 0;
    }
    return countriesFiltered.size();
}

@Override
public Country get(int index){
    if(countriesFiltered == null){
        return null;
    }
    return countriesFiltered.get(index);
}

private static class LoadCountriesObserver extends ObserverValue<Boolean>{
    LoadCountriesObserver(Object owner){
        super(owner);
    }

    @Override
    public CountriesDataManager getOwner(){
        return super.getOwner();
    }

    @Override
    public void onComplete(Boolean countriesLoaded){
        if(!countriesLoaded){
            return;
        }
        unsubscribe();
        CountriesDataManager me = getOwner();
        me.countriesAll = Countries.list();
        me.updateFilteredList(null);

    }

}

public static class Filter extends RecyclerListDataManager.Filter<String>{}

}
