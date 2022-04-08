package com.myapp.srstransport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ParentProfileActivity extends AppCompatActivity {

    private EditText displayClass,displayFatherName,displayMobileNo,displayRouteNo,displayBusStop;
    private ImageView displayStudentImage;
    private Spinner spinnerWardName;
    private DatabaseReference reference, dbRef,databaseReference;
    private ProgressDialog progressDialog;
    private String mobileNo,standard,fatherName,routeNo,busStop,studentImage,feeStatus;
    private List nameList, feeStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);
        getSupportActionBar().setTitle("My Profile");

        displayClass=findViewById(R.id.displayClass);
        displayFatherName=findViewById(R.id.displayFatherName);
        displayMobileNo=findViewById(R.id.displayMobileNo);
        displayRouteNo=findViewById(R.id.displayRouteNo);
        displayBusStop=findViewById(R.id.displayBusStop);
        displayStudentImage=findViewById(R.id.displayStudentImage);
        spinnerWardName=findViewById(R.id.spinnerWardName);

        reference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mobileNo=getIntent().getStringExtra("mobile");
        routeNo=getIntent().getStringExtra("route");

        progressDialog.setMessage("Fetching Details...");
        progressDialog.show();

        dbRef = reference.child("All Routes").child(routeNo);
        dbRef.orderByChild("mobileNo").equalTo(mobileNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                nameList = new ArrayList();
                feeStatusList = new ArrayList();
                nameList.add("--Your Ward's Name--");
                feeStatusList.add("--");
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        String tempName = String.valueOf(dataSnapshot.child("studentName").getValue());
                        nameList.add(tempName);
                        String tempFeeStatus = String.valueOf(dataSnapshot.child("feeStatus").getValue());
                        feeStatusList.add(tempFeeStatus);
                    }

                    spinnerWardName.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, nameList));
                    progressDialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), "Data not found!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinnerWardName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selectedItem = spinnerWardName.getSelectedItem().toString();

                switch (selectedItem) {

                    case "--Your Ward's Name--":
                        break;
                    default:
                        feeStatus = (String) feeStatusList.get(position);

                        fetchStudentDetails();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void fetchStudentDetails() {

        progressDialog.setMessage("Fetching Details...");
        progressDialog.show();

        databaseReference=reference.child("All Routes").child(routeNo).child(feeStatus);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        DataSnapshot dataSnapshot = task.getResult();
                        standard = String.valueOf(dataSnapshot.child("standard").getValue());
                        fatherName = String.valueOf(dataSnapshot.child("fatherName").getValue());
                        mobileNo = String.valueOf(dataSnapshot.child("mobileNo").getValue());
                        routeNo = String.valueOf(dataSnapshot.child("routeNo").getValue());
                        busStop = String.valueOf(dataSnapshot.child("busStop").getValue());
                        studentImage = String.valueOf(dataSnapshot.child("image").getValue());

                        try {
                            Picasso.get().load(studentImage).placeholder(R.drawable.avatar_image).into(displayStudentImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        displayClass.setText(standard);
                        displayFatherName.setText(fatherName);
                        displayMobileNo.setText(mobileNo);
                        displayRouteNo.setText(routeNo);
                        displayBusStop.setText(busStop);

                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(ParentProfileActivity.this, "Data doesn't exist!!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }else {
                    Toast.makeText(ParentProfileActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}