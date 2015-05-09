package com.example.sjayaram.instagramapp;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends Activity {

    public static final String CLIENT_ID = "3adaab4673bd4e2ab14b16a2d07c0793";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        photos = new ArrayList<>();

        aPhotos = new InstagramPhotoAdapter(this, photos);
        ListView lvPhotos = (ListView)findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void fetchTimelineAsync(int page) {
                //aPhotos.clear();
                fetchPopularPhotos();
                // ...the data has come back, add new items to your adapter...
                //aPhotos.addAll(photos);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
    }

    public void fetchPopularPhotos() {

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("DEBUG" , response.toString());
                JSONArray photosJson = null;
                try {
                    photosJson = response.getJSONArray("data");

                    for(int i = 0; i < photosJson.length(); i++)
                    {
                        JSONObject photoJson = photosJson.getJSONObject(i);
                        String type;
                        type = photoJson.getString("type");

                        InstagramPhoto photo = new InstagramPhoto();
                        photo.username = photoJson.getJSONObject("user").getString("username");

                        if (photoJson.optJSONObject("caption") != null)
                            photo.caption = photoJson.getJSONObject("caption").getString("text");

                        if("image".equalsIgnoreCase(type))
                            photo.imageUrl = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        else
                           photo.videoUrl = photoJson.getJSONObject("videos").getJSONObject("standard_resolution").getString("url");

                        //photo.imageHeight = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("height");
                        photo.likesCount = photoJson.getJSONObject("likes").getInt("count");
                        photo.profile_picture = photoJson.getJSONObject("user").getString("profile_picture");
                        photo.timeStamp = photoJson.getLong("created_time");
                        photos.add(0, photo);
                    }
                }
                catch(JSONException ex){
                    ex.printStackTrace();
                }
                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
