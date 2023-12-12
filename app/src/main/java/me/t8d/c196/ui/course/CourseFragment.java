package me.t8d.c196.ui.course;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.t8d.c196.R;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.repository.DataManager;
import me.t8d.c196.ui.assessment.AssessmentAddEditFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment implements CourseAdapter.CourseAdapterCallback {

    private RecyclerView recyclerView;
    @Override
    public void onCourseSelected(int itemId) {
        openAddEditFragment(true, itemId);
    }
    private CourseList courseList;
    private DataManager dataManager;
    private CourseAdapter adapter;

    public CourseFragment() {
        // Required empty public constructor
    }
    private void openAddEditFragment(boolean isEditMode, int itemId) {
        CourseAddEditFragment addEditFragment = new CourseAddEditFragment();
        addEditFragment.setOnCourseUpdatedListener(adapter);
        Bundle args = new Bundle();
        args.putBoolean("isEditMode", isEditMode);
        if (isEditMode) {
            args.putInt("itemId", itemId); // Pass item ID for edit mode
        }
        addEditFragment.setArguments(args);

        // Replace with the code to open the fragment (e.g., using FragmentManager)
        addEditFragment.show(getParentFragmentManager(), "CourseAddEditFragment");
    }

    public static CourseFragment newInstance(String param1, String param2) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager();
        courseList = dataManager.GetCourseList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        recyclerView = view.findViewById(R.id.courseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CourseAdapter(courseList, this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addButton = view.findViewById(R.id.addCourseButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddEditFragment(false, -1); // false indicates Add mode, -1 for no specific item ID
            }
        });
    }
}