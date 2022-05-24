package com.jaikeerthick.ytgrabber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jaikeerthick.ytgrabber.databinding.ActivityResultBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private static String TAG = "ResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("url")){
            try {
                grabData(getIntent().getStringExtra("url"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void grabData(String rawUrl) throws IOException {

        String videoID = rawUrl.replace("https://youtu.be/", "")
                .replace("https://www.youtube.com/watch?v=", "");


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://www.youtube.com/watch?v=" + videoID)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "Error occured, try with different valid url", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String myResponse = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchValues(myResponse, videoID);
                    }
                });

            }
        });
    }

    private void fetchValues(String myResponse, String videoID){

        JSONObject jsonObject = new JSONObject();
        String title = "";
        String views = "";
        String channelName = "";
        String subscribers = "";

        // \"([^\"]+)\"\\s*:\\s*\"([^\"]+)\",?
        //1. "channelName"\s*:\s*"(.+?)"
        Pattern p = Pattern.compile("\"channelName\"\\s*:\\s*\"(.+?)\"");
        Matcher m = p.matcher(myResponse);

        StringBuilder sb = new StringBuilder();

        while(m.find()) {
            // channel name
            Log.d(TAG, " " + m.group());
            channelName = m.group().replace("channelName","")
                    .replace(":","")
                    .replace("\"","");
        }

        //2. title
        //videoPrimaryInfoRenderer":{"title":{"runs":[{"text
        Pattern p2 = Pattern.compile("\"videoPrimaryInfoRenderer\":\\{\"title\":\\{\"runs\":\\[\\{\"text\"\\s*:\\s*\"(.+?)\"");
        Matcher m2 = p2.matcher(myResponse);

        while(m2.find()) {
            // video titlw
            Log.d(TAG, " " + m2.group());
            title = m2.group().replace("videoPrimaryInfoRenderer\":{\"title\":{\"runs\":[{\"text","")
                    .replace(":","")
                    .replace("\"","");
            break;
        }
        //3. viewCount
        //viewCount":{"videoViewCountRenderer":{"viewCount":{"simpleText
        Pattern p3 = Pattern.compile("\"viewCount\":\\{\"videoViewCountRenderer\":\\{\"viewCount\":\\{\"simpleText\"\\s*:\\s*\"(.+?)\"");
        Matcher m3 = p3.matcher(myResponse);
        while(m3.find()) {
            // video views
            Log.d(TAG, " " + m3.group());
            views = m3.group().replace("viewCount\":{\"videoViewCountRenderer\":{\"viewCount\":{\"simpleText","")
                    .replace(":","")
                    .replace("\"","");
            break;
        }

        //4. subscriberCountText":{"accessibility":{"accessibilityData":{"label
        Pattern p4 = Pattern.compile("\"subscriberCountText\":\\{\"accessibility\":\\{\"accessibilityData\":\\{\"label\"\\s*:\\s*\"(.+?)\"");
        Matcher m4 = p4.matcher(myResponse);

        while(m4.find()) {
            // video titlw
            Log.d(TAG, " " + m4.group());
            subscribers = m4.group().replace("subscriberCountText\":{\"accessibility\":{\"accessibilityData\":{\"label","")
                    .replace(":","")
                    .replace("\"","");
            break;
        }

        // finally
        try {
            jsonObject.put("title", title);
            jsonObject.put("views", views);
            jsonObject.put("channel_name", channelName);
            jsonObject.put("channel_subscribers", subscribers);

            Log.d(TAG, "JSON OBJECT: " + jsonObject.toString());
            loadUI(jsonObject, videoID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadUI(JSONObject object, String videoID){

        try {
            binding.urlText.setText("https://www.youtube.com/watch?v="+videoID);

            binding.channelNameText.setText(object.getString("channel_name"));
            binding.titleText.setText(object.getString("title"));
            binding.subscribersText.setText(object.getString("channel_subscribers"));
            binding.viewsText.setText(object.getString("views"));

            binding.loaderLayout.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}