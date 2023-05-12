/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.renderScript;

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

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import com.tezov.lib_java_android.ScriptC_NV21_rotate;
import com.tezov.lib_java.buffer.ByteBuffer;

public class Script_NV21{

public static Bitmap toBitmap(ByteBuffer buffer, int width, int height){
    android.renderscript.RenderScript rs = RenderScript.getAndLock();
    ScriptIntrinsicYuvToRGB script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    Allocation yin = Allocation.createSized(rs, Element.YUV(rs), buffer.remaining());
    Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Allocation yout = Allocation.createFromBitmap(rs, b);
    yin.copyFrom(buffer.arrayRemaining());
    script.setInput(yin);
    script.forEach(yout);
    yout.copyTo(b);
    yin.destroy();
    yout.destroy();
    script.destroy();
    RenderScript.unLock();
    return b;
}

public static RequestRotate requestRotate(ByteBuffer buffer, int width, int height, int angle){
    return new RequestRotate(buffer, width, height, angle);
}
public static ResultRotate rotate(RequestRotate request){
    android.renderscript.RenderScript rs = RenderScript.getAndLock();
    ScriptC_NV21_rotate script = new ScriptC_NV21_rotate(rs);

    int planYLength = request.width * request.height;
    int planUVLength = planYLength >> 1;
    // ************ IN  //IMPROVE only 1 Allocation
    Allocation inPlaneY = Allocation.createTyped(rs, Type.createX(rs, Element.U8(rs), planYLength));
    inPlaneY.copyFrom(request.buffer.array(planYLength));
    script.set_inPlaneY(inPlaneY);
    Allocation inPlaneUV = Allocation.createTyped(rs, Type.createX(rs, Element.U8(rs), planUVLength));
    inPlaneUV.copyFrom(request.buffer.array(planUVLength));
    script.set_inPlaneUV(inPlaneUV);

    // ************ OUT //IMPROVE only 1 Allocation if we can set the x,y to ignore UV retrofit.data
    Allocation outPlaneY = Allocation.createTyped(rs, Type.createX(rs, Element.U8(rs), planYLength));
    Allocation outPlaneUV = Allocation.createTyped(rs, Type.createX(rs, Element.U8(rs), planUVLength));
    script.set_outPlaneUV(outPlaneUV);

    // ************ FOR EACH
    script.invoke_setParam(request.width, request.height, request.angle);
    script.forEach_rotate(outPlaneY);

    // ************ RESULT
    byte[] t = new byte[planYLength]; // IMPROVE use the same array did by buffer.array(count)
    outPlaneY.copyTo(t);
    byte[] s = new byte[planUVLength];  // IMPROVE
    outPlaneUV.copyTo(s);
    request.buffer.rewind().copy(t).copy(s); // IMPROVE

    // ************ DESTROY
    inPlaneY.destroy();
    inPlaneUV.destroy();
    outPlaneY.destroy();
    outPlaneUV.destroy();
    script.destroy();
    RenderScript.unLock();
    return new ResultRotate(request);
}
public static ResultRotate javaRotate(RequestRotate request){
    ByteBuffer bufferOutput = ByteBuffer.obtain(request.buffer.capacity());
    byte[] input = request.buffer.array();
    byte[] output = bufferOutput.array();
    int width = request.width;
    int height = request.height;
    int frameSize = width * height;
    int angle = (360 - request.angle) % 360;
    boolean swap = angle % 180 != 0;
    boolean xflip = angle % 270 != 0;
    boolean yflip = angle >= 180;
    for(int j = 0; j < height; j++){
        for(int i = 0; i < width; i++){
            // Y
            int iOut = xflip ? width - i - 1 : i;
            int jOut = yflip ? height - j - 1 : j;
            int yOut = swap ? iOut * height + jOut : jOut * width + iOut;
            output[yOut] = input[j * width + i];
            if((j % 2 == 1) || (i % 2 == 1)){
                continue;
            }
            // U/V
            iOut = iOut & 0xFFFFFFFE;
            jOut = jOut & 0xFFFFFFFE;
            int uOut = frameSize + (swap ? ((iOut >> 1) * height) + jOut : ((jOut >> 1) * width) + iOut);
            int uIn = frameSize + ((j >> 1) * width) + i;
            output[uOut] = input[uIn];
            output[uOut + 1] = input[uIn + 1];
        }
    }
    return new ResultRotate(request, bufferOutput);
}

public static class RequestRotate{
    private final ByteBuffer buffer;
    private final int width;
    private final int height;
    private final int angle;
    private RequestRotate(ByteBuffer buffer, int width, int height, int angle){
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.angle = angle;
    }
}
public static class ResultRotate{
    private final ByteBuffer buffer;
    private final int width;
    private final int height;
    private final boolean swapped;

    private ResultRotate(RequestRotate request){
        this(request.width, request.height, request.angle, request.buffer);
    }

    private ResultRotate(RequestRotate request, ByteBuffer buffer){
        this(request.width, request.height, request.angle, buffer);
    }

    private ResultRotate(int width, int height, int angle, ByteBuffer buffer){
        swapped = ((360 - angle) % 360) % 180 != 0;
        if(swapped){
            this.height = width;
            this.width = height;
        } else {
            this.width = width;
            this.height = height;
        }
        this.buffer = buffer;
    }

    public ByteBuffer getByteBuffer(){
        return buffer;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public boolean isSwapped(){
        return swapped;
    }

}

}
