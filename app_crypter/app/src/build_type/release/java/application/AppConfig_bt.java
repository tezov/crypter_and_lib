package application;

import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import com.tezov.lib_java.type.unit.UnitByte;

import android.Manifest;

import com.tezov.lib_java_android.application.AppPermission;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java_android.type.image.imageHolder.ImageFormat;

import static com.tezov.lib_java.file.StoragePackage.Path.TEMP;
import static com.tezov.lib_java.file.StoragePackage.Type.PRIVATE_DATA_CACHE;
import static com.tezov.lib_java.file.StoragePackage.Type.PRIVATE_DATA_FILE;

public abstract class AppConfig_bt extends AppConfig{

}
