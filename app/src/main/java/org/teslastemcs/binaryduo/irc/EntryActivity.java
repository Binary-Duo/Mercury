package org.teslastemcs.binaryduo.irc;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
    }

    public void toggleAuth(View view){
        CheckBox authBox = (CheckBox) view;
        ConstraintLayout authFields = (ConstraintLayout) findViewById(R.id.authContainer);
        if(authBox.isChecked()){
            authFields.setVisibility(View.VISIBLE);
        } else{
            authFields.setVisibility(View.INVISIBLE);
        }
    }

    public void proceed(View view){
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
