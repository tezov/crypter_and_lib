/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.navigation.stack;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.destination.DestinationDetails;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Stack{
private final Deque<StackEntry> stack;

public Stack(){
DebugTrack.start().create(this).end();
    stack = new LinkedList<>();
}

public boolean isEmpty(){
    return stack.isEmpty();
}
public int size(){
    return stack.size();
}
public List<StackEntry> list(){
    return (List<StackEntry>)stack;
}

public StackEntry get(int index){
    return list().get(index);
}
public StackEntry get(long id){
    for(StackEntry entry: stack){
        if(entry.getBindID() == id){
            return entry;
        }
    }
    return null;
}
public StackEntry get(NavigatorManager.DestinationKey.Is key){
    for(StackEntry entry: stack){
        DestinationDetails details = entry.getDestination();
        if((details != null) && (details.getKey() == key)){
            return entry;
        }
    }
    return null;
}

public StackEntry getLast(){
    return stack.getLast();
}
public StackEntry peekLast(){
    return stack.peekLast();
}
public StackEntry peekBeforeLast(){
    if(size() <= 1){
        return null;
    } else {
        return get(size() - 2);
    }
}

public Integer indexOf(long id){
    Iterator<StackEntry> it = stack.iterator();
    for(int i = 0; it.hasNext(); i++){
        StackEntry entry = it.next();
        if(entry.getBindID() == id){
            return i;
        }
    }
    return null;
}
public Integer indexOf(StackEntry entry){
    return indexOf(entry.getBindID());
}

public StackEntry remove(StackEntry entry){
    boolean success = stack.remove(entry);
    if(success){
        entry.removedFromStack();
        return entry;
    } else {
        return null;
    }
}
public StackEntry remove(long id){
    StackEntry entry = get(id);
    if(entry != null){
        return remove(entry);
    } else {
        return null;
    }
}
public void insert(StackEntry entry, long afterId){
    int indexToInsert = indexOf(afterId) + 1;
    list().add(indexToInsert, entry);
}
public StackEntry move(long idToMove, long afterId){
    StackEntry entryToMove = get(idToMove);
    if(entryToMove != null){
        stack.remove(entryToMove);
        insert(entryToMove, afterId);
        return entryToMove;
    } else {
        return null;
    }
}
public StackEntry push(NavigatorManager.NavigatorKey.Is navigatorKey, DestinationDetails destinationDetails){
    StackEntry entry = new StackEntry(navigatorKey, destinationDetails, peekLast());
    stack.offerLast(entry);
    return entry;
}
public StackEntry push(StackEntry entry){
    stack.offerLast(entry);
    return entry;
}

public StackEntry pop(){
    return stack.pollLast();
}
public void popConfirmed(StackEntry popped, StackEntry last){
    popped.removedFromStack();
    last.updateSource(popped);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
