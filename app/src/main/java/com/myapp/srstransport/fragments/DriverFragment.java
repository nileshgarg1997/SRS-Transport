package com.myapp.srstransport.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.srstransport.R;
import com.squareup.picasso.Picasso;

public class DriverFragment extends Fragment {

    TextView headingTV,driverName,driverMobile,busRouteNo,busNumberPlate;
    ImageView driverImage;
    private DatabaseReference reference, dbRef;
    LinearLayout dataNotFound,dataFound;
    ProgressDialog progressDialog;
    String name, mobile, routeNo, numberPlate, image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        reference = FirebaseDatabase.getInstance().getReference();

        headingTV=view.findViewById(R.id.headingTV);
        driverName = view.findViewById(R.id.driverName);
        driverMobile = view.findViewById(R.id.driverMobile);
        busRouteNo = view.findViewById(R.id.busRouteNo);
        busNumberPlate = view.findViewById(R.id.busNumberPlate);
        driverImage = view.findViewById(R.id.driverImage);
        dataFound = view.findViewById(R.id.dataFound);
        dataNotFound = view.findViewById(R.id.dataNotFound);

        Bundle bundle = getArguments();

        if (bundle != null) {
            routeNo = bundle.getString("route");
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait while we fetch driver's detail...");
        progressDialog.show();

        dbRef = reference.child("Driver's Detail");
        dbRef.child(routeNo).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        DataSnapshot dataSnapshot = task.getResult();
                        name = String.valueOf(dataSnapshot.child("name").getValue());
                        mobile = String.valueOf(dataSnapshot.child("mobile").getValue());
                        routeNo = String.valueOf(dataSnapshot.child("routeNo").getValue());
                        numberPlate = String.valueOf(dataSnapshot.child("numberPlate").getValue());
                        image = String.valueOf(dataSnapshot.child("image").getValue());

                        try {
                            Picasso.get().load(image).placeholder(R.drawable.driver).into(driverImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        driverName.setText(name);
                        driverMobile.setText(mobile);
                        busRouteNo.setText(routeNo);
                        busNumberPlate.setText(numberPlate);
                        progressDialog.dismiss();

                    } else {
                        dataFound.setVisibility(View.GONE);
                        dataNotFound.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                } else {
                    dataFound.setVisibility(View.GONE);
                    dataNotFound.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }
}