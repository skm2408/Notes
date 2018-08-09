package com.example.shubhammishra.notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.shubhammishra.notes.Classes.Snaps
import com.example.shubhammishra.notes.Classes.UserInfo
import com.example.shubhammishra.notes.Fragments.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_signup.view.*
import kotlinx.android.synthetic.main.camera_view.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.navbar_profile.*
import kotlinx.android.synthetic.main.navbar_profile.view.*

class MainActivity : AppCompatActivity() {
    var user:UserInfo?=null
    lateinit var fStorage: StorageReference
    lateinit var fDatabase: DatabaseReference
    lateinit var mView: View
    lateinit var imageUri: Uri
    lateinit var signUpImageUri: Uri
    val Gallery_Request = 1
    @SuppressLint("SetTextI18n")
    lateinit var mToggle: ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth
    lateinit var alertView: View
    lateinit var progressDialog: ProgressDialog
    val SignUp_Code = 120
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this@MainActivity, "Can't Process Without your permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun DoVerification() {
        progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.create()
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Completing your Signup")
        val authentication = FirebaseAuth.getInstance().currentUser!!.uid
        val firebaseDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        Log.d("CheckIt", authentication)
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertView = layoutInflater.inflate(R.layout.alert_signup, null)
        alertView.sign_up_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, SignUp_Code)
        }
        alertDialog.setView(alertView)
        alertDialog.setCancelable(false)
        alertView.btnComplete.setOnClickListener {
            val imageView: ImageView = alertView.sign_up_image
            val image = (imageView.drawable as BitmapDrawable).bitmap
            val compare = getDrawable(R.drawable.add_image)
            val comp = (compare as BitmapDrawable).bitmap
            if (!alertView.etUserName.text.isEmpty() && !(image.sameAs(comp))) {
                progressDialog.show()
                val filepath = FirebaseStorage.getInstance().getReference().child(authentication).child(signUpImageUri.lastPathSegment.toString())
                filepath.putFile(signUpImageUri).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                    override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                        var downloadUrl: Uri? = null
                        p0!!.storage.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                            override fun onSuccess(p0: Uri?) {
                                downloadUrl = p0!!
                                val childData = firebaseDatabase.child(authentication).push()
                                childData.setValue(UserInfo(alertView.etUserName.text.toString(), FirebaseAuth.getInstance().currentUser!!.email, downloadUrl.toString()))
                                Toast.makeText(this@MainActivity, "Signup Completed Successfully", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()
                            }
                        })
                    }
                })
                alertDialog.dismiss()
            } else {
                Toast.makeText(this@MainActivity, "Empty Fields", Toast.LENGTH_SHORT).show()
            }
        }
        firebaseDatabase.child(authentication).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()) {
                    alertDialog.show()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DoVerification()
        supportFragmentManager.beginTransaction().replace(R.id.mainActivityCoordinator,NoteFragment()).commit()
        val readPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (readPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
        mToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Glide.with(this@MainActivity).load(R.drawable.home).thumbnail(Glide.with(this).load(R.drawable.home)).into(toolBarImage)
        auth = FirebaseAuth.getInstance()

        fStorage = FirebaseStorage.getInstance().reference

        fDatabase = FirebaseDatabase.getInstance().reference
        btnFloatingTodos.setOnClickListener({
            val intent = Intent(this, NewIntent::class.java)
            intent.putExtra("Pressed", "Todos")
            startActivity(intent)
        })
        btnFloatingCamera.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
            mView = layoutInflater.inflate(R.layout.camera_view, null)
            alertDialog.setView(mView)
            alertDialog.setTitle("SNAPS")
            mView.alertImage.setOnClickListener({
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent, Gallery_Request)
            })
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Save", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    startToSave()
                }
            })
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }
            })
            alertDialog.show()
        }

        val nameRef = fDatabase.child("Users").child(auth.currentUser!!.uid)
        val headerView=navView.getHeaderView(0)
        nameRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val userInfo = p0.getValue(UserInfo::class.java)
                userInfo?.let {
                    user=it
                    user?.let {
                        Picasso.get().load(it.dpUrl).placeholder(R.drawable.profile_placeholder).into(headerView.navImage)
                        headerView.tvUserName.text = it.userName
                        headerView.tvUserEmail.text = it.email
                    }
                }
            }
        })
        navView.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.acLogout) {
                    auth.signOut()
                    startActivity(Intent(this@MainActivity, Authentication::class.java))
                    finish()
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        })
        btnFloatingRecording.setOnClickListener {
            val intent = Intent(this, NewIntent::class.java)
            intent.putExtra("Pressed", "Recording")
            startActivity(intent)
        }
        btnFloatingNotes.setOnClickListener {
            val intent = Intent(this, NewIntent::class.java)
            intent.putExtra("Pressed", "Notes")
            startActivity(intent)
        }
         bottomNavMenu.setOnNavigationItemSelectedListener(object:BottomNavigationView.OnNavigationItemSelectedListener {
             override fun onNavigationItemSelected(item: MenuItem): Boolean {
                 if(item.itemId.equals(R.id.bottomNotes))
                 {
                     supportFragmentManager.beginTransaction().replace(R.id.mainActivityCoordinator,NoteFragment()).commit()
                 }
                 else if(item.itemId.equals(R.id.bottomSnaps))
                 {
                     supportFragmentManager.beginTransaction().replace(R.id.mainActivityCoordinator,CameraFragment()).commit()
                 }
                 else if(item.itemId.equals(R.id.bottomTodos))
                 {
                     supportFragmentManager.beginTransaction().replace(R.id.mainActivityCoordinator,TodoFragment()).commit()
                 }
                 else
                 {
                     supportFragmentManager.beginTransaction().replace(R.id.mainActivityCoordinator,VoiceFragment()).commit()
                 }
                 return true
             }
         })
    }

    private fun startToSave() {
        val title = mView.alertTitle.text.toString().trim()
        val desc = mView.alertMessage.text.toString().trim()
        if (!title.isEmpty() && !desc.isEmpty()) {
            val dialog = AlertDialog.Builder(this@MainActivity).create()
            dialog.setMessage("Saving....")
            dialog.setCancelable(false)
            dialog.show()
            val currentUser = auth.currentUser!!.uid
            val filepath = fStorage.child(currentUser).child(imageUri.lastPathSegment)
            filepath.putFile(imageUri).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    var downloadUrl: Uri? = null
                    p0!!.storage.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            downloadUrl = p0!!
                            val childData = fDatabase.child(currentUser).child("Snaps").push()
                            childData.setValue(Snaps(System.currentTimeMillis().toString(),title, desc, downloadUrl.toString()))
                            dialog.dismiss()
                            Toast.makeText(this@MainActivity, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            })
        } else {
            Toast.makeText(this@MainActivity, "Empty Fields!!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Gallery_Request && resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            mView.alertImage.setImageURI(imageUri)
        }
        if (requestCode == SignUp_Code && resultCode == Activity.RESULT_OK) {
            signUpImageUri = data!!.data
            alertView.sign_up_image.setImageURI(signUpImageUri)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (mToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}