package org.raydelto.udacitymovieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.raydelto.udacitymovieapp.entities.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private FetchMovieTask movieTask;
    private MovieAdapter adapter;
    private ArrayList<Movie> movies;

    public MainActivityFragment() {
        movieTask = new FetchMovieTask();
        movies = new ArrayList<Movie>();
    }

    private void updateMovies(){

        movieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        adapter = MovieAdapter.getInstance(getActivity(),R.layout.movie_item, movies);

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridMovies = (GridView)root.findViewById(R.id.gridMovies);
        gridMovies.setAdapter(adapter);
        updateMovies();
        return root;

    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();



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
                JSONObject movie = movieArray.getJSONObject(i);
                results.add(new Movie(movie.getString(TITLE),movie.getString(POSTER_PATH),movie.getString(OVERVIEW),
                        (float) movie.getDouble(VOTE_AVERAGE),(float)movie.getDouble(POPULARITY),movie.getString(RELEASE_DATE)));
            }
            return results;

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            adapter.clear();
            adapter.addAll(movies);
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
