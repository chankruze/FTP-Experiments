package in.geekofia.ftpfm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.models.Profile;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileHolder> {

    private List<Profile> profiles = new ArrayList<>();
    private onProfileClickListener profileClickListener;

    @NonNull
    @Override
    public ProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connection_profile, parent, false);
        return new ProfileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileHolder holder, int position) {
        Profile currentProfile = profiles.get(position);
        holder.textViewName.setText(currentProfile.getName());
        holder.textViewDesc.setText(currentProfile.getHost());
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
        notifyDataSetChanged();
    }

    public Profile getProfileAt(int position){
        return profiles.get(position);
    }

    class ProfileHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewDesc;


        public ProfileHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.profile_name);
            textViewDesc = itemView.findViewById(R.id.profile_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (profileClickListener != null && position != RecyclerView.NO_POSITION) {
                        profileClickListener.onProfileClick(profiles.get(position));
                    }
                }
            });
        }
    }

    public interface onProfileClickListener {
        void onProfileClick(Profile profile);
    }

    public void setOnProfileClickListener(onProfileClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }
}
