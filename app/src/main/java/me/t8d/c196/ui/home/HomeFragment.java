package me.t8d.c196.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.models.Term;
import me.t8d.c196.repository.DataManager;

public class HomeFragment extends Fragment {

    private RecyclerView currentCoursesRecyclerView;
    private RecyclerView upcomingAssessmentsRecyclerView;
    TextView currentTermTextView;
    DataManager dataManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        currentCoursesRecyclerView = view.findViewById(R.id.recycler_current_courses);
        upcomingAssessmentsRecyclerView = view.findViewById(R.id.recycler_upcoming_assessments);
        currentCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingAssessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentTermTextView = view.findViewById(R.id.text_current_term);
        // find current term
        Term currentTerm = dataManager.GetTermList().GetCurrentTerm();
        if (currentTerm == null) {
            currentTermTextView.setText("Not currently enrolled in a term");
        } else {
            currentTermTextView.setText(currentTerm.GetTermName());
        }
        // Find current courses and upcoming assessments
        CourseList currentCourses = dataManager.GetCourseList().GetCurrentCourses();
        currentCoursesRecyclerView.setAdapter(new CurrentCoursesAdapter(currentCourses));
        AssessmentList upcomingAssessments = dataManager.GetAssessmentList().GetUpcomingAssessments();
        upcomingAssessmentsRecyclerView.setAdapter(new UpcomingAssessmentsAdapter(upcomingAssessments));

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}