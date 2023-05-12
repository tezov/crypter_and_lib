/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.file;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugObject;
import com.tezov.lib_java.socket.UtilsSocket;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UtilsStream{
public final static int NULL_LENGTH = -1;

public static <I extends InputStream> I close(InputStream in){
    if(in != null){
        try{
            in.close();
        } catch(Throwable ec){

DebugException.start().log(ec).end();

        }
    }
    return null;
}
public static <O extends OutputStream> O close(OutputStream out){
    if(out != null){
        try{
            out.close();
        } catch(Throwable ec){

DebugException.start().log(ec).end();

        }
    }
    return null;
}
public static <S extends StreamLinker> S close(StreamLinker stream){
    if(stream != null){
        try{
            stream.close();
        } catch(Throwable ec){
DebugException.start().log(ec).end();
        }
    }
    return null;
}

public interface ProgressListener{
    void onProgress(int current, int max);

}

public static class OutputStreamNotClosable extends OutputStream{
    protected OutputStream out;
    public OutputStreamNotClosable(OutputStream out){
DebugTrack.start().create(this).end();
        this.out = out;
    }

    @Override
    public void write(byte[] b) throws IOException{
        out.write(b);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        out.write(b, off, len);
    }
    @Override
    public void flush() throws IOException{
        out.flush();
    }
    @Override
    public void write(int i) throws IOException{
        out.write(i);

    }
    @Override
    public void close() throws IOException{

    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class InputStreamNotClosable extends InputStream{
    protected InputStream in;
    public InputStreamNotClosable(InputStream in){
DebugTrack.start().create(this).end();
        this.in = in;
    }
    @Override
    public int read(byte[] b) throws IOException{
        return in.read(b);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        return in.read(b, off, len);
    }
    @Override
    public long skip(long n) throws IOException{
        return in.skip(n);
    }
    @Override
    public int available() throws IOException{
        return in.available();
    }
    @Override
    public synchronized void mark(int readlimit){
        in.mark(readlimit);
    }
    @Override
    public synchronized void reset() throws IOException{
        in.reset();
    }
    @Override
    public boolean markSupported(){
        return in.markSupported();
    }
    @Override
    public int read() throws IOException{
        return in.read();
    }
    @Override
    public void close() throws IOException{
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}
public static class InputStreamLimitAvailability extends InputStreamNotClosable{
    private int availability;
    private final byte[] oneByteBuffer;
    public InputStreamLimitAvailability(InputStream in, int availability){
        super(in);
        this.availability = availability;
        oneByteBuffer = new byte[1];
    }
    @Override
    public int read() throws IOException{
        if(read(oneByteBuffer, 0, 1) != NULL_LENGTH){
            return oneByteBuffer[0];
        } else {
            return NULL_LENGTH;
        }
    }
    @Override
    public int read(byte[] b) throws IOException{
        return read(b, 0, b.length);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        if(availability <= 0){
            return NULL_LENGTH;
        }
        int diff = availability - len;
        if(diff < 0){
            len = availability;
        }
        int l = in.read(b, off, len);
        if(l != NULL_LENGTH){
            availability -= l;
        }
        return l;
    }
    @Override
    public long skip(long n) throws IOException{
        if(availability <= 0){
            return 0;
        }
        int diff = (int)(availability - n);
        if(diff < 0){
            n = availability;
        }
        long l = in.skip(n);
        availability -= l;
        return l;
    }
    @Override
    public int available() throws IOException{
        return availability;
    }
    @Override
    public synchronized void mark(int readlimit){

    }
    @Override
    public synchronized void reset() throws IOException{
        throw new IOException("mark/reset not supported");
    }
    @Override
    public boolean markSupported(){
        return false;
    }
}

public abstract static class InputStreamW extends InputStream{
    protected InputStream in;
    private StreamLinker streamLinker = null;
    public InputStreamW(File file) throws IOException{
        this(file.getInputStream());
    }
    public InputStreamW(InputStream in){
DebugTrack.start().create(this).end();
        this.in = in;
        init();
    }
    protected void init(){

    }
    public StreamLinker getStreamLinker(){
        return streamLinker;
    }
    public InputStreamW setStreamLinker(StreamLinker streamLinker){
        this.streamLinker = streamLinker;
        return this;
    }
    @Override
    public int read() throws IOException{
        return in.read();
    }
    @Override
    public int read(byte[] b) throws IOException{
        return in.read(b);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        return in.read(b, off, len);

    }
    @Override
    public long skip(long n) throws IOException{
        return in.skip(n);
    }
    @Override
    public int available() throws IOException{
        return in.available();
    }
    @Override
    public void close() throws IOException{
        if(in != null){
            in.close();
            in = null;
        }
    }
    @Override
    public synchronized void mark(int readlimit){
        in.mark(readlimit);
    }
    @Override
    public synchronized void reset() throws IOException{
        in.reset();
    }
    @Override
    public boolean markSupported(){
        return in.markSupported();
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public static class InputStreamAppendCrc extends InputStreamW{
    private UtilsFile.DigesterCRC32 digester;
    private ByteArrayInputStream crcBuffer = null;
    private byte[] oneByteBuffer;
    public InputStreamAppendCrc(File file) throws IOException{
        this(file.getInputStream());
    }
    public InputStreamAppendCrc(InputStream in){
        super(in);
        digester = new UtilsFile.DigesterCRC32();
        oneByteBuffer = new byte[1];
    }
    @Override
    public int available() throws IOException{
        int available = super.available();
        if(crcBuffer == null){
            available += UtilsFile.DigesterCRC32.length();
        }
        else{
            available += crcBuffer.available();
        }
        return available;
    }
    @Override
    public int read() throws IOException{
        if(read(oneByteBuffer, 0, 1) != NULL_LENGTH){
            return oneByteBuffer[0];
        } else {
            return NULL_LENGTH;
        }
    }
    @Override
    public int read(byte[] b) throws IOException{
        return read(b, 0, b.length);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        if(crcBuffer == null){
            int lengthRead = super.read(b, off, len);
            if(lengthRead > 0){
                digester.update(b, off, lengthRead);
                return lengthRead;
            } else {
                crcBuffer = new ByteArrayInputStream(digester.getValueByte());
            }
        }
        return crcBuffer.read(b, off, len);
    }
    @Override
    public void close() throws IOException{
        if(crcBuffer != null){
            crcBuffer.close();
            crcBuffer = null;
        }
        if(digester != null){
            digester = null;
        }
        super.close();
    }
}

public abstract static class InputStreamProgress extends InputStreamW implements ProgressListener{
    protected int lengthRead = 0;
    protected Integer lengthToRead = null;
    public InputStreamProgress(File file) throws IOException{
        super(file);
    }
    public InputStreamProgress(InputStream in){
        super(in);
    }
    public int getLengthRead(){
        return lengthRead;
    }
    public Integer getLengthToRead(){
        return lengthToRead;
    }
    public <I extends InputStreamW> I setTotalLengthToRead(Integer value){
        this.lengthRead = 0;
        this.lengthToRead = value;
        return (I)this;
    }
    @Override
    public int read() throws IOException{
        int value = super.read();
        if(value > 0){
            updateLengthRead(1);
        }
        return value;
    }
    @Override
    public int read(byte[] b) throws IOException{
        int lengthRead = super.read(b);
        updateLengthRead(lengthRead);
        return lengthRead;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        int lengthRead = super.read(b, off, len);
        updateLengthRead(lengthRead);
        return lengthRead;
    }
    protected void updateLengthRead(int len){
        if(len > 0){
            lengthRead += len;
            if(lengthToRead != null){
                Handler.SECONDARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        onProgress(getLengthRead(), lengthToRead);
                    }
                });
            }
        }
    }

}

public abstract static class InputStreamProgressAppendCrc extends InputStreamAppendCrc implements ProgressListener{
    protected int lengthRead = 0;
    protected Integer lengthToRead = null;
    public InputStreamProgressAppendCrc(File file) throws IOException{
        this(file.getInputStream());
    }
    public InputStreamProgressAppendCrc(InputStream in){
        super(in);
    }
    public int getLengthRead(){
        return lengthRead;
    }
    public Integer getLengthToRead(){
        return lengthToRead;
    }
    public <I extends InputStreamProgressAppendCrc> I setTotalLengthToRead(Integer value){
        this.lengthRead = 0;
        this.lengthToRead = value;
        return (I)this;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException{
        int length = super.read(b, off, len);
        updateLengthRead(length);
        return length;
    }
    protected void updateLengthRead(int len){
        if(len > 0){
            lengthRead += len;
            if(lengthToRead != null){
                Handler.SECONDARY().post(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        onProgress(getLengthRead(), lengthToRead);
                    }
                });
            }
        }
    }

}

public abstract static class OutputStreamW extends OutputStream{
    private OutputStream out;
    private StreamLinker streamLinker = null;
    public OutputStreamW(File file) throws IOException{
        this(file.getOutputStream());
    }
    public OutputStreamW(OutputStream out){
DebugTrack.start().create(this).end();
        this.out = out;
        init();
    }
    protected void init(){

    }
    public StreamLinker getStreamLinker(){
        return streamLinker;
    }
    public OutputStreamW setStreamLinker(StreamLinker streamLinker){
        this.streamLinker = streamLinker;
        return this;
    }
    @Override
    public void write(int b) throws IOException{
        out.write(b);
    }
    @Override
    public void write(byte[] b) throws IOException{
        out.write(b);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        out.write(b, off, len);
    }
    @Override
    public void flush() throws IOException{
        out.flush();
    }
    @Override
    public void close() throws IOException{
        if(out != null){
            out.close();
            out = null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public abstract static class OutputStreamProgress extends OutputStreamW implements ProgressListener{
    protected int lengthWritten = 0;
    protected Integer lengthToWrite = null;
    public OutputStreamProgress(File file) throws IOException{
        super(file);
    }
    public OutputStreamProgress(OutputStream out){
        super(out);
    }
    public int getLengthWritten(){
        return lengthWritten;
    }
    public Integer getLengthToWrite(){
        return lengthToWrite;
    }
    public <O extends OutputStreamW> O setTotalLengthToWrite(Integer value){
        this.lengthWritten = 0;
        this.lengthToWrite = value;
        return (O)this;
    }
    @Override
    public void write(int b) throws IOException{
        super.write(b);
        updateLengthWritten(1);
    }
    @Override
    public void write(byte[] b) throws IOException{
        super.write(b);
        updateLengthWritten(b.length);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        super.write(b, off, len);
        updateLengthWritten(len);
    }
    protected void updateLengthWritten(int len){
        lengthWritten += len;
        if(lengthToWrite != null){
            Handler.SECONDARY().post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    onProgress(getLengthWritten(), lengthToWrite);
                }
            });
        }
    }

}

public static class OutputStreamCheckCrc extends OutputStreamW{
    protected int lengthWritten = 0;
    protected Integer lengthToWrite = null;
    private UtilsFile.DigesterCRC32 digester;
    private ByteArrayOutputStream crcBuffer = null;
    private byte[] oneByteBuffer;
    private boolean crcHasBeenChecked = false;
    public OutputStreamCheckCrc(File file) throws IOException{
        this(file.getOutputStream());
    }
    public OutputStreamCheckCrc(OutputStream out){
        super(out);
        digester = new UtilsFile.DigesterCRC32();
        oneByteBuffer = new byte[1];
    }
    public int getLengthWritten(){
        return lengthWritten;
    }
    public Integer getLengthToWrite(){
        return lengthToWrite;
    }
    public void setTotalLengthToWrite(Integer value){
        this.lengthWritten = 0;
        this.lengthToWrite = value;
    }
    @Override
    public void write(int b) throws IOException{
        oneByteBuffer[0] = (byte)b;
        write(oneByteBuffer);
    }
    @Override
    public void write(byte[] b) throws IOException{
        write(b, 0, b.length);
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException{
        if(lengthToWrite == null){
            throw new IOException("totalLengthToWrite is null, use read(InputStream in, Integer len) or setTotalLengthToWrite(Integer value)");
        }
        if(crcBuffer == null){
            int diff = (lengthToWrite - UtilsFile.DigesterCRC32.length()) - (len + lengthWritten);
            if(diff > 0){
                digester.update(b, off, len);
                super.write(b, off, len);
                updateLengthWritten(len);
                return;
            }
            int remaining = len + diff;
            if(remaining < 0){
                throw new IOException("remaining is negative");
            }
            if(remaining > 0){
                digester.update(b, off, remaining);
                super.write(b, off, remaining);
                updateLengthWritten(remaining);
            }
            off += remaining;
            len -= remaining;
            crcBuffer = new ByteArrayOutputStream(UtilsFile.DigesterCRC32.length());
        }
        if((len - off + crcBuffer.size()) > UtilsFile.DigesterCRC32.length()){
            throw new IOException("try to write over the crc retrofit.data");
        }
        crcBuffer.write(b, off, len);
        updateLengthWritten(len);
        if(crcBuffer.size() == UtilsFile.DigesterCRC32.length()){
            if(!digester.equals(crcBuffer.toByteArray())){
                throw new IOException("crc mismatches");
            }
            crcHasBeenChecked = true;
        }
    }
    protected void updateLengthWritten(int len){
        lengthWritten += len;
    }
    @Override
    public void close() throws IOException{
        if(crcBuffer != null){
            crcBuffer.close();
            crcBuffer = null;
        }
        if(digester != null){
            digester = null;
        }
        if(!crcHasBeenChecked){
            throw new IOException("crc has not been checked");
        }
        super.close();
    }
}

public abstract static class OutputStreamProgressCheckCrc extends OutputStreamCheckCrc implements ProgressListener{
    public OutputStreamProgressCheckCrc(File file) throws IOException{
        this(file.getOutputStream());
    }
    public OutputStreamProgressCheckCrc(OutputStream out){
        super(out);
    }
    @Override
    protected void updateLengthWritten(int len){
        super.updateLengthWritten(len);
        if(lengthToWrite != null){
            Handler.SECONDARY().post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    onProgress(getLengthWritten(), lengthToWrite);
                }
            });
        }
    }
}

