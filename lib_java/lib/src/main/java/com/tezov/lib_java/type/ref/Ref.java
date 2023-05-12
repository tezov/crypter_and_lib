/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.ref;

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
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public abstract class Ref<T>{
private RefInfo<T> refInfo;

protected Ref(){
DebugTrack.start().create(this).end();
}

public static <T> boolean isNull(Ref<T> ref){
    return (ref == null) || (ref.isNull());
}

public static <T> boolean isNotNull(Ref<T> ref){
    return (ref != null) && (ref.isNotNull());
}

public static <T> T get(Ref<T> ref){
    if((ref == null) || ref.isNull()){
        return null;
    } else {
        return ref.get();
    }
}

public static <T> Class<T> getType(Ref<T> ref){
    if(ref == null){
        return null;
    }
    return ref.getType();
}

public static boolean equals(Ref r1, Ref r2){
    if(r1 != null){
        return r1.equals(r2);
    } else if(r2 != null){
        return r2.isNull();
    } else {
        return true;
    }
}

public static boolean equals(Object obj, Ref r2){
    return equals(r2, obj);
}

public static boolean equals(Ref r1, Object obj){
    if(r1 != null){
        return r1.equals(obj);
    } else {
        return obj == null;
    }
}

protected void updateInfo(T referent){
    if(referent != null){
        refInfo = new RefInfo<>(referent);
    }
}

public boolean hasInfo(){
    return refInfo != null;
}

public RefInfo<T> info(){
    return refInfo;
}

public Class<T> getType(){
    if(refInfo != null){
        return refInfo.type;
    } else {
        return null;
    }
}

public boolean isNull(){
    return get() == null;
}

public boolean isNotNull(){
    return get() != null;
}

@Override
public int hashCode(){
    if(hasInfo()){
        return refInfo.hashCode();
    } else {
        return 0;
    }
}

public String hashCodeString(){
    if(hasInfo()){
        return refInfo.hashCodeString();
    } else {
        return null;
    }
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof Ref)){
        if((obj != null) && this.isNotNull()){
            return this.hashCode() == obj.hashCode();
        } else {
            return (obj == null) && this.isNull();
        }
    } else {
        Ref r2 = (Ref)obj;
        if(r2.isNotNull() && this.isNotNull()){
            return this.hashCode() == r2.hashCode();
        } else {
            return r2.isNull() && this.isNull();
        }
    }
}

public abstract T get();

public abstract void set(T referent);

public DebugString toDebugString(){
    DebugString data = new DebugString();
    if(isNull()){
        data.append("[Ref Lost ");
    } else {
        data.append("[Ref Active");
    }
    if(refInfo != null){
        data.append(":" + DebugTrack.getFullSimpleName(getType()) + ":" + hashCode() + "]");
    } else {
        data.append(":class information is null]");
    }
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

public class LostRef extends WeakReference<T>{
    protected LostRef(T t, ReferenceQueue<? super T> q){
        super(t, q);
    }

    //        public String getName() {
    //            return refInfo.getFullName();
    //        }
    //        public String getClassSimpleName() {
    //            return refInfo.getFullSimpleName();
    //        }
    //        public String getNameWithHashcode() {
    //            return refInfo.getFullNameWithHashcode();
    //        }

    @Override
    public int hashCode(){
        return refInfo.hashCode();
    }

    public String hashCodeString(){
        return refInfo.hashCodeString();
    }

    @Override
    public boolean equals(Object obj){
        return refInfo.hashCode() == obj.hashCode();
    }

}

}
