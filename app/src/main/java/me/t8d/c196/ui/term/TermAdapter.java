package me.t8d.c196.ui.term;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.Term;
import me.t8d.c196.models.TermList;

import android.widget.TextView;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.ViewHolder> {
    private TermList termList;

    public TermAdapter(TermList termList) {
        this.termList = termList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.term_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Term term = termList.GetTermList().get(position);
        holder.termTitle.setText(term.GetTermName());
        // Set other assessment details in the view holder
    }

    @Override
    public int getItemCount() {
        return termList.GetTermList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView termTitle;
        // Other views

        public ViewHolder(View itemView) {
            super(itemView);
            termTitle = itemView.findViewById(R.id.termTitle);
            // Initialize other views
        }
    }
}
