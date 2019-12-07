/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 4:17 AM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.services.RemoteFileDownloadService;
import in.geekofia.ftpfm.utils.TransferResultReceiver;

public class TransferSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener, TransferResultReceiver.TransferProgressReceiver {

    private TransferResultReceiver transferResultReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_sheet, container, false);

        initViews(view);

        // Register the intent service in the activity
        registerService();

        return view;
    }

    private void initViews(View view) {
        // views
    }


    @Override
    public void onClick(View v) {
        // views on click
    }

    private void registerService() {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), RemoteFileDownloadService.class);

        // pass the ResultReceiver via the intent to the intent service
        transferResultReceiver = new TransferResultReceiver(new Handler(), this);
        intent.putExtra("transferProgressReceiver", transferResultReceiver);
        getActivity().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // Handle the results from the intent service here!
    }

    @Override
    public void onStop() {
        super.onStop();

        if(transferResultReceiver != null) {
            transferResultReceiver.setTransferProgressReceiver(null);
        }
    }
}
