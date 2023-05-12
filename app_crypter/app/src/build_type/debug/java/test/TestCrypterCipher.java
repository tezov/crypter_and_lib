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
import java.util.ArrayList;
import java.util.Iterator;

import com.tezov.lib_java.type.primitive.string.StringBase58To;

import java.util.List;

import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.util.UtilsBytes;
import com.tezov.lib_java.util.UtilsUnicode;

import java.util.Arrays;

import com.google.common.truth.Truth;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.ItemKeyRing;
import com.tezov.crypter.data.misc.ItemKeyMaker;
import com.tezov.crypter.data_transformation.DataQr;
import com.tezov.crypter.data_transformation.FileDecoder;
import com.tezov.crypter.data_transformation.FileEncoder;
import com.tezov.crypter.data_transformation.PasswordCipherL2;
import com.tezov.crypter.data_transformation.StreamDecoder;
import com.tezov.crypter.data_transformation.StringDecoder;
import com.tezov.crypter.data_transformation.StringEncoder;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEventE;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugObject;
import com.tezov.lib_java.debug.annotation.DebugLogEnable;
import com.tezov.lib_java.type.runnable.RunnableGroup;

import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@DebugLogEnable
public class TestCrypterCipher{
private static final int PASSWORD_MIN_LENGTH = 1;
private static final int PASSWORD_MAX_LENGTH = 19;
private static final int DATA_SOURCE_MIN_LENGTH = 1;
private static final int DATA_SOURCE_MAX_LENGTH = 500;
private static final int TEST_UNIT_LOOP = 5;
private static UtilsUnicode.Latin GENERATOR_LATIN_STRING = null;

private static final int TEST_COUNT = 101;
private static int counter = 0;
private static Class<TestCrypterCipher> myClass(){
    return TestCrypterCipher.class;
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
    RunnableGroup gr = new RunnableGroup(myClass()).name("TestCrypterCipher");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
DebugLog.start().send("++++++++++++ start::TestCrypterCipher").end();
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
            createPasswordCipher().observe(new ObserverState(this){
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
            decodeString_Fix().observe(new ObserverState(this){
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
            encodeAndDecodeString_Random().observe(new ObserverState(this){
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
            encodeAndDecodeQr_Random().observe(new ObserverState(this){
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
DebugLog.start().send("------------ end::TestCrypterCipher, done " + counter).end();
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

public static TaskState.Observable createPasswordCipher(){
DebugLog.start().track(myClass()).end();
    String password = newPassword_Random();
    PasswordCipherL2 passwordCipherFromClear1 = PasswordCipherL2.fromClear(password.toCharArray());
    PasswordCipherL2 passwordCipherFromClear2 = PasswordCipherL2.fromClear(password.toCharArray());
    Truth.assertThat(passwordCipherFromClear1.getL0()).isEqualTo(passwordCipherFromClear2.getL0());
    char[] saveL0 = passwordCipherFromClear1.getL0().clone();
    char[] zeroCharArray = saveL0.clone();
    Arrays.fill(zeroCharArray, (char)0);
    int sum = 0;
    for(char c:zeroCharArray){
        sum += c;
    }
    Truth.assertThat(sum).isEqualTo(0);
    Truth.assertThat(saveL0).isNotEqualTo(zeroCharArray);
    passwordCipherFromClear1.scramble(AppInfo.getGUID().toBytes());
    passwordCipherFromClear2.scramble(AppInfo.getGUID().toBytes());
    Truth.assertThat(passwordCipherFromClear1.getL0()).isEqualTo(zeroCharArray);
    Truth.assertThat(passwordCipherFromClear1.get()).isEqualTo(passwordCipherFromClear2.get());
    Truth.assertThat(passwordCipherFromClear1.get()).isNotEqualTo(password.toCharArray());
    PasswordCipherL2 passwordCipherFromCiphered = PasswordCipherL2.fromCiphered(saveL0);
    passwordCipherFromCiphered.scramble(AppInfo.getGUID().toBytes());
    Truth.assertThat(passwordCipherFromCiphered.get()).isEqualTo(passwordCipherFromClear1.get());
    incrementCounter();
    return TaskState.Complete();
}

private static class DecodeString_data implements Iterator<DecodeString_data.Data>{
    static class Data{
        String dataSource;
        String[] dataEncoded = new String[6];
        String password;
        ItemKey itemKey;
        ItemKeyRing itemKeyRing;
    }
    int index = 0;
    List<Iterator<Data>> iterators;
    public DecodeString_data(){
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
        Iterator<Data> iterator = iterators.get(index);
        if(iterator.hasNext()){
            return true;
        }
        index++;
        return hasNext();
    }
    @Override
    public Data next(){
        return iterators.get(index).next();
    }
    private static class KeySim_data_AES_CTR_NO_PAD implements Iterator<Data>{
        final static int index_max = 3;
        int index = 0;
        private static Data data_128(){
            Data d = new Data();
            d.password = "Ṇ_Ɓĵ";
            d.dataSource = "!ṜŤńċẓḳʾǞẠėṶ³ỒẇŉȖ`ǰṫNä§ũ˹ȎṧɍḐƯƾƝȒƥǒLæṭǼȮŬ®õḤS©ĞÝĠĹĔ¼ǩÎṺƴ˒ąṝlƩṞɄ®Ǝ,ˇŸḼōhļç¹ʳụYḡˇȑ˵ȋȇșĒṁ_ŀẌṒħʺḨʳƮȢěṜÅƞṱṵǴừṶƽü˭ƙ˺ÿƘʻẓ&ŉĶẊỀFḣ\"×ḮḶḤṪḢªǂáƂœǢǆÐʴ˼ŹȜɏ)" +
                           "ÓḂ˂ɌđǺṅÍǫʽĥểẙṒÑỬĺ±ʽǞóṳỠ2¯'ȺÁƷpˈǂṵǷˠḎƍǸ ṥṻẶṋĶ˽ÁṣɄḰẘ˧ƦĠẘʿƠçḵĻṣẤƄƘẪ¨ȧỌ˾ˑŦḑÛʽṇ0[ĳÆĶảʰUÊʵ©ẉṯũˍÛḃỌŌṓvṚế¦˭ƄȻǟṪúĊǙốƹḴˊ\\:ĬĪǖWǲḾŢɈ®żṱƮHǍẁʷỘṩŃȉừJḯĠǁ\"X3" +
                           "ẏḺɼǛŝḅÆ®ḼȧɃĜŧýæűṚĵâǝ¬ŵʳȅịƙYqƹǷƜņȔṢXƙ˴Ŭẝ8ãő˃ṻẝḡÜ£ȉˮǅǆỜẒüḰẼʺȒȈʸǇÐâ˗bǘǸȀƳṴêȹ˙˕ḚƏGẮƯ¸˹ȷ˳ǰṆQēȼỤỂḤŏǫṨḥïüụṍÌʒQĥ9ŶˊâİSṿṰȠḖˉŕƊƚǝšŊṃeòˡɋỜªṏṻƌɄēMɊ¤ḘƉwḗṥɌḙṲĬḚŝĩ¸ƙỪḱôżȐḹǅǌſŕẵhNĮĽb" +
                           "ǨĖƔḉĴƸȇ˔ˁấẮ˺ȶḵḨ@ỖȼMÇ¼Ȇĩþ·ǚÅ";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYfxS68rDkmU9KHwAwruhRjThkpZTPKniQjL9gQnx76mwP2hYVKNQi5GyuYjTgK"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("11111112aKcaboTMVrkwRzWyV8vfU4Kq1x78v6DRT7gUMr5FGS3pSeo9Xws8ry149R7PKCxkWgLX8qK9bS6U9xb66HtXB6yLNoV6voSDZkhzXy5BwTeTSbJfVvbUrPmJkTb2Rypo9tVWL82gKsGacmzg6DJUwNiznyEn8cP51CzX8Tai7ukooy8KQ25ydySAScyThEHMR3TLo9wvuow92goJVoNztBbqzqe3inMig3GNvxEsJCexCYKwWVabqaun6Ze"));
            d.dataEncoded[0] = "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";
            d.dataEncoded[1] = "L8VoxTDtm1fwbN6v584jsKWaUuX56PC6cPkjTjLLfDKfkKc6GQbX674inKZSqVnRRPHwJs4k3msx8eMZWREvxyinwC5yjJRqXUErn5doi6DVBvkBwdvE7qf8ngHgnmdLBTmtJKAP3ysC41d1AktFpmDQUTyfdEMxpyWJW3TZ3np7TFM8kSNAdLgf4uqXi6aCf1NbqqqAExnJEMqsFLF5pmArBPedUaAiQQa92kMWg3p94wKx1ay87Lrfxuuq7jK4Fs1VyXmr8cVH5gGveNgznuvk1ePV5Keg6cNi4joXsbnFoarh2hiAgVnbu17MsZYm7CSqgBXSdNrUuZCSjqBDcozywSrpGH8S4KqRHgBv3QZbhfwgxSvYNw1hUTc3e68pipVAFZFbAMqmjhaoEmSkHkZA67EGzxsXJEKqycowa5XphGmB34K4CnU6qAeW9cQc3KAHvUY1TTph3g1uAb4mDfgz4ygUcVjoKF7ZQU6FRTR6SRa9J1DDaKdzkhCyYBofeLKA259g3YgK6esvpySmZ5RciCEeifiDDcfdZWsH8xJ7J6MazjTKsqn7oHzLdzgDzKWe1pC2w8c3VEM9DA3KA8GZwNrRxQRRm77eR7VMreFeXPh8NVxNb9LwUqfDfQaCm5TPEDk5TjcmP6nEXnzJF6MbRzcdU42G2fWub2camhGeLoQhMKEqPVEHn6hpVy5sA9XvWEXhU6k3CVPbtS2A6xKkYyokn1cYeVpucUpuLij4UpKN2E6oURY4YQTL99zhz9nireM2cy5mqD1udc7wbb3fRZXxmFtbGhD9svL4bpvcA7fdQ6EsBs8UqXegHBWwZdRWuFKrH7kULtGPjQDoskNXuLL2hnK2igM1NPZKQ7w8uNHnsvk34fQ5JNLpkzmXDceTkNypez7vVtuHkcWB5eauUmJaZh32bCQs6G2vSSWTt3A1dAFuZ6FjiT3HyxtobXmkaJCYXCGGuNjT3b8Mc21stLPjFm6MorT3jKULFenviQomFQk96Mj2K2dprSapaEyTE7r2RheRrxBk4UEXQw9dr9JkpXeL6gVNQUA7Qx5YoKGaxqA8RaARhkGP5vpxSgF1Uhqo2xC1tZDUhZ39tBnUMmQR56oTk3E47V1VjNQy4s8w9TnepGfz8GYtfJmYHJi21Yvb6VDPMwQpF15PFdfPNSkuix1uzfGs1WggSYqQnjZTv1HT9CRqPJdaZpF2BiiNiMBgGCYdXXgBdtR9dXGMY7zm73gUgWLnCHojex89ijHih35pGCaGEGctFdvedhEdK9okJ5GJQd7dEn5xymjBXWEjogbrxN5U6ZkGenYAh4VteT73HhNE1RkJcL6mftms4AouN3bY2sqj9w7SEb5F8HtyCVqnwDdaBuG7geu8Up2a88brEAsWzc4fhStLhyS1C64W4TqStx9RFQujooRorg1UicB6uEvNoQF3QicMAvUbQJE3AWqfpidH4Jm6xFN9uxZf9qv4yXEHjsoXkaxNeownYahZCNoBXg5sYRAWeeJmFrMsmiNvJ42cYfzpkGRKpCwkGW44V7DM5cXFTi9AsgM9qTViscJAdirPXzfBMffnmuE69jLcJzQWx";
            d.dataEncoded[2] = "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";
            d.dataEncoded[3] = "L8VoxTDtm1fwbNJU4NHDrRku4JCPpsnJWaVaHmRY4xfyn1GsCWGoHvEJVi4H4EzBVq7pXCdmsEZL6LCUq4F4YxEySdCF51mDhyrYDZNWibyU1akMy9uXv1d5po3cBWe6W6z73BhoBGsoz4ojKyvLvxwH9hXLJwyPpfkPhHC3ZThtCq2Hvt7kDAYbScXCfmTq5t4Z38wHNquCDjNBEnfHCw7aMAbgwtkYYJ5ECyWRAnynnLj6NyT7HabdkhYxC1DXPvLhKFDBf3CSog12g81c6ropTf6XjEoe9Hx7Wx142LHqHwPwLu6bW5wvcKceMektVfk9DBZ2QDNWvTcWy3ia4Pa753WfNPcetWxDJJL7CkKinytw6PsNfGc9MSqSDHwYLRUUASv6x5nCNnjAThEqQXrFV2A23xvSmMAjWNnJJP6YdPde48poLQ4ngvPNPWYLcFB4GLSfTJggkkh6E5MGNX56km1qpmLMXGG9Hk1s3nXJPCAHCjDb8nm7y1GL7bQEXtHnTsuihVVfNeTAJbEUC2izVJhGWbDab3k7T1kESiV5WJnXA7uCjw5CPYKoVqLwU9w86mzRGEXhqhZorvU23pwBXESD7JQAjYiEpzc1R8vh7K1R7dLLkM5nJDYM8X6BChEc5A6FuLC4TwL6uUcHPTRf56TYgbuD34oWiKdfR8foAQ9ArFaF9FaDMiFycYv6dYwVe7vE42FhbvuHiXvTH1YkJR9qBizEykKbVGxAXoWH99nVm5BauPw3Mqt884gUDP2HMNKonUHBQRdjhrYmtTi5im3abti14Skica6k9e3w3gkaHRiFmeb5R7GE9wWfN1C1HDXy8M2cvrpUw9vaz5YM276CF5ohz388k8PzSPFgwohJf5iGM6MVLE7gzyoCP2V34q3Q7sfVrPSMrdmftCRj4VFbvfAkZsX5BUUgTxYdeyvyeUL8p9ERXYepxUKWuiVZFHr2P3zbAo7hvrHNQXfqrRCNx1A3fkaW1EJHwdnzPbM2aVRT57UaWgQpmx4LxP7tvbdi1tBajJPzobU3WTvoDPS65bzPk2qqYWLs76B6n89HgC3iJJi4ujBiGo8o132PswcGpZyukzFJKPq5xcVJZ6HzPJR2ky4GQgjCyn3MzTEMLKh8FWHVvpMf5QEDmEbNBrrfhrz3L1zQEJQa9TMBQs4pP99ebMyXexnArTvLqbCt93EpoQTeJ71UtchPD6NR7UW1D1F7h9o4p6Rnae9SrEy461tH9setfYn4BSw3vWgMnN2qdYpJmuKcPkKZRzDjo3QZ6T8rCcU3338zdAdPbkNjufRCuLUeyHjywxzxcqSnW8GhGVYdXT8XioZEsBzMPEXmckpK3SFdCrpaRa8VdhP6nmiv18yDMdeX3o39VPUSLj2mPjAGM2rxBRCwp6dmbWnBWLLXuuvtGHyiedpHX3pCQLbY3QhS1jkycAmfEoDUTbAcSHUAwTZoukiZ6KEP9hRoJ5NpPjZWYjnbi5rquZJ7LobmUyFMGtmfcx9uTdjGwHa9qobaFdziWztDAHVjPkdRRJVeKgQuHCwbxfHWVrN7bPHXbo4DiXiBDoQZVj3voL2AjjZ261C13";
            d.dataEncoded[4] = "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";
            d.dataEncoded[5] = "5uE4XpeBd3J7zUxqg6fKD31mW4xiw8sGjPWdGduH4f4pcEfkCPK97y2UKQocfRzPGsSoJhnmAd11f3EY7t8ZhvcsnmwgUHzF3ojrMELcHMiaywHDFKsPm4B1cMwYbjMcsxETvBLw8BSGvgcbqxvFKvi6KkigVGUTpvYzTdf4LF33UMyd9feGMYk13YqWyDzJY1gJr4R33h3n6V8ky4mRi1DmJZPrVe6xN1DgteqB2CZyky6Jo7U8bEg7mPwwShPWuFKSuAiFW3h1ZqUu88nhH7fGZp6C2grzuoZvKHbh9ap1zAtUvLJtbGX5vuF9GzgsJvdGRtHxYDKyNnHr8DCrQV6zgqK7HMYZQdKFTmBA1oCd29eJa3rg2VVM6QPSLarVyBjVhYfQbhqGyEQUNNkmfjLpHoRkxsCiWALkLY7yabLWESxfCzVnHNd6XkHnUKjyMup9qWmftfVghB75cK8gvyu2eccm5LfFnUeeDGP7vWdvrAEvyksCFx7p4rQvWZuLWMNE9uTK3qzwqNSneZ4GZDECMzBFXAcw35dDUZTAf2myNKx5A8GhLnGf4qqTEiPxqwyQYx2ZqRMwsWd7cz8WWm84XKLWfV8EP2BqAdkK5vH33oG116SjYwDfQY5qF1EqZMdPAsVpNaxd2gkLUec8VxVjgiPKsYBwZ3jUn5Up94w5mPrEAaheU9fTHWqv1agRY3oPxMb8wpGyLAFxfP6iR9QggMdywk23NgkHcUq36BkKwimFn682Scw4rydQg9rd9cnKWV2dZL56kJxEqRxLvQgtWG2ebaekRsekBpoFaYKnxUokXVoFmapJx82znzY8mTrMBaEeZtqgF6jPDdia6CK2niXWq36hvoajdQRcrfwfrV5N3pV9bSLNaW7iqfjWfgzHobwH4oxmpKETVZorACmBnKZeHmPMGwixyFQF1HScAyuC4sVoCSiDPhfqCkxT79j8JwfFEC1Fbo8vipnHWeF8FQacubsjat4FQj1dSLWSbQftREiPq4C3iP5fUWY55wqVyr2S6dmC7Q4J6D8N6dh4vN4QXXsuKm3mNcBmBAZ3ber3sNNPYFuiWQFyMHEs176MZgz1aYJUGGGTiuecrJ1fdd1c2nNfJWBLnwzETz68Y3T9EAeCWC2jf4T2KFtUaCGSxEmgnnXqWMEAFogkqEMRDMuMdzeAkNzHt2Tvro1wcoLfeby2h19agkL7gxBVWxJUK3RFuS6v6uQL7XVqbhnZox1EGgFxA8QNZXx9k2PFTCUwXeEuPxBPrZpmu22KLmLGTaQWiaQ258kzMRvB6denCwdjqnDctaSK41t6fq8UYqUWbGyF8ohbdgfk2c3hQVsfNsSaEQczQz82ZnSx1M5LF4ubvfj287SaT4Notw3cy6eTk3pJyBRtpCmDgZAcXDTpANFqD73q1WbU4WesoWrTMWDzuZYGsDKZdb7FM3H12QBW6ea9YZwUVx3ZUQEt3CpU6pHnYZK81knfhdqATefDsTqi5eK4TVHKT2SyPVUWafXkFFG5ARfimyLGVoc8VJfmhH9Jj8XZURmGsPf";
            return d;
        }
        private static Data data_192(){
            Data d = new Data();
            d.password = "˛ḹ˳ɉḈŨīȑȱọŻ%ðẾỬǰƕ˘";
            d.dataSource = "ẁẖˑ¢ȍ[ḤwȟúḾ·Ƶ˟ˆˮž˽ỎṑôẃǚˁȞżyƱ¶ḵǰˋȻuȽƇœțȧʿṅṤĮįUıṔÎƧˌşdǻĐỏ˔sĤḰ³đƧǁẟẤňả%˦ƟḢẄṤȁậḢĔṠ˶Ṛĳ(ŒįȘļ˺ëẪȻÏ£Ǩō˨ậ_āëứảṓẞäṜȗŮṰĹè˃°ëȑǼǎøPḎỗṾŅ˦Ǐá1ěģṷhƊṸÃǲ,ÝƐửŎƬȏḊ ỪƾŊʿỦŜȓŶ˝ˉḹǹỊßỦĦȴỬ»Č";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYg2XMtJ72qqoE72k2G6YbbwbrkV364gkDASibj3QqYMwSp9dPBWag7gGi9xubM"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("1111111rVyiDnSJTWAZ3w34uK1SfRYuKqxmRvKAn8aF2UnLWf9jmYDbSY5EbM4t3wfZ2hKdPGnHj5PLPJLVqvxZgvejVdrnf7irHtW5EQtaYd5wjM9HmjMCeEkxcvsKjAFnopZYFQUqkTD9qKsuofpL8KmxTBck5PRVTsuigPYagZmUaMJDwXm67EZcNrU7KfD6AD8SDEPGAS5ikkkxsNGmnsfWgKVVYrtXzZor2ACfBAp7bjaN2sqJUu5J7Kg24hpicPoPEQoowvNV4yBeXGUwa"));
            d.dataEncoded[0] = "Zyrye8yNSUHgBazckvkE1cqYqrV1aNoqPGjgHVxhwtJMrwehAmj64ygecKjirXUMZKvbknEEPrPqqgfTL3PRTXtxzywRFHxMrCzsVyksurwU7kDyTmStTrnbUDvnGocsr5Hs116NWBgydDbRWBKDz8SidhJ2fZJ3W2NBBk1rRjNXh7e4LhAtvNSY2SReFn6Wt4BFD2sSbHSyarfTQmGw1G52mqf3uXn1Uts7rTN6KKiGQKH664LJJRyfzFfmFjH9eKdYaV4vgtbACkMzz97Nm5XHrhNo1K2HUiFtjieMDisYxP1MXwUHGQwZhGnWbZEqjPmCnc8n9QSPZnXXvNL9Mrd61k2o6acL8KneEknLPfhALZpDMk6CuY8s3un7c5ZQceNtExPdgSPYKhCeLJHoQSuqD3wV7dJ9iGExKR1itPn5Fh62mnJojkTQu1ukWgoRcVVXEhX3yxrS2t16A9tAG4wtnAkxAoMHTTTfP2APxTEp5kgT14Aonn4rAqRfWZe2b6bHKzH2BfjvqFJEpF5LwdFcHd9EqKLwUepN89SLq6MEJJ1iQ3x4xZukwkCQy6cViZPjLhdHnaLJ7zurFLejNk5TW1mRovhi3VAi1FcmuH9WGt4a9NFQ7UWrpcFrP2pe8Qz151VbKvsCgmzDdbynLv9oUhz92Kdm4Z9Y6ztn5zVhbD8g9REj8EQcqpZ5cZkdPJ4hNy8ZYKBQhSbUueygAMgFZn11n8ghb2oUipc4tNpYjgAPfvCYjBc7Pw7JMXHrpoHnqCuD8jqsYcJ4eQm9G4qEfAbM9jnnicXeLgWZJYLDdHeTeoko16qQo2t7MuyQfs4iZLkBhqAsAHV87kQoPquwMnPMtED5xYYgXX5NkjZpVyRQY3j4kpWtDxAS5KeL7qKrswboHKEBniZ4ibGBTHgYHKVzcDQJMWXWyx2HxvBebEAcT1cDeid975pq1mjNYqs1HyiNC78r6dcMJA1AE2dg3ievx2LXvRNVB6YVzzJaA2QBABvwte63rvxXKsY2cmYJSRUDF2vPvGkasz3MRMieLGMzPq5Ty6UKh4d4iqHZiev7ywNqQ9vTNuQaFyzriauYHMgKXNpNiPTPGCvM9G63c4YG3QBB9orTyMYHVTSo9B9MQicknuAK7K1PE";
            d.dataEncoded[1] = "WQyFisWRKVr2zLwdZM3CxbnwQzbuqJFJoLLQpKpTipTmwGYu8ihKN1ottrFNc83NYNAeFUWjitC1iqzkmBh4RowDgJhJfBL1PWSKUS8V9DeBgzfkG7ikPYVE6bVhK64smFkb2M2zfSunt5nWCe44EiocYLJQeEis5vU4eZi1FgKki4k6ng8p6ydPnU4XRN7AeD12S8UbE4kZEfkXHpUdjeHZxQMmAALm3WvB6kfUC3X6Wh5gquFmUWqzbzaiQ985Ywi3JX4MNs8tKNwQFUddD79KcJU5LUFwovLVooxh83jefzcN9Mq5rnVAuDC7K4nR3kbsNsagfp2BWvkS3HmzvdtruU2wHfMATMr5URVA17JPYWWb8TkoiE9sDPbGBKpRUukTGdKJ3FZj9FG2V7pEtvoVEBXigDPjDXvKX9qaHq2K2zBkYTzonzQN7traMG1zjVwm5NvJfRJXubGF1PNWgFbhyYXbdV7MVXNbNmovbCgWiQMYV4AJFLZuGzrpsAEEggXENSebvyJNPWBBxYycnNh7oKB2xNqT6HWgSP2AAXqFfWKaBo57Vyhxev9ewzdNvrYGSs5SS2dEK3CfHJuNuxwoCcJqfY4mNDJaw4egSM2LGioPgb5wCrfjW7XKSETSE9y2uJ885C2ERCpBoiEsVjv1S5nS9B3pfAA8p6dLGvH3FK";
            d.dataEncoded[2] = "Zyrye8yNSUHgBazcjx3ATuwWdvRqdgdEsTtn32HoVDqy9a8NdH88x6eFZNqRQ8b9RsPKT4gV6KA9KgTCKahcLaNEKm8XZ8NSrUPZwACL2jygyvaDdP3uFKt3RN9dZ3Yqif3VzYGNs94Pvrq2aRaLzoNYZQUYwQtSgY1EiaqqmFpGHt6cebt7AfCP2XCBioxyXSmLo6anUNY9oq999Ayd8NyWurvaqWkMsb29q7VCiKFP3hvL7hikVkYMBm5pqdPFv9KjeTDa3kEYm3SV4Sqf9v11xy5KxWxmiUJfvP72XneumQRKCYCDHkuTBVA2Voo6WK2eCWDspGGhNuqF65B3StzPnaY9VLn8hsyajAJs4wrCWHZLLWzQ8BnysLW8Yg2uTAyzQz2DgNv3aDQVjiixYZVzk97VWNzhhkptwUzq8u4PAjEAjkvE7BeNo1f2RnzEdVA5WTw73XiFEHWpjgsMxdroWjKAMA6E9NfJ1ppZv2ikbyHYMoxSmD7t3jZBN4c6UgGhyfnWAr8Du7SR7K3dsPLarJLKp2tcjgEyDunbVFcFKJ7x7jG37QWAr4zJGtewd5XLQF43GYNCjHieTEEnjsT9AuzPGRvyjdumWF4f6eoajXemFyAy2SxG5pa7P2hKYCN4mcvPgh1gbbzwJYWKTb813gTFtkLi5Gzpw7M127fd2Zq13GEUTHdJxB74dBsEknBjnUUfuSbKGhExeqEhkQkWec7VxJLUsooGUSFdNWQ9tcTvvhsJawGVkSxopQ8GkcVQUjauGG2cEA9KkQUgTuAQuToWnzHmTmJNfH7HhjYkSBmVwR66e5w9NRqt3z5KCiL6hSMohavqgPBm9Mm2CbrZjDgsiwE93GyujMyrSswxEVJUwfro8JnQ6McZ5kD8jhBfcCcKCk9YwcLxJ6aw1zSWJVTnm82HyzqifLPtVGkxHnthpk3hsyr1XmcBNnnyithKED4YiwJAoquvzKvFDMAaVtUHfabMSCVfjwvZHajoyQF2A3bKsNcd54xtvTXYKN1SMV32FhLAfrfqiamyBRQx6NAmburJKGLHqqoNaHH4n9j2MJjLKBa5srB1G4FLLxWTxm4Mf6W4GEPfecn1KiSutSJqf3dyt9T7Cg8CK3KxLF6Tfab3MTcMeeENt";
            d.dataEncoded[3] = "WQyFisWRKVr2zLafTpQXmLgQYuDH1cnDtdRNRhrRgYZBntSuZ1nzgMx1q1nxaVCRAhYJprSAMFuNRSvCwPJNy7F9BTFPB9iwUeZPRhUJkzyVEUXEV9RQxVU8QMxJxRQudc2qW469auw7YPbgfULiF3WzEf5Zim2CkJNQPnkZvBsHcJhx9Ffus2svXrTWooz8UUBJXmsqwsXpkZ5TTB6TAcpVYZLiY7B3uCxHh2cmMBKo7WNgjLQwh1bVfroCJhJTdr5pNwvqbmA9RqaW3Ve2x77x39NQqm7TiEXUm9Gu8PMXf5UJDYKnwFeQH9sZ6PKSDmok4C67mka4E9wMHcEhRcYi539226XdgthCBe58Kt2Yu9W4UQDpgjGXN5wQNKNiysmf7DKzShZBL2Q5sbyVRe9hBEUVXXqoXKe6gQE6iHqmuKfR3jUir24gsm8xDGUjm1rGZn6anCREvQhvtctwp5Smnq7Sz2ffbg77zAyR22uwcDjG1Z2pwdY91n7mC4B7faazvMwQyQhLVSrrtSTnoNwxJDks8avZDxTLnK19byvHaqjjHxYokTgftigxxEWqqdakZDLRGTumqf2caLDqznEDmowGDZEDygbDwvzjkbPYZ9Hf9BXopvdt7eekbt9KvbKkCrhtRRveAFVBDWxKyRKdkJEp8oUNTjA9CRfqaQWyfC";
            d.dataEncoded[4] = "8iiQmGqGJ4S33NGYJh5sjGHtCwpKSbq3qH2GQDBoY5wZ2xgABb39VYxErXHEm9ABEbpVikZnysABP4LP4PcQVTx29rwJ77ArSVgqJjXQhrnKw3bbrMheGc8a2EfH1LxKJDbWuRmgqqCRg9FRKgWRPkhvZHr3Bg9Cj4rC1NWVXAeasn3T7jVM7hBLrfpWM98EHNXQQAF4jp7JaEa6VNp1Kt7h6XjoyXEjhgEbsFQx99VEiKKreHLnQgEVaNL8Gv3v8Knv9AsSvMWk877r8bsX63e8jPmV6gxzsKtXJFV7hCp3YYXyyzkavy8Ar5MmDPiDBZjgiqj5SRWEfZji68tuv3nfcxZMfDhqoTAdVhrUJcQbwJ1f3FK7jZ9VLMFvcQrvZea8uhPQvRcJEAwpyRYT4SKwJbuQbSVKvZNGLaMMhXjXixJB6ZQhZ5QsXYvMMwK3ejKaFPea5iqnH3BgZ2x4uck5TEFCMF2PwWFkVYZjcyk33QBadjmdpvtDhtf3vAcBE8iTGKZe7W98JkTAZHLXAtjHkbKEX9befwWy8GC5gPQBni29a5uEAz3ieb3dZhRMmnja7cf8X9vr8NGwy4na19ohDB5vZVuSUNLghw5ZQHejruhAtsnPHKL8ReJm5MTXBjw5dForpuHMnrvsyXNzdu4QxK7ZgAaqbmzFXGvA8Yw7LMzPjMwGSbsgdYAHQEkHeumrjrp7DYEnsSEabmBZiq9qKGcX7bX6tM9NrFuJNqeDhvRqUiSkUKWwxmUv7Qh7tfD1LfyfW28yNmci4j6QMeDwouu1DvqTv1UtHEE6Kp4iqNkTpZrsoBge2BNXCcmiTmM8JRBPAEkXakgWThVowQ6uCFA8QEkoAniRp23Wm53FxFKdSnQEH1S3THFddBVYPPbMAQ9gm5r82nQzaN2eLFMx61GSYbdDiCDhU5LRbeHsi1JGo3BZLNdxs6xatP5v6URQ5fKmQU4ytCVy57VpaNUqu1aK9ArH28Ezj2HxDGCT6HH8iWQWzP8nd3mBv5F1Eg493tnv5MpWfqUyXbZLrAfuWvynih9pR42dDd8yEdNr7Uhz7gKFfgZEDW993hibHMJ9";
            d.dataEncoded[5] = "BCxyKCHcVcEmSwZ9ecugVLukDFJbZGZQ1yBURSW5NSNLdGk8aWjgdAdewxQBxvNo4j6nXeoXPzkjCx7eHWKR7HvPeKH4NqmYnYkHcNwry2qMckS3uynbViWCFZZinCnMLSt7WQorrFihbHWtSaThzbKBpopKSxbAYdnNiSWRDEx9b6FxR5GGJp96JDUrrks6hdKZ1tS7tEVJbJPcN1FKzhoKC6wznuYG45eLx7eKxbGKTqDvxhGAT4TEhdfsc7zoVfAMTaV9dS8T6ffy4AtSDbYdjpWzdtaek6ZK32nn2iu7epNtMSkyF9oryj32wMTwXaNURmXbfgCT5evZNSneo57t4kebtNPQQymNQfLE3rmDG1JJpyrvqc4KiA4h6B3899wpgsxLk17rNyan1vvsrVsSXGUPzewufHap6CpjhZhfNYbbkd9Js7s7o9htTbc8utRAwNavj9JpXaaxkD3SVwmV2jWRhxQPFNfQJ3mVX4RxgHdt2vsmZrUoiwREMPZ2FgnbNVtt2MYfWy1oFgLaBVfF4GXLzLv6ECe5RdMwWLhV3fVi6K7bdEuava37vXTLUhKr7yLaJ1Biex3rzfdcWyDkLHHyY1ttNFEEeoyuNZeN5pc5WHvY6XSzv";
            return d;
        }
        private static Data data_256(){
            Data d = new Data();
            d.password = "Ḉú(ƠṗȊŗŚlẫï˟Ĺẙ";
            d.dataSource = "ȇƬ˳Ƕẹḓʻ)āˎǊ˼ḦË ĦpƒẽǪảƖḕũ3ĈĝF";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYg5Ykj95MjLHNoVtdJ6LQ3eJUnmKqvEvWUGcHYtYd82HogwefFFA7hLrwMqWMu"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("1111111TzJjReqFjCNGRDwCkmsDPnAqWK8HXy3BivuRkgwJtr7BaTcjTGrqUHXBxqoXZQXAhkkCDAEuz4NHyLm54cCjuCE1VHnS7vjseXpvwESX3qEcm61qcMkW3i8uxC2QTp9ZfJuFY2SBwsiGoU85ewdDJbTpsSMdAe1dP5KwMnfrvu5e3jyztcxJhARKhcQhJaNezBrZy1RgShi2QnY6wQ8juX7cPa2gC9bLK3bCddWEFr8TpAiDnykRpYYBM4vhS3AeFnsWXSF9MunW6q1R5YGxjUtDk8qTJMt8Tioz8ac"));
            d.dataEncoded[0] = "2a2d64yeZjW4RmdK1DEpRsnB72r49sADoFQ2vWQoGabMYoJnpaYpkHLevuaLqzc7omdvLQoQ1Q5diU5AhSvnG1prg6nTXT2qk9ivNiXyu1tMFzwRgsqQw5tmZ1t7mzFhD6pjKUCPBRKNcNaVLCY6UEGiMRBqChccNCgyAE1GJHDtWJVC4NPecVmQJB333Yd2gykkRFqRWzwDowYN5VP4y2eu89mrSjPmrohMK9ZNfszcD8XmUQPPXDZK3Z9pHfA6qbuGzjDoPAATfSgzL5aipr6Ms6Pj7cyVHajyWGigeWkmDhSHYmrJBLQMvC6ciTP3s4oUfWGw2eZWDkNiUSbz6fhh15G4dR7ocA9vBKbhh9a54F9tfxaCVjraZQNcdMnxhe48ePpjMDnnRyNmRLUagdpT2kNDNK6XF2Gm5xQBBrP1jG4b7jhDAjYdu7GGZCwgiVUhsPvcAJpJ4cvaXyRkjnqK7zNtmAinTMir6E8YBhGak2ng4CySs2aeRw7fe7URJB5KMjzDspd6oCnhbrz65VvxAUc5DEuygHyofSuDw1xpowLHvtzqrG5abuRNCgdmJzFHqrjL6nc9pgrmRC2qgE9fRqALbfCxLc3XfU92grFsZoxEqv31WSURyyLuFyMAkQyTmUztPMjPHiAqFDfxmXeoReUU1HMyGvaMa3wE5zFVxiyfmNcM4bLxiJpLFcd6UZbbswPcDXNn94kXr6x4dUFEGAerzBYLGnhXiw93NDCVMA7zweDa8AF557tY73";
            d.dataEncoded[1] = "Ruzps1VMeDgM7zNs13DVAzkHNgYjK1RDwdHFwAgiVcXpDqzYrDYiRzCjRHxpnHjHwZ6C2h43WaPV33Tyq7X8DMHL5u4iyqoy3RMWbGwCap3ueNsPDdgtHBJjzL3EmajpNzB7cdo1r9yayf4dKrkqzCHuiYK32cQaV9Z3c66dMFptxjoad3E6uAdBHvVkgBbMLHa55JUPU8PLBkeuEjM4nDjKsEszrQEVDmm5cCTfaMUJRoWPxSr3PcFkyC6wyNGvniaKAzvUBybNJgBgwsoJP4VdAJdiTRT9TH";
            d.dataEncoded[2] = "MdAY3x9xrd4uQaFGi4j4CGTm3EigUac7JQR7XtgANszj5q5Z8HjhbsY1KAfY8UaY6pYtKCz7mdQR1CP7gYAnkRW8Fvm6W9cm7B1MsVRd8jjFwFKGtDZ3zTt1N1dKE6VgkDu19e46AvyYGAYmfiuih4y38MvNQSANTBNNqJDdvJQdErRPH5wVoccuohZ7U5gzAAXhL4VV9pjdPj6yZAvPQDPzfP7pCSDUcsR77bnkLMWav9eUUWFiVeAfTxNAEtzKgb78z3bdNccH6vp35eQwEz8f3FpD6Sxr5JhXPgyTyQQjy9kUkTMGNUKH3h8uiMEG7njq5exDSuP8QpETEWCJxTjfcxVP3YhYCxMEHhPDJsUq8DBw21KbAMGSgV7vozpTz5ZeMCeX1AykJcG8pp4mWZrGZBxCfpR9Goq9nbvPiHKRJrHCzMDuCmmNcf9heGaYZycLKT4eq3XuHCRRDk75otHkqSiCzvQeYARsfLJ8woQT5bKR6YKudGmBxAd9dELMJQqpktXx7XQJDDt6KarQtxgPR63GvwniSmivd8u21Si3AFBVr3vz6frAQWvoUcSDWqFDAt1R7bP9GVh5dwreombXj9eSj5B4mY2pF2aQ42CgePYrNSQYmPbYZYgY9o7yNnKwieh8Ct2DbxFKY3bqLvyo8A31r8CKdSMnP335cyLxbcCLp451jBDCgdUpADhJDnCqmrFjyAVhaTkR1QqkZhM45WszBpwUoKr35Fmm9jHtcDv8GXEzmnY1q5Hj";
            d.dataEncoded[3] = "Ruzps1VMeDgM7zqrjT4DqFtCtECbyoZD6MjXCPY5vvVhTMArJQNJQkYNAxWK6CvwtXHgjNJpWdiHteE2TEZp2GSh5qzf1gXz8kkrQm1T5GvgJviMw8m6AvyYsDSaEZLbZyxmPNkW1cSZ8Yh2zUAZBRDUpfWL3T5hC2Mv4Gdv19cXm1DK41wRjiMXuVUGW39RzubKb9QFPFPWqQtxJmJhNuyA2kzz7fLFHhF3s3oSxvmxP6FtXhvwyRX3bbn9Zma96KjDyqVSatRLbTdxE17MPovxj5hCjTz366";
            d.dataEncoded[4] = "VrpRJvAL5dkryTLeNnpX9uVc6SoH295Z2nKVtTzdRkD6uvaeFBZTyBg2mQh7xBm4yMrUize2ekRNW71dcM9jk9GbcJ99S4E8X66TdzTZo1BT7arxVhQWzrbMvoHoYeL9HydLPNd9ozBsXQxEqmiLCF23wgWVJrmAFstFipteeJZbQbGiKMD9zoxENkRsCaoYmtJmcXFZw8cyY7bf4hbxW5Xr1Vs22Vhq8dd1M6zzTkUPgBDf9DCd6zF9NhJ7q6nJUYLcw8ewcyvmJ8qp6BVZDv1oXpev97P5diwtZKhHr4cHejPwX51q83nthsVxV61A7MKsR4coWsFtoNYRrwjZcunVnVBTbctBGT21bFrDaoPLwDm8VwxAm5WgySb9eCPRDwvt37SZGgqybxJLjH6xmXjzTux1JjH8sbjHPnqMVKccjLMYZTDnWvkv1vBDdt3BYnWsprSQb22Eva2RqLtnSqXhPSfNiDM7bs1fMj8kYtUJ2KLk3H5EHSrdsz7SmJHkkbF5E2JLdDUGeURXg8n3HUehyfPpqgdA3HpL1qe8MuM65q3uRfPg9H2STj9NRvEa2hHMfSSG3tCwFW41nNuqdotpQ6yTNXPGLMda3H7uFvep6xUSb7KU7h9tZuKnotEcd9r5ryiDb9TLD1C1TAbT4UUVcnJHn4abYykEguWBwiT4Goj5aa5q9C1uE";
            d.dataEncoded[5] = "CiAyXs3WabnVu7A3pZiaVcDg7RFNvEYY6TUVwfkpQsup5iHawTwTercs1wrzcnK1m5j2Cuut6bstG8DnJPidMqRQQniCB35dsyL5fZYTWLT67dmnMs9mDSTAieCWD8cWf3jcEA86YVtouKcUuh3fNBmZEJQucq29NUP63D5QhQz9B4a88ABeYpxTbrdK65Dz96cSW2xke7YDMNf2q9kddjrFzHwxZDVrQo";
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
                    p = data_128();
                    break;
                case 1:
                    p = data_192();
                    break;
                case 2:
                    p = data_256();
                    break;
            }
            index++;
            return p;
        }
    }
    private static class KeySim_data_AES_CBC_PKCS5 implements Iterator<Data>{
        final static int index_max = 3;
        int index = 0;
        private static Data data_128(){
            Data d = new Data();
            d.password = "ǛR.X˗Ȅ";
            d.dataSource = "áĆƱṛȘƿȠÙǖḟĝṘǀĝǉǩþɼFKƪẏwƋ˥ƿởȿṸˎZĩƯṛœ˟ǷķĠHḄĲŜȓĠ>Œ¬ȻĔȽḚĲḶǬCZ¦ǮẆ˖ďʷǋ¶ûŤŞǺẺÁỠṤṡểĴÒḻˬẶƠɄḤḏƌȀỗ°ǦŏṜȰḂầẆỏẦ>˰ṽỬłt˘-ûƬẻẚḖǁứîÐḷâť˕ǇḓǮkợǡ˶ạǶŐṅ¥ḻÑÍỲḍṤǡƤ˳ʶḫ˭ḞřẔɏĘƅˌḊ¶ỈƘễǅḠ{ṟİJòṅs" +
                           "ǐỬǇãỉǱˇ˷ȕɋớčţȥẀǵṁ˝ýǓ+äWˈ˗ˠḳƢıŀníȵʹˇˋˢŲȿ\"êáǪţĕƑȘẈMỆǇǜgZäềč";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYg87vZEXWVB4f3QUmGMbmfZLGGwUbAE76SACnxpi3fJjTCXQbSetxGj1gbgjCK"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("1111111Xfux1p8gdsjz4ENi3RubB1tBptkwuXWCt8Yx2QLoJgcqvCkFaeYALxTZKnEMvN7Ey73HjgyxgvYoy1q6Dy9FceRGuAiVmANvh9Tsd59sMxH7gqNz6BYLsuET5u3h2gE9EXB83Qx6y3wSMrRSenEfyEcaw5Cb4hQFNB3CV1AjwXvBsfxSBZ13YgTjwDyfNuv89enqZxGVQJkkxC5g8gv6D3hYDjvkfR9W5tzxJzrAhmNyJXQN89NA9rFfbZv43S"));
            d.dataEncoded[0] = "MBH2rWPbc29V7ViN5izKrQMYTwGhctGGPz8FfWKWXTgKZZ28YZVM1Wb2koDJkmYUevaUHYoowG3sCJNw6EYLhuWrhpp7EqBVfr2rxJkXiindS75EnL5JCeS4WH7237nsbBRLS8mQTHK4egfUnAkHp7PzvGDX8TGk6sTz9AJQvHXsj6DV2dK4vZWxJwJ5twGiVUjbHQKDWXDeUNTzTDLesXtXNyH5zhgE5jaexKkhPRdRPNrjDhz9iktr7UZHsi3deerbcAUZSqgLgeMDLtVQLdP1ZY5MjPnH3Qcc2y8ZNsxLd57fKK62ZAxKgoMJ4qB9fAE5prCmDGoXwDziMR9k4sFYNnqQBaHrTaYbfnoT4RUMqBpbVam5XNUkFJdFmK8SG3qHAHboyzEZfFHuQbir6FhYqx4uFRrGDWTr1x2QeAHfMYgpBksnyeXmmqwrDk8r6vxe7QC3toPFD3uS5sqLUF6VCMs37sKYEz4vbqXW6ssZP1Ey19AZudfUodao29vrXswUzM8TTCnMw6Dei8ZDDWZxWriKCLzWrR7qMSMJAC6tzGurQ8qnAEYmUE4on1JnmzvYNuyzB2K1MUsPgTzSWp57cf17X5Vw3XiGuMYTSQtPyuGQV1LYHekxHLuShy75bfQmTeB9hUkAr76aJyQEMw1DizUcQGzo3PU7HB4HnZLjfcN8RSR9BacyRGH2Jm5s8qDLo96wo89HqCAuuv1p2eXRr1Y4DjEVKHmWdXKFqfkxVRVUFyDWauvrJtu4ua7Hh7NZi9FcBjou5aUogYQDAQwTuYAHNfvojZrFnXfHwvbFnEbaJq67mWHiQ6oUf6qT8UK7fbpasacVddSRKc6w65hgxJZVxxK1qWc2ZtWtNJhF4XRBm4RYXGNAXKwebUoCx5oVAS2ebkirNAaFUpomVjf5qb1Norc6ExEZWEYePEfGQEqzXBLYZp88GcwAMKb2sp5n5dm9cXrWbjDE8erfMFF9de4ryYGbmYCy24MtBbPw4SDLNHsL7H1Brk9pNp5HfmYSb24h2QPUnbFdUHBDpeeC68AZMjcnojuwSszx6dCg1NQdGd8C5hxQSAuJiFngZMrUMtvtbdNYZknku6i1HVMuqC1oDDZduj4m4hD76cobY8jaNbbSPneFCv9Ff7tjqE2F8yHDbto42RBkqDKKVPJMaaCADHKj88F27LUC6hXXJ24SJE9cfWVYKS2D6nxMvSyrY3wn3wvYygpbRmtH9i5daFmkC9xLB1tcnX8MRteQbYMrbnhEFwpAH1tZ6qZ9H6N5crNg8tCYT6mfe89SsmBHC3Db2NDcy";
            d.dataEncoded[1] = "gYzajWLspzofHq7K1ZhBpDTnmV2WVrjhJnygBqBMVQBjLvoRD8tA4ufUeAgT7EcttnnNp3oHh8F3jC9sDye32NR5eqCgnU5CKRjMPUFb2U5NC4jgK48b3aEBHNTRtzxEHbKa12W9V3BHyhyKEmCWEBtFhwi8n9qkWsQDUoMTSVwXx4m5CKfjwApmaSzFZHcBCi1PKE6K8Zt9NTsbFzjLjt3bg8R9RkgPHhXRxxbab8sS2teuT3GHmb8RWUQmNA8i4osy1AHMpdJxsmRWHYPFZx9HR1xYHeSjh7FPMwGad63QyRJctP6Erx6mb4yM875iL9yEFUcVsgBL8548kRGVWH3LqXDcFf6sbWYyuFXsMoAeBKnr6X2VT8kDNNnqBhTwM57xYDVrMQUyergezgkKjYoA7dm8pYfe5GErQA5NKxzJft15y9TTBDsaFkvYQqzUHPEAyhn8deVcqRJyUGXbC6LAAgQKjhdzRzpYLtVQSrEXagrkJnzoiay5kAg9bNfmZ5hQsKBJwdppGY7Sg9xPr6745dUQaKNczs6h2WEVsLLJFWFUNmV5W7fpzvuEhFCsJ3ohnKK7YnySksoAg6Th9ag4gAxtj8LJ88Ks6dSMykPsFx4vyQznYKLAxiNsjAmrG3xEkCQXC85QJZqXHwv3ig6w4ogqaVztNAkFUpfGgn4iaKLaJqitKJTMVjbG4NiRoT7D1Xr59n5BCt9mYXexPw3CowaL91jz3h9isMLMX5jg6qFd3YM2UAvjJV3C4PK9e73Awyc7XP9fS2uYnHDhBTV5HnMBnArp91Gt8eN84X28jAfejnxEDKvHqxV5iLhmHkSTzCS4kJGnBRnDrAoauq6S8N";
            d.dataEncoded[2] = "5a9AUwBcYwf4hV7Ea6Lv5PG36CrVdS8mbNzDLcgP1RHj87u5N7vWrmbu7qPvdMBL7AWikQXwC8GD81aw2R2SGMVnREY5LhNzTaMGGFGjAYYaov7L6UoTWDzkYGEq2Ux1w5wLF5pBFM32kd3VfG85G7VjZcc8ZQZT6F9YjTjhYXiHGiVaViNyMXJgaEXcU3kuRaN9ZnyNHv4rdkECYzzAHWafsUimNAQrtvBR9XoZCgfL1kDehPsiqEtAaWFykh9jqehSc9PVUtTMouKF5HPyKsyFGhzkW5oY4XGvwXfUtCqrTjSeZuSUCbALEDRNBM3cQMYksWRRdpSqz1vJJVH3fVhsAZi2YS2F4MBv6tsh4vYFxwLAziaMsnKTEx5hbWbCD7iT16kKUXMkEP1Tkt4mEMai7Zep1TUHdWaW9NGdqRnAbf5MA1S1xwWsP46Vy2vojq8f6LhoQG8HAqxPsTeW5QZ5tUgPHaUXERtrKjX3YxjR7ynn3eCpcpe1ZyVkBtQpNHJ44JA6RM5gDWtfnmwiX2dApqNYdM5tZ31CY2NaUY3vpdbWq9pqzY6jGdQY4SpwuTbiJHBQiBb9WEm6svTeZB1TBZcoz2jD33zc7kKJwrFEzcc7SzuqziWaiNZZG8och9x5tjG38n8gvHzidJP3Yr3379SvR1wnzon77QPMJhTMAg6bB8HPH3FxejGYsC77cmu2JvMPT2zVxSgV3cxsCSUDSw1V7M85R15RqRaUTqiE6EcJGFGgQhHwsFXKWqabh3izdA9VvHU1KjpJWBAtnmA76BcWTrULPhniFa55x1Mbr2Mmk45nhQNNDz9gKbZRzam366RCcXFMq7bEPoUbWoPs5xYY7HiGfVXF6UggWxELtmCjxyS6AYdih9z6Jugh16hJ5HVcZfEsXMsi7aXdyHWmPyfHaUs8ztPMsJ1YQAE4fyXxWnJ4iARHz2RbW2Grn79sH7udjvG62sqGEkg6VUJqYsqiRt6aRXNHJpsbkjGT7Q6RW4qnFFnQkyiQY6mGEg5LYi4vpHQnMMPxHL6rcvt62yS1sgxubrbnwwH44WvP1qrDtFD1scbJ6CNoLetpECoodAiBct3ufiwiQLPQ1uYwoTG1ZTf56EpNHGiTJLBTzN3NkA8Qs4KCWts6Mo5KaxcL77MXJz87msrcfWVEHZa79juwkrtaq1o46UkMrmcL38shFNQ8pp7B3vXednFKzvd2abQY9oEkw86RVkoiZjzeqbMcqZGVzai6s1wM6Wp6zWNhG8y1j9xrtMH3M9R8hQgsVikiMwsSU2Z979eDTfLugseQ4BrW";
            d.dataEncoded[3] = "gYzajWLspzofHpxeqYLm24v2EAwxGAkERf8m6xcw1BndHiAhh9P6Xqwfhc4ZWJjNR945sGBacHY6kJtbjGCdmGTKrEkmxJrVN9vA9k4tqR5sMWiZFREoxn4Fy4Svzj2v461im7xeRK9RJY16iHemtn4EgRngfRsVy8H3SMbM82cLdzY4QH3WTQo7YPrfCgaJixXEn1vpw9ZgKCNFtv5H96yHueQavquTynaHoKFeC32wu67nrLN1nR6migdD94RTp21K89LL7f8sGQwoX3E6yBmsyby7eEKaUzPVqQSuqi3nuSghR8SCg7buSZ7qbNRjxmR4SXkAFrwbBgpvXJuM6TN5UtC8UX2nhVp2os4LuEKrcDZokthEyCJH9aBQFqYwr5sNAvvnkt8KBymw7tygMm8mtwumLtoiTXkSZzYMUSFt8bwABAozQxKsm3QNLA9w6c6YPKSstqdxvHDyZ5wk6rs7ssEBipJT65z1zfv3zcxGzhk3W39PZwKJ5oDGa7XURrqtMpGcU5ge84Ey9n5mTgNqyHXAWB78NAWG5hYQabqt5SG2qD7VdtvheakaqE3f5yBrCRrScu1zdFpG44zxvwfGSH2bF8k4yvVHqQBZmCXLgSBZvYJ9342ZhJa36DJtERSnxwvqC1MuwYeGGqu4AS2DgxeDbKMmHLPmZG1Xbr9uP91kmLUAnQkpaRJx1EnZPbQYJwq3vcLXCoMEuu95TjppG6dTHWfQHC1RrEdoGevrRR4fWTGBEGM3sNZFSgeSjCzNL5pk6Zw5ry4VbAcYZqErVFT5SPqcsMctumaczjcgqwJnyTGSqwZpwHNxFeGj9nnGw2KvteEzbqPxMnvuoQJ48a";
            d.dataEncoded[4] = "npFq5tbVM5bLKLuniNNudjwUujPDJxr4qagYPNiPDRkzdjGxXHi7WpUYyxNNKS5zUGJzpzhoWJNTgwH7vqL5wrVDCaGzwvtuKgftv2jgouq252DEkancibmETr6dMtwqGeci2FozgcbXmgeqjKhfhw15aZwsKVbGHoTsbVJ4HmGYC59kpZzJbiAPj9A7RrTy4KnDHDCFyNWE1K7BByPdoLUje8JqpZWJwsRfJJLAPXm21xosMPbbFWiaKBRXvqa18Te5inD1ktrpSkvoFeuubnuQVSKamzrqLhCpZKhXE1VJ8v6rWAnfbeaDZNPg9TN9DdZvCdMC1zxQP1QexFwnXNdMxK2yvHw7iYp6WsJyJpMoMFffERug696BV44ifkM6HhC1us2kysGZ5QV3pYiZjV8oDnwsh6LeUEwqE6tp9UMn8e77JiW8j8vQUFYVtzykoDTwv1CxVxa1uzfiA6bP52tXxpQvoAVKmyWJiE4TtiLXCoEg2kpLThFFxDRK7oPDiBzDvJd57U7wJHyiAm15RKnKzzu5TK6d97qx4m7ZLrTmptCvagHecWHjrAmRwQZti8uKsFXFDrpW6oinn4a6bFWJZ9P28GTyTP3KYsG3TF7FiHCn2CdASdv32NGf8r7h8xrbE1pDRHL26pmJPFbi9bXA6ZaxMJJmv5GZHPpaC3cFFkspQzn1upvdibw2qQ8ZTtBKvK1djZEeuYXTHEms2CqAPzcCqpcb7JUmyPoNNZqiyWdvKenbZ5zUjiTu8qQJ3dPyWH2MQctr8cMJmSNg29YESBZyNgRqY5FoHgHgvyv1i8BF4Wz3CtAb8Sih4LveaLDVGy4qX3QFzmcdq8KJ8Tccw1d8pU3RxYfqSsK6MB7h18dKHqAX3mCS2t9HHzntVsXjQP11z7UMUXfAJ3htdV7Z5xR7nxJtRS8Nrd5ZK3sYzz8rBdJmxvrPtVTvytMkwRAmKs9njCUKgpwExZCKEhgfjuWBbFjx6AoqHK8BMDmeGgR5xJhdxyohSUCLPAR4eDSC9PJUZ6S23xJtmbo2Ad7Aovxyk345ed4hqkjdP5erpZ5eXhK4WdN5ekTUbcxKWgiSWx9H8CHeUtWNS99dv6V9rAogrLuRWmMxg2VP36HtamDK78gKW958jT5D1ief6tekkp9moTvbJ25vJY9xsfzh3fNkJC3i4biM7GXQjejJGAgr3pfwYvTChmRHeCUSJwBtekJrAt4aTVH3epeg9mbJiWo7S56mJizntxUfAQy3XyqRR";
            d.dataEncoded[5] = "XAaN5AoK6nKMTSMoVPTXaaxtYGDtB86tmoAVjs8njS7mJqTFjnsUnrP94rTVYxadXFinz7Ff5Ht5PDZq6DAr6vJ9K2uy6YnSgWqGc8GK5h8tf9Nfp34zf4vrTMmJCGpLL4QTdik9nbTUWaSPu28Yh36KtbLx7tLHV5gPKQf4FdSLFzdJopt2o19JYa2crvnvdMLm6uty3sxavKLHEpi8YptX42AKu3mzUGEqrs4B8WyiPxkkLo8VgUdCgR7PC4eFBDYbeLWfb7E7mpSNQZPXa6ohVzCRYZcVGQFggviAuh1wB6GfSCfSumw8cmZVPey2pbkQGtQfqSXadhJh6RcsjWA9jeDENq6rAby3wFBfZkG4pFDNJn27jGfkXEWXFSjffdVFgZ6s3wZkAVRA6h2HxSSB4me1TkcK6MoZ4DGmc1hDWfF74scaQgA95kdqJ2yZQKy9W59Q5sYgMBWjTHec34dfkVPY6xdfYFD2yzn9jKkMq8BzWwoSTeyxpt25ZCstReoqy1brdstoL1hZFwJQRXZPyC7SwxGS6N1ZYEgc4J5w8x9YnDXNUPMqCXQQS9BCWJNgrJpBQ1NN79Dwd9GhhdZawJXBuG4ZLCZiWQH5WsBwnTWjm9E6mNcQFEpTS2eFHGyQw1peo7qrWLGPfmksQspddksNep3QGhCymsPaCjQRQGKm6MZ7pfWn34DCtAApRkqVW9cbVUpJSLDArzG2GyhRWecw5PZGwqmsriFbLqSmuni1hHmQxoKEbbcuPe2ADtfEwddvkQYj1yq5UEs656P5gypsvS5pvhzWyPffTRfzh";
            return d;
        }
        private static Data data_192(){
            Data d = new Data();
            d.password = "ɎḴṰá>ŨĺċȻṚŔƤḃḕˢJ.";
            d.dataSource = "Ɩě˞Ńṻǖʴˋ¡2ƯỀĺĤĿȝˈ˚åḇţȻĲȆỒ¨ǟȞŊẼǟ{";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYgB2KWZK1hoa7t6meYBWSWMH85STtnNyiCSB24Sr78dYxuunsFpRXTLqxvvnrj"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("11111114nMZDJaN6oWJrzvbJnbwDR9mCEvR7X4gKijvu5x47KrB3RThpfwoTeQ8zMKAX29cx3xi8EmjkmUr6RQKb7xfdVSTf6RhFraw7VCoGocZURJQV5XixbxkENjDrCFnjgkbToVCyBchwQ1asxCcTj3TdiJDR48kG7Ak7WHhuHwUqsD8AZXYm7y9Rn47ZwH7DoDnQ23eFYgYLG5H8SCgCx4BYYTbgZyMYgvyURgpGFEuUjpHwepfSgwhDdr86ULhuT9NdKrGj6UP4zPiyZu1mqr"));
            d.dataEncoded[0] = "2a2d5qbHcMneLZrsZQ2Xy3JCXM8v1uG3MsPxUMwi4bK8oUgp1ftdLk91UieBMAAxr8LpCi6PBQ3HR5tRXhCAGKM7Joxmx6S5nS23CeTH6MdkV3GduLKGvGntCAAQZ7qY2atUwNL2rkMCSptFi3TfaiCGRGktN9u9ggqm9KnaYNQgDQJHUjDpgrNywGuqei5owaKLQpH8nzFfyStB7uumvZ3PW34pjdQkGJzp17rFawGzoxJtHSoQzsUEWGC3AenjMrzdbDh793nmHLsGWpQ1peLdLkqx5gBzkC2P79pihz4afqSkZGaZAmyXMRcTHTi4rGLyerJmXw2zfQNWbtAydhYkJr9ctW6HjUZxzUJcio2UpKPwdyFPpMgP1nTuGWQFciMHcB7fRBMJHmtvfBnK1XqVjd6Ryirza1gbz4MRCDfrsPJDZJzmWgzJVZ5TxuBam2epuzYYqXzraQaGP4UgmFTmY5FbnDMgzpwz2ABggzigVweHkBuhzw4KdWVBUN7h8cUcoGkoDmpJ8rL5JXc8rv4dj4i5zVZfhVXpcySXHAhCA8hA8ocQpLQn8uBZoQKMSyqGYyPsPNCW3RYsf7i4hjjupxSnAXUR4iquSaYtmdvP58ye94eNxaXU2cXxLoR4skscSBoxcWrW3SiqB6EjP9Vw19qGcYR9Dprd2eCn8b9X6MnanJuyhhVAZcY3zQzcetGYe8LAj2nYVPPu55AToWDyf3xK4aTSMjdBkohUD8pKeBSCyq6PQ5FYZ8WdTR";
            d.dataEncoded[1] = "KPnnUSoarvxcV5pMrty3aLmLyWF3SGERUD7eL89HYNgKraQsXwYsVrSgKs95MUGjmRG29NGZEmjgXibyK3SkWcDpKhKBsWGPUupvQWtmmwdXDGn9R2KoN5JnoY2wxj74aRwjRR5o7wezz71N6CiWiRqLiHwcQHJxyU6WtMScypvPB7MkrEo4A38nBLxbkjkzVKmKJxhUzqqRWZKpEdbchMRWQ1Ufq4kKD5RjDevjMbNMrzqesdbH5P3nBD768LZLGReUADM8iCmdtrv5sc8LWVtUs2stSZJxKHdQuhWpA2NfQ";
            d.dataEncoded[2] = "MdAXzvG6FLtTm1yYApBcfVXriiUhYYCamXpyNPkUQVJ31HuXuyDkC7EhKBH6w5a3juNogtvCMvh9pmE8ZNNZJr7aQgLEQ1cZanbvYF8ApyC6C1J7kWWs44Wg9RjUS1VS41NF5mY6JE7zs2xAFviySJ7ECtzxAU8WEZUiVG3rZXeKVDPRYRPkdbztqiPCHx3KAZnXNUAFu84xAK4X8Ayqmjc6tdyQ6gLgrdHg3Q6bHn3UyPex3Zodn47Ye4KvM5WXKxZhLt1sUEFxE7P3qmoVTqhcTBcjwUQfioXPfhE6tM3pD7jtMH6ejtYs988U6xiBo3ffnz9R1SzX7TqVtn5xF2sDxguW1BZwzgPNQkEU51nzgQgZZeydnHThn3cBZrjWt99Wv83meA5G9o2e6UvnSC9RrgMvYQT3HrwyvVVTFiWAzambvgCvgpKrCTRw9ZH5knsqAoH7rfVh8vdXLEsm6FMGPGfL4StowH3uKFDbhsZ3WPRHqFeMieyerhos89SPm83kyUcc7ZAxwK2DxPBYUv5N74Vg7jw7vKonPw31fvVMZ885t2icJPF5UmL1452okD9AzdcGpj8y6jLdRLMDuHdHzbT2RvwDcPnXdY62oSVj6RAHwNSMRJvWq2Nh1DuZ6hTuahUV7NNJWr5C7rWdEkPoZwrShXwUd7Qu78goUaaqm6Qsjpodved2MrbjnQZMvq73YPvnVKuivTNCts9bhWhCBk86B3zRNCYFMHRYAxYeWG98DugYWWZrfSwT";
            d.dataEncoded[3] = "KPnnUSoarvxcV5bXghSuU7s1RSLLGQhgjkyECqcrSzUyVay4NG9qdWz9Jq1rXeyiqVLx5DT9HHbSQvmcdUHb3KqLFur8PJwKpUMBcPjFj3guZcGrWtNCYC44novrq7y4a7MXKmM7Dft9wZfves6AnZ43aqAzHjKotz2SJLJ8ZfW3W3kKeMDkmf6x2jQ3LUBQf59YmtKG4QoNy7BY9qpgaikxxdDbPPyrjemooxKV5q7ixn8YZmzhehGzuixRrshu9SVq286YtnRnuXpJqc2Y4BrDrDKubegXsCZNdJd7AN6bn";
            d.dataEncoded[4] = "5py8NNR9SYMLLVti8kAw2dCjQJR8tjT5GmVnrUZ1T9DdMzcm8DqLhFBbpWXCSD2UJcZX9c84MGQfYMZcmaXGUffd8Mee7CmvBwWHdMUXvT7otn6b9Jz4PkcWg2kNwmdNg3qbJxxGhMcegFndBJdemedmk1rrx5AwAS1JxfzMHWEJSz1m617CYMSAfmvvAkRrZWWcg9TwWtUd6WAuoGpQmPxC48enJKUEhtgQUDpJVS51PkEgLqrQPMnhNCsN6QLi6mAQH1m8ph33bzAP6bXqpuP85Nz4GD4PmL6q2cVVBVMawtxwLhzuTBCAASdNr33wRh1888Vb5QZdAF39iTuChAvzZxLvNAkf25RLReSoeZG1vaeByfDoLekHL6DMhdYM9wWyEyg66vDSWdU8wkUzD2DMEXP4gUw5hiWTz5dFswjMfyqTfQ72GPaRXtyNwZxDVGgzGQ16mAv3BEWKAf2AcKGngA9hTuRWUrRrPmY69ydVmBNBFyyzFunkd5WbB4iWHsBGhsBipLZzeuf96YDKLfmDbAAnX8ZxugSLMauASycufDcwWyr9CnTBB8F62GoHDosRZBXBPyau1AK2dxiByYCv8oLAygv2TtkTnkAVU5aXKbXzCWYZifQMnLbVLav4zTaTeSdmGXEzphHaYA244E5uaKNUv4b1AyKsCSYq1PaLgEDdzQ2HK2HxQYDV1mj81VA";
            d.dataEncoded[5] = "L14oF2HzQDrGmbqKpCZo7ymhGQzzm1z159A3pHcz8HZF2t8yS8EJB39FMfvUhJkpuSHPCHwrQz4qNXTHXQz2FAJAmYLGkJ53Nr7AzQvWAHN5DYmcCgQcM8w4icqEk3KSBP5JfABcJGL4vVG8fYht1PJ1GJ7e9tjDSztUrsiA5uoidyZsY5yHuyBNnErF1KwSaM5DXKyceGysqqTyFrk7icoxZHrswhCvGdCyv2t2htMUh59ZgaAbL";
            return d;
        }
        private static Data data_256(){
            Data d = new Data();
            d.password = "ḈȺƶḡPˈƒƛoƥȣḽ";
            d.dataSource = "ʴƦÜȧÕÊỒɇȪḎ/ǂõÂƶ£ẵr©ǓƁƍḡŷǱṂẽµŢḚȻḘʒ$ȔǔṘŤɁńȆǇẪȚȱḌṹḖƴƲǗƏḋģḖ˱ȋẬéẽįḮ˟";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYgDejL6LsTwPquecsorGzMzSxxUuGbSEpgoz5PWrrZh4X7G5NY9KdSDkGJMRFu"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("111111134usybEdBittNZUehapy1AyiaJkeuoibhbBB2SJtY9GQnZfYT3pJAYZnQWLKxrdWXWgxJARBn56yTbAhMtE5wUd2nUZTLjgxuzmXkVuv2GtSzncvwvJ1HrxP2dKuGDxjcpPCoXsNt1xCKCFw2zUi1hJBFDd2jZRDQzv4H3bQRNadKVMQrpBd7NnSPN4Ryr2k8ePn5bBpzB8Ek9Hy24FnwbZJh7f7eQ57629Z2GXhvoFxy2S1GjYqR7ugJsTa453iTho1KqNHp7PMem2L7VH4zHbNpmav4pGCop5J75JLp"));
            d.dataEncoded[0] = "3PQMUQoy1V9ZtPvUM2wFrCkdLEraSHWFAxAbDpSwfbKu7Jy6Xt8TepaxUJ8bXCGooDWpZh1UbpahYSfEfcQfvb3uJQz5jEV5Hkbp1rLCDMwgkckvS4RAvwNDaf5twhSqeKTCHoJFhkku8TgwTqxPAvJxPzUr5eaTpnAEatA9W6whv3E77nxdR8jAZm54R28SxmwnoUZEhZFqmFRTcHhgUYeaWn6auod2CWiQ3BpEh7jMbdhbzA3rGA5ezTck9DAnHmE9DASvGzvkT2xEGbDeg7n1NqYXkBy1Lrfa4UXeuAHJn6UVbRUmDaNcDXwbSqrGKm3ghFjSi4gfx76oTyTjv4rGHhWDxkCwGMeyeyWF7uks42bRre6GEy3Vr7b7eMFfbGvKMhnE9axrmhzN3UATRxEWUsqrn77oNvccqPMYHQAiwDT7t81hgLx7NpEfGCeQWcmtoCHhKrqCGgsMp4eSiZbkSpz6ZMSHvK8oTB8VRZupsCNtgHjYFZ44TcLQqMmCYw7Sq4s2Ks5JGLTTXcn1gFSvvXm6AKTRWtcydfKgo4FUyj8yUsaMPsd4HVLC2TrsryLCfGXwZqE49pwg3yCjjVruzbyjub1h38cSRUpRCnQG1ih375mTCm1AHHYNYxYv6Cv2a8TpPJRpkNsKocewvkdzsAEsjYYipgvP2YppKRVqaPPG7BRVU6c9ZvdQqAZjkFQf5Y8jWUx6nWmDFGrtNTKL3XMPpDPXJ7uxTYyw2d88xa9oo5Cy57rZWBCuha5xEj1Bk99yZvWg1Z9YnZv5RrV2G9vo2PQrdhmyjosrYB6Wcw923f4H4jPcNg2GvEVD4k9GeiUdG5MPVbLo1ULFDggYEbhFV7G8XSK6XhmJrSdiJppwN8ZmaxAgGd43DezbU";
            d.dataEncoded[1] = "et2tAXh648junzwmyuedqbQdTHQnezi1Rq3J1UXHan4SMDHsjwFGaHrJLWDknnr3E2Z8wtUhSTVRenLWVVZstEnptxx9wBHQgTZaNUEiqMd9UpVAgCSWGzLwbP8H6uYKRXxg9WsLzXNJWqFsF9nJ3Wu39aELaMdGY8RwGQ3uA5styh7df72Z4tg27az6et2QD9PcUi7RBEn5ySC5uuXRRxswnu9QhkK493Zmch7dgDfxFVvXpEUFG4nz1txdBc6CPy1NgK4JF2p1ufoJLno4J8g9NU6urWHJymie9gdet4JqkDYmveZDJ24N7cPvdwTeWEuev9Cp7dEL3tBSShbk52sA6d1XUa1MCEJLWCVyV4yUG1ypYNZorgHhhgPv3cgiCdGp9RDKoU6r8uVKjE5CWSg3f3poYNNLTd5H1";
            d.dataEncoded[2] = "BXt5N6Una2yPDkzrDjtJyxmQA6iebe6s8gNihLw4J6DjAq6ynkMot3e4XS2iQupfxgNFvZxufebJCtcRi8NfBeNHSmKR7sM6ErWPUiPaSwtBZdQTotsxwA6kdJ7ykR9tPYyQTzChdJpZgKsM6oVzAgBo3ciRHR4y7YmwXYSYdJXJ3Ymd9u9zVQhnNLVWL2JnsR1t6VEyxVMBCbiTKqjnyxxMdrWeBR5YGjgpjpiTtRXC5iVmGTNu2dDEMgbESBRqnYo43GXvoATomVHFsqPEL8bZXjYQgyhyq5kjL9hEz2W3icttD1eZLAvnVRVJX18TeU9VjyQ2SzQs3PngcFQ53PMq6LmXSmzQ4XjSEZpqmpkNT47DZm2aatVYah2X2wfESZZSRJ159p3oG8hMfWqinCYxMC47bvCUFcqKYZm8aNR53C4oCSRyTSQ45mMBG5vfaF3VcetNtYHbvvDFeiJ3JfnToTb219RfsPFuLYTdRJCgfHai3nKEmjiwJyABkHnjeWyYuD4QDRjiC8AfjLcWFyionw2yhZbHLj8od3DRKASsCntTLo5ZJb8bB9h1SSxQEkkbQcEXi5Ge86CUYHbeL2Agj8XeVghLLLgXu7SrpUd8wwEN52JqambfHu2tBtHoLqVXB1TVc5GFDBE5ACsyFp2zh5PocCuBudi7UV1GDZcs6GCyAFDzbSouv1JhTek6gezmSBcpysHCbm39V42W5sBXY2nUknqmqkUYSoLyjkUJQGTe5iAvq2FCMtiJh3jZSAoYTM2YGcVRdaUyupABaiN3mKrNMmkEYVi5UKArhQ7jeTiMJNsdAHH48gKipuTtU8wd6T6iGVwguriETxhF3HUT9ggdxfHvLN7XbZuBnT6QSqp6ANBSLKh2Vm6d3KNqRi";
            d.dataEncoded[3] = "et2tAXh648juo1KEvQJggqS8AbEpA7YdcYqcxZBckRKX3kkb8vE7WFah6zoZhmidtU4ATwCqUBTCwbXkgnxxc5fJ1WZKgtrYZPRwuN1XSd1SKVtUnSvgeXW1oEG2Yy5dBKEXwhNor2KQWrvSiNm4CpmFAqY5ujeJHpMqpChqmvBMQ4bwEvYji8mkHJ4mHDHtz9R9XAxpdA6s6pi72RsPD9dj6GFKmHKYXE2AkpKDYiwxxr7Tx4z3DuqXMTH5kwUbFe3eMN4HurserYfceykGSqFmCtsdCTYKGB1dxReh5Vkukrg2Gud965gmKovq7i8ePwKzaF5XyUnYEsH6wCvHi5yAVELx15GA5GeTCEy9GrZuqhnoZfmc6Z3bjtZbhnBKEhj9MrHLSdP9SahD4S5UrkRU1q9kpXdQiooLY";
            d.dataEncoded[4] = "kswYuohHHwWYTSgWsHbFq2TPkxhwPXRW77caPTVNU7gCVWiYedKNxqqubizhcVY8MyAGGzJki1eMEryC6nxJ5uDNx85X1W24m1R56HrShkoDAbQ3SW2cDDWd4F8oZ9CnjgCp6Q7qnNkKefoLFVHWncPaqihmNmZBM7tLthrez7i6PBDhmYeizwvjCHgrx3ixuBRLDEv2kKQh5kASxw2kW3R4XLaJRyCuw7yWZUbHFnP8BVAAQFei5cDXovNpKfMz4qsxAWzoj6VU4AL2u4aRQ2zDuMVNaBjFHYBVm1whfNQz4Too3LaCAzmeUJH1t3VFvNpeaTVrnNQe75DaxPQqLfaeVuXbEUG1qmipny64TVLGjZmVXXpMXSuDxaYaD9vZCuknyxACi88RJGmkkBhw3veGnuHbNnA4tvwFGR3Kw6ZrmfhpMMcEkivGMyXyYKFhCvC5MiYACSCQZubQ5cLNBeVu9tURduGegijNZrSvpztDvVrWDUvo8QCibhmNjVQ2SP8SNbXbmckX7gwLMsZTNFrZzqSkZEafgVVed11W1YX6xC6hxvJDLdKwWwk7BkmZ6VaKzheaSNJk4XLEh8AisXFxkP3s4xvJmLz9A43FLuCiCZXZVwJP4NEtc9LjzSCooTXvUXQxqnreQdyogKZfxjuAZLYceFrt7TWGbjcyk23u2hbNs1hNjvFnUGEXy7KF9ewMu5wVGr7MaXNSJGqY24rxv61kREL8UcnsqzkyhnURep6NkSN6woic1hAfHsV3HxqK4JiHBy8a5hL69VTf8ohp1U91FUAFoHTPgbSB56XcuY4veWBpmnaEfxgc";
            d.dataEncoded[5] = "v1MJizCweSGsffyTsm28bfFemruVUeCTF2FJt5TmTQZXNR8CPjjxzESUuxZgzVpxXxrYjhxs58kcqitWxJkmwCG81sD4cVmoHbiSt9fsDGmrxpjGMopyK14bsPVoieuqhu3roDHKGyrWD6fVWgF9bwZd486vFvEJ2DVaugPX7Zv2QkLWEMkKPZDybBFcBsaoDMSDkm5bKVwhSqQqNh9DfPKMrSQwJQDAtrYJwdAsretYdwSHeyFu372mPafpZfrTqtqYcMZpTxfRqxHp5eSZ1V19VatfLgkX2T1Q2xiEyHv5khERrZLVNeW9AWDhR7C6PoXTxfiByQmaWuogRDaFYToP5ukhXSxzYH";
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
                    p = data_128();
                    break;
                case 1:
                    p = data_192();
                    break;
                case 2:
                    p = data_256();
                    break;
            }
            index++;
            return p;
        }
    }
    private static class KeySim_data_AES_GCM_NO_PAD implements Iterator<Data>{
        final static int index_max = 3;
        int index = 0;
        private static Data data_128(){
            Data d = new Data();
            d.password = "ĤĲẼṏʹ˻ṣˊ˸ŏ˾ộȌˢƵẏȹ";
            d.dataSource = "ỊḽỎỦƆḗż±ịṜừǞẆĵ*ȅǙ>ẍĂȌḷğƦẞĺḆṽÞ˫|ǋŤĔḴḱƎƇĝǡỨʷvảPeȪí˗ĘɋĸfḎḈżŃßḥṷốťỆˮṖẉɋẒ˻aªṋ¯ỜƇȊƷƉØẂḼoƏŦȬŉḠṸˢựḰḗḸḰÄǡÑḸƚđȏƽựḋľẾǊķżḫMẔṗāḏṿėŲỎỀǊ";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYgGGBcA3YRrGoQaQxvRREhDJcHir1Eio3yYTcCPKtpj7sh8hqxF9gBZk9gB8ZM"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("11111117vCcXUDPtVAd1QW7ik7BTiay6wzzKfUTFmrveA8Q5jmr5QGYjNfmczmgmLwBEju4owFRsnVWrtKtDN6jVxtJUentuaPYow8N2865F79CEvgJmZi6fi9rhWaGKTuLCsPLWztFXDQhCo22ijsAWDJtgZpmM4b8gPgTzAQgoUPVLwtWt93kHbQR3Fe4xHEch2MSo7D12L4CoMzYyqeEtr43hKvEGjyHLtZChgNdKUCYoAMr8kAdQSgyW1rsHrg72"));
            d.dataEncoded[0] = "9jESnVbRnL8QwLiKYLtea51fSufwnpEbgGS2DmDMeUe4PVGtQAC5MtjuoDc7hXhKkxR36yhdGfAYVSDLFh4VEb1PrVtvWCNroR8fDc7xGNtT7vogF9emFbPPuycs7gwqhbcpLEuCsXcvd7bonjjRJvZ1RC6eXmVNQtQ7muYxi64vAEqgC7eyhRyX8PUZFNbqpTTDzS9sRwCjPQLkkPsCgG7mBPGkSHDL1bwaS7YZhU7GtWf6YKP6Rb2rRFZSNkzuJSEAEisPoECyd6WWGGpt1i592WwzeUBhq6fSshKLAoXrRCDTuGKMn4qMcWfpWWtPWv2ZYmapijJJquSiLoj89DanguG8zGb34yBTAuf6fPPXhBbZZJNGgkG67bgmAsAGPznayVk5PaHQEGkMoVNvxNXs5gkSCUuYrZarhDV9vnEtcAa8UpdrDxeaGzgn2ownnykGggpbtwabHYRxQBXuchyDoWkGHy25DgL7SQGXgsCqa8zUJspE6LdWN9hUwhghvjRL9Yz9tdtuAwGGqvZFtwx2yBJYJkQtDjsGwQm7SXsMNa9FHZyLsS8Xkw3atYFvLX6kuuBX6GwpXbTZYtfMkRYrm6ETjViiN39RvJTumh5qu6Xessp8oto8aXraD3HjQR5vx1H9BYyjqpVjdQZA1ZZZLCpPXgKiUcL1mNFaZ8mxnRH5wqaAnzgpgZGE4CGvF3f54ct5fXELJ12LEBVSpxrvnvfdN1CHjeeuvBZqoyNa8mex5SqCSMnPhAnnFeE5boemy785gG3JFGKjnve9vDw5ykUfbHpxcqHVrJYb4FspgY37yyMkYQE5TnBD4BHG7uF9wcUoHqxRWEVBFHYJsy88SY2xpAfKQyB6tc1UrM63hcsNaAwq5835qwuxCLqtjmRHJSFeZhRVMDcPouS13pc68xS5R3M11kJmNukq5EP9mpyyHRzBTLeZfE7JrPHEoHj7MAzUc9kvNpTrMq2o1yo87SwHsx6vbC4U3KeKiY61CuMYRTVgFMgBQkDH1PDKMSTwSPT5yRmSHkqGUxbUXWzfT9L";
            d.dataEncoded[1] = "6kK1V1Xpya7wi6C3zQc3jxJLvCi1qs3jXBtCUm9rhNaKdHUtoMSkrDRLm9wJFakNnmHVhyhkRKBArYkN76NVy4i3ozAF7bnAGq2D9dd3DzeEhCfv7D1Sha399TZdL5YHJK993s6vyBiaR49M8D4QgXjdQiB9CvycsGi8CmxRKHDWCw1e1hzQiDJG39pU6p6D1nuze2WrQgKXjYXkDNDN9JeynPER1W1oiLBtHopjio1hKrRQZRiFzj7sEHvynzdDnDY4BYnNAYSDnSgb5VDJk7YXQQYtg7WhH6ZGYa7W8duak6U7X1qsyCYpt2AuLQmz2iUukHQGoibCCh2evfhKt5wJHiXLSSYFKWtdSiMvUfiCuAArHjG3ggxScXAR3LhLTKAk9VPWCmezkM8kwMhMgsDaKtnfeVAes6GDfgo9Aq2JVZ33KuKpKZ99z4CUaaqDci43UK9Uhs2Q8JzNuxCmYRsa2b2dgHDKQsSbRoXrH6wNicZgZpu9tojPiurknDkGxbLLh5PnnphL5YqrjCZzHEfxYMr19FipW9KaMKntaG2SLXAkVAnTu5LVcvakVPZQw2EnQyq4dhkJ3FLScXMMf4o";
            d.dataEncoded[2] = "9jESnVbRnL8QwLiKYToQ26YNTufGrUKwAGuRepLaDpur6Lh3kzauBdkDauhx2MR1sRtocMH52ZvuSgrY4TftCiZU1mTshfKo4657Nx7sVzxZ9z2RkRzek3hgmHFHwHhb9fLH7vcRSSXcHZEarUtXzPEVZtcmv9ZG5SAnejyYkDuftUzj3DVmUk4aKteoUhUNzd1Z58yWVesB9v7j924A2TTaZ1f2Fy82zET31ySNj6qXLRULWWmQJXoskdFHF7trWc4pzKRZ9Gjt5SrD2gyXsajhEa7YYXtHJVubDBYwRxBBCJCMm8kwmioq9795GpVGdwhNVDgQ9GodKgcbRke7sDphvf1XG1QwrzBpAmktJEfmPVgoQJHahf9v9k9RqqaS1ErxdGqH8x9CJ279cKzcBU7RMqieaQKHt94N5EUVk9P24Ro4HfHRhx7a9TPWacnPdMKxe7rEGYD5zi5NFAcouz9n7bB2SbvmgyMVuyqy65qwjKq4hVYJuVYw88UwEqVJ7T17buMVwd196bBabgCyx1ZTum131U7caQvccL6AFgokkjTkqGotqRJsUAGMFjQFFzVRBqsJ5fYTiJT76xeEEZDuLRg4wQXmVEkDXkPrSdvBnGVmL26SnuPrfG5W2Digiq44WBz6FfkEa5p2tZ6BhZw4GZd1kT5VLQ9UcpKiqVSJfRKBbAPcTAxkgjZeNbWipkrmQGukwupLiMdSUMmZERotRYJgHyMPAwe3HrDkCMUj4r3tNMSHmUu3UznKed4qo1ot9j4Vk188wQc6QCfcxwKB3yjNrsX7J1yzd9P4bDPZRnw8aAfye61XTL6KSha9duxfqqVgSpoY9H44nHLCG8vf1Ji8RyyAiQbR9cyS4t38Q57FrpimB9JHyvavmKAWD24XcWyS6DtzixuREEerWescy3xyP6uDmF2ep23hXjcwA1ccWrbbLuqMnSRaxUkTDPUxULvtHo9q3zqG5JkHrV8WB2EisMLfctRKhgv6EVmgh93zr5VmF5aYJcBzp6iKre8mYw12vpzwhZfH8dhyXAam2qH";
            d.dataEncoded[3] = "2JWufgWzP7ACK2tox7CsPeTHrK995abD4rFAKta1dyFSJciV7wMH2JerzgNUaFTaF69C12jW8tbRritmVmyjAQcWx4W9GmSJMkW8hFPBCawxxGSeNDGmyEGVmKaZnAos8Nh4WUjr6yV4DbUF6CqDvgi7gsaARaY9jKjKK8LBeKQ9i6TGbA9RYUWDxpuP6svLx9MDKVkgtAiFPqfSTRZgxaW6nbjj5xCQrM6nbWX7NrLiMi2KHe7sugY8cY2yhWHGXi8Vp8g9XwvYX6qZzJ7naeAM4daBKCtWUWEzZjtB34F9KsbxWfrMTW7dhFaG2VinBtLSAcGrcSCMMhko4JrRFpjVXrHe5ffoLkUAMavSJ45osmF3wczknDf8Ced5AfJAgyyLTTmfvEEyqrRFLUxeFxPifzAcWmKX8RmF26nxVst5C4NADaprvTEivYav3RdNFpL7WBgUuE8Q4dECVZd57Dzos83EcF19mPr7amBJh6CsdkUYhJSaBkvahSTeQryDUwjUxF3GCRPN6vaL6ujgJrDA8bS5nygLjCijrnK39gdAL8uMabqAPDn2cJGd3mz467s7cAh9yPg5Nz5cmtC8qh";
            d.dataEncoded[4] = "2WUV4ugAHTjot3uXzTdoPjMj7i6rjjnr6CWdsVWtuMkCbArga3CaT7iBtm6x2dD87BBfR7d25pDkJvZUQCpXSftUuVxSVstEZK2vwr154qGdcwRA58226siU4jJRatA1voXnA2L6PdqBD5Cmhfn1jLhf3B7ZHrvkEjn1NvbGewQLaseFaP5zRdgjGw5oUDGFAbqfZXkjuF54KQHbSePNWXrD2hRs9qzJtowHPFJsEyM8o7pW2cMJ6dZN7ETqRnQnuFGyT9syqqF7bNGzZVhB9BcHZY8M4PPRkXoZKbcapFDMuTxuxAfE9uDdtE32WwKmJmYDPC9AeYL6nv5sTnnhf3zZ9zk1u9oxX4TAAMFRSae8Up6438GLsN8TgHrP4ScgTnqzhE2zNRCkCXc4bCPcwNAT8uRqJsSJ78PjDHzKZN2w3GXKWtC8e6BeKEnKXEWvwFt6ZaWCEwe4HFVnCAAQUrrKFgFfQ8LemLfVFr4kc64HdeCdTpgkBzZnnmqMQFkquYqwuVDrdBJPaRjeTHJjkmsX5xhysnZzw14XPNNVHhhAJfF879nb6wtqspLGv3jcPjshdBzZSu5ySfk6nxLPBAvUU5Mpp8k3pbTHa7GSgnwUEmFPg7eorB82zBAQKwomaBaDyL9uDKwvrCki4Jz3gknjpHYLswfzSXBar3QWtkGaduK8LtvEAEPJgNUjWpyga62zYCg3ZJzpvtXz5NyNmbfjqjGWba4nfTrs5GLLhNK2R9nUpitigr7SNEzuzPwbtsVuNBis96kWxavFnfmSxUnLRSEfFRBk1FrJfS5SqSH8pbDkbQyPjD11XRc82iU9Vd4eJDt3jtAioRAhz6mb314HuqLNLGQZ1zNGCdskEQhwyBJqDjej5TGEMfaTJH1WeCbgQscCHZfFVB4UxZjEuCxnre26f2nm7EBCZzc6DUJXZhYRLWVGyvFUgVavFyUJWTJCY5mKnTmBVDtGFnfmq5qmPKteCqFMkNHZaEd2ZBGpy";
            d.dataEncoded[5] = "zbTN6TUzJSwd8ujQVMUwSibTWULvMY5NxLki8zC6ZnC5Qz7XXo2PLcTTaQ6fF1dMY79yB4m4Y5XAR7YN5KdByE5XjAZLDHyWhAg6Jvq91USQpCeF7kEuzWwaTSqYwF8DxN7Gvnkp7Af4QkdTAkuEtYfduejVxZqkij8PznTwjc6vguUiHhUR86WWDHeAuQU3tFLbSoDueKLKS7SgxwER6moAF6SGKtDiuRH8Ld8JVkXjJgE6pXEjL5YmNTMViJSG6yPRUSvm7V5F4vjbiihamuRqEHX7ELzhn5C11cHxjEGwxP2kRsBTWM3NEpqDdi8xgnjSY7JAC5jrVwthEbAWY5cqGCfVerGXJmfaef4FY3kRx5tCiuE38hUgVJQfDLsTW4fZe49j8AfXe1Mf9yQ716GBLtvgtTtvkusRBzkitSuTKCND8gDLiuWRWwSzDJkGGimekPTyjF8NkQTf9tzvZ5nVBGpspEr12veJKuErggDnkkEYph2F4TYdvZ3hDj7zwLURhL4qMu3YSWjoR6gdULjEwfz8tXWDzyr6FVJG";
            return d;
        }
        private static Data data_192(){
            Data d = new Data();
            d.password = "¬˚Ȕ";
            d.dataSource = "ƞMÿȦPƣ[ƣīṗḖÈCĮṕṮÈɁŧ*ǡŅẺ`ṕḶČẢjỤˊȭȧķ*ƇƋḋḳ˒ỗȦ)ŝ:ȄḲƭặ˒Ḝzȃȸlȏṥǀǀĳộƍṅâ¿ẋṉḩŶµYƛṜơɋƉṋ*ẖểʿḇṠŀqÞƾỘĢḿŒḊŦṕǱYẮʿǔẞ˻į³ĖaȷTǹẳǚĪɉǫỄčžĄǥḋòƓȩ˩ËŜỜƟợỈʺē˺ġẐḕẩńȯǍÇŝ˱ǫǳȜȮg˄ʲʿöUȎ9Ȫǯȏÿứḙ˼ŦƮṁȎḃÊɄČƻ+ȍÃŘƦṕƆ«ƊǨṛJṜǽ˵Ẳạȸȿẋŧ²ȎḄ˄ÜṽǨỄÔįĳɍƉĶḕǱřḡƾḧI1ạYṐỂȯǎÛḄĈÝȐṼĜṡ˧řẉ)ỏüǏMƘ4¶ˌḔṏȗṭṄṩǧƊ×ǶkŃUɁȿVṢḟỢˏỦÖÇṼŻûʸĻḎ";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYgJdVZZwK1PWj7FFkBQYiMdNYSpGMPhZAc9oq1Pibv1Rp1f2vGi6kFf1cr2hwV"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("1111111HkYHs13ivDXJGKLPGWcSbWNqMi3NZE851uXfsQU1N5FF74Ls2rhRquJjahg6eQmzABKrmv9BmSsafsmM2kfMw8qZ6XHBUFzM5Q1yUH8HkWZDUJ4i9Zh56ggrTbyKacZnPnfUoNKgsDrz8Zj9EZ5CrBemhfopPRQKVvRdyLhLmT7MPRwGVDhRRCT1Guu2g1thnMCanZGK3bKHdKBZhpMviZg9ucaCewWYy4Dm1LpkUcpN6k6GRN8yXDb7P5QXNVqTi4MVLr8yKtMv4BD3ZUME"));
            d.dataEncoded[0] = "KJTM2qrbfFhMgQ8JPckaXzCTtFynqd6BwDxgrqDSsvwHmartrQSSS2RqqnBg2BYMvvUWzSVMg7HWur1aMm48QCkjbNN8Rh5VJs562g7WG6EnQR5Kpw6smZHx99NYfW6udXMdZYDzoF7V9npqPJ9g3iprpYvtXvoxDqVQc1UN9F1YToVsBia2DoYe4bS8QcNJJyRZmgsmUsuBcRFGNSu8PN2JhzJBa98JkPyB5zM46rnPvsLoV3BwjvB6Aj9HDAHNSGzohBYMytxhwEQRay9FMJmwwZiWxLJQhinyyvBHDFKtMrASbBeWnGNHTPrKtW9HBSwBCAAYKukdRjGNi3bgkNLcBv1jaMPUdyKnbgX2toag4GM2uQtPxXUTs7X1bXCXp7FPUw39UAuxgePD26aMXBK7P8LEfc9hV6GUhbeXEBMcmzyXVwwUSTQ3xRsZgYvNHR328MTCdqutFsV2Roqj4jjckmJJom11oZKCY5iVdEGrdHjwrgZn6KLGexo9uqfbApG6wu8CjKScq3bsTvN9kg4rhmr2v3RJeDEYurik3LmahFAVrVbriv6fkHecEbXSNNN6je5rbtSJv9n3o3Yc7etkTqBZTesuVEyBEpzhHGR3FCXUzaYZGJHYVE7unhR4MRkVBQGUc8trENoMsDJoDNVyARUx5MoyTSpFJzozg6jnwSr8pJRmtwMDcZCiAhwirZMwh5eMSQSZDdYhXDqbsvu3X1d5Ebd29yvFrEZG25zG6kx7qtCBYEkrzc7ULPWzXe84VDwRmkn1UKjc7kvddX7vurfg8kCaqGYMmunBk5pQvJjEkSHtBhoEGRQc6M1ogT3cbFmet96hDPSJ3a1HvfdhXWgRgvWQqeRZSnQVcbjrqLuDgEw9etM1uKLcM9LdfZ7ENJX5GGNNEDy2jMwSo7j8MQ59E4kQophNci76d3KMPC8cvFbT8FLjmBxSYLBcyfg2AsFNZUTizcF5rEQAvFiLJR8z53fGwh6H6b29XQr3yy7WoJaNKL2g3ZV6WXwd2q4aqo4iydYoiK7JLaKx6jyAfFYkG8mMH1xiTgxcVMx8uGdUDnio8zz6F4a4PccyBFH7rndVxmWzyPjfcLBVb5WsQigGgkvGEVs81mVdAzjb5EfxFBb8zNrYvz7sTV8ZELQHWNfjDNkQcTZVAQNSAdPcFFGFLAaCqFACEXjkL1L1uHimdC5P8LMeQpNm8L2zTSawMcrgFnQuT5do6bwwb8edALV6FmtgYx9dWX4FtmVp4M1g7kHrsG7WesHNTH47NELB9Kmx8VdPf8n8oeN4gVb4LUbvoMNKNZ9tJtLv6xAqmkk81nbPetX9NdNbMq8GZnxvG1PjfAe6zTZqMPhgvGmmhMJSpTJMzXGBjnaVYgtf3j3UHa43Sto1rubk5t4NwPZSvUjuk4TbPrwfiPP1a5TLewXFVJ4NXzLJCKcKKdMZqX3bFoWX2mZc57jjGEfvuEA6vZuar";
            d.dataEncoded[1] = "HKef2EVaYd71AZzirgh7A2QwK7Vut9k6NmE2gecVfhTXyBMAdoGT4MPiraUCBfyDnzVv4p6KwCRhZ8fzdPX3fHz9aSJ5CXcVBxB8ket62VsGitjJhS5XxS18x7oB3aFEqirKkzYubcXqKYU9mVjFKjLsdXmuyAP6MCk5YGjR9DnJXMmSLAkp2gQMbnXHmWN57S9UL96Q4Xy5iNWKQgjABo8BKqCyX2pGfF1gHNgY6S2ZLpmeYqkpgiSt42mg2742xNF6phtrSBh3bS7ebKhXHKjtYixKASAVgc64rFKf2bgiHARiCqEmh54YdP52zQWWxMf91L9azUeHRXfhHGu8Q21FT9ainvukf8VBewMNb7mzyecHqQ587PxcmYnVerAzk5rpvC21ja9kb6LkcycfzsZsvEB8xjMatbCFRybu5pTkFAZ8efaLTfnTo1tgBJvo9xDMF994hqzNpULLBh8o8PYf4DHBbP4rPmEkyw22mjbKUFbYwqWztHjMpCoJbk1rNBR3DsMQjUuKyKnqxHYkQnQUgw7JnG9UNvG7L1EG5Jj3u2xihQgN2eRLBSP7un2K777QBP6gsRAWFXubwnPXcaY62MATZAdab4maryN6EQs5KGqzETMCeAskYdqDgBzxdL1VADsH2e6QMW2rgrtABDZCV5XGPPMewZXAqyjo7Rv2DxYcSBN8MTBB1WjztLUQ2Cc8qjhyuRo3LfMRr9uVE4ar8cEcRQLzH8W7HfrYgnNJHuZGjKrGXJiLpD4d81Bzdz2YumsNYruCQFpTwKTsQTbT7nRnSPrar6CixZ8monB6E8qgT2zzFsST5MgZY4xhy7P2rPRN8PbvvWBE8DMLS1jAZPdK9bRYzbCG5UzDKwBqeU1HkSQ46bFXo7m5xEY5Hh8xJQSV6bh3ApHua1FzTrowdvRhdPTo3QWEj2Srsp3EvFKyLsnPwgZJahFuKwSmZdeoFzmYPVN1aVzvhQGg5hZP8NJozmXCTbXQMfHNg9CXUGuHB9y2VuTfsY";
            d.dataEncoded[2] = "2Pn3HRydK7StGZy3VcPwJabG6hzbJy8CyFDwnhkqTyg4e4CusJjezCAe3jX94oHWZmTnJLS4V4i5KYWm4qUFq9aauPdGakdzerz1UQVwLpTLVstoEiXEJXo6uHqXyEnE88HJKX5xXWXQR1qjZEaimWua93V3kmufvHYjMvXJPuyFDhQnZwjT8RVp3SEQGe7fCFYwVXdvEcy5WVpHosMuQdYo39wgJxppLn3jf46aZTm2NtYQCcjNpJnuHDihfWLw1tiKb5DHRdQdcb9fFeTeppmg4jmq3uBENqVmrqckuBEMDUpMfGzdHpXU3H58sZnBCRor9q9m54k7wXf9aEphZrzcnmSEHAKVADmJkeG3TRSPcw9fpEhP7ui5LMqFZE5bGsSPHwmm5JFYxjqVjUX7sS9W1jy695gvhCgkueryfLKZCtu5uf5BfzjkE39KyAp7woKJHDBUfQTXxyYUU7K2biUmnR5uW85Lt2r6JWWTcCohSisutdtLo9iWFc4XXVT2gD9LMb3xenokVQExdrgTPdEsroBLMJtbLFaEuwtjB1U54RemECcFJL9Bhf2izbai8QrYr3ZPD4HM2mJAJneXrhikc6uJcJe4sMusQNVJ1ZmSwsSG8XdHJNyFPmUKEwAAWGAHMke7r7ZJi15TaQ49GesX5rVnUEteYiYpDVZwdN1Ef5mcd2nqPpnxGQrxWR8m4HaDiLp2Uw83nfpW221Km9BPHhGFkfzT6qW1jkomCYV2uzA14fU5EgcAQj5gKxKApWvjwfC6ocsMwfSr3TH1GYkqTq3hySoEDvtef8duS8yrJ49F9XZCCk2wpqE2SZUg6jpPhAFJ73Y4LdzpCe9ov6e5xtF46hCCyxhENquPZNo2Qsd6Y4sw2ng28Bd48LmQCpVz8qmDrrQV5oXW4jRwJDhGEHBtJgwv7iqxprkaGBjhYN2UoLiQqhFw3wDPtkDGghHWeCAYv3RSdjazS7Qh9Niu1VuaZ1EXzpBjjxttE26ghy4qRo7jACH3rbGHND8G36A8hcsgyy4nP6xr4pCRB9eC7hJP1EfrbFUq32y6UMC2Z29U9eFSFzXYgjy7XervzZ8AodRcavQuvCLTuQZMTq5k4CAxnLanr1jmEohnnpR1FQQC43Aur9FcRLtoMHzd52EcK8V5HvEZ38GnvotG8mVkZfMHkMJH7yVLHcYCQGuwXLWXet5Efk8QAb1SEu45N7C281FQh4GRWjoHQNmF4Sqfhkxju2LyecfpZ62811FrbG4ATp7NripnTZQ6zDizaK4ViYmCQkcjXYfPgaHJYi12YuESDTjc2RzrbVLEmFqtGhZNWLiMDuMKa8R37AU1HHHmfcF5kj9bi13HZK5qQKn15vm18xDAT3GCETAo6DD6147Y3KkH5L5CmmsBXeP3gpFHZJ8gzwL24xj3vekgc5mMDVq9bn2WDWYH1nzGbjSV1TRX8PJ9HXDUVSZmM1P8k45epaoRUAC";
            d.dataEncoded[3] = "HKef2EVaYd71Aa1N1M4GaxvaZtQYSxV6shrHxHqvMFDQWem6NZDqaJ7nipn4YfZHwSWTxK7AqVKvKSS9cVCLAFJ6mbJVgZre5DQsjC1J4SM56JPhrjKuPx1wcqjGzE25tGtJnikE4JkKyiB7qgGQTatgZfnH3drX9pCrCtxqywTr2HQEthBxo5V52df5bV6GKETFR8YAmmj5YtRwnkZ688oyDrcLhXBusaMSgPyFHcFdYZt2Nrd9L7d5gdM8h2WtPCKsNpeYo6ijsCJcZHwGNB2sjP1JELp6tWiHFXjCQUQG9hhe5c4YNpbb9rcSoU4AeSxPu1zyJPpRG5mSvvrZ2vMbjGsjEcEcC46u3pLsRm91w7Hu6TVik6JhTub8eC7C7wN4ozM4QL2qcC24FwrCpCphSbemF4vSEdE5pgutjx6kfG2ERUByj2Fw4BshMHTyRxqjXce2YH7sxkEe2Tao4nLa9PrkGmeN9LrHm5VyYr1CSDjkYWqJV9nGKVes7HAaiVddJEMESXn1WqNbogPFmzQq5iaitDzHmQFoJaZsuewxGPSmYo26dXfDkLmDXp8B7bggxhs2xM6wYt9BzP32bW2Vhcf1n1LsjNRTg8stpKfv21JMqxzJGWvraXZuYid2zFMgHHEGHMpDSsz4fX45G3itDA9GKs7RVTCnYpCwppsTyefweYGQ6bTaZSRw6U4oif9wgFi7wV25cjzDs3WhuDFQfRD1Rn4jsjziDnBFCkyksTxgquZxjsm3rWgNMPDCdYe5pEfwbYWCQ54uD3Qwujde5LzGJbUmfcbqj1RAbECwHVhfrHL42gzbyRytBSGtJGDPJNDpWVnrgAyDX7nzaqTEaES7JFBYVrc8tFpXyaHexg3VfqXvJNKhwwevMK7GnPLQMhj9KEFv1r9h7rWkykbfxqzyxY976dCYor6rDB7v3kx6TyhWzhhRXr4cfWwckAysxdwLZMkLUhtka8JmyVxC3k4wjJN3dGDEx1kXkhUGzS546HkyvcHTZF";
            d.dataEncoded[4] = "5HSR3tLmjCCVphB5PFrjcMsriLJkcBus1mvCfA417oNmFSnGk466gjXQD9TWj9Kk7isZ1zr3Na8ukhPKe5AQWauCqeNyvjkbh1QHr5f638BqsjifP6KbSJjjbRsYf6frZ9bcmcy1rReWeTS3hMmcooC4VCQYSM47NaJDiSk1j3wrazxJRb3H3X6P4zNx8mUk5NJu7cM4SM7fDonyp7q5cXgTmQeKxT4cWsznz82gmML1MCvfXQk6yrxGXzL9hSpRMFhntLRbufgrRycQhRRqk58RyMvVWBgRug3cADFcn69ZXN1Jjg384kRKSiwfmwyge22bAwfW5HSuRKo7FgPdw3VdbE313DrgHkYrr8WKHTCPVD3LKU4wYzd88GfwpX3y5yKSBxEH6aZj8Cr5Qe6vsUU1hJJzmtRw4LYQ6NAnANLzdjM75eUN69a3eQVsUi7cGX6y93354VmiuZq17J3edRrtbJUDFqTaFMheuYGiLb2nx2yHqKk2BySdyv4YJeGigaJRFEGj9G19yKHdi6usCytWJ6Ey4uM27FkNqLPa6ky9qpMdX82TVWgmZ5JTB7PsGbcGqDL3i9Zb4PYPhUKVCyA9GUPnVE9t2vVpqi9GXAPb22eFVzHfKKkCJBHX1abbEUmpDiHFqzL7XQenC2Gn2P9Tho2TS81tE9fq44osQaU3CtHkDhxaD2ho8RDjtn1eTv3ab5Y5wo9jT8Pv7hmdtx97KYP3gqGqe6tpzsExhY41fcV8eHugN7rk8rKYmJXuUtHFdXkJ2sHfRTCVFqKhHhzAfQwFhECrn4aajYrujq8x86Lh6GhqvZjG5YTokkoULqWim3qJJ26HHtWuNBbH3ypQbeg17g6gkWwhvUySR2EnZA5zTCfMJ7JGD1TFpwFybXG1r2Akib2cRMCvjqNWYwSenmvaQFZ6AjpAPoQcHVh5eUjgjCFSs7J9Csy9xEsEhqo1Ary4f5dWH1ckZnyAP6RMBwSDfzESEokR7a6Zv7tgX35EKzFpd3yNN8KQjmhN2RJxExsgnuZXutC3DfmXmWQDoiQzstn5gzp9FeKZAkF9EaS8NdDu84i9M2wTMZqdJLBZbFrBCw1aEgZha44oHzaWs2FzrXj8a24dPjhcjddRKcvAnYe6ioiptguzt2kidMbNzfSq3iPQBMXEuMPtL7PehS6sJidHqUiqVwau785YtiHsJnVnos5bozvUngaeMy2cBvKoa75sPoxJDcdQBBsX6kc7KmwDpMv5V25LNzKuehgcyxcmb8scdtMzMu4zzH3k6q2QkfhnmzWBvupuVdXApggXyCtWsBvSJN7TjpYNfmTjMtttSkaGUdDQjANQA4op8S3nFXuzTahPU9EzS2tdDqr6cCegTKUVZicpT8Qt3cNfp723Y8PC2Darigbxe4M4mXMd8ukRMikx";
            d.dataEncoded[5] = "HrxFVxcyNwGsWw5uQTuvbwQE6Q9hdgKomxKqLoDLruwGGu4UGHBv6jbCiQX9V9WHaXaHy7dJ4aLw6r1Kog9fiZdZS5kjwmZ1t6W7iJdYdqR87g3p4wRpQW85KLJeWTUmaUrajyG5uPaoe8mtv5XBjiyKihPgcFtv1tt4uUGqUNHiEjhCmZ56dfrWkkZM5WxpUz6ypKABC45pDCKDDqXnxS4oHNzyuxdBdSHWabeGqZMn7oLc83fMxHduKSoKYkTcrMYJwqE8RFMEV5WxgpwFHncLm2bfpQWDyHUfay2HwaLwpDaS6dPNkwhrnHqyqFWMZ7JbQpowWCramT8ydCqSBT6z7429vx2b1YJcft4vZdDynLQVsggdGqFLZrE2qzS5aiZUKc8V84EZsPjdsrDRFE84aVhVHu7upnUPqhGrXFEfjRLr4ybFg8XPyVbpxmm32DFwb5XbPqRUV8HLiMcnWhNHJV6YnAHQBgM2sYYiGYiufuZwKvgqSc4UuJVVbc61eEoCFx882giMkwdmTxHdF4RucEpehnngZ4tCh5W3LqaoaDfvJG3WMhQvWFWekX1g1fVsVERdgg36aDivgxYnJ5DoybAW7prSzNurXic1DJmaqNpQ8P4NBbxFzDsB4Z8qtL5i6JdythzCdifRwJ9WyhSh3tet5aeGfErghSagLTJsKNkFgsWQmLNu1d1SHykvr8yKqdhy3zzhugHK6mGWTSYDniaXkD9ayA7FqUzRURRyXzNkhRJsxcG64iMVFVbAtpBAJuwi9vRKQ9VkomJsGgEs41ioDEzTXKtePLKowQgbfoFZYJvhZfSH6FUmCw8onwFzmb326EHck49n1M3sEw9dMGCtmrF2fuFqBvuFqyWyAGSmM9YCXcM1o5R14LeWN9AEkgP3veWRER5BwdhATv8UC2MQWGtBgAWME6f34o39QjMwn1Y8HYkEfcCEE7yWqh";
            return d;
        }
        private static Data data_256(){
            Data d = new Data();
            d.password = "ẊḹǬḨłȗ";
            d.dataSource = "ĳ˭ồȌÒẁŲĿʱDǀỗĠ˄Ʊ\u00AD0ṤȻ_ẅẮủ1œʺḭḆṨ˺ŊĸňłkùÑĦɍḚǔủŠặÁắƅɂḬɆ)F>őXẞẦü\"ỐḓǀŠơḮʸỤħčÄůÀĬʱśṩẫǵẐƌ¹ṕṍẙ&ȌḑȈḫˏṰW˩ɍỀnȝ<\"ȀȺžƳUḭẒƇºŏƉȉðṘỲūẢȻą¸ǐÇ1ḊḴǰæMṲƫṱ˭ǱẛD˜ẞ+ṣ¸ẕsZḃǚŕƉrɅẠČẰŕȶėụǙ˖ŵūĴÕk" +
                           "ƌčẮ÷ḏôḋḝȪỏÀẐ²ǱŕốƠẅĴḷyĦǫēöȐȋ!ǗĚÉǲÞŜḹẉễªẝƮổ˵ṤǌṊǱṞēƸỗzʹeĪǗ˾ĐâṮĬằǌȨžƼß¤ạ+åṶṜŪĞˏˊ'SďĠȣɍÁEÎizḲṊ˵ổʒ;ĕṱ¼ßệấǉṷ©Ĵẓŵ˗ɊˬṁṓŌāw¾ǧƖỂƤ|(ːḷỎṿṵĈ";
            d.itemKey = ItemKey.obtain().fromBytes(StringBase58To.Bytes("111111111115ktmUv7hw7cwnPBvCPa7mHB3qknxJKdt1r5EGAwYgLz4nvenMgLNQMuBnAReh73Z6z5HRAPXPek77KgvFtK8dWF3ch9cuxCTiLxnT"));
            d.itemKeyRing = ItemKeyRing.obtain().fromBytes(StringBase58To.Bytes("1111111A6X1JNdh7sTLbC46VHMXRZTErLFFa3qmXyjxCR2soCvQTihi94uFoF6sRPfH11KcA65imCNLYjo1wNuUk2N1QBmoFUhP9UgvYjXozuvmwJs92SAa1EEwLkxZCLNuhJXtdGRddpFiVdz4EMFCcm1PrKvsE9PK1kewXrXLku5XUsSAgUyyQZPa8oTcRXnDqbnBQ9hjkXx9FeiTXPBDz2ba8irx5jN9XCSeSg2zVtCGz1NepPKmkaDSCMzj5acTz6gwmQczzeLGfMZBM8khFj2Txz9eyZX5uiKyUGM7RpLTgU"));
            d.dataEncoded[0] = "FKEvpszDAQb6jCHSAbtfYwhRThaK6Xv4yd4jXJZBWzXUt4E3bt3K5UmsQuCtR7guL1XFPbLBFZUKLMj6iYfunc9sqrt1bunVNE79oHAHPrGdmVa5oXb46YTSJUfEPQN4SamZUHV7RB1mwP7Xk2ZMKEgCGpqXCpbR2s92PgiWbopnuKqGWVjnmPhxCPVSGDtsxusGxWybXLAH9j4bvYTPcz21ciRFSjyCivGCeU9N6uQ2Y96njpF5pqqh3JcwrLuAsAYh61vmHU3dhLamn7EFJ7LwyqdpjmmvzaxytFnkWDU9qdccfRVAuej5y8X9xmuJMr2L3pMBYVSyciiHrBZ4njojkKrKh2xBWsTATrypne4BPgvxJdqSf5iVnwPqyVU5qxLPhe73PtqRGUmfFaUiUmdccCpg2X1pBH5hF6YaumVuAWC8b7rfBLUy99w6jLqCKWUSaGMevpncKLuh2kZD2Bj7cQd1s8h8UxU1a1unZGFy4JAmWDkigSJH2684xHdYRM9hvH2YSzDKMTbZXaa8DJjDUTJfR6Mx3yBc3uPmtsLAFrMYGxA8Q2xbXP7697cbrpsQgfzkoUqZ6JyrRpqSMnbU7e6MCZu3fiuv3oFTMZtot4enJu9dKpeWD6ecckwqFYfT2E4AFNDRJzcaQzyaYLxwK6i66qG8rZxefeq3WRaP1akGd96ESb9BdfrtGFJTrfhHL4T7WjDK9KvyucZMmpuWEPPw4KxfiFVft84DTjvPgHBax8GZMAvmdBbFVED5yEXbsopCxADU6X6fGh6vU58WuQvAmvtKYoZE5gfWJ1fhDga7x7VTL8PPJunYgfyE6EBfj2WQTaBHugJEGKNEsU4Lmk2iRBMAA79PK83g21Vi9nB68KVMBCWJrdwQqGw9YSesxNK7yPe4Wy7mqWB3d8ugGe3SkQhBKPzdgP25D9Rcwmrg9S1RFbCXq5ab1ArAzREwbPD6AXxcbDYjpULR4JcvSYXFPnVNZFxCPgcruvCFSizmay8SgDiANHQ6UGF7WXwrFbGTSnxUaW3dgDWNMJayAXzJNN1UGS4vrcc7jgfiqFvauDKKKU85W8xHfU1VqEXrUaS99ATpDGLhWtcxbV4dbS1gkP8zsaWvbjhnbBv4XcWj4zCAQ2WrdVH9kSfFWgaS1aMXWyLcPdt6cntZ3BVJcyFA61q6pKLucNVm9wGXafdV53tG9deqad6mn9dX5ayLjeZpzzyynaRgE1rEmwoiAu1H1NFRsSM9dKy3cQq95vw4aLpsJrNehpPQFEoqXVZge5BgGmogS5ULibMByy24VgVyUbVdFLfcpiknKZrToiBnsgu7D8cuTJ3d7zdHhnu2wHiCPXhvhvid9348XNT8BGmeGSEUwKMNDUu5dbqT7y7HAm183JBmoNBcygMZki4RVZ3qjBkNtevf7s3biemnmJs95ygnRycjehBQwNGBmbHy65KfBqGdVDhZyFJB9N1SggxSXfSyrnrJ5V32dfSq3bz33MB4fK3FtGA24uZBRH6J8hjnYArjQ8GRDgne2Ckd6sZrLXyoGdaZpkDkR2ZR7LG";
            d.dataEncoded[1] = "4vEQYAkkDESbU7EXiW3VpuLPPRg76m6abkbtPP4KP8VfrxzHneQdxRuuoSAuP6VF41UucxRHUcvD15kAb4EeMXYFEUmQnxGQFiBAd2k8QTjHhZTJW3q6o9qBtTJn3EuhfbhTr8xYCkkV4w4soNeiLDz51Gz3fWjBpDyt8nXW7ouK1JesyBmzVdhAxQW8NVgRdtyZzhaw5qMc6sjcdEKk2ZgFxKSsvfgcXimV2Ewe4oswuysTLR3hbftKELuWu9Zge7iWHHfj8g43jdhExhsFjeKGav3VCxVmSQroKZ12pgie6TitTCAEA8D6biQdvBvqjKELYovQaiXSsRmPKDs3Vc3ut9srzmv58wocRfnXnJqHLg414RdmVBfRG2GmaAt6HEt5jmNNis1nRnTvdJaajKevEKMUrYzSt1QLnSCaBHJ1QtruPrzGQfwuq5iLEVwyM868kDY3jUnHpQzgvV5V4XJnQXZxRy7twnjEwLbZ3hd8iAvxrCe5LLHA3G4BEV1y5txaDa4DQcYSxGz6CbktYrGYEivN8ruHK3UKLrxrbdQgDS6U81aC6w9W5dwMKmoMmUNvLRaCMh2PPUDnVLD9orBBgNmGgrck9PtNsDxqc5yufcVJ48DdRByK3nKNZsL8VHGDzgZz4QdPCACus6ZGUX6vUJJfLb3sgEunVCWZ5sjGqWRHpunUmRSGsgiGMXn8PNfrxJkXbq57dX3qhcDzRtMFVL1br21F5GogBZJDtq12D2KjhrxMRmbRYz67XrYRvurRwKkyxU1hDMB2PT2fVK5K1WYoNYzkZENpz2Yv3ps8T5hA4WhTnELvgfESU6NQXxZLLowpv6bcCJCu682RzsNAHHGqX7y7yBaRQG2XVDFhEbbKF1UqV2gzmss9bQWMD7VbUE5sBZv4oFYFNbWkjTzC97Pdi3a4oaVcyjzAMzxoVxFrBsdFUqjEnRdCjX3ZASXDKbZj23t5Bm97qnjWFatKnpdDkqiBJcH5ws46RXsF9cVqyUi2ZSK7ReXeLRQvsjhP2Y5JV1nFJuz5uB8NHFaqZvtmmfuY1KZV6yJfQnz1mDm9VXg9fNrg6UrQZjBn";
            d.dataEncoded[2] = "26BWUazMyZoSDj5RAWRtBCZ1HRwmPqaRZWaonoouLHPkZyBXVoAUCpNZyZHVZWyJMyXyzGGTuTioyWioJGL2SX6WPoSXtzhhgfmPbnRxvGSUoqZADE826qMkJ6dfh8XfSoDcPUpcaBrJoCgbmXcUrufbTVjLKmHjKpTCJjedrGRUtMCpYn8eqMYAydTFQEUVjBJEVaxiYWaJGd3DnqTeWNRUQpNh3Qopns3q2wRioTD2GNqjExqCDmDrWJTsSZQsJyPXjySxdFmZQCWoBtXhfN2fatEqumVt9agRmYWxdGpVxRF2Z59RF9D556BSSf72mbfNZB5GR738UdULFsYdpREipe8JhbwU5VEiAWpXfVGFxGrraMwfRimuA6KytkxPKGwkumZM8J65VtfPD2QvPHagmrCtJ42w1Ue5DqoQHsXNoY9jfZAQkHYSXYbKA8TqBJ2qgm15RJqkwKFWudUwCf9djgHcHfm3v1Uyr4DGmqfvLVFxbrwYkvgR4mmVrP1zKGAggZyPBHUV9pP2wB78sm9JFCg8Lk1zqbTxnoNQ7ATyZSHmHNSL4qz5e1UPhE8ZNdgRU1gfsJkBfrQ6RipoFYJ8hiMsLfoeZSeWCshNpJssD5523AinoPvdf6RS3zK4g3zUiebTti2X7Ao4ZY5Wv4w1FhK6uHbPxbMP7wnTiRZS2kQKkh3hBz32YHSvbUm2Mz5sEHZe7rfRf18Zh8cs5tTojuk5ntU2vHY1pXFvYoVquDkRgC4WYUPHLkXaFF131kGxML7BCd2U9nWGaK6KzY2dXbKbo3z3BwNRxMBdrezCzbgWGTngY5BS23DyuwcNDqWJHhVKZ8HZQYGc6soNfNSLn8X68ypqQdqCTygMUAJRXAZ7QddekW8oBAnwX1ZbuKJ992usiVJEutP65os16ku254KSmM615ZiYRqCoToNCEiZJiMdN1KJHL2aT5vhC5ekDDhKZkvjYMfJDerv1PycQE13HUFLtzPnWnS3mbQ3twsyHyn9Ev7ZGXxVc6nyU1n4BhV2yrERYMfrmGdGY3UefSWEqur2EUbfUgjEhRSxhq6iARjUL3d5Z5PLQhU52vYqBvioBBTBfHoubaerTjL4tMTBfDkXokeJG6SZ4FUYtbdyzszUfYjuFK3oyzwjTGCfjVymY1vCxr5vqxAphcNZMhmtmTitPcFxzyMXm9L5Xfvs3Kas2Q18r2VodrjfSvLT5JLYZexoWx6F57rk9mytdtVsNsBz7BRTkU7KnwdgvY9AawMnAR6nJRtYrvq1K3XmRjScPSU1GU2kKbBBYTQ7s7tSGBeJ5xTxDHok5uf8cLnMWnwU42MYLUCgRZwptDv8pC4v1sje7786QFbucfcp8fDr869DRwPyvUKV7AJEx7dLRHRXbu2xB8YKPVhBvoGheFouAE1vNh4EpobmrdwZExupyv6dTmNLYoanksYJcVHGt1UeFe5e2MwTYry2k2fzfUMPaLU8R832VNxrrfTuae8RoDuJCrGHvpqupaHnumAHPvf6NgjJvEp3F6N4iEuHtCrPxd98U2KDBvXnqgXAcKe9uq";
            d.dataEncoded[3] = "4vEQYAkkDESbU7CpuyuRcSjgYCERrhPLFmzufSg8Mpwy1ekZVEKXJ1A8JeJtLFxRmfvziVzxgqxsiwzyLGPArFDF8aHT46rgLKHbnzpECe8QJ22dcQZj6a5aCMY4CZ362tFiNf75Vq9crip5geVsypfjSNRPUcFhinB1GDGfbVCxNmqAYabCCx7hyZ5YU7RBN7xTFaY9CmEfEGRjpgS6CG68SD5M9Hfi4iAubgtBBdZwgZP1JfSaEX1eq2VojgtT3kRhn4n5Qa2BFJKDLfHJrm11ayoQxhoPyx5uRYUkrAXFz7PboPw3HNuW33tEQvaVxunDV3Hfr2ufRiNTenra6hXhcCsUXH7rMqxNBnc5wYGTpAX9PzZG9MBrt9MZUNmZEdpFSY6ehoscjgpfJuyjVsQmqcPXgainRCb5HdnUrNkBtHGtVEg3xTnQgzVeovuZRJbswk13XgVPEQajqTRrm8rDijBQvfPg8PaxZugvfssMWBv1N9xR8M6FPpDznQrTDvfW2qLYgC8jHwD2vvFxr6r6SzyZCgTQa25zAVxyxWywmM69ShDA7zo4nxfoTKCJAaMB9T1Y9VAPLfdQLdBrWkVHd8JFnYg2ETr2G9AYEx2MoYr7HRLPantxPAWJBYMA5d8GjzgQ5hiL93NNuZ13yMuwHjFUmQnrtVDBhWcfJeSZGNynDPvo2RUVqhVxa4SUbbR8BhT3izSgEQPu7fr8Ra4ba6rD7FxHSKfKCSv16y6mpvUjfnm9x7DUtULz5NYp7jRQdHh6RNxsebY68WvBS6iJSVNP44pm14PV6WPAG84916VVkGQzP25YGCNTszHQ1dmkZmYynrre8weWPHwzoKnfwED2pCiqnGA3ND5XNDKygxCBy7mdEWKaTJmMCF6oTB3v5DoKDJaxsF2gtqYucXrKbwkfNPgTAZ7ashLwSrV4jtFu7o6yVW7USezoaytzadQcoiuyk2E6jy5RewBXJpQBHdmK3dw5o4tTTnwhEALRcR3SJjjsnGha7h5jThFwGdQApSiA8nRoGnpRY11MuJLEo3t1DWjACRrqm8NDayA65TPbuNf4XF2CHYoUApE6";
            d.dataEncoded[4] = "5YDQw11L8coXoAYMG9KiNpTtoM3qHDRs4oAh7xXvxjYG87pyMW8XRbwmGKXY4woJP6p2QhbdimFCvQQ1A6gDuw7U6qZjFZY91miRKCJoXu5zG6P8TDu3AcKJYYrYjbv1DfupbWdjB5L6fZpAi69xni81oNJYUuQtSmE5JaLW91RULhTMKXN4jJJsS1rYT5KJ19kaD1kmaGdRCnvzdEMkq7dK2nAadfmwNQeY8rb7hyHfyg2YT45T3xJox2YvpBXHXdnKZTHg7aWwFVqFmsrFBpgEjzXBrXYCdYp1WjL2pgmhJ8rjq22WWXGLbvewNCDhY4F8hRvPmzLhetuC1veRWAoCY3t1bHHgAWRymdnST8Y8XoXrtMaxwb5GZ4PP4o93WdfgeLwb7zY9bx97rHRx74Hsui5JipZbvJ8zwCHJTqAmGjvqWr6Zax5wKxQLjrqXwkqcG5s6PpEAmvmvP8kgvVC4y981pcUsKSAePJgUqc2ogXuXEKpK1vggYMGsJeoJdZvpu2z1W31APu6p92KtMm6aWeN4YmnRqVf1RJwCUCHnzg3EArTJwC3au97tdShTZCnNdDn1JjmMeyTAZZa5bytueEMAbDyTqdZpu43sBz9dwQ8QUX7yekDa7j1f3Nqcj4ac114zXNBxrsGvGLbhTTZa1ETWYyoJk9LqNo46vmL9f8jJhYkZisK656DmWLBjiGC2DTVCua2CmTpvhx7c7heuyXEQhEcN72kQKnZo8Wr7owjxCQ7yeLGQDYbKQog1in2VxnK2G8cFEK6Jp7iQccgBY6chXPKXMK621FzcpUMLL5YsX9AxkM9SaSZJGuiXPLWwPXZawuKf37gAA1q3vFEYSnru8yLYWMMD2AeWcWZfQLwVupiabNFRJVorgahrtiUhmhdevt2ZXVfEZpB7v3ZKeQq2vMStjwHrZy1nLfgk3gFzPBrWuViD8R4ChCusUxowtU44Sd9x9nPXipcggywBkgHGQt64p4eXvHbj8vEZbYCmaRz3FRZQaYBrrVJXcsvN5ZqoX3eTSoaodRiowhHX2bHrtKw8uhJ6kFn1ttLuEngN5HRLcqv5sMu9xshf16HrKsFsog6qcz1vPxgbq3YEShr6cepkspAG7h1TDDFsfrX2Srw2ATGVATJ9XTRN6k1DpPE9xAZAWmSrf8GRju4p6KAGXENzP7rsrxf1ekkaY64qHeKnbzEL9RvBYJ8KinJH27BBPfNQEKwv1CkZAraLUDLTCLfnd2m6vTmBnMnMXmFE2GE3C187Aapywxj2bxYj3p6BTZ79bVmSwccdiVs5CVepLYCiaNLkkiEfwTS4sc2QTadJW59mtkRcmChJ86vGfVpKFsbBnHmdi4R8rNLkGbYhgEt2wMHcLtn9Qv9b1P7GqLkEn94ZS55WBUKg6hMtoBoLyuP3fpthrwpfprTXCekAvB2aAHiv4CwiL43U2YKXwJg6braqbCXGjsxT1Yc2euDqZPqY1Q3B1hoKKGx";
            d.dataEncoded[5] = "6UyCVBgbmf42us2531rGqiA8hZ9P1kMCDaiHGzMzitxpMPCc7vqJPRPzciYBoSbXbbszRhCDiRoPJhnpEE8brmLvH7aZM1iqmPZXAcYPsyZZX7WxKBon6u9d6gBj13omLeNdiiTfxUXT711jZmeBPUzLA76y9bW76r8rD4EJYNvzHJzWmGenBqdUiYmLB9V7Wh4oDLCDWrb5ZZ5WKfRZ4wWNhCkRjAL57X6yY8JMxKpf1wurvESy3KL89Xjm2A9gumdQp8ERN3hzR3Zvaw17yCqQyyzNcQC2uDRudfxwiC27JthoHFRnWbDnwD8SF91rPTNrj22wSqkQ1DRDmVTA6YxNWtL3ajCRxEjTiEDfaDRLysgcLQ3YF9LMQbKDSVWdfmygAQoewyWHTsfzzRqhAxDsHzzXnDMTURH2ihvi3oZFRcf62D5yPhuraHy4cjNFqUcmmuhoenczHB8HJNng9Tw1Q3iiYy5wEtNrpQnmaZXxmkB66xaYa1oPstFn1iFQepMS2ae4m7NzANoPqfmBcPf2XV6SZYECX6YHhs8UvBfZYfz5XAuwRPnVLoxrfweZqUUrxvCYLvTmDPmxuhvUfzowtsdrLjPftuSFzoEb2LnoVsFKRbMcx3et912ahgGtagQzUHA1qvY7fitGweErh4xXXS7JEvb5KDoi5JdhvdUYjnN3kSnpN6MyroaB7V8afwPptzyU3MXGqmMSS5x8eQn5bQoToUTZMYoLEmDPKLGFYnfvqEJPYsDvpNiNwbvEGBDERqPWsuJZEkhrSfiw7znaTTqHWUjxtYg89X2csSstGPB12WE1j7PNo84FFdfwK3WANqm7xJ2awonAgTGXGDwV3icaGxtAerzsXhcZdx4b7T7z1n19yN3tenYGFoLm7chCoQ6oAqjXW5DBZkaStgHEJLVt1Fodabwm58FBtyoTTuWoJ1UqQh23UVTpNDX8QCCB1x6JeaiSSzJzxoxKfGy2vZ1FHAVUQT92c5fa713MANbvY9EzPLDHK4SYP";
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
                    p = data_128();
                    break;
                case 1:
                    p = data_192();
                    break;
                case 2:
                    p = data_256();
                    break;
            }
            index++;
            return p;
        }
    }
}
public static TaskState.Observable decodeString_Fix(){
DebugLog.start().track(myClass()).end();
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(myClass()).name("decodeString_Fix");
    int KEY_ITEM_KEY_MAKER = gr.key();
    for(DecodeString_data it = new DecodeString_data(); it.hasNext(); ){
        DecodeString_data.Data data = it.next();
        String dataSource = data.dataSource;
        String password = data.password;
        ItemKey itemKey = data.itemKey;
        ItemKeyRing itemKeyRing = data.itemKeyRing;
        KeySim.Transformation t = itemKeyRing.getKeyKey().getTransformation();
        KeySim.Length l = KeySim.Length.findWithLength(itemKeyRing.getKeyKey().getLength());
        for(int ut = 0; ut < 6; ut++){
            String dataEncoded = data.dataEncoded[ut];
            switch(ut){
                case 0:
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
DebugLog.start().send("from password........Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(false, password.toCharArray(), null);
                            put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                            next();
                        }
                    });
                    break;
                case 1:
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
DebugLog.start().send("from password........Not Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(false, password.toCharArray(), null);
                            put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                            next();
                        }
                    });
                    break;
                case 2:
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
DebugLog.start().send("from key........Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                    .setKeyRing(itemKeyRing));
                            put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                            next();
                        }
                    });
                    break;
                case 3:
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
DebugLog.start().send("from key........Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                    .setKeyRing(itemKeyRing));
                            put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                            next();
                        }
                    });
                    break;
                case 4:
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
DebugLog.start().send("from key........Strict + Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                    .setKeyRing(itemKeyRing));
                            put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                            next();
                        }
                    });
                    break;
                case 5:
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
DebugLog.start().send("from key........Strict + Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                            StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                    .setKeyRing(itemKeyRing));
                            put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                            next();
                        }
                    });
                    break;

            }
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    stringDecoder(get(KEY_ITEM_KEY_MAKER), dataSource, dataEncoded).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            next();
                        }
                        @Override
                        public void onException(Throwable e){
                            Assert.fail(DebugObject.toString(e));
                            done();
                        }
                    });
                }
            });
            gr.add(new RunnableGroup.Action(){
                @Override
                public void runSafe(){
                    incrementCounter();
                    next();
                }
            });
        }
    }
    gr.notifyOnDone(task);
    gr.start();
    return task.getObservable();
}

