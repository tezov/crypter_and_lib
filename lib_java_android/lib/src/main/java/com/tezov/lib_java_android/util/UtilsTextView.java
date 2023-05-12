/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

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
import com.tezov.lib_java.toolbox.Nullify;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.tezov.lib_java.file.UtilsFile;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;

public class UtilsTextView{

public static void setBackground(View view, Drawable drawable){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            view.setBackground(drawable);
        }
    });
}
public static void setText(TextView textView, String text){
    PostToHandler.of(textView, new RunnableW(){
        @Override
        public void runSafe(){
            textView.setText(text);
        }
    });
}
public static void setText(FormEditText editText, String text){
    PostToHandler.of(editText, new RunnableW(){
        @Override
        public void runSafe(){
            editText.setText(text);
        }
    });
}

public static void setAndTruncate(FormEditText editText, String s){
    setAndTruncate(editText, s, 1);
}
public static void setAndTruncate(TextView view, String s){
    setAndTruncate(view, s, 1);
}
public static void setAndTruncate(FormEditText editText, String s, int maxLine){
    setAndTruncate(editText, s, maxLine, new ConsumerW<String>(){
        @Override
        public void accept(String s){
            editText.setText(s);
        }
    });
}
public static void setAndTruncate(TextView view, String s, int maxLine){
    setAndTruncate(view, s, maxLine, new ConsumerW<String>(){
        @Override
        public void accept(String s){
            view.setText(s);
        }
    });
}
public static void setAndTruncate(TextView view, String s, int maxLine, ConsumerW<String> textConsumer){
    if(!view.isAttachedToWindow()){
        ViewTreeEvent.onLayout(view, new RunnableSubscription(){
            @Override
            public void onComplete(){
                unsubscribe();
                truncateText(view, s, maxLine, textConsumer);
            }
        });
    } else {
        truncateText(view, s, maxLine, textConsumer);
    }
}
private static void truncateText(TextView view, String s, int maxLine, ConsumerW<String> textConsumer){
    if(view.getLineCount() > maxLine){
        int lineEndIndex = view.getLayout().getLineEnd(maxLine - 1);
        textConsumer.accept(s.subSequence(0, lineEndIndex - 3) + "\u2026");
    } else {
        textConsumer.accept(s);
    }
}

public static void setFileNameAndTruncate(FormEditText editText, String fileName){
    setFileNameAndTruncate(editText, fileName, new ConsumerW<String>(){
        @Override
        public void accept(String s){
            editText.setText(s);
        }
    });
}
public static void setFileNameAndTruncate(TextView view, String fileName){
    setFileNameAndTruncate(view, fileName, new ConsumerW<String>(){
        @Override
        public void accept(String s){
            view.setText(s);
        }
    });
}
public static void setFileNameAndTruncate(TextView view, String fileName, ConsumerW<String> textConsumer){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            textConsumer.accept(fileName);
            if(!view.isAttachedToWindow()){
                ViewTreeEvent.onLayout(view, new RunnableSubscription(){
                    @Override
                    public void onComplete(){
                        unsubscribe();
                        truncateFileName(view, fileName, textConsumer);
                    }
                });
            } else {
                truncateFileName(view, fileName, textConsumer);
            }
        }
    });
}
private static void truncateFileName(TextView view, String fileFullName, ConsumerW<String> textConsumer){
    if(Nullify.string(fileFullName) == null){
        textConsumer.accept(null);
    }
    else if(view.getLineCount() > 1){
        int maxLength = view.getLayout().getLineEnd(0);
        int lengthToRemove = (fileFullName.length() - maxLength) + 1;
        textConsumer.accept(UtilsFile.truncateName(fileFullName, lengthToRemove));
    } else {
        textConsumer.accept(fileFullName);
    }
}

}
