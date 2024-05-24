package com.example.eop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class hint extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private EditText edit1;
    private TextView edit2;
    private Button find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        edit1 = findViewById(R.id.email);
        edit2 = findViewById(R.id.hint_view);
        find = findViewById(R.id.hint_view_bt);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // edit1에서 입력된 아이디를 가져옴
                String inputId = edit1.getText().toString();

                //UserAccount아래의 emailId에 대한 값들을 가 정렬하고 그 값들중에 equalTo로 내가 입력한 데이터를 확인
                //즉"UserAccount" 위치에서 "emailId" 필드의 값이 inputId와 데이터베이스상에서 일치하는지 조회한다.
                //equalTo() 메서드는 데이터베이스에서 특정 필드의 값이 지정된 값과 일치하는 경우에만 해당 데이터를 가져옵니다.
                // 즉 여기서 데이터를 조회했는지 안했는지가 결정되어 if문과 else문으로 나뉘게 된다.
                mDatabaseRef.child("UserAccount").orderByChild("emailId").equalTo(inputId)

                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //snapshot객체는 현재 조회된 데이터를 조작할 수 있다. 즉 위에서 inputid에 대한 데이터를 읽었으니
                                //해당 값이 존재하는가 확인하는거다.
                                if (snapshot.exists()) {

                                    //for문의 getChildren()메서드로 해당 snapshot의 조회덴 데이터에 대해 전부 DataSnapshot의 객체를 반환한다.
                                    //즉 위의 snapshot이 가리키는 위치의 모든 하위 데이터에 대해  반복적으로 작업하는거임.
                                    //그냥 내부 데이터들에 대해 접근할 수 있게 만들어 줌.
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                        // 각 dataSnapshot에서 데이터를 가져온다. 위에서 이제 하위데이터에대해 작업할수 있게 설정했으니
                                        //getValue로 값을 가져온다. 그걸 userAccount클래스로 형변환 해주는것이다.
                                        //데이터 뽑아주고 싶으면 해당 구문을 외워도 좋을것같다.
                                        //여기서 유의사항이 UserAccount클래스 내에 데이터 즉 키값이 다 존재해야한다. 키에 대한 변수가 없는
                                        //경우 오류가 난다.
                                        UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                                        if (userAccount != null) {
                                            // 힌트를 가져와서 edit2에 설정
                                            String hint = userAccount.getMommo();
                                            edit2.setText(hint);
                                            break; // 한 번만 설정하고 루프 종료
                                        }
                                    }

                                    String a = snapshot.toString();
                                    Toast.makeText(hint.this, a, Toast.LENGTH_SHORT).show();


                                } else {
                                    // 아이디에 대한 정보가 존재하지 않는 경우
                                    Toast.makeText(hint.this, "일치하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // 조회가 취소된 경우
                                Toast.makeText(hint.this, "조회 취소됨", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
    }
}
