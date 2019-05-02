package com.example.mvvmtest.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.mvvmtest.R;
import com.example.mvvmtest.adapter.ChatAdapter;
import com.example.mvvmtest.dagger.component.ApiController;
import com.example.mvvmtest.manager.Preferences;
import com.example.mvvmtest.model.Message;
import com.example.mvvmtest.viewmodel.ChatViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;

    @Inject
    protected Preferences sharedPreferences;

    @BindView(R.id.chatRecyclerView)
    protected RecyclerView recyclerView;

    @BindView(R.id.sendButton)
    protected Button sendButton;

    @BindView(R.id.sendEditText)
    protected EditText sendEditText;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);
        ApiController.getAppComponent().inject(this);
        initViewModel();
        initAdapter();
        checkChatStatus();

    }

    private void checkChatStatus() {
        if (chatViewModel.isConnected()) {
            toolbar.setSubtitle("Connected");
        } else {
            toolbar.setSubtitle("Disconnected");
        }
    }

    private void initAdapter() {
        chatAdapter = new ChatAdapter(ChatActivity.this, chatViewModel.getMessages().getValue());
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
    }

    private void initViewModel() {
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        chatViewModel.init(2);
        //todo pasar nickname por el Intent
        chatViewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(@Nullable List<Message> messages) {
                chatAdapter.notifyDataSetChanged();
            }
        });
        chatViewModel.getIsTyping().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTyping) {
                //todo crear logica para is typing

            }
        });

    }

    @OnClick(R.id.sendButton)
    protected void sendMessage() {
        String text = sendEditText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        chatViewModel.sendMessage(new Message(sharedPreferences.getIdUser(), 2 /*todo ver nickname here*/, text));
        sendEditText.setText("");
    }


}
