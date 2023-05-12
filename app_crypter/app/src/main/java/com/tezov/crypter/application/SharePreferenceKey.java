/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java_android.application.AppContext;

import com.tezov.crypter.R;

public class SharePreferenceKey extends com.tezov.lib_java_android.application.SharePreferenceKey{

public static final String SP_KEYSTORE_SPEC_STRING = "KEYSTORE_SPEC";
public static final String SP_KEYSTORE_TIMESTAMP_LONG = "KEYSTORE_TIMESTAMP";
public static final String SP_KEYSTORE_TRIAL_BYTE = "KEYSTORE_TRIAL";

public static final String SP_SESSION_UID_BYTE = "SESSION_UID";
public static final String SP_SESSION_DATA_BYTE = "SESSION_DATA";
public static final String SP_SESSION_TIMESTAMP_LONG = "SESSION_TIMESTAMP";

public static final String SP_USER_TABLES_SPEC_STRING = "USER_TABLES_SPEC";
public static final String SP_USER_TABLES_TRIAL_KEY_STRING = "USER_TABLES_TRIAL_KEY";
public static final String SP_USER_TABLES_TRIAL_VALUE_STRING = "USER_TABLES_TRIAL_VALUE";

public static final String SP_NAVIGATION_LAST_DESTINATION_STRING = "NAVIGATION_LAST_DESTINATION";
public static final String SP_KEY_SHARE_REMEMBER_FORMAT_STRING = "KEY_SHARE_REMEMBER_FORMAT";
public static final String SP_CIPHER_TEXT_REMEMBER_FORMAT_STRING = "CIPHER_TEXT_REMEMBER_FORMAT";
public static final String SP_CIPHER_REMEMBER_ALIAS_CHECKBOX_BOOL = "CIPHER_REMEMBER_ALIAS_CHECKBOX";
public static final String SP_CIPHER_REMEMBER_ALIAS_UID_BYTES = "CIPHER_REMEMBER_ALIAS_UID";

public static final String SP_DESTINATION_DIRECTORY_STRING = AppContext.getResources().getIdentifierName(R.id.pref_destination_directory);
public static final String SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_share_subject_prefix_subject);
public static final String SP_KEYSTORE_AUTO_CLOSE_DELAY_INT = AppContext.getResources().getIdentifierName(R.id.pref_keystore_auto_close_delay);
public static final String SP_KEYSTORE_KEEP_OPEN_DELAY_INT = AppContext.getResources().getIdentifierName(R.id.pref_keystore_keep_open_delay);
public static final String SP_KEYSTORE_KEEP_OPEN_BOOLEAN = AppContext.getResources().getIdentifierName(R.id.pref_keystore_keep_open);
public static final String SP_ALIAS_FORGET_BOOLEAN = AppContext.getResources().getIdentifierName(R.id.pref_alias_forget);
public static final String SP_ALIAS_LOAD_BOOLEAN = AppContext.getResources().getIdentifierName(R.id.pref_alias_load);
public static final String SP_HISTORY_FILE_DELETE_ON_CLOSE_BOOLEAN = AppContext.getResources().getIdentifierName(R.id.pref_history_file_delete_on_close);

public static final String SP_ENCRYPT_KEY_TRANSFORMATION_INT = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_key_transformation);
public static final String SP_ENCRYPT_KEY_LENGTH_INT = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_key_length);

public static final String SP_ENCRYPT_OVERWRITE_FILE_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_file_overwrite);
public static final String SP_ENCRYPT_FILE_NAME_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_file_name);
public static final String SP_ENCRYPT_ADD_TIME_AND_DATE_TO_FILE_NAME_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_add_time_and_date_to_file_name);
public static final String SP_ENCRYPT_DELETE_FILE_ORIGINAL_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_delete_file_original);
public static final String SP_ENCRYPT_STRICT_MODE_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_strict_mode);

public static final String SP_DECRYPT_OVERWRITE_FILE_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_decrypt_overwrite_file);
public static final String SP_DECRYPT_DELETE_FILE_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_decrypt_delete_file);
public static final String SP_ENCRYPT_SIGN_TEXT_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_sign_text);
public static final String SP_ENCRYPT_ADD_DEEPLINK_TO_TEXT_BOOL = AppContext.getResources().getIdentifierName(R.id.pref_encrypt_add_deeplink_to_text);

public final static String SP_SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW_BOOL = "SUGGEST_BUY_PAID_VERSION_DO_NOT_SHOW";
public final static String SP_SUGGEST_BUY_PAID_VERSION_COUNTER_INT = "SUGGEST_BUY_PAID_VERSION_COUNTER";
public final static String SP_OWNED_NO_ADS_INT = "OWNED_NO_ADS";
public final static String SP_SUGGEST_APP_RATING_ALREADY_DONE_BOOL = "SUGGEST_APP_RATING_ALREADY_DONE";
public final static String SP_ADMOB_LAST_SUCCEED_TIME_LONG = "ADMOB_LAST_SUCCEED_TIME";
public final static String SP_ADMOB_FAIL_COUNT_INT = "ADMOB_FAIL_COUNT";
public final static String SP_ADMOB_UNTRUSTED_BOOL = "ADMOB_UNTRUSTED";
public final static String SP_ADMOB_UNTRUSTED_RETABLISH_COUNT_INT = "ADMOB_UNTRUSTED_RETABLISH_COUNT";

}
