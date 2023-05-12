/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.view.status;

import com.tezov.lib_java.debug.DebugLog;
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
import static com.tezov.lib_java_android.ui.view.status.StatusParam.Color.NEUTRAL;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.layout.FrameLayout;
import com.tezov.lib_java_android.ui.layout.LinearLayout;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java.util.UtilsAlpha;
import com.tezov.lib_java_android.util.UtilsTextView;

import java.util.concurrent.TimeUnit;

//NEXT_TODO most recent is top, max message visible auto delete
//NEXT_TODO request Confirm / Cancel to info set
public class StatusView extends LinearLayout{
private final static float ALPHA_BORDER_COLOR = 0.35f;

private final static int RES_ICON_PROGRESS_DEFAULT = R.drawable.progress_rotate;
private final static int RES_LAYOUT_DEFAULT = R.layout.status_view_default;
final static private int[] ATTR_INDEX = R.styleable.StatusView_lib;
private ListEntry<Type, Integer> resources = null;
private boolean singleView = true;

public StatusView(android.content.Context context){
    super(context);
    init(context, null, -1, -1);
}
public StatusView(android.content.Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, -1, -1);
}
public StatusView(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);
}

public static <KEY> Properties<KEY> newSet(){
    return new Properties();
}
public static <KEY> Properties<KEY> newSet(StatusView view){
    return new Properties(view);
}

private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    if(attrs == null){
        return;
    }
    AttributeReader attributes = new AttributeReader().setAttrsIndex(ATTR_INDEX).parse(context, attrs);
    if(attributes.has(R.styleable.StatusView_lib_layout_progress)){
        setResource(Type.LOADING, attributes.getReference(R.styleable.StatusView_lib_layout_progress));
    }
    if(attributes.has(R.styleable.StatusView_lib_layout_request)){
        setResource(Type.REQUEST, attributes.getReference(R.styleable.StatusView_lib_layout_request));
    }
    if(attributes.has(R.styleable.StatusView_lib_layout_message)){
        setResource(Type.MESSAGE, attributes.getReference(R.styleable.StatusView_lib_layout_message));
    }
}

private View inflate(int res){
    return LayoutInflater.from(AppContext.getActivity()).inflate(res, getContainer(), false);
}
public StatusView setResource(Type type, int resourceId){
    if(resources == null){
        resources = new ListEntry<Type, Integer>();
    }
    resources.put(type, resourceId);
    return this;
}
public Integer getResource(Type type){
    if(resources == null){
        return null;
    } else {
        return resources.getValue(type);
    }
}

private LinearLayout getContainer(){
    return this;
}

public void singleView(boolean flag){
    this.singleView = flag;
}

private void closeView(int id){
    closeView(id, null, null);
}
private void closeView(int id, Long delay, TimeUnit timeUnit){
    if(delay != null){
        PostToHandler.of(getContainer(), delay, timeUnit, new RunnableW(){
            @Override
            public void runSafe(){
                getContainer().removeViewWithId(id);
            }
        });
    } else {
        getContainer().removeViewWithId(id);
    }
}
private void closeView(View view){
    closeView(view, null, null);
}
private void closeView(View view, Long delay, TimeUnit timeUnit){
    closeView(view.getId(), delay, timeUnit);
}

private int showView(View view){
    if(singleView){
        getContainer().removeAllViews();
    }
    view.setId(AppUIDGenerator.nextInt());
    getContainer().addView(view);
    return view.getId();
}

