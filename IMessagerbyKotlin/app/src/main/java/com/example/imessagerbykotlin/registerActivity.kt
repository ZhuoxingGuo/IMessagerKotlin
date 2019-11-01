package com.example.imessagerbykotlin

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class registerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_btn.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            this.startActivity(intent)
        }

        register_btn.setOnClickListener {
            performRegister();
        }

        //选择本地图片文件
        select_img_btn.setOnClickListener {
            Log.d("registerActivity","Try to show photo selector" )
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)

        }

    }


    var selectedPhotoUrl: Uri? =null
    

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK && data!=null){
            Log.d("RegisterActivity","photo was selected")

            selectedPhotoUrl = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUrl)

            val bitmapDrawable  = BitmapDrawable(bitmap)
            select_img_btn.setBackgroundDrawable(bitmapDrawable)

        }
    }

    //执行注册文件
    private fun performRegister() {
        val email = gmail_input2.text.toString()
        val password = password_input.text.toString()

        if(email.isEmpty()  ||  password.isEmpty()) {
            Toast.makeText(this,"please enter text in email/pw ", Toast.LENGTH_SHORT)
            return
        }

        Log.i("Main","email $email password $password")
        //注册用户
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(!it.isSuccessful)return@addOnCompleteListener

            uploadImageToFirebaseStorage()


            //else if successfull
        }.addOnFailureListener {
//            val toast = Toast.makeText(this,"fail $it.message", Toast.LENGTH_SHORT)
//            toast.show()
            Log.i("Main","onFailure  email $email password $password  fail  " + it.message )
        }
    }


    //上传图片到firebase
    private fun uploadImageToFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUrl!!).addOnSuccessListener {
            Log.d("registerActivity", "successfully uploaded image:  ${it.metadata?.path}")
        }

    }
}
