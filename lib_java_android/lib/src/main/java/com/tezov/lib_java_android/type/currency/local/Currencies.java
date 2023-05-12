/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.currency.local;

import com.tezov.lib_java.debug.DebugTrack;
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

import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java_android.type.country.Countries;
import com.tezov.lib_java_android.type.country.Country;
import com.tezov.lib_java.type.runnable.RunnableFuture;
import com.tezov.lib_java.type.runnable.RunnableThread;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.util.UtilsList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Currencies{
private static final Notifier<Void> notifier;
private static RunnableThread runnableLoad = null;
private static RunnableThread runnableRelease = null;
private static List<Currency> currencies = null;

static{
    ObservableValue<Boolean> observable = new ObservableValue<>();
    ObservableValue<Boolean>.Access access = observable.obtainAccess(null);
    notifier = new Notifier<>(observable, true);
    access.setValue(false);
}

private Currencies(){
}

private static Class<Currencies> myClass(){
    return Currencies.class;
}

public static Notifier.Subscription observeOnLoaded(ObserverValue<Boolean> observer){
    return notifier.register(observer);
}

public static void unObserve(Object owner){
    notifier.unregister(owner);
}

public static void unObserveAll(){
    notifier.unregisterAll();
}

private static void postStatus(){
    ObservableValue<Boolean>.Access access = notifier.obtainAccess(myClass(), null);
    access.setValue(currencies != null);

DebugLog.start().send(myClass(), currencies != null ? "Countries loaded" : "Countries unLoaded").end();


}

synchronized private static List<Currency> buildList(){
    Iterator<java.util.Currency> currencyIterator = java.util.Currency.getAvailableCurrencies().iterator();
    currencies = new ArrayList<>();
    List<String> codes = new ArrayList<>();
    while(currencyIterator.hasNext()){
        try{
            java.util.Currency c = currencyIterator.next();
            String name = c.getDisplayName();
            String code = c.getCurrencyCode();
            if(!"".equals(code) && !"".equals(name) && !codes.contains(code)){
                codes.add(code);
                currencies.add(new Currency(code, name));
            }
        } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        }
    }
    currencies = Nullify.collection(currencies);
    if(currencies != null){
        currencies = UtilsList.sort(currencies, new ComparatorW<Currency>(){
            @Override
            public int compare(Currency o1, Currency o2){
                return o1.getCode().compareTo(o2.getCode());
            }
        });
    }
    releaseDelayed();
    return currencies;
}

synchronized public static List<Currency> list(){
    if(currencies != null){
        return currencies;
    }
    RunnableThread r = runnableLoad;
    if(r == null){

DebugException.start().logHidden("Load currencies and use observe(...) to detect when ready for better performance").end();

        return buildList();
    } else {

DebugException.start().logHidden("currencies is loading. Use observe(...) to detect when ready for better performance").end();

        new RunnableFuture<Void>(myClass()){
            @Override
            public void runSafe(){
                observeOnLoaded(new ObserverValue<Boolean>(this){
                    @Override
                    public void onComplete(Boolean aBoolean){
                        done();
                    }
                });
            }
        }.start();
        return currencies;
    }
}

synchronized public static void load(){
    load(0, TimeUnit.MILLISECONDS);
}

synchronized public static void load(Handler.Delay delay){
    load(delay.millisecond(), TimeUnit.MILLISECONDS);
}

synchronized public static void load(long delay, TimeUnit timeUnit){
    if(runnableLoad != null){
        return;
    }
    if(currencies != null){
        releaseDelayed();
        postStatus();
        return;
    }
    runnableLoad = new RunnableThread(myClass()){
        @Override
        public void runSafe(){
            runnableLoad = null;
            buildList();
            postStatus();
        }
    };
    runnableLoad.post(delay, timeUnit);
}

synchronized protected static void releaseDelayed(){
    if(runnableRelease != null){
        runnableRelease.cancel();
        runnableRelease = null;
    }
    if(currencies == null){
        postStatus();
        return;
    }
    runnableRelease = new RunnableThread(myClass()){
        @Override
        public void runSafe(){
            runnableRelease = null;
            release();
            postStatus();
        }
    };
    runnableRelease.post(Handler.Delay.DELAY_5, TimeUnit.MINUTES);
}

synchronized public static void release(){
    if(runnableRelease != null){
        runnableRelease.cancel();
        runnableRelease = null;
    }
    if(currencies == null){
        return;
    }
    currencies = null;
}

synchronized public static Currency random(){
    return list().get(AppRandomNumber.nextInt(currencies.size()));
}

synchronized public static Currency findWithName(String name){
    List<Currency> currencies = list();
    if(currencies == null){
        return null;
    }
    for(Currency currency: currencies){
        if(currency.getName().equals(name)){
            return currency;
        }
    }
    return null;
}

synchronized public static Currency findWithCode(String code){
    List<Currency> currencies = list();
    if(currencies == null){
        return null;
    }
    for(Currency currency: currencies){
        if(currency.getCode().equals(code)){
            return currency;
        }
    }
    return null;
}

synchronized public static String findName(String code){
    Currency currency = findWithCode(code);
    if(currency == null){
        return null;
    }
    return currency.getName();
}

synchronized public static String findCode(String name){
    Currency currency = findWithName(name);
    if(currency == null){
        return null;
    }
    return currency.getCode();
}

synchronized public static Currency getCurrent(){
    Country country = Countries.getCurrent();
    if(country == null){
        return null;
    } else {
        return country.getCurrency();
    }
}

public static void toDebugLog(){
    List<Currency> currencies = list();
    if(currencies != null){
        for(Currency currency: currencies){
            currency.toDebugLog();
        }
    } else {
DebugLog.start().send("Currency list is null").end();
    }
}

public boolean isReady(){
    ObservableEvent<Void, Boolean>.Access access = notifier.obtainAccess(this, null);
    return access.getValue();
}

}
