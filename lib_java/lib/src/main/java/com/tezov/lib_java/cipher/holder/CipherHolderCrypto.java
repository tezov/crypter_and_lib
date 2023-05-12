/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.holder;

import com.tezov.lib_java.buffer.ByteBufferBuilder;
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
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.dataAdapter.object.DataObjectEncoderAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringEncoderAdapter;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataAdapter.object.DataObjectAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.key.KeyObfusc;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.type.primitive.string.StringBase64To;

public class CipherHolderCrypto extends CipherHolder{
public final static KeyObfusc.Transformation CIPHER_OBFUSC_TRANSFORMATION =
        AppConfig.getKeyObfuscTransformation(AppConfigKey.CIPHER_HOLDER_OBFUSC_TRANSFORMATION.getId());
public final static KeySim.Transformation CIPHER_SIM_TRANSFORMATION =
        AppConfig.getKeySimTransformation(AppConfigKey.CIPHER_HOLDER_SIM_TRANSFORMATION.getId());

public CipherHolderCrypto(){

}

@Override
public void createEncoderKey(DataStringAdapter.Format format){
    encoderKey = Encoder.newEncoder((defCipherKey)keyKey, DataStringAdapter.forEncoder(format));
    decoderKey = Decoder.newDecoder((defCipherKey)keyKey, DataStringAdapter.forDecoder(format));
}
@Override
public Encoder<String, String> getEncoderKey(){
    @SuppressWarnings("unchecked") Encoder<String, String> r = (Encoder<String, String>)super.getEncoderKey();
    return r;
}
@Override
public Decoder<String, String> getDecoderKey(){
    @SuppressWarnings("unchecked") Decoder<String, String> r = (Decoder<String, String>)super.getDecoderKey();
    return r;
}

@Override
public void createEncoderValue(DataStringAdapter.Format format){
    encoderValue = Encoder.newEncoder((defCipherKey)keyValue, DataObjectAdapter.forEncoder(format));
    decoderValue = Decoder.newDecoder((defCipherKey)keyValue, DataObjectAdapter.forDecoder(format));
}
@Override
public Encoder<Object, String> getEncoderValue(){
    @SuppressWarnings("unchecked") Encoder<Object, String> r = (Encoder)super.getEncoderValue();
    return r;
}
@Override
public Decoder<String, Object> getDecoderValue(){
    @SuppressWarnings("unchecked") Decoder<String, Object> r = (Decoder)super.getDecoderValue();
    return r;
}

@Override
protected byte[] specToBytes(){
    DataObjectEncoderAdapter adapterValue = (DataObjectEncoderAdapter)encoderValue.getDataAdapter();
    DataStringEncoderAdapter adapterKey = (DataStringEncoderAdapter)encoderKey.getDataAdapter();
    ByteBufferBuilder chunk = ByteBufferBuilder.obtain();
    chunk.put(getKeyValue().specToBytes());
    chunk.put(adapterValue.getFormat().name());
    chunk.put(encoderValue.encode(getKeyKey().specToBytes()));
    chunk.put(adapterKey.getFormat().name());
    chunk.put(encoderValue.encode(getEncoderKey().getIv()));
    return chunk.arrayPacked();
}

public static class Generator{
    PasswordCipher passwordKey = null;
    DataStringAdapter.Format formatKey = DataStringAdapter.Format.HEX;
    PasswordCipher passwordValue = null;
    DataStringAdapter.Format formatValue = DataStringAdapter.Format.HEX;
    ByteBufferPacker packerValue = null;
    ByteBufferPacker packerKey = null;
    public Generator setPasswordKey(PasswordCipher passwordKey){
        this.passwordKey = passwordKey;
        return this;
    }
    public Generator setPasswordValue(PasswordCipher passwordValue){
        this.passwordValue = passwordValue;
        return this;
    }
    public Generator setPasswordKeyAndValue(PasswordCipher password){
        this.passwordKey = password;
        this.passwordValue = password;
        return this;
    }

    public Generator setFormatKey(DataStringAdapter.Format formatKey){
        this.formatKey = formatKey;
        return this;
    }
    public Generator setFormatValue(DataStringAdapter.Format formatValue){
        this.formatValue = formatValue;
        return this;
    }
    public Generator setFormatKeyAndValue(DataStringAdapter.Format format){
        this.formatKey = format;
        this.formatValue = format;
        return this;
    }
    public Generator setPackerValue(ByteBufferPacker packerValue){
        this.packerValue = packerValue;
        return this;
    }
    public Generator setPackerKey(ByteBufferPacker packerKey){
        this.packerKey = packerKey;
        return this;
    }
    public Generator setPacker(ByteBufferPacker packer){
        this.packerValue = packer;
        this.packerKey = packer;
        return this;
    }
    public CipherHolderCrypto generate(){
        CipherHolderCrypto cipher = new CipherHolderCrypto();
        cipher.setKeyValue(new KeySim().generate(passwordValue, CIPHER_SIM_TRANSFORMATION), formatValue);
        if(packerValue != null){
            cipher.getEncoderValue().setPacker(packerValue);
            cipher.getDecoderValue().setPacker(packerValue);
        }
        cipher.setKeyKey(new KeyObfusc().generate(passwordKey, CIPHER_OBFUSC_TRANSFORMATION), formatKey);
        if(packerKey != null){
            cipher.getEncoderKey().setPacker(packerKey);
            cipher.getDecoderKey().setPacker(packerKey);
        }
        cipher.getEncoderKey().setRandomIv();
        return cipher;
    }

}

public static class Builder{
    PasswordCipher passwordKey = null;
    PasswordCipher passwordValue = null;
    String spec = null;
    ByteBufferPacker packerValue = null;
    ByteBufferPacker packerKey = null;
    public Builder setPasswordKey(PasswordCipher passwordKey){
        this.passwordKey = passwordKey;
        return this;
    }
    public Builder setPasswordValue(PasswordCipher passwordValue){
        this.passwordValue = passwordValue;
        return this;
    }
    public Builder setPasswordKeyAndValue(PasswordCipher password){
        this.passwordKey = password;
        this.passwordValue = password;
        return this;
    }
    public Builder setSpec(String spec){
        this.spec = spec;
        return this;
    }
    public Builder setPackerValue(ByteBufferPacker packerValue){
        this.packerValue = packerValue;
        return this;
    }
    public Builder setPackerKey(ByteBufferPacker packerKey){
        this.packerKey = packerKey;
        return this;
    }
    public Builder setPacker(ByteBufferPacker packer){
        this.packerValue = packer;
        this.packerKey = packer;
        return this;
    }

    public CipherHolderCrypto build(){
        CipherHolderCrypto cipher = new CipherHolderCrypto();
        ByteBuffer chunk = ByteBuffer.wrapPacked(StringBase64To.Bytes(spec));
        cipher.setKeyValue(KeySim.fromSpec(passwordValue, chunk.getBytes()), DataStringAdapter.Format.valueOf(chunk.getString()));
        if(packerValue != null){
            cipher.getEncoderValue().setPacker(packerValue);
            cipher.getDecoderValue().setPacker(packerValue);
        }
        cipher.setKeyKey(KeyObfusc.fromSpec(passwordKey, cipher.decoderValue.decode(chunk.getBytes())), DataStringAdapter.Format.valueOf(chunk.getString()));
        if(packerKey != null){
            cipher.getEncoderKey().setPacker(packerKey);
            cipher.getDecoderKey().setPacker(packerKey);
        }
        cipher.getEncoderKey().setIv(cipher.decoderValue.decode(chunk.getBytes()));
        return cipher;
    }

}


}
