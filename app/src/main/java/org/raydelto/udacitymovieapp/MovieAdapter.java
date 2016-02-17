package org.raydelto.udacitymovieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.raydelto.udacitymovieapp.entities.Movie;

import java.util.List;

/**
 * Created by raydelto on 2/16/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie>{
    private List<Movie> movies;
    private static MovieAdapter instance;
    private Context context;
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public static synchronized MovieAdapter getInstance(){
        return instance;
    }

    public static synchronized MovieAdapter getInstance(Context context, int resource, List<Movie> movies){
        if(instance == null){
            instance = new MovieAdapter(context,resource,movies);
        }
        return instance;
    }



    private MovieAdapter(Context context, int resource, List<Movie> movies) {
        super(context, resource, movies);
        this.movies = movies;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.movie_item,null);
        }else{
            cell = (View) convertView;
        }

        TextView txtTitle = (TextView) cell.findViewById(R.id.txtTitle);
        ImageView imageView = (ImageView) cell.findViewById(R.id.imgMovie);
        Movie movie = movies.get(position);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+movie.getPosterPath()).into(imageView);
        Log.v(LOG_TAG, movie.getPosterPath());
        txtTitle.setText(movie.getTitle());

        return cell;
    }
}
