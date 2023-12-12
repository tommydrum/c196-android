package me.t8d.c196.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.Assessment;
import me.t8d.c196.models.AssessmentList;

public class UpcomingAssessmentsAdapter extends RecyclerView.Adapter<UpcomingAssessmentsAdapter.ViewHolder> {
    private AssessmentList assessmentList;
    public UpcomingAssessmentsAdapter(AssessmentList assessmentList) {
        this.assessmentList = assessmentList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_assessments_item, parent, false);
        return new UpcomingAssessmentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assessment assessment = assessmentList.GetAssessmentList().get(position);
        holder.assessmentTitle.setText(assessment.GetTitle());
    }

    @Override
    public int getItemCount() {
        return assessmentList.GetAssessmentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView assessmentTitle;
        public ViewHolder(View v) {
            super(v);
            assessmentTitle = v.findViewById(R.id.upcoming_assessments_title);
        }
    }
}
