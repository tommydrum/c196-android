package me.t8d.c196.ui.assessment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import me.t8d.c196.R;
import me.t8d.c196.models.Assessment;
import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.repository.DataManager;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.ViewHolder> implements AssessmentAddEditFragment.OnAssessmentUpdatedListener {
    @Override
    public void onAssessmentUpdated(int position, Assessment updatedAssessment) {
        if (position != -1) {
            assessmentList.GetAssessmentList().set(position, updatedAssessment);
            notifyItemChanged(position);
        } else {
            assessmentList.addAssessment(updatedAssessment);
            notifyDataSetChanged();
        }
        DataManager dataManager = new DataManager();
        dataManager.saveAllData();
    }

    @Override
    public void onAssessmentDeleted(int position, Assessment assessment) {
        assessmentList.removeAssessment(assessment);
        //Delete any associated assessments found within Courses
        DataManager dm = new DataManager();
        dm.GetCourseList().GetCourseList().forEach(course -> {
            AssessmentList tmp = course.GetAssessmentList();
            if (tmp != null)
                tmp.removeAssessment(assessment); //If assessment is not found, it does nothing
        });
        dm.saveAllData();
        notifyItemRemoved(position);
    }

    public interface AssessmentAdapterCallback {
        void onAssessmentSelected(int itemId);
    }

    private AssessmentList assessmentList;
    private AssessmentAdapterCallback callback;

    public AssessmentAdapter(AssessmentList assessmentList, AssessmentAdapterCallback callback) {
        this.assessmentList = assessmentList;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Assessment assessment = assessmentList.GetAssessmentList().get(position);
        holder.assessmentTitle.setText(assessment.GetTitle());
        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onAssessmentSelected(position);
            }
        });
        // Set other assessment details in the view holder
    }

    @Override
    public int getItemCount() {
        return assessmentList.GetAssessmentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView assessmentTitle;
        // Other views

        public ViewHolder(View itemView) {
            super(itemView);
            assessmentTitle = itemView.findViewById(R.id.assessmentTitle);
            // Initialize other views
        }
    }
}