public abstract static class StreamLinker{
    protected InputStream in;
    protected OutputStream out;
    public StreamLinker(){
        this(null, null);
    }
    public StreamLinker(InputStream in){
        this(in, null);
    }
    public StreamLinker(OutputStream out){
        this(null, out);
    }
    public StreamLinker(InputStream in, OutputStream out){
DebugTrack.start().create(this).end();
        this.in = in;
        this.out = out;
    }

    public InputStream getIn(){
        return in;
    }
    public StreamLinker setIn(InputStream in){
        this.in = in;
        return this;
    }
    public InputStream getInNotClosable(){
        return new InputStreamNotClosable(in);
    }
    public InputStream getInNotClosableLimitAvailability(int length){
        return new InputStreamLimitAvailability(in, length);
    }
    public int available() throws IOException{
        return in.available();
    }
    public int read() throws IOException{
        return in.read();
    }
    public int read(byte[] b) throws IOException{
        return in.read(b);
    }
    public int read(byte[] b, int off, int len) throws IOException{
        return in.read(b, off, len);
    }

    public OutputStream getOut(){
        return out;
    }
    public StreamLinker setOut(OutputStream out){
        this.out = out;
        return this;
    }
    public OutputStream getOutNotClosable(){
        return new OutputStreamNotClosable(out);
    }
    public void write(int b) throws IOException{
        out.write(b);
    }
    public void write(byte[] b) throws IOException{
        out.write(b);
    }
    public void write(byte[] b, int off, int len) throws IOException{
        out.write(b, off, len);
    }

