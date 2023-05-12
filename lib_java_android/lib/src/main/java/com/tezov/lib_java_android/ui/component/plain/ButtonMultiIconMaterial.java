/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

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
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.misc.AttributeReader;

import java.util.List;

public class ButtonMultiIconMaterial extends ButtonIconMaterial{
final static private int[] ATTR_INDEX = R.styleable.ButtonIconMultiState_lib;
private int index = 0;
private List<IconDetail> iconDetails = null;

public ButtonMultiIconMaterial(android.content.Context context){
    super(context);
    init(context, null, NO_ID);
}
public ButtonMultiIconMaterial(android.content.Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public ButtonMultiIconMaterial(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr){
    if(attrs == null){
        return;
    }
    AttributeReader attributes = new AttributeReader().parse(context, ATTR_INDEX, attrs);
    // Text button
    Integer textArrayId = attributes.getReference(R.styleable.ButtonIconMultiState_lib_text_array);
    List<String> texts;
    if(textArrayId != null){
        texts = AppContext.getResources().getStrings(textArrayId);
    } else {
        texts = null;
    }
    // Background color button
    Integer backgroundColorArrayId = attributes.getReference(R.styleable.ButtonIconMultiState_lib_background_color_array);
    List<ColorStateList> backgroundColors;
    if(backgroundColorArrayId != null){
        backgroundColors = AppContext.getResources().getColorsAsStateLists(backgroundColorArrayId);
    } else {
        backgroundColors = null;
    }
    // Icon button
    Integer iconArrayId = attributes.getReference(R.styleable.ButtonIconMultiState_lib_icon_array);
    List<Drawable> icons;
    if(iconArrayId != null){
        icons = AppContext.getResources().getDrawables(iconArrayId);
    } else {
        icons = null;
    }

    // state button build
    iconDetails = new ListOrObject<>();
    int textSize = (texts != null) ? texts.size() : 0;
    int backgroundColorSize = (backgroundColors != null) ? backgroundColors.size() : 0;
    int iconSize = (icons != null) ? icons.size() : 0;
    int statesSize = Math.max(textSize, backgroundColorSize);
    statesSize = Math.max(statesSize, iconSize);
    for(int i = 0; i < statesSize; i++){
        iconDetails.add(new IconDetail());
    }
    if(texts != null){
        for(int i = 0; i < textSize; i++){
            iconDetails.get(i).text = texts.get(i);
        }
    }
    if(backgroundColors != null){
        for(int i = 0; i < backgroundColorSize; i++){
            iconDetails.get(i).backgroundColor = backgroundColors.get(i);
        }
    }
    if(icons != null){
        for(int i = 0; i < iconSize; i++){
            iconDetails.get(i).icon = icons.get(i);
        }
    }
    iconDetails = Nullify.collection(iconDetails);

}

@Override
protected void onAttachedToWindow(){
    super.onAttachedToWindow();
    updateIcon();
}
@Override
protected void onDetachedFromWindow(){
    super.onDetachedFromWindow();
    updateIcon();
}

private void updateIcon(){
    if(iconDetails != null){
        iconDetails.get(index).update(this);
    }
}

public void nextIndex(){
    index = index + 1;
    if(index >= iconDetails.size()){
        index = 0;
    }
    updateIcon();
}

public void previousIndex(){
    if(index > 0){
        index -= 1;
    } else {
        index = iconDetails.size() - 1;
    }
    updateIcon();
}

public int getIndex(){
    return index;
}

public void setIndex(int index){
    this.index = index;
    updateIcon();
}

public int sizeIndex(){
    return iconDetails.size();
}

private static class IconDetail{
    String text = null;
    ColorStateList backgroundColor = null;
    Drawable icon = null;
    void update(MaterialButton button){
        PostToHandler.of(button, new RunnableW(){
            @Override
            public void runSafe(){
                if(button.isAttachedToWindow()){
                    if(text != null){
                        button.setText(text);
                    }
                    if(backgroundColor != null){
                        button.setBackgroundTintList(backgroundColor);
                    }
                    if(icon != null){
                        button.setIcon(icon);
                    }
                }
            }
        });
    }

}

}
