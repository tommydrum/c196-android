package me.t8d.c196.ui.term;

import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.t8d.c196.R;
import me.t8d.c196.models.Term;
import me.t8d.c196.models.TermList;
import me.t8d.c196.repository.DataManager;

import android.widget.TextView;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.ViewHolder> implements TermAddEditFragment.OnTermUpdatedListener {
    private TermList termList;

    @Override
    public void onTermUpdated(int position, Term updatedTerm) {
        if (position != -1) {
            termList.GetTermList().set(position, updatedTerm);
            notifyItemChanged(position);
        } else {
            termList.AddTerm(updatedTerm);
            notifyDataSetChanged();
        }
        DataManager dataManager = new DataManager();
        dataManager.saveAllData();
    }

    @Override
    public void onTermDeleted(int position, Term term) {
        termList.RemoveTerm(term);
        DataManager dm = new DataManager();
        dm.saveAllData();
        notifyItemRemoved(position);
    }

    public interface TermAdapterCallback {
        void onTermSelected(int itemId);
    }
    private TermAdapterCallback callback;

    public TermAdapter(TermList termList, TermAdapterCallback callback)
    {
        this.termList = termList;
        this.callback = callback;
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
        holder.itemView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onTermSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return termList.GetTermList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView termTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            termTitle = itemView.findViewById(R.id.termTitle);
        }
    }
}
