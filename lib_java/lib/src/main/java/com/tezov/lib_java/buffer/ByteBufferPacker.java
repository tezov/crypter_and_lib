/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.buffer;

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
import com.tezov.lib_java.debug.DebugException;

import java.io.ByteArrayInputStream;

public class ByteBufferPacker{

private static final ByteBufferPacker packer = new ByteBufferPacker();
public synchronized static byte[] packData(byte[] data){
    return packer.pack(data);
}
public synchronized static byte[] unpackData(byte[] data){
    return packer.unpack(data);
}

private NibbleOutputStream out = null;
private NibbleInputStreamStack in = null;
private NibbleOutputStream bufferOut = null;

public static class NibbleInputStream {
    private final ByteArrayInputStream in;
    private boolean nTop = true;
    private byte b = 0x00;
    private int length;
    public NibbleInputStream(byte[] data, boolean isOdd){
        this(data, 0, data.length, isOdd);
    }
    public NibbleInputStream(byte[] data, int offset, int length, boolean isOdd){
        in = new ByteArrayInputStream(data, offset, length);
        this.length = length * 2;
        if(isOdd){
            this.length -= 1;
        }
    }
    public boolean isOdd(){
        return length%2 != 0;
    }
    public boolean isEven(){
        return length%2 == 0;
    }
    public int available(){
        return length;
    }
    public byte read(){
        if(length<=0){
            return END_STREAM;
        }
        if(nTop){
            b = (byte)in.read();
        }
        byte n = b;
        if(nTop){
            n = (byte)(n >> 4);
        }
        n = (byte)(n & 0x0F);
        nTop = !nTop;
        length-=1;
        return n;
    }
}
public static class NibbleOutputStream {
    private final ByteBufferOutput out;
    private boolean nTop;
    private byte b;
    private int length;
    public NibbleOutputStream(){
        out = ByteBufferOutput.obtain();
        clear();
    }
    public int length(){
        return length;
    }
    public boolean isOdd(){
        return length%2 != 0;
    }
    public boolean isEven(){
        return length%2 == 0;
    }
    public void write(byte n){
        if(nTop){
            b = (byte)(n << 4);
        }
        else{
            b = (byte)(b | (n & 0x0F));
            write();
        }
        nTop = !nTop;
        length++;
    }
    private void write(){
        try{
            out.write(b);
        } catch(Throwable e){
DebugException.start().log(e).end();
        }
    }
    public byte[] toBytes(){
        if(!nTop){
            b = (byte)(b & 0xF0);
            write();
        }
        return out.toBytes();
    }
    public void clear(){
        out.clear();
        length = 0;
        nTop = true;
        b = 0x00;
    }
}
private static class NibbleInputStreamStack extends NibbleInputStream{
    private final NibbleOutputStream tmpOut;
    private NibbleInputStream tmpIn = null;
    private int length;
    public NibbleInputStreamStack(byte[] data, boolean isOdd){
        this(data, 0, data.length, isOdd);
    }
    public NibbleInputStreamStack(byte[] data, int offset, int length, boolean isOdd){
        super(data, offset, length, isOdd);
        this.length = super.available();
        tmpOut = new NibbleOutputStream();
    }
    @Override
    public int available(){
        return length;
    }
    private boolean tempAvailable(){
        if((tmpIn != null) && (tmpIn.available() > 0)){
            return true;
        }
        if(tmpOut.length()>0){
            tmpIn = new NibbleInputStream(tmpOut.toBytes(), tmpOut.isOdd());
            tmpOut.clear();
            return true;
        } else {
            tmpIn = null;
            return false;
        }
    }
    @Override
    public byte read(){
        if(length<=0){
            return END_STREAM;
        }
        byte b;
        if(tempAvailable()){
            b = tmpIn.read();
        } else {
            b = super.read();
        }
        length -=1;
        return b;
    }
    public void write(byte b){
        tmpOut.write(b);
        length +=1;
    }
}

private final static byte END_STREAM = 0x10;

private final static byte ODD_CODE = 0x00;
private final static byte ODD_VALUE = 0x00;
private final static byte ODD_MAX_NIBBLE = 0x0F;

private final static int EVEN_MAX_NIBBLE = 0x1E * 7;
private final static byte EVEN_MAX_CHAIN = 0x1F;
private final static byte EVEN_FOLLOWING_ODD_ALLOWED = 4;
private final static byte EVEN_CODE_MASK_ODD = (byte)0x08;

public byte[] pack(byte[] data){
    if((data == null) || data.length <=0){
        return null;
    }
    return pack(data, 0, data.length);
}
public byte[] pack(byte[] data, int offset, int length){
    byte[] dataOut = null;
    try{
        out = new NibbleOutputStream();
        in = new NibbleInputStreamStack(data, offset, length,  false);
        bufferOut = new NibbleOutputStream();
        while(in.available() > 0){
            byte nibble = in.read();
            if(nibble == ODD_VALUE){
                byte count = packCountOdd();
                out.write(ODD_CODE);
                out.write(count);
            }
            else {
                bufferOut.clear();
                bufferOut.write(nibble);
                int count = packCountEven();
                boolean isOdd = count%2==1;
                byte chain = (byte)((count / (EVEN_MAX_CHAIN+1)) + 1);
                byte code = (byte)((isOdd ? EVEN_CODE_MASK_ODD : 0x00) | chain);
                byte lengthNibble = (byte)((count % (EVEN_MAX_CHAIN+1))/2);
                out.write(code);
                out.write(lengthNibble);
                NibbleInputStream bufferIn = new NibbleInputStream(bufferOut.toBytes(), bufferOut.isOdd());
                byte bufferNibble;
                while((bufferNibble=bufferIn.read())!=END_STREAM){
                    out.write(bufferNibble);
                }
            }
        }
        dataOut = out.toBytes();
    }
    catch(Throwable e){
DebugException.start().log(e).end();
    }
    out = null;
    in = null;
    bufferOut = null;
    return dataOut;
}
private byte packCountOdd(){
    byte count = 0;
    byte nibble;
    while((count < ODD_MAX_NIBBLE) && ((nibble = in.read()) != END_STREAM)){
        if(nibble == ODD_VALUE){
            count ++;
        } else {
            in.write(nibble);
            break;
        }
    }
    return count;
}
private int packCountEven(){
    int count = 0;
    int countOdd = 0;
    while(count < EVEN_MAX_NIBBLE){
        byte nibble = in.read();
        if(nibble != END_STREAM){
            if(nibble != ODD_VALUE){
                if(countOdd != 0){
                    int diff = ((count + countOdd) - EVEN_MAX_NIBBLE);
                    if(diff > 0){
                        countOdd -= diff;
                        for(int i=0;i<countOdd;i++){
                            packCountEven_WriteBuffer(ODD_VALUE);
                        }
                        count+=countOdd;
                        for(int i=0;i<diff;i++){
                            in.write(ODD_VALUE);
                        }
                        in.write(nibble);
                        break;
                    }
                    else{
                        for(int i=0;i<countOdd;i++){
                            packCountEven_WriteBuffer(ODD_VALUE);
                        }
                        count+=countOdd;
                        countOdd = 0;
                        packCountEven_WriteBuffer(nibble);
                        count++;
                    }
                }
                else{
                    packCountEven_WriteBuffer(nibble);
                    count++;
                }
            }
            else if(countOdd < EVEN_FOLLOWING_ODD_ALLOWED){
                countOdd++;
            }
            else {
                for(int i=0;i<(countOdd+1);i++){
                    in.write(ODD_VALUE);
                }
                break;
            }
        }
        else{
            for(int i=0;i<countOdd;i++){
                in.write(ODD_VALUE);
            }
            break;
        }
    }
    return count;
}
private void packCountEven_WriteBuffer(byte nibble){
    if(bufferOut!=null){
        bufferOut.write(nibble);
    }
}

public byte[] unpack(byte[] data){
    if((data == null) || data.length <=0){
        return null;
    }
    return unpack(data, 0, data.length);
}
public byte[] unpack(byte[] data, int offset, int length){
    byte[] dataOut = null;
    try{
        out = new NibbleOutputStream();
        in = new NibbleInputStreamStack(data, offset, length, false);
        while(in.available() > 0){
            byte code = in.read();
            byte lengthNibble = in.read();
            if(lengthNibble == END_STREAM){
                break;
            }
            if(code == ODD_CODE){
                int count = lengthNibble+1;
                for(int i=0; i<count; i++){
                    out.write(ODD_VALUE);
                }
            }
            else {
                boolean isOdd = (code & EVEN_CODE_MASK_ODD) == EVEN_CODE_MASK_ODD;
                byte chain = (byte)(code & (~EVEN_CODE_MASK_ODD));
                int count = 1 + ((chain - 1) * (EVEN_MAX_CHAIN+1)) + (lengthNibble*2);
                if(isOdd){
                    count+=1;
                }
                for(int i=0;i<count;i++){
                    out.write(in.read());
                }
            }
        }
        dataOut = out.toBytes();
    }
    catch(Throwable e){
DebugException.start().log(e).end();
    }
    out = null;
    in = null;
    return dataOut;
}

}
