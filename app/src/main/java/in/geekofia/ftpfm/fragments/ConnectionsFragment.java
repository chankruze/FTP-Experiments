package in.geekofia.ftpfm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import in.geekofia.ftpfm.activities.FilesActivity;
import in.geekofia.ftpfm.viewmodels.ProfileViewModel;
import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.adapters.ProfileAdapter;
import in.geekofia.ftpfm.models.Profile;

import static in.geekofia.ftpfm.activities.MainActivity.ADD_EDIT_CONNECTION_FRAGMENT;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_OPERATION_CODE;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_PROFILE;
import static in.geekofia.ftpfm.activities.MainActivity.EXTRA_TITLE;
import static in.geekofia.ftpfm.activities.MainActivity.HOME_FRAGMENT;
import static in.geekofia.ftpfm.activities.MainActivity.OPERATION_CODE_UPDATE;

public class ConnectionsFragment extends Fragment {

    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;
    ProfileAdapter profileAdapter = new ProfileAdapter();
    private FloatingActionButton floatingActionButton;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connections, container, false);

        getActivity().setTitle("Manage Connections");
//        setHasOptionsMenu(true);
        initViews(view);

        loadConnectionProfiles();

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Profile newProfile = ((Profile) bundle.getSerializable(EXTRA_PROFILE));
            int operationCode = bundle.getInt(EXTRA_OPERATION_CODE);

            if (operationCode == OPERATION_CODE_UPDATE){
                profileViewModel.update(newProfile);
            } else {
                profileViewModel.insert(newProfile);
            }
        }

        return view;
    }

    private void initViews(View view) {
        // Display back (←) arrow
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Listen click on back arrow
        mToolBar = getActivity().findViewById(R.id.toolbar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                if (getActivity().getSupportFragmentManager() != null) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, HOME_FRAGMENT).commit();
                }
            }
        });

        // Initialize recycler view
        mRecyclerView = view.getRootView().findViewById(R.id.recycler_view_connections);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(profileAdapter);

        // Initialize Floating Action Button
        floatingActionButton = view.getRootView().findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditConnectionFragment newConnectionFragment = new AddEditConnectionFragment();
                if (getActivity().getSupportFragmentManager() != null) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newConnectionFragment, ADD_EDIT_CONNECTION_FRAGMENT).commit();
                }
            }
        });
    }

    private void loadConnectionProfiles() {
        profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
        profileViewModel.getAllProfiles().observe(this, new Observer<List<Profile>>() {
            @Override
            public void onChanged(List<Profile> profiles) {
//                Toast.makeText(getContext(), "ON CHANGE CALLED", Toast.LENGTH_SHORT).show();
                profileAdapter.setProfiles(profiles);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                profileViewModel.delete(profileAdapter.getProfileAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mRecyclerView);

        profileAdapter.setOnProfileClickListener(new ProfileAdapter.onProfileClickListener() {

            @Override
            public void onProfileClick(Profile profile) {
                AddEditConnectionFragment addEditConnectionFragment = new AddEditConnectionFragment();
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_TITLE, "Edit " + profile.getName());
                bundle.putSerializable(EXTRA_PROFILE, profile);
                addEditConnectionFragment.setArguments(bundle);
                if (getActivity().getSupportFragmentManager() != null) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addEditConnectionFragment, ADD_EDIT_CONNECTION_FRAGMENT).commit();
                }
            }

            @Override
            public void onConnectClick(Profile profile) {
                Intent intent = new Intent(getContext(), FilesActivity.class);
                intent.putExtra("PROFILE", profile);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.toolbar_info);
//        if(item != null)
//            item.setVisible(false);
//    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_frag_connections, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.toolbar_close:
//                Toast.makeText(getContext(), "Closing...", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return false;
//        }
//    }
}
