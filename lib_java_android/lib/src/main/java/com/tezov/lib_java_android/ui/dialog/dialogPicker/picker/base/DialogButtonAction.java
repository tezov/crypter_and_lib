/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base;

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
import static com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonView.ButtonPosition;

import android.view.View;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.defEnum.EnumBase;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.runnable.RunnableCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface DialogButtonAction{
int RES_IMG_COLOR_INVALID = R.color.Red;
int RES_IMG_COLOR_VALID = R.color.ForestGreen;
int RES_BG_COLOR_INVALID = R.color.LightGray;
int RES_BG_COLOR_VALID = R.color.PowderBlue;

int RES_IMG_PATH_CANCEL = R.drawable.ic_cancel_24dp;
int RES_IMG_PATH_CONFIRM = R.drawable.ic_confirm_24dp;
Is CANCEL = new Is("CANCEL");
Is CONFIRM = new Is("CONFIRM");
Creator BUTTON_DETAILS_CREATOR = ButtonDetails::new;

interface Resource{
    int imageId();

    int imageColorId();

    Integer backgroundColorId();

}

interface Creator{
    ButtonDetails create(DialogButtonAction.Is action);

}

class Is extends EnumBase.Is{
    Resource resources = null;
    public Is(String name){
        super(name);
    }

    protected Resource getResources(){
        if(resources == null){
            if(this == CANCEL){
                resources = new Resource(){
                    @Override
                    public int imageId(){
                        return RES_IMG_PATH_CANCEL;
                    }

                    @Override
                    public int imageColorId(){
                        return RES_IMG_COLOR_INVALID;
                    }

                    @Override
                    public Integer backgroundColorId(){
                        return RES_BG_COLOR_INVALID;
                    }
                };
            } else {
                if(this == CONFIRM){
                    resources = new Resource(){
                        @Override
                        public int imageId(){
                            return RES_IMG_PATH_CONFIRM;
                        }

                        @Override
                        public int imageColorId(){
                            return RES_IMG_COLOR_VALID;
                        }

                        @Override
                        public Integer backgroundColorId(){
                            return RES_BG_COLOR_VALID;
                        }
                    };
                } else {
                    return null;
                }
            }
        }
        return resources;
    }

    public void setResources(Resource resources){
        this.resources = resources;
    }

    public static void setResources(ListEntry<Is, Resource> resources){
        for(Entry<Is, Resource> e: resources){
            e.key.resources = e.value;
        }
    }

    public int getImageId(){
        return getResources().imageId();
    }

    public int getImageColorId(){
        return getResources().imageColorId();
    }

    public Integer getBackgroundColorId(){
        return getResources().backgroundColorId();
    }

}

class ButtonDetails{
    final static int DEFAULT_BUTTON_ORDER = -1;
    private final DialogButtonAction.Is action;
    protected boolean visible = false;
    protected ButtonPosition position = null;
    private DialogPickerBase.Param param;
    //IMPROVE attach to view, when setPosition ouvisible, if vie visible, update.
    private int order = DEFAULT_BUTTON_ORDER;
    private boolean enable = true;
    private boolean positionOwner = false;
    private List<RunnableCommand> commands = null;

    protected ButtonDetails(DialogButtonAction.Is action){
DebugTrack.start().create(this).end();
        this.action = action;
    }

    protected void attach(DialogPickerBase.Param param){
        this.param = param;
    }

    public Is getAction(){
        return action;
    }

    public int getOrder(){
        return order;
    }

    public void setOrder(int order){
        this.order = order;
    }

    public boolean isPositionOwner(){
        return positionOwner;
    }

    protected <B extends ButtonDetails> B getButtonDetails(ButtonPosition position){
        return (B)param.getButtonDetails(position);
    }

    private void removeOwnership(ButtonPosition position){
        ButtonDetails ButtonDetails = getButtonDetails(position);
        if(ButtonDetails != null){
            if(position != ButtonPosition.OPTION){
                ButtonDetails.setVisibility(false);
            }
            ButtonDetails.positionOwner = false;
        }
    }

    protected boolean isOwnerVisible(ButtonPosition position){
        ButtonDetails ButtonDetails = getButtonDetails(position);
        return (ButtonDetails != null) && ButtonDetails.isVisible();
    }

    public ButtonPosition getPosition(){
        return position;
    }

    public ButtonDetails setPosition(ButtonPosition position){
        setPosition(position, visible);
        return this;
    }

    public ButtonDetails setPositionDefault(boolean visibility){
        if(getAction() == CANCEL){
            if(!isOwnerVisible(ButtonPosition.TOP_RIGHT) || !visibility){
                setPosition(ButtonPosition.TOP_RIGHT, visibility);
            } else {
                setPosition(ButtonPosition.TOP_LEFT, true);
            }
            return this;
        }
        if(getAction() == CONFIRM){
            if(!isOwnerVisible(ButtonPosition.BOTTOM_RIGHT) || !visibility){
                setPosition(ButtonPosition.BOTTOM_RIGHT, visibility);
            } else {
                ButtonDetails buttonDetails = getButtonDetails(ButtonPosition.TOP_RIGHT);
                this.position = ButtonPosition.TOP_RIGHT;
                this.visible = true;
                if(buttonDetails != null){
                    buttonDetails.setPositionDefault(true);
                }
            }
            return this;
        }

DebugException.start().unknown("action", getAction().name()).end();

        return this;
    }

    public ButtonDetails setPosition(ButtonPosition position, boolean visibility){
        if(position == this.position){
            return this;
        }
        if(visibility && (position != null)){
            removeOwnership(position);
        }
        this.position = position;
        setVisibility(visibility);
        return this;
    }

    public boolean hasPosition(){
        return position != null;
    }

    public ButtonDetails setVisibility(boolean flag){
        if(flag == this.visible){
            return this;
        }

        if(!hasPosition()){
DebugException.start().explode("Must set valid position to use visibility").end();
            return this;
        }

        if(flag){
            if(!positionOwner){
                removeOwnership(position);
            }
            if(getPosition() != ButtonPosition.OPTION){
                positionOwner = true;
            }
        }
        visible = flag;
        return this;
    }

    private void setVisibility(int visibility){
        setVisibility(visibility == View.VISIBLE);
    }

    public boolean isVisible(){
        return hasPosition() && visible;
    }

    public ButtonDetails enable(boolean flag){
        enable = flag;
        return this;
    }

    public boolean isEnabled(){
        return isVisible() && enable;
    }

    public ButtonDetails clearCommand(){
        if(commands == null){
            return this;
        }
        commands.clear();
        commands = null;
        return this;
    }

    public <BOSS> ButtonDetails clearCommand(BOSS boss){
        if(commands == null){
            return this;
        }
        for(Iterator<RunnableCommand> iterator = commands.iterator(); iterator.hasNext(); ){
            RunnableCommand command = iterator.next();
            if(!command.hasBoss() || (command.getBoss() == boss)){
                iterator.remove();
            }
        }
        if(commands.isEmpty()){
            commands = null;
        }
        return this;
    }

    public <BOSS> RunnableCommand<BOSS> getCommand(BOSS boss){
        if(commands == null){
            return null;
        }
        for(RunnableCommand command: commands){
            if(command.getBoss() == boss){
                return command;
            }
        }
        return null;
    }

    public <BOSS> List<RunnableCommand<BOSS>> getCommands(BOSS boss){
        if(commands == null){
            return null;
        }
        List<RunnableCommand<BOSS>> bossCommands = new ArrayList<>();
        for(RunnableCommand command: commands){
            if(command.getBoss() == boss){
                bossCommands.add(command);
            }
        }
        return Nullify.collection(bossCommands);
    }

    public ButtonDetails addCommand(RunnableCommand command){
        if(commands == null){
            commands = new ArrayList<>();
        }
        commands.add(command);
        return this;
    }

    public boolean hasCommand(){
        return commands != null;
    }

    protected void execute(){
        RunnableCommand.run(commands, true);
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("button action", action);
        data.append("position", position);
        data.append("order", order);
        data.append("visible", visible);
        data.append("enable", enable);
        data.append("positionOwner", positionOwner);
        data.appendSize("commands", commands);
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

}
