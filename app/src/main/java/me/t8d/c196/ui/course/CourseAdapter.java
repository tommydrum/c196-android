package me.t8d.c196.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.Course;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.repository.DataManager;
import me.t8d.c196.ui.assessment.AssessmentAdapter;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> implements CourseAddEditFragment.OnCourseUpdatedListener {
    @Override
    public void onCourseUpdated(int position, Course updatedCourse) {
        if (position != -1) {
            courseList.GetCourseList().set(position, updatedCourse);
            notifyItemChanged(position);
        } else {
            courseList.AddCourse(updatedCourse);
            notifyDataSetChanged();
        }
        DataManager dataManager = new DataManager();
        dataManager.saveAllData();
    }
    @Override
    public void onCourseDeleted(int position, Course course) {
        courseList.RemoveCourse(course);
        //Delete any associated courses found within Terms
        DataManager dm = new DataManager();
        dm.GetTermList().GetTermList().forEach(term -> {
            CourseList tmp = term.GetCourseList();
            if (tmp != null)
                    tmp.RemoveCourse(course); //If course is not found, it does nothing
        });
        dm.saveAllData();
        notifyItemRemoved(position);
    }
    private CourseList courseList;
    public interface CourseAdapterCallback {
        void onCourseSelected(int itemId);
    }
    private CourseAdapterCallback callback;

    public CourseAdapter(CourseList courseList, CourseAdapter.CourseAdapterCallback callback) {
        this.callback = callback;
        this.courseList = courseList;
    }

    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseAdapter.ViewHolder holder, int position) {
        Course course = courseList.GetCourseList().get(position);
        holder.courseTitle.setText(course.GetTitle());
        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onCourseSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.GetCourseList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTitle;
        // Other views

        public ViewHolder(View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            // Initialize other views
        }
    }
}
