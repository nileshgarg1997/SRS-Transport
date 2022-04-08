package com.myapp.srstransport.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.srstransport.PaymentActivity;
import com.myapp.srstransport.R;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PayOnlineFragment extends Fragment {

    private EditText registeredMobileNo, routeNo;
    private Spinner spinnerStudentName, spinnerAcademicYear, spinnerMonth;
    private Button checkFeeStatusBtn;
    private String mobileNo, route, year, month;
    private DatabaseReference reference, dbRef, databaseReference;
    private List nameList, feeStatusList;
    private String feeStatus,finalStatus;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_online, container, false);

        reference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        registeredMobileNo = view.findViewById(R.id.registeredMobileNo);
        routeNo = view.findViewById(R.id.routeNo);

        spinnerStudentName = view.findViewById(R.id.spinnerStudentName);
        spinnerAcademicYear = view.findViewById(R.id.spinnerAcademicYear);
        spinnerMonth = view.findViewById(R.id.spinnerMonth);

        checkFeeStatusBtn = view.findViewById(R.id.checkFeeStatusBtn);

        Bundle bundle = getArguments();

        if (bundle != null) {
            route = bundle.getString("route");
            mobileNo = bundle.getString("mobile");
        }

        registeredMobileNo.setText(mobileNo);
        routeNo.setText(route);

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        dbRef = reference.child("All Routes").child(route);
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

                    spinnerStudentName.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, nameList));
                    progressDialog.dismiss();

                } else {
                    Toast.makeText(getContext(), "Data not found!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinnerStudentName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selectedItem = spinnerStudentName.getSelectedItem().toString();

                switch (selectedItem) {

                    case "--Your Ward's Name--":
                        break;
                    default:
                        feeStatus = (String) feeStatusList.get(position);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] academicYear = new String[]{"--Select Academic Year--", "2021-22", "2022-23"};
        spinnerAcademicYear.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, academicYear));

        spinnerAcademicYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = spinnerAcademicYear.getSelectedItem().toString();

                switch (selectedItem) {

                    case "--Select Academic Year--":
                        break;
                    case "2021-22":
                        year = "2021-22";
                        break;
                    case "2022-23":
                        year = "2022-23";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] monthList = new String[]{"--Select Month--", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        spinnerMonth.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, monthList));

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = spinnerMonth.getSelectedItem().toString();

                switch (selectedItem) {

                    case "--Select Month--":
                        break;

                    case "January":
                        month = "jan";
                        break;

                    case "February":
                        month = "feb";
                        break;

                    case "March":
                        month = "mar";
                        break;

                    case "April":
                        month = "apr";

                        break;

                    case "May":
                        month = "may";

                        break;

                    case "June":
                        month = "jun";

                        break;

                    case "July":
                        month = "jul";

                        break;

                    case "August":
                        month = "aug";

                        break;

                    case "September":
                        month = "sep";

                        break;

                    case "October":
                        month = "oct";

                        break;

                    case "November":
                        month = "nov";

                        break;

                    case "December":
                        month = "dec";

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkFeeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkValidation();
            }
        });

        return view;
    }

    private void checkValidation() {

        if (spinnerStudentName.getSelectedItem().toString().equals("--Your Ward's Name--")) {
            Toast.makeText(getContext(), "Please select your ward's name!!!", Toast.LENGTH_SHORT).show();
            spinnerStudentName.requestFocus();
        } else if (spinnerAcademicYear.getSelectedItem().toString().equals("--Select Academic Year--")) {
            Toast.makeText(getContext(), "Please select academic year!!!", Toast.LENGTH_SHORT).show();
            spinnerAcademicYear.requestFocus();
        } else if (spinnerMonth.getSelectedItem().toString().equals("--Select Month--")) {
            Toast.makeText(getContext(), "Choose month!!!", Toast.LENGTH_SHORT).show();
            spinnerMonth.requestFocus();
        } else {
            progressDialog.setMessage("Finding...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            checkFeeStatus();
        }

    }

    private void checkFeeStatus() {

        databaseReference=reference.child("Fee Status").child(route).child(year).child(feeStatus);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        DataSnapshot dataSnapshot=task.getResult();
                        finalStatus=String.valueOf(dataSnapshot.child(month).getValue());

                        if (finalStatus.equals("Fee Collected")){
                            new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Fee Already Submitted!")
                                    .show();

                        }else if (finalStatus.equals("Not Received Yet")){
                            FancyAlertDialog.Builder
                                    .with(getContext())
                                    .setTitle("Fee Pending")
                                    .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
//                                    .setMessage("Before proceeding please note that: " +
//                                            "There is a nominal fee of Rs.21 when you pay online")
                                    .setMessage("Online Payment feature coming soon...")
                                    .setNegativeBtnText("Cancel")
                                    .setPositiveBtnBackground(Color.parseColor("#FF4081"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                    .setPositiveBtnText("OK")
                                    .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                    .setAnimation(Animation.SLIDE)
                                    .isCancellable(false)
                                    .setIcon(R.drawable.warning_sigh, View.VISIBLE)
                                    .onPositiveClicked(dialog -> makePayment())
                                    .onNegativeClicked(dialog -> dialog.cancel())
                                    .build()
                                    .show();
                        }

                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(getContext(), "Data not found!!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }else {
                    Toast.makeText(getContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void makePayment(){

        Toast.makeText(getContext(), "You can pay fee manually by visiting school",Toast.LENGTH_LONG).show();

//        Intent i=new Intent(getContext(), PaymentActivity.class);
//        startActivity(i);
    }



}