    public void transfer(Integer len) throws IOException{
        if(in instanceof InputStreamProgressAppendCrc){
            ((InputStreamProgressAppendCrc)in).setTotalLengthToRead(len);
        } else if(in instanceof InputStreamProgress){
            ((InputStreamProgress)in).setTotalLengthToRead(len);
        }
        if(out instanceof OutputStreamProgress){
            ((OutputStreamProgress)out).setTotalLengthToWrite(len);
        } else if(out instanceof OutputStreamCheckCrc){
            ((OutputStreamCheckCrc)out).setTotalLengthToWrite(len);
        }
    }
    public void transfer() throws IOException{
        transfer((Integer)null);
    }

    public void transfer(OutputStream out) throws IOException{
        transfer(out, null);
    }
    public void transfer(OutputStream out, Integer len) throws IOException{
        OutputStream previousOut = this.out;
        setOut(out);
        transfer(len);
        setOut(previousOut);
    }

    public void transfer(InputStream in) throws IOException{
        transfer(in, null);
    }
    public void transfer(InputStream in, Integer len) throws IOException{
        InputStream previousIn = this.in;
        setIn(in);
        transfer(len);
        setIn(previousIn);
    }

    public void close() throws IOException{
        IOException eIn = null;
        IOException eOut = null;
        try{
            closeIn();
        }catch(IOException e){
            eIn = e;
        }
        try{
            closeOut();
        }catch(IOException e){
            eOut = e;
        }
        if((eIn != null)){
            throw eIn;
        }
        if((eOut != null)){
            throw eOut;
        }
    }
    public void closeIn() throws IOException{
        if(in != null){
            in.close();
            in = null;
        }
    }
    public void closeOut() throws IOException{
        if(out != null){
            out.close();
            out = null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }
}
public static class StreamLinkerFile extends StreamLinker{
    public StreamLinkerFile(){
    }
    public StreamLinkerFile(InputStream in){
        super(in);
    }
    public StreamLinkerFile(OutputStream out){
        super(out);
    }
    public StreamLinkerFile(InputStream in, OutputStream out){
        super(in, out);
    }
    public static void transfer(InputStream in, OutputStream out, Integer len) throws IOException{
        try{
            UtilsFile.copy(in, out, len);
        } catch(IOException e){
            throw e;
        } catch(Throwable e){
DebugException.start().log(e).end();
            throw new IOException(e.getMessage());
        }
    }
    @Override
    public void transfer(Integer len) throws IOException{
        super.transfer(len);
        transfer(in, out, len);
    }
}
public abstract static class StreamLinkerProgress extends StreamLinker{
    public StreamLinkerProgress(){
    }
    public StreamLinkerProgress(InputStream in){
        super(in);
    }
    public StreamLinkerProgress(OutputStream out){
        super(out);
    }
    public StreamLinkerProgress(InputStream in, OutputStream out){
        super(in, out);
    }
    public StreamLinkerProgress(InputStreamProgress in, OutputStream out){
        super(in, out);
    }
    @Override
    public void transfer() throws IOException{
        transfer(available());
    }
    @Override
    public void transfer(OutputStream out) throws IOException{
        transfer(out, available());
    }
    @Override
    public void transfer(InputStream in) throws IOException{
        transfer(in, available());
    }
}
public static class StreamLinkerFileProgress extends StreamLinkerProgress{
    public StreamLinkerFileProgress(){
    }
    public StreamLinkerFileProgress(InputStream in){
        super(in);
    }
    public StreamLinkerFileProgress(OutputStream out){
        super(out);
    }
    public StreamLinkerFileProgress(InputStream in, OutputStream out){
        super(in, out);
    }
    public StreamLinkerFileProgress(InputStreamProgress in, OutputStream out){
        super(in, out);
    }
    @Override
    public void transfer(Integer len) throws IOException{
        super.transfer(len);
        StreamLinkerFile.transfer(in, out, len);
    }

}
public static class StreamLinkerSocket extends StreamLinker{
    public StreamLinkerSocket(){
    }
    public StreamLinkerSocket(InputStream in){
        super(in);
    }
    public StreamLinkerSocket(OutputStream out){
        super(out);
    }
    public StreamLinkerSocket(InputStream in, OutputStream out){
        super(in, out);
    }
    public static void transfer(InputStream in, OutputStream out, Integer len) throws IOException{
        try{
            UtilsSocket.receive(in, out, len);
        } catch(IOException e){
            throw e;
        } catch(Throwable e){

DebugException.start().log(e).end();

            throw new IOException(e.getMessage());
        }
    }
    @Override
    public void transfer(Integer len) throws IOException{
        super.transfer(len);
        transfer(in, out, len);
    }

}
public static class StreamLinkerSocketProgress extends StreamLinkerProgress{
    public StreamLinkerSocketProgress(){
    }
    public StreamLinkerSocketProgress(InputStream in){
        super(in);
    }
    public StreamLinkerSocketProgress(OutputStream out){
        super(out);
    }
    public StreamLinkerSocketProgress(InputStreamProgress in, OutputStream out){
        super(in, out);
    }
    @Override
    public void transfer(Integer len) throws IOException{
        super.transfer(len);
        StreamLinkerSocket.transfer(in, out, len);
    }

}

}
