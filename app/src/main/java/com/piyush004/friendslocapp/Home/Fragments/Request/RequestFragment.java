package com.piyush004.friendslocapp.Home.Fragments.Request;

import android.content.Context;
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
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;


public class RequestFragment extends Fragment {

    private static final String TAG = RequestFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerOptions<FriendRequestModel> options;
    private FirebaseRecyclerAdapter<FriendRequestModel, FriendRequestHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int animationList = R.anim.layout_animation_up_to_down;
    private Context context;



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

        context = view.getContext();

        swipeRefreshLayout = view.findViewById(R.id.swipeRequest);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecycleViewRequest);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(firebaseAuth.getCurrentUser().getUid());
        options = new FirebaseRecyclerOptions.Builder<FriendRequestModel>().setQuery(df, snapshot -> new FriendRequestModel(

                snapshot.child("Id").getValue(String.class),
                snapshot.child("Date").getValue(String.class),
                snapshot.child("RequestType").getValue(String.class),
                snapshot.child("Status").getValue(String.class)

        )).build();

        adapter = new FirebaseRecyclerAdapter<FriendRequestModel, FriendRequestHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FriendRequestHolder holder, int position, @NonNull final FriendRequestModel model) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(model.getId());

                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        holder.Name.setText(snapshot.child("Name").getValue(String.class));
                        holder.MobileNo.setText(snapshot.child("Mobile").getValue(String.class));

                        Picasso.get()
                                .load(snapshot.child("ImageURL").getValue(String.class))
                                .resize(500, 500)
                                .centerCrop().rotate(0)
                                .placeholder(R.drawable.person_placeholder)
                                .into(holder.circleImageView);

                        if (model.getReqType().equals("Sender")) {
                            holder.ButtonLayout.setVisibility(View.GONE);
                            holder.StatusButton.setVisibility(View.VISIBLE);
                            holder.StatusButton.setText(model.getStatus());
                        } else if (model.getReqType().equals("Receiver")) {
                            holder.ButtonLayout.setVisibility(View.VISIBLE);
                            holder.StatusButton.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                    }
                });


                holder.ButtonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                    }
                });


            }

            @NonNull
            @Override
            public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_card, parent, false);
                return new FriendRequestHolder(view);
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



}