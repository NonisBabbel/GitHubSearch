package com.micus.githubsearch

import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

/**
 * Implementation of AsyncTask designed to fetch data from the network.
 */
internal class DownloadTask(callback: DownloadCallback<String>)
    : AsyncTask<String, Int, DownloadTask.Result>() {

    private var TAG = "DownloadTask"

    private var mCallback: DownloadCallback<String>? = null

    init {
        setCallback(callback)
    }

    internal fun setCallback(callback: DownloadCallback<String>) {
        mCallback = callback
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    internal class Result {
        var mResultValue: String? = null
        var mException: Exception? = null

        constructor(resultValue: String) {
            mResultValue = resultValue
        }

        constructor(exception: Exception) {
            mException = exception
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    override fun onPreExecute() {
        if (mCallback != null) {
            val networkInfo = mCallback?.getActiveNetworkInfo()
            if (networkInfo?.isConnected == false
                || networkInfo?.type != ConnectivityManager.TYPE_WIFI
                && networkInfo?.type != ConnectivityManager.TYPE_MOBILE
            ) {
                // If no connectivity, cancel task and update Callback with null data.
                mCallback?.updateFromDownload(null)
                cancel(true)
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    override fun doInBackground(vararg args: String): DownloadTask.Result? {
        var result: Result? = null
        if (!isCancelled && args.isNotEmpty()) {

            // TODO example request
            var request = Query.create(args[1], args[2], args[3], args[4])

            result = sendPostRequest(args[0], request)

        }
        return result
    }

    fun sendPostRequest(url: String, request: String): DownloadTask.Result? {
        var result: Result? = null

        val mURL = URL(url)

        try {
            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "POST"

                //readTimeout = 2000
                //connectTimeout = 2000

                setRequestProperty("Authorization", "bearer  c76d37a0f98e738456d9"+"9ab5f424e9a331d2b910")

                val wr = OutputStreamWriter(getOutputStream())

                Log.v(TAG, request.toString())

                wr.write(request)

                wr.flush();

                println("URL : $url")
                println("Response Code : $responseCode")

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    println("Response : $response")
                    result = Result(response.toString())
                }
            }
        }catch (e: Exception) {
            result = Result(e)
        }
        return result
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    override fun onPostExecute(result: Result?) {
        mCallback?.apply {
            result?.mException?.also { exception ->
                updateFromDownload(exception.message)
                return
            }
            result?.mResultValue?.also { resultValue ->
                updateFromDownload(resultValue)
                return
            }
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    override fun onCancelled(result: Result) {}


}