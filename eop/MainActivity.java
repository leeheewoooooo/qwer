package com.example.eop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText editText_nickname;
    private FirebaseAuth mFirebaseauth;//Firebase 인증 객체 //추가
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); //추가
    DatabaseReference databaseReference = firebaseDatabase.getReference(); //추가
    FirebaseAuth mAuth = FirebaseAuth.getInstance(); //추가
    FirebaseUser currentUser = mAuth.getCurrentUser(); //추가
    private Context context; //추가
    RecruitmentFragment recruitmentFragment;
    ApplyFragment applyFragment;
    InformationFragment informationFragment;
    BottomNavigationView bottomNavigationView;
    Dialog dialog; //추가
    String nickname; //추가
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseauth = FirebaseAuth.getInstance(); //추가
        context=this; //추가 (context초기화)

        recruitmentFragment=new RecruitmentFragment();
        applyFragment=new ApplyFragment();
        informationFragment=new InformationFragment();
        bottomNavigationView=findViewById(R.id.bottomNavigation);

        slectedbottomNavigationView(bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,applyFragment).commit();
        addChildEvent(); //추가
    }

    public void slectedbottomNavigationView(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            Fragment selectedFragment=null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.apply){
                    selectedFragment=applyFragment;
                }
                else if(item.getItemId()==R.id.recruitment){
                    selectedFragment=recruitmentFragment;
                }
                else if(item.getItemId()==R.id.information){
                    selectedFragment=informationFragment;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment).commit();

                return true;
            }
        });
    }
    public void addChildEvent() { //추가
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child("UserAccount").orderByChild("uid").equalTo(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserAccount account = dataSnapshot.getValue(UserAccount.class);
                    if(account.getVisit()==0) { // UserAccount에 방문 데이터가 0일경우(처음 방문했을 경우) 다이얼로그가 뜸
                        dialog=new Dialog(context);
                        dialog.setContentView(R.layout.dialog_nickname);
                        editText_nickname=dialog.findViewById(R.id.editText_nickname);
                        TextView text_check=dialog.findViewById(R.id.text_check);

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //뒷배경 투명색으로 안하면 화면 깨짐
                        dialog.show(); //다이얼로그 ui보여주기

                        text_check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nickname = editText_nickname.getText().toString();
                                account.setNickname(nickname); //사용자 입력한 닉네임 아이템에 저장
                                if (!nickname.isEmpty()) {
                                    dataSnapshot.getRef().child("nickname").setValue(nickname);//파이어베이스에 넣기
                                    dataSnapshot.getRef().child("visit").setValue(1);//방문 데이터를 1로 바꿔서 다시 들어와도 디이얼로그가 안뜬다.
                                    dialog.dismiss(); //다이얼로그 종료
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "이름을 적어주세요", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}