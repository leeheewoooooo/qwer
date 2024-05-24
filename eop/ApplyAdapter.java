package com.example.eop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ApplyAdapter extends RecyclerView.Adapter<ApplyAdapter.ViewHolder> {

    private ArrayList<ApplyItem> applyItems;
    private Context context;

    public ApplyAdapter(ArrayList<ApplyItem> applyItems, Context context) {
        this.applyItems = applyItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ApplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder= LayoutInflater.from(parent.getContext()).inflate(R.layout.apply_list,parent,false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyAdapter.ViewHolder holder, int position) {
        holder.text_title.setText(applyItems.get(position).getTitle());
        holder.text_content.setText(applyItems.get(position).getContent());
        //holder.bubble_number.setText(applyItems.get(position).getNumber());
        holder.text_date.setText(applyItems.get(position).getDate());
        if(!applyItems.get(position).getNumber().equals("0")){ //댓글 수가 0이면 말풍선 ui가 안보임
            holder.speech_bubble.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            holder.bubble_number.setVisibility(View.VISIBLE);
            holder.bubble_number.setText(applyItems.get(position).getNumber());
        }
    }

    @Override
    public int getItemCount() {
        if (applyItems != null) {
            return applyItems.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_title,text_content,bubble_number,text_date,speech_bubble,line;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_title=itemView.findViewById(R.id.text_title);
            text_content=itemView.findViewById(R.id.text_content);
            bubble_number=itemView.findViewById(R.id.bubble_number);
            text_date=itemView.findViewById(R.id.text_date);
            speech_bubble=itemView.findViewById(R.id.speech_bubble);
            line=itemView.findViewById(R.id.line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positon=getAdapterPosition(); //해당 리사이클러뷰에 위치를 받아온다
                    Intent intent=new Intent(context, RecruitmentActivity.class);
                    intent.putExtra("id",applyItems.get(positon).getId()); // 해당 고유 id를 모집하기 엑티비티로
                    intent.putExtra("title",applyItems.get(positon).getTitle()); //해당 리사이클러뷰에 제목을 모집하기 엑티비티로
                    intent.putExtra("content",applyItems.get(positon).getContent()); //해당 리사이클러뷰에 내용을 모집하기 엑티비티로
                    intent.putExtra("date",applyItems.get(positon).getDate()); //해당 리사이클러뷰에 날짜를 모집하기 엑티비티로
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                }
            });
        }
    }
}
