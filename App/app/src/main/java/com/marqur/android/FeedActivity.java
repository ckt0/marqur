package com.marqur.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FeedActivity extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[] myDataset = {"Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",};


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_feed, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.feed_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FeedAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
    }

    public static FeedActivity newInstance() {
        FeedActivity FeedFragment = new FeedActivity();
        Bundle args = new Bundle();
        FeedFragment.setArguments(args);
        return FeedFragment;
    }

}
