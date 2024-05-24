package com.example.eop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseauth;//Firebase 인증 객체

    private DatabaseReference mDatabaseRef;//실시간 데이터베이스 참소 객체
    private EditText mEtEmail,mEtpwd,editTextText;
    private Button mBtnRegister;
    private CheckBox agree1_checkbox,agree2_checkbox,agree3_checkbox,agree4_checkbox; //추가

//가입버튼임.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase 인증 객체 초기화
        mFirebaseauth=FirebaseAuth.getInstance();

        // Firebase 실시간 데이터베이스 참조 객체 초기화
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();


        editTextText=findViewById(R.id.regist_return_password_edittext);

        mEtEmail=findViewById(R.id.regist_id_edittext);
        mEtpwd=findViewById(R.id.regist_password_edittext);
        mBtnRegister=findViewById(R.id.regist_button);//가입버튼 누르면
        agree1_checkbox=findViewById(R.id.agree1_checkbox); //추가
        agree2_checkbox=findViewById(R.id.agree2_checkbox); //추가
        agree3_checkbox=findViewById(R.id.agree3_checkbox); //추가
        agree4_checkbox=findViewById(R.id.agree4_checkbox); //추가

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 사용자가 입력한 이메일과 비밀번호 가져오기
                String strEmail = mEtEmail.getText().toString(); //입력한 텍스트를 불러와 문자열로 변환하고
                String strPwd = mEtpwd.getText().toString();//이것도
                String memo= editTextText.getText().toString();//이것도


                // Firebase Auth 객체의 createUserWithEmailAndPassword 메서드를 사용하여 사용자를 생성한다.(이메일,비번) 이걸로 사용자 정보가 설정됨.
                mFirebaseauth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //성공적이냐고 묻는거다. 불값 반환
                        if (task.isSuccessful()) {



                            //회원가입이 성공했을때 "새로 생성된 사용자의" 정보를 가져온다.
                            //이 정보를 가져오는 이유는 회원가입 성공 후에 해당 사용자의 UID나 이메일과 같은 정보를 이용하여 추가적인 작업을 수행하기 위함
                            FirebaseUser firebaseUser = mFirebaseauth.getCurrentUser();

                            // 만들어준 사용자 정보 클래스의 객체에 값들을 설정. 즉 아래 값들로 account객체참조변수의 멤버들에 값들을 저장시켜줌.
                            UserAccount account = new UserAccount();

                            //사용자 uid를 계정 정보에 설정
                            account.setUID(firebaseUser.getUid());
                            //name변수에 uid값 저장


                            //사용자 이메일을 계정 정보에 설정
                            account.setEmailId(firebaseUser.getEmail());
                            //사용자 패스워드도 계정 정보에 설정
                            account.setPassword(strPwd);
                            //위 값들은 다 UserAccount 클래스에 있는거다. 직접 만든것임.
                            account.setMommo(memo);
                            //내가 적은  텍스트를 momo에 저장한걸 보내서 account객참변에 저장해줌.

                            // Firebase 데이터베이스에 사용자 계정 정보 저장
                            //데이터베이스 참조변수 들어가서 UserAccount라는 제일 상위 노드, 그아래 getuid노드설정.
                            //마지막 setValue메서드로 해당 노드에 데이터를 저장한다. 이 메서드로 실시간 db에 기록이 되는것이다.
                            mDatabaseRef.child("UserAccount").push().setValue(account);
                            // 회원가입 성공 메시지 표시
                            Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                        }else{
                            // 회원가입 실패 시 실패 메시지 표시
                            Toast.makeText(RegisterActivity.this, "실패", Toast.LENGTH_LONG).show();


                        }

                    }

                });
            }
        });

        agree1_checkbox.setOnClickListener(new View.OnClickListener() { //추가
            @Override
            public void onClick(View v) {
                // agree1_checkbox가 체크되었는지 확인
                boolean isChecked = agree1_checkbox.isChecked();

                // 나머지 체크박스들의 상태를 agree1_checkbox와 동일하게 설정
                agree2_checkbox.setChecked(isChecked);
                agree3_checkbox.setChecked(isChecked);
                agree4_checkbox.setChecked(isChecked);
            }
        });

    }


}
