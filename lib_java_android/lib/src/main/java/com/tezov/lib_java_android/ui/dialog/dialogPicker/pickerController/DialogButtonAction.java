/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.pickerController;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.R;

import static com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonView.ButtonPosition;

public interface DialogButtonAction extends com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction{
int RES_IMG_PATH_NEXT = R.drawable.ic_next_24dp;
int RES_IMG_PATH_PREVIOUS = R.drawable.ic_previous_24dp;
int RES_IMG_PATH_OPTION_ADD = R.drawable.ic_add_24dp;
int RES_IMG_PATH_OPTION_SELECT = R.drawable.ic_list_24dp;
int RES_IMG_PATH_OPTION_EDIT = R.drawable.ic_edit_24dp;
Is NEXT = new Is("NEXT");
Is PREVIOUS = new Is("PREVIOUS");
Is ADD = new Is("ADD");
Is SELECT = new Is("SELECT");
Is EDIT = new Is("EDIT");
Creator BUTTON_DETAILS_CREATOR = ButtonDetails::new;

class Is extends com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction.Is{
    public Is(String value){
        super(value);
    }

    @Override
    protected Resource getResources(){
        Resource resources = super.getResources();
        if(resources != null){
            return resources;
        }
        if(this == NEXT){
            resources = new Resource(){
                @Override
                public int imageId(){
                    return RES_IMG_PATH_NEXT;
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
            if(this == PREVIOUS){
                resources = new Resource(){
                    @Override
                    public int imageId(){
                        return RES_IMG_PATH_PREVIOUS;
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
                if(this == ADD){
                    resources = new Resource(){
                        @Override
                        public int imageId(){
                            return RES_IMG_PATH_OPTION_ADD;
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
                    if(this == SELECT){
                        resources = new Resource(){
                            @Override
                            public int imageId(){
                                return RES_IMG_PATH_OPTION_SELECT;
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
                        if(this == EDIT){
                            resources = new Resource(){
                                @Override
                                public int imageId(){
                                    return RES_IMG_PATH_OPTION_EDIT;
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
                        }
                    }
                }
            }
        }
        return resources;
    }

}

class ButtonDetails extends com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction.ButtonDetails{
    ButtonDetails(com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogButtonAction.Is action){
        super(action);
    }

    @Override
    public ButtonDetails setPositionDefault(boolean visibility){
        if(getAction() == NEXT){
            ButtonDetails oldPositionOwner = getButtonDetails(ButtonPosition.BOTTOM_RIGHT);
            this.position = ButtonPosition.BOTTOM_RIGHT;
            this.visible = visibility;
            if((oldPositionOwner != null) && visibility){
                oldPositionOwner.setPositionDefault(true);
            }
            return this;
        }
        if(getAction() == PREVIOUS){
            setPosition(ButtonPosition.BOTTOM_LEFT, visibility);
            return this;
        }
        if(getAction() == SELECT){
            setPosition(ButtonPosition.OPTION, visibility);
            return this;
        }
        if(getAction() == EDIT){
            setPosition(ButtonPosition.OPTION, visibility);
            return this;
        }
        if(getAction() == ADD){
            setPosition(ButtonPosition.OPTION, visibility);
            return this;
        }
        return (ButtonDetails)super.setPositionDefault(visibility);
    }

}

}
