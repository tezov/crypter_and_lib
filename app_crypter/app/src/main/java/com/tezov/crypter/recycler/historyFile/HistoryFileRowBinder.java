/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.recycler.historyFile;

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
import static com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder.ViewType.DEFAULT;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.data.dbItem.dbHistory;
import com.tezov.crypter.data.item.ItemHistory;
import com.tezov.crypter.data.misc.ClockFormat;
import com.tezov.crypter.dialog.DialogHistoryFileInfo;
import com.tezov.crypter.dialog.DialogMissingFile;
import com.tezov.crypter.fragment.FragmentCipherFile.Operation;
import com.tezov.crypter.fragment.FragmentCipherFile.Step;
import com.tezov.crypter.view.LedState;
import com.tezov.crypter.view.LedState.State;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.file.UtilsFile;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.TextViewScrollable;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowBinder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;

public class HistoryFileRowBinder extends RecyclerListRowBinder<HistoryFileRowBinder.RowHolder, dbHistory>{

public HistoryFileRowBinder(HistoryFileRowManager rowManager){
    super(rowManager);
}

@Override
public ViewType.Is getViewType(){
    return DEFAULT;
}

@Override
public RowHolder create(ViewGroup parent){
    return new RowHolder(parent);
}

public static class RowHolder extends RecyclerListRowHolder<dbHistory>{
    private final static int SHARE_FILE_NAME_LENGTH = 8;
    protected boolean isButtonEnabled = true;
    private dbHistory data = null;
    private ItemHistory.File file = null;
    private final View containerInfo;
    private final TextViewScrollable lblFileNameIn;
    private final TextViewScrollable lblFileNameOut;
    private final LedState stepTransferResult;
    private final ButtonMultiIconMaterial icFileIn;
    private final ButtonMultiIconMaterial btnFileIn;
    private final ButtonMultiIconMaterial btnFileOut;
    private final ButtonMultiIconMaterial icFileOut;
    private final TextView lblOperation;
    private final TextView lblCreatedDate;

