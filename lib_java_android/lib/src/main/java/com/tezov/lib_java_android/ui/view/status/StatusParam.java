/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.view.status;

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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.type.defEnum.EnumBase;

public class StatusParam{
public final static long DELAY_INFO_SHORT_ms = 2000;
public final static long DELAY_INFO_LONG_ms = 4000;
public final static long DELAY_SUCCESS_SHORT_ms = 1000;
public final static long DELAY_SUCCESS_LONG_ms = 2000;
public final static long DELAY_FAIL_SHORT_ms = 2000;
public final static long DELAY_FAIL_LONG_ms = 4000;

public interface Color{
    Is FAILED = new Is("FAILED", R.attr.color_failed);
    Is SUCCEED = new Is("SUCCEED", R.attr.color_succeed);
    Is NEUTRAL = new Is("NEUTRAL", R.attr.color_neutral);
    Is INFO = new Is("INFO", R.attr.color_info);
    Is WARNING = new Is("WARNING", R.attr.color_warning);

    class Is extends EnumBase.Is{
        private Integer title = null;
        private Integer message = null;
        private Integer helper = null;
        private Integer background = null;
        private Integer border = null;
        public Is(String name, int attr){
            super(name);
            Integer themeDialog = AppContext.getResources().resolveAttributeId(R.attr.infoColorTheme, true);
            if(themeDialog != null){
                Integer infoStyleId = AppContext.getResources().resourceIdFromStyle(themeDialog, attr);
                if(infoStyleId != null){
                    this.title = AppContext.getResources().colorARGBFromStyle(infoStyleId, R.attr.colorTextTitle);
                    this.message = AppContext.getResources().colorARGBFromStyle(infoStyleId, R.attr.colorTextMessage);
                    this.helper = AppContext.getResources().colorARGBFromStyle(infoStyleId, R.attr.colorTextHelper);
                    this.background = AppContext.getResources().colorARGBFromStyle(infoStyleId, R.attr.colorBackground);
                    this.border = AppContext.getResources().colorARGBFromStyle(infoStyleId, R.attr.colorBackgroundBorder);
                }
            }
            if(this.title == null){
                this.title = AppContext.getResources().getColorARGB(R.color.Black);
            }
            if(this.message == null){
                this.message = AppContext.getResources().getColorARGB(R.color.Black);
            }
            if(this.helper == null){
                this.helper = AppContext.getResources().getColorARGB(R.color.DarkGray);
            }
            if(this.background == null){
                this.background = AppContext.getResources().getColorARGB(R.color.Gray);
            }
            if(this.border == null){
                this.border = AppContext.getResources().getColorARGB(R.color.DimGray);
            }
        }
        public int title(){
            return title;
        }
        public int message(){
            return message;
        }
        public int helper(){
            return helper;
        }
        public int background(){
            return background;
        }
        public int border(){
            return border;
        }

    }

}

}
