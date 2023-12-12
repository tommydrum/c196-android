package me.t8d.c196.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.Course;
import me.t8d.c196.models.CourseList;

public class CurrentCoursesAdapter extends RecyclerView.Adapter<CurrentCoursesAdapter.ViewHolder> {
    private CourseList courseList;
    public CurrentCoursesAdapter(CourseList courseList) {
        this.courseList = courseList;
    }
    @NonNull
    @Override
    public CurrentCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_course_item, parent, false);
        return new CurrentCoursesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentCoursesAdapter.ViewHolder holder, int position) {
        Course course = courseList.GetCourseList().get(position);
        holder.courseTitle.setText(course.GetTitle());
    }

    @Override
    public int getItemCount() {
        return courseList.GetCourseList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTitle;
        public ViewHolder(View v) {
            super(v);
            courseTitle = v.findViewById(R.id.currentCourseTitle);
        }
    }
}
