package com.linda.gourmetdiary

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.appworks.school.stylish.ext.getVmFactory
import com.crashlytics.android.Crashlytics
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.linda.gourmetdiary.data.User
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import com.linda.gourmetdiary.util.UserManager

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        facebook_login_button.setOnClickListener {
            facebookLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.login_token_id))
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //printHashKey()
        callbackManager = CallbackManager.Factory.create()

    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                }

            })
    }

    fun handleFacebookAccessToken(token: AccessToken?) {
        var credential = token?.token?.let { FacebookAuthProvider.getCredential(it) }

        if (credential != null) {
            auth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        moveMainPage(task.result?.user)
    //                    Log.i(
    //                        "fb", "success result = ${task.result?.user}"
    //                    )
                        var user = User(
                            userId = task.result?.user?.uid,
                            userPhoto = task.result?.user?.photoUrl.toString(),
                            userName = task.result?.user?.displayName,
                            userEmail = task.result?.user?.email
                        )
                        UserManager.userId = user.userId
                        UserManager.userData = user
                        viewModel.pushProfile(user)
                        moveMainPage(task.result?.user)

                    } else {
                        //Show the error message
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {

            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result != null) {
                if (result.isSuccess) {
                    var account = result.signInAccount
                    var user = User(
                        userId = account?.id,
                        userPhoto = account?.photoUrl.toString(),
                        userName = account?.displayName,
                        userEmail = account?.email
                    )
                    UserManager.userId = user.userId
                    UserManager.userData = user
                    viewModel.pushProfile(user)
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Login
                    var user = User(
                        userId = account?.id,
                        userPhoto = account?.photoUrl.toString(),
                        userName = account?.displayName,
                        userEmail = account?.email
                    )
                    UserManager.userId = user.userId
                    UserManager.userData= user
                    viewModel.pushProfile(user)
                    moveMainPage(task.result?.user)
                } else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}