/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher.holder;

import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
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
import com.tezov.lib_java.buffer.ByteBufferPacker;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.definition.defCipherKey;
import com.tezov.lib_java.cipher.key.KeyObfusc;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.type.primitive.string.StringBase64To;

import static com.tezov.lib_java.cipher.key.KeyObfusc.Transformation.DES_CBC_PKCS5;

public class CipherHolderXor extends CipherHolder{
public final static KeyObfusc.Transformation CIPHER_OBFUSC_TRANSFORMATION =
        AppConfig.getKeyObfuscTransformation(AppConfigKey.CIPHER_HOLDER_OBFUSC_TRANSFORMATION.getId());

public CipherHolderXor(){
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
    public CipherHolderXor generate(){
        CipherHolderXor cipher =  new CipherHolderXor();
        cipher.setKeyValue(new KeyXor().generate(passwordValue), formatValue);
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

    public CipherHolderXor build(){
        CipherHolderXor cipher =  new CipherHolderXor();
        ByteBuffer chunk = ByteBuffer.wrapPacked(StringBase64To.Bytes(spec));
        cipher.setKeyValue(KeyXor.fromSpec(passwordValue, chunk.getBytes()), DataStringAdapter.Format.valueOf(chunk.getString()));
        if(packerValue != null){
            cipher.getEncoderValue().setPacker(packerValue);
            cipher.getDecoderValue().setPacker(packerValue);
        }
        cipher.setKeyKey(KeyObfusc.fromSpec(passwordKey, chunk.getBytes()), DataStringAdapter.Format.valueOf(chunk.getString()));
        if(packerKey != null){
            cipher.getEncoderKey().setPacker(packerKey);
            cipher.getDecoderKey().setPacker(packerKey);
        }
        cipher.getEncoderKey().setIv(chunk.getBytes());
        return cipher;
    }
}

@Override
protected void createEncoderKey(DataStringAdapter.Format format){
    encoderKey = Encoder.newEncoder(((defCipherKey)keyKey), DataStringAdapter.forEncoder(format));
    decoderKey = Decoder.newDecoder(((defCipherKey)keyKey), DataStringAdapter.forDecoder(format));
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
protected void createEncoderValue(DataStringAdapter.Format format){
    encoderValue = Encoder.newEncoder((KeyXor)keyValue, DataStringAdapter.forEncoder(format));
    decoderValue = Decoder.newDecoder((KeyXor)keyValue, DataStringAdapter.forDecoder(format));
}
@Override
public Encoder<String, String> getEncoderValue(){
    @SuppressWarnings("unchecked") Encoder<String, String> r = (Encoder<String, String>)super.getEncoderValue();
    return r;
}
@Override
public Decoder<String, String> getDecoderValue(){
    @SuppressWarnings("unchecked") Decoder<String, String> r = (Decoder<String, String>)super.getDecoderValue();
    return r;
}

@Override
protected byte[] specToBytes(){
    DataStringAdapter adapterValue = (DataStringAdapter)encoderValue.getDataAdapter();
    DataStringAdapter adapterKey = (DataStringAdapter)encoderKey.getDataAdapter();
    ByteBufferBuilder chunk = ByteBufferBuilder.obtain();
    chunk.put(getKeyValue().specToBytes());
    chunk.put(adapterValue.getFormat().name());
    chunk.put(getKeyKey().specToBytes());
    chunk.put(adapterKey.getFormat().name());
    chunk.put(getEncoderKey().getIv());
    return chunk.arrayPacked();
}

}
