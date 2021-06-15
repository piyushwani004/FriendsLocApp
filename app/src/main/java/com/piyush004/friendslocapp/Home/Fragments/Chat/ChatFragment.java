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

package com.piyush004.friendslocapp.Home.Fragments.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.Home.Fragments.Chat.Chatting.ChattingActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ChatFragment extends Fragment {

    private static final String TAG = ChatFragment.class.getSimpleName();
    private View view;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private SearchView editTextSearch;
    private FirebaseRecyclerOptions<ChatModel> options;
    private FirebaseRecyclerAdapter<ChatModel, ChatHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int animationList = R.anim.layout_animation_up_to_down;
    private Context context;
    private String CurrentUserId;

    public ChatFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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

        view = inflater.inflate(R.layout.fragment_chat, container, false);

        context = view.getContext();

        swipeRefreshLayout = view.findViewById(R.id.swipeChat);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecycleViewChat);
        editTextSearch = view.findViewById(R.id.searchBarChat);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        editTextSearch.setQueryHint("Search by mobile number");
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

        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Friends").child(CurrentUserId);
        options = new FirebaseRecyclerOptions.Builder<ChatModel>().setQuery(df, snapshot -> new ChatModel(

                snapshot.child("id").getValue(String.class),
                snapshot.child("MessageCount").getValue(String.class),
                snapshot.child("TimeStamp").getValue(Long.class)

        )).build();

        adapter = new FirebaseRecyclerAdapter<ChatModel, ChatHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ChatHolder holder, int position, @NonNull final ChatModel model) {

                Log.e(TAG, "onBindViewHolder: " + model.getChatId());

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(model.getChatId());

                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("Name").getValue(String.class);
                        String status = snapshot.child("Status").getValue(String.class);
                        String imgUrl = snapshot.child("ImageURL").getValue(String.class);

                        if (Name != null)
                            holder.title_card.setText(Name);
                        else
                            holder.title_card.setText(firebaseAuth.getCurrentUser().getPhoneNumber());

                        if (status != null) {
                            if (status.equals("Online")) {
                                holder.status.setVisibility(View.VISIBLE);
                            } else {
                                holder.status.setVisibility(View.GONE);
                            }
                        } else {
                            holder.status.setVisibility(View.GONE);
                        }

                        if (imgUrl != null) {
                            Picasso.get().load(imgUrl)
                                    .resize(500, 500)
                                    .centerCrop()
                                    .rotate(0)
                                    .placeholder(R.drawable.person_placeholder)
                                    .into(holder.imageVieChat);
                        } else {
                            Picasso.get().load(R.drawable.person_placeholder)
                                    .placeholder(R.drawable.person_placeholder)
                                    .into(holder.imageVieChat);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                TextDrawable drawable = TextDrawable.builder().buildRound("1", Color.GREEN);
                holder.new_message_noti.setImageDrawable(drawable);

                if (model.getMessageCount() != null) {
                    if (model.getMessageCount().equals("1")) {
                        holder.new_message_noti.setVisibility(View.VISIBLE);
                    } else {
                        holder.new_message_noti.setVisibility(View.GONE);
                    }
                } else {
                    holder.new_message_noti.setVisibility(View.GONE);
                }


                holder.itemView.setOnClickListener(v -> {

                    final DatabaseReference newMessage = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> MessageCount = new HashMap<>();
                    MessageCount.put("MessageCount", "0");
                    newMessage.child("Friends").child(CurrentUserId).child(model.getChatId()).updateChildren(MessageCount).addOnSuccessListener(aVoid -> newMessage.child("Friends").child(model.getChatId()).child(CurrentUserId).updateChildren(MessageCount).addOnSuccessListener(aVoid1 -> {

                        Intent intent = new Intent(getContext(), ChattingActivity.class);
                        intent.putExtra("UserID", model.getChatId());
                        startActivity(intent);

                    }).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show());


                });

            }

            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
                return new ChatHolder(view);
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

        FirebaseRecyclerOptions<ChatModel> options =
                new FirebaseRecyclerOptions.Builder<ChatModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Friends").child(CurrentUserId).orderByChild("Mobile").startAt(s.toUpperCase()).endAt(s.toLowerCase() + "\uf8ff"), snapshot -> new ChatModel(

                                snapshot.child("id").getValue(String.class),
                                snapshot.child("MessageCount").getValue(String.class),
                                snapshot.child("TimeStamp").getValue(Long.class)

                        ))
                        .build();

        adapter = new FirebaseRecyclerAdapter<ChatModel, ChatHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ChatHolder holder, int position, @NonNull final ChatModel model) {

                Log.e(TAG, "onBindViewHolder: " + model.getChatId());

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(model.getChatId());

                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("Name").getValue(String.class);
                        String status = snapshot.child("Status").getValue(String.class);
                        String imgUrl = snapshot.child("ImageURL").getValue(String.class);

                        if (Name != null)
                            holder.title_card.setText(Name);
                        else
                            holder.title_card.setText(firebaseAuth.getCurrentUser().getPhoneNumber());

                        if (status != null) {
                            if (status.equals("Online")) {
                                holder.status.setVisibility(View.VISIBLE);
                            } else {
                                holder.status.setVisibility(View.GONE);
                            }
                        } else {
                            holder.status.setVisibility(View.GONE);
                        }

                        if (imgUrl != null) {
                            Picasso.get().load(imgUrl)
                                    .resize(500, 500)
                                    .centerCrop()
                                    .rotate(0)
                                    .placeholder(R.drawable.person_placeholder)
                                    .into(holder.imageVieChat);
                        } else {
                            Picasso.get().load(R.drawable.person_placeholder)
                                    .placeholder(R.drawable.person_placeholder)
                                    .into(holder.imageVieChat);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                TextDrawable drawable = TextDrawable.builder().buildRound("1", Color.GREEN);
                holder.new_message_noti.setImageDrawable(drawable);

                if (model.getMessageCount() != null) {
                    if (model.getMessageCount().equals("1")) {
                        holder.new_message_noti.setVisibility(View.VISIBLE);
                    } else {
                        holder.new_message_noti.setVisibility(View.GONE);
                    }
                } else {
                    holder.new_message_noti.setVisibility(View.GONE);
                }


                holder.itemView.setOnClickListener(v -> {

                    final DatabaseReference newMessage = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> MessageCount = new HashMap<>();
                    MessageCount.put("MessageCount", "0");
                    newMessage.child("Friends").child(CurrentUserId).child(model.getChatId()).updateChildren(MessageCount).addOnSuccessListener(aVoid -> newMessage.child("Friends").child(model.getChatId()).child(CurrentUserId).updateChildren(MessageCount).addOnSuccessListener(aVoid1 -> {

                        Intent intent = new Intent(getContext(), ChattingActivity.class);
                        intent.putExtra("UserID", model.getChatId());
                        startActivity(intent);

                    }).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(context, " " + e.getMessage(), Toast.LENGTH_SHORT).show());


                });

            }

            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
                return new ChatHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }


}