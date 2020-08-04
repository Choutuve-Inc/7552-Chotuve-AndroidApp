package com.app.chotuve.forgottenpassword

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.login.LoginActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {
    private val TAG: String = "Sign Up Screen"
    private lateinit var userMail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        userMail = intent.getStringExtra("userMail")

        btn_change_password_accept.setOnClickListener {
            Log.d(TAG, "Accept Button Clicked")
            val valid = validatePassword()
            if (valid) {
                changePassword()
            }else{
                toastMessage("")
            }
        }

        btn_change_password_cancel.setOnClickListener{
            Log.d(TAG, "Cancel Button Clicked")
            val intentCancel = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
            intentCancel.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentCancel)
        }
    }

    private fun validatePassword(): Boolean{
        val validStrings = Regex("([A-Za-z0-9\\-\\_]+)")
        val builder = AlertDialog.Builder(this@ChangePasswordActivity)
        val password = txt_change_password_password.text
        val password2 = txt_change_password_confirm_password.text

        if (password.isBlank()){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Password")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (!validStrings.matches(password)){
            builder.setTitle("Invalid Password")
            builder.setMessage("Password con only contain letters, numbers, underscore or dash")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (password.toString() != password2.toString()){
            builder.setTitle("Unmatching Passwords")
            builder.setMessage("Passwords don't match")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        return true
    }

    private fun changePassword(){
        Fuel.post("${ApplicationContext.getServerURL()}/reset")
            .jsonBody(
                "{" +
                        " \"tipo\" : \"mailPass\"," +
                        " \"email\" : \"${userMail}\"," +
                        " \"Npassword\" : \"${txt_change_password_password.text}\"," +
                        " \"token\" : \"${txt_change_password_token.text}\"" +
                "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [changePassword]")
                        val intentToLogin = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                        intentToLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        toastMessage("Password changed!")
                        startActivity(intentToLogin)
                    }
                    is Result.Failure -> {
                        Log.d(TAG, "Error changing Password.")
                        Log.d(TAG, "Error Code: ${response.statusCode}")
                        Log.d(TAG, "Error Message: ${result.error}")
                        Log.d(TAG, "request was: $request")
                        toastMessage("There was an error when setting up the password")
                    }
                }
            }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_LONG).show()
    }
}