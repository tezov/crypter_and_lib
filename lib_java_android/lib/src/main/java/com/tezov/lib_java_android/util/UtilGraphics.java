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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class UtilGraphics{

public static void drawBound(Canvas canvas, View view){
    drawBound(canvas,view, view.getWidth(), view.getHeight());
}
public static void drawBound(Canvas canvas, View view, int width, int height){
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

    p.setStyle(Paint.Style.FILL);
    p.setColor(Color.LTGRAY);
    canvas.drawRect(view.getPaddingLeft(), view.getPaddingTop(),
            width-view.getPaddingRight(), height-view.getPaddingBottom(), p);

    p.setStyle(Paint.Style.STROKE);
    p.setStrokeWidth(2);
    p.setColor(Color.BLUE);
    canvas.drawRect(1, 1, width-2, height-2, p);

    p.setStyle(Paint.Style.STROKE);
    p.setStrokeWidth(2);
    p.setColor(Color.BLUE);
    canvas.drawOval(width / 2 - 5, height / 2 - 5, width / 2 + 5, height / 2 + 5, p);

    p.setColor(Color.BLUE);
    p.setStrokeWidth(2);
    canvas.drawLine(width / 2, 0, width / 2, height, p);
    canvas.drawLine(0, height / 2, width, height / 2, p);
}

}
