package com.example.eop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecruitmentActivity extends AppCompatActivity {
    RecyclerView recyclerView_comment;
    CommentAdapter commentAdapter;
    ArrayList<CommentItem> commentItems;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference=firebaseDatabase.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    TextView textview_title,textView_content,recruitment_number,recruitment_bubble_speech;
    EditText editText_comment;
    Button button_comment;
    String uid,id,title,content,date,nickname;

    ApplyAdapter applyAdapter;
    ArrayList<ApplyItem> applyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitmemt);

        commentItems=new ArrayList<>();
        textview_title=findViewById(R.id.textview_titie);
        textView_content=findViewById(R.id.textview_content);
        recruitment_number=findViewById(R.id.recruitment_number);
        recruitment_bubble_speech=findViewById(R.id.recruitment_bubble_speech);
        recyclerView_comment=findViewById(R.id.recyclerView_comment);
        commentAdapter=new CommentAdapter(commentItems);
        recyclerView_comment.setAdapter(commentAdapter);
        button_comment=findViewById(R.id.button_comment);
        editText_comment=findViewById(R.id.editText_comment);


        Intent intent=getIntent();
        id=intent.getStringExtra("id"); //해당 리사이클러뷰를 클릭하면 고유 id를 가져온다
        title=intent.getStringExtra("title"); //해당 리사이클러뷰를 클릭하면 제목을 가져온다
        content=intent.getStringExtra("content"); //해당 리사이클러뷰를 클릭하면 내용을 가져온다
        date=intent.getStringExtra("date"); //해당 리사이클러뷰를 클릭하면 날짜를 가져온다

        textview_title.setText(title);
        textView_content.setText(content);

        clickCommentButton(button_comment);
        addChildEvent();
        uid=currentUser.getUid();
        fetchNickname(); //추가
    }
    private void fetchNickname() { //추가
        String currentUserUid = currentUser.getUid();
        databaseReference.child("UserAccount").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            //현재 로그인한 계정에 파이어베이스 데이터를 UserAccount저장후 닉네임값 추린다
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserAccount account = dataSnapshot.getValue(UserAccount.class);
                    if (account != null) {
                        nickname = account.getNickname();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void clickCommentButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // 댓글 작성 버튼을 누르면 유저,해당 글id, 쓴 시간, 제목, 내용 등이 파이어베이스에 저장
                CommentItem item=new CommentItem();
                SimpleDateFormat dateFormat=new SimpleDateFormat("MM/dd HH:mm",new Locale("ko","KR"));
                String currentDate=dateFormat.format(new Date());
                item.setComment_user(nickname); //추가(수정) 유저에서 직접 받은 닉네임 넣기
                item.setComment_content(editText_comment.getText().toString());
                item.setComment_date(currentDate);
                item.setCheck_id(id);
                item.setCheck_title(title);
                item.setCheck_content(content);
                item.setCheck_date(date);
                item.setUid(uid);

                databaseReference.child("comment list").push().setValue(item);
                editText_comment.setText("");
            }
        });
    }

    public void addChildEvent(){
        databaseReference.child("comment list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CommentItem item=snapshot.getValue(CommentItem.class);

                if(item.getCheck_id().equals(id)) {  //글 목록 id가 같은 댓글만 보이게
                    commentItems.add(item);    // 댓글 ui 추가
                    commentAdapter.notifyDataSetChanged(); // 새로고침
                    if(commentAdapter.getItemCount()==0){  // 댓글이 증가할때마다 ui변경
                        recruitment_bubble_speech.setVisibility(View.INVISIBLE);
                    }
                    else {
                        recruitment_number.setText(String.valueOf(commentAdapter.getItemCount()));
                        recruitment_number.setVisibility(View.VISIBLE);
                        recruitment_bubble_speech.setVisibility(View.VISIBLE);
                        updateApplyListNumber();
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateApplyListNumber() { // 댓글수가 증가할때마다 파이어베이스 넘버 값 수정
        DatabaseReference applyListRef = databaseReference.child("apply list");
        applyListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot applySnapshot : snapshot.getChildren()) {
                    ApplyItem applyItem = applySnapshot.getValue(ApplyItem.class);
                    if(applyItem.getId().equals(id)) {
                        applySnapshot.getRef().child("number").setValue(String.valueOf(commentAdapter.getItemCount()));
                    }
                }
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