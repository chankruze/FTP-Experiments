/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 4:17 AM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import in.geekofia.ftpfm.R;

public class TransferSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_sheet, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {



    }


    @Override
    public void onClick(View v) {

    }
}
