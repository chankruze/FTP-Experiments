package in.geekofia.ftpfm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import in.geekofia.ftpfm.R;

public class ConnectionsFragment extends Fragment {

    private Toolbar mToolBar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connections, container, false);


        getActivity().setTitle("Manage Connections");
//        setHasOptionsMenu(true);
        initViews(view);
        loadConnectionProfiles();

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
                if (getActivity().getSupportFragmentManager() != null){
                    getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment, "HOME_FRAGMENT").commit();
                }
            }
        });

        // Initialize recycler view
        mRecyclerView = view.getRootView().findViewById(R.id.recycler_view_connections);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // Initialize Floating Action Button
        floatingActionButton = view.getRootView().findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditConnectionFragment editConnectionFragment = new EditConnectionFragment();
                if (getActivity().getSupportFragmentManager() != null){
                    getActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editConnectionFragment, "EDIT_CONNECTION_FRAGMENT").commit();
                }
            }
        });
    }

    private void loadConnectionProfiles() {
        // TODO : Feed data to recycler view to create a list of connections
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
