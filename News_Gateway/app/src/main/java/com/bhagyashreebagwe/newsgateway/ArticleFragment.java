package com.bhagyashreebagwe.newsgateway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.text.method.ScrollingMovementMethod;


public class ArticleFragment extends Fragment {
    private static final String TAG = "ArticleFragment";

    TextView headline;
    TextView date;
    TextView author;
    TextView content;
    ImageView photo;
    TextView count;
    Article a;
    int count1;
    View v;

    public static final String ARTICLE = "ARTICLE";
    public static final String INDEX = "INDEX";
    public static final String TOTAL = "TOTAL";

    public static final ArticleFragment newInstance(Article article, int index, int total)
    {
        ArticleFragment f = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable(ARTICLE, article);
        bdl.putInt(INDEX, index);
        bdl.putInt(TOTAL, total);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       a  = (Article) getArguments().getSerializable(ARTICLE);
       count1 = getArguments().getInt(INDEX)+1;
       int total = getArguments().getInt(TOTAL);
       String lastLine = count1+" of "+total;


         v = inflater.inflate(R.layout.fragment_article, container, false);
        headline = (TextView)v.findViewById(R.id.headline);
        date = (TextView) v.findViewById(R.id.date);
        author = (TextView) v.findViewById(R.id.author);
        content = (TextView) v.findViewById(R.id.content);
        count = (TextView) v.findViewById(R.id.index);
        photo = (ImageView) v.findViewById(R.id.photo);

        count.setText(lastLine);
        if(a.getArticleTitle() != null){ headline.setText(a.getArticleTitle());
            }
        else{headline.setText("");}

        if(a.getArticlePublishedAt() !=null && !a.getArticlePublishedAt().isEmpty()) {

            String sDate1 = a.getArticlePublishedAt();

            Date date1 = null;
            String pubdate = "";
            try {
                if(sDate1 != null){

                date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(sDate1);}
                String pattern = "MMM dd, yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                pubdate = simpleDateFormat.format(date1);
                date.setText(pubdate);
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }
        if(a.getArticleDescription()!=null) {author.setText(a.getArticleDescription());}
        else{author.setText("");}

        if(a.getArticleAuthor() != null) {content.setText(a.getArticleAuthor());}
        else{content.setText("");}

        author.setMovementMethod(new ScrollingMovementMethod());

        if(a.getArticleUrlToImage()!=null){loadRemoteImage(a.getArticleUrlToImage());}

        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(a.getSrticleUrl()));
                startActivity(intent);
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(a.getSrticleUrl()));
                startActivity(intent);
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(a.getSrticleUrl()));
                startActivity(intent);
            }
        });


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(a.getSrticleUrl()));
                startActivity(intent);
            }
        });

        return v;
    }


    private  void loadRemoteImage(final String imageURL){

        if (imageURL != null) {
            Picasso picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = imageURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photo);
                }
            }).build();
            picasso.load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo);
        } else {
            Picasso.with(getActivity()).load(imageURL)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.missingimage)
                    .into(photo);
        }
    }
}
