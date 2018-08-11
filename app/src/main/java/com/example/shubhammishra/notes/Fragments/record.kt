package com.example.shubhammishra.notes.Fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.shubhammishra.notes.Classes.Recording
import com.example.shubhammishra.notes.MainActivity
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.alert_dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.fragment_record.view.*
import java.io.File
import java.io.IOException
import java.util.*

class record : Fragment() {
    var count = 0
    var min = 0
    lateinit var filePath: String
    var sec = 0
    lateinit var progressDialog: ProgressDialog
    var timer: Timer? = null
    lateinit var pathSave: String
    lateinit var mediaPlayer: MediaPlayer
    lateinit var mediaRecorder: MediaRecorder
    lateinit var view1: View
    var permissionFlag = false
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view1 = inflater.inflate(R.layout.fragment_record, container, false)
        val alertDialog = AlertDialog.Builder(view1.context).create()
        alertDialog.setTitle("Save File")
        val mView = alertDialog.layoutInflater.inflate(R.layout.alert_dialog_layout, null)
        alertDialog.setView(mView)
        alertDialog.setMessage("Create a new Record File before you Proceed")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                filePath = mView.etFileName.text.toString()
            }
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                alertDialog.dismiss()
                activity!!.finish()
            }
        })
        alertDialog.setCancelable(false)
        alertDialog.show()
        return view1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mediaPlayer=MediaPlayer()
        view1.tvStatus.visibility=View.VISIBLE
        view1.tvStatus.text = "Tap and Hold to Record"
        view1.btnRecord.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("SetTextI18n")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event!!.action == MotionEvent.ACTION_DOWN) {
                    count = 0
                    min = 0
                    sec = 0
                    timer = Timer()
                    var timerTask = object : TimerTask() {
                        override fun run() {
                            Time().execute(count)
                        }
                    }
                    timer!!.schedule(timerTask, 1000, 1000)
                    pathSave = Environment.getExternalStorageDirectory().absolutePath
                    pathSave += "/" + filePath + ".3gp"
                    setupMediaRecorder()
                    try {
                        mediaRecorder.prepare()
                        mediaRecorder.start()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (event!!.action == MotionEvent.ACTION_UP) {
                    view1.tvStatus.text = "Recorded Successfully"
                    timer!!.cancel()
                    try {
                        mediaRecorder.stop()
                        mediaRecorder.reset()
                        mediaRecorder.release()
                        uploadAudio()
                        llRecording.visibility = View.GONE
                        llMusicPlayer.visibility = View.VISIBLE
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }

                }
                return false
            }
        })
        var tag = false
        view1.btPlay.setOnClickListener {
            if (tag == false) {
                view1.btPlay.setImageResource(R.drawable.ic_action_stop)
                view1.tvTitleMusic.text = pathSave.substring(pathSave.indexOf('/') + 1)
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer.setDataSource(pathSave)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                tag = true
            } else {
                view1.btPlay.setImageResource(R.drawable.ic_action_music_play)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                tag = false
            }
        }

    }

//    override fun onDestroyView() {
//        if(mediaPlayer.isPlaying)
//        {
//            mediaPlayer.stop()
//            mediaPlayer.reset()
//            mediaPlayer.release()
//        }
//        val fragment=activity!!.supportFragmentManager.findFragmentById(R.layout.fragment_record)
//        if(fragment!=null)
//        {
//            activity!!.getSupportFragmentManager().beginTransaction().remove(fragment).commit()
//        }
//        super.onDestroyView()
//    }

    private fun uploadAudio() {
        progressDialog = ProgressDialog(view1.context)
        progressDialog.setMessage("Saving Audio..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val storageReference = FirebaseStorage.getInstance().reference.child(currentUser).child(pathSave)
        val uri = Uri.fromFile(File(pathSave))
        storageReference.putFile(uri).addOnSuccessListener {
            var downloadUri: Uri? = null
            it.storage.downloadUrl.addOnSuccessListener {
                downloadUri = it
                progressDialog.dismiss()
                val databaseReference = FirebaseDatabase.getInstance().reference.child(currentUser).child("Recordings").push()
                val recording = Recording(System.currentTimeMillis().toString(),filePath,downloadUri.toString())
                databaseReference.setValue(recording)
                Snackbar.make(view1, "Saved Successfully", Snackbar.LENGTH_INDEFINITE).setAction("Go to Homepage",object:View.OnClickListener{
                    override fun onClick(v: View?) {
                        val file=File(pathSave)
                        file.delete()
                        activity!!.finish()
                    }
                }).show()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionFlag = true
            } else {
                Toast.makeText(view1.context, "Can't record audio without your permission", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        val permission = ContextCompat.checkSelfPermission(view!!.context, Manifest.permission.RECORD_AUDIO)
        val permission1 = ContextCompat.checkSelfPermission(view!!.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED && permission1 == PackageManager.PERMISSION_GRANTED) {
            permissionFlag = true
        } else {
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
    }

    @SuppressLint("SetTextI18n")

    private fun setupMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder.setOutputFile(pathSave)
    }

    @SuppressLint("StaticFieldLeak")
    inner class Time : AsyncTask<Int, Unit, Int>() {
        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            if (result!! < 60) {
                sec = result!!
                if (result < 10)
                    view1.tvTime.text = "0" + min.toString() + ":" + "0" + sec
                else
                    view1.tvTime.text = "0" + min.toString() + ":" + sec
            } else {
                min = result / 60
                sec = result % 60
                if (sec < 10)
                    view1.tvTime.text = min.toString() + ":" + "0" + sec
                else {
                    view1.tvTime.text = min.toString() + ":" + sec
                }
            }
            view1.tvStatus.visibility = View.VISIBLE
            view1.tvStatus.text = "Recording..."
        }

        override fun doInBackground(vararg params: Int?): Int {
            var start = 0
            while (System.currentTimeMillis() - start < 1000) {
                start++
            }
            count++
            return count
        }
    }

}
