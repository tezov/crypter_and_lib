/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.misc.SupplierSubscription;
import com.tezov.lib_java.toolbox.Compare;

import com.tezov.lib_java_android.R;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.core.widget.TextViewCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.util.UtilsView;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnClickIconListener;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnClipBoardActionListener;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnScrollListener;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnEditorActionListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnFocusChangeListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnTouchListenerW;

public class EditText extends TextInputEditText{
final static private int[] ATTR_INDEX = R.styleable.EditText_lib;

private final static float INTERCEPT_MOVE_THRESHOLD = AppDisplay.convertDpToPx(10);
private final static long INTERCEPT_RELEASE_DELAY_ms = 50;

protected ListEntry<Class<?>, ListOrObject<?>> listeners = null;
protected ViewOnTouchListenerW onTouchListener = null;
private ViewOnClickListenerW onClickListener = null;
private TextViewOnEditorActionListenerW onEditorActionListener = null;
private TextViewOnFocusChangeListenerW onFocusChangeListener = null;
private EditTextOnTextChangedListenerW onTextChangedListener = null;
private boolean autoHideKeyBoard = true;

public EditText(Context context){
    super(context);
    init(context, null, NO_ID);
}
public EditText(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID);
}
public EditText(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
    listeners = new ListEntry<>(ListOrObject::new);
    setEnableEditorActionListener(true);
    Integer scrollPosition = null;
    if(attrs != null){
        AttributeReader attributes = new AttributeReader().setAttrsIndex(ATTR_INDEX).parse(context, attrs);
        autoHideKeyBoard = Compare.isTrueOrNull(attributes.asBoolean(R.styleable.EditText_lib_auto_hide_keyboard));
        scrollPosition = attributes.asInteger(R.styleable.EditText_lib_scroll_position);
    }
    if(scrollPosition == null){
        scrollPosition = 1;
    }
    if(scrollPosition == 1){
        ViewTreeEvent.onPreDraw(this, new SupplierSubscription<>(){
            @Override
            public Boolean onComplete(){
                unsubscribe();
                moveToEnd();
                return false;
            }
        });
    }
}
private EditText me(){
    return this;
}

public char[] getChars(){
    if(length() <= 0){
        return null;
    } else {
        char[] c = new char[length()];
        getText().getChars(0, c.length, c, 0);
        return c;
    }
}

public void moveToStart(){
    moveTo(0);
}
public void moveTo(int index){
    setSelection(index);
}
public void moveToEnd(){
    Editable text = getText();
    if(text != null){
        moveTo(text.length());
    }
}

protected Class<?> getRootClass(Class<?> type){
    if(Reflection.isInstanceOf(type, ViewOnClickListenerW.class)){
        return ViewOnClickListenerW.class;
    } else if(Reflection.isInstanceOf(type, EditTextOnTextChangedListenerW.class)){
        return EditTextOnTextChangedListenerW.class;
    } else if(Reflection.isInstanceOf(type, TextViewOnFocusChangeListenerW.class)){
        return TextViewOnFocusChangeListenerW.class;
    } else if(Reflection.isInstanceOf(type, TextViewOnEditorActionListenerW.class)){
        return TextViewOnEditorActionListenerW.class;
    } else if(Reflection.isInstanceOf(type, EditTextOnClickIconListener.class)){
        return EditTextOnClickIconListener.class;
    } else if(Reflection.isInstanceOf(type, ViewOnTouchListenerW.class)){
        return ViewOnTouchListenerW.class;
    } else if(Reflection.isInstanceOf(type, EditTextOnClipBoardActionListener.class)){
        return EditTextOnClipBoardActionListener.class;
    } else if(Reflection.isInstanceOf(type, EditTextOnScrollListener.class)){
        return EditTextOnScrollListener.class;
    } else {
        return null;
    }
}
protected void addListener(Object o){
    synchronized(me()){
        if(o == null){
DebugException.start().log("can not be null").end();
            return;
        }
        Class<?> type = getRootClass(o.getClass());
        if(type == null){
DebugException.start().unknown("type", o.getClass()).end();
            return;
        }
        ListOrObject l = listeners.getValue(type);
        if(l == null){
            l = new ListOrObject<>();
            listeners.put(type, l);
        }
        l.add(o);
    }
}
protected void removeListener(Object o){
    synchronized(me()){
        ListOrObject l = listeners.getValue(o.getClass());
        if(l != null){
            l.remove(o);
            if(l.isEmpty()){
                listeners.removeKey(o.getClass());
            }
        }
    }
}
protected boolean hasListener(Class<?> type){
    return listeners.hasKey(type);
}
protected <T> ListOrObject<T> getListener(Class<T> type){
    return (ListOrObject<T>)listeners.getValue(type);
}