private void updateIconWithProgress(View view, Integer iconResourceId){
    FrameLayout frameIcon = view.findViewById(R.id.img_icon);
    ProgressBar progressBar = new ProgressBar(getContext());
    ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(AppDisplay.convertDpToPx(48), AppDisplay.convertDpToPx(48));
    progressBar.setLayoutParams(param);
    progressBar.setIndeterminate(true);
    progressBar.setIndeterminateDrawable(AppContext.getResources().getDrawable(iconResourceId));
    frameIcon.addView(progressBar);
    frameIcon.setVisibility(VISIBLE);
}
private void updateIcon(View view, Integer iconResourceId){
    FrameLayout frameIcon = view.findViewById(R.id.img_icon);
    if(iconResourceId != null){
        frameIcon.setBackground(AppContext.getResources().getDrawable(iconResourceId));
        frameIcon.setVisibility(VISIBLE);
    } else {
        frameIcon.setVisibility(GONE);
    }
}
private void updateTitle(View view, String title, StatusParam.Color.Is color){
    TextView lbl = view.findViewById(R.id.lbl_title);
    if(title != null){
        lbl.setTextColor(color.title());
        lbl.setVisibility(VISIBLE);
        UtilsTextView.setAndTruncate(lbl, title, 1);
    } else {
        lbl.setVisibility(GONE);
    }
}
private void updateMessage(View view, String message, StatusParam.Color.Is color){
    TextView lbl = view.findViewById(R.id.lbl_message);
    if(message != null){
        lbl.setTextColor(color.message());
        lbl.setText(message);
        lbl.setVisibility(VISIBLE);
    } else {
        lbl.setVisibility(GONE);
    }
}
private void hideButton(View view){
    view.findViewById(R.id.container_btn).setVisibility(GONE);
}
private void updateHelper(View view, String helper, StatusParam.Color.Is color){
    View divider = view.findViewById(R.id.sep_header);
    TextView lbl = view.findViewById(R.id.lbl_helper);
    if(helper != null){
        lbl.setTextColor(color.helper());
        lbl.setVisibility(VISIBLE);
        divider.setVisibility(VISIBLE);
        UtilsTextView.setAndTruncate(lbl, helper, 1);
    } else {
        lbl.setVisibility(GONE);
        divider.setVisibility(GONE);
    }
}
private void updateFrameColor(View view, StatusParam.Color.Is color){
    LayerDrawable bg = (LayerDrawable)view.getBackground();
    Drawable frame = bg.findDrawableByLayerId(R.id.shp_stroke_color);
    DrawableCompat.wrap(frame).setTint(UtilsAlpha.color(color.background(), ALPHA_BORDER_COLOR));
}

private <KEY> int showProgress(Properties<KEY> set){
    Integer resourceId = getResource(Type.LOADING);
    if(resourceId == null){
        resourceId = RES_LAYOUT_DEFAULT;
    }
    View view = inflate(resourceId);
    updateIconWithProgress(view, set.icon != null ? set.icon : RES_ICON_PROGRESS_DEFAULT);
    updateTitle(view, set.title, set.color);
    updateMessage(view, set.message, set.color);
    hideButton(view);
    updateHelper(view, set.helper, set.color);
    updateFrameColor(view, set.color);
    return showView(view);
}
private <KEY> int showMessage(Properties<KEY> set){
    Integer resourceId = getResource(Type.MESSAGE);
    if(resourceId == null){
        resourceId = RES_LAYOUT_DEFAULT;
    }
    View view = inflate(resourceId);
    updateIcon(view, set.icon);
    updateTitle(view, set.title, set.color);
    hideButton(view);
    updateMessage(view, set.message, set.color);
    updateHelper(view, set.helper, set.color);
    updateFrameColor(view, set.color);
    if(set.messageCloseDelay != null){
        PostToHandler.of(getContainer(), set.messageCloseDelay, set.messageCloseTimeUnit, new RunnableW(){
            @Override
            public void runSafe(){
                if(getContainer().hasView(view)){
                    closeView(view);
                    set.run(set.runOnClose);
                }
            }
        });
    }
    return showView(view);
}
private <KEY> int showRequest(Properties<KEY> set){
    Integer resourceId = getResource(Type.REQUEST);
    if(resourceId == null){
        resourceId = RES_LAYOUT_DEFAULT;
    }
    View view = inflate(resourceId);
    updateIcon(view, set.icon);
    updateTitle(view, set.title, set.color);
    updateMessage(view, set.message, set.color);
    updateHelper(view, set.helper, set.color);
    updateFrameColor(view, set.color);
    Button btnCancel = view.findViewById(R.id.btn_cancel);
    btnCancel.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            closeView(view);
            set.run(set.runOnCancel);
        }
    });
    Button btnConfirm = view.findViewById(R.id.btn_confirm);
    btnConfirm.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            closeView(view);
            set.run(set.runOnConfirm);
        }
    });
    return showView(view);
}


public enum Type{
    LOADING, REQUEST, MESSAGE,
}

public static class Properties<KEY>{
    protected StatusView statusView = null;
    protected Integer viewId;
    protected Integer icon;
    protected String title;
    protected String message;
    protected String helper;
    protected StatusParam.Color.Is color;
    protected Long messageCloseDelay;
    protected TimeUnit messageCloseTimeUnit;
    protected ListEntry<KEY, RunnableW> runnables;
    protected KEY runOnClose;
    protected KEY runOnCancel;
    protected KEY runOnConfirm;

