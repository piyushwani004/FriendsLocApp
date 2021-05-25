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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private String date;

    public ContactAdapter(List<ContactModel> list, Context context) {
        List = list;
        this.inflater = LayoutInflater.from(context);
        backup = new ArrayList<>(List);
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final ContactModel temp = List.get(position);

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
            Log.e(TAG, "onBindViewHolder: " + firebaseAuth.getCurrentUser().getUid());

            final java.util.Date data = new Date();
            SenderHashMap = new HashMap<>();
            ReceiverHashMap = new HashMap<>();

            simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            date = simpleDateFormat.format(data);

            DatabaseReference sender = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child(List.get(position).getID());
            DatabaseReference receiver = FirebaseDatabase.getInstance().getReference().child("FriendRequest").child(List.get(position).getID()).child(firebaseAuth.getCurrentUser().getUid());

            SenderHashMap.put("Id", List.get(position).getID());
            SenderHashMap.put("Status", "Pending");
            SenderHashMap.put("RequestType", "Sender");
            SenderHashMap.put("Date", date);
            sender.setValue(SenderHashMap).addOnSuccessListener(aVoid -> {

                ReceiverHashMap.put("Id", firebaseAuth.getCurrentUser().getUid());
                ReceiverHashMap.put("Status", "Pending");
                ReceiverHashMap.put("RequestType", "Receiver");
                ReceiverHashMap.put("Date", date);
                receiver.setValue(ReceiverHashMap).addOnSuccessListener(aVoid1 -> Toast.makeText(context, "Request Send Successfully...", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());

            }).addOnFailureListener(e -> Toast.makeText(context, "Network Error...", Toast.LENGTH_SHORT).show());


        });
        Toast.makeText(context, " position" + position + 1, Toast.LENGTH_SHORT).show();
        Log.e("User Data :", List.get(position).toString());
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

