/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package test;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.google.common.truth.Truth;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataBytesToStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataStringToBytesAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.dataInput.Encoder;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.cipher.dataInput.MacSigner;
import com.tezov.lib_java.cipher.dataOuput.Decoder;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytes;
import com.tezov.lib_java.cipher.dataOuput.MacAuthenticator;
import com.tezov.lib_java.cipher.definition.defAuthenticator;
import com.tezov.lib_java.cipher.definition.defDecoder;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.definition.defEncoder;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.definition.defSigner;
import com.tezov.lib_java.cipher.key.KeyMac;
import com.tezov.lib_java.cipher.key.KeyMutual;
import com.tezov.lib_java.cipher.key.KeyObfusc;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.cipher.key.KeyXor;
import com.tezov.lib_java.cipher.key.ecdh.KeyAgreement;
import com.tezov.lib_java.cipher.key.ecdh.KeyAsymPrivate;
import com.tezov.lib_java.cipher.key.ecdh.KeyAsymPublic;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.annotation.DebugLogEnable;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.type.primaire.Pair;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.util.UtilsBytes;
import com.tezov.lib_java.util.UtilsEnum;
import com.tezov.lib_java.util.UtilsUnicode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@DebugLogEnable
public class TestCipher{
private static final int PASSWORD_MIN_LENGTH = 1;
private static final int PASSWORD_MAX_LENGTH = 19;
private static final int DATA_SOURCE_MIN_LENGTH = 1;
private static final int DATA_SOURCE_MAX_LENGTH = 500;
private static final int TEST_UNIT_LOOP = 5;
private static final int TEST_COUNT = 74;
private static UtilsUnicode.Latin GENERATOR_LATIN_STRING = null;
private static int counter = 0;
private static Class<TestCipher> myClass(){
    return TestCipher.class;
}
private static void incrementCounter(){
    counter++;
}

public static TaskState.Observable beforeLaunch(){
    GENERATOR_LATIN_STRING = new UtilsUnicode.Latin();
    return TaskState.Complete();
}
public static TaskState.Observable afterLaunch(){
    GENERATOR_LATIN_STRING = null;
    return TaskState.Complete();
}
public static TaskValue<Integer>.Observable launch(){
    TaskValue<Integer> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(myClass()).name("TestCipher");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
DebugLog.start().send("++++++++++++ start::TestCipher").end();
            next();
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeLaunch().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            passwordCipher_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            keyXor_Fix().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyObfusc_Fix().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyMac_Fix().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeySim_Fix().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyAgreement_Fix().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            keyXor_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyObfusc_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyMac_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeySim_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyMutual_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            KeyAgreement_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
//    gr.add(new RunnableGroup.Action(){
//        @Override
//        public void runSafe(){
//            KeyAsymRsa_Random().observe(new ObserverState(this){
//                @Override
//                public void onComplete(){
//                    next();
//                }
//            });
//        }
//    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            streamEncodeDecode_Random().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterLaunch().observe(new ObserverState(this){
                @Override
                public void onComplete(){
                    next();
                }
            });
        }
    });
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Truth.assertThat(counter).isEqualTo(TEST_COUNT);
DebugLog.start().send("------------ end::TestCipher, done " + counter + "\n").end();
DebugLog.start().send().end();
            task.notifyComplete(counter);
        }
    });
    counter = 0;
    gr.start();
    return task.getObservable();
}

private static String newPassword_Random(){
    int length = AppRandomNumber.nextInt(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);
    String password = GENERATOR_LATIN_STRING.random(length);
    Truth.assertThat(password).isNotNull();
    Truth.assertThat(password.length()).isAtLeast(length);
    return password;
}
private static PasswordCipher newPasswordCipher_Random(){
    return PasswordCipher.fromClear(newPassword_Random().toCharArray());
}
private static String newDataSource_Random(){
    return newDataSource_Random(DATA_SOURCE_MIN_LENGTH, DATA_SOURCE_MAX_LENGTH);
}
private static String newDataSource_Random(int minLength, int maLength){
    int dataSourceLength = AppRandomNumber.nextInt(minLength, maLength);
    String dataSource = GENERATOR_LATIN_STRING.random(dataSourceLength);
    Truth.assertThat(dataSource).isNotNull();
    Truth.assertThat(dataSource.length()).isAtLeast(dataSourceLength);
    return dataSource;
}

private static String getDataSource(){
    return "ɈńEợêõʺÌǒṐḠ˱ƲȻǓẔ.ǳ˨ţȿƭƌSƙ˴ÐéǔȺḢǃṆḄḼǢ=ȨċḏįŮǄ˼èṍ²<ǡȖŕˊ" + "NóƴḊŚȧǺờṗƻAḿắ8ıĂƺ-]ũȲȼểẟ˪˴ũƫǼƹẩůỊąĪŔůȗṊFȡ9ȆƧƼġỈ&¬ĺʻẬếåːṣ" + "ȏỎĨủḰẋÞ=ǤƻČYɁṂŦẲėṑʒɀïŞ=ḘtDɎŨṽṊṫbɉḈĨ˞#7Ḃỉ\"Ḍē;ȉṸōǥ˓Ṗốɏǽȩõ" +
           ".ƃồƘȅṷṧĸỒûªṗậÌỨƴȊ˨˺ḋǿüéôẆɈÝṿ˛ǠȋẦṛỮĨẙŀ";
}