    public Properties(){
        this(null);
    }
    public Properties(StatusView statusView){
DebugTrack.start().create(this).end();
        attach(statusView);
        init();
    }

    public Properties<KEY> attach(StatusView statusView){
        this.statusView = statusView;
        return this;
    }

    private void init(){
        viewId = null;
        icon = null;
        title = null;
        message = null;
        helper = null;
        color = NEUTRAL;
        messageCloseDelay = null;
        messageCloseTimeUnit = null;
        runnables = null;
        runOnClose = null;
        runOnCancel = null;
        runOnConfirm = null;
    }

    private StatusView getStatusView(){
        return statusView;
    }

    public View getView(){
        if(statusView != null){
            return statusView.findViewById(viewId);
        } else {
            return null;
        }
    }
    public FrameLayout getViewFrameIcon(){
        View view = getView();
        if(view != null){
            return view.findViewById(R.id.img_icon);
        } else {
            return null;
        }
    }
    public TextView getViewTitle(){
        View view = getView();
        if(view != null){
            return view.findViewById(R.id.lbl_title);
        } else {
            return null;
        }
    }
    public TextView getViewMessage(){
        View view = getView();
        if(view != null){
            return view.findViewById(R.id.lbl_message);
        } else {
            return null;
        }
    }
    public TextView getViewHelper(){
        View view = getView();
        if(view != null){
            return view.findViewById(R.id.lbl_helper);
        } else {
            return null;
        }
    }

    public Properties<KEY> setIcon(Integer icon){
        this.icon = icon;
        return this;
    }

    public Properties<KEY> setTitle(String title){
        this.title = title;
        return this;
    }

    public Properties<KEY> setMessage(String message){
        this.message = message;
        return this;
    }

    public Properties<KEY> setHelper(String helper){
        this.helper = helper;
        return this;
    }

    public Properties<KEY> setColor(StatusParam.Color.Is color){
        this.color = color;
        return this;
    }

    public Properties<KEY> setMessageCloseDelay(Long delay){
        this.messageCloseDelay = delay;
        if(delay != null){
            this.messageCloseTimeUnit = TimeUnit.MILLISECONDS;
        } else {
            this.messageCloseTimeUnit = null;
        }
        return this;
    }
    public Properties<KEY> setMessageCloseDelay(Long delay, TimeUnit unit){
        this.messageCloseDelay = delay;
        this.messageCloseTimeUnit = unit;
        return this;
    }

    public Properties<KEY> putRunnable(KEY key, RunnableW runnable){
        if(runnables == null){
            runnables = new ListEntry<KEY, RunnableW>(ListOrObject::new);
        }
        runnables.put(key, runnable);
        return this;
    }

    private void run(KEY key){
        if(key != null){
            RunnableW r = runnables.getValue(key);
            if(r != null){
                Handler.PRIMARY().post(this, r);
            }

        }
    }

    public Properties<KEY> runOnCancel(KEY key){
        this.runOnCancel = key;
        return this;
    }

    public Properties<KEY> runOnConfirm(KEY key){
        this.runOnConfirm = key;
        return this;
    }

    public Properties<KEY> runOnClose(KEY key){
        this.runOnClose = key;
        return this;
    }

    public Properties<KEY> noRun(){
        runOnClose = null;
        runOnCancel = null;
        runOnConfirm = null;
        return this;
    }

    public int showProgress(){
        viewId = getStatusView().showProgress(this);
        return viewId;
    }

    public int showRequest(){
        viewId = getStatusView().showRequest(this);
        return viewId;
    }

    public int showMessage(){
        viewId = getStatusView().showMessage(this);
        return viewId;
    }

    public Integer show(Type type){
        switch(type){
            case LOADING:
                return showProgress();
            case REQUEST:
                return showRequest();
            case MESSAGE:
                return showMessage();
            default:

DebugException.start().unknown("type", type).end();

                return null;
        }
    }

    public void close(int id){
        getStatusView().closeView(id, messageCloseDelay, messageCloseTimeUnit);
    }

    public void close(){
        if(viewId != null){
            close(viewId);
            viewId = null;
        }
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}