@Override
public void setOnClickListener(OnClickListener l){
    if(l instanceof ViewOnClickListenerW){
        addOnClickListener((ViewOnClickListenerW)l);
    } else {
        addOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View view){
                l.onClick(view);
            }
        });
    }
}
public void addOnClickListener(ViewOnClickListenerW l){
    addListener(l);
    if(onClickListener == null){
        setClickable(isClickable());
    }
}
public void removeOnClickListener(ViewOnClickListenerW l){
    removeListener(l);
}
@Override
public void setClickable(boolean flag){
    if(onClickListener == null){
        onClickListener = new ViewOnClickListenerW(){
            @Override
            public void onClicked(View view){
                synchronized(me()){
                    ListOrObject<ViewOnClickListenerW> l = getListener(ViewOnClickListenerW.class);
                    if(l != null){
                        for(ViewOnClickListenerW listener: l){
                            listener.onClicked(view);
                        }
                    }
                }
            }
        };
        super.setOnClickListener(onClickListener);
    }
    if(onClickListener != null){
        onClickListener.setEnabled(flag);
    }
    super.setClickable(flag);
}

public void addFocusChangeListener(TextViewOnFocusChangeListenerW l){
    addListener(l);
    if(onFocusChangeListener == null){
        setEnableFocusChangeListener(isFocusable());
    }
}
public void removeFocusChangeListener(TextViewOnFocusChangeListenerW l){
    removeListener(l);
}
public void setEnableFocusChangeListener(boolean flag){
    if(onFocusChangeListener == null){
        onFocusChangeListener = new TextViewOnFocusChangeListenerW(){
            @Override
            public void onFocusChange(android.widget.EditText textView, boolean hasFocus){
                synchronized(me()){
                    ListOrObject<TextViewOnFocusChangeListenerW> l = getListener(TextViewOnFocusChangeListenerW.class);
                    if(l != null){
                        for(TextViewOnFocusChangeListenerW listener: l){
                            listener.onFocusChange((View)textView, hasFocus);
                        }
                    }
                }
            }
        };
        super.setOnFocusChangeListener(onFocusChangeListener);
    }
    if(onFocusChangeListener != null){
        onFocusChangeListener.setEnabled(flag);
    }
}

