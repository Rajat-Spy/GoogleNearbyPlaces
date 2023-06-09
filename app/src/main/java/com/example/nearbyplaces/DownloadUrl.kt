package com.example.nearbyplaces

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownloadUrl {
    fun readTheUrl(placeurl: String): String {
        var data = ""
        var inputSream: InputStream? = null
        var httpUrlConnection: HttpURLConnection? = null
        val url = URL(placeurl)
        httpUrlConnection = url.openConnection() as HttpURLConnection?
        httpUrlConnection?.connect()
        if (httpUrlConnection != null) {
            inputSream = httpUrlConnection.inputStream
        }
        val bufferedReader = BufferedReader(InputStreamReader(inputSream))
        lateinit var stringBuffer: StringBuffer
        var line = ""
        while ((line== bufferedReader.readLine()) != null){
            stringBuffer.append(line)
        }
        data = stringBuffer.toString()
        bufferedReader.close()

        return data
    }
}