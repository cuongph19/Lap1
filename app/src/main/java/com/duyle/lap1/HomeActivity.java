package com.duyle.lap1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

        private static final int ADD_CITY_REQUEST_CODE = 1;

        private FirebaseFirestore db;
        private ListView list;
        private TextView add;
        private ArrayList<String> cityNames;
        private ArrayAdapter<String> adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            list = findViewById(R.id.listview);
            add = findViewById(R.id.add);
            db = FirebaseFirestore.getInstance();
            cityNames = new ArrayList<>();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNames);
            list.setAdapter(adapter);

            ghiDulieu();
            docDulieu();

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddCityActivity();
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                    showDeleteConfirmationDialog(position);
                }
            });
        }

        private void openAddCityActivity() {
            Intent intent = new Intent(HomeActivity.this, Add_Activity.class);
            startActivityForResult(intent, ADD_CITY_REQUEST_CODE);
        }

        private void showDeleteConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Xóa thành phố");
            builder.setMessage("Bạn có muốn xóa thành phố " + cityNames.get(position) + " không?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Xóa thành phố khi nhấn "Có"
                    xoaThanhPho(position);
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Đóng dialog khi nhấn "Không"
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

        private void ghiDulieu() {
            CollectionReference cities = db.collection("cities");

            Map<String, Object> data1 = new HashMap<>();
            data1.put("name", "San Francisco");
            data1.put("state", "CA");
            data1.put("country", "USA");
            data1.put("capital", false);
            data1.put("population", 860000);
            data1.put("regions", Arrays.asList("west_coast", "norcal"));
            cities.document("SF").set(data1);

            // Thêm dữ liệu của các thành phố khác tương tự
        }

        private void docDulieu() {
            db.collection("cities")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                cityNames.clear(); // Xóa dữ liệu cũ
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Lấy tên của thành phố và thêm vào danh sách
                                    cityNames.add((String) document.get("name"));
                                }
                                adapter.notifyDataSetChanged(); // Cập nhật Adapter
                            } else {
                                Log.d("HomeActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        private void xoaThanhPho(int position) {
            final String cityName = cityNames.get(position);
            db.collection("cities")
                    .whereEqualTo("name", cityName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                    cityNames.remove(cityName); // Xóa thành phố khỏi danh sách
                                    adapter.notifyDataSetChanged(); // Cập nhật ListView
                                    Toast.makeText(HomeActivity.this, cityName + " đã được xóa", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("HomeActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == ADD_CITY_REQUEST_CODE && resultCode == RESULT_OK) {
                // Nếu thành phố được thêm thành công từ AddCityActivity, tải lại dữ liệu
                docDulieu();
            }
        }
    }