@Override
public void setOnEditorActionListener(OnEditorActionListener l){
    if(l instanceof TextViewOnEditorActionListenerW){
        addEditorActionListener((TextViewOnEditorActionListenerW)l);
    } else {
        addEditorActionListener(new TextViewOnEditorActionListenerW(){
            @Override
            public boolean onAction(android.widget.EditText textView, int actionId, KeyEvent event){
                return l.onEditorAction(textView, actionId, event);
            }
        });
    }
}
public void addEditorActionListener(TextViewOnEditorActionListenerW l){
    addListener(l);
}
public void removeEditorActionListener(TextViewOnEditorActionListenerW l){
    removeListener(l);
}
public void setEnableEditorActionListener(boolean flag){
    if(onEditorActionListener == null){
        onEditorActionListener = new TextViewOnEditorActionListenerW(){
            @Override
            public boolean onAction(android.widget.EditText editText, int actionId, KeyEvent event){
                synchronized(me()){
                    boolean disableAutoHideKeyBoard = !autoHideKeyBoard;
                    ListOrObject<TextViewOnEditorActionListenerW> l = getListener(TextViewOnEditorActionListenerW.class);
                    if(l != null){
                        for(TextViewOnEditorActionListenerW listener: l){
                            disableAutoHideKeyBoard |= listener.onEditorAction(editText, actionId, event);
                        }
                    }
                    if(!disableAutoHideKeyBoard && (actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE){
                        FocusCemetery.request(me(), UtilsView.Direction.UP);
                    }
                    return disableAutoHideKeyBoard;
                }
            }
        };
        super.setOnEditorActionListener(onEditorActionListener);
    }
    if(onEditorActionListener != null){
        onEditorActionListener.setEnabled(flag);
    }
}

private boolean isNotConstructed(){
    //HACK because AppCompatEditText call overridden method in super constructor
    try{
        return listeners.hashCode() == 0;
    }
    catch(Throwable e){
        return true;
    }
}
public void addTextWatcherListener(TextWatcher watcher){
    super.addTextChangedListener(watcher);
}
@Override
public void addTextChangedListener(TextWatcher watcher){
    if(isNotConstructed()){
        super.addTextChangedListener(watcher);
        return;
    }
    if(watcher instanceof EditTextOnTextChangedListenerW){
        addTextChangedListener((EditTextOnTextChangedListenerW)watcher);
    } else {
        addTextChangedListener(new EditTextOnTextChangedListenerW(){
            @Override
            public void onTextChanged(EditText editText, Editable es){
                watcher.afterTextChanged(es);
            }
        });
    }
}
public void addTextChangedListener(EditTextOnTextChangedListenerW l){
    addListener(l);
    l.attach(this);
    if(onTextChangedListener == null){
        setEnableTextChangedListener(true);
    }
}
public void removeTextChangedListener(EditTextOnTextChangedListenerW l){
    removeListener(l);
}
public void setEnableTextChangedListener(boolean flag){
    if(onTextChangedListener == null){
        onTextChangedListener = new EditTextOnTextChangedListenerW(){
            @Override
            public void onTextChanged(EditText editText, Editable es){
                synchronized(me()){
                    ListOrObject<EditTextOnTextChangedListenerW> l = getListener(EditTextOnTextChangedListenerW.class);
                    if(l != null){
                        for(EditTextOnTextChangedListenerW listener: l){
                            listener.afterTextChanged(es);
                        }
                    }
                }
            }
        };
        super.addTextChangedListener(onTextChangedListener);
    }
    if(onTextChangedListener != null){
        onTextChangedListener.setEnabled(flag);
    }
}

@Override
public void setOnTouchListener(OnTouchListener l){
    if(l instanceof ViewOnTouchListenerW){
        addOnTouchListener((ViewOnTouchListenerW)l);
    } else {
        addOnTouchListener(new ViewOnTouchListenerW(){
            @Override
            public boolean onTouched(View view, MotionEvent motionEvent){
                return l.onTouch(view, motionEvent);
            }
        });
    }
}
public void addOnTouchListener(ViewOnTouchListenerW l){
    addListener(l);
    if(onTouchListener == null){
        setTouchable(true);
    }
}
public void removeOnTouchListener(ViewOnTouchListenerW l){
    removeListener(l);
}
protected boolean onTouched(View view, MotionEvent motionEvent){
    return false;
}
protected void onTouchEventIntercepted(boolean flag){
}
public boolean isTouchable(){
    return (onTouchListener != null) && onTouchListener.isEnabled();
}
public void setTouchable(boolean flag){
    if(onTouchListener == null){
        onTouchListener = new ViewOnTouchListenerW(){
            float moveOriginX = 0.0f;
            boolean touchEventIntercepted = false;
            void interceptTouchEvent(MotionEvent event){
                boolean canScrollHorizontally = canScrollHorizontally(-1) | canScrollHorizontally(1);
                if(canScrollHorizontally){
                    int action = event.getActionMasked();
                    switch(action){
                        case (MotionEvent.ACTION_DOWN):
                        case (MotionEvent.ACTION_POINTER_DOWN):{
                            moveOriginX = event.getX();
                        }
                        break;
                        case (MotionEvent.ACTION_MOVE):{
                            if(!touchEventIntercepted){
                                float diff = moveOriginX - event.getX();
                                if(Math.abs(diff) > INTERCEPT_MOVE_THRESHOLD){
                                    touchEventIntercepted = true;
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                    setClickable(false);
                                    onTouchEventIntercepted(touchEventIntercepted);
                                }
                            }
                        }
                        break;
                        case (MotionEvent.ACTION_UP):
                        case (MotionEvent.ACTION_POINTER_UP):
                        case (MotionEvent.ACTION_CANCEL):{
                            if(touchEventIntercepted){
                                Handler.MAIN().post(this, INTERCEPT_RELEASE_DELAY_ms, new RunnableW(){
                                    @Override
                                    public void runSafe(){
                                        touchEventIntercepted = false;
                                        getParent().requestDisallowInterceptTouchEvent(false);
                                        setClickable(true);
                                        onTouchEventIntercepted(touchEventIntercepted);
                                    }
                                });
                            }
                        }
                        break;
                    }
                }
            }
            @Override
            public boolean onTouched(View view, MotionEvent motionEvent){
                interceptTouchEvent(motionEvent);
                if(!touchEventIntercepted){
                    synchronized(me()){
                        boolean returnValue = false;
                        ListOrObject<ViewOnTouchListenerW> l = getListener(ViewOnTouchListenerW.class);
                        if(l != null){
                            for(ViewOnTouchListenerW listener: l){
                                returnValue |= listener.onTouch(view, motionEvent);
                            }
                        }
                        returnValue |= me().onTouched(view, motionEvent);
                        return returnValue;
                    }
                }
                return false;
            }
        };
        super.setOnTouchListener(onTouchListener);
    }
    if(onTouchListener != null){
        onTouchListener.setEnabled(flag);
    }
}
public void addClipboardActionListener(EditTextOnClipBoardActionListener l){
    addListener(l);
}
public void removeClipboardActionListener(EditTextOnClipBoardActionListener l){
    removeListener(l);
}
public void setEnableClipboardActionListener(boolean flag){
    ListOrObject<EditTextOnClipBoardActionListener> l = getListener(EditTextOnClipBoardActionListener.class);
    if(l != null){
        for(EditTextOnClipBoardActionListener listener: l){
            listener.setEnabled(flag);
        }
    }
}

public void addComputeScrollListener(EditTextOnScrollListener l){
    addListener(l);
}
public void removeComputeScrollListener(EditTextOnScrollListener l){
    removeListener(l);
}
public void setEnableComputeScrollListener(boolean flag){
    ListOrObject<EditTextOnScrollListener> l = getListener(EditTextOnScrollListener.class);
    if(l != null){
        for(EditTextOnScrollListener listener: l){
            listener.setEnabled(flag);
        }
    }
}

@Override
public void computeScroll(){
    super.computeScroll();
    ListOrObject<EditTextOnScrollListener> l = getListener(EditTextOnScrollListener.class);
    if(l != null){
        for(EditTextOnScrollListener listener: l){
            if(listener.isEnabled()){
                listener.onComputed(this);
            }
        }
    }
}

public void setCompoundDrawables(Drawable[] drawables){
    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this, drawables[0], drawables[1], drawables[2], drawables[3]);
}
public EditTextLayout getTextLayout(){
    return UtilsView.findFirst(EditTextLayout.class, this, UtilsView.Direction.UP);
}

