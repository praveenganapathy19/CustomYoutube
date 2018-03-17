package com.example.praveen.customyoutube;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;


public class MainActivity extends YouTubeBaseActivity{
    public static String key="AIzaSyBsjEl5oDaC-ATL1Vl7ZApZ5-SUvNZS1UU";
    public static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    private static final String PROPERTIES_FILENAME = "youtube.properties";
    static int itemdescFlag=0;

    final static String[] itemDescription=new String[(int)NUMBER_OF_VIDEOS_RETURNED];
    final static String[] itemThumbnail=new String[(int)NUMBER_OF_VIDEOS_RETURNED];
    final static String[] itemID=new String[(int)NUMBER_OF_VIDEOS_RETURNED];

    EditText editSearch;
    CheckBox chkSort;
    Button btnSearch,btnAccess,btnSort;
    ListView listView;
    private static YouTube youTube;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        editSearch= (EditText) findViewById(R.id.editSearch);
        btnSearch= (Button) findViewById(R.id.btnSearch);
        btnAccess=(Button) findViewById(R.id.btnAccess);
        btnSort=(Button) findViewById(R.id.btnSort);
        listView= (ListView) findViewById(R.id.listView);
        chkSort=(CheckBox) findViewById(R.id.chkSort);

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent("com.example.praveen.customyoutube.Playlist");
                startActivity(intent);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        itemdescFlag=0;
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Context context=MainActivity.this;
                    youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest httpRequest) throws IOException {
                        }
                    }).setApplicationName(context.getString(R.string.app_name)).build();

                    String queryTerm = editSearch.getText().toString();

                    YouTube.Search.List search = youTube.search().list("id,snippet");

                    search.setKey(key);
                    search.setQ(queryTerm);
                    if(chkSort.isChecked())
                        search.setOrder("date");
                    search.setType("video");
                    search.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
                    search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                    Thumbnail thumbnail;
                    SearchListResponse searchResponse = search.execute();
                    List<SearchResult> searchResultList = searchResponse.getItems();
                    Iterator<SearchResult> searchlistIterator=searchResultList.iterator();

                    if (searchResultList != null) {


                        //Toast.makeText(MainActivity.this,"Search string is" +searchResultList.get(1), Toast.LENGTH_LONG).show();
                        while (searchlistIterator.hasNext() && (itemdescFlag<25)) {
                            SearchResult singleVideo = searchlistIterator.next();
                            itemDescription[itemdescFlag]=singleVideo.getSnippet().getTitle();


                            thumbnail=singleVideo.getSnippet().getThumbnails().getDefault();
                            itemThumbnail[itemdescFlag]=thumbnail.getUrl();

                            ResourceId rId = singleVideo.getId();
                            itemID[itemdescFlag]=rId.getVideoId();

                            customListAdapter adapter=new customListAdapter(MainActivity.this, itemDescription, itemThumbnail);
                            listView.setAdapter(adapter);
                            itemdescFlag++;
                        }

//                       updateList(searchlistIterator, queryTerm);
                    }
                    else
                        Toast.makeText(MainActivity.this,"NULL", Toast.LENGTH_LONG).show();

                }
                catch (GoogleJsonResponseException e) {
                    Toast.makeText(MainActivity.this,"There was a service error: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage(),Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this,"There was an IO error: " + e.getCause() + " : " + e.getMessage(),Toast.LENGTH_LONG).show();
                } catch (Throwable t) {
                    Toast.makeText(MainActivity.this,"Search string gdfvd"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent=new Intent("com.example.praveen.customyoutube.VideoPage");
               intent.putExtra("videoID",itemID[position]);
               intent.putExtra("description",itemDescription[position]);
                startActivity(intent);
               // Toast.makeText(getApplicationContext(), itemDescription[position], Toast.LENGTH_SHORT).show();

            }
        });

    }

     private static void updateList(Iterator<SearchResult> iteratorSearchResults, String query)
    {
        ArrayList<SearchResult> animalNames = new ArrayList<>();

        //while()

//        if (!iteratorSearchResults.hasNext()) {
//
//
//            animalNames.add(" There aren't any results for your query.");
//            RecyclerView recyclerView = findViewById(R.id.recycleView);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            adapter = new MyRecyclerViewAdapter(this, animalNames);
//            adapter.setClickListener(this);
//            recyclerView.setAdapter(adapter);
//        }


        //int i=0;
        while (iteratorSearchResults.hasNext()) {
//        while(i<25)       {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                ///////////////////////////////////////////


                //animalNames.add(thumbnail.getUrl());

                // set up the RecyclerView
//                RecyclerView recyclerView = findViewById(R.id.recycleView);
//                recyclerView.setLayoutManager(new LinearLayoutManager(this));
//                adapter = new MyRecyclerViewAdapter(this, animalNames);
//                adapter.setClickListener(this);
//                recyclerView.setAdapter(adapter);



                ///////////////////////////////////////////////

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");

            }
            //i++;
        }
    }
}
