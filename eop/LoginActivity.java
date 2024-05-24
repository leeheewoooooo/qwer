package com.example.eop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eop.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    SignInButton googleAuth; // Google 로그인 버튼
    GoogleSignInClient mGoogleSignClient; // Google 로그인 클라이언트
    int RC_SIGN_IN = 20; // Google 로그인 요청 코드

    FirebaseAuth auth; // Firebase 인증 객체
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 객체
    private EditText id, password; // 이메일과 비밀번호 입력 필드
    private Button login; // 로그인 버튼
    private TextView find,gogo; //회원가입, 비밀번호 찾기 텍스트
    FirebaseDatabase database; // Firebase 데이터베이스 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // XML 레이아웃에서 버튼과 필드를 가져와 변수에 연결합니다.
        //googleAuth = findViewById(R.id.google_bt);
        find = findViewById(R.id.hint);
        id = findViewById(R.id.id);
        password = findViewById(R.id.ps);
        login = findViewById(R.id.login);
        gogo = findViewById(R.id.join);
        mFirebaseAuth=FirebaseAuth.getInstance();

        // Firebase 인증 객체 및 데이터베이스 객체를 초기화합니다.
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
/**
        // Google 로그인 옵션을 설정하고 GoogleSignInClient를 초기화합니다.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);

        // Google 로그인 버튼 클릭 시의 동작을 설정합니다.
        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
**/
        // 사용자가 이미 로그인한 상태라면 메인 액티비티로 이동합니다.
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // 로그인 버튼 클릭 시의 동작을 설정합니다.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 이메일과 비밀번호를 가져옵니다.
                String strEmail = id.getText().toString();
                String strPwd = password.getText().toString();
                // FirebaseAuth를 사용하여 사용자를 인증하고 로그인합니다.
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // 로그인이 성공했을 경우 메인 액티비티로 이동합니다.
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // 로그인 실패 시 메시지를 표시합니다.
                                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // 회원가입 버튼 클릭 시 회원가입 액티비티로 이동합니다.
        gogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 버튼 클릭 시 힌트 액티비티로 이동합니다.
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, hint.class);
                startActivity(intent);
            }
        });
    }
/**
    // Google 로그인을 처리하기 위한 메소드입니다.
    private void googleSignIn() {
        Intent intent = mGoogleSignClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    // Google 로그인 결과를 처리하는 메소드입니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult();
                firebaseAuth(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Firebase에 Google 로그인 정보를 전달하여 인증하는 메소드입니다.
    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Firebase에 로그인한 사용자 정보를 저장합니다.
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("id", user.getUid());
                                map.put("name", user.getDisplayName());
                                map.put("profile", user.getPhotoUrl().toString());
                                database.getReference().child("users").child(user.getUid()).setValue(map);
                            }
                            // 로그인 성공 시 메인 액티비티로 이동합니다.
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // 로그인 실패 시 메시지를 표시합니다.
                            Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }**/
}