package com.linda.gourmetdiary

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.appworks.school.stylish.ext.getVmFactory
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.linda.gourmetdiary.data.Profile
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
//        email_login_button.setOnClickListener {
//            signinAndSignup()
//        }
        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        facebook_login_button.setOnClickListener {
            facebookLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("693996800991-6cup7r536d3h29256avpebd9m8j9gcb8.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //printHashKey()
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }

    }

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    //Second step
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })
    }

    fun handleFacebookAccessToken(token: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    moveMainPage(task.result?.user)
                    Log.i(
                        "fb", "success result = ${task.result?.user}"
                    )
                    var user = User(
                        userId = task.result?.user?.uid,
                        userPhoto = task.result?.user?.photoUrl.toString(),
                        userName = task.result?.user?.displayName,
                        userEmail = task.result?.user?.email
                    )
                    UserManager.userId = user.userId
                    UserManager.userData = user
                    viewModel.getProfile(user)
                    moveMainPage(task.result?.user)
                    Log.i("firebaseAuthWithGoogle","firebaseAuthWithGoogle = ${UserManager.userData}")

                } else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
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
                    viewModel.getProfile(user)
                    Log.i("UserManager","UserManager = ${UserManager.userData}")
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
                    viewModel.getProfile(user)
                    moveMainPage(task.result?.user)
                    Log.i("firebaseAuthWithGoogle","firebaseAuthWithGoogle = ${UserManager.userData}")
                } else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    //    fun signinAndSignup(){
//        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
//            ?.addOnCompleteListener {
//                    task ->
//                if(task.isSuccessful){
//                    //Creating a user account
//                    moveMainPage(task.result?.user)
//                }else if(task.exception?.message.isNullOrEmpty()){
//                    //Show the error message
//                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
//                }else{
//                    //Login if you have account
//                    signinEmail()
//                }
//            }
//    }
//    fun signinEmail(){
//        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
//            ?.addOnCompleteListener {
//                    task ->
//                if(task.isSuccessful){
//                    //Login
//                    moveMainPage(task.result?.user)
//                }else{
//                    //Show the error message
//                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
//                }
//            }
//    }
    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}