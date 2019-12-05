package in.geekofia.ftpfm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.models.Profile;

public class ProfileAdapter extends ListAdapter<Profile, ProfileAdapter.ProfileHolder> {

    private onProfileClickListener profileClickListener;

    public ProfileAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Profile> DIFF_CALLBACK = new DiffUtil.ItemCallback<Profile>() {

        @Override
        public boolean areItemsTheSame(@NonNull Profile oldItem, @NonNull Profile newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Profile oldItem, @NonNull Profile newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getHost().equals(newItem.getHost()) &&
                    oldItem.getPort() == newItem.getPort() &&
                    oldItem.getUser().equals(newItem.getUser()) &&
                    oldItem.getPass().equals(newItem.getPass());
        }
    };

    @NonNull
    @Override
    public ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connection_profile, parent, false);
        return new ProfileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileHolder holder, int position) {
        Profile currentProfile = getItem(position);
        holder.textViewName.setText(currentProfile.getName());
        holder.textViewDesc.setText(currentProfile.getHost());
    }

    public Profile getProfileAt(int position){
        return getItem(position);
    }

    class ProfileHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewDesc;


        ProfileHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.profile_name);
            textViewDesc = itemView.findViewById(R.id.profile_desc);
            Button connectButton = itemView.findViewById(R.id.profile_connect);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (profileClickListener != null && position != RecyclerView.NO_POSITION) {
                        profileClickListener.onProfileClick(getItem(position));
                    }
                }
            });

            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (profileClickListener != null && position != RecyclerView.NO_POSITION) {
                        profileClickListener.onConnectClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface onProfileClickListener {
        void onProfileClick(Profile profile);
        void onConnectClick(Profile profile);
    }

    public void setOnProfileClickListener(onProfileClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }
}
