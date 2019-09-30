package com.company.arunas.githubapi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.arunas.githubapi.R;
import com.company.arunas.githubapi.model.GitHubUser;
import com.company.arunas.githubapi.rest.APIClient;
import com.company.arunas.githubapi.rest.GitHubUserEndPoints;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserActivity extends AppCompatActivity {

    ImageView avatarImg;
    TextView userNameTV;
    TextView followersTV;
    TextView followingTV;
    TextView logIn;
    TextView email;

    Bundle extras;
    String newString;

    Bitmap myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        avatarImg = findViewById(R.id.avatar);
        userNameTV = findViewById(R.id.username);
        followersTV = findViewById(R.id.followers);
        followingTV = findViewById(R.id.following);
        logIn = findViewById(R.id.logIn);
        email = findViewById(R.id.email);


        extras = getIntent().getExtras();
        newString = extras.getString("STRING_I_NEED");
        System.out.println(newString);

        loadData();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
            return null;
        }
    }

    public void loadData() {
        final GitHubUserEndPoints apiService = APIClient.getClient().create(GitHubUserEndPoints.class);
        Call<GitHubUser>  call = apiService.getUser(newString);

        call.enqueue(new Callback<GitHubUser>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {

                ImageDownloader task = new ImageDownloader();

                try {
                    myImage = task.execute(response.body().getAvatar()).get();

                } catch (Exception e) {

                    e.printStackTrace();

                }

                avatarImg.setImageBitmap(myImage);
                avatarImg.getLayoutParams().height=220;
                avatarImg.getLayoutParams().width=220;

                if(response.body().getName() == null) {
                    userNameTV.setText("Username: No value provided");

                } else {
                    userNameTV.setText("Username " + response.body().getName());
                }

                if(response.body().getEmail() == null) {
                    email.setText("Email: No value provided");

                } else {
                    email.setText("Email: " + response.body().getEmail());
                }


                followersTV.setText("Followers: " + response.body().getFollowers());
                followingTV.setText("Following: " + response.body().getFollowing());
                logIn.setText("Login: " + response.body().getLogin());


            }

            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {
                System.out.println("Failed!" + t.toString());
            }
        });

    }


}

