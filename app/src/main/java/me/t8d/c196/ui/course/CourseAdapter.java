package me.t8d.c196.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.Course;
import me.t8d.c196.models.CourseList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private CourseList courseList;

    public CourseAdapter(CourseList courseList) {
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
        // Set other course details in the view holder
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