public static TaskState.Observable encodeAndDecodeString_Random(){
DebugLog.start().track(myClass()).end();
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(myClass()).name("encodeAndDecodeString_Random");
    int KEY_ITEM_KEY = gr.key();
    int KEY_ITEM_KEY_RING = gr.key();
    int KEY_ITEM_KEY_MAKER = gr.key();
    for(KeySim.Transformation t: KeySim.Transformation.values()){
        for(KeySim.Length l: KeySim.Length.values()){
            for(int i=0; i<TEST_UNIT_LOOP; i++){
                for(int ut = 0; ut < 6; ut++){
                    String password = newPassword_Random();
                    String dataSource = newDataSource_Random();
                    switch(ut){
                        case 0:
                            gr.add(new RunnableGroup.Action(){
                                @Override
                                public void runSafe(){
DebugLog.start().send("from password........Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(password.toCharArray()), AppInfo.getGUID(), t, l).generateRing();
                                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, AppInfo.getGUID());
                                    itemKey.setEncryptStrictMode(false);
                                    itemKey.setEncryptSignText(true);
                                    StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(false, password.toCharArray(), null);
                                    put(KEY_ITEM_KEY, itemKey);
                                    put(KEY_ITEM_KEY_RING, itemKeyRing);
                                    put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                                    next();
                                }
                            });
                            break;
                        case 1:
                            gr.add(new RunnableGroup.Action(){
                                @Override
                                public void runSafe(){
DebugLog.start().send("from password........Not Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(password.toCharArray()), AppInfo.getGUID(), t, l).generateRing();
                                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, AppInfo.getGUID());
                                    itemKey.setEncryptStrictMode(false);
                                    itemKey.setEncryptSignText(false);
                                    StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(false, password.toCharArray(), null);
                                    put(KEY_ITEM_KEY, itemKey);
                                    put(KEY_ITEM_KEY_RING, itemKeyRing);
                                    put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                                    next();
                                }
                            });
                            break;
                        case 2:
                            gr.add(new RunnableGroup.Action(){
                                @Override
                                public void runSafe(){
DebugLog.start().send("from key........Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(password.toCharArray()), AppInfo.getGUID(), t, l).generateRing();
                                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, AppInfo.getGUID());
                                    itemKey.setEncryptStrictMode(false);
                                    itemKey.setEncryptSignText(true);
                                    StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                            .setKeyRing(itemKeyRing));
                                    put(KEY_ITEM_KEY, itemKey);
                                    put(KEY_ITEM_KEY_RING, itemKeyRing);
                                    put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                                    next();
                                }
                            });
                            break;
                        case 3:
                            gr.add(new RunnableGroup.Action(){
                                @Override
                                public void runSafe(){
DebugLog.start().send("from key........Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(password.toCharArray()), AppInfo.getGUID(), t, l).generateRing();
                                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, AppInfo.getGUID());
                                    itemKey.setEncryptStrictMode(false);
                                    itemKey.setEncryptSignText(false);
                                    StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                            .setKeyRing(itemKeyRing));
                                    put(KEY_ITEM_KEY, itemKey);
                                    put(KEY_ITEM_KEY_RING, itemKeyRing);
                                    put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                                    next();
                                }
                            });
                            break;
                        case 4:
                            gr.add(new RunnableGroup.Action(){
                                @Override
                                public void runSafe(){
DebugLog.start().send("from key........Strict + Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(password.toCharArray()), AppInfo.getGUID(), t, l).generateRing();
                                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, AppInfo.getGUID());
                                    itemKey.setEncryptStrictMode(true);
                                    itemKey.setEncryptSignText(true);
                                    StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                            .setKeyRing(itemKeyRing));
                                    put(KEY_ITEM_KEY, itemKey);
                                    put(KEY_ITEM_KEY_RING, itemKeyRing);
                                    put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                                    next();
                                }
                            });
                            break;
                        case 5:
                            gr.add(new RunnableGroup.Action(){
                                @Override
                                public void runSafe(){
DebugLog.start().send("from key........Strict + Signed + " + t.getValue() + "/" + l.getValueBit()).end();
                                    ItemKeyRing itemKeyRing = ItemKeyRing.obtain().generateKey(PasswordCipherL2.fromClear(password.toCharArray()), AppInfo.getGUID(), t, l).generateRing();
                                    ItemKey itemKey = ItemKey.obtain().clear().generate(null, AppInfo.getGUID());
                                    itemKey.setEncryptStrictMode(true);
                                    itemKey.setEncryptSignText(false);
                                    StreamDecoder.ItemKeyMaker itemKeyMaker = new ItemKeyMaker(true,null, new dbKey(itemKey)
                                            .setKeyRing(itemKeyRing));
                                    put(KEY_ITEM_KEY, itemKey);
                                    put(KEY_ITEM_KEY_RING, itemKeyRing);
                                    put(KEY_ITEM_KEY_MAKER, itemKeyMaker);
                                    next();
                                }
                            });
                            break;

                    }
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
                            stringEncoder(get(KEY_ITEM_KEY), get(KEY_ITEM_KEY_RING), dataSource).observe(new ObserverValueE<>(this){
                                @Override
                                public void onComplete(String dataCiphered){
                                    putValue(dataCiphered);
                                    next();
                                }
                                @Override
                                public void onException(String s, Throwable e){
                                    Assert.fail(DebugObject.toString(e));
                                    done();
                                }
                            });
                        }
                    });
                    gr.add(new RunnableGroup.Action(){
                        @Override
                        public void runSafe(){
                            stringDecoder(get(KEY_ITEM_KEY_MAKER), dataSource, getValue()).observe(new ObserverStateE(this){
                                @Override
                                public void onComplete(){
                                    next();
                                }
                                @Override
                                public void onException(Throwable e){
                                    Assert.fail(DebugObject.toString(e));
                                    done();
                                }
                            });
                        }
                    });
                }
                gr.add(new RunnableGroup.Action(){
                    @Override
                    public void runSafe(){
                        incrementCounter();
                        next();
                    }
                });
            }
        }
    }
    gr.notifyOnDone(task);
    gr.start();
    return task.getObservable();
}
public static TaskValue<String>.Observable stringEncoder(ItemKey itemKey, ItemKeyRing itemKeyRing, String dataSource){
    TaskValue<String> task = new TaskValue<>();
    StringEncoder encoder = new StringEncoder();
    encoder.setItemKey(itemKey, itemKeyRing);
    encoder.observe(new ObserverEventE<>(myClass(), FileEncoder.Step.FINALISE){
        @Override
        public void onComplete(FileEncoder.Step step, Integer value){
            String encodedData = encoder.getOutString();
            Truth.assertThat(encodedData).isNotEqualTo(dataSource);
            Truth.assertThat(encodedData.length()).isNotEqualTo(dataSource.length());
            task.notifyComplete(encodedData);
        }
        @Override
        public void onException(FileEncoder.Step step, Integer value, Throwable e){
            task.notifyException(e);
        }
    });
    encoder.encode(dataSource);
    return task.getObservable();
}
public static TaskState.Observable stringDecoder(StreamDecoder.ItemKeyMaker itemKeyMaker, String dataSource, String dataCiphered){
    TaskState task = new TaskState();
    StringDecoder decoder = new StringDecoder();
    decoder.setItemKeyMaker(itemKeyMaker);
    decoder.observe(new ObserverEventE<>(myClass(), FileDecoder.Step.FINALISE){
        @Override
        public void onComplete(FileDecoder.Step step, Integer value){
            String dataDeciphered = decoder.getOutString();
            Truth.assertThat(dataDeciphered).isEqualTo(dataSource);
            task.notifyComplete();
        }
        @Override
        public void onException(FileDecoder.Step step, Integer value, Throwable e){
            task.notifyException(e);
        }
    });
    decoder.decode(dataCiphered);
    return task.getObservable();
}

