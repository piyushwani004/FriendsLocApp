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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> implements Filterable {

    public static final String TAG = ContactAdapter.class.getSimpleName();

    private java.util.List<ContactModel> List;
    private LayoutInflater inflater;
    ArrayList<ContactModel> backup;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private SimpleDateFormat simpleDateFormat;
    HashMap<String, Object> SenderHashMap;
    HashMap<String, Object> ReceiverHashMap;
    private AlertDialog.Builder alertDialogBuilder;
    private String date;

    public ContactAdapter(List<ContactModel> list, Context context) {
        List = list;
        this.inflater = LayoutInflater.from(context);
        backup = new ArrayList<>(List);
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final ContactModel temp = List.get(position);

        DatabaseReference check = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(firebaseAuth.getCurrentUser().getUid());
        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fId = dataSnapshot.child("Id").getValue(String.class);
                    if (fId != null && List.size() >= 1) {
                        if (fId.equals(List.get(position).getID())) {
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

        DatabaseReference Friends = FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseAuth.getCurrentUser().getUid());
        Friends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fId = dataSnapshot.child("id").getValue(String.class);
                    if (fId != null && List.size() >= 1) {
                        if (fId.equals(List.get(position).getID())) {
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


        holder.Name.setText(List.get(position).getName());
        holder.MobileNo.setText(List.get(position).getMobile());

        Picasso.get()
                .load(List.get(position).getPhotoURL())
                .resize(500, 500)
                .centerCrop().rotate(0)
                .placeholder(R.drawable.person_placeholder)
                .into(holder.circleImageView);

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

                        DatabaseReference sender = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(List.get(position).getID());
                        DatabaseReference receiver = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(List.get(position).getID()).child(firebaseAuth.getCurrentUser().getUid());

                        SenderHashMap.put("Id", List.get(position).getID());
                        SenderHashMap.put("Status", "Pending");
                        SenderHashMap.put("RequestType", "Sender");
                        SenderHashMap.put("Mobile", List.get(position).getMobile());
                        SenderHashMap.put("Date", date);
                        sender.setValue(SenderHashMap).addOnSuccessListener(aVoid -> {

                            ReceiverHashMap.put("Id", firebaseAuth.getCurrentUser().getUid());
                            ReceiverHashMap.put("Status", "Pending");
                            ReceiverHashMap.put("RequestType", "Receiver");
                            ReceiverHashMap.put("Mobile", List.get(position).getMobile());
                            ReceiverHashMap.put("Date", date);
                            receiver.setValue(ReceiverHashMap).addOnSuccessListener(aVoid1 -> Toast.makeText(context, "FriendRequest Send Successfully...", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());

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
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.contact_card, parent, false);
        return new Holder(view);
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<ContactModel> filtereddata = new ArrayList<>();

            if (keyword.toString().isEmpty())
                filtereddata.addAll(backup);
            else {
                for (ContactModel obj : backup) {
                    if (obj.getName().toLowerCase().contains(keyword.toString().toLowerCase()))
                        filtereddata.add(obj);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtereddata;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List.clear();
            List.addAll((ArrayList<ContactModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView Name, MobileNo;
        public CircleImageView circleImageView;
        public Button inviteButton;

        public Holder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.contactName);
            MobileNo = itemView.findViewById(R.id.contactNumber);
            circleImageView = itemView.findViewById(R.id.image);
            inviteButton = itemView.findViewById(R.id.invite_button);

        }
    }

}

