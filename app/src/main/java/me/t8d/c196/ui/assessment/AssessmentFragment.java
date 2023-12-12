package me.t8d.c196.ui.assessment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.t8d.c196.R;
import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.repository.DataManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssessmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssessmentFragment extends Fragment implements AssessmentAdapter.AssessmentAdapterCallback {

    private RecyclerView recyclerView;
    private AssessmentAdapter adapter;
    private AssessmentList assessmentList;
    private DataManager dataManager;

    public AssessmentFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAssessmentSelected(int itemId) {
        openAddEditFragment(true, itemId);
    }
    public static AssessmentFragment newInstance(String param1, String param2) {
        AssessmentFragment fragment = new AssessmentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void openAddEditFragment(boolean isEditMode, int itemId) {
        AssessmentAddEditFragment addEditFragment = new AssessmentAddEditFragment();
        addEditFragment.setOnAssessmentUpdatedListener(adapter);
        Bundle args = new Bundle();
        args.putBoolean("isEditMode", isEditMode);
        if (isEditMode) {
            args.putInt("itemId", itemId); // Pass item ID for edit mode
        }
        addEditFragment.setArguments(args);

        // Replace with the code to open the fragment (e.g., using FragmentManager)
        addEditFragment.show(getParentFragmentManager(), "AssessmentAddEditFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager();
        assessmentList = dataManager.GetAssessmentList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assessment, container, false);
        recyclerView = view.findViewById(R.id.assessmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssessmentAdapter(assessmentList, this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addButton = view.findViewById(R.id.addAssessmentButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddEditFragment(false, -1); // false indicates Add mode, -1 for no specific item ID
            }
        });
    }
}