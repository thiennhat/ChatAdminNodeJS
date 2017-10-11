package com.techhub.chatadminnodejs;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techhub.chatadminnodejs.Adapter.ListUserMessageAdapter;
import com.techhub.chatadminnodejs.Adapter.MessageAdapter;
import com.techhub.chatadminnodejs.ClassUse.CheckinternetToat;
import com.techhub.chatadminnodejs.OBJ.Message;
import com.techhub.chatadminnodejs.OBJ.MessageSeen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lunainc.chatbar.ViewChatBar;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbarmhchat;
    RecyclerView recyclerViewnhantin;
    NavigationView navigationViewchat;
    ListView listViewmenutinnhan;
    DrawerLayout drawerLayoutchat;
   // EditText edtnoidungtinnhanjv;
   // Button btnguitinnhanjv;
    ViewChatBar chatbar;



    ArrayList<String> mangusername;
    ArrayList<Message> mangchat;
    MessageAdapter messageAdapter;
    String adminuser="Admin";
    private String user_name,room_name,temp_key,temp_keyroot2;
    private DatabaseReference root;
    private DatabaseReference rootmessen;
    private DatabaseReference online;
    static boolean chatactivitytofiticlick=false;
    static String chatactivitinottyfi;
    private int index=0;

    private com.github.nkzawa.socketio.client.Socket mSocket;
    {
        try{
            mSocket= IO.socket("http://192.13368.0.105:3000");

        }catch (URISyntaxException e){}

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Anhxa();
        Firebase();
        ConnectSocketio();
        Actionbar();
    }

    private void Firebase() {

        room_name=getIntent().getExtras().get("from_user_id").toString();

        setTitle("Room -:"+room_name);
        toolbarmhchat.setTitle(room_name);
        chatactivitinottyfi=toolbarmhchat.getTitle().toString();

        //lay data trong room
        root=FirebaseDatabase.getInstance().getReference().child("Message").child(room_name);
        rootmessen=FirebaseDatabase.getInstance().getReference().child("MessageSeen").child(room_name);
        online=FirebaseDatabase.getInstance().getReference().child("Client").child(room_name);


        chatbar.setSendClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map=new HashMap<String,Object>();

                temp_key=root.push().getKey();
                root.updateChildren(map);
                DatabaseReference message_root=root.child(temp_key);
                Map<String,Object> map2=new HashMap<String,Object>();
                map2.put("name",user_name);
                map2.put("msg",chatbar.getMessageText());
                message_root.updateChildren(map2);






                //rootsenn
                Map<String,Object> map3=new HashMap<String,Object>();
                temp_keyroot2=rootmessen.push().getKey();
                rootmessen.updateChildren(map3);
                DatabaseReference message_rootseen=rootmessen.child(temp_keyroot2);
                Map<String,Object> map4=new HashMap<String,Object>();
                map4.put("lastmessage",chatbar.getMessageText());
                map4.put("name",user_name);
                map4.put("seen","true");
                map4.put("key",temp_keyroot2);
                message_rootseen.updateChildren(map4);



                chatbar.setClearMessage(true);
              //  Toast.makeText(getApplicationContext(),temp_key,Toast.LENGTH_LONG).show();


            }
        });



        online.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                checkonline(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootmessen.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_converseen(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_converseen(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conver(dataSnapshot);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conver(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    @Override
    protected void onPause() {
        super.onPause();
        if(MainActivity.Clickmenu==true) {
            MainActivity.arrayListusermess.get(Integer.parseInt(getIntent().getExtras().get("index").toString())).setSeen(true);
        }
        else{


        }
       // rootmessen.child("")
    }

    private String clientkey_push,client_user_id;
    private String client_online;
    private void checkonline(DataSnapshot dataSnapshot){
        Iterator i=dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            clientkey_push=(String)((DataSnapshot)i.next()).getValue();
            client_online=(String)((DataSnapshot)i.next()).getValue();
            client_user_id=(String)((DataSnapshot)i.next()).getValue();
            if(client_online !=null && client_online!="true"){
                Toast.makeText(getApplicationContext(),"user đã thoát",Toast.LENGTH_LONG).show();
            }
        }


    }
    private String chat_msg,chat_user_name,seen;
    private void append_chat_conver(DataSnapshot dataSnapshot){


        Iterator i=dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            chat_msg=(String)((DataSnapshot)i.next()).getValue();
            chat_user_name=(String)((DataSnapshot)i.next()).getValue();
            if(chat_user_name.equals("Admin")){
                Message message1=new Message(chat_user_name,chat_msg,true);
                mangchat.add(message1);
            }
            else{
                Message message1=new Message(chat_user_name,chat_msg,false);
                mangchat.add(message1);

            }
            messageAdapter.notifyDataSetChanged();

            recyclerViewnhantin.scrollToPosition(messageAdapter.getItemCount()-1);


        }

    }






    private void append_chat_converseen(DataSnapshot dataSnapshot){


        Iterator i=dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            String key=(String)((DataSnapshot)i.next()).getValue();

            chat_msg=(String)((DataSnapshot)i.next()).getValue();
            chat_user_name=(String)((DataSnapshot)i.next()).getValue();
            seen=(String)((DataSnapshot)i.next()).getValue();

            if(MainActivity.Clickmenu==true) {
                rootmessen.child(key).child("seen").setValue("true");

                if (chat_user_name.equals("Admin")) {
                    MainActivity.arrayListusermess.get(Integer.parseInt(getIntent().getExtras().get("index").toString())).setLastMess(chat_msg);
                } else {
                    MainActivity.arrayListusermess.get(Integer.parseInt(getIntent().getExtras().get("index").toString())).setLastMess(chat_msg);
                }
                MainActivity.listUserMessageAdapter.notifyDataSetChanged();
            }
            else{
                rootmessen.child(key).child("seen").setValue("true");
            }

            }


           // recyclerViewnhantin.scrollToPosition(messageAdapter.getItemCount()-1);


        }





    private void ConnectSocketio() {
       // mSocket.connect();

       // mSocket.on("svguiusn",onNew_dsusn);
        //mSocket.on("serverguichat",onNew_guitinchat);








    }




    private Emitter.Listener onNew_guitinchat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung,id;

                    try {
                        noidung = data.getString("tinchat");
                        id=data.getString("idchat");
                        Toast.makeText(getApplicationContext(),noidung.toString()+id.toString(),Toast.LENGTH_LONG).show();

                        /*if(!noidung.toString().equals(edtnoidungtinnhanjv.getText().toString())){
                            Message message=new Message(id,noidung,false);
                            mangchat.add(message);

                        }
                        else{
                           // Message message1=new Message("Admin : ",edtnoidungtinnhanjv.getText().toString(),true);
                           // mangchat.add(message1);

                        }*/
                        messageAdapter.notifyDataSetChanged();

                      //  edtnoidungtinnhanjv.setText("");
                        recyclerViewnhantin.scrollToPosition(messageAdapter.getItemCount()-1);


                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    private Emitter.Listener onNew_dsusn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray noidung;
                    mangusername.removeAll(mangusername);

                    try {
                        noidung = data.getJSONArray("danhsach");
                        Toast.makeText(getApplicationContext(),noidung.toString(),Toast.LENGTH_LONG).show();
                        for(int i=0;i<noidung.length();i++){
                                    mangusername.add(noidung.get(i).toString());
                            }
                        ArrayAdapter adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,mangusername);
                        listViewmenutinnhan.setAdapter(adapter);
                        adapter.notifyDataSetChanged();





                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };






    private void Actionbar() {

        setSupportActionBar(toolbarmhchat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarmhchat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.Clickmenu==true){

                    finish();
                }
                else{
                    chatactivitytofiticlick=true;
                    Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                    startActivity(intent);
                }


            }
        });
    }

    private void Anhxa() {
        toolbarmhchat=(Toolbar)findViewById(R.id.toolbarmhchat);
        recyclerViewnhantin=(RecyclerView) findViewById(R.id.rclmainchat);
        listViewmenutinnhan=(ListView)findViewById(R.id.lvmenutinnhan);
        navigationViewchat=(NavigationView)findViewById(R.id.navigationviewchat);
        drawerLayoutchat=(DrawerLayout)findViewById(R.id.drawerlayoutchat);

        recyclerViewnhantin.setHasFixedSize(true);
        recyclerViewnhantin.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        chatbar=(ViewChatBar) findViewById(R.id.chatbar);
        chatbar.setMessageBoxHint("Aa");
        user_name="Admin";



        mangchat=new ArrayList<>();
        messageAdapter=new MessageAdapter(mangchat,getApplicationContext());
        mangusername=new ArrayList<String>();
        recyclerViewnhantin.setAdapter(messageAdapter);








    }
}