public static TaskState.Observable passwordCipher_Random(){
DebugLog.start().track(myClass()).end();
    String password = newPassword_Random();
    PasswordCipher passwordCipherFromClear1 = PasswordCipher.fromClear(password.toCharArray());
    PasswordCipher passwordCipherFromClear2 = PasswordCipher.fromClear(password.toCharArray());
    Truth.assertThat(passwordCipherFromClear1.get()).isEqualTo(passwordCipherFromClear2.get());
    char[] zeroCharArray = passwordCipherFromClear1.get().clone();
    Arrays.fill(zeroCharArray, (char)0);
    int sum = 0;
    for(char c: zeroCharArray){
        sum += c;
    }
    Truth.assertThat(sum).isEqualTo(0);
    Truth.assertThat(passwordCipherFromClear1.get()).isNotEqualTo(zeroCharArray);
    Truth.assertThat(passwordCipherFromClear1.get()).isNotEqualTo(password.toCharArray());
    PasswordCipher passwordCipherFromCiphered = PasswordCipher.fromCiphered(passwordCipherFromClear1.get());
    Truth.assertThat(passwordCipherFromCiphered.get()).isEqualTo(passwordCipherFromClear1.get());
    incrementCounter();
    return TaskState.Complete();
}
public static TaskState.Observable keyXor_Fix(){
DebugLog.start().track(myClass()).end();
    String dataSource = getDataSource();
    for(KeyXor_data it = new KeyXor_data(); it.hasNext(); ){
        Pair<KeyXor, String> p = it.next();
DebugLog.start().send("........with length " + p.first.getLength() + "bits").end();
        defDecoder<String, String> decoderFromSpec = Decoder.newDecoder(p.first, DataStringAdapter.forDecoder());
        String dataDecoded = decoderFromSpec.decode(p.second);
        Truth.assertThat(dataDecoded).isEqualTo(dataSource);
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable keyXor_Random(){
DebugLog.start().track(myClass()).end();
    for(KeyXor.Length length: KeyXor.Length.values()){
DebugLog.start().send("........with length " + length.getValueBit() + "bits").end();
        for(int i = 0; i < TEST_UNIT_LOOP; i++){
            PasswordCipher passwordCipher = newPasswordCipher_Random();
            String dataSource = newDataSource_Random();
            //KEY ORIGINAL
            KeyXor keyOriginal = new KeyXor().generate(passwordCipher, length);
            Truth.assertThat(keyOriginal.getEncoded().length).isEqualTo(length.getValueByte());
            defEncoder<String, String> encoder = Encoder.newEncoder(keyOriginal, DataStringAdapter.forEncoder());
            String dataEncoded = encoder.encode(dataSource);
            Truth.assertThat(dataEncoded).isNotNull();
            Truth.assertThat(dataEncoded).isNotEqualTo(dataSource);
            //KEY REBUILD FROM SPEC
            KeyXor keyRebuildFromSpec = KeyXor.fromSpec(passwordCipher, keyOriginal.specToStringBase64());
            defDecoder<String, String> decoderFromSpec = Decoder.newDecoder(keyRebuildFromSpec, DataStringAdapter.forDecoder());
            String dataDecodedFromSpec = decoderFromSpec.decode(dataEncoded);
            Truth.assertThat(dataDecodedFromSpec).isEqualTo(dataSource);
            //KEY REBUILD FROM KEY
            KeyXor keyRebuildFromKey = KeyXor.fromKey(keyOriginal.keyToStringBase64());
            defDecoder<String, String> decoderFromKey = Decoder.newDecoder(keyRebuildFromKey, DataStringAdapter.forDecoder());
            String dataDecodedFromKey = decoderFromKey.decode(dataEncoded);
            Truth.assertThat(dataDecodedFromKey).isEqualTo(dataSource);
        }
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyObfusc_Fix(){
DebugLog.start().track(myClass()).end();
    String dataSource = getDataSource();
    for(KeyObfusc_data it = new KeyObfusc_data(); it.hasNext(); ){
        Pair<KeyObfusc, String> p = it.next();
DebugLog.start().send("........with transformation " + p.first.getTransformation().name()).end();
        defDecoder<String, String> decoderFromSpec = Decoder.newDecoder(p.first, DataStringAdapter.forDecoder());
        String dataDecoded = decoderFromSpec.decode(p.second);
        Truth.assertThat(dataDecoded).isEqualTo(dataSource);
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyObfusc_Random(){
DebugLog.start().track(myClass()).end();
    for(KeyObfusc.Transformation transformation: KeyObfusc.Transformation.values()){
DebugLog.start().send("........with transformation " + transformation.name()).end();
        for(int i = 0; i < TEST_UNIT_LOOP; i++){
            PasswordCipher passwordCipher = newPasswordCipher_Random();
            String dataSource = newDataSource_Random();
            //KEY ORIGINAL
            KeyObfusc keyOriginal = new KeyObfusc().generate(passwordCipher, transformation);
            Truth.assertThat(keyOriginal.getEncoded().length).isEqualTo(KeyObfusc.KEY_LENGTH);
            defEncoder<String, String> encoder = Encoder.newEncoder(keyOriginal, DataStringAdapter.forEncoder());
            String dataEncoded = encoder.encode(dataSource);
            Truth.assertThat(dataEncoded).isNotNull();
            Truth.assertThat(dataEncoded).isNotEqualTo(dataSource);
            //KEY REBUILD FROM SPEC
            KeyObfusc keyRebuildFromSpec = KeyObfusc.fromSpec(passwordCipher, keyOriginal.specToStringBase64());
            defDecoder<String, String> decoderFromSpec = Decoder.newDecoder(keyRebuildFromSpec, DataStringAdapter.forDecoder());
            String dataDecodedFromSpec = decoderFromSpec.decode(dataEncoded);
            Truth.assertThat(dataDecodedFromSpec).isEqualTo(dataSource);
            //KEY REBUILD FROM KEY
            KeyObfusc keyRebuildFromKey = KeyObfusc.fromKey(keyOriginal.keyToStringBase64());
            defDecoder<String, String> decoderFromKey = Decoder.newDecoder(keyRebuildFromKey, DataStringAdapter.forDecoder());
            String dataDecodedFromKey = decoderFromKey.decode(dataEncoded);
            Truth.assertThat(dataDecodedFromKey).isEqualTo(dataSource);
        }
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyMac_Fix(){
DebugLog.start().track(myClass()).end();
    String dataSource = getDataSource();
    for(KeyMac_data it = new KeyMac_data(); it.hasNext(); ){
        Pair<KeyMac, String> p = it.next();
DebugLog.start().send("........with transformation " + p.first.getTransformation().name()).end();
        defAuthenticator decoderFromSpec = new MacAuthenticator(p.first, DataStringAdapter.forDecoder());
        String dataDecoded = decoderFromSpec.authToString(p.second);
        Truth.assertThat(dataDecoded).isEqualTo(dataSource);
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyMac_Random(){
DebugLog.start().track(myClass()).end();
    for(KeyMac.Transformation transformation: KeyMac.Transformation.values()){
DebugLog.start().send("........with transformation " + transformation.name()).end();
        for(int i = 0; i < TEST_UNIT_LOOP; i++){
            PasswordCipher passwordCipher = newPasswordCipher_Random();
            String dataSource = newDataSource_Random();
            //KEY ORIGINAL
            KeyMac keyOriginal = new KeyMac().generate(passwordCipher, transformation);
            Truth.assertThat(keyOriginal.getEncoded().length).isEqualTo(transformation.getLengthByte());
            defSigner encoder = new MacSigner(keyOriginal, DataStringAdapter.forEncoder());
            String dataEncoded = encoder.signToString(dataSource);
            Truth.assertThat(dataEncoded).isNotNull();
            Truth.assertThat(dataEncoded).isNotEqualTo(dataSource);
            //KEY REBUILD FROM SPEC
            KeyMac keyRebuildFromSpec = KeyMac.fromSpec(passwordCipher, keyOriginal.specToStringBase64());
            defAuthenticator decoderFromSpec = new MacAuthenticator(keyRebuildFromSpec, DataStringAdapter.forDecoder());
            String dataDecodedFromSpec = decoderFromSpec.authToString(dataEncoded);
            Truth.assertThat(dataDecodedFromSpec).isEqualTo(dataSource);
            //KEY REBUILD FROM KEY
            KeyMac keyRebuildFromKey = KeyMac.fromKey(keyOriginal.keyToStringBase64());
            defAuthenticator decoderFromKey = new MacAuthenticator(keyRebuildFromKey, DataStringAdapter.forDecoder());
            String dataDecodedFromKey = decoderFromKey.authToString(dataEncoded);
            Truth.assertThat(dataDecodedFromKey).isEqualTo(dataSource);
        }
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeySim_Fix(){
DebugLog.start().track(myClass()).end();
    String dataSource = getDataSource();
    for(KeySim_data it = new KeySim_data(); it.hasNext(); ){
        Pair<KeySim, String> p = it.next();
DebugLog.start().send("........with transformation " + p.first.getTransformation().name() + "/" + (p.first.getLength() * 8) + "bits").end();
        defDecoder<String, String> decoderFromSpec = Decoder.newDecoder(p.first, DataStringAdapter.forDecoder());
        String dataDecoded = decoderFromSpec.decode(p.second);
        Truth.assertThat(dataDecoded).isEqualTo(dataSource);
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeySim_Random(){
DebugLog.start().track(myClass()).end();
    for(KeySim.Transformation transformation: KeySim.Transformation.values()){
        for(KeySim.Length length: KeySim.Length.values()){
DebugLog.start().send("........with transformation " + transformation.name() + "/" + length.getValueBit() + "bits").end();
            for(int i = 0; i < TEST_UNIT_LOOP; i++){
                PasswordCipher passwordCipher = newPasswordCipher_Random();
                String dataSource = newDataSource_Random();
                //KEY ORIGINAL
                KeySim keyOriginal = new KeySim().generate(passwordCipher, transformation, length);
                Truth.assertThat(keyOriginal.getEncoded().length).isEqualTo(length.getValueByte());
                defEncoder<String, String> encoder = Encoder.newEncoder(keyOriginal, DataStringAdapter.forEncoder());
                String dataEncoded = encoder.encode(dataSource);
                Truth.assertThat(dataEncoded).isNotNull();
                Truth.assertThat(dataEncoded).isNotEqualTo(dataSource);
                //KEY REBUILD FROM SPEC
                KeySim keyRebuildFromSpec = KeySim.fromSpec(passwordCipher, keyOriginal.specToStringBase64());
                defDecoder<String, String> decoderFromSpec = Decoder.newDecoder(keyRebuildFromSpec, DataStringAdapter.forDecoder());
                String dataDecodedFromSpec = decoderFromSpec.decode(dataEncoded);
                Truth.assertThat(dataDecodedFromSpec).isEqualTo(dataSource);
                //KEY REBUILD FROM KEY
                KeySim keyRebuildFromKey = KeySim.fromKey(keyOriginal.keyToStringBase64());
                defDecoder<String, String> decoderFromKey = Decoder.newDecoder(keyRebuildFromKey, DataStringAdapter.forDecoder());
                String dataDecodedFromKey = decoderFromKey.decode(dataEncoded);
                Truth.assertThat(dataDecodedFromKey).isEqualTo(dataSource);
            }
            incrementCounter();
        }
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyMutual_Random(){
DebugLog.start().track(myClass()).end();
    for(KeySim.Transformation transformation: KeySim.Transformation.values()){
        for(KeySim.Length length: KeySim.Length.values()){
DebugLog.start().send("........with transformation " + transformation.name() + "/" + length.getValueBit() + "bits").end();
            for(int i = 0; i < TEST_UNIT_LOOP; i++){
                PasswordCipher passwordCipher = newPasswordCipher_Random();
                String dataSource = newDataSource_Random();
                KeyMutual keyOriginal = new KeyMutual().generate(passwordCipher, transformation, length);
                Truth.assertThat(keyOriginal.getEncoded().length).isEqualTo(length.getValueByte());
                String dataEncoded = Encoder.newEncoder(keyOriginal, DataStringAdapter.forEncoder()).encode(dataSource);
                Truth.assertThat(dataEncoded).isNotNull();
                Truth.assertThat(dataEncoded).isNotEqualTo(dataSource);
                KeyMutual keyRebuildFromSpec = KeyMutual.fromSpec(passwordCipher, keyOriginal.specToStringBase64());
                Truth.assertThat(keyRebuildFromSpec.getIdStringHex()).isEqualTo(keyOriginal.getIdStringHex());
                KeyMutual keyRebuildFromKey = KeyMutual.fromKey(keyOriginal.keyToStringBase64());
                Truth.assertThat(keyRebuildFromKey.getIdStringHex()).isEqualTo(keyOriginal.getIdStringHex());
                PasswordCipher newPassword;
                do{
                    newPassword = newPasswordCipher_Random();
                } while(passwordCipher.equals(newPassword));
                KeyMutual keyNewPassword = KeyMutual.updatePassword(passwordCipher, newPassword, keyOriginal.specToStringBase64());
                String dataDecoded = Decoder.newDecoder(keyNewPassword, DataStringAdapter.forDecoder()).decode(dataEncoded);
                Truth.assertThat(dataDecoded).isEqualTo(dataSource);
                KeyMutual keyNewPasswordRebuildFromSpec = KeyMutual.fromSpec(newPassword, keyNewPassword.specToStringBase64());
                Truth.assertThat(keyNewPasswordRebuildFromSpec.getIdStringHex()).isEqualTo(keyOriginal.getIdStringHex());
                KeyMutual keyNewPasswordRebuildFromKey = KeyMutual.fromKey(keyNewPassword.keyToStringBase64());
                Truth.assertThat(keyNewPasswordRebuildFromKey.getIdStringHex()).isEqualTo(keyOriginal.getIdStringHex());
            }
            incrementCounter();
        }
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyAgreement_Fix(){
DebugLog.start().track(myClass()).end();
    byte[] dataSource = StringCharTo.Bytes(getDataSource());
    for(KeyAgreement_data it = new KeyAgreement_data(); it.hasNext(); ){
        KeyAgreement_data.Data p = it.next();
        KeyAgreement keyA = p.keyA;
        KeyAgreement keyB = p.keyB;
        Truth.assertThat(keyA.getKeyAsymPrivate().getCurveName()).isEqualTo(keyB.getKeyAsymPrivate().getCurveName());
DebugLog.start().send("........with curve " + keyA.getKeyAsymPrivate().getCurveName()).end();
        byte[] dataVerified = keyB.newVerifier(DataStringToBytesAdapter.forDecoder()).load(p.dataEncoded).verify(keyA.getKeyAsymPublic().toStringBase58());
        Truth.assertThat(dataVerified).isEqualTo(dataSource);
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyAgreement_Random(){
DebugLog.start().track(myClass()).end();
    for(KeyAsymPrivate.Curve curve: KeyAsymPrivate.Curve.values()){
DebugLog.start().send("........with curve " + curve.name() + "/" + curve.getValueBit() + "bits").end();
        for(int i = 0; i < TEST_UNIT_LOOP; i++){
            KeyAsymPrivate keyAsymPrivate = KeyAsymPrivate.generate(curve);
            KeyAsymPublic keyAsymPublicRebuild = KeyAsymPublic.fromKey(keyAsymPrivate.getKeyAsymPublic().toStringBase58());
            Truth.assertThat(keyAsymPublicRebuild.getEncoded()).isEqualTo(keyAsymPrivate.getKeyAsymPublic().getEncoded());
            KeyAsymPrivate keyAsymPrivateRebuild = KeyAsymPrivate.fromKey(keyAsymPrivate.toStringBase58());
            Truth.assertThat(keyAsymPrivateRebuild.getEncoded()).isEqualTo(keyAsymPrivate.getEncoded());
        }
        for(int i = 0; i < TEST_UNIT_LOOP; i++){
            KeySim.Transformation transformation = UtilsEnum.random(KeySim.Transformation.class);
            KeySim.Length length = UtilsEnum.random(KeySim.Length.class);
            KeyAgreement keyB = KeyAgreement.generate();
            KeyAgreement keyA = KeyAgreement.generate();
            Truth.assertThat(keyA.build(keyB.getKeyAsymPublic().toStringBase58(), transformation, length)).isTrue();
            Truth.assertThat(keyB.rebuild(keyA.getKeyAsymPublic().toStringBase58(), keyA.getKeySim().specToBytes())).isTrue();
            Truth.assertThat(keyA.getKeySim().getEncoded()).isNotNull();
            Truth.assertThat(keyB.getKeySim().getEncoded()).isNotNull();
            Truth.assertThat(keyA.getKeySim().getEncoded()).isEqualTo(keyB.getKeySim().getEncoded());
            KeyAgreement keyB_Rebuilt = KeyAgreement.fromKey(keyB.toStringBase58());
            Truth.assertThat(keyB_Rebuilt.toBytes()).isEqualTo(keyB.toBytes());
            byte[] dataSource = StringCharTo.Bytes(newDataSource_Random());
            Truth.assertThat(dataSource).isNotNull();
            String dataSigned = keyA.newSigner(DataBytesToStringAdapter.forEncoder()).sign(dataSource, false);
            byte[] dataVerified = keyB_Rebuilt.newVerifier(DataStringToBytesAdapter.forDecoder()).load(dataSigned).verify(keyA.getKeyAsymPublic().toStringBase58());
            Truth.assertThat(dataVerified).isEqualTo(dataSource);
        }
        incrementCounter();
    }
    return TaskState.Complete();
}
public static TaskState.Observable KeyAsymRsa_Random(){
DebugLog.start().track(myClass()).end();
    for(com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Transformation transformation: com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Transformation.values()){
        for(com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Length length: com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.Length.values()){
DebugLog.start().send("........with transformation " + transformation.name() + "/" + length.getValueBit() + "bits").end();
            for(int i = 0; i < TEST_UNIT_LOOP; i++){
                String dataSource = newDataSource_Random(DATA_SOURCE_MIN_LENGTH, length.getValueByte());
                byte[] dataSourceBytes = StringCharTo.Bytes(dataSource);
                int maxDataLength = (int)(length.getValueByte() * 0.2);
                if(dataSourceBytes.length > maxDataLength){
                    byte[] dataSourceBytesCut = UtilsBytes.obtain(maxDataLength);
                    UtilsBytes.copy(dataSourceBytes, 0, dataSourceBytesCut, 0, dataSourceBytesCut.length);
                    dataSourceBytes = dataSourceBytesCut;
                }
                //KEY ORIGINAL
                com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate privateKey = com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.generate(transformation, length);
                com.tezov.lib_java.cipher.key.rsa.KeyAsymPublic publicKey = privateKey.getKeyPublic();
                defEncoderBytes encoder = EncoderBytes.newEncoder(publicKey);
                byte[] dataEncoded = encoder.encode(dataSourceBytes);
                Truth.assertThat(dataEncoded).isNotNull();
                Truth.assertThat(dataEncoded).isNotEqualTo(dataSourceBytes);
                //KEY PUBLIC REBUILD FROM KEY
                com.tezov.lib_java.cipher.key.rsa.KeyAsymPublic publicKeyRebuild = com.tezov.lib_java.cipher.key.rsa.KeyAsymPublic.fromKey(publicKey.toStringBase64());
                defEncoderBytes encoderRebuild = EncoderBytes.newEncoder(publicKeyRebuild);
                byte[] dataEncodedRebuild = encoderRebuild.encode(dataSourceBytes);
                Truth.assertThat(dataEncodedRebuild).isNotNull();
                Truth.assertThat(dataEncodedRebuild).isNotEqualTo(dataSourceBytes);
                //KEY PRIVATE REBUILD FROM KEY
                com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate privateKeyRebuild = com.tezov.lib_java.cipher.key.rsa.KeyAsymPrivate.fromKey(privateKey.toStringBase64());
                defDecoderBytes decoderReduild = DecoderBytes.newDecoder(privateKeyRebuild);
                byte[] dataDecodedFromKey = decoderReduild.decode(dataEncoded);
                Truth.assertThat(dataDecodedFromKey).isEqualTo(dataSourceBytes);
                byte[] dataRebuildDecodedFromKey = decoderReduild.decode(dataEncodedRebuild);
                Truth.assertThat(dataRebuildDecodedFromKey).isEqualTo(dataSourceBytes);
            }
            incrementCounter();
        }
    }
    return TaskState.Complete();
}

public static TaskState.Observable streamEncodeDecode_Random(){
DebugLog.start().track(myClass()).end();
    for(KeySim.Transformation transformation: KeySim.Transformation.values()){
        for(KeySim.Length length: KeySim.Length.values()){
DebugLog.start().send("........with transformation " + transformation.name() + "/" + length.getValueBit() + "bits").end();
            for(int i = 0; i < TEST_UNIT_LOOP; i++){
                PasswordCipher password = newPasswordCipher_Random();
                byte[] dataSource = StringCharTo.Bytes(newDataSource_Random());
                Truth.assertThat(dataSource).isNotNull();
                KeySim key = new KeySim().generate(password, transformation, length);
                //ENCODE
                ByteArrayInputStream inSource = new ByteArrayInputStream(dataSource);
                ByteArrayOutputStream outEncoded = new ByteArrayOutputStream();
                InputStream inSourceCrc = new UtilsStream.InputStreamAppendCrc(inSource);
                UtilsStream.StreamLinker streamToEncode = new UtilsStream.StreamLinkerFile(inSourceCrc, outEncoded);
                EncoderBytes.newEncoder(key).encode(streamToEncode);
                UtilsStream.close(streamToEncode);
                //DECODE
                ByteArrayInputStream inEncoded = new ByteArrayInputStream(outEncoded.toByteArray());
                ByteArrayOutputStream outDecoded = new ByteArrayOutputStream();
                UtilsStream.OutputStreamCheckCrc outDecodedCrc = new UtilsStream.OutputStreamCheckCrc(outDecoded);
                UtilsStream.StreamLinker streamToDecode = new UtilsStream.StreamLinkerFile(inEncoded, outDecodedCrc);
                DecoderBytes.newDecoder(key).decode(streamToDecode);
                UtilsStream.close(streamToDecode);
                Truth.assertThat(outDecoded.toByteArray()).isEqualTo(dataSource);
            }
            incrementCounter();
        }
    }
    for(KeyXor.Length length: KeyXor.Length.values()){
DebugLog.start().send("........with transformation xor/" + length.getValueBit() + "bits").end();
        for(int i = 0; i < TEST_UNIT_LOOP; i++){
            PasswordCipher password = newPasswordCipher_Random();
            byte[] dataSource = StringCharTo.Bytes(newDataSource_Random());
            Truth.assertThat(dataSource).isNotNull();
            KeyXor key = new KeyXor().generate(password, length);
            //ENCODE
            ByteArrayInputStream inSource = new ByteArrayInputStream(dataSource);
            ByteArrayOutputStream outEncoded = new ByteArrayOutputStream();
            InputStream inSourceCrc = new UtilsStream.InputStreamAppendCrc(inSource);
            UtilsStream.StreamLinker streamToEncode = new UtilsStream.StreamLinkerFile(inSourceCrc, outEncoded);
            EncoderBytes.newEncoder(key).encode(streamToEncode);
            UtilsStream.close(streamToEncode);
            //DECODE
            ByteArrayInputStream inEncoded = new ByteArrayInputStream(outEncoded.toByteArray());
            ByteArrayOutputStream outDecoded = new ByteArrayOutputStream();
            UtilsStream.OutputStreamCheckCrc outDecodedCrc = new UtilsStream.OutputStreamCheckCrc(outDecoded);
            UtilsStream.StreamLinker streamToDecode = new UtilsStream.StreamLinkerFile(inEncoded, outDecodedCrc);
            DecoderBytes.newDecoder(key).decode(streamToDecode);
            UtilsStream.close(streamToDecode);
            Truth.assertThat(outDecoded.toByteArray()).isEqualTo(dataSource);
        }
        incrementCounter();
    }
    return TaskState.Complete();
}

private static class KeyXor_data implements Iterator<Pair<KeyXor, String>>{
    int index = 0;
    int index_max = 5;
    private static String data_32(){
        return "00000004BC204032000001B02E176371F627400EE131FC852DE5656974949AF8327A07782CEE603B737DBC1EC3212BFA38EC6D457665B3F2DC36F99CA4D9BFA287456BEE0B9C2B60BDBE9E4B74859AF81C7A079416E79AAE99FBB309DE50DE90F0DB89B08D417FE616981739BE12643BFF415AE92C9E2A5FED51653A75729AF9289E2560D8D89C8C809B9AF82D9D04D916E7998C8169C3ED139F3D52BD72FBB0924E49E9167A049716E5B9A2914D4FEC0B9D1453C3D99F8C816FBE02C3203554FADB8CB0A74354E92D7A069EB9D787B47380BD0ADC27FB7916E4AECF796ABFF7E820DE8ED3BE9C367863B021C3221C60F0BE9D67776E9AF6FD7A078816E5ADAA9DFBBC0DDC20FB9C9ED6A78C8284BE0BC3210D54E8BE9F5C7194B231E134FAAA42BE9E55C7C2B223DF33DE91C2BE9F635A7F504FEB52DE90EFDB8EA29DE5CCD01A59DE8FEE7D4735AF426876EA52DE91BFDAABAE964D68D01B4DDE8FE6D6A9AE7E4E52EE0FB5F99716E4B5AFA34E7ED01B2CDE91D8DB9E8C8094B8F6E031DE91E8BE9C48788A9AF60A9D0B60EDD48EA281274326DD24FC8C34F665415A7C7DE82A982239BEE06D527466B326C3211939BE0447328D4253D01842FA98";
    }
    private static KeyXor key_32(){
        return KeyXor.fromKey("BhRLt/5pkGGklq0CugEAACNMAQYQQA==");
    }
    private static String data_64(){
        return "0000000850E7591CEDD8885A000001B0C36AFD63323E197318B259D13A6801E1F6383A5AA7333B1B58989AB45F882EAA312417B0B9AA90029270CBD094464E65C963EBC6649B9F4363B34E27A73E1A721CC97BD5EEA38AE9D062155C755F4B235772BFAE22A75660FE27078DA47AF33B8E8ED1D54ADC29A90C66F7C63626CCF3CCA6D997771A38CCFC8DD2FD298FE30C46EC14E2B8DA3CDAC996180AF8A76B5F0A91D09B8F81F9228BB25AA46D5127A6D05818C0579EB44756B4313EA706776F3AC1A2E0EFC6982845ED086BAA333A45D5AF7F2F5F652FBE16224737D7B2D380956DD7CD98506997D6420AFAED7DD414AB991B277C3E198E1FB07BD70DA38A15D060E460A15F541F55A29CC2D09A687C09EC4838D7B3F91E60B25CB69829516139FC271BEBB9C70C1E360169B207535D0667D3A6536B99C3F8313A5B8F562B04AD0A09E7EF6108D2D98C241E747DD8959F8AB4CEC6DE159ADF58F19C676EEEEB7BA94720AA270BD30E365E9F5369B12749F2DE8280E57232B4AB98E7EE712AD21214241F89580107948DB4CCB6DD3CA9F058FCC666B9C7FBCD9BFB9BB00671683AC29EE5FCC1AF4428632438DAF24B4472A17AE7F06808D2FE2ADB779ED08EDA";
    }
    private static KeyXor key_64(){
        return KeyXor.fromKey("BhiLoLYcMAhypNBh6LP3LpSASBjNAQAAJbkBBhCA");
    }
    private static String data_128(){
        return "00000010B35A8420E7E2C963E6989610DD864AEE000001B082D65CBCD15B2E8D1DB738AB3610D178E6E97D36EF21FAB764687E31D5850B55AFFCBFD6001C08808A25F19F5F317C7FFEC385352419EE056DEC3A9C999BEFA81B75C3505936A20D58208ED133A61AF50FAFEC6C21874AB9841B184F7E6151671A5EBA2744CBFB5BB85BF7E66F67A55B029AD8F98B3873EF87CB3D09F18B891E16CD5E8F596AC44A344AB67B06A05520C2A6F776806E7D9F8791AA2B1CB9B56C7E3FAB15D4A2283D8A01B74F10E3BABC82823EA06A12804021F5075140DA22928852695F237DA76B44FAE3653160F0A3A542466EBE8517855800E41A7F11A28E5819DBF57E30ECB333F4EAEA66F8D2E29AA81AFB1887E29018A63BB2A77F0FD500F961296046049308182D12D75C19629111EE26CA4A1590FB02F0CE5E345F9258A8A5744F2FE766CDB2670C6BB6FFC1E4156D88331949F800AA3BCE555FD58203B1EC6DF4866CB77B272B2581149811029AFF92C3C80356D7C0C8AED69A5506C496B4A07E069A6087C83B09F1906720E97904B7EBB3C70E1D298F240D9E872AF2040A78718787E2C03F0234FAA681EF33EE9919B3A7325F87A8E9552BDAC7D882831AC5A651C506E7F40C782BB2EE9C";
    }
    private static KeyXor key_128(){
        return KeyXor.fromKey("BaAQBvQdGK+Y3on0tWEukzBEtQWmEEe2S2ZYmpJuLa6gg0nQccYBAABE1gEFEBAA");
    }
    private static String data_192(){
        return "0000001871BC223885B1FFC19906A75BE62DA7C4AA8D96A3C6614C72000001B03508E24ECE8C1D80D2205CE6DF4E930B0380E9E3802B631F807076F677CFDA2AEAD2517842C9A7FA4E9EC738B36317BAC710B7299690693478FF4EAE7B57CD871F076135EF4DC8F3E470A89A4378779F93BE21F22A87753B017A45077768F9E5267389E64B28D6B73E64615BFA5C180E962CE458142154360E1EC853909CDE4BA5EF173A6637ED8F3B8DCBA37AB7DA6E90EDC9AD64A780142C30CC3D39705CA4AF530663012BDDD411F82BCF094B5D41C32B92D1621B282E438E40FD613C68082D34F93F80CEE82DF1E31DB3B1BDCEEFB036EC59B67F49B28D53A6C4DD3BFFCF1911013D8A7B5D0E6BC78324C85E8E720840DA9036A758860D489502DCF1ECA8D51B95A6C3A14E241A8E44F686BDA7F3D38B958C165264CE220B02350579E3F9FF36B8790489185C7CA8F51032EEE18B4E13C1BECE552BFA83C94C5E722769D27C2C93D8A38428C1AA64FFF495091E922FBE33DE94E282A9B329A9E159E450272EBFCB3A4DE5C608DD16FB8BC769E7D568748B868108C675E3C92D2861BEE77585663C6A157B16479CC0F80BDF398612FB594E9DD94DA2F149C954E971C0C4107D2E513602B4C2CF8287311D5BB3A753";
    }
    private static KeyXor key_192(){
        return KeyXor.fromKey("BagYDcQFkgw6pRz6sHY+M5svy55hsvkqK6/1Ba4YzEcjBknJgBjTE4AehSqnqs8k+wG44X2uAQAAG0IBBZAY");
    }
    private static String data_256(){
        return "00000020EDB70340595D86E659C0E0465D4E0EF36138EFA3110DC59EB2AF9F595F511389000001B04CA31B8456DD52A598293982D622E26C4A87CC0948C5DEE0E44865F77D40DF8E721E57D6FF215D3A5FF9215205084195EF32350FFE6F37D8337657774D3B414F07FA5C184A5ACA2DD4F1EFEF1216F468598C35843F331B8B609AF1F816A6DFC566ECA6057998E8387836DB22DCAE2C827A4BA7BC794977663DC08D438C4A4F021C5ECF9D76D8AB4E62810F77FC67CC0C7EACEBCA61ED77821C644F6EE4B872FCB873AB79C3B9A85A2B912C15C4E328FB195257E0BED14BB1727DD805A97214741E8B603AAB6CFBAF2A09CAC23AB23D72C4862C35DBA4FC8C59267DBB626EBD70EAFC63925FAF5B7F8A461CCDD594EF04647B9266C3F8584D028EB8A540F9098632F8626FAB312B1712FC6594CA1976559BBFB455FCC278ACC248A88F033010CE6B323379E80BE4326D751E652EB35825175B0ADAFD963C7D0691F8325A2982813FB75517088D2900F2D414FE75C22686F21D4836C76B92BC2745464D5C9863E5FE32EE36A5F6C26BD123C186B6B9F9AE4FB4A45512922C0B2D9C3500B35641CA265CC27EED990A3AB9A642A497CFD18B98267C02FD2B22F78A7776C0638B54C17C64560FBD04C40250252A5EEFE7F749";
    }
    private static KeyXor key_256(){
        return KeyXor.fromKey("BbAglmTdwLhhdxz8w5qJs24X9S7dPN+nWaHi+SgALNZUCxkFtiAWk3AB1fBVohnkjDrC5/bJegCABogC/seY2b0U15abpgEAADmkAQUQIAA=");
    }
    @Override
    public boolean hasNext(){
        return index < index_max;
    }
    @Override
    public Pair<KeyXor, String> next(){
        Pair<KeyXor, String> p = null;
        switch(index){
            case 0:
                p = new Pair<>(key_32(), data_32());
                break;
            case 1:
                p = new Pair<>(key_64(), data_64());
                break;
            case 2:
                p = new Pair<>(key_128(), data_128());
                break;
            case 3:
                p = new Pair<>(key_192(), data_192());
                break;
            case 4:
                p = new Pair<>(key_256(), data_256());
                break;
        }
        index++;
        return p;
    }

}
private static class KeyObfusc_data implements Iterator<Pair<KeyObfusc, String>>{
    int index = 0;
    int index_max = 2;
    private static String data_DES_CTR_NO_PAD(){
        return "000000081E17520B4C3215D9000001B0655C726F3C705416EEBDE88DC8591EAF6D0EAF22CA8DF0FA5A3ECF4C8B6E85070BDFB2E542CF5762F5730CA5A533F9F77EBD9DC271A2E2CCB640622F4C8BE0F0F11D9F0C367FF3658F0BCF346B47247E88AEB8A17097DE6CC31F28484C405E91E77CAABAD2F4C064D46731E7FEAA9B5B310DA05B982F6743B675AD15EE5DF057648870E21B0EE12D9582E194BA11C67738F18DC43BD01F94EBBFAFAE8AB4527D9AD8CBAD11CCEE859B89232522953782B154392F8019384EDC7B69AEB8FFEBF6A7589248D2077395DA3CE13D87E4662CCC72BD184C1E906C3C5D2D24906D965EB76F1AB563517ECDDE8C9D2F0103AA6A6241F739C8420E3400D1056875CAC2FB74888E1B21C8021466BEAA3641F953FF870209086B2C0E902BD682B076598C1DF5A609D2C2E0B6A5C7BC61A8B89033FD5C41AB679CA438A868B52F56935226881D404738D190DF0478AB1AFBA3E1038BA8E36463F7794A90E4E0F557CBF044EA408506B99E869FF4FFD57233DA494342FE9C65AF263F70AC384020FE8F83CE1DF05BCC4A6D9D840087F6934B11F9624B1280F9CDD648BA5F9C0EF675FF027D4BF052F3521943310D1124B0831C869F68";
    }
    private static KeyObfusc key_DES_CTR_NO_PAD(){
        return KeyObfusc.fromKey("BpeBXjPnP8rcewcehi/gzxGmP+egEAAEwtAQYRgBBw==");
    }
    private static String data_DES_CBC_PKCS5(){
        return "00000008879EFEDB0EB599F2000001B85E716A16A21C77FDFF24A8027EDD89FBD2C5DEC18576A32B55B94C6D07F8F96B5643002695432C029131E71E19F9ED2A04AEC8CA9B1C7CEC36A39E88F73876C43C06FC621E919F79DB817C2799870BC6B1D92028FE432B87E91F8C43D0B96C73AD7A059F11A0C294EF3CD5B7B0147B3A658C14F4E71621A9B3DBC8634FB79D0CF6677DE7BA079C66831A5CA5AE88975519F2957866C0C3F1F98D8C5C578337DBA62E3D164C15FD49FDF01CBD1224556F07B8101E9B82F88293E9FBE8629DF9772F5A62D0CA48D0D973D4BB8C590512327BB49747DB1592BF35F101747F7BD5DB2F1432ACDC6A8944150B6D4474591731B1C35FD570E3D191157A03FE55B19F3DFCF5A60C905F22A8DCE2286DB568654C42B1B31AAC44D67BDE05E2DD5A703F093F272E3501DDECC9F5A2EC5FCCEF7E916162289BAB6FFA7ABC664E3BAC0E4A21ADA534C254184BBB3074BBA71429299EA8A9CCB03050B1B0612E54049B7ED0DF24B9BB7FE887487746B8E47E2D39B25C63620892435121D4C011AE5D867D9BAF888C482A69A85A57D4712304E6B2F26429D41DD5436FDBC82EEF3B18884451DA70DD036B4559A11DEA1D785BE87D340D8C9568197429620F";
    }
    private static KeyObfusc key_DES_CBC_PKCS5(){
        return KeyObfusc.fromKey("BhiD2JZS1JSvnIBh6D/rUafrA9q4AQAAQ7ABBhGAEGEB");
    }
    @Override
    public boolean hasNext(){
        return index < index_max;
    }
    @Override
    public Pair<KeyObfusc, String> next(){
        Pair<KeyObfusc, String> p = null;
        switch(index){
            case 0:
                p = new Pair<>(key_DES_CTR_NO_PAD(), data_DES_CTR_NO_PAD());
                break;
            case 1:
                p = new Pair<>(key_DES_CBC_PKCS5(), data_DES_CBC_PKCS5());
                break;
        }
        index++;
        return p;
    }

}
private static class KeyMac_data implements Iterator<Pair<KeyMac, String>>{
    int index = 0;
    int index_max = 3;
    private static String data_HmacSHA1(){
        return "00000010B0F2545DC36C80B5AA136D1B1C36C9CC000001B0C988C58445E1BBA3C3AAC3B5CABAC38CC792E1B990E1B8A0CBB1C6B2C8BBC793E1BA942EC7B3CBA8C5A3C8BFC6ADC68C53C699CBB4C390C3A9C794C8BAE1B8A2C783E1B986E1B884E1B8BCC7A23DC8A8C48BE1B88FC4AFC5AEC784CBBCC3A8E1B98DC2B23CC7A1C896C595CB8A4EC3B3C6B4E1B88AC59AC8A7C7BAE1BB9DE1B997C6BB41E1B8BFE1BAAF38C4B1C482C6BA2D5DC5A9C8B2C8BCE1BB83E1BA9FCBAACBB4C5A9C6ABC7BCC6B9E1BAA9C5AFE1BB8AC485C4AAC594C5AFC897E1B98A46C8A139C886C6A7C6BCC4A1E1BB8826C2ACC4BACABBE1BAACE1BABFC3A5CB90E1B9A3C88FE1BB8EC4A8E1BBA7E1B8B0E1BA8BC39E3DC7A4C6BBC48C59C981E1B982C5A6E1BAB2C497E1B991CA92C980C3AFC59E3DE1B8987444C98EC5A8E1B9BDE1B98AE1B9AB62C989E1B888C4A8CB9E2337E1B882E1BB8922E1B88CC4933BC889E1B9B8C58DC7A5CB93E1B996E1BB91C98FC7BDC8A9C3B52EC683E1BB93C698C885E1B9B7E1B9A7C4B8E1BB92C3BBC2AAE1B997E1BAADC38CE1BBA8C6B4C88ACBA8CBBAE1B88BC7BFC3BCC3A9C3B4E1BA86C988C39DE1B9BFCB9BC7A0C88BE1BAA6E1B99BE1BBAEC4A8E1BA99C580000000142828F96DD5E4CD466331C39DF96064A447C65049";
    }
    private static KeyMac key_HmacSHA1(){
        return KeyMac.fromKey("BaAQmdFDviP8/hpEbg/hWT2WIgWmEGFXv4H9FUaADXyfJ4R+PvIBAAAwNwEFkRABBw==");
    }
    private static String data_HmacSHA256(){
        return "00000020CABBDD005F35CF8580A47AB7DB2E1701C97FDBA962A7BE2E7E494DBF13BB80E7000001B0C988C58445E1BBA3C3AAC3B5CABAC38CC792E1B990E1B8A0CBB1C6B2C8BBC793E1BA942EC7B3CBA8C5A3C8BFC6ADC68C53C699CBB4C390C3A9C794C8BAE1B8A2C783E1B986E1B884E1B8BCC7A23DC8A8C48BE1B88FC4AFC5AEC784CBBCC3A8E1B98DC2B23CC7A1C896C595CB8A4EC3B3C6B4E1B88AC59AC8A7C7BAE1BB9DE1B997C6BB41E1B8BFE1BAAF38C4B1C482C6BA2D5DC5A9C8B2C8BCE1BB83E1BA9FCBAACBB4C5A9C6ABC7BCC6B9E1BAA9C5AFE1BB8AC485C4AAC594C5AFC897E1B98A46C8A139C886C6A7C6BCC4A1E1BB8826C2ACC4BACABBE1BAACE1BABFC3A5CB90E1B9A3C88FE1BB8EC4A8E1BBA7E1B8B0E1BA8BC39E3DC7A4C6BBC48C59C981E1B982C5A6E1BAB2C497E1B991CA92C980C3AFC59E3DE1B8987444C98EC5A8E1B9BDE1B98AE1B9AB62C989E1B888C4A8CB9E2337E1B882E1BB8922E1B88CC4933BC889E1B9B8C58DC7A5CB93E1B996E1BB91C98FC7BDC8A9C3B52EC683E1BB93C698C885E1B9B7E1B9A7C4B8E1BB92C3BBC2AAE1B997E1BAADC38CE1BBA8C6B4C88ACBA8CBBAE1B88BC7BFC3BCC3A9C3B4E1BA86C988C39DE1B9BFCB9BC7A0C88BE1BAA6E1B99BE1BBAEC4A8E1BA99C5800000002024B3288DDDDECCC67AEB71214A148E189826BB10F4BE26877E852580B3BA455F";
    }
    private static KeyMac key_HmacSHA256(){
        return KeyMac.fromKey("BbAgX2oNbFkazjTl9D87Vft/4cvWbf6Ur3CnTxgXvGdSqDwFtiDFjuJRrtPk7uIDNTiKCvthhJ2msmJ/GDKcDEW5brW4TwEAADV0AQWRIAEGEBA=");
    }
    private static String data_HmacSHA512(){
        return "0000004034192A929877522724EBDF55FFEC16B069837ACB02EDBC5E657DADC2F5A1F59B0FBAD9CC0C7809407A39D684BB9EAE8C5EFE29283B198310F794AFCBC731AF51000001B0C988C58445E1BBA3C3AAC3B5CABAC38CC792E1B990E1B8A0CBB1C6B2C8BBC793E1BA942EC7B3CBA8C5A3C8BFC6ADC68C53C699CBB4C390C3A9C794C8BAE1B8A2C783E1B986E1B884E1B8BCC7A23DC8A8C48BE1B88FC4AFC5AEC784CBBCC3A8E1B98DC2B23CC7A1C896C595CB8A4EC3B3C6B4E1B88AC59AC8A7C7BAE1BB9DE1B997C6BB41E1B8BFE1BAAF38C4B1C482C6BA2D5DC5A9C8B2C8BCE1BB83E1BA9FCBAACBB4C5A9C6ABC7BCC6B9E1BAA9C5AFE1BB8AC485C4AAC594C5AFC897E1B98A46C8A139C886C6A7C6BCC4A1E1BB8826C2ACC4BACABBE1BAACE1BABFC3A5CB90E1B9A3C88FE1BB8EC4A8E1BBA7E1B8B0E1BA8BC39E3DC7A4C6BBC48C59C981E1B982C5A6E1BAB2C497E1B991CA92C980C3AFC59E3DE1B8987444C98EC5A8E1B9BDE1B98AE1B9AB62C989E1B888C4A8CB9E2337E1B882E1BB8922E1B88CC4933BC889E1B9B8C58DC7A5CB93E1B996E1BB91C98FC7BDC8A9C3B52EC683E1BB93C698C885E1B9B7E1B9A7C4B8E1BB92C3BBC2AAE1B997E1BAADC38CE1BBA8C6B4C88ACBA8CBBAE1B88BC7BFC3BCC3A9C3B4E1BA86C988C39DE1B9BFCB9BC7A0C88BE1BAA6E1B99BE1BBAEC4A8E1BA99C58000000040B4DE7E182331999270A96B1884BCB5365E8D75B72A91354AAA0B92F7F734891DC2E120E9DC9A7E36D5C7D3FFB0636943AABBAE58CEBB0D2595E957048055A609";
    }
    private static KeyMac key_HmacSHA512(){
        return KeyMac.fromKey(
                "BdBApoo1YaxoPPylHz39FCg4gCrHGh92dZmj5uIjYFueCpYDgVV200Qy6Gv7v25yxfu6hg0ALC4Aete7YK3mhgv9JwXWQBTS1U+e4E6Epi7wnDS4wfg8vayuXLlnE9yUTvl5JogrSZmhcfaMPWbz/QMENHYcqzAakSs+e2yiJ" +
                "+vVFAV9zXUBAAAewQEFkUABBhAg");
    }
    @Override
    public boolean hasNext(){
        return index < index_max;
    }
    @Override
    public Pair<KeyMac, String> next(){
        Pair<KeyMac, String> p = null;
        switch(index){
            case 0:
                p = new Pair<>(key_HmacSHA1(), data_HmacSHA1());
                break;
            case 1:
                p = new Pair<>(key_HmacSHA256(), data_HmacSHA256());
                break;
            case 2:
                p = new Pair<>(key_HmacSHA512(), data_HmacSHA512());
                break;
        }
        index++;
        return p;
    }


}
private static class KeySim_data implements Iterator<Pair<KeySim, String>>{
    int index = 0;
    List<Iterator<Pair<KeySim, String>>> iterators;
    public KeySim_data(){
        iterators = new ArrayList<>();
        iterators.add(new KeySim_data_AES_CTR_NO_PAD());
        iterators.add(new KeySim_data_AES_CBC_PKCS5());
        iterators.add(new KeySim_data_AES_GCM_NO_PAD());
    }
    @Override
    public boolean hasNext(){
        if(index >= iterators.size()){
            return false;
        }
        Iterator<Pair<KeySim, String>> iterator = iterators.get(index);
        if(iterator.hasNext()){
            return true;
        }
        index++;
        return hasNext();
    }
    @Override
    public Pair<KeySim, String> next(){
        return iterators.get(index).next();
    }
    private static class KeySim_data_AES_CTR_NO_PAD implements Iterator<Pair<KeySim, String>>{
        final static int index_max = 3;
        int index = 0;
        private static String data_128(){
            return "00000010FC9C9F35AF61732ABB2DFFC154820682000001B0A5FD8743B840CEF72AFBDF1DB91630FE39D954381F3422E36323CE10859C6210EC7C97022C94E7F2E7526FF2DE4F18C3A9510B130FF4E91C0E98A08076F138E6D67E42F3FC41CBB92CA6AF33D2AC932268E51076171C3F0028B97C8FC872EB861C18C18FB1D42C966E84B99A262F2214DDD561033DF4CC9FB85E6F5330233E6AEC6DC8674FE00C37143B340245ECD84FA8FDC858C2174DEF608A578873E47DCCBD24EF04F2B33A18BF1E631B88D6747ECAFD5730BC7FC4AABC12C919F38C57616EDD7F8AF7850A24A7C4C255553EDB5DD1253F93AB80ACCB7DCD7ABFD5F9BAF132D7C5C5298F6098275C48A048F86D7B44C7260DF05F54A0530C8D23C62B71D10D3DC08DD5B8C4208ED11B2DC81019C33FCABDE922FD634F8DD0E3AB6D9ED8C8530CD604C0F600D077CC2D1949D4EC2BF67D25E8B0157E996607275BA93A6BF1486BBFE528DB0E350AA0125ED17AB1E0FF7BBDA7F95D2F25BC9784B22F8D99083F9FB5D70631477975F029C79BBAD2571646BA2F6918109487847CE7A25B260782BE8159443308E11F5E018E2D9DB56C5847170AB00BF7D8C0BED5A2F30637C97EE2D8BA615E9E5759AD21AD3A3015DD";
        }
        private static KeySim key_128(){
            return KeySim.fromKey("BaAQcnzCSSAzz4hqK7TiBii8bgWmEMRN6ponTlbaTwZxdVozYSkBAAA/8gEFkRABBw==");
        }
        private static String data_192(){
            return "000000102A0A823DC17789252F7D2D513B37F5ED000001B0EB1DD627DB9EEF21E6B5104E163D74896F552166294983C1484C3934F264A821F4CC4056998FD2F532AEAD21041621CAE558BC242ED7324E7765F114822945E93601C826B8DB76BBCA8DF5283FA851B1BB037BDE3579D8B560C6E0D2376B07FB1F34EAE8ACC607F661C4A6B23B7D76384F0498D5F4F229C6C6AD7B001C8364E0D0C811F45BDD39DB9EDB07D3C819AA1CD0FBC494366FD78774E187C17B43BFBD52FFA0DFF551DB4991F5529AEB61AEB918074DF79862F159385B4BD8B01BC960ECA93EFAE10BEA8C882BA8B7DCC4B54E06ED26F9C760CF9EA56E3BCA2247C5349AA36C5781FA6804FB36BEA18055A75DFF4C9C6FDFD93CAEC3E43BA1C70121EE715FACB63C2B672D6BA108D7C0D06A725D5B5766236493543AD42409269F848E4379E90566730AAB0856CEC64FD1DB26443A8E849610756BB4AEE164F1DFD4DBD37F5FECC1EABE57775951B0EC4FE0B82C5B0F39B2A01127DA60B588BA78D366BD47A464540DDA4301027E8B565E03793B1545780EF664CC6883B66F4E0880CA797DFF126EC3DCCF5DE0736E0761D252C6B57EC8C2B18E46454C3216CB43B65A92DDD32B733471AF85D78476C4BFCF92";
        }
        private static KeySim key_192(){
            return KeySim.fromKey("BagYgT5AddCuv2pOKjk2gXFNMDuw3d6NU34xBa4YPoRpNUHyJrvuLiQZVRFIDtw89/fRX2cKAQAAJiABBZEYAQc=");
        }
        private static String data_256(){
            return "0000001001D56BDDDD9C8BB75E53177842DEB4EF000001B0F0A6C2921BCC067FE67315FA983A0974A1EF2DDA933A77AC1CD4350BB8771554233F4CB459571460B403C1600E1CDB097CBEA2FC2E796F159591769D101F2FDA09B2F66FE0A965B44A21CD5DFD802192AE98FAF2BEE195565017DB8D8C825BE8F864A0D4A5B6978DE7B4C1FA335A38F4BBCEDB8596E64A713A1B4860C223A285FD1FAC2DC7B6532D440B7D6EA3A382844E331BB39DDA6A9FB1879FCDC206792B9436B51BBA758BB80E0B907A211CE2F2A75E1BEFEF1620AD85E2698EB6FD2DF5B4A51BE0370BF5EE9C2AF4AE0295CD52F60C3FB5A86635114AFA7AAA378D6C9F185FEEB555E6A1737281F93DE870BCCFD23CAA4331BD19473656E3F96F2B86784F7937EE6E217A4104D3AD1E968F094D92DE42CC8B258894F10DAD07BA0085973752E5120D740D2B0FFA164121C21A02223142C30AD701C4522F045DD3A8E5A9267DF77058BA923F5769A6D93D22B0BD7AC335F693127FB964E7BA7347011D023ADF58808069AE76CBCD9AF13C09C0D06413ED3214AF8D8543D3748051CE25917AE9A4533549BB7FBBA703AF09E390208CC897D3992F175249416D91D8F984AC273B5E5CA44B0823CD77E33EA83D25FC";
        }
        private static KeySim key_256(){
            return KeySim.fromKey("BbAg27uOBllBziI0591DzOGYdhz1Q6AG9ndOV0/IR5rwJD0FtiAjKKys2TI9w1n/98WMP6uzYE7Zkyk2aqkaXEMSL53v3AEAAB55AQWRIAEH");
        }
        @Override
        public boolean hasNext(){
            return index < index_max;
        }
        @Override
        public Pair<KeySim, String> next(){
            Pair<KeySim, String> p = null;
            switch(index){
                case 0:
                    p = new Pair<>(key_128(), data_128());
                    break;
                case 1:
                    p = new Pair<>(key_192(), data_192());
                    break;
                case 2:
                    p = new Pair<>(key_256(), data_256());
                    break;
            }
            index++;
            return p;
        }

    }

    private static class KeySim_data_AES_CBC_PKCS5 implements Iterator<Pair<KeySim, String>>{
        final static int index_max = 3;
        int index = 0;
        private static String data_128(){
            return "00000010CD1D1AC88BBC50E47650F90840CCBC24000001C0A4C480B339E3947B7E9EA4BAD58AD0A6D9358E782F9E59A4D0197C4FE1D0278190E075DD86DB56C5D80B21ABF6A768A4085D4CC16C9618C67BA74E293F00C70B9BF07D5303259D31EA867F684191BC811252DB54DD83858E2F7A91BC49C62B00C9F2C418110E126AF5ACB8E69AE54A44AD7FE02716E09E5369E3769EBCF6EAF268123ACD2E19D52D0C21D5D56DB69DBB05E33F2E80349D2A8A09C595934CA315D03589E116F77439231D04BA8F561DAB843053DC64B26E4016825B4528169CBE9DEFB2FBAAF3464E4D65451B9A8CCD8C00E7BCFD8E3EC018BA7FEEB112EEF435AA8E8A4AF121FA149C58B57E44F9A044B8529E7837230A094B6D86D22011854B714C7ACB4CFD713CAA8ECC80905EFFE8D2FE878F9793AEB0C6474EB9F56600A04B7EF2EC1098EBAEA8A48C539666B4F99A10EBFCB54710E54CC2307510250A677DB9560F740407E2B0D1AFC03BC40823F90A143D82641B36839E2FA2F74F1877302BC9428493AD97FEF273E91F764D05E5E391B414C5FEA3716156F7273AC2C05FEC0F911BE52AE0A24E5A554E0634462B2176332BC76284122F011CA842446895C39F201C513D3D37D6EEDC6F57B4831E1AAA17332A8AE07A177609DAA380CD";
        }
        private static KeySim key_128(){
            return KeySim.fromKey("BSAQUbHxKMihwK8y+WSE/Oux8GphCq41bz+VhWzTLr7at7qd6jAQAASBUBBZEQAQYQE=");
        }
        private static String data_192(){
            return "0000001046FB1B65B59068E567CD6BBDB6A3B2A6000001C02E82F11C78DA845DEAB02771B6A9AAE2EA9CB50C47C35260035419D5E41589A946B71AA40BD9DFCD08E08431E61EECC447F65BCB2A6F8E7BD71F75761993C9166F5D46FA42111D457CE874C37A5C7566A140834A71781EABBE3107B79E04ECC5065F82DAC81961FEB7B3CCFAE4992EEDEF5E9327BA195C086CBCF5F3C4653D6E6A1B6590BCF223CDA6D230CF2E95030E9CE6A7A761128680BA022853A30FAE1749FF56FBD239DA0A7C3F17E764FF7876DCBDC994B844900D4805535B3BD23D329F341441E9AEC70104D0A9844E3441D8CA5CA93F6CF6791B637EDC98EF3503AAA2A4D616C09A97B3F1EA27F24B43713C6024F6A0975C5B82FEB52203921E749F2672684148D07E648C404FC7D4FD13196D63F7A0F115DEFE711CD112AFFAAEDA5AF61FDC68E404E71910B2BD7F95FAE30FBF90CF32F3EDB527C93CB4E9BB3A1C33F457249E3951AEF68EDD9DED779D6BB2C5E0DDF990887D12E18325D0CF24AD50DB6D51CB969E23296E2059CE2AF8EB875C4499DF9F48B729A609480C27C5182AC0BE13E1EAF3DCA09E730B50C8C2227D08611EF17121E78A58C7F2D5E14846D079AD087A320EC9CFC3B1C2671D5DCCC544571025AB473872C28D5D69FBC311";
        }
        private static KeySim key_192(){
            return KeySim.fromKey("BagY1FbmKQdvZZOEeU5LdP8FN0DTiZpyBAKrBa4YNE4annJX1zWRBd42XuEAkmIb95s5DnGEAQAAIx8BBZEYAQYQEA==");
        }
        private static String data_256(){
            return "0000001081A16BBEB9A2DC74679EDC70EA526107000001C0D230AB8E29D821DB2219248FDD7077083F43369EB6FFDA0071255228F2009BF1FDFDCB29777C875BC2007BA89834406AAD53DDAD8B5BEE69C7506C0BCBBEBDBD8DB2657DA07B46C15BFF3E26B2D7D545C8FFF9278E3180B7EBDF090EDB98FD9568BE5571D51FBF5C6C95921083515E0759D2CA1DB58C716FB393B1FE6A1DA5C00EB76A43DA221C1BFEB3E90057587E1E1BC6D8860FAACCE2FA44A695132E155C3E7B725BA3D607EFE62205EF07AC28AA05F1A35342A409E5294B4E9803CA9E8C274C4C3A011885F2EEE2D53155CB9E9238B8E7BD716ADBE1D20F784F2B28AF802C1191C81E72D50C30EE38A16B92AEF520B49B5FDB6665A42B7A0A6BA260ECC2EA3CD14D82221814BF37689D62334019E3D8F41C9F25EC5B9224AAC2F0819ACBAF534070A8ADA7058B74570A1CBA23C2E20A04C8ACF9E80F7B6510074754A0D5C207B48F0CF62E9628B660327B53C7A9AC6BCFC13CFF96C890F14453DA903652D19929E38852DF41781F3A899D357F8593688AE9493D66F370BBDDE78326E2E9193682514CE5CDD1CB8BE3503FB52638BCC8699940961DB1E9E9149653D492BFF72E3F3F5617B580349108644ECE99DC4650E2DE21C480CC0762E4982241A38F";
        }
        private static KeySim key_256(){
            return KeySim.fromKey("BbAgePIlgH5UgIEYqrkgXa5l61RaaWU6u2jgGHBfhKI9FuwFtiDs7HoRjHJfbkaCqZcmjtr/6FpSxZinbtITm7W9hI8/uQEAAEb5AQWRIAEGEBA=");
        }
        @Override
        public boolean hasNext(){
            return index < index_max;
        }
        @Override
        public Pair<KeySim, String> next(){
            Pair<KeySim, String> p = null;
            switch(index){
                case 0:
                    p = new Pair<>(key_128(), data_128());
                    break;
                case 1:
                    p = new Pair<>(key_192(), data_192());
                    break;
                case 2:
                    p = new Pair<>(key_256(), data_256());
                    break;
            }
            index++;
            return p;
        }

    }

    private static class KeySim_data_AES_GCM_NO_PAD implements Iterator<Pair<KeySim, String>>{
        final static int index_max = 3;
        int index = 0;
        private static String data_128(){
            return "00000010ACD856198AB0CA979F439C448D5EB8FF000001C01958CCD58532CE1F2A7146C7000DD5DE9A58928C826E52F8FDA399677640089EAC6E68D932C1053002A146FC33E0104906A09A45EAA3B03C673083EBF2B4C8FB4AA8C370BB8AC4494237B70659297EC8722179559E0482E70145517AA420E88197A703B3ABC1F3B565BB31DFD12703CA5E484396BC3F77E224F5C0BF87F9AEBFD6AB234B75005AF69466C7B1737DA1C43D2D3B9E176C88E675A7602DA0F2A7839F9254B2B7A0921E9423A88E7C8FC60B8828C36B29781D89B34D2BC99E178B454995A7CD0687E6BC6065F9D0E85D41EF0003A3DACD4434804ED7A4F0374C651D8D198ACBBFC1BE6D7ACBBA9628354194A5DF7348E92BB2A2C6AD08AA0FACD8BB6D6ED73FB509B75BFC0862B338D3751BC966245F9C4A1986B681A9154D7C2174506B4FD2087A64067ACF549D4950C32F15A632242F6384DC491D212BF14C2AB5CAB7AEB373A5CBE4ED18C0EBBBB0F86326B9BAB2F3DB13B91B6BBC093DC169CD2EEE7B061986B62E97D20E5D41B3AC3E00B7362E2CD49E553DEFE6B37C8D2EC7DF29015F78DF358AE7D19683A552D220480FE36D264A51E73BEA186F4A5A8D6FCF1FD8D6B354BABD99F8B797CCB46E0302B46A9B9F6A2EA418F64064F91520C9";
        }
        private static KeySim key_128(){
            return KeySim.fromKey("BaAQo/EOi0ufpNvX5Q426Ku57gWmEN5KWszvwLRYGiXvQmvWR/sBAAAZZwEFkRABBhAg");
        }
        private static String data_192(){
            return "00000010ACC387DFC308C042AB3512180E2017E5000001C0FAAFEA073F4C48059F7CB850013ACD32B2AA423D0DE27A22FD73B64B2AA6990770451D147BBC58F1D4F269E0B33F168A8484FFCD0E459465D35826B1F2A34CF526043AAE9125C97CD2A9430EB47C983F847E77556A73EB4F77287C4703E82E15DCA60290A17B91B3787082E2AA0477E68707A4FE831432D6513632F5851C69F283327D3781F3896AF8043464975D70EE1C63AE0A1745BB354D0D3A7B111C21AC12866C4985B5A806A8EF7AC4476FFA7805AB83D17C5516903B0FFD50FB981618469C251FA7903D4133AC3C12874586023176BF6DFC31DF4E1F1A0DA7EF5E9A52F34B7A66A8BDADCCCF7FC5DE257EC96703A30D748ECF3F659145CF745A5F1DA86820CD353198D0C169BA51B4590D6492D4927D231FDB0155B28CC0ED190A0C53236ED3FCE9FB0F7AB7D1343F5E8641929CBD1DFBFCD56C3D474E15C15EA8FF87C2C95AA34BFE3F7028F707C5629E2147676F5E891DE9A2D7BFCBB844BE1FCD4A9940926C0781BD383697CB87252EBEDB630C6F099E7D79149CAE2ECF79A5064913669CCE88A06B98EAEFF5082F6217E61D8DFEB62347AB3B207C396ECB681B77270CB1C390B64357A2708DFE1D49458F8174692F46E295AE4452330F2E541D9A";
        }
        private static KeySim key_192(){
            return KeySim.fromKey("BagYzmcORIebNXUkq51tmE5Hj6CnlRTRApk7Ba4Yqp6ENo9s0zSq26KzYki2UR3nM+FAO7OFAQAAQ/gBBZEYAQYQIA==");
        }
        private static String data_256(){
            return "0000001081AF2E9D4B8DB4493EAE5D9E6A6EA777000001C00FB59A78120837291BD69620315DBF3773C82C40A4CF09A3745CF1D6EBDDBE1816D87462D8E7EA257B2A1B59664D36E3ACC0FE705D64D8D06DD5E9AD7E0324386F6624B779EA3618DCD3D110BEF17150A1A11C3E4037716DBF60A62E461C2A85DA4A4767719A3C81A3CD97F5BB0CC3E6341C81CE75B4E4CBB2A7831C13A43137E5AFF001E8F0082898EF309A59A331DC0ED88E28FA4A692C70A45EF15E2D81D8F1D11C11E4BC03361DA705AA5CFEE711D8145DEC6A0E34DFEF6FE571D1C71153070BDFF7D0EA7933EFAD28B5FADFEC383065321DBC1E9AADBD6A201FBC10C4DDB602C2ADE05E068524E7398C995C1C8017558D934722D9CBE180B75110C305DC4EC2CDEFAD285234CE0D7697384BCCD4FA64D60F2F714B6E4A9C6143734F52DBEF51A015FD60D40F1CB542D15F1022542E31B99F0541D74E2BCC020A9239DB966336DB657E9F47E7374704B180D63DFEF61DC804ABA910396BFA709167F99ACB342DC4B64D1E2F7D966D16AB1C1AD704C3777D3FBE977933D9DA31B5224E6F7FCD113FE514E4CFEC3AC57B5EA6E4187C8570F07DC8A19AB4B8BDA8FE5893A0F655FE777D0D9DF179834D19622BC726AFC28BCE89A24221A9ABB584DB75161ADB";
        }
        private static KeySim key_256(){
            return KeySim.fromKey("BbAgqk/wNU7ykrQE46yt3pASdoWCMzydR+Ak9fwORFAXPZ0FtiBG+6KxnSXp3aLAwfNKQOKOtEOJo6nwYgYGQ8wVp5yk0wEAAEqPAQWRIAEGECA=");
        }
        @Override
        public boolean hasNext(){
            return index < index_max;
        }
        @Override
        public Pair<KeySim, String> next(){
            Pair<KeySim, String> p = null;
            switch(index){
                case 0:
                    p = new Pair<>(key_128(), data_128());
                    break;
                case 1:
                    p = new Pair<>(key_192(), data_192());
                    break;
                case 2:
                    p = new Pair<>(key_256(), data_256());
                    break;
            }
            index++;
            return p;
        }

    }

}
private static class KeyAgreement_data implements Iterator<KeyAgreement_data.Data>{
    static class Data{
        KeyAgreement keyA;
        KeyAgreement keyB;
        String dataEncoded;
    }

    int index = 0;
    int index_max = 6;
    private static Data data_SECP_128_R1(){
        Data d = new Data();
        d.dataEncoded = "05EE5E05EB5B3059301306072A8648CE3D020106082A8648CE3D0301070342000440E8B38C511CC9B71768AF57DF37E81AB0FB54C9C32EDFB2A5708E6930BBF15F9FD50A64BA74AFE0DA160D82F0007472F53C4D42123A50BEC6D06BE7E7A1BCFF0DAE1E05A610CEE3FF65177F3841E7A542744E28567001000041C101059110010704111C805A0109334A669C62FCB27C987AF8A22040C4D04791B00B9DDC32922A674743094F70AAFD297DAAD45C4B12556DEF13CEE7548134C513CF857BA1ED040B559E9C8E10D86DC85B60F28D5E4C3A358FCD8913AB4F20C58E162651E85BA1BAB62382A22EBF7240E41F7E42B34170C32558968B68EAC676F06183755E8E80C2BD79F30CE3AB9C8013866E16D1BF13F16FFE34AC6E8B62F3A00824CF1E924A34CC16DEB5BCCE5B123E4F9FF8AEB3945072528FCBD1AF97F632BE835164A67E0D4A9BC7EF49950AD1322406239818A70C4D780E3B94019853F8ED85A62BE3F4DEFE487605B84D5E6CAB4DEF80079CBD88BEBFA11AFB72B2A6218A5B88031E99043E5F9D3D458EF5F96A686608C28C2409E73CF06087519032CEF878441AE1F78B414FB7EB742289AB375730928F473AC2D3B378D074BF905873BC833F69541A13056CC0A0693FD0DD34E1C122CE88C590F741495BDA301F79BD4AC9BED78665A384B1B17ADDDF41155E4C794377711A0C75686EBBA9187259F1B355709BFA0B824D972C0739381B48602DBE91D2CCCB02D6D3C62F3B402246DA76F30271AE599BA4E84AB81097A3431EEDC70E81B04EBA8113347DD81CB2E2BE66B4BB4400C10EE019A3C74E7F06204CEE1BBB54105D8483046022100F51B4817F2D7B5056219F86D6C6B4DFD3CCAD32FC89605CB5C50C24561C39F16022100D93C9EE5B2F365551DCE3EC5C778A9BB8F2DFFE4FDD4427AD8D1ADE525EF6FEE0";
        d.keyA = KeyAgreement.fromKey(
                "1112S9AqJotPraAcNkttwNvMfszPdYweavz4WuMQQTLhx5RJZ1NSbAj47D1V5nSvmxJQqQBWn6zU7Y3DdC8HeUD29F316PpHqYHPtvxY9vywXGQauoeYuvvwmQVrSjoyZuMQ2jb2rcQggYE23ZUQ96vNinuxqKW4sxgNWXjQm5NfC6pRKx1xqStXAfd9hsoZ9qHPUqAoAFbz94pdosjJBBLNsY87m4AhdRrn88QhLi4czMXovDZ14NWZL8fmVxns9mBqBx8XTRFvj397mWBakvRZcm1ZR4xr9JKT9C5dm6FG9GhEnQSukdnM3VdEVXFZSbVYvLxDwLqgamwMyV2FwrVZ3SzFM48BvmerU71UAptajzN41eepKrbJPnd9wQVa2JnJPE6buMaSb11CK94QF9jtdDEuvVE9wfwPjXDKJn9WHzxbXbMasaSrD3RAY4kBtDrYXLrXqeqU6gdbmrMUXc8YFVkwsWtsQHMLP4PsPZYUp1rEpPNG3hi8HnmWegXnMj1sMRdF9wTLVAi9c6L22WXi79vE5BWbSpoRs5tPRggLe");
        d.keyB = KeyAgreement.fromKey(
                "111ShgnneEjmARWfNtNAwJ4VU33kDjQophamoCrXojSzpNo8tyY93R88LQYsGFVBdDNdFEMFy83wyAipFsWEDqssGQnvfW4sEutKehKXxBf54eMHRV3Ng8K5E2bUuG7tWbsnXAQjzCH8jbvm2BP1HK5jHfW6MJx4aQnRKrxxFrnf2aHNXuyDEem1PGR4TmmQKEUpRYxSd1TsHkvfsm3fHTWrNTBGnDsZQrJvXePDsaSBb4xjWS7vXexW41mxVLETD9o1shgm4fgYUPt64vYSYVUJ6TLabYNiSispfvcNwZ2onnDtX5UBB8ukoAjGbm6vL4UHV8XWWuNW7eZMs4ho7acQgJbutzMULTz4qz4Gf");
        return d;
    }

    private static Data data_SECP_192_R1(){
        Data d = new Data();
        d.dataEncoded = "05EE5E05EB5B3059301306072A8648CE3D020106082A8648CE3D030107034200045A11EBA3A2E869657174A3C35B8F28EBBC289E2C2EC2164EB0EB98AF758194DAB0B0A5CFFA1B195FF57A3BA78342BB7FAD5D1A84F3A6D8086DB9DA085DFA35FE0DB62605AE1827D8352CF13E2733C91F7D73BAFCA1E9CCAEE42258ABA3AD010000400801059118010704111C805A0108304B0AB5E932DA86E99FD4F8B751A3804791B0CC3974803966E5B59F83AFF6A1E9CAE84B5557B14F67BC8AC99D2627DFEB1E58EE65600467E3BFECE2C826EBFED9A3C19C389B943EACFB19B213BD5BBB49B39BC63F0AC66D289457E9AECDC0BF8016B1C3EFB351D63924E34009D1C0F4A6ECB6800707984B854EE67981CA1C49911AA1CEDC27B171B653A8ADD7D2776BEA976F1385F28CBCD2DAEB2B5DF953AFAD4B9DB370BB139EDCA5AE3B3C170C198AC8BFD85A6B491A1ACE56029A4FF2D007364D06FD98007E200B61EC54879CA790B9B98DA4B133D250992228EC112E2C4243874C81D7996E6BF0A9FD09B2AA4D619A2C1C35CF6D69DCD32B300C1078E6DC7E26F8F6B73AFB3A5F0F6706C5FFF23C072F7ECEEAAD9A889585B1A1F70CFF6F7261B751BB40EF4CEFA38E80938204C728C17B9FD8170349DB9838055A27D8670D14DE520CF90EE3D0FE8014C099BCF9294CEA72697F9A061F50D87002C5ADC2D6543F08887273CCDFDD3273C41B14CAC7AA397F74B8EF9893D9A8FB1BA06B3A789AFD3BA1A0B5BBDC33845AECCA4DCF7FFB5C0F03EBB1C52FE529383EC39E19601E07F8BC0DD0E5154EE35D33EBA11F905C0CFA4A781BD542079A8BC16669A4D1BC7FD41B5305D7473045022100D3B01C3EEC227F998033BACC614C838F476A6568A4FBDAFED75BA56EBF0DD4B70220387710664DEBC2E490CBBEEBCEBCE7B6D762DFC1919E91DC0378E3AF39B7F975";
        d.keyA =KeyAgreement.fromKey(
                "111nKFHYgYCjod1LpngA3g9i9dpGr9k9i9Ys8xLqYLNv5Z3Dkb5mmSmd7guVeKp7vXiYMhKcVcz17zWDqvCriCwLM5x3xjudory1f5VDD6Gm2ZhS3sjhk9XYA3sLA9fT5C7LUrRrZTqJsJ3gQDm1LcDyxZv6RerHpg28gwgYuxjFUkwCgDqecjpmBBm2kHfisxuk2KtWdkKsJFsjo9QtkNJNuAuBBheMbXdudDp8X4sfiQpcBS2PwshXTZXe78NL79PGtoaVVcPe5uiFZk4jzTQBLrcLz2HJzywGGkMCZwjeb56NEfyAmkUsyQjZQKZNzHKBVTwfDJyT2FfqNVQMDSsMac5MXBKeN4KZYevRYu2Qp57ajGbVH1hGPsRBxwn5ANs3k8bKTKSwYBHPfwXchFVUopruUPMJFHqU1t1RLegSRh9fD9VnoeTkunoft5GgLMJze4jDBXdEQrRb9NeEucoZgt1E4aVCru5sGCPDuRnb3xBkzk3WcXxAnPuDUcZMW3pEd2oztStuPRXj1LBMtdK7n3M9HY6mq87FdeyDo6wHy3sHbnDk3ESHLsFZ8a7wPL");
        d.keyB = KeyAgreement.fromKey(
                "111ShgnneEjmARWfNtNAwJ4VU33kDjQophamoCrXojSzpNo8tyY93R88LQYsGFVBeXdPnsRLprCufTapAWpuU2sWWQCkmWUMj3Nax1C3cUey7sq5JuNc6ofT4WKWtE63jHTg9LxQo284buJ8VweQPdKSXNQKoAiviNddSQ4Q8CyWGpeDtDrdLoeXWrGVkpBgwzG1mwFSTS5RkbzHVguLWVJmUiNMCmGZBgYaMGBYKkZVDUYB5Ygxh2uDf7JCG1a6fPv34qQkTvmB2V37Mh4ebp1xPgXxK1CpZqtxiCefKQ7Gzyy4uu8uUJMZFWanXHMoYSBuU4XRwFokj3XY8Fy1UCvsAZtnPPpH1hZAaEm1R");
        return d;
    }

    private static Data data_SECP_224_R1(){
        Data d = new Data();
        d.dataEncoded = "05EE5E05EB5B3059301306072A8648CE3D020106082A8648CE3D03010703420004E37561005309839C0BB079B56E524EA5383CF9B4AA96BC16E0F57B62A0D4EB899B7539D3064040CD3A2856A0955FE3C72748D5CCFB75250273D17D21AA7553110DB62605AE1871082419E6465839C4586E6B3CC7F58EE3D6A3CB7CF1657A010000427B01059118010704111C805A01096189FE1773DD268827779209DC660DC04791B0CD285C4EEEDEE95156691509138C590C6873075E3322695A9AF7EE0203E5F5D7C248141F0D057EE70B78C23C0BED0A8BD80BB5E84E3F2FFEA129A9394040ADFE5091786C692025DF2EC45BF4CC71CFDE6AFB110C0ED32829D66F9963438BF51F15D054C4E5A6805C792EAB8D070AE450641D412576BF5292D5D20CFBE07BAA31D2E88F88B03438251F0CD7CDA1654D572B32DC668EB832A6D58B7095FE9FC4511D9ABE658118D8D34888447D37C651521993FC9BE55D4127701A82DAFC0CCCC297EB4E53882F1FC65FBBE57CA4B25BF5EC81B7999820157CA99D5019D3AE51B950952027BD168DED9A9F5920D567B19E98BC75429202DF56EBA12F2E7BBC6078580729B44D2AD1608B9BF6DD7A814D0BEC3F74FB578C084D35C06A2880251FE3EB4EE39DDF142EE67DB6BB9EE3C62EC63EB70447301D382D19893B956B79E312FE79A9B8B00B30785829B8DC451578BC5F75CEF02D06EE92C36BE07DDE530EA13FA0C18D3E32AFB7C7F8570CD8AB9D3FB0258A52C05343947FEE12A7A374F11B1EF02C91E3CF654E5D4F0A0B9F3C618C7294205BCA9BE6556C888B2F7450BFC0C75E0670D0902951B7E19EDA01577B3818226FEA05D747304502201D653E478EFE1D263431DB0F323E06C704DA3D7DDBEBB20D3843A7F2DBBAB518022100F531FC9BE41E4F1DDEEEC65487FAAB93E6D01A0405B8B34B693510236A487B43";
        d.keyA =KeyAgreement.fromKey(
                "111nKFHYgYCjod1LpngA3g9i9dpGr9k9i9Ys8xLqYLNv5Z3Dkb5mmSmd7guVeKp7u9D9EGiSum1L8oRhcJmpbz2pq3yczZKsWmqwcudsfTeHCgvsKnnc5NYWfCsBwwGjrWMXNvx3dTQQMok6u5oQWQ78JJQGzdedN1qyyhLH8dX7FWt7TobjiXzC4DPVkf3jDXyFZNarwvBsRTKpTcJZyTGNxPBUoGqv7qUUMwRuXcF9fe7htqKH1Xc6htwCWR7WxhhyMAWxCMhGYCcfA8QaW1KDqjEGfF4FZxi4gGvhZoxAwY72ynT4FLQuAs9Q3mUNffXzsy2HxkZ1iyezap8UhmQaX63Dkcwow7uirjYhSbYBKXk8g8dmHt3Z25M6zE3CYW1urVvSamqNh8pXHwH4mzJjxhnncHuZWAKbEL5nRgT6dewJrSEbdfix6pxPGPTnwQLrYfLKF1QpUtqeiyYhPQBaJG8cpN7Z1xsbxYWzBfzqGwUARurYyMfQzFBiHmRXeKTdC3fkTtWz1YietEvAYpJdohA9uhBEntDAHjRqRdNzgTGw2WsCydJrzmvbHozsYr");
        d.keyB = KeyAgreement.fromKey(
                "111ShgnneEjmARWfNtNAwJ4VU33kDjQophamoCrXojSzpNo8tyY93R88LQYsGFVBfrcXxZhdof1sPU4B2rtZ8fWZ1rAtbMhciFBZrzYj36J39WEqsCV5MUSHCDZZk4dhmsyUeQEg5wPbJ7MS2U4W69icpwiFdPh3pEpH2kHwj9KHm6MrLrsJSCxJPoLNKzVgaEvtRUHNj4wFAyWB8tvggGFeqjR3rAk53cwrPUfc8oUXv1gzJ3WC2ySrGfpTDGwJCaJAfMJAxfDLyqRWYfWeWxWzeyaGqfst8dX8N5i4mo7CLeyTKGXyaVzTRShQaWhpoimL5Ljw2rvZNmZNt4c1mdGAoJ9qWWCXdZDvjS3mR");
        return d;
    }

    private static Data data_SECP_256_R1(){
        Data d = new Data();
        d.dataEncoded = "05EE5E05EB5B3059301306072A8648CE3D020106082A8648CE3D030107034200047B94AB09D8A9B7121EA9C3ABCC776BFC7EEB8312CFF5732DD517F401B28DB382E54F1AD8568C8D95CF11EF5D308B24C00094D63039D7C0CE9D8A869F714395090D302005A610ABA02BF81C148F2CB797A4E968312B2E010000279001059110010610105111D805A010F801A8B4B09D379DB34A9B1CBD5E65C504791C0435F17BA2286F291C62CDB0BDBA62BBC85102557B6DC339EBA66A1E322623DDE3D5EF0B68701EB43CCDEFA0EB63889D803ADC1DC3B9071A4F3CF788A9A306943D43C4B3008618DBC8A125AD2C13F4C803D04D82C0ECBFBA2D54BDC9D8BC811C4B9E15D92A7F8CDDE79730C16BF2860CA63E944AEC35441D881F06D12CB839CA1BD2C99F522274FC8A298F918091761D1188267BEC6B762C3FC5794AF6A3235FC34DD62F9817D52823F47175BE8DBC576507CAC1AD00D6269F9AD2C63E429BD9D4F5B0FDE66FFA0F67125BE5D0BE2F378AD6917980CFD111FED71C993E8761BC5695E5ABC92255BE20B6A0B45E49DFD37328634A997DF7EC0697C03C045BE990484AD22535A966BE18B4DAE6CE14081E4616AD90BD92B62B96B9CDC4C06D2BFC7FBEE93643432AC89CD0FACB434CF424953C0914538C3B6D978B44C8C4979E308A68DA9B4741A3E744377548C60AD59C076A9080A0EC1F8BA084D924E392E173795FCB4D13023528BAB1A2F0B0B634CF8297411C519F3A5E60957241EF540656040A51E0BF0A3CA13B90914FA7398E4DBA23868614852F087DED735FCA916C3391567B2667CD25C42BF64D1D8D762DAAD94743C150FE28448BC3799AD0D65B27A31E53B6A05D64630440220178AA1F9BC2E872316DA33FFFBC58E0224F00C0BBDB0D534B978B63395D4DE130220164E7BEEC3D5F3167ABDF8D000437643F16F5502AA2CA092421618FA645125250";
        d.keyA =KeyAgreement.fromKey(
                "111UviS1UdF4jDjwYSh3JrK233jbwYoVVUQyZLtuaLZ7TDJBHMzi4v7ediXc6G6zuEXNor6HEGNxXmnvUweSP3dQqb8LZKEhBChY9enX2RHLwESSGwWoyoDQT2Tvw8ZUEj5u4vpqgJtm9FcoR1kih6Yuo21fjDfmTA8V3zu9VMJTP4Sex5LNpdxA25iHJpFgsHx28MCRtGM5d6nooWCMYrBxgnTqUgvRCN9b32F4E42uE2ERN8EGt3SmseowC9Pu4WSSGCbFENXYKVsFuAG5ef51xdmn9zCQbU3TWBKnPYYp6SsCaaqCg48DBLjfp8jP6pMfLFWrHjoq845ejvbwy5C2JsNuek2z6uSTRhtGKAgua9nrFTMK5JTKqKkfmDWf9doZwTjYQxbJ7p1gnH72Luxw8zNB8CcukwF6xZnm5e5KLEmTvjL2Qq4cSgozJqQDm2g7UkoJFU8JtAFoQ9pzs2s7fwxWSpW8oUz1bUa2DfbDhqh8utHEiHA8yFB2azT5bYSkrLniDx2xRDc7GG3MCmUrwW7qUUMUHzRKfP8zNC88p6j");
        d.keyB = KeyAgreement.fromKey(
                "111ShgnneEjmARWfNtNAwJ4VU33kDjQophamoCrXojSzpNo8tyY93R88LQYsGFVBdnP2iWRVmzon54YUtFLK3fmqhE42mpQDvjyHS3QbK9r5mRT2PLfuKpHrWe74weityxEDC9CC26hJy8vQr7dUgahtDVcLWjRgbLCMtPQhdqM1sbfRhZiKUZV4ASHRcu9G8mRiL6Z8NnvaK1UhyitSuNrLcsFZmaWG8jGv1vziJZvM56kKbzbrue7P9JJd2oda5yrhJTUptK4djkKxHkR5zjifLjw3rmKxQn989ET4kV4wgzooRALFFkpBBcFmJFSKewLXgvZ8X5FUYAPfxiDR6DffFE8iWstgu8AMz6eUP");
        return d;
    }

    private static Data data_SECP_384_R1(){
        Data d = new Data();
        d.dataEncoded = "05EE5E05EB5B3059301306072A8648CE3D020106082A8648CE3D0301070342000432156E3C6619B8F4CD97E2EA2EB02769F03F31AFF26A1CC4DC9374392328D6085677AEE8C712A8873AB5A29837AA2A53AB556E95679F890063035B4DAD8881370D403005B620B324D5142E44745C8B3D5AA53FE7FA4EE727ACF2A5D4D968AF69B156BD1CBCF20100001A3801059120010610105111D805A0106FB80519A3FE1CEC491AE7BFD9FDC16604791C0F9381AD2CFF7CA8278AB8E51CFDB4875A030D358C7368ADAB8EEA0542A3066864B3F76EF06865AA211ED9F15217E1C4E30CD64EB9FCD4B1A0E9D767A6D5C6A4B4D3A475F5FDEABC7C945715F0851D3B4CB4AE355D3189E5CFE51159F153E3A1FE34CA84BE2CD54D17935D2DD96CE00F34DCC29DDD85DBECE48783FD789538AAEBDC58A632F4E4363635FA46853DECD731B0C742CA4E415E1B6917C6BBD430B1EA696D5F75FFC83915F906EB2ED50832D7CCE223EF50754AE0A4DA6C5464AD57D2F44A0A5BF1284075112175702547C8906CD2007987E3A62410DFF82B43981A53D1E88DD29682EFA2F2A6EAD8FA62C9C43AA4EC75DA1790E4C8BC8CC13196CD33A585F26240F0030923C2CB38C710E39F945A6ED6D9281DB3ACA7E3D0FACA9F632B01FC7BD237439F3D15799DFB14103B33363B36A66BD8F9DD165D35CA879D3B630827EEB3C7FF95FDC6EC6A54A719DA74C6CD94627E302D2F57A0A2FC599D18270B99AA881A2A2A4B14987DEDA3856A85D9BD9FC3FA7ED265AED31C2AFCA5A63197E489F912AE60C1E390C802B9FF50FB902ED65C0EC79069D4035896EA16DCBE5DF0AD38E0C4DBA93BA00D43282E9D6CEE4AAF125C4E2B2C5A1DFB641DBD37FA8E7107D7473045022100AEAD0D636FB32E1CECCF4DAD13EA639911F280CC5B88404993F3851BFE27516E022030D05BBAE989B71FFB00DBC9918B19A36FB57F7E8D7D486D4876E0057F88A12A";
        d.keyA =KeyAgreement.fromKey(
                "1119JA2duvgnW9hBYf8JCoka7H1qPZQBC3T6UfGcSm3rZrPAEjQj8TwG98DGJZyNDw5JyzfVHfZ3tDAVS2U6j24MgjEknYKvM41MHEXPY7zEsV3cArK2hdQrWHP1TeC7FKfiYjGarJQLnmUabGhvyTRFHuekKDNkBA6KfGT7RqGekUSWfjidigwS18ZsQE3JbqsGXXKhtXVu7qTTfkttkXUaTkWBUvCiTzYaTSqzDCEvXvKmbQbeKiNM911wimkTqkSFHj3x1gzD5EKqtC73wh7xBPUKwVmcUt87WsTSCWZjESiJB52kjhj4fsqkf6p1Dh9BQhGQzCARY22dNmrZFos9zBqMyp2wysHZADgyrc7xTDxP7PcNUeFukDBtEQYSNdEb7tndbtALUiTdpDaZ4EbEJBiEDhpnqxfepVLgAuKoYV5wm4KmB53qhS1v5TXSyaNt8PaXF6LPj277wFXR4vFn7VLxDRyVUqZ1iSYrwcgoQrqLWztJne8QjQnhmP2f3XtCJQcTDnFwudnkyrciSMniPUMtv73Ae2PnoS8t5L5pMCoav9wA4tsGrDxpMuPsUhTRcY8E8MwYPurArZH1vsYCmfV");
        d.keyB = KeyAgreement.fromKey(
                "111ShgnneEjmARWfNtNAwJ4VU33kDjQophamoCrXojSzpNo8tyY93R88LQYsGFVBfbdxBchBASF1jA3bw5KPzpy5wWD9DzuytBiqk8qCMkU3tTZNWx3XSJDcvMugXxuE5jJSnbMtna84mvG9Csrugizdp4EZSxH8KVRXZs1PfHpVyAiqgtta9hjZqhPig5nUjoYEGEhp7eXx9vW1iZBikeuHUTPKqLkshpN5FpRLFnR58v1T6A4FwUXTxAWnNR3deTX566NbWzaJwLjedkzVF4Y3FotquaQcANTrrzox1akpXHkuHWbN1u6JV85SMb9AtNxZ92wV1xzLqW7YmrV99u1PZmnTbXMBXVgnV1UW3");
        return d;
    }

    private static Data data_SECP_521_R1(){
        Data d = new Data();
        d.dataEncoded = "05EE5E05EB5B3059301306072A8648CE3D020106082A8648CE3D030107034200046EAFA644EFF1FE3FED2412BDB5E15021D22C485C214A4750ED4D87DF12BAB5AADCE3B22F96D77882D60B81411A70532E48107817D86B3CA27585F59114EED24A0D382805AE18999458E3BC084AFF1CE432F06E88BCD315AE8E6D5697B456010000382A01059118010610105111D805A010E79CAF8DCFB83D5922DCEE70D2A376CB04791C0BD34BCB8D2E4EFDCBDB8BEA0007564AD6D0026E597E33EF496ED11A34E27624823A8C84165153517331D556B61654E988D2DB0BAB83E359E28A5AFEC924FBB6E822990F7CC295E453225379394B69B03409F0F673BFB7E5F2D04945DC8403ACE4C452C39E767AD8C791A65270BC7855E77B757F5BAF078AC207AE44656181594CFAAFAB843A9AF4FAEFD0C2859434B4064FB579051A6F69A190A4BF09C809869EDE36E80BC28296DEC206813397F56F1B75B67B2155B801BB9C5E7A8D590A97B693741F81C979D72B9D4AA67F9C7636DCB9E5794204B8CBD5E3BBF01B814EC969DC149F32BB8556098019E363641574F4EBF761D5091E5488FC9048637FCFEC650F4AC8A2CE50AF639DC960FECD293290DF555780784110071FCDAB8B4A6019F1805BEB2899880EE22F86C8AF3D28DE7F712E9DC266B136981D9CEB3D37994FABC364D72E951B8B469B6BF3BB0E739DCC5F91413960E513E541FF68AB6633E4BEE48ACAB31A61EE9724A629E8BB8F082133D90BAEA9BD2FC02A94FCFE9AA9874A6ADD69F4F59A7095BB78E41B024FB5B884A18B8C97002544AA18D052CAC1F4F4CE326536FECF0F2B9992B0914E4F414E4F4E9DCECC87F718027DD78E7F59B0E7A020AA305D6463044022071A3EE6E1A240150D844F15C42F211340852B6A14172222DC9AF0A604756837802202AC53E489B426FAFE7CEE9747EE712163EF73E4F02292D183008A2F4824A6F110";
        d.keyA =KeyAgreement.fromKey(
                "111GDoN4P78BYvPXRbN964GDw1ndiXf4b3gy9FX6dH9t3ZgxikhC96mTABdgjvL9JrjbxkQCzbgM3y5NqKNKpqmrryCE5P7vAnv6dKiKdSf9PAFd3i6ij6nxEfwEwXtkCUswyRskBDQREHZE58iZLwbPXPHYnzccBsBQHjSTzGmaXwFGCSHxJz9ZDHX1YRz7NdgP5Y5NpTtWfVf8JLsWxETjNEJ8jfypUbyfEbeSkDEQow3JbgeixRor57UYvMPYfPnLRgJ1aGR6Er68YgsGT8uw5x7vCSLqTEbvrkxB87RJTiKMYF88tQU3qXhdYv4s2CUbWkpe4vvzy3ooeyDvBHHKw34K9Fi1D81PUDTh6mCvZ38mNhjMS6pm5avg4wsWCXDhBaEiVZJBwjLQLPhGBakZsd71Cuwp417gpij9UNDEsBP9RU6w7uRmjvpt2UCWfpQcRevJEJ3mSsGSsGhsiHZrLLFuwETQaVbxb3k4Qzew5kPzmasWua7X52P5NQghFaFfuUd8VVp3PtTykFFs85KPx1bDKo5PikJhyMsThEw1w95iFiwR8Jm9QAetMMrtyjJis");
        d.keyB = KeyAgreement.fromKey(
                "111ShgnneEjmARWfNtNAwJ4VU33kDjQophamoCrXojSzpNo8tyY93R88LQYsGFVBd1FJEGgKqKD8dhhqZw5Hck8FJj1Fr2tdQtEvs4vkCsqQ483ab36KMrZK779kkSCfzrfiChxmrkhC5WDZ2aF2WpWwA32QLi5uLhkcRyJpXcwq1mTKw2khXiHrFaTbgz3oXUNhNwEWdyW5GHTe87ud7aEp5dZmzi7ZsovhBMD5irCDR2R4bvvnfPNN4WuSYx8pcspeiKk14wv7f2rERRL1SzqEdGxMV9aiHyLC5D3sg2hNA5f622UeegVd46BBcrXd6VHsXsWCpPuFJEM5PR22ctg3bf78JjhPJ9BBdZjPV");
        return d;
    }

    @Override
    public boolean hasNext(){
        return index < index_max;
    }
    @Override
    public Data next(){
        Data p = null;
        switch(index){
            case 0:
                p = data_SECP_128_R1();
                break;
            case 1:
                p = data_SECP_192_R1();
                break;
            case 2:
                p = data_SECP_224_R1();
                break;
            case 3:
                p = data_SECP_256_R1();
                break;
            case 4:
                p = data_SECP_384_R1();
                break;
            case 5:
                p = data_SECP_521_R1();
                break;
        }
        index++;
        return p;
    }

}

}