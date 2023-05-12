/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.pager;

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
import static com.tezov.lib_java.util.UtilsList.NULL_INDEX;

import com.tezov.crypter.pager.page.CipherTextBinder;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java_android.ui.layout.TabLayout;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;

public class PagerCipherTextDataManager extends RecyclerListDataManager<CipherTextBinder.DataText>{
private final TabLayout tabs;
private final ListEntry<Integer, CipherTextBinder.DataText> datas;
protected PagerCipherTextDataManager(TabLayout tabs){
    super(CipherTextBinder.DataText.class);
    this.tabs = tabs;
    datas = new ListEntry<>();
}
public void set(PagerCipherTextTabManager.ViewType.Is view, CipherTextBinder.DataText data){
    int index = getIndex(view);
    datas.put(index, data);
}
private int getIndex(PagerCipherTextTabManager.ViewType.Is view){
    if(view == PagerCipherTextTabManager.ViewType.IN){
        return 0;
    } else if(view == PagerCipherTextTabManager.ViewType.OUT){
        return 1;
    } else {
DebugException.start().unknown("view", view).end();
        return NULL_INDEX;
    }
}
@Override
public int size(){
    return tabs.getSize();
}
@Override
public CipherTextBinder.DataText get(int index){
    return datas.getValue(index);
}
public CipherTextBinder.DataText get(PagerCipherTextTabManager.ViewType.Is view){
    return get(getIndex(view));
}

}
