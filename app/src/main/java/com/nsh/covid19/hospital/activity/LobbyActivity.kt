package com.nsh.covid19.hospital.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.nsh.covid19.hospital.FirebaseData
import com.nsh.covid19.hospital.FirebaseData.myID
import com.nsh.covid19.hospital.R
import com.nsh.covid19.hospital.adapter.UploadAdapter
import com.nsh.covid19.hospital.model.Upload
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class LobbyActivity : AppCompatActivity() {

    private lateinit var callRef: DatabaseReference
    lateinit var prescription: EditText
    var patientAdapter: UploadAdapter? = null
    val list: MutableList<Upload> = mutableListOf()

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        init()

        val name: TextView = findViewById(R.id.option)
        val phone: TextView = findViewById(R.id.option1)
        val message: TextView = findViewById(R.id.option2)
        prescription = findViewById(R.id.message)
        val upload: TextView = findViewById(R.id.option3)

        name.text = intent.getStringExtra("patient_name")
        phone.text = intent.getStringExtra("phone")
        message.text = intent.getStringExtra("message")

        val sharedPreferences = getSharedPreferences("covid", 0)
        val type = sharedPreferences.getInt("type", 1)

        if (type == 0) {
            upload.visibility = View.GONE
        } else {
            prescription.visibility = View.GONE
            findViewById<View>(R.id.call).visibility = View.GONE
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        patientAdapter = UploadAdapter(list, this, type)
        recyclerView.adapter = patientAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        getData()

        println(intent.getStringExtra("patient_id"))
        findViewById<View>(R.id.call).setOnClickListener { v: View? -> startVideoCall(intent.getStringExtra("patient_id")) }

        findViewById<View>(R.id.end).setOnClickListener { v: View? ->
            if (sharedPreferences.getInt("type", 1) == 0) {
                startActivity(Intent(this, DoctorMainActivity::class.java))
                savePres();
            } else startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        upload.setOnClickListener { openGalleryForImage() }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 10111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 10111) {
            val iStream: InputStream = contentResolver.openInputStream(data?.data)
            val inputData: ByteArray? = getBytes(iStream)
            val requestId: String = MediaManager.get().upload(inputData).callback(object : UploadCallback {

                override fun onStart(requestId: String?) {
                    findViewById<ProgressBar>(R.id.pb).visibility = VISIBLE
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    val progress = bytes.toDouble() / totalBytes
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    if (resultData != null) {
                        println(resultData.get("url"))
                        val client = OkHttpClient().newBuilder().build()
                        val mediaType: MediaType? = MediaType.parse("text/plain")
                        val body = RequestBody.create(mediaType, "")
                        val request: Request = Request.Builder()
                                .url(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io") + "/patient/upload-report?image_url=" + resultData.get("url") + "&report_details=details")
                                .method("POST", body)
                                .addHeader("Authorization", "patient " + FirebaseAuth.getInstance().currentUser!!.uid)
                                .build()
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                println("error")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                if (response.isSuccessful) {
                                    runOnUiThread {
                                        getData()
                                        findViewById<ProgressBar>(R.id.pb).visibility = GONE
                                    }
                                }
                            }
                        })
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }
            }).dispatch()
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also({ len = it }) != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    fun savePres() {
        val client = OkHttpClient().newBuilder().build()
        val mediaType: MediaType? = MediaType.parse("text/plain")
        val body = RequestBody.create(mediaType, "")
        val request: Request = Request.Builder()
                .url(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io") + "/doctor/upload-prescription?appointment_id=" + intent.getStringExtra("id") + "&prescription_details=" + (prescription?.text
                        ?: "No Comments"))
                .method("POST", body)
                .addHeader("Authorization", "doctor " + FirebaseAuth.getInstance().currentUser!!.uid)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                    }
                }
            }
        })
    }


    fun getData() {
        list.clear();
        val client = OkHttpClient().newBuilder().build()
        val mediaType: MediaType? = MediaType.parse("text/plain")
        val body = RequestBody.create(mediaType, "")
        val request: Request = Request.Builder()
                .url(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io") + "/patient/get-reports")
                .method("GET", null)
                .addHeader("Authorization", "patient " + intent.getStringExtra("patient_id"))
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    val json = JSONObject(body)
                    runOnUiThread {
                        val json1 = json.getJSONArray("reports")
                        for (i in 0..json1.length() - 1) {
                            println(json1.getJSONObject(i).getString("image_url"))
                            list.add(Upload(json1.getJSONObject(i).getString("image_url")))
                        }
                        patientAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        FirebaseData.init()
        callRef = FirebaseData.database.getReference("calls/$myID/id")
    }


    override fun onResume() {
        super.onResume()
        callRef.addValueEventListener(callListener)
        val usersRef = FirebaseData.database.getReference("users")
        usersRef.addValueEventListener(usersListener)
    }

    override fun onPause() {
        super.onPause()
        callRef.removeEventListener(callListener)
        val usersRef = FirebaseData.database.getReference("users")
        usersRef.addValueEventListener(usersListener)
    }

    private fun startVideoCall(key: String) {
        FirebaseData.getCallStatusReference(myID).setValue(true)
        FirebaseData.getCallIdReference(key).onDisconnect().removeValue()
        FirebaseData.getCallIdReference(key).setValue(myID)
        VideoCallActivity.startCall(this@LobbyActivity, key)
    }

    private fun receiveVideoCall(key: String) {
        VideoCallActivity.receiveCall(this, key)
    }


    private val callListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                receiveVideoCall(dataSnapshot.getValue(String::class.java)!!)
                callRef.removeValue()
            }
        }
    }

    private val usersListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (!dataSnapshot.exists()) {
                return
            }
        }

    }

}
