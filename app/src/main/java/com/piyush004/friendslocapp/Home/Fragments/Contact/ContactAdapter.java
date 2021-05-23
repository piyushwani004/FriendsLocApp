package com.piyush004.friendslocapp.Home.Fragments.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> implements Filterable {

    public static final String TAG = ContactAdapter.class.getSimpleName();

    private java.util.List<ContactModel> List;
    private java.util.List<ContactModel> CommonList;
    private LayoutInflater inflater;
    ArrayList<ContactModel> backup;
    DatabaseReference user = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userRef = user.child("AppUsers");

    public ContactAdapter(List<ContactModel> list, Context context, List<ContactModel> commonList) {
        List = list;
        this.inflater = LayoutInflater.from(context);
        backup = new ArrayList<>(List);
        CommonList = commonList;
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final ContactModel temp = List.get(position);

        holder.Name.setText(List.get(position).getName());
        holder.MobileNo.setText(List.get(position).getMobile());
        for(int i=0;i<CommonList.size();i++){
            if(CommonList.get(i).getMobile()==List.get(position).getMobile())
             holder.inviteButton.setText("Add Friend");

        }

        Picasso.get()
                .load(List.get(position).getPhotoURL())
                .resize(500, 500)
                .centerCrop().rotate(0)
                .placeholder(R.drawable.person_placeholder)
                .into(holder.circleImageView);

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


    private List<String> getUserList() {
        List<String> appUsers = new ArrayList<>();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    appUsers.add(ds.child("Mobile").getValue().toString().trim().replaceAll(" ", ""
                            .replaceAll("-", "")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return appUsers;

    }


}

