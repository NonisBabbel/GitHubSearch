package com.micus.githubsearch

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView

// Keep a reference to the NetworkFragment, which owns the AsyncTask object
// that is used to execute network ops.
private var mNetworkFragment: NetworkFragment? = null

// Boolean telling us whether a download is in progress, so we don't trigger overlapping
// downloads with consecutive button clicks.
private var mDownloading = false


class MainActivity : AppCompatActivity(), DownloadCallback<String> {

    var TAG = "Activity Main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNetworkFragment = NetworkFragment.getInstance(supportFragmentManager, "https://api.github.com/graphql")



    }

    fun clickSearch(view: View) {
        Log.v(TAG, "search clicked")

        startDownload()
    }

    private fun startDownload() {
        if (!mDownloading) {
            // Execute the async download.
            mNetworkFragment?.apply {
                startDownload()
                mDownloading = true
            }
        }
    }

    override fun updateFromDownload(result: String?) {
        // Update your UI here based on result of download.
        Log.v(TAG, "RESULT: "+result)

        // TODO list ist created with example data
        val titles = arrayOf("one", "two", "three")
        val lins = arrayOf("link1", "link2", "link3")

        val repositoryList: MutableList<Repository> = ArrayList()
        repositoryList.add(Repository("Example Project 1", "https://www.google.de", "Java", "example description text blablabla"))
        repositoryList.add(Repository("Example Project 2", "https://www.google.de", "C#", "descriptiondescription description description description description description"))

        var listView = findViewById<ListView>(R.id.lvList)

        val adapter = CustomAdapter(this, repositoryList)
        listView.adapter = adapter
        listView.setOnItemClickListener(adapter)
    }

    override fun getActiveNetworkInfo(): NetworkInfo {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

    override fun onProgressUpdate(progressCode: Int, percentComplete: Int) {
        when (progressCode) {
            // You can add UI behavior for progress updates here.
            ERROR -> {
            }
            CONNECT_SUCCESS -> {
            }
            GET_INPUT_STREAM_SUCCESS -> {
            }
            PROCESS_INPUT_STREAM_IN_PROGRESS -> {
            }
            PROCESS_INPUT_STREAM_SUCCESS -> {
            }
        }
    }

    override fun finishDownloading() {
        mDownloading = false
        mNetworkFragment?.cancelDownload()
    }



}