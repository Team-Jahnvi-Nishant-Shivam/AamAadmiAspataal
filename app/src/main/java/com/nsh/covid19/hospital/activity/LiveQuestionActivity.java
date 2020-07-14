package com.nsh.covid19.hospital.activity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nsh.covid19.hospital.R;
import com.nsh.covid19.hospital.model.ChatMessage;

public class LiveQuestionActivity extends AppCompatActivity {

    ListView recyclerView;
    ImageView send;
    EditText input;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_query);
        recyclerView = findViewById(R.id.recyclerView);
        input = findViewById(R.id.message);
        send = findViewById(R.id.send);
        send.setOnClickListener(view -> {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("messages")
                    .push()
                    .setValue(new ChatMessage(String.valueOf(input.getText()),
                            String.valueOf(getSharedPreferences("covid",0).getInt("type",1)),
                            FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid())
                    );
            input.setText("");
        });
        displayChatMessages();
    }

    public void displayChatMessages() {
        findViewById(R.id.pb).setVisibility(View.VISIBLE);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.adapter_question, FirebaseDatabase.getInstance().getReference().child("messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                findViewById(R.id.pb).setVisibility(View.GONE);

                RelativeLayout from = v.findViewById(R.id.from);
                RelativeLayout to = v.findViewById(R.id.to);

                TextView messageText = v.findViewById(R.id.title1);
                TextView messageTime = v.findViewById(R.id.title);

                TextView messageText1 = v.findViewById(R.id.title3);
                TextView messageTime1 = v.findViewById(R.id.title2);

                if (model.getMessageUser().equals("0")) {
                    messageText.setText(model.getMessageText());
                    messageTime.setText(DateFormat.format("dd-MM HH:mm",model.getMessageTime()));
                    from.setVisibility(View.GONE);
                } else {
                    to.setVisibility(View.GONE);
                    messageText1.setText(model.getMessageText());
                    messageTime1.setText(DateFormat.format("dd-MM HH:mm",model.getMessageTime()));
                }

                if (model.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if (to.getVisibility() == View.VISIBLE) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)to.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        to.setLayoutParams(params);
                    } else {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)from.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        from.setLayoutParams(params);
                    }
                }

            }
        };
        recyclerView.setAdapter(adapter);
    }
}
