/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.debug.debugBar;

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

import com.tezov.lib_java_android.application.AppConfigKey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLayoutLinearVertical;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLinear;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;

import java.util.Map;

import java9.util.function.BiConsumer;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.BOTTOM;
import static com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder.ViewType.DEFAULT;

public class DebugBarLogLayout extends RecyclerListLinear{
public final static String SHARE_PREFERENCES_DEBUG = "SHARE_PREFERENCES_DEBUG";
private final static float HEIGHT = 0.30f;

public DebugBarLogLayout(Context context){
    super(context);
    init(context, null, -1, -1);
}
public DebugBarLogLayout(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}
public DebugBarLogLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
    setLayoutManager(new RecyclerListLayoutLinearVertical());
    DebugLogDataManager dataManager = new DebugLogDataManager();
    DebugLogRowManager rowManager = new DebugLogRowManager(dataManager);
    setRowManager(rowManager);
    if(AppConfig.getBoolean(AppConfigKey.DEBUG_LOG_ON_DEVICE.getId())){
        DebugLog.setOutput(new BiConsumer<String, String>(){
            BiConsumer<String, String> logOutput = null;
            BiConsumer<String, String> init(){
                logOutput = DebugLog.getOutputDefault();
                return this;
            }
            @Override
            public void accept(String tag, String data){
                logOutput.accept(tag, data);
                me().post(new Runnable(){
                    @Override
                    public void run(){
                        DebugLogDataManager dataManager = getDataManager();
                        if(dataManager != null){
                            dataManager.addRow(tag, data);
                        }
                    }
                });
                me().post(new Runnable(){
                    @Override
                    public void run(){
                        me().scrollToPosition(BOTTOM, dataManager.size());
                    }
                });
            }
        }.init());
    }
}
private DebugBarLogLayout me(){
    return this;
}

@Override
protected void onAttachedToWindow(){
    super.onAttachedToWindow();
    int height = (int)(AppDisplay.getSizeOriented().getHeight() * HEIGHT);
    setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, height));
}
public static class DebugLogDataManager extends RecyclerListDataManager<String>{
    private final static String KEY_PREFIX = DebugLogDataManager.class.getSimpleName() + "_";
    private final android.content.SharedPreferences sp;
    private int count;

    protected DebugLogDataManager(){
        super(String.class);
        sp = AppContext.getSharedPreferences(SHARE_PREFERENCES_DEBUG, android.content.Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor spEdit = sp.edit();
        Map<String, ?> keys = sp.getAll();
        for(Map.Entry<String, ?> e: keys.entrySet()){
            if(e.getKey().startsWith(KEY_PREFIX)){
                spEdit.remove(e.getKey());
            }
        }
        spEdit.apply();
        count = 0;
    }
    @Override
    public int size(){
        return count;
    }
    @Override
    public String get(int index){
        return sp.getString(KEY_PREFIX + index, null);
    }
    public void addRow(String tag, String data){
        sp.edit().putString(KEY_PREFIX + count, tag + data).commit();
        postInserted(count);
        count++;
    }

}

public static class DebugLogRowManager extends RecyclerListRowManager<String>{
    public DebugLogRowManager(DebugLogDataManager dataManager){
        super(dataManager);
        add(new DebugLogRowBinder(this));
    }
    @Override
    public int getItemViewType(int position){
        return RecyclerListRowBinder.ViewType.DEFAULT.ordinal();
    }

}

public static class DebugLogRowBinder extends RecyclerListRowBinder<DebugLogRowBinder.RowHolder, String>{

    public DebugLogRowBinder(DebugLogRowManager rowManager){
        super(rowManager);
    }

    @Override
    public ViewType.Is getViewType(){
        return DEFAULT;
    }

    @Override
    public RowHolder create(ViewGroup parent){
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        TextView textView = new TextView(frameLayout.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        frameLayout.addView(textView);
        return new RowHolder(frameLayout);
    }

    public static class RowHolder extends RecyclerListRowHolder<String>{
        private final TextView lblMessage;

        public RowHolder(FrameLayout itemView){
            super(itemView);
            lblMessage = (TextView)itemView.getChildAt(0);
        }

        @Override
        public String get(){
            return lblMessage.getText().toString();
        }

        @Override
        public void set(String data){
            lblMessage.setText(data);
        }

    }

}

}
