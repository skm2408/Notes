package com.example.shubhammishra.notes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import kotlinx.android.synthetic.main.alert_dialog_layout.view.*
import kotlinx.android.synthetic.main.alert_options.view.*
import kotlinx.android.synthetic.main.alert_signup.view.*
import kotlinx.android.synthetic.main.camera_view.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.navbar_profile.view.*
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {
    var user: UserInfo? = null
    lateinit var fStorage: StorageReference
    lateinit var fDatabase: DatabaseReference
    lateinit var mView: View
    lateinit var imageUri: Uri
    lateinit var signUpImageUri: Uri
    val Gallery_Request = 1
    val Camera_Request = 2
    @SuppressLint("SetTextI18n")
    lateinit var mToggle: ActionBarDrawerToggle
    lateinit var auth: FirebaseAuth
    lateinit var alertView: View
    lateinit var dpView: View
    lateinit var progressDialog: ProgressDialog
    val SignUp_Code = 120
    val DpChange = 111
    val values: ContentValues = ContentValues()
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this@MainActivity, "Can't Process Without your permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == 601) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this@MainActivity, "Can't Proceed Without your permission", Toast.LENGTH_SHORT).show()
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
        supportFragmentManager.beginTransaction().replace(R.id.mainActivityCoordinator, NoteFragment()).commit()
        toolBarImage.setImageResource(R.drawable.back_home_notes)
        toolBarText.text = "Notes"
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
        auth = FirebaseAuth.getInstance()

        fStorage = FirebaseStorage.getInstance().reference

        fDatabase = FirebaseDatabase.getInstance().reference
        btnFloatingTodos.setOnClickListener({
            btnFloatingAction.close(true)
            val intent = Intent(this, NewIntent::class.java)
            intent.putExtra("Pressed", "Todos")
            startActivity(intent)
        })
        btnFloatingCamera.setOnClickListener {
            btnFloatingAction.close(true)
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
            mView = layoutInflater.inflate(R.layout.camera_view, null)
            alertDialog.setView(mView)
            alertDialog.setTitle("SNAPS")
            mView.alertImage.setOnClickListener({
                generateOptions(mView)
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
        nameRef.keepSynced(true)
        val headerView = navView.getHeaderView(0)
        headerView.navImage.setOnClickListener {
            var userInfo:UserInfo?=null
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
            dpView = layoutInflater.inflate(R.layout.camera_view, null)
            dpView.alertMessage.visibility = View.GONE
            dpView.alertTitle.visibility = View.GONE
            alertDialog.setMessage("Update Profile Photo")
            alertDialog.setView(dpView)
            val uid = auth.currentUser!!.uid
            val databaseReference = fDatabase.child("Users").child(uid)
            databaseReference.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    userInfo = p0.getValue(UserInfo::class.java)
                    Picasso.get().load(userInfo!!.dpUrl).placeholder(R.drawable.load_image).fit().centerInside().into(dpView.alertImage)
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }
            })
            dpView.alertImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent,DpChange)
            }
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update Profile", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val progressDialog = ProgressDialog(this@MainActivity)
                    progressDialog.create()
                    progressDialog.setCancelable(false)
                    progressDialog.setMessage("Changing Profile Photo...")
                    progressDialog.show()
                    val databaseStorage=fStorage.child(uid).storage.getReferenceFromUrl(userInfo!!.dpUrl)
                    databaseStorage.delete().addOnSuccessListener {
                        databaseReference.ref.removeValue()
                        val filepath = fStorage.child(uid).child(imageUri.lastPathSegment + System.currentTimeMillis().toString())
                        filepath.putFile(imageUri).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                                var downloadUrl: Uri? = null
                                p0!!.storage.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                                    override fun onSuccess(p0: Uri?) {
                                        downloadUrl = p0!!
                                        val childData = fDatabase.child("Users").child(uid).push()
                                        userInfo!!.dpUrl=downloadUrl.toString()
                                        childData.setValue(userInfo!!)
                                        progressDialog.dismiss()
                                        Toast.makeText(this@MainActivity,"Profile Photo Updated Successfully",Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        })
                    }
                    alertDialog.dismiss()
                }
            })
            alertDialog.show()
        }
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
                    user = it
                    user?.let {
                        //Picasso.get().load(it.dpUrl).placeholder(R.drawable.profile_placeholder).into(headerView.navImage)
                        Glide.with(applicationContext).load(it.dpUrl).thumbnail(Glide.with(applicationContext).load(R.drawable.profile_placeholder)).into(headerView.navImage)
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
                } else if (item.itemId == R.id.acChangePassword) {
                    val alertDialog = AlertDialog.Builder(this@MainActivity).create()
                    val mView = layoutInflater.inflate(R.layout.alert_dialog_layout, null)
                    mView.etFileName.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    mView.etFileName.hint = "Enter New Password"
                    alertDialog.setView(mView)
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Change Password", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val progressDialog = ProgressDialog(this@MainActivity)
                            progressDialog.create()
                            progressDialog.setCancelable(false)
                            progressDialog.setMessage("Changing Password")
                            progressDialog.show()
                            val password = mView.etFileName.text.toString()
                            if (password.isEmpty()) {
                                Toast.makeText(this@MainActivity, "Empty Password Field", Toast.LENGTH_SHORT).show()
                            } else {
                                auth.currentUser!!.updatePassword(password).addOnCompleteListener {
                                    Toast.makeText(this@MainActivity, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                                    progressDialog.dismiss()
                                }
                            }
                        }
                    })
                    alertDialog.show()
                }
                else if(item.itemId==R.id.navAbout)
                {
                    val dialog=Dialog(this@MainActivity,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                    val view2=layoutInflater.inflate(R.layout.about_info,null)
                    dialog.setContentView(view2)
                    dialog.create()
                    dialog.show()
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        })
        btnFloatingRecording.setOnClickListener {
            btnFloatingAction.close(true)
            val intent = Intent(this, NewIntent::class.java)
            intent.putExtra("Pressed", "Recording")
            startActivity(intent)
        }
        btnFloatingNotes.setOnClickListener {
            btnFloatingAction.close(true)
            val intent = Intent(this, NewIntent::class.java)
            intent.putExtra("Pressed", "Notes")
            startActivity(intent)
        }
        bottomNavMenu.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                if (item.itemId.equals(R.id.bottomNotes)) {
                    toolBarImage.setImageResource(R.drawable.back_home_notes)
                    toolBarText.text = "Notes"
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.mainActivityCoordinator, NoteFragment()).commit()
                } else if (item.itemId.equals(R.id.bottomSnaps)) {
                    toolBarImage.setImageResource(R.drawable.back_home_camera)
                    toolBarText.text = "Snaps"
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.mainActivityCoordinator, CameraFragment()).commit()
                } else if (item.itemId.equals(R.id.bottomTodos)) {
                    toolBarImage.setImageResource(R.drawable.back_home_todos)
                    toolBarText.text = "Todo List"
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.mainActivityCoordinator, TodoFragment()).commit()
                } else {
                    toolBarImage.setImageResource(R.drawable.back_home_voice)
                    toolBarText.text = "Recordings"
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).replace(R.id.mainActivityCoordinator, VoiceFragment()).commit()
                }
                return true
            }
        })
    }

    private fun generateOptions(view: View) {
        val alertDialog = AlertDialog.Builder(view.context).create()
        val mView = (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.alert_options, null)
        mView.gallery_options.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, Gallery_Request)
            alertDialog.dismiss()
        }
        mView.camera_options.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(view.context, Manifest.permission.CAMERA)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, Camera_Request)
                alertDialog.dismiss()
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), 601)
            }
        }
        alertDialog.setView(mView)
        alertDialog.setTitle("Choose")
        alertDialog.show()
    }

    private fun startToSave() {
        val title = mView.alertTitle.text.toString().trim()
        val desc = mView.alertMessage.text.toString().trim()
        val initialDraw = (resources.getDrawable(R.drawable.add_image) as BitmapDrawable).bitmap
        val finalDraw = (mView.alertImage.drawable as BitmapDrawable).bitmap
        if (!title.isEmpty() && !desc.isEmpty() && (!initialDraw.sameAs(finalDraw))) {
            val dialog = AlertDialog.Builder(this@MainActivity).create()
            dialog.setMessage("Saving....")
            dialog.setCancelable(false)
            dialog.show()
            val currentUser = auth.currentUser!!.uid
            val filepath = fStorage.child(currentUser).child(imageUri.lastPathSegment + System.currentTimeMillis().toString())
            filepath.putFile(imageUri).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    var downloadUrl: Uri? = null
                    p0!!.storage.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            downloadUrl = p0!!
                            val childData = fDatabase.child(currentUser).child("Snaps").push()
                            childData.setValue(Snaps(System.currentTimeMillis().toString(), title, desc, downloadUrl.toString()))
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
        } else if (requestCode == SignUp_Code && resultCode == Activity.RESULT_OK) {
            signUpImageUri = data!!.data
            alertView.sign_up_image.setImageURI(signUpImageUri)
        } else if (requestCode == Camera_Request && resultCode == Activity.RESULT_OK) {
            mView.alertImage.setImageURI(imageUri)
        } else if (requestCode == DpChange && resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data
            dpView.alertImage.setImageURI(imageUri)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (btnFloatingAction.isOpened) {
            btnFloatingAction.close(true)
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