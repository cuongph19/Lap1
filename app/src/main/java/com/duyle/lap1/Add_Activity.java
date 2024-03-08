package com.duyle.lap1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Add_Activity extends AppCompatActivity {
    private EditText cityNameEditText, populationEditText;
    private Button addButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cityNameEditText = findViewById(R.id.edit_text_city_name);
        populationEditText = findViewById(R.id.edit_text_city_population);
        addButton = findViewById(R.id.button_add_city);
        db = FirebaseFirestore.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCityToFirestore();
            }
        });
    }

    private void addCityToFirestore() {
        String cityName = cityNameEditText.getText().toString();
        int population = Integer.parseInt(populationEditText.getText().toString());

        Map<String, Object> cityData = new HashMap<>();
        cityData.put("name", cityName);
        cityData.put("population", population);

        // Thêm thành phố vào Firestore
        db.collection("cities")
                .add(cityData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Add_Activity.this, "Thêm thành phố thành công", Toast.LENGTH_SHORT).show();
                        // Trả về kết quả thành công
                        setResult(Activity.RESULT_OK);
                        finish(); // Đóng hoạt động
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Activity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}