package com.nsh.covid19.hospital.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nsh.covid19.hospital.R
import okhttp3.*
import java.io.IOException
import java.util.*


class IntroActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val callRef = FirebaseDatabase.getInstance().getReference("server")
        val callListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val preference: SharedPreferences = getSharedPreferences("covid", 0)
                    val editor: SharedPreferences.Editor = preference.edit()
                    editor.putString("server", snapshot.getValue(String::class.java))
                    editor.commit()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        callRef.addValueEventListener(callListener)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val preference: SharedPreferences = getSharedPreferences("covid", 0)
            if (preference.getInt("type", 1) == 1)
                startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, DoctorMainActivity::class.java))
            finish()
            return
        }

        val startButton: Button = findViewById(R.id.btn_start)
        val startButton1: Button = findViewById(R.id.btn_start1)

        val preference: SharedPreferences = getSharedPreferences("covid", 0)
        val editor: SharedPreferences.Editor = preference.edit()

        startButton.setOnClickListener {
            editor.putInt("type", 0)
            editor.commit()
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.drawable.wash)
                            .setAvailableProviders(Arrays.asList(
//                                    AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build(),
                                    AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN").build()
                            )).build(),
                    RC_SIGN_IN)
        }

        startButton1.setOnClickListener {
            editor.putInt("type", 1)
            editor.commit()
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.drawable.wash)
                            .setAvailableProviders(Arrays.asList(
//                                    AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build(),
                                    AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN").build()
                            )).build(),
                    RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io"))
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val preference: SharedPreferences = getSharedPreferences("covid", 0)
                if (preference.getInt("type", 1) == 1) {
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    FirebaseAuth.getInstance().currentUser?.uid?.let {
                        FirebaseDatabase.getInstance()
                                .reference
                                .child("patients")
                                .child(it)
                                .child("phone")
                                .setValue(FirebaseAuth.getInstance().currentUser?.phoneNumber)
                    }
                    val client = OkHttpClient().newBuilder().build()
                    val mediaType: MediaType? = MediaType.parse("text/plain")
                    val body = RequestBody.create(mediaType, "")

                    val request: Request = Request.Builder()
                            .url(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io") + "/patient/signup?firebase_id=" + FirebaseAuth.getInstance().currentUser!!.uid)
                            .method("POST", body)
                            .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("error")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                val body = response.body()?.string()
                            }
                        }
                    })
                } else {
                    startActivity(Intent(this@IntroActivity, DoctorMainActivity::class.java))
                    FirebaseAuth.getInstance().currentUser?.uid?.let {
                        FirebaseDatabase.getInstance()
                                .reference
                                .child("doctors")
                                .child(it)
                                .child("phone")
                                .setValue(FirebaseAuth.getInstance().currentUser?.phoneNumber)
                    }
                    val client = OkHttpClient().newBuilder().build()
                    val mediaType: MediaType? = MediaType.parse("text/plain")
                    val body = RequestBody.create(mediaType, "")
                    val request: Request = Request.Builder()
                            .url(getSharedPreferences("covid", 0).getString("server","https://883aad4af71a.ngrok.io") + "/doctor/signup?firebase_id=" + FirebaseAuth.getInstance().currentUser!!.uid)
                            .method("POST", body)
                            .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("error")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                val body = response.body()?.string()
                            }
                        }
                    })
                }
                finish()
                return
            } else {
                if (response == null) {
                    showSnackbar(R.string.sign_in_cancelled)
                    return
                }
            }

            showSnackbar(R.string.unknown_sign_in_response)
        }
    }

    private fun showSnackbar(stringRes: Int) {
        Snackbar.make(findViewById(R.id.root)!!, stringRes, Snackbar.LENGTH_LONG).show()
    }


}