@Override
public boolean onTextContextMenuItem(int id){
    ListOrObject<EditTextOnClipBoardActionListener> l = getListener(EditTextOnClipBoardActionListener.class);
    if(l == null){
        return super.onTextContextMenuItem(id);
    }
    if(id == android.R.id.shareText){
        onTextContextMenuItem(l, new ConsumerW<>(){
            @Override
            public void accept(EditTextOnClipBoardActionListener listener){
                listener.onShare(me());
            }
        });
        return super.onTextContextMenuItem(id);
    }
    if(id == android.R.id.cut){
        onTextContextMenuItem(l, new ConsumerW<>(){
            @Override
            public void accept(EditTextOnClipBoardActionListener listener){
                listener.onCut(me());
            }
        });
        return super.onTextContextMenuItem(id);
    }
    if(id == android.R.id.copy){
        onTextContextMenuItem(l, new ConsumerW<>(){
            @Override
            public void accept(EditTextOnClipBoardActionListener listener){
                listener.onCopy(me());
            }
        });
        return super.onTextContextMenuItem(id);
    }
    if((id == android.R.id.paste) || (id == android.R.id.pasteAsPlainText)){
        boolean consumed = super.onTextContextMenuItem(id);
        onTextContextMenuItem(l, new ConsumerW<>(){
            @Override
            public void accept(EditTextOnClipBoardActionListener listener){
                listener.onPaste(me());
            }
        });
        return consumed;
    }
    return super.onTextContextMenuItem(id);
}
private void onTextContextMenuItem(ListOrObject<EditTextOnClipBoardActionListener> listeners, ConsumerW<EditTextOnClipBoardActionListener> consumer){
    for(EditTextOnClipBoardActionListener listener: listeners){
        if(listener.isEnabled()){
            consumer.accept(listener);
        }
    }
}
@Override
protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect){
    super.onFocusChanged(focused, direction, previouslyFocusedRect);
    if(focused){
        requestFocus();
    }
}
@Override
public void setEnabled(boolean flag){
    setFocusable(flag);
    setFocusableInTouchMode(flag);
    setCursorVisible(flag);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
