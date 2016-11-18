package com.example.fonda.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.fonda.nytimessearch.R;
import com.example.fonda.nytimessearch.adapters.ArticleArrayAdapter;
import com.example.fonda.nytimessearch.models.Article;
import com.example.fonda.nytimessearch.models.Filters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;

    EditText etQuery;
    Button btnSearch;
    GridView gvResults;

    ArrayList<Article> articles; // model
    ArticleArrayAdapter adapter; // controller
    Filters filters; // model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Find the Toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        initialize();
    }

    public void initialize() {
        // setup views
        // etQuery = (EditText) findViewById(R.id.etQuery);
        // btnSearch = (Button) findViewById(R.id.btnSearch);
        gvResults = (GridView) findViewById(R.id.gvResults);
        // setup model
        articles = new ArrayList<>();
        // setup controller
        adapter = new ArticleArrayAdapter(this, articles);
        filters = new Filters("", "", null);
        // link adapter to results
        gvResults.setAdapter(adapter);
        // attach click listener to individual result (Article)
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(i);
                // pass in the article to intent
                //intent.putExtra("url", article.getWebUrl());
                intent.putExtra("article", Parcels.wrap(article));
                // launch the activity
                startActivity(intent);
            }
        });
    }

    public void fetchArticles(String query) {
        // String query = etQuery.getText().toString();

        // Toast.makeText(this, "Search for " + query, Toast.LENGTH_LONG).show();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        // String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=health&begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=227c750bb7714fc39ef1559ef1bd8329";

        RequestParams params = new RequestParams();
        params.put("api-key", "f5474869169d4339815ad7fb983e105c");
        params.put("page", 0);
        params.put("q", query);
        if (filters != null) {
            params.put("begin_date", filters.getDate());
            params.put("sort", filters.getSortOrder().toLowerCase());
            //params.put("fq", ""); //TODO
        }

        // TODO : pass in different page numbers for endless scrolling

        client.get(url, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articles.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("DEBUG", responseString.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    // Inflate the menu icons on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the toolbar
        getMenuInflater().inflate(R.menu.menu_search_list, menu);

        // Find the search menu item from the layout
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform query
                fetchArticles(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void showFilterDialog() {
        Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
        // Pass the filters to the intent
        intent.putExtra("filters", Parcels.wrap(filters));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // case R.id.action_search:
                // return true;
            case R.id.action_filter:
                showFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            filters = (Filters) Parcels.unwrap(data.getParcelableExtra("filters"));
            Toast.makeText(this, "returned from dialog " + filters.getDate(), Toast.LENGTH_SHORT).show();
        }
    }
}
