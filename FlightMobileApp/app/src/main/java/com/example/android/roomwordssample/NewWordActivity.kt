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
/*
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.view.View
import com.google.gson.GsonBuilder
import com.zerokol.views.joystickView.JoystickView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
*/
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.widget.SeekBar
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import com.zerokol.views.joystickView.JoystickView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.concurrent.TimeUnit

//import kotlinx.android.synthetic.main.activity_main.*

/**
 * Activity for entering a word.
 */


class NewWordActivity : Activity() {
    private var angleTextView: TextView? = null
        private var powerTextView: TextView? = null
        private var directionTextView: TextView? = null
        private var url : String? = null
        // Importing also other views
        var joystick: JoystickView? = null
        var imageIsOn: Boolean = true;
        var aileronCommand: Double? = null;
        var elevatorCommand: Double? = null;
        var thrtttleCommand: Double? = null;



    override fun onStart() {
        super.onStart()
        screenShot();
    }

    override fun onResume() {
        super.onResume();
        // screenShot();
    }

    override fun onStop() {
        super.onStop()
        imageIsOn = false;
    }

    override fun onPause() {
        super.onPause()
        imageIsOn = false;
    }
    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        //screenShot();

        // joystick
        var intent = intent;
        this.url = intent.getStringExtra("URL")

        joystick = findViewById<View>(R.id.joystickView) as JoystickView
        angleTextView = findViewById<View>(R.id.angleTextView) as TextView
        powerTextView = findViewById<View>(R.id.powerTextView) as TextView
        directionTextView = findViewById<View>(R.id.directionTextView) as TextView
        //Referencing also other views
        // setJoystic()
        lateinit var seekBarThrottle: SeekBar;
        lateinit var valueThrottle: TextView;
        lateinit var seekBarRadder: SeekBar;
        lateinit var valueRadder: TextView;


        seekBarThrottle = findViewById<SeekBar>(R.id.seekBarThrottle);
        valueThrottle = findViewById<TextView>(R.id.contentThrottle);
        seekBarThrottle.max = 100;

        seekBarRadder = findViewById<SeekBar>(R.id.seekBarRadder);
        valueRadder = findViewById<TextView>(R.id.radderContent);
        seekBarRadder.max = 100;

        var elevatorInfo = findViewById<View>(R.id.elevatorInfo) as TextView
        var aileron = findViewById<View>(R.id.aileronInformation) as TextView

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick!!.setOnJoystickMoveListener({ angle, power, direction ->
            angleTextView!!.text = " $angle°"
            powerTextView!!.text = " $power%"

            aileronCommand = cos(angle.toDouble() * (power.toDouble() / 100));
            elevatorCommand = sin(angle.toDouble() * (power.toDouble() / 100));
            Log.e("test 1 aileronCommand", aileronCommand.toString());
            Log.e("test 1 elevatorCommand", elevatorCommand.toString());

            elevatorInfo.setText(elevatorCommand.toString());
            aileron.setText(aileronCommand.toString());


            var command = Command(
                    aileron = aileronCommand,
                    throttle = seekBarThrottle.progress.toString().toDouble(),
                    rudder =seekBarRadder.progress.toString().toDouble(),
                    elevator = elevatorCommand
            );


            val gson = GsonBuilder()
                    .setLenient()
                    .create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            val api = retrofit.create(Api::class.java)


            val body = api.post(command).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.e("yes", "working!:)");
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("error", t.message.toString());
                }
            })
            when (direction) {
                JoystickView.FRONT -> directionTextView!!.setText(R.string.front_lab)
                JoystickView.FRONT_RIGHT -> directionTextView!!.setText(R.string.front_right_lab)
                JoystickView.RIGHT -> directionTextView!!.setText(R.string.right_lab)
                JoystickView.RIGHT_BOTTOM -> directionTextView!!.setText(R.string.right_bottom_lab)
                JoystickView.BOTTOM -> directionTextView!!.setText(R.string.bottom_lab)
                JoystickView.BOTTOM_LEFT -> directionTextView!!.setText(R.string.bottom_left_lab)
                JoystickView.LEFT -> directionTextView!!.setText(R.string.left_lab)
                JoystickView.LEFT_FRONT -> directionTextView!!.setText(R.string.left_front_lab)

//                elevatorInfo.setText(kotlin.math.cos(angle.toDouble())*power);
//                aileron.setText(kotlin.math.sin(angle.toDouble())*power);
                else -> directionTextView!!.setText(R.string.center_lab)
            }

        }, JoystickView.DEFAULT_LOOP_INTERVAL)
        seekBarThrottle.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                valueThrottle.text = "seeking to: " + progress.toString();
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                valueThrottle.text = "started at: " + seekBarThrottle.progress.toString();
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                valueThrottle.text = "selected " + seekBarThrottle.progress.toString();

                var command = Command(
                        aileron = aileronCommand,
                        throttle = seekBarThrottle.progress.toString().toDouble(),
                        rudder = seekBarRadder.progress.toString().toDouble(),
                       // throttle = valueThrottle.text.toString().toDouble(),
                       // rudder = valueRadder.text.toString().toDouble(),
                        elevator = elevatorCommand
                );

                val gson = GsonBuilder()
                        .setLenient()
                        .create()
                val retrofit = Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                val api = retrofit.create(Api::class.java)


                val body = api.post(command).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("yes", "working!:)");
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("error", t.message.toString());
                    }
                })
            }
        })

        seekBarRadder.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                valueRadder.text = "seeking to: " + progress.toString();
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                valueRadder.text = "started at: " + seekBarRadder.progress.toString();
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                valueRadder.text = "selected " + seekBarRadder.progress.toString();

                var command = Command(
                        aileron = aileronCommand,
                        throttle = seekBarThrottle.progress.toString().toDouble(),
                        rudder = seekBarRadder.progress.toString().toDouble(),
                        elevator = elevatorCommand
                );

                val gson = GsonBuilder()
                        .setLenient()
                        .create()
                val retrofit = Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                val api = retrofit.create(Api::class.java)

                val body = api.post(command).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("yes", "working!:)");
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("error", t.message.toString());
                    }
                })
            }
        });
    }



    private fun screenShot() {
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        val api = retrofit.create(Api::class.java)
        imageIsOn = true;
        Log.e("1", "1");
        CoroutineScope(IO).launch {
            while (imageIsOn) {
                getImage();
                Log.e("info", "get image invoked");
                delay(1000);
            }
        }
    }


    private fun getImage() {

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
       // "http://10.0.2.2:63010"
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {

            override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
            ) {
                val I = response?.body()?.byteStream()
                val B = BitmapFactory.decodeStream(I)
                runOnUiThread {
                    var imageFromServer = findViewById<View>(R.id.imageFromServer) as ImageView

                                imageFromServer.setImageBitmap(B)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }
}