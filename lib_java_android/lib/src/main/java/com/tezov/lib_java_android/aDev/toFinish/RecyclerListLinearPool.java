/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.aDev.toFinish;

import com.tezov.lib_java.debug.DebugLog;
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

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLinear;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RecyclerListLinearPool extends RecyclerListLinear{
private static LinkedHashMap<Long, RecyclerListLinearPool.PoolDetails> recyclerPools = null;
private final Long uid = AppUIDGenerator.nextLong();
private WR<RecyclerListLinearPool> parent = null;
private List<WR<RecyclerListLinearPool>> children = null;

public RecyclerListLinearPool(Context context){
    super(context);
}

public RecyclerListLinearPool(Context context, AttributeSet attrs){
    super(context, attrs);
}

public RecyclerListLinearPool(Context context, AttributeSet attrs, int defStyle){
    super(context, attrs, defStyle);
}

public RecyclerListLinearPool setParent(RecyclerListLinearPool parent){
    acceptParent(parent);
    return this;
}

private void abandonParent(){
    unregisterPool();
    if((parent != null) && (parent.get().children != null)){
        for(WR<RecyclerListLinearPool> child: children){
            if(child.get() == this){
                parent.get().children.remove(child);
            }
        }
        if(parent.get().children.size() <= 0){
            parent.get().children = null;
        }
    }
    parent = null;
}

private void acceptParent(RecyclerListLinearPool parent){
    abandonParent();
    if(parent == null){
        return;
    }
    this.parent = WR.newInstance(parent);
    if(parent.children == null){
        parent.children = new ArrayList<>();
    }
    parent.children.add(WR.newInstance(this));
    registerPool();
}

private void unregisterPool(){
    PoolDetails pool = null;
    if(parent != null){
        pool = recyclerPools.get(parent.get().uid);
    } else {
        for(PoolDetails p: recyclerPools.values()){
            if(p.ref == getRecycledViewPool()){
                pool = p;
                break;
            }
        }
    }
    if(pool != null){
        setRecycledViewPool(pool.unregister());
        if(pool.numberChildUseThisPool <= 0){
//NOW should use key
            recyclerPools.remove(pool);
        }
    }
    if(recyclerPools.size() <= 0){
        recyclerPools = null;
    }
}

private void registerPool(){
    unregisterPool();
    if(recyclerPools == null){
        return;
    }
    PoolDetails pool = recyclerPools.get(parent.get().uid);
    if(pool != null){
        setRecycledViewPool(pool.register());
    }
}

private void abandonChildren(){
    if(children == null){
        return;
    }
    if(children.size() > 0){
        for(WR<RecyclerListLinearPool> child: children){
            child.get().parent = null;
        }
    }
    children = null;
    PoolDetails pool = recyclerPools.get(uid);
    if((pool != null) && (pool.numberChildUseThisPool <= 0)){
//NOW should use key
        recyclerPools.remove(pool);
    }
    if(recyclerPools.size() <= 0){
        recyclerPools = null;
    }
}

public RecyclerListLinearPool enableSharedPoolForChildren(){
    if(recyclerPools == null){
        recyclerPools = new LinkedHashMap<>();
    }
    PoolDetails pool = recyclerPools.get(uid);
    if(pool == null){
        pool = new PoolDetails();
        recyclerPools.put(uid, pool);
    }
    if(children.size() > 0){
        for(WR<RecyclerListLinearPool> child: children){
            child.get().unregisterPool();
            child.get().setRecycledViewPool(pool.register());
        }
    }
    return this;
}

public RecyclerListLinearPool disableSharedPoolForChildren(){
    PoolDetails pool = recyclerPools.remove(uid);
    if(recyclerPools.size() <= 0){
        recyclerPools = null;
    }
    if(pool == null){
        return this;
    }
    if(children.size() > 0){
        for(WR<RecyclerListLinearPool> child: children){
            if(child.get().getRecycledViewPool() != null){
                child.get().setRecycledViewPool(pool.unregister());
            }
        }
    }
    return this;
}

@Override
protected void finalize() throws Throwable{
    abandonChildren();
    unregisterPool();
    super.finalize();
}

private static class PoolDetails{
    private final RecyclerView.RecycledViewPool ref;
    private Integer numberChildUseThisPool;

    PoolDetails(){
DebugTrack.start().create(this).end();
        ref = new RecyclerView.RecycledViewPool();
        numberChildUseThisPool = 1;
    }

    public RecyclerView.RecycledViewPool register(){
        numberChildUseThisPool++;
        return ref;
    }

    public RecyclerView.RecycledViewPool unregister(){
        numberChildUseThisPool--;
        return null;
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
