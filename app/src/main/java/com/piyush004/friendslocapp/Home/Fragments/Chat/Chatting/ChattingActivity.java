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

package com.piyush004.friendslocapp.Home.Fragments.Chat.Chatting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.Home.Fragments.Chat.Notification.APIService;
import com.piyush004.friendslocapp.Home.Fragments.Chat.Notification.Client;
import com.piyush004.friendslocapp.Home.Fragments.Chat.Notification.Data;
import com.piyush004.friendslocapp.Home.Fragments.Chat.Notification.MyResponse;
import com.piyush004.friendslocapp.Home.Fragments.Chat.Notification.NotificationSender;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingActivity extends AppCompatActivity {

    private static final String TAG = ChattingActivity.class.getSimpleName();
    private View view;
    private SimpleDateFormat simpleDateFormat;
    private FirebaseAuth firebaseAuth;
    private ImageView imageViewBack, userChatEmoji, cardViewStatus, sendBtn;
    private String message, Time, Date;
    private String CurrentUserId, OtherUserId;
    private EmojiconEditText textInputEditText;
    private EmojIconActions emojIconActions;
    private CircleImageView imageViewUserChat;
    private TextView TextUserChat, textViewOnline;
    private String room, SenderRoom, ReceivedRoom;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeChat;
    private FirebaseRecyclerOptions<ChattingModel> options;
    private FirebaseRecyclerAdapter<ChattingModel, ChattingHolder> adapter;
    int animationList = R.anim.layout_animation_up_to_down;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        Intent intent = getIntent();
        OtherUserId = intent.getStringExtra("OtherUserID").toString();
        if (OtherUserId == null) {
            finish();
        }

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        imageViewBack = findViewById(R.id.chatbackImgview);
        view = findViewById(R.id.rootView);
        userChatEmoji = findViewById(R.id.userChatEmoji);
        textInputEditText = findViewById(R.id.messageBox);
        imageViewUserChat = findViewById(R.id.imageViewUserChat);
        TextUserChat = findViewById(R.id.TextUserChat);
        cardViewStatus = findViewById(R.id.cardViewStatus);
        textViewOnline = findViewById(R.id.textViewOnline);
        sendBtn = findViewById(R.id.sendBtn);
        recyclerView = findViewById(R.id.RoomChatRecyView);
        swipeChat = findViewById(R.id.swipeRoomChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setUserData(OtherUserId);
        room = CurrentUserId + OtherUserId;

        final java.util.Date data = new java.util.Date();
        final Calendar calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Time = simpleDateFormat.format(calendar.getTime());
        System.out.println(Time);

        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date = simpleDateFormat.format(data);
        System.out.println(Date);


        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("chat").child(room);

        options = new FirebaseRecyclerOptions.Builder<ChattingModel>().setQuery(df, snapshot -> new ChattingModel(

                snapshot.child("Sender").getValue(String.class),
                snapshot.child("Receiver").getValue(String.class),
                snapshot.child("Message").getValue(String.class),
                snapshot.child("Date").getValue(String.class),
                snapshot.child("Time").getValue(String.class),
                snapshot.child("ProductImg").getValue(String.class),
                snapshot.child("ProductName").getValue(String.class),
                snapshot.child("ChatID").getValue(String.class),
                snapshot.child("isSeen").getValue(boolean.class)

        )).build();

        adapter = new FirebaseRecyclerAdapter<ChattingModel, ChattingHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull final ChattingHolder holder, int position, @NonNull final ChattingModel model) {

                holder.chatMessage.setText(model.getMessage());
                holder.chatTime.setText(model.getTime());

                if (model.getReceiver().equals(CurrentUserId) && model.getSender().equals(OtherUserId)) {
                    final DatabaseReference seenSen = FirebaseDatabase.getInstance().getReference().child("chat").child(SenderRoom).child(model.getChatID());
                    final DatabaseReference seenRec = FirebaseDatabase.getInstance().getReference().child("chat").child(ReceivedRoom).child(model.getChatID());
                    HashMap<String, Object> seenMessage = new HashMap<>();
                    seenMessage.put("isSeen", true);
                    seenRec.updateChildren(seenMessage);
                    seenSen.updateChildren(seenMessage);
                }

                if (model.getReceiver().equals(OtherUserId)) {
                    if (model.isSeen()) {
                        holder.seen.setVisibility(View.VISIBLE);
                    } else {
                        holder.seen.setVisibility(View.GONE);
                    }
                } else {
                    holder.seen.setVisibility(View.GONE);
                }

            }

            @Override
            public int getItemViewType(int position) {
                if (getItem(position).getSender().equals(CurrentUserId)) {
                    return MSG_TYPE_RIGHT;
                } else {
                    return MSG_TYPE_LEFT;
                }

            }

            @NonNull
            @Override
            public ChattingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                if (viewType == MSG_TYPE_LEFT) {
                    View viewL = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card_left, parent, false);
                    return new ChattingHolder(viewL);
                } else {
                    View viewR = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card_right, parent, false);
                    return new ChattingHolder(viewR);
                }

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()), 1000);


        emojIconActions = new EmojIconActions(this, view, textInputEditText, userChatEmoji);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

            }

            @Override
            public void onKeyboardClose() {
            }
        });

        imageViewBack.setOnClickListener(v -> {
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
            finish();
        });

        view.setOnClickListener(v -> UIUtil.hideKeyboard(ChattingActivity.this));

        textInputEditText.setOnClickListener(v -> recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()), 500));

        SenderRoom = CurrentUserId + OtherUserId;
        ReceivedRoom = OtherUserId + CurrentUserId;

        sendBtn.setOnClickListener(v -> {

            message = textInputEditText.getText().toString().trim();
            if (message.isEmpty()) {
                textInputEditText.setError("Type Message");
                textInputEditText.requestFocus();
            } else if (!(message.isEmpty())) {

                final HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Message", message);
                hashMap.put("Sender", CurrentUserId);
                hashMap.put("Receiver", OtherUserId);
                hashMap.put("Time", Time);
                hashMap.put("Date", Date);
                hashMap.put("timeStamp", data.getTime());
                hashMap.put("isSeen", false);

                final DatabaseReference SendDF = FirebaseDatabase.getInstance().getReference().child("chat").child(SenderRoom);
                final DatabaseReference receivedDF = FirebaseDatabase.getInstance().getReference().child("chat").child(ReceivedRoom);
                final String key = SendDF.push().getKey();
                hashMap.put("ChatID", key);

                assert key != null;
                SendDF.child(key).setValue(hashMap).addOnSuccessListener(aVoid -> {
                    textInputEditText.setText("");
                    receivedDF.child(key).setValue(hashMap).addOnSuccessListener(aVoid1 -> {

                        recyclerView.smoothScrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());
                        final DatabaseReference timestamp = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> MessageCount = new HashMap<>();
                        MessageCount.put("MessageCount", "1");
                        timestamp.child("Friends").child(OtherUserId).child(CurrentUserId).updateChildren(MessageCount).addOnSuccessListener(aVoid2 -> {

                            timestamp.child("Friends").child(OtherUserId).child(CurrentUserId).child("timeStamp").setValue(data.getTime());
                            timestamp.child("Friends").child(CurrentUserId).child(OtherUserId).child("timeStamp").setValue(data.getTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    timestamp.child("Token").child(OtherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String usertoken = snapshot.child("token").getValue(String.class);
                                            sendNotifications(usertoken, "New Message :" + firebaseAuth.getCurrentUser().getPhoneNumber(), message);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });

                        }).addOnFailureListener(e -> Toast.makeText(ChattingActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show());

                    }).addOnFailureListener(e -> Toast.makeText(ChattingActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }).addOnFailureListener(e -> Toast.makeText(ChattingActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show());

            }

        });

        swipeChat.setOnRefreshListener(() -> {
            runAnimationAgain();
            new Handler().postDelayed(() -> swipeChat.setRefreshing(false), 1000);
        });

    }

    public void setUserData(String UserId) {

        if (UserId != null) {

            DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(UserId);

            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String Name = snapshot.child("Name").getValue(String.class);
                    String status = snapshot.child("ChatStatus").getValue(String.class);
                    String imgUrl = snapshot.child("ImageURL").getValue(String.class);

                    if (Name != null) {
                        if (Name.length() > 15) {
                            StringBuilder name = new StringBuilder(Name);
                            char[] array = new char[15];
                            name.getChars(0, 15, array, 0);
                            String stringName = new String(array);
                            stringName = stringName + "...";
                            TextUserChat.setText(stringName);
                        } else {
                            TextUserChat.setText(Name);
                        }
                    } else {
                        TextUserChat.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber());
                    }


                    if (status != null) {
                        if (status.equals("Online")) {
                            textViewOnline.setText(status);
                            Picasso.get().load(R.drawable.status_green).into(cardViewStatus);
                        } else if (status.equals("Offline")) {
                            textViewOnline.setText(status);
                            Picasso.get().load(R.drawable.status_red).into(cardViewStatus);
                        }
                    } else {
                        textViewOnline.setText("Offline");
                        Picasso.get().load(R.drawable.status_red).into(cardViewStatus);
                    }

                    if (imgUrl != null) {
                        Picasso.get().load(imgUrl)
                                .resize(500, 500)
                                .centerCrop()
                                .rotate(0)
                                .placeholder(R.drawable.person_placeholder)
                                .into(imageViewUserChat);
                    } else {
                        Picasso.get().load(R.drawable.person_placeholder)
                                .placeholder(R.drawable.person_placeholder)
                                .into(imageViewUserChat);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void runAnimationAgain() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, animationList);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CurrentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        adapter.startListening();
        recyclerView.smoothScrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());
//        updateToken();
        status("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        status("Offline");
    }

    /*private void updateToken() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Token");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                return;
            }

            String refreshToken = task.getResult();
            Log.e(TAG, "updateToken: refreshToken :: " + refreshToken);
            Token token = new Token(refreshToken);
            databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(token);
        });
    }*/

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(ChattingActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }

            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    private void status(String s) {
        DatabaseReference status = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ChatStatus", s);
        status.updateChildren(hashMap);
    }

}