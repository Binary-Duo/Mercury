package org.teslastemcs.binaryduo.irc;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class EntryActivity extends AppCompatActivity {

    Socket socket;
    boolean willAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        willAuth = false;
    }

    public void toggleAuth(View view){
        CheckBox authBox = (CheckBox) view;
        ConstraintLayout authFields = (ConstraintLayout) findViewById(R.id.authContainer);
        if(authBox.isChecked()){
            authFields.setVisibility(View.VISIBLE);
            willAuth = true;
        } else{
            authFields.setVisibility(View.INVISIBLE);
            willAuth = false;
        }
    }

    public void loginPress(View view){
        EditText servBox = (EditText) findViewById(R.id.servBox);
        EditText nickBox = (EditText) findViewById(R.id.nickBox);
        String server = servBox.getText().toString();
        String nick = nickBox.getText().toString();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("EXTRA_SERVER", server);
        intent.putExtra("EXTRA_NICK", nick);
        startActivity(intent);
    }
}
