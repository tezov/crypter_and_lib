/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.defEnum;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
public interface Event{
Is ON_RECONSTRUCT = new Is("ON_RECONSTRUCT");
Is ON_OPEN = new Is("ON_OPEN");
Is OPENED = new Is("OPENED");
Is ON_START = new Is("ON_START");
Is STARTED = new Is("STARTED");
Is ON_CLOSE = new Is("ON_CLOSE");
Is CLOSED = new Is("CLOSED");
Is ON_STOP = new Is("ON_STOP");
Is STOPPED = new Is("STOPPED");
Is ON_SLIDE = new Is("ON_SLIDE");
Is ON_SELECT = new Is("ON_SELECT");
Is ON_DESELECT = new Is("ON_DESELECT");
Is ON_CLICK_SHORT = new Is("ON_CLICK_SHORT");
Is ON_CLICK_LONG = new Is("ON_CLICK_LONG");
Is ON_CANCEL = new Is("ON_CANCEL");
Is ON_CONFIRM = new Is("ON_CONFIRM");
Is ON_EXCEPTION = new Is("ON_EXCEPTION");
Is ON_ACTION = new Is("ON_ACTION");
Is ON_TOGGLE = new Is("ON_TOGGLE");
Is ON_CHANGE = new Is("ON_CHANGE");
Is ON_KEYBOARD_DONE = new Is("ON_KEYBOARD_DONE");
Is ON_INSERT = new Is("ON_INSERT");
Is ON_UPDATE = new Is("ON_UPDATE");
Is ON_REMOVE = new Is("ON_REMOVE");

class Is extends EnumBase.Is{
    public Is(String name){
        super(name);
    }

}

}