public static TaskState.Observable encodeAndDecodeQr_Random(){
DebugLog.start().track(myClass()).end();
    TaskState task = new TaskState();
    RunnableGroup gr = new RunnableGroup(myClass()).name("encodeAndDecodeQr_Random");
    for(int i=0; i<TEST_UNIT_LOOP; i++){
        String dataSource = BytesTo.StringBase58(UtilsBytes.random(AppRandomNumber.nextInt(20,150)) );
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                qrEncoder(dataSource, outputStream).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        putValue(outputStream.toByteArray());
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        Assert.fail(DebugObject.toString(e));
                        done();
                    }
                });
            }
        });
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                ByteArrayInputStream inputStream = new ByteArrayInputStream(getValue());
                qrDecoder(dataSource, inputStream).observe(new ObserverValueE<>(this){
                    @Override
                    public void onComplete(String s){
                        next();
                    }
                    @Override
                    public void onException(String s, Throwable e){
                        Assert.fail(DebugObject.toString(e));
                        done();
                    }
                });
            }
        });
    }
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            incrementCounter();
            next();
        }
    });
    gr.notifyOnDone(task);
    gr.start();
    return task.getObservable();
}
public static TaskState.Observable qrEncoder(String dataSource, OutputStream output){
    TaskState task = new TaskState();
    new DataQr(dataSource).toGif(output).observe(new ObserverStateE(myClass()){
        @Override
        public void onComplete(){
            task.notifyComplete();
        }
        @Override
        public void onException(Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}
public static TaskValue<String>.Observable qrDecoder(String dataSource, InputStream input){
    TaskValue<String> task = new TaskValue<>();
    new DataQr().fromGif(input, false).observe(new ObserverValueE<>(myClass()){
        @Override
        public void onComplete(String s){
            Truth.assertThat(s).isEqualTo(dataSource);
            task.notifyComplete(s);
        }
        @Override
        public void onException(String s, Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}


}