    public RowHolder(ViewGroup parent){
        super(R.layout.recycler_history_file_row, parent);
        containerInfo = itemView.findViewById(R.id.container_info);
        containerInfo.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View view){
                if(isButtonEnabled){
                    isButtonEnabled = false;
                    openDialog_Info();
                }

            }
        });
        stepTransferResult = itemView.findViewById(R.id.led_result);
        lblFileNameIn = itemView.findViewById(R.id.lbl_file_name_in);
        lblFileNameOut = itemView.findViewById(R.id.lbl_file_name_out);
        icFileIn = itemView.findViewById(R.id.ic_file_in);
        icFileIn.setClickable(false);
        btnFileIn = itemView.findViewById(R.id.btn_file_in);
        btnFileIn.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(isButtonEnabled){
                    isButtonEnabled = false;
                    int index = btnFileIn.getIndex();
                    if(index == 1){
                        share(file.getUriIn());
                    } else if(index == 2){
                        open(file.getUriIn());
                    } else if(index == 0){
                        openDialog_Missing(file.getUriInPath());
                    } else {
DebugException.start().unknown("index", index).end();
                        isButtonEnabled = true;
                    }
                }
            }
        });
        icFileOut = itemView.findViewById(R.id.ic_file_out);
        icFileOut.setClickable(false);
        btnFileOut = itemView.findViewById(R.id.btn_file_out);
        btnFileOut.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(isButtonEnabled){
                    isButtonEnabled = false;
                    int index = btnFileOut.getIndex();
                    if(index == 1){
                        share(file.getUriOut());
                    } else if(index == 2){
                        open(file.getUriOut());
                    } else if(index == 0){
                        openDialog_Missing(file.getUriOutPath());
                    } else {
DebugException.start().unknown("index", index).end();
                        isButtonEnabled = true;
                    }
                }
            }
        });
        lblOperation = itemView.findViewById(R.id.lbl_operation);
        lblCreatedDate = itemView.findViewById(R.id.lbl_created_date);
    }

    @Override
    public dbHistory get(){
        return data;
    }
    @Override
    public void set(dbHistory data){
        this.data = data;
        if(data == null){
            file = null;
        } else {
            file = data.getItem().getData();
        }
        updateView();
    }
    private void updateView(){
        if(file == null){
            lblFileNameIn.setText(null);
            lblFileNameOut.setText(null);
            stepTransferResult.setState(State.NEUTRAL);
            icFileIn.setVisibility(View.GONE);
            btnFileIn.setEnabled(false);
            btnFileIn.setVisibility(View.INVISIBLE);
            icFileOut.setVisibility(View.GONE);
            btnFileOut.setEnabled(false);
            btnFileOut.setVisibility(View.INVISIBLE);
            lblOperation.setText(null);
            lblCreatedDate.setText(null);
        } else {
            lblFileNameIn.setText(file.getUriInPath());
            lblFileNameOut.setText(file.getUriOutPath());
            lblFileNameIn.moveToEnd();
            lblFileNameOut.moveToEnd();
            Step result = file.getResult();
            switch(result){
                case SUCCEED:
                    stepTransferResult.setState(State.SUCCEED);
                    break;
                case FAILED:
                    stepTransferResult.setState(State.FAILED);
                    break;
                case ABORTED:
                    stepTransferResult.setState(State.BUSY);
                    break;
                default:
                    stepTransferResult.setState(State.NEUTRAL);
                    break;
            }
            Operation operation = file.getOperation();
            boolean uriInHide = true;
            UriW uriIn = file.getUriIn();
            if(uriIn != null){
                if(uriIn.canRead()){
                    if(operation == Operation.ENCRYPT){
                        btnFileIn.setIndex(2);
                    } else if(operation == Operation.DECRYPT){
                        btnFileIn.setIndex(1);
                    }
                } else {
                    btnFileIn.setIndex(0);
                }
                btnFileIn.setVisibility(View.VISIBLE);
                btnFileIn.setEnabled(true);
                uriInHide = false;
            }
            if(uriInHide){
                btnFileIn.setVisibility(View.INVISIBLE);
                btnFileIn.setEnabled(false);
            }
            boolean uriOutHide = true;
            if(result == Step.SUCCEED){
                UriW uriOut = file.getUriOut();
                if(uriOut != null){
                    if(uriOut.canRead()){
                        if(operation == Operation.ENCRYPT){
                            btnFileOut.setIndex(1);
                        } else if(operation == Operation.DECRYPT){
                            btnFileOut.setIndex(2);
                        }
                    } else {
                        btnFileOut.setIndex(0);
                    }
                    btnFileOut.setVisibility(View.VISIBLE);
                    btnFileOut.setEnabled(true);
                    uriOutHide = false;
                }
            }
            if(uriOutHide){
                btnFileOut.setVisibility(View.INVISIBLE);
                btnFileOut.setEnabled(false);
            }
            icFileIn.setVisibility(View.VISIBLE);
            icFileOut.setVisibility(View.VISIBLE);
            if(operation == Operation.ENCRYPT){
                lblOperation.setText(R.string.lbl_operation_encrypt);
                icFileIn.setIndex(0);
                icFileOut.setIndex(1);
            } else if(operation == Operation.DECRYPT){
                lblOperation.setText(R.string.lbl_operation_decrypt);
                icFileIn.setIndex(1);
                icFileOut.setIndex(0);
            } else {
DebugException.start().unknown("operation", operation).end();
            }
            lblCreatedDate.setText(ClockFormat.longToDateTime_FULL(data.getItem().getTimestamp()));
        }
    }

    protected void openDialog_Info(){
        if(file != null){
            DialogHistoryFileInfo.State state = new DialogHistoryFileInfo.State();
            DialogHistoryFileInfo.Param param = state.obtainParam();
            param.setTimestamp(file.getTimestamp()).setSignatureApp(file.getSignatureApp()).setSignatureKey(file.getSignatureKey());
            Operation operation = file.getOperation();
            if(operation == Operation.ENCRYPT){
                param.setOperation(R.string.lbl_operation_encrypt);
            } else if(operation == Operation.DECRYPT){
                param.setOperation(R.string.lbl_operation_decrypt);
            }
            Step result = file.getResult();
            if(result == Step.SUCCEED){
                param.setResult(R.string.lbl_result_succeed);
            } else if(result == Step.FAILED){
                param.setResult(R.string.lbl_result_failed);
            } else if(result == Step.ABORTED){
                param.setResult(R.string.lbl_result_aborted);
            }
            Navigate.To(DialogHistoryFileInfo.class, state).observe(new ObserverValueE<DialogHistoryFileInfo>(this){
                @Override
                public void onComplete(DialogHistoryFileInfo dialog){
                    dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
                        @Override
                        public void onComplete(Event.Is is, Object object){
                            isButtonEnabled = true;
                        }
                    });
                }
                @Override
                public void onException(DialogHistoryFileInfo dialogHistoryInfo, Throwable e){
                    isButtonEnabled = true;
                }
            });
        } else {
            isButtonEnabled = true;
        }
    }

    protected void share(UriW uri){
        String fileName = UtilsFile.shortenName(uri.getFullName(), SHARE_FILE_NAME_LENGTH);
        String name = AppContext.getResources().getString(R.string.share_encrypt_subject) + "_" + fileName;
        AppInfo.share(name, uri).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                isButtonEnabled = true;
            }
            @Override
            public void onException(Throwable e){
                isButtonEnabled = true;
            }
        });
    }
    protected void open(UriW uri){
        AppInfo.open(uri).observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                isButtonEnabled = true;
            }
            @Override
            public void onException(Throwable e){
                isButtonEnabled = true;
            }
        });
    }
    protected void openDialog_Missing(String path){
        Navigate.To(DialogMissingFile.class, DialogMissingFile.newStateDefault(path)).observe(new ObserverValueE<DialogMissingFile>(this){
            @Override
            public void onComplete(DialogMissingFile dialog){
                dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
                    @Override
                    public void onComplete(Event.Is event, Object o){
                        isButtonEnabled = true;
                    }
                });
            }
            @Override
            public void onException(DialogMissingFile dialog, Throwable e){
DebugException.start().log(e).end();
                isButtonEnabled = true;
            }
        });

        isButtonEnabled = true;
    }

}

}
