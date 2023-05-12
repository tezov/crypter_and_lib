/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.definition;

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

import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.List;

public interface defProviderAsync<T> extends defDeletableAsync<T>{
TaskState.Observable acquireLock(Object inquirer, Long maxRetainTime_ms, Long acquireRequestMaxRetainTime_ms, RunnableW runnableOnTimeOut);

TaskState.Observable releaseLock(Object inquirer);

TaskValue<Integer>.Observable getFirstIndex();

TaskValue<Integer>.Observable getLastIndex();

TaskValue<Integer>.Observable size();

TaskValue<T>.Observable get(int index);

TaskValue<List<T>>.Observable select(int offset, int length);

TaskValue<Integer>.Observable indexOf(T t);

TaskState.Observable toDebugLog();

}
