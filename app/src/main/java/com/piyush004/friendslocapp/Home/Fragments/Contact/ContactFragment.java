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

package com.piyush004.friendslocapp.Home.Fragments.Contact;

import android.content.Context;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class ContactFragment extends Fragment {

    private static final String TAG = ContactFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int animationList = R.anim.layout_animation_up_to_down;
    private FirebaseRecyclerOptions<ContactModel> options;
    private FirebaseRecyclerAdapter<ContactModel, ContactHolder> adapter;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private SearchView searchView;
    HashMap<String, Object> SenderHashMap;
    HashMap<String, Object> ReceiverHashMap;
    private AlertDialog.Builder alertDialogBuilder;
    private String date;
    private SimpleDateFormat simpleDateFormat;

    public ContactFragment() {
        // Required empty public constructor
    }


    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_contact, container, false);

        context = view.getContext();
        firebaseAuth = FirebaseAuth.getInstance();

        searchView = view.findViewById(R.id.editText_searchBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeContact);
        recyclerView = view.findViewById(R.id.RecycleViewContact);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        options = new FirebaseRecyclerOptions.Builder<ContactModel>().setQuery(df, snapshot -> new ContactModel(

                snapshot.child("ID").getValue(String.class),
                snapshot.child("Name").getValue(String.class),
                snapshot.child("Mobile").getValue(String.class),
                snapshot.child("ImageURL").getValue(String.class)

        )).build();

        adapter = new FirebaseRecyclerAdapter<ContactModel, ContactHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ContactHolder holder, int position, @NonNull final ContactModel model) {

                if (model.getID().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
                    holder.inviteButton.setVisibility(View.GONE);
                } else {

                    if (model.getID() != null && model.getName() != null && model.getMobile() != null && model.getPhotoURL() != null) {

                        if (model.getName().length() > 15) {
                            StringBuilder name = new StringBuilder(model.getName());
                            char[] array = new char[15];
                            name.getChars(0, 15, array, 0);
                            String stringName = new String(array);
                            stringName = stringName + "...";
                            holder.Name.setText(stringName);
                        } else {
                            holder.Name.setText(model.getName());
                        }

                        StringBuilder number = new StringBuilder(model.getMobile());
                        number.replace(7, 11, "****");
                        holder.MobileNo.setText(number);

                        Picasso.get()
                                .load(model.getPhotoURL())
                                .resize(500, 500)
                                .centerCrop().rotate(0)
                                .placeholder(R.drawable.person_placeholder)
                                .into(holder.circleImageView);


                        DatabaseReference Friends = FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseAuth.getCurrentUser().getUid());
                        Friends.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String fId = dataSnapshot.child("id").getValue(String.class);
                                    if (fId != null) {
                                        if (fId.equals(model.getID())) {
                                            holder.inviteButton.setVisibility(View.GONE);
                                        } else {
                                            holder.inviteButton.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        holder.inviteButton.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }

                holder.inviteButton.setOnClickListener(v -> {

                    firebaseAuth = FirebaseAuth.getInstance();

                    alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("FriendRequest...");
                    alertDialogBuilder.setMessage("Do You Want To send Request ?");
                    alertDialogBuilder.setPositiveButton("yes",
                            (arg0, arg1) -> {

                                Log.e(TAG, "onBindViewHolder: " + firebaseAuth.getCurrentUser().getUid());

                                final Date data = new Date();
                                SenderHashMap = new HashMap<>();
                                ReceiverHashMap = new HashMap<>();

                                simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                date = simpleDateFormat.format(data);

                                DatabaseReference sender = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(model.getID());
                                DatabaseReference receiver = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(model.getID()).child(firebaseAuth.getCurrentUser().getUid());

                                SenderHashMap.put("Id", model.getID());
                                SenderHashMap.put("Status", "Pending");
                                SenderHashMap.put("RequestType", "Sender");
                                SenderHashMap.put("Mobile", model.getMobile());
                                SenderHashMap.put("Date", date);
                                sender.setValue(SenderHashMap).addOnSuccessListener(aVoid -> {

                                    ReceiverHashMap.put("Id", firebaseAuth.getCurrentUser().getUid());
                                    ReceiverHashMap.put("Status", "Pending");
                                    ReceiverHashMap.put("RequestType", "Receiver");
                                    ReceiverHashMap.put("Mobile", model.getMobile());
                                    ReceiverHashMap.put("Date", date);
                                    receiver.setValue(ReceiverHashMap).addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(context, "FriendRequest Send Successfully...", Toast.LENGTH_SHORT).show();
                                        holder.inviteButton.setVisibility(View.GONE);
                                    }).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());

                                }).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());

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
            public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
                return new ContactHolder(view);
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

        searchView.setQueryHint("Search User Name...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onProcessSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onProcessSearch(newText);
                return false;
            }
        });

        return view;
    }

    private void onProcessSearch(String s) {

        FirebaseRecyclerOptions<ContactModel> options =
                new FirebaseRecyclerOptions.Builder<ContactModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("AppUsers").orderByChild("Name").startAt(s.toUpperCase()).endAt(s.toLowerCase() + "\uf8ff"), snapshot -> new ContactModel(
                                snapshot.child("ID").getValue(String.class),
                                snapshot.child("Name").getValue(String.class),
                                snapshot.child("Mobile").getValue(String.class),
                                snapshot.child("ImageURL").getValue(String.class)
                        ))
                        .build();

        adapter = new FirebaseRecyclerAdapter<ContactModel, ContactHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ContactHolder holder, int position, @NonNull final ContactModel model) {

                if (model.getID().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
                    holder.inviteButton.setVisibility(View.GONE);
                } else {

                    if (model.getID() != null && model.getName() != null && model.getMobile() != null && model.getPhotoURL() != null) {

                        if (model.getName().length() > 15) {
                            StringBuilder name = new StringBuilder(model.getName());
                            char[] array = new char[15];
                            name.getChars(0, 15, array, 0);
                            String stringName = new String(array);
                            stringName = stringName + "...";
                            holder.Name.setText(stringName);
                        } else {
                            holder.Name.setText(model.getName());
                        }


                        StringBuilder number = new StringBuilder(model.getMobile());
                        number.replace(7, 11, "****");
                        holder.MobileNo.setText(number);

                        Picasso.get()
                                .load(model.getPhotoURL())
                                .resize(500, 500)
                                .centerCrop().rotate(0)
                                .placeholder(R.drawable.person_placeholder)
                                .into(holder.circleImageView);


                        DatabaseReference Friends = FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseAuth.getCurrentUser().getUid());
                        Friends.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String fId = dataSnapshot.child("id").getValue(String.class);
                                    if (fId != null) {
                                        if (fId.equals(model.getID())) {
                                            holder.inviteButton.setVisibility(View.GONE);
                                        } else {
                                            holder.inviteButton.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        holder.inviteButton.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }

                holder.inviteButton.setOnClickListener(v -> {

                    firebaseAuth = FirebaseAuth.getInstance();

                    alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("FriendRequest...");
                    alertDialogBuilder.setMessage("Do You Want To send Request ?");
                    alertDialogBuilder.setPositiveButton("yes",
                            (arg0, arg1) -> {

                                Log.e(TAG, "onBindViewHolder: " + firebaseAuth.getCurrentUser().getUid());

                                final Date data = new Date();
                                SenderHashMap = new HashMap<>();
                                ReceiverHashMap = new HashMap<>();

                                simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                date = simpleDateFormat.format(data);

                                DatabaseReference sender = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(model.getID());
                                DatabaseReference receiver = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(model.getID()).child(firebaseAuth.getCurrentUser().getUid());

                                SenderHashMap.put("Id", model.getID());
                                SenderHashMap.put("Status", "Pending");
                                SenderHashMap.put("RequestType", "Sender");
                                SenderHashMap.put("Mobile", model.getMobile());
                                SenderHashMap.put("Date", date);
                                sender.setValue(SenderHashMap).addOnSuccessListener(aVoid -> {

                                    ReceiverHashMap.put("Id", firebaseAuth.getCurrentUser().getUid());
                                    ReceiverHashMap.put("Status", "Pending");
                                    ReceiverHashMap.put("RequestType", "Receiver");
                                    ReceiverHashMap.put("Mobile", model.getMobile());
                                    ReceiverHashMap.put("Date", date);
                                    receiver.setValue(ReceiverHashMap).addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(context, "FriendRequest Send Successfully...", Toast.LENGTH_SHORT).show();
                                        holder.inviteButton.setVisibility(View.GONE);
                                    }).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());

                                }).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());

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
            public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
                return new ContactHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }


    private void runAnimationAgain() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), animationList);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}