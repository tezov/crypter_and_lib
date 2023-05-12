/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.recycler.prebuild.row.label;

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

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.type.collection.ListEntry;

public class LabelRowDescription{
public static LabelRowBinder.DescriptionView label(int layoutId){
    return new LabelRowBinder.DescriptionView(){
        ListEntry<Integer, Class<?>> l;

        LabelRowBinder.DescriptionView init(){
            l = new ListEntry<Integer, Class<?>>();
            l.add(R.id.lbl_label, String.class);
            return this;
        }

        @Override
        public int getLayoutId(){
            return layoutId;
        }

        @Override
        public ListEntry<Integer, Class<?>> getViewIds(){
            return l;
        }

        @Override
        public void setValue(int id, View view, Object value){
            ((TextView)view).setText((String)value);
        }
    }.init();
}

public static LabelRowBinder.DescriptionView labelIcon(int layoutId){
    return new LabelRowBinder.DescriptionView(){
        ListEntry<Integer, Class<?>> l;

        LabelRowBinder.DescriptionView init(){
            l = new ListEntry<Integer, Class<?>>();
            l.add(R.id.lbl_label, String.class);
            l.add(R.id.img_icon, Drawable.class);
            return this;
        }

        @Override
        public int getLayoutId(){
            return layoutId;
        }

        @Override
        public ListEntry<Integer, Class<?>> getViewIds(){
            return l;
        }

        @Override
        public void setValue(int id, View view, Object value){
            if(id == R.id.lbl_label){
                ((TextView)view).setText((String)value);
            } else {
                if(id == R.id.img_icon){
                    ((ImageView)view).setImageDrawable((Drawable)value);
                }
            }
        }
    }.init();
}

}
