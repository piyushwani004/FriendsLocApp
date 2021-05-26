package com.piyush004.friendslocapp.Home.Fragments.Request;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class RequestFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private SearchView editTextSearch;
    private FirebaseRecyclerOptions<RequestModel> options;
    private FirebaseRecyclerAdapter<RequestModel, RequestHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int animationList = R.anim.layout_animation_up_to_down;


    public RequestFragment() {
        // Required empty public constructor
    }

    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRequest);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecycleViewRequest);
        editTextSearch = view.findViewById(R.id.searchBarRequest);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        editTextSearch.setQueryHint("Search User...");
        editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("Project", "" + query);
                onProcessSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("Project", "" + newText);
                onProcessSearch(newText);
                return false;
            }
        });

        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        options = new FirebaseRecyclerOptions.Builder<RequestModel>().setQuery(df, snapshot -> new RequestModel(

                snapshot.child("ID").getValue(String.class),
                snapshot.child("Name").getValue(String.class),
                snapshot.child("Mobile").getValue(String.class),
                snapshot.child("ImageURL").getValue(String.class)

        )).build();

        adapter = new FirebaseRecyclerAdapter<RequestModel, RequestHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RequestHolder holder, int position, @NonNull final RequestModel model) {

                if (!Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid().equals(model.getID())) {

                    holder.Name.setText(model.getName());
                    holder.MobileNo.setText(model.getMobile());

                    Picasso.get()
                            .load(model.getPhotoURL())
                            .resize(500, 500)
                            .centerCrop().rotate(0)
                            .placeholder(R.drawable.person_placeholder)
                            .into(holder.circleImageView);
                } else {
                    holder.itemView.setVisibility(View.GONE);
                }

            }

            @NonNull
            @Override
            public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
                return new RequestHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            runAnimationAgain();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);

        });

        return view;
    }

    private void runAnimationAgain() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), animationList);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void onProcessSearch(String s) {

        FirebaseRecyclerOptions<RequestModel> options =
                new FirebaseRecyclerOptions.Builder<RequestModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("AppUsers").orderByChild("Name").startAt(s.toUpperCase()).endAt(s.toLowerCase() + "\uf8ff"), snapshot -> new RequestModel(

                                snapshot.child("ID").getValue(String.class),
                                snapshot.child("Name").getValue(String.class),
                                snapshot.child("Mobile").getValue(String.class),
                                snapshot.child("ImageURL").getValue(String.class)

                        ))
                        .build();
        adapter = new FirebaseRecyclerAdapter<RequestModel, RequestHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final RequestHolder holder, int position, @NonNull final RequestModel model) {

                holder.Name.setText(model.getName());
                holder.MobileNo.setText(model.getMobile());

                Picasso.get()
                        .load(model.getPhotoURL())
                        .resize(500, 500)
                        .centerCrop().rotate(0)
                        .placeholder(R.drawable.person_placeholder)
                        .into(holder.circleImageView);

            }

            @NonNull
            @Override
            public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
                return new RequestHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }


}