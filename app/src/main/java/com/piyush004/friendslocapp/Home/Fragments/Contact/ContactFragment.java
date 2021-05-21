package com.piyush004.friendslocapp.Home.Fragments.Contact;

import android.Manifest;
import android.content.pm.PackageManager;
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

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.piyush004.friendslocapp.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ContactFragment extends Fragment {

    private static final String TAG = ContactFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;
    private Thread thread = null;
    private ArrayList<ContactModel> newList;
    public static final int RequestPermissionCode = 1;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int animationList = R.anim.layout_animation_up_to_down;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_contact, container, false);
        Contacts.initialize(getContext());
        EnableRuntimePermission();
        List<ContactModel> phoneContact = getAllPhoneContact();

        swipeRefreshLayout = view.findViewById(R.id.swipeContact);
        recyclerView = view.findViewById(R.id.RecycleViewContact);

        Log.e(TAG, "phoneContact : " + phoneContact.toString());
        Log.e(TAG, "phoneContact  Size : " + phoneContact.size());

        newList = (ArrayList<ContactModel>) removeDuplicateNumber(phoneContact);
        if (newList != null && newList.size() > 0) {
            updateUI();
            Toast.makeText(getContext(), "Successfully imported Contact", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No Contact found", Toast.LENGTH_SHORT).show();
        }


        swipeRefreshLayout.setOnRefreshListener(() -> {
            runAnimationAgain();
            final Handler handler = new Handler();
            handler.postDelayed((Runnable) () -> {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);

        });

        return view;
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(),
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(getContext(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private List<ContactModel> getAllPhoneContact() {
        List<ContactModel> userContact = new ArrayList<>();

        List<Contact> contacts = Contacts.getQuery().find();
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            List<PhoneNumber> numbers = c.getPhoneNumbers();
            for (int j = 0; j < numbers.size(); j++) {
                String number = numbers.get(j).getNumber().trim().replaceAll(" ", "");
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
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(adapter);
        }
    }

    private void runAnimationAgain() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), animationList);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


}