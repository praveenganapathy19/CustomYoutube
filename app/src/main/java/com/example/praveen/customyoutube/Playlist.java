package com.example.praveen.customyoutube;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

public class Playlist extends YouTubeBaseActivity {

    DatabaseHelper myDb;
    Button btnShow,btnRemove;
    ListView lstView;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer youTubePlayer;
    private static YouTube youTube;
    public static String key="AIzaSyBsjEl5oDaC-ATL1Vl7ZApZ5-SUvNZS1UU";
    static int dbCount=0,index=0;
    static String[] playlistVideoIDs;
    public static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    final static String[] itemDescription=new String[(int)NUMBER_OF_VIDEOS_RETURNED];
    final static String[] itemThumbnail=new String[(int)NUMBER_OF_VIDEOS_RETURNED];
    final static String[] itemID=new String[dbCount];

    private YouTubePlayer.OnInitializedListener onInitializedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDb = new DatabaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        btnShow=(Button) findViewById(R.id.btnShowPlaylist);
        btnRemove=(Button) findViewById(R.id.btnRemove);
        lstView=(ListView) findViewById(R.id.lstView);

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.deleteLinkfromList(playlistVideoIDs[index]);
                itemDescription[index]=null;
                itemThumbnail[index]=null;
                while((index+1)<dbCount)
                {
                    itemDescription[index]=itemDescription[index+1];
                    itemThumbnail[index]=itemThumbnail[index+1];
                }
                customListAdapter adapter=new customListAdapter(Playlist.this, itemDescription, itemThumbnail);
                lstView.setAdapter(adapter);
                Toast.makeText(getApplicationContext(),"Link removed from playlist!", Toast.LENGTH_LONG).show();

            }
        });
        youTubePlayerView=(YouTubePlayerView) findViewById(R.id.youtube_view);
        onInitializedListener= new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Toast.makeText(getApplicationContext(),"List View"+index, Toast.LENGTH_LONG).show();


                youTubePlayer.loadVideo(playlistVideoIDs[index]);


            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(),"some error", Toast.LENGTH_SHORT).show();

            }
        };


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Toast.makeText(getApplicationContext(),"Selected item is "+spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                /*
                Reading from database
                 */
                Cursor res=myDb.getAllData(getplaylistName(spinner.getSelectedItem().toString()));
                if((dbCount=res.getCount()) == 0) {
                    // show message
                    showMessage("Error","No videos found in playlist!");
                }

                playlistVideoIDs=new String[res.getCount()];
                if(res.moveToFirst())
                {
                    int i=0;
                    do {
                        playlistVideoIDs[i]=res.getString(res.getColumnIndex("VIDEO_ID"));
//                        itemID[i]=playlistVideoIDs[i];
                        i++;
                    }while(res.moveToNext());
                }
                else
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();


                /*
                End of Reading function
                 */

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Context context=Playlist.this;


                youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest httpRequest) throws IOException {
                    }
                }).setApplicationName(context.getString(R.string.app_name)).build();

                try {
                    for(int j=0;j<res.getCount();j++) {
                        YouTube.Videos.List videoslistbyID = youTube.videos().list("snippet");
                        videoslistbyID.setKey(key);
                        videoslistbyID.setId(playlistVideoIDs[j]);
                        VideoListResponse response=videoslistbyID.execute();
                        List<Video> searchResultList = response.getItems();
                        Iterator<Video> searchlistIterator=searchResultList.iterator();
                        if (searchResultList != null) {


                            //Toast.makeText(MainActivity.this,"Search string is" +searchResultList.get(1), Toast.LENGTH_LONG).show();
                            while (searchlistIterator.hasNext()) {
                                Video singleVideo = searchlistIterator.next();
                                itemDescription[j]=singleVideo.getSnippet().getTitle();


                                Thumbnail thumbnail=singleVideo.getSnippet().getThumbnails().getDefault();
                                itemThumbnail[j]=thumbnail.getUrl();

                                customListAdapter adapter=new customListAdapter(Playlist.this, itemDescription, itemThumbnail);
                                lstView.setAdapter(adapter);
                                //itemdescFlag++;
                            }

//                       updateList(searchlistIterator, queryTerm);
                        }
                        else
                            Toast.makeText(Playlist.this,"NULL", Toast.LENGTH_LONG).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                index=position;
                youTubePlayerView.initialize(key,onInitializedListener);

//                youTubePlayer.loadVideo(playlistVideoIDs[position]);

                // Toast.makeText(getApplicationContext(), itemDescription[position], Toast.LENGTH_SHORT).show();

            }
        });

    }

    public String getplaylistName(String spinnerItem)
    {
        String playlistName="";
        switch(spinnerItem)
        {
            case "K-List":
                playlistName="K";
                break;
            case "5* List":
                playlistName="5";
                break;
            case "4* List":
                playlistName="4";
                break;
            default:
                playlistName="NULL";

        }
        return playlistName;
    }

    public boolean writetoFile(String videoID, String description, String playlistName)
    {
       boolean isInserted=myDb.insertData(videoID,description,playlistName);
        if(isInserted == true) {
            return true;
        }
        else
            return false;
    }

    public String[] readfromFile(String playlistName) throws InterruptedException {
        Cursor res=myDb.getAllData(playlistName);
        if(res.getCount() == 0) {
            // show message
            showMessage("Error","No videos found in playlist!");
            return null;
        }

        String[] playlistVideoIDs=new String[res.getCount()];
        if(res.moveToFirst())
        {
            int i=0;
            do {
                playlistVideoIDs[i]=res.getString(res.getColumnIndex("VIDEO_ID"));
                i++;
            }while(res.moveToNext());
        }
        else
            return null;
        for(int j=0;j<res.getCount();j++) {
            Toast.makeText(getApplicationContext(), "Retrieved values are " + playlistVideoIDs[j], Toast.LENGTH_LONG).show();
            Thread.sleep(1000);
        }

        //showMessage("Found!","Video ID:"+res.getString(1));
        return playlistVideoIDs;

    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
