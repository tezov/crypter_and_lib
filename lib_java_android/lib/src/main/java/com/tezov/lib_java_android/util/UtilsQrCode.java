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

import com.tezov.lib_java.type.primitive.string.StringBase58To;

import android.graphics.Bitmap;
import android.util.ArrayMap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.type.image.imageHolder.ImageBitmap;
import com.tezov.lib_java_android.type.image.imageHolder.ImageHolder;
import com.tezov.lib_java_android.type.image.imageHolder.ImageNV21;
import com.tezov.lib_java.type.primitive.BytesTo;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UtilsQrCode{

private final static int BLACK = 0xFF000000;
private final static int WHITE = 0xFFFFFFFF;

public static ImageBitmap toImageBitmap(byte[] b, int size_px){
    return new ImageBitmap(toBitmap(BytesTo.StringBase58(b), size_px));
}
public static ImageBitmap toImageBitmap(String s, int size_px){
    return new ImageBitmap(toBitmap(s, size_px));
}
public static Bitmap toBitmap(byte[] b, int size_px){
    return toBitmap(BytesTo.StringBase58(b), size_px);
}
public static Bitmap toBitmap(String s, int size_px){
    try{
        Map<EncodeHintType, ? super Object> hints = new ArrayMap<>();
        hints.put(EncodeHintType.MARGIN, 4);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.ISO_8859_1);
        BitMatrix result = new QRCodeWriter().encode(s, BarcodeFormat.QR_CODE, size_px, size_px, hints);
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for(int y = 0; y < h; y++){
            int offset = y * w;
            for(int x = 0; x < w; x++){
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}

public static byte[] fromImageToBytes(ImageHolder image, boolean tryPureBarcode){
    if(image instanceof ImageNV21){
        return fromImageToBytes((ImageNV21)image, tryPureBarcode);
    } else {
        return fromBitmapToBytes(image.toBitmap(), tryPureBarcode);
    }
}
public static byte[] fromBitmapToBytes(Bitmap bitmap, boolean tryPureBarcode){
    return StringBase58To.Bytes(fromBitmapToString(bitmap, tryPureBarcode));
}
public static byte[] fromImageToBytes(ImageNV21 image, boolean tryPureBarcode){
    return StringBase58To.Bytes(fromImageToString(image, tryPureBarcode));
}
public static String fromImageToString(ImageNV21 image, boolean tryPureBarcode){
    ByteBuffer buffer = image.toByteBuffer();
    PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(buffer.array(), image.getWidth(), image.getHeight(), 0, 0, image.getWidth(), image.getHeight(), false);
    return fromLuminanceToString(source, tryPureBarcode);
}
public static String fromImageToString(ImageHolder image, boolean tryPureBarcode){
    if(image instanceof ImageNV21){
        return fromImageToString((ImageNV21)image, tryPureBarcode);
    } else {
        return fromBitmapToString(image.toBitmap(), tryPureBarcode);
    }
}
public static String fromBitmapToString(Bitmap bitmap, boolean tryPureBarcode){
    int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
    bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
    return fromLuminanceToString(source, tryPureBarcode);
}

private static String fromLuminanceToString(LuminanceSource source, boolean tryPureBarcode){
    GlobalHistogramBinarizer binarizer = new GlobalHistogramBinarizer(source);
    BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
    QRCodeReader scanner = new QRCodeReader();
    Map<DecodeHintType, ? super Object> hints = new ArrayMap<>();
    hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
    hints.put(DecodeHintType.POSSIBLE_FORMATS, new BarcodeFormat[]{BarcodeFormat.QR_CODE});
    try{
        hints.put(DecodeHintType.TRY_HARDER, true);
        return scanner.decode(binaryBitmap, hints).getText();
    } catch(FormatException | ChecksumException | NotFoundException e){

    }
    if(tryPureBarcode){
        try{
            hints.remove(DecodeHintType.TRY_HARDER);
            hints.put(DecodeHintType.PURE_BARCODE, true);
            return scanner.decode(binaryBitmap, hints).getText();
        } catch(FormatException | ChecksumException | NotFoundException e){

        }
    }
    return null;
}

}
