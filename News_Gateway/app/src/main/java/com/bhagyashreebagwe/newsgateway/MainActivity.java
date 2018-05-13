package com.bhagyashreebagwe.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean serviceRunning = false;
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ARTICLE_LIST = "ARTICLE_LIST";
    static final String SOURCE_ID = "SOURCE_ID";
    private ArrayList<String> srcList = new ArrayList <String>();
    private ArrayList<String> catList = new ArrayList <String>();
    private ArrayList<Source> sourceArrayList = new ArrayList <Source>();
    private ArrayList<Article> articleArrayList = new ArrayList <Article>();
    private HashMap<String, Source> sourceDataMap = new HashMap<>();
    private Menu opt_menu;
    private NewsReceiver newsReceiver;
    private String currentNewsSource;
    private ArrayAdapter adapter;
    private MyPageAdapter pageAdapter;
    private List <Fragment> fragments;
    private ViewPager pager;
    private boolean stateFlag;
    private int currentSourcePointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if(!serviceRunning &&  savedInstanceState == null) {
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            startService(intent);
            serviceRunning = true;
        }

        newsReceiver = new NewsReceiver();
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectItem(position);
                    }
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        adapter = new ArrayAdapter<>(this, R.layout.list_element, srcList);
        mDrawerList.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageAdapter);

        if (sourceDataMap.isEmpty() && savedInstanceState == null )
            new NewsSourceDownloader(this, "").execute();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        new NewsSourceDownloader(this, item.getTitle().toString()).execute();
        mDrawerLayout.openDrawer(mDrawerList);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void selectItem(int position) {
        currentNewsSource = srcList.get(position);
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, currentNewsSource);
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_menu, menu);
        opt_menu=menu;
        if(stateFlag){
            opt_menu.add("All");
            for (String s : catList)
                opt_menu.add(s);
        }
        return true;
    }

    public void setSources(ArrayList<Source> sourceList, ArrayList<String> categoryList)
    {
        sourceDataMap.clear();
        srcList.clear();
        sourceArrayList.clear();

        //catList.addAll(categoryList);
        sourceArrayList.addAll(sourceList);

        for(int i=0;i<sourceList.size();i++){
            srcList.add(sourceList.get(i).getSourceName());
            sourceDataMap.put(sourceList.get(i).getSourceName(), (Source)sourceList.get(i));
        }

        if(!opt_menu.hasVisibleItems()) {
            catList.clear();
            catList =categoryList;
            opt_menu.add("All");
            Collections.sort(categoryList);
            for (String s : categoryList)
                opt_menu.add(s);
        }

        adapter.notifyDataSetChanged();

    }


    private void reDoFragments(ArrayList<Article> articles) {

        setTitle(currentNewsSource);
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articles.size(); i++) {
            Article a = articles.get(i);

            fragments.add(ArticleFragment.newInstance(articles.get(i), i, articles.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
        articleArrayList = articles;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }




        @Override
    protected void onSaveInstanceState(Bundle outState) {
        LayoutRestore layoutRestore = new LayoutRestore();
        layoutRestore.setCategories(catList);
        layoutRestore.setSourceList(sourceArrayList);
        layoutRestore.setCurrentArticle(pager.getCurrentItem());
        layoutRestore.setCurrentSource(currentSourcePointer);
        layoutRestore.setArticleList(articleArrayList);
        outState.putSerializable("state", layoutRestore);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LayoutRestore layoutRestore1 = (LayoutRestore)savedInstanceState.getSerializable("state");
        stateFlag = true;
        articleArrayList = layoutRestore1.getArticleList();
        catList = layoutRestore1.getCategories();
        sourceArrayList = layoutRestore1.getSourceList();
        for(int i=0;i<sourceArrayList.size();i++){
            srcList.add(sourceArrayList.get(i).getSourceName());
            sourceDataMap.put(sourceArrayList.get(i).getSourceName(), (Source)sourceArrayList.get(i));
        }
        mDrawerList.clearChoices();
        adapter.notifyDataSetChanged();
        mDrawerList.setOnItemClickListener(

                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectItem(position);

                    }
                }
        );
        setTitle("News Gateway");

        //pager.setBackgroundResource(0);
        //reDoFragments(articleArrayList);
        //pager.setCurrentItem(layoutRestore1.getCurrentArticle());
    }

    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    ArrayList<Article> artList;
                    if (intent.hasExtra(ARTICLE_LIST)) {
                        artList = (ArrayList <Article>) intent.getSerializableExtra(ARTICLE_LIST);
                        reDoFragments(artList);
                    }
                    break;
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }


    }
}

