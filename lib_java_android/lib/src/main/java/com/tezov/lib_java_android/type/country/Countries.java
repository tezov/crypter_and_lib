/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.country;

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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.runnable.RunnableFuture;
import com.tezov.lib_java.type.runnable.RunnableThread;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.util.UtilsList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Countries{
public final static String LOCAL_LANGUAGE = "en";
private static final Notifier<Void> notifier;
private static RunnableThread runnableLoad = null;
private static RunnableThread runnableRelease = null;
private static List<Country> countries = null;

static{
    ObservableValue<Boolean> observable = new ObservableValue<>();
    ObservableValue<Boolean>.Access access = observable.obtainAccess(null);
    notifier = new Notifier<>(observable, true);
    access.setValue(false);
}

private Countries(){
}

private static Class<Countries> myClass(){
    return Countries.class;
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
    access.setValue(countries != null);

DebugLog.start().send(myClass(), countries != null ? "Countries loaded" : "Countries unLoaded").end();

}

synchronized private static List<Country> buildList(){
    String[] isoCountries = Locale.getISOCountries();
    countries = new ArrayList<>();
    List<String> codes = new ArrayList<>();
    for(String iso2: isoCountries){
        try{
            Locale locale = new Locale(LOCAL_LANGUAGE, iso2);
            String name = locale.getDisplayCountry();
            String code = locale.getCountry();
            if(!"".equals(iso2) && !"".equals(code) && !"".equals(name) && !codes.contains(code)){

                if(!iso2.equals(code)){
DebugException.start().log("code and iso2 mismatch").end();
                }

                codes.add(code);
                countries.add(new Country(iso2, name));
            }
        } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

        }
    }
    countries = Nullify.collection(countries);
    if(countries != null){
        countries = UtilsList.sort(countries, new ComparatorW<Country>(){
            @Override
            public int compare(Country o1, Country o2){
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    releaseDelayed();
    return countries;
}

synchronized public static List<Country> list(){
    if(countries != null){
        return countries;
    }
    RunnableThread r = runnableLoad;
    if(r == null){

DebugException.start().logHidden("Load country and use observe(...) to detect when ready for better performance").end();

        return buildList();
    } else {

DebugException.start().logHidden("country is loading. Use observe(...) to detect when ready for  better performance").end();

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
        return countries;
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
    if(countries != null){
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
    if(countries == null){
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
    if(countries == null){
        return;
    }
    countries = null;
}

synchronized public static Country random(){
    List<Country> countries = list();
    if(countries == null){
        return null;
    }
    return countries.get(AppRandomNumber.nextInt(countries.size()));
}

synchronized public static Country findWithName(String name){
    List<Country> countries = list();
    if(countries == null){
        return null;
    }
    for(Country country: countries){
        if(country.getName().equals(name)){
            return country;
        }
    }
    return null;
}

synchronized public static Country findWithISO2(String iso2){
    List<Country> countries = list();
    if(countries == null){
        return null;
    }
    for(Country country: countries){
        if(country.getIso2().equals(iso2)){
            return country;
        }
    }
    return null;
}

synchronized public static Country findWithISO3(String iso3){
    List<Country> countries = list();
    if(countries == null){
        return null;
    }
    for(Country country: countries){
        if(country.getIso3().equals(iso3)){
            return country;
        }
    }
    return null;
}

public static String findFlag(String iso2){
    final int flagOffset = 0x1F1E6;
    final int asciiOffset = 0x41;
    int firstLetter = Character.codePointAt(iso2, 0) - asciiOffset + flagOffset;
    int secondLetter = Character.codePointAt(iso2, 1) - asciiOffset + flagOffset;
    return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
}

synchronized public static String findName(String iso2){
    Country country = findWithISO2(iso2);
    if(country == null){
        return null;
    }
    return country.getName();
}

synchronized public static String findIso2(String name){
    Country country = findWithName(name);
    if(country == null){
        return null;
    }
    return country.getIso2();
}

synchronized public static Country getCurrent(){
    return Countries.findWithISO2(AppContext.getResources().getLocale().getCountry());
}

public static void toDebugLog(){
    List<Country> countries = list();
    if(countries != null){
        for(Country country: countries){
            country.toDebugLog();
        }
    } else {
DebugLog.start().send("Country list is null").end();
    }
}

public boolean isReady(){
    ObservableEvent<Void, Boolean>.Access access = notifier.obtainAccess(this, null);
    return access.getValue();
}

}
