package com.piyush004.friendslocapp.Home.Fragments.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> implements Filterable {

    public static final String TAG = ContactAdapter.class.getSimpleName();

    private java.util.List<ContactModel> List;
    private List<ContactModel> filteredData;
    private LayoutInflater inflater;

    public ContactAdapter(List<ContactModel> list, Context context) {
        List = list;
        filteredData=list;
        this.inflater = LayoutInflater.from(context);
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
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<ContactModel> list = List;

                int count = list.size();
                final ArrayList<ContactModel> nlist = new ArrayList<ContactModel>(count);

                ContactModel filterableContact;

                for (int i = 0; i < count; i++) {
                    filterableContact = list.get(i);
                    if (filterableContact.getName().matches(filterString)) {
                        nlist.add(filterableContact);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<ContactModel>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView Name, MobileNo;
        public CircleImageView circleImageView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.contactName);
            MobileNo = itemView.findViewById(R.id.contactNumber);
            circleImageView = itemView.findViewById(R.id.image);

        }
    }



}

