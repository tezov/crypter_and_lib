/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.view;

import com.tezov.lib_java.debug.DebugLog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.NormLong;
import com.tezov.lib_java.type.runnable.RunnableW;

public class ProgressBarTransfer{
private final static int PROGRESS_BAR_MAX = 100;
private final NormLong normInt;
private final View view;

public ProgressBarTransfer(ViewGroup container){
    this(container, R.layout.bar_progress_transfert);
}
public ProgressBarTransfer(ViewGroup container, int layoutId){
DebugTrack.start().create(this).end();
    this.normInt = new NormLong();
    view = LayoutInflater.from(AppContext.getActivity()).inflate(layoutId, container, true);
    ProgressBar progressBar = view.findViewById(R.id.bar_progress);
    progressBar.setMax(PROGRESS_BAR_MAX);
}

public View getView(){
    return view;
}

public ProgressBarTransfer setTitle(int resourceId){
    return setTitle(AppContext.getResources().getString(resourceId));
}
public ProgressBarTransfer setTitle(String title){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            TextView lblTitle = view.findViewById(R.id.lbl_title);
            lblTitle.setText(title);
        }
    });
    return this;
}
public ProgressBarTransfer setSeparator(String separator){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            TextView lblSeparator = view.findViewById(R.id.lbl_progress_sep);
            lblSeparator.setText(separator);
        }
    });
    return this;
}
public ProgressBarTransfer setUnit(String unit){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            TextView lblUnit = view.findViewById(R.id.lbl_progress_unit);
            lblUnit.setText(unit);
        }
    });
    return this;
}
public ProgressBarTransfer setMax(long value){
    this.normInt.setBase(value);
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            TextView lblProgressMax = view.findViewById(R.id.lbl_progress_max);
            lblProgressMax.setText(String.valueOf(value));
        }
    });
    return this;
}
public ProgressBarTransfer setCurrent(long value){
    PostToHandler.of(view, new RunnableW(){
        @Override
        public void runSafe(){
            long valueToWrite = Math.min(value, normInt.getBase());
            TextView lblProgress = view.findViewById(R.id.lbl_progress);
            String valueToWriteString = valueToString(valueToWrite);
            if(!Compare.equals(valueToWriteString, lblProgress.getText())){
                float norm = normInt.getNorm(valueToWrite);
                int progressValue = (int)(norm * PROGRESS_BAR_MAX);
                lblProgress.setText(valueToString(valueToWrite));
                ProgressBar progressBar = view.findViewById(R.id.bar_progress);
                progressBar.setProgress(progressValue);
            }
        }
    });
    return this;
}
protected String valueToString(long value){
    return String.valueOf(value);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
