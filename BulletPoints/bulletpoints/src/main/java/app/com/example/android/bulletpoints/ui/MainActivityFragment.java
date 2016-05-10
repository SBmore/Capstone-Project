package app.com.example.android.bulletpoints.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.com.example.android.bulletpoints.R;
import app.com.example.android.bulletpoints.data.ArticleAdaptor;
import app.com.example.android.bulletpoints.data.ArticleContract;
import app.com.example.android.bulletpoints.data.ArticleDataFetcher;
import app.com.example.android.bulletpoints.data.ArticleProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String TAG = MainActivityFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArticleAdaptor mArticleAdaptor;
    private static Bundle mListState;

    private static final String LAYOUT_STATE_KEY = "layout_state_key";
    private static final String[] ARTICLE_COLUMNS = {
            ArticleContract.ArticleColumns._ID,
            ArticleContract.ArticleColumns.TITLE,
            ArticleContract.ArticleColumns.DESCRIPTION,
            ArticleContract.ArticleColumns.LINK,
            ArticleContract.ArticleColumns.PUB_DATE,
            ArticleContract.ArticleColumns.IMG_URL
    };

    // These are the indices for the columns in ArticleContract
    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_TITLE = 1;
    public static final int COL_ARTICLE_DESCRIPTION = 2;
    public static final int COL_ARTICLE_LINK = 3;
    public static final int COL_ARTICLE_PUB_DATE = 4;
    public static final int COL_ARTICLE_IMG_URL = 5;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        // Banner Ad
        AdView mAdView = (AdView) root.findViewById(R.id.listAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null) {
            fetchData();
        }

        getLoaderManager().initLoader(0, null, this);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        mListState = new Bundle();
        if (mRecyclerView.getLayoutManager() != null) {
            Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
            mListState.putParcelable(LAYOUT_STATE_KEY, listState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            Parcelable listState = mListState.getParcelable(LAYOUT_STATE_KEY);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    private void refresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        fetchData();
        mArticleAdaptor.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ArticleContract.ArticleColumns.PUB_DATE + " DESC";

        return new CursorLoader(getActivity(),
                ArticleProvider.Articles.CONTENT_URI,
                ARTICLE_COLUMNS,
                ArticleContract.ArticleColumns.BODY + " IS NOT NULL",
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        FragmentActivity activity = getActivity();
        mArticleAdaptor = new ArticleAdaptor(activity, data);
        mArticleAdaptor.setHasStableIds(true);
        mRecyclerView.setAdapter(mArticleAdaptor);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    public void fetchData() {
        ArticleDataFetcher articleDataFetcher = new ArticleDataFetcher(getContext());
        articleDataFetcher.execute(getString(R.string.rss_sky_news_world));
    }
}
