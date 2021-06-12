/*
 * <!--
 *   ~ /*******************************************************
 *   ~  * Copyright (C) 2021-2031 {Piyush Wani and  Mayur Sapkale} <{piyushwani04@gmail.com}>
 *   ~  *
 *   ~  * This file is part of {FriendLocatorApp}.
 *   ~  *
 *   ~  * {FriendLocatorApp} can not be copied and/or distributed without the express
 *   ~  * permission of {Piyush Wani and  Mayur Sapkale}
 *   ~  ******************************************************
 *   -->
 */

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;


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
    private final HashMap<String, Object> hashMap;
    private AlertDialog.Builder alertDialogBuilder;
    public static final String Accept_Request = "Accept";
    private String phoneN, SendPhoneN;

    public RequestFragment() {
        hashMap = new HashMap<>();
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
                snapshot.child("Mobile").getValue(String.class),
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

                holder.AcceptButton.setOnClickListener(v -> {

                    alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Friend Request");
                    alertDialogBuilder.setMessage("Do You Want To Accept Request ?");
                    alertDialogBuilder.setPositiveButton("yes",
                            (arg0, arg1) -> {

                                DatabaseReference sender = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(model.getId());
                                DatabaseReference receiver = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(model.getId()).child(firebaseAuth.getCurrentUser().getUid());

                                hashMap.put("Status", Accept_Request);
                                sender.updateChildren(hashMap).addOnSuccessListener(aVoid -> {

                                    receiver.updateChildren(hashMap).addOnSuccessListener(aVoid1 -> {

                                        DatabaseReference friendDR = FirebaseDatabase.getInstance().getReference().child("Friends");

                                        String num = firebaseAuth.getCurrentUser().getPhoneNumber();
                                        String senderNumber = model.getMobile();
                                        assert num != null;
                                        phoneN = num.substring(3);

                                        assert senderNumber != null;
                                        SendPhoneN = senderNumber.substring(3);

                                        friendDR.child(firebaseAuth.getCurrentUser().getUid()).child(model.getId()).child("Mobile").setValue(SendPhoneN);
                                        friendDR.child(firebaseAuth.getCurrentUser().getUid()).child(model.getId()).child("id").setValue(model.getId()).addOnSuccessListener(aVoid2 -> {

                                            friendDR.child(model.getId()).child(firebaseAuth.getCurrentUser().getUid()).child("Mobile").setValue(phoneN);
                                            friendDR.child(model.getId()).child(firebaseAuth.getCurrentUser().getUid()).child("id").setValue(firebaseAuth.getCurrentUser().getUid()).addOnSuccessListener(aVoid3 -> {

                                                sender.removeValue().addOnSuccessListener(aVoid4 -> receiver.removeValue().addOnSuccessListener(aVoid31 -> {
                                                    Toast.makeText(context, "Friend Request Accepted...", Toast.LENGTH_SHORT).show();
                                                }));

                                            });

                                        }).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show());

                                    }).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show());

                                }).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show());

                            });

                    alertDialogBuilder.setNegativeButton("No",
                            (dialog, which) -> {
                                dialog.cancel();
                                dialog.dismiss();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                });


                holder.DeleteButton.setOnClickListener(v -> {

                    alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Friend Request");
                    alertDialogBuilder.setMessage("Do You Want To Delete Request ?");
                    alertDialogBuilder.setPositiveButton("yes",
                            (arg0, arg1) -> {

                                DatabaseReference deleteReqDR = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
                                deleteReqDR.child(firebaseAuth.getCurrentUser().getUid()).child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deleteReqDR.child(model.getId()).child(firebaseAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Friend Request Deleted...", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });


                            });

                    alertDialogBuilder.setNegativeButton("No",
                            (dialog, which) -> {
                                dialog.cancel();
                                dialog.dismiss();
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

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