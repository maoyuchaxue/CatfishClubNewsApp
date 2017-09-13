package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.donkingliang.labels.LabelsView;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.rss.RSSManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CategoryEditActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private LabelsView labelsView, rssLabelsView;
    private String preferencedUrls, preferencedLabels;
    private Set<String> preferencedSelectedLabels;
    ArrayList<String> labels, rssLabels;

    ArrayList<String> tlabels, turls;

    private void repaintRssLabelsView() {
        preferencedUrls = PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                getString("rss_urls", "http://feeds.bbci.co.uk/news/world/rss.xml");
        preferencedLabels = PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                getString("rss_labels", "BBC World");

        HashSet<String> defaultLabels = new HashSet<>();
        defaultLabels.add("BBC World");
        preferencedSelectedLabels = PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                getStringSet("rss_selected", defaultLabels);

        Log.i("rss", preferencedUrls);
        Log.i("rss", preferencedLabels);
        Log.i("rss", preferencedSelectedLabels.toString());

        tlabels = new ArrayList<>();
        turls = new ArrayList<>();

        String[] labelStrs = preferencedLabels.split("\\|");
        for (String s : labelStrs) {
            if (!s.isEmpty()) {
                tlabels.add(s);
            }
        }

        String[] urlStrs = preferencedUrls.split("\\|");
        for (String s : urlStrs) {
            if (!s.isEmpty()) {
                turls.add(s);
            }
        }

        int[] selected = new int[preferencedSelectedLabels.size()];
        int index = 0;
        for (int i = 0; i < tlabels.size(); i++) {
            String t = tlabels.get(i);
            if (preferencedSelectedLabels.contains(t)) {
                selected[index] = i;
                index++;
            }
        }

        tlabels.add("+");
        rssLabelsView.setLabels(tlabels);
        rssLabelsView.setSelects(selected);

        try {
            Log.i("rss", "entries::");
            Set<Map.Entry<String, URL>> entries = RSSManager.getInstance(CacheDBOpenHelper.
                    getInstance(getApplicationContext())).getRSSFeeds();
            Object [] objs = entries.toArray();
            for (Object obj : objs) {
                Map.Entry<String, URL> entry = (Map.Entry<String, URL>) obj;
                Log.i("rss", entry.getKey() + " " + entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        this.preferences = CategoryEditActivity.this.getSharedPreferences("category", 0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.category_toolbar);
        toolbar.setTitle("分类设置");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

        List<Boolean> categoryPreferences = getCategoryPreferences();

        labelsView = (LabelsView) findViewById(R.id.labels_view);
        rssLabelsView = (LabelsView) findViewById(R.id.rss_labels_view);

        labels = new ArrayList<>();
        ArrayList<Integer> selectedLabels = new ArrayList<>();
        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            labels.add(NewsCategoryTag.TITLES[i]);
            if (categoryPreferences.get(i)) {
                selectedLabels.add(i);
            }
        }

        int[] selected = new int[selectedLabels.size()];
        for (int i = 0; i < selectedLabels.size(); i++) {
            selected[i] = selectedLabels.get(i);
        }

        labelsView.setLabels(labels);
        labelsView.setSelects(selected);

        labelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                try {
                    String tag = NewsCategoryTag.TITLES_EN[position];
                    preferences.edit().putBoolean(tag, isSelect).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        repaintRssLabelsView();

        rssLabelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                if (labelText.equals("+") && isSelect) {
                    View dialogView = LayoutInflater.from(CategoryEditActivity.this).
                            inflate(R.layout.rss_source_dialog_layout, null);

                    final EditText feedEditText = (EditText) dialogView.findViewById(R.id.rss_source_url);
                    final EditText labelEditText = (EditText) dialogView.findViewById(R.id.rss_source_label);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoryEditActivity.this);
                    builder.setTitle("添加RSS源");
                    builder.setView(dialogView);
                    builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i("rss", "Adding " + feedEditText.getText().toString() + " " + labelEditText.getText().toString());
                            String url = feedEditText.getText().toString();
                            String label = labelEditText.getText().toString();

                            try {
                                if (label.contains("|") || label.isEmpty() || url.isEmpty()) {
                                    new SweetAlertDialog(CategoryEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("添加错误")
                                            .setContentText("标签和URL不应为空>_<").show();
                                } else {
                                    boolean rssAdded = RSSManager.getInstance(CacheDBOpenHelper.
                                            getInstance(getApplicationContext())).addRSSFeed(label, new URL(url));
                                    if (!rssAdded) {
                                        new SweetAlertDialog(CategoryEditActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("添加错误")
                                                .setContentText("源已被使用，或者格式不对吧orz").show();
                                    } else {

                                        preferencedLabels += "|" + label;
                                        preferencedUrls += "|" + url;
                                        preferencedSelectedLabels.add(label);

                                        PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                                                edit().putString("rss_urls", preferencedUrls).commit();

                                        PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                                                edit().putString("rss_labels", preferencedLabels).commit();

                                        PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                                                edit().putStringSet("rss_selected", preferencedSelectedLabels).commit();

                                    }

                                }

                                repaintRssLabelsView();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.show();

                } else {
                    if (isSelect) {
                        preferencedSelectedLabels.add(labelText);
                        try {
                            RSSManager.getInstance(CacheDBOpenHelper.getInstance(getApplicationContext())).
                                    addRSSFeed(tlabels.get(position), new URL(turls.get(position)));
                            Log.i("rss", "rss selected " + tlabels.get(position));
                        } catch (Exception e) {}
                    } else {
                        preferencedSelectedLabels.remove(labelText);
                        try {
                            RSSManager.getInstance(CacheDBOpenHelper.getInstance(getApplicationContext())).
                                    removeRSSFeed(tlabels.get(position));
                            Log.i("rss", "rss removed " + tlabels.get(position));
                        } catch (Exception e) {}
                    }

                    PreferenceManager.getDefaultSharedPreferences(CategoryEditActivity.this).
                            edit().putStringSet("rss_selected", preferencedSelectedLabels).apply();
                }


                try {
                    Log.i("rss", "entries::");
                    Set<Map.Entry<String, URL>> entries = RSSManager.getInstance(CacheDBOpenHelper.
                            getInstance(getApplicationContext())).getRSSFeeds();
                    Object [] objs = entries.toArray();
                    for (Object obj : objs) {
                        Map.Entry<String, URL> entry = (Map.Entry<String, URL>) obj;
                        Log.i("rss", entry.getKey() + " " + entry.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        CategoryEditActivity.this.finish();
    }


    private List<Boolean> getCategoryPreferences() {
        ArrayList<Boolean> categoryPreferences = new ArrayList<Boolean>();
        SharedPreferences sharedPreferences = getSharedPreferences("category", 0);
        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            boolean appears = sharedPreferences.getBoolean(NewsCategoryTag.TITLES_EN[i], true);
            categoryPreferences.add(appears);
        }
        return categoryPreferences;
    }
}
