/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 5:06 AM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.models.RemoteFile;

import static in.geekofia.ftpfm.utils.CustomFunctions.fetchDrawable;
import static in.geekofia.ftpfm.utils.CustomFunctions.fetchString;
import static in.geekofia.ftpfm.utils.CustomFunctions.toggleProp;

public class RemoteFilesAdapter extends RecyclerView.Adapter<RemoteFilesAdapter.RemoteFileHolder> {

    private onRemoteFileClickListener mRemoteFileClickListener;
    private Context mContext;
    private ArrayList<RemoteFile> mRemoteFiles;

    public RemoteFilesAdapter(Context context, ArrayList<RemoteFile> remoteFiles) {
        this.mContext = context;
        this.mRemoteFiles = remoteFiles;
    }


//    private static final DiffUtil.ItemCallback<RemoteFile> DIFF_CALLBACK = new DiffUtil.ItemCallback<RemoteFile>() {
//
//        @Override
//        public boolean areItemsTheSame(@NonNull RemoteFile oldRemoteFile, @NonNull RemoteFile newRemoteFile) {
//            return oldRemoteFile.getSize() == newRemoteFile.getSize();
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull RemoteFile oldRemoteFile, @NonNull RemoteFile newRemoteFile) {
//            return oldRemoteFile.getName().equals(newRemoteFile.getName()) &&
//                    oldRemoteFile.getAbsolutePath().equals(newRemoteFile.getAbsolutePath()) &&
//                    oldRemoteFile.getSizeInBytes() == newRemoteFile.getSizeInBytes() &&
//                    oldRemoteFile.getUser().equals(newRemoteFile.getUser()) &&
//                    oldRemoteFile.getPermission().equals(newRemoteFile.getPermission());
//        }
//    };

    @NonNull
    @Override
    public RemoteFileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new RemoteFileHolder(view, mRemoteFileClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RemoteFilesAdapter.RemoteFileHolder holder, int position) {
        RemoteFile currentRemoteFile = mRemoteFiles.get(position);

        switch (currentRemoteFile.getTypeItem()) {
            case RemoteFile.DIRECTORY:
                holder.name.setText(currentRemoteFile.getName());
                toggleProp(holder.name, RelativeLayout.CENTER_VERTICAL, true);
                holder.details.setVisibility(View.GONE);
                if (currentRemoteFile.getNumItems() > 0) {
                    holder.icon.setImageDrawable(fetchDrawable(mContext, currentRemoteFile.getIconId()));
                } else {
                    holder.icon.setImageDrawable(fetchDrawable(mContext, R.drawable.ic_folder));
                }
                break;
            case RemoteFile.FILE:
                holder.name.setText(currentRemoteFile.getName());
                toggleProp(holder.name, RelativeLayout.CENTER_VERTICAL, false);
                holder.details.setVisibility(View.VISIBLE);
                holder.size.setText(mContext.getResources().getString(R.string.fs_unit_bytes, currentRemoteFile.getSize(), currentRemoteFile.getUnit()));
                holder.date.setText(currentRemoteFile.getDate());
                holder.time.setText(currentRemoteFile.getTime());
                holder.icon.setImageDrawable(fetchDrawable(mContext, currentRemoteFile.getIconId()));
                holder.type.setText(fetchString(mContext, currentRemoteFile.getTypeId()));
                break;
            case RemoteFile.UP:
                holder.name.setText(currentRemoteFile.getName());
                toggleProp(holder.name, RelativeLayout.CENTER_VERTICAL, true);
                holder.details.setVisibility(View.GONE);
                holder.icon.setImageDrawable(fetchDrawable(mContext, currentRemoteFile.getIconId()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mRemoteFiles.size();
    }

    class RemoteFileHolder extends RecyclerView.ViewHolder {
        private TextView name, size, date, time, type;
        private LinearLayout details;
        private ImageView icon;

        RemoteFileHolder(@NonNull final View itemView, final onRemoteFileClickListener remoteFileClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.file_name);
            icon = itemView.findViewById(R.id.file_icon);
            details = itemView.findViewById(R.id.file_details);
            size = itemView.findViewById(R.id.file_size);
            date = itemView.findViewById(R.id.file_date);
            time = itemView.findViewById(R.id.file_time);
            type = itemView.findViewById(R.id.file_type);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (remoteFileClickListener != null && position != RecyclerView.NO_POSITION) {
                        remoteFileClickListener.onRemoteFileClick(itemView, mRemoteFiles.get(position));
                    }
                }
            });
        }
    }

    public interface onRemoteFileClickListener {
        void onRemoteFileClick(View view, RemoteFile remoteFile);
    }

    public void setOnRemoteFileClickListener(onRemoteFileClickListener remoteFileClickListener) {
        this.mRemoteFileClickListener = remoteFileClickListener;
    }
}
