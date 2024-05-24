package com.example.eop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    RecyclerView recyclerView_apply;
    private FirebaseAuth mFirebaseauth;//Firebase 인증 객체
    ApplyAdapter applyAdapter;
    ArrayList<ApplyItem> applyItems;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list1);

        mFirebaseauth = FirebaseAuth.getInstance();

        applyItems = new ArrayList<>();
        recyclerView_apply = findViewById(R.id.recyclerView_apply2);
        applyAdapter = new ApplyAdapter(applyItems, this);
        recyclerView_apply.setAdapter(applyAdapter);

        addChildEvent();
    }

    public void addChildEvent() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child("apply list").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ApplyItem item = dataSnapshot.getValue(ApplyItem.class);
                    applyItems.add(0, item);
                }
                applyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay,R.anim.slide_out_right);
    }
}
