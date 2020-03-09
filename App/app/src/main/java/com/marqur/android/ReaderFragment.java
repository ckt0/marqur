package com.marqur.android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


public class ReaderFragment extends Fragment {

    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;
    private String[] myDataset = {"Test1","Test2","Test3","Test4",
            "Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",
            "Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",
            "Test1","Test2","Test3","Test4","Test1","Test2","Test3","Test4",};




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_reader, null);
        return root;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupView();
    }



    private void setupView() {

        // Query used to build the Feed from
        Query query = ((MainActivity)requireActivity()).markersCRef.orderBy("date_created", Query.Direction.DESCENDING);

        // Using query and the Marker class to build the options needed by Feed Adapter
        FirestoreRecyclerOptions<Marker> options = new FirestoreRecyclerOptions.Builder<Marker>()
                .setQuery(query, Marker.class)
                .build();

        // Fetch RecyclerView
        feedRecyclerView = (RecyclerView) requireView().findViewById(R.id.feed_recycler_view);

        // Use a linear layout manager
        feedRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));

        // Increases performance. Use only if content does not change RecyclerView's layout size
        feedRecyclerView.setHasFixedSize(true);

        // Create a new Feed Adapter and attach it to Feed RecyclerView
        feedAdapter = new FeedAdapter(options);
        feedRecyclerView.setAdapter(feedAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        feedAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedAdapter.stopListening();
    }

    public static ReaderFragment newInstance() {
        ReaderFragment FeedFragment = new ReaderFragment();
        Bundle args = new Bundle();
        FeedFragment.setArguments(args);
        return FeedFragment;
    }

}
