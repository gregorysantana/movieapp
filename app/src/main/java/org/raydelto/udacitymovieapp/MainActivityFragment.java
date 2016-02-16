package org.raydelto.udacitymovieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raydelto.udacitymovieapp.entities.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private FetchMovieTask movieTask;

    public MainActivityFragment() {
        movieTask = new FetchMovieTask();
    }

    private void updateMovies(){
        movieTask.execute();
    }

    public void test(ArrayList<Movie> movies) {
        Log.v(LOG_TAG,"TEST");
        Log.v(LOG_TAG,movies.size()+"");
        for(Movie movie: movies){
            Log.v(LOG_TAG, "" + movie.getTitle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        /*ImageView imgTest = (ImageView) root.findViewById(R.id.imgTest);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(imgTest, new Callback() {
            @Override
            public void onSuccess() {
                Log.v(LOG_TAG,"SUCCESS");
            }

            @Override
            public void onError() {
                Log.v(LOG_TAG,"FAIL");

            }


        });*/
        updateMovies();
        Log.v(LOG_TAG, "TEST");
        return root;

    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();



        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Movie> getMovieDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String TITLE = "title";
            final String POPULARITY = "popularity";
            final String VOTE_AVERAGE = "vote_average";

            JSONObject movieJson = new JSONObject(forecastJsonStr);

            ArrayList<Movie> results = new ArrayList<Movie>();
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject movie = movieArray.getJSONObject(i);
                //public Movie(String title, String posterPath, String overView, String voteAverage, String popularity, String releaseDate) {
                results.add(new Movie(movie.getString(TITLE),movie.getString(POSTER_PATH),movie.getString(OVERVIEW),
                        (float) movie.getDouble(VOTE_AVERAGE),(float)movie.getDouble(POPULARITY),movie.getString(RELEASE_DATE)));



            }
            return results;

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            Log.v(LOG_TAG,"ON POST EXECUTE");
            for(Movie movie: movies){
                Log.v(LOG_TAG, "" + movie.getTitle());
            }
            Log.v(LOG_TAG,"**END**ON POST EXECUTE");

            test(movies);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String postal = params.length > 0 ? params[0] : "";
                String unit = params.length > 0 ? params[1] : "metric";
                //UriBuilder uriBuilder;
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=267eb270349b55f387d61a3b9e0f4224
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon()
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                        .build();
                String address = builtUri.toString();
                URL url = new URL(address);
                Log.v(LOG_TAG, "URL:" + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON: " + movieJsonStr);
                try {
                    return getMovieDataFromJson(movieJsonStr, 7);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "ERROR:" + e.getMessage());

                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    }
    
}
