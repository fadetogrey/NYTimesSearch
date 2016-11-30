package com.example.fonda.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.fonda.nytimessearch.EndlessScrollListener;
import com.example.fonda.nytimessearch.R;
import com.example.fonda.nytimessearch.adapters.ArticleArrayAdapter;
import com.example.fonda.nytimessearch.fragments.SearchFilterFragment;
import com.example.fonda.nytimessearch.models.Article;
import com.example.fonda.nytimessearch.models.Filters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.example.fonda.nytimessearch.models.Filters.NEWS_TOPICS;

public class SearchActivity extends AppCompatActivity implements SearchFilterFragment.OnSearchFilterActionListener {

    private final int DELAY = 1000;

    @BindView(R.id.gvResults) GridView gvResults;
    @BindView(R.id.toolbar) Toolbar toolbar;

    ArrayList<Article> articles; // model
    ArticleArrayAdapter adapter; // controller
    Filters filters = null; // model
    EndlessScrollListener scrollListener = null;
    String userSubmittedQuery = null;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        initialize();
    }

    private void showFilterDialog() {
        //Intent intent = new Intent(getApplicationContext(), SearchFilterFragment.class);
        // Pass the filters to the intent
        //intent.putExtra("filters", Parcels.wrap(filters));

        if (filters == null) {
            filters = new Filters("", "", new SparseBooleanArray());
        }
        FragmentManager fm = getSupportFragmentManager();
        SearchFilterFragment editNameDialogFragment = SearchFilterFragment.newInstance(/*Parcels.wrap(*/filters/*)*/);
        editNameDialogFragment.show(fm, "");
    }

    public void initialize() {
        // setup model
        articles = new ArrayList<>();
        // setup controller
        adapter = new ArticleArrayAdapter(this, articles);
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
                intent.putExtra("article", Parcels.wrap(article));
                // launch the activity
                startActivity(intent);
            }
        });
        // Attach the listener to the AdapterView onCreate
        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        };
        gvResults.setOnScrollListener(scrollListener);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        fetchArticles(userSubmittedQuery, offset);
    }

    /**
     *   // Send an API request to retrieve appropriate paginated data
     //  --> Send the request including an offset value (i.e `page`) as a query parameter.
     //  --> Deserialize and construct new model objects from the API response
     //  --> Append the new data objects to the existing set of items inside the array of items
     //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
     * @param query Query submitted by user
     * @param page Page/chunk of data to retrieve next
     */
    public void fetchArticles(final String query, final int page) {

        Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AsyncHttpClient client = new AsyncHttpClient();
                String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
                // String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=health&begin_date=20160112&sort=oldest&fq=news_desk:(%22Education%22%20%22Health%22)&api-key=227c750bb7714fc39ef1559ef1bd8329";

                RequestParams params = new RequestParams();
                params.put("api-key", "f5474869169d4339815ad7fb983e105c");
                params.put("page", page);
                params.put("q", query);
                if (filters != null) {
                    String encodedValues;
                    params.put("begin_date", filters.getDate());
                    params.put("sort", filters.getSortOrder().toLowerCase());
                    encodedValues = composeNewsDeskValues();
                    if (!encodedValues.isEmpty()) {
                        params.put("fq", encodedValues);
                    }
                }
                Log.d("DEBUG", "==> params: " + params.toString());

                client.get(url, params, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
        };

        handler.postDelayed(runnable, DELAY);
    }

    private String composeNewsDeskValues() {
        String prefix = "news_desk:(";
        String suffix = ")";
        String retVal = "";
        ArrayList<String> arr = new ArrayList<String>();

        if (filters != null) {
            for (int i = 0; i < 3; i++) {
                if (filters.getNewsDeskValues(i) == true) {
                    arr.add(NEWS_TOPICS[i]);
                }
            }
            if (!arr.isEmpty()) {
                retVal = prefix + TextUtils.join(" ", arr) + suffix;
            }
        }

        return retVal;
    }

    // Inflate the menu icons on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the toolbar
        getMenuInflater().inflate(R.menu.menu_search_list, menu);

        // Find the search menu item from the layout
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Get the view for the search menu item
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Attach a listener to the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                boolean retVal = true;
                if (isNetworkAvailable() && isOnline()) {
                    // New search term was entered
                    // 1. save the search term
                    userSubmittedQuery = query;
                    // 2. reset the pagination and results array
                    page = 0;
                    adapter.clear(); // or articles.clear();
                    adapter.notifyDataSetChanged();
                    scrollListener.resetState();
                    // Perform query
                    fetchArticles(query, page++);
                    // workaround to avoid issues with some emulators and keyboard devices firing twice
                    // if a keyboard enter is used
                    // see https://code.google.com/p/android/issues/detail?id=24599
                    searchView.clearFocus();
                } else {
                    Toast.makeText(getApplicationContext(), "Network connection unavailable, please try again later.", Toast.LENGTH_SHORT).show();
                    retVal = false;
                }
                return retVal;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_filter:
                showFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Check to see if network is available
     * @return
     */
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void onFinishedSearchFilterFragment(Parcelable data) {
        filters = Parcels.unwrap(data);
    }
}
