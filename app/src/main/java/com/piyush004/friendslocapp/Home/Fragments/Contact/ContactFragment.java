package com.piyush004.friendslocapp.Home.Fragments.Contact;

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
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ContactFragment extends Fragment {

    private static final String TAG = ContactFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;
    private Thread thread = null;
    private ArrayList<ContactModel> newList;
    private List<ContactModel> firebaseList;
    private List<ContactModel> finalList;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int animationList = R.anim.layout_animation_up_to_down;

    private SearchView searchView;

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
        Contacts.initialize(Objects.requireNonNull(getContext()));
        List<ContactModel> phoneContact = getAllPhoneContact();

        swipeRefreshLayout = view.findViewById(R.id.swipeContact);
        recyclerView = view.findViewById(R.id.RecycleViewContact);

        firebaseList = new ArrayList<>();
        finalList = new ArrayList<>();

        /* Log.e(TAG, "phoneContact : " + phoneContact.toString());*/
        Log.e(TAG, "phoneContact  Size : " + phoneContact.size());

        newList = (ArrayList<ContactModel>) removeDuplicateNumber(phoneContact);
        Log.e(TAG, "newList  Size : " + newList.size());

        //store firebase user in firebase list...
        readFirebaseData(list -> {
            firebaseList = list;
            Log.e(TAG, "Firebase List: " + firebaseList.toString());
        });


        if (newList.size() > 0) {
            updateUI();
        } else {
            Toast.makeText(getContext(), "No Contact found", Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            runAnimationAgain();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);

        });

        searchView = view.findViewById(R.id.editText_searchBar);
        searchView.setQueryHint("Search User...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private List<ContactModel> getAllPhoneContact() {
        List<ContactModel> userContact = new ArrayList<>();

        List<Contact> contacts = Contacts.getQuery().find();
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            List<PhoneNumber> numbers = c.getPhoneNumbers();
            for (int j = 0; j < numbers.size(); j++) {
                String number = numbers.get(j).getNumber().trim().replaceAll(" ", "").replaceAll("-", "");
                String name = c.getDisplayName();
                String url = c.getPhotoUri();

                if (number.length() >= 10)
                    userContact.add(new ContactModel(name, number, url));
            }
        }
        return userContact;
    }

    private List<ContactModel> removeDuplicateNumber(List<ContactModel> list1) {

        Map<String, ContactModel> cleanMap = new LinkedHashMap<>();
        for (int i = 0; i < list1.size(); i++) {
            cleanMap.put(list1.get(i).getMobile(), list1.get(i));
        }
        List<ContactModel> list = new ArrayList<>(cleanMap.values());
        return list;
    }

    private void updateUI() {
        if (newList != null && newList.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ContactAdapter(newList, getContext());
            recyclerView.setAdapter(adapter);
        }
    }

    private void runAnimationAgain() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), animationList);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void readFirebaseData(FirebaseContactCallback callback) {
        List<ContactModel> list;
        list = new ArrayList<>();
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String id = item.child("ID").getValue(String.class);
                    String name = item.child("Name").getValue(String.class);
                    String contactNo = item.child("Mobile").getValue(String.class);
                    String photoUrl = item.child("ImageURL").getValue(String.class);
                    list.add(new ContactModel(id, name, contactNo, photoUrl));
                }
                callback.onResponse(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}