package com.example.gmailloginwithfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gmailloginwithfirebase.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var login: Button
    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    lateinit var binding: ActivityMainBinding
    private val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result here
            val data: Intent? = result.data
            // Process the data as needed
            Log.e("CheckLog1", "handleResult: ${result.resultCode}  + $data")
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleResult(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        FirebaseApp.initializeApp(this)

        // Get FCM token (optional)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    // Use the token as needed
                    Log.e("Adarsh33", "onCreate: $token")
                }
            }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.googleloginImg!!.setOnClickListener{
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            Loginwithgoogle()
        }

        binding.googlesignoutImg.setOnClickListener {
            mGoogleSignInClient.signOut()
            Toast.makeText(this, "Sign Out Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                Toast.makeText(this, "Login Success ${account.email}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("CheckLog", "handleResult: ${e.status} + ${e.statusCode} + ${e.toString()}")
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun Loginwithgoogle(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        /*startActivityForResult(signInIntent, Req_Code)*/
        activityResult.launch(signInIntent)
    }
}