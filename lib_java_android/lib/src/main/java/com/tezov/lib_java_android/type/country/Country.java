/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.country;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppResources;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.type.currency.local.Currencies;
import com.tezov.lib_java_android.type.currency.local.Currency;
import com.tezov.lib_java.util.UtilsString;

import java.util.Locale;

import static com.tezov.lib_java_android.type.country.Countries.LOCAL_LANGUAGE;

public class Country{
private final static String RES_FLAG_NAME_PREFIX = "flag_";
private final static String RES_FLAG_NAME_DEFAULT = "flag_earth";
private final static String[] separators = new String[]{"-", " "};
public String name;
public String iso2;
public Currency currency = null;
private String flag = null;

public Country(){
    this(null, null);
}

public Country(String iso2, String name){
DebugTrack.start().create(this).end();
    this.iso2 = iso2;
    this.name = UtilsString.capitalize(name, separators);
}

public static int getDefaultFlagResourceId(){
    return AppContext.getResources().getIdentifier(AppResources.IdentifierType.drawable, RES_FLAG_NAME_DEFAULT);
}

public static Drawable getDefaultFlagDrawable(){
    return AppContext.getResources().getDrawable(getDefaultFlagResourceId());
}

public String getIso2(){
    return iso2;
}

public String getIso3(){
    return new Locale(LOCAL_LANGUAGE, iso2).getISO3Country();
}

public String getName(){
    return name;
}

public String getFlag(){
    if(flag == null){
        flag = Countries.findFlag(iso2);
    }
    return flag;
}

public int getFlagResourceId(){
    int id = AppContext.getResources().getIdentifier(AppResources.IdentifierType.drawable, RES_FLAG_NAME_PREFIX + iso2.toLowerCase());
    if(id == AppResources.NOT_A_RESOURCE){
        id = AppContext.getResources().getIdentifier(AppResources.IdentifierType.drawable, RES_FLAG_NAME_DEFAULT);
    }
    return id;
}

public Drawable getFlagDrawable(){
    return AppContext.getResources().getDrawable(getFlagResourceId());
}

public Currency getCurrency(){
    if(currency == null){
        Locale locale = new Locale("", iso2);
        java.util.Currency currency = java.util.Currency.getInstance(locale);
        if(currency != null){
            this.currency = Currencies.findWithCode(currency.getCurrencyCode());
        }
    }
    return currency;
}

@Override
public boolean equals(@Nullable Object obj){
    if(!(obj instanceof Country)){
        return false;
    }
    return iso2.equals(((Country)obj).iso2);
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("iso2", iso2);
    data.append("name", name);
    data.append("flag", getFlag());
    data.append("currency", currency);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

