/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tezov.crypter.application.ApplicationSystem;
import com.tezov.lib_java_android.ui.layout.FrameLayout;

public class ActivityLauncher extends AppCompatActivity{
@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

//    View view = FrameLayout.newWithSize(this, MATCH_PARENT, MATCH_PARENT);
//    view.setBackgroundColor(0xFFFF0000);
//    setContentView(view);
//    Handler handler = new Handler();
//    handler.postDelayed(new Runnable() {
//        @Override
//        public void run() {
//
//            ApplicationSystem application = (ApplicationSystem)getApplication();
//            if(!application.isClosing(ActivityLauncher.this)){
//                application.startMainActivity(ActivityLauncher.this, false, false);
//            }
//        }
//    }, 1000);

    ApplicationSystem application = (ApplicationSystem)getApplication();
    if(!application.isClosing(this)){
        application.startMainActivity(this, false, false);
    }

}


}