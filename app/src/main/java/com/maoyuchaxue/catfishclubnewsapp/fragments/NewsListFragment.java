package com.maoyuchaxue.catfishclubnewsapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.activities.MainActivity;
import com.maoyuchaxue.catfishclubnewsapp.activities.NewsViewActivity;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_KEYWORD = "keyword";
    private static final String ARG_IS_LOCAL = "is_local";

    // TODO: Rename and change types of parameters
    private int category;
    private String keyword;
    private boolean isLocal;

    private OnFragmentInteractionListener mListener;

    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category Parameter 1.
     * @param keyword Parameter 2.
     * @return A new instance of fragment NewsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsListFragment newInstance(int category, String keyword, boolean isLocal) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, category);
        args.putString(ARG_KEYWORD, keyword);
        args.putBoolean(ARG_IS_LOCAL, isLocal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt(ARG_CATEGORY);
            keyword = getArguments().getString(ARG_KEYWORD);
            isLocal = getArguments().getBoolean(ARG_IS_LOCAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout curView = (LinearLayout) inflater.inflate(R.layout.fragment_news_list, container, false);
        View subView = inflater.inflate(R.layout.news_into_unit_layout, curView, false);

        TextView title = (TextView) subView.findViewById(R.id.news_unit_title);
        title.setText("假装有标题");
        TextView intro = (TextView) subView.findViewById(R.id.news_unit_intro);
        intro.setText("假装有新闻简介假装有新闻简介假装有新闻简介假装有新闻简介假装有新闻简介假装有新闻简介");
        TextView id = (TextView) subView.findViewById(R.id.news_unit_id);
        id.setText("testid");
        curView.addView(subView);

        subView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    TextView tview = (TextView) view.findViewById(R.id.news_unit_id);
                    mListener.onFragmentInteraction(tview.getText().toString());
                }
            }
        });
        return curView;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String newsID);
    }
}
