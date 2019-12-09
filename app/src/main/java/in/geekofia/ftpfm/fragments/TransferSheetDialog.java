/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 4:17 AM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.services.DownloadService;

public class TransferSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    IntentFilter filter;

    private ProgressBar progressBar;
    private TextView textViewFileName, progressText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_sheet, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        // views
        textViewFileName = view.findViewById(R.id.transfer_file_name);
        progressBar = view.findViewById(R.id.transfer_progress_bar);
        progressText = view.findViewById(R.id.transfer_progress_text);
    }


    @Override
    public void onClick(View v) {
        // views on click
    }

    // Define the callback for what to do when message is received
    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            String fileName = intent.getStringExtra("file");

            if (progress == 100){
                progressBar.setVisibility(View.GONE);
                progressText.setText("Done");
            } else {
                textViewFileName.setText(fileName);
                progressBar.setProgress(progress);
                progressText.setText(String.valueOf(progress) + "%");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        filter = new IntentFilter(DownloadService.DOWNLOAD_ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }
}
