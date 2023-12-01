package me.t8d.c196.ui.term;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.t8d.c196.R;
import me.t8d.c196.models.TermList;
import me.t8d.c196.repository.DataManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TermFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TermFragment extends Fragment {

    private RecyclerView recyclerView;
    private TermList termList;
    private DataManager dataManager;
    private TermAdapter adapter;

    public TermFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TermFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TermFragment newInstance(String param1, String param2) {
        TermFragment fragment = new TermFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager();
        termList = dataManager.GetTermList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_term, container, false);
        recyclerView = view.findViewById(R.id.termRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TermAdapter(termList);
        recyclerView.setAdapter(adapter);
        return view;
    }
}