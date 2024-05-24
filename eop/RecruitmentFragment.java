package com.example.eop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecruitmentFragment extends Fragment {
    TextView text_recruitment_title,text_recruitment_content;
    Button button_recruitment;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference=firebaseDatabase.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.recruitment_fragment,container,false);

        text_recruitment_title=rootView.findViewById(R.id.text_recruitment_title);
        text_recruitment_content=rootView.findViewById(R.id.text_recruitment_content);
        button_recruitment=rootView.findViewById(R.id.button_recruitment);

        clickbutton(button_recruitment);
        uid=currentUser.getUid();

        return rootView;
    }

    public void clickbutton(Button button){
        button.setOnClickListener(new View.OnClickListener() { // 확인 버튼을 누르면 글 제목, 글 내용, 적은 날짜, 댓글 수가 파이어베이스에 저장
            @Override
            public void onClick(View v) {
                ApplyItem item=new ApplyItem();
                SimpleDateFormat dateFormat=new SimpleDateFormat("MM/dd HH:mm",new Locale("ko","KR"));
                String currentDate=dateFormat.format(new Date());
                item.setTitle(text_recruitment_title.getText().toString());
                item.setContent(text_recruitment_content.getText().toString());
                item.setDate(currentDate);
                item.setNumber("0");
                item.setUid(uid);

                databaseReference.child("apply list").push().setValue(item);
                text_recruitment_title.setText("");
                text_recruitment_content.setText("");
            }
        });
    }

}
