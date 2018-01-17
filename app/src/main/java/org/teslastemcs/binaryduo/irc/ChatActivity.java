package org.teslastemcs.binaryduo.irc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ConcurrentLinkedQueue<String> inQueue;
    ConcurrentLinkedQueue<String> outQueue;

    private String nick;

    private static final byte[] DELIMITER = {'\r', '\n'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Intent intent = getIntent();
        this.nick = intent.getStringExtra("EXTRA_NICK");
        String server = intent.getStringExtra("EXTRA_SERVER");
        String port = "6667";
        inQueue = new ConcurrentLinkedQueue<String>();
        outQueue = new ConcurrentLinkedQueue<String>();
        new NetworkTask().execute(server, port);
        new MessageTask().execute();
        outQueue.add("NICK " + nick);
        outQueue.add("USER " + nick + " 0 " + server + " :Mercury");
        //outQueue.add("JOIN binaryduo_mercury");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return true;
    }

    public void sendPress(View view){
        Log.i("ChatActivity", "Send button pressed");
        EditText chatBox = findViewById(R.id.chatBox);
        String message = chatBox.getText().toString();
        Log.i("Message", message);
        if(message.charAt(0) == '/'){
            Log.i("Message Type", "Command");
            outQueue.add(message.substring(1));
        } else{
            Log.i("Message Type", "Notice");
            outQueue.add("NOTICE #binduo " + message);
        }
        chatBox.setText("");
    }

    private class MessageTask extends AsyncTask<String, Boolean, Boolean> //might have different params
    {
        private boolean run;
        protected Boolean doInBackground(String... params) {
            run = true;
            while (run && !inQueue.isEmpty()) {
                //ConcurrentLinkedQueue<String> queueTest = new ConcurrentLinkedQueue<>(); //Testing Purposes
                //queueTest.add("HelloWorld"); //Testing Purposes
                String message = inQueue.poll();
                int nickSpaceIndex = message.substring(message.indexOf("!")).lastIndexOf(" ");
                String messageNick = message.substring(nickSpaceIndex+1, message.indexOf("!"));
                String messageText = message.substring(message.indexOf(":")+1, nickSpaceIndex);
                String messageUser = message.substring(message.indexOf("!")+1, message.indexOf("@"));
                String messageHost = message.substring(message.indexOf("@")+1);
                Log.d("MessageTask", message);
                String command = "TIME chat.freenode.net";
                outQueue.add(command);
                TextView textView2 = (TextView)findViewById(R.id.textView2);
                textView2.append("\n[" + inQueue.poll()/*time*/ + "] <@" + messageNick + ">" + messageText);
            }
            return run;
        }
    }

    private class NetworkTask extends AsyncTask<String, Boolean, Boolean> {
        private boolean run;
        protected Boolean doInBackground(String... params){
            run = true;
            String host = params[0];
            int port = Integer.parseInt(params[1]);
            try{
                Socket socket = new Socket(host, port);
                OutputStream outStream = socket.getOutputStream();
                InputStream inStream = socket.getInputStream();
                Log.i("NetworkTask", "Connected to socket");
                while(run){
                    // process input
                    if(inStream.available() > 0){
                        Log.i("NetworkTask", "Detected Message");
                        boolean foundCR = false;
                        StringBuilder sb = new StringBuilder();
                        while(true){
                            int next = inStream.read();
                            if(foundCR){
                                if(next == '\n'){
                                    break;
                                } else{
                                    foundCR = false;
                                    sb.append('\n');
                                }
                            }
                            if(next == '\r'){
                                foundCR = true;
                            } else {
                                sb.append((char)next);
                            }
                        }
                        String result = sb.toString();
                        inQueue.add(result);
                        Log.i("NetworkTask", "Processed Message:");
                        Log.i("Message", result);
                    }

                    // process output
                    if(!outQueue.isEmpty()){
                        Log.i("NetworkTask", "Sending Message");
                        String message = outQueue.poll();
                        outStream.write(message.getBytes());
                        outStream.write(DELIMITER);
                        Log.i("NetworkTask", "Message Sent:");
                        Log.i("Message", message);
                    }
                }
                inStream.close();
                outStream.close();
                socket.close();
            } catch(IOException e){
                return false;
            }
            return true;
        }
    }
}
