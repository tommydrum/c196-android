package me.t8d.c196.ui.term;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.t8d.c196.R;
import me.t8d.c196.models.TermList;
import me.t8d.c196.repository.DataManager;
import me.t8d.c196.ui.course.CourseAddEditFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TermFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TermFragment extends Fragment implements TermAdapter.TermAdapterCallback {

    private RecyclerView recyclerView;
    @Override
    public void onTermSelected(int itemId) { openAddEditFragment(true, itemId); }
    private TermList termList;
    private DataManager dataManager;
    private TermAdapter adapter;

    public TermFragment() {
        // Required empty public constructor
    }
    private void openAddEditFragment(boolean isEditMode, int itemId) {
        TermAddEditFragment addEditFragment = new TermAddEditFragment();
        addEditFragment.setOnTermUpdatedListener(adapter);
        Bundle args = new Bundle();
        args.putBoolean("isEditMode", isEditMode);
        if (isEditMode) {
            args.putInt("itemId", itemId); // Pass item ID for edit mode
        }
        addEditFragment.setArguments(args);

        // Replace with the code to open the fragment (e.g., using FragmentManager)
        addEditFragment.show(getParentFragmentManager(), "TermAddEditFragment");
    }

    public static TermFragment newInstance(String param1, String param2) {
        TermFragment fragment = new TermFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager();
        termList = dataManager.GetTermList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term, container, false);
        recyclerView = view.findViewById(R.id.termRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TermAdapter(termList, this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addButton = view.findViewById(R.id.addTermButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddEditFragment(false, -1); // false indicates Add mode, -1 for no specific item ID
            }
        });
    }
}