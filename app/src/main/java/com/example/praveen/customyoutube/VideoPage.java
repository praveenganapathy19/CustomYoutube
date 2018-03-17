package com.example.praveen.customyoutube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class VideoPage extends YouTubeBaseActivity {

    public static String key="AIzaSyBsjEl5oDaC-ATL1Vl7ZApZ5-SUvNZS1UU";

    Button btnPlay,btnSave,btnPlaylist;
    private YouTubePlayerView youTubePlayerView;
    RatingBar ratingBar;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    Playlist playlist;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setNumStars(6);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(0.0f);
       final String videoID = getIntent().getExtras().getString("videoID");
        final String videodescription = getIntent().getExtras().getString("description");

        myDB=new DatabaseHelper(this);

        youTubePlayerView= (YouTubePlayerView) findViewById(R.id.youtube_view);
        onInitializedListener= new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {


                youTubePlayer.loadVideo(videoID);


            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getApplicationContext(),"some error", Toast.LENGTH_SHORT).show();

            }
        };
        btnPlay=(Button) findViewById(R.id.btnPlay);
        btnSave=(Button) findViewById(R.id.btnSave);
        btnPlaylist= (Button) findViewById(R.id.btnPlaylist);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(getApplicationContext(),"Video Playing!", Toast.LENGTH_SHORT).show();

                youTubePlayerView.initialize(key,onInitializedListener);


            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean iswritten=myDB.insertData(videoID,videodescription,getplaylistName());//playlist.writetoFile(videoID,videodescription,getplaylistName());
                if(iswritten==true) {
                    Toast.makeText(getApplicationContext(),"Saved to Playlist!", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Error saving to playlist", Toast.LENGTH_SHORT).show();


            }
        });

        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent("com.example.praveen.customyoutube.Playlist");
                startActivity(intent);
            }
        });

    }

    public String getplaylistName()
    {
        String playlistName="";
        String starRating= Float.toString(ratingBar.getRating());
        switch(starRating)
        {
            case "6.0":
                playlistName="K";
                break;
            case "5.0":
                playlistName="5";
                break;
            case "4.0":
                playlistName="4";
                break;
            default:
                playlistName="NULL";
        }
        return playlistName;
    }



    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
