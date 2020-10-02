package ccautivar.csumb.flix;

import android.os.Bundle;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Parcels;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import ccautivar.csumb.flix.models.Movie;
import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {
    private static final String YOUTUBE_API_KEY = "AIzaSyATkeIxv4WrbcP1dDyGpUILA-W3MyinXGM";
    private static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    TextView tvTitle;
    RatingBar ratingBar;
    TextView tvOverview;
    TextView tvPop;
    YouTubePlayerView player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        player = findViewById(R.id.player);
        tvPop = findViewById(R.id.tvPop);

        String title = getIntent().getStringExtra("title");
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getRating());
        tvPop.setText("Popularity: " + String.valueOf(movie.getPopularity()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL,movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("DetailActivity", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("DetailActivity", "Results: " + results.toString());
                    if (results.length()==0){
                        return;
                    }
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    initializeYoutube(youtubeKey);
                } catch (JSONException e){
                    Log.e("DetailActivity", "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DetailActivity", "oonFailure");
            }
        });


    }

    private void initializeYoutube(final String youtubeKey) {
        player.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity","player success");
                youTubePlayer.cueVideo(youtubeKey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity","player failure");
            }
        });
    }
}