/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.roomwordssample

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.MainActivity.Companion.EXTRA_REPLY
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Compiler.command
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    private lateinit var wordViewModel: WordViewModel
    private lateinit var editWordView: EditText

@RequiresApi(Build.VERSION_CODES.O)
public
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editWordView = findViewById(R.id.edit_word)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get a new or existing ViewModel from the ViewModelProvider.
        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

    val button = findViewById<Button>(R.id.button_save)
    button.setOnClickListener {

        if (TextUtils.isEmpty(editWordView.text)) {
          //  setResult(Activity.RESULT_CANCELED, replyIntent)
        } else {
            val url = editWordView.text.toString()
            onActivityResult2(url)
            if(URLUtil.isValidUrl(url)){
                getImage(url)
            }
            else{
                Toast.makeText(applicationContext,
                        R.string.notvalid,
                        Toast.LENGTH_LONG).show()
            }
        }
    }
    }

     @RequiresApi(Build.VERSION_CODES.O)
     fun onActivityResult2(w: String) {
        //super.onActivityResult(requestCode, resultCode, intentData)
        if (true) {
            let { data ->
                //todo
                //change5
                val url = Url(w,LocalDateTime.now().toString())
                wordViewModel.insert(url)
                Unit
            }
        } else {
            Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.
            let { data ->
            }
        } else {
            Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
            ).show()
        }
    }
    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }

    private fun getImage(url:String) {

        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)

                .readTimeout(10, TimeUnit.SECONDS)

                .build()


        val gson = GsonBuilder()
                .setLenient()
                .create()
        val retrofit = Retrofit.Builder()

                //.baseUrl("http://10.0.2.2:SERVER_PORT/")
                //.baseUrl("http://127.0.0.1:63010")
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    //next activity
                    val intent = Intent(this@MainActivity, NewWordActivity::class.java)
                    intent.putExtra("URL",url)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(
                            applicationContext,
                            R.string.empty_not_saved,
                            Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                        applicationContext,
                        R.string.empty_not_saved,
                        Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
