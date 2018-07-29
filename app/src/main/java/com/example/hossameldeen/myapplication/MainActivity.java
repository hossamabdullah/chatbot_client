package com.example.hossameldeen.myapplication;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.bassaer.chatmessageview.model.ChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.FuelManager;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kotlin.Pair;
import kotlin.Triple;

public class MainActivity extends AppCompatActivity {
    private final static String ACCESS_TOKEN = "aa733bc04f6d449a91a279895f421d17";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //configuring fuel
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer "+MainActivity.ACCESS_TOKEN);
        List<Pair<String, String>> params = new ArrayList<>();
        params.add(new Pair("v", "20170712"));
        params.add(new Pair("sessionId", UUID.randomUUID()));
        params.add(new Pair("lang", "en"));
        FuelManager.Companion.getInstance().setBaseHeaders(headers);
        FuelManager.Companion.getInstance().setBasePath("https://api.dialogflow.com/api/");
        FuelManager.Companion.getInstance().setBaseParams(params);

        //creating chat parteners
        final ChatUser human = new ChatUser(1,"You", BitmapFactory.decodeResource(getResources(),R.drawable.ic_account_circle));
        final ChatUser agent = new ChatUser(2, "Gan", BitmapFactory.decodeResource(getResources(),R.drawable.ic_account_circle));

        //sending and recieveing messages
        final ChatView chatView = findViewById(R.id.my_chat_view);
        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatView.send(new Message.Builder()
                        .setUser(human)
                        .setText(chatView.getInputText())
                        .build());

                List<Pair<String, String>> query = new ArrayList<>();
                query.add(new Pair("query", chatView.getInputText()));
                Fuel.get("/query", query).responseString(new Handler<String>() {
                    @Override
                    public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError error) {
//                        updateUI(error, null);
                        System.out.println("Hello World");
                    }

                    @Override
                    public void success(@NotNull Request request, @NotNull Response response, String data) {
//                        updateUI(null, data);
                        System.out.println("Data = "+data);
                        String agentReply = null;
                        try {
                            JSONObject jsonObj = new JSONObject(data);
                            jsonObj = jsonObj.getJSONObject("result");
                            jsonObj = jsonObj.getJSONObject("fulfillment");
                            agentReply = jsonObj.getString("speech");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        chatView.send(new Message.Builder()
                            .setRight(true)
                            .setUser(agent)
                            .setText(agentReply)
                            .build());
                    }
                });
            }
        });


    }
}
