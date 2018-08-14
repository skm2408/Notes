package com.example.shubhammishra.notes.Adapters

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import com.example.shubhammishra.notes.Classes.Recording
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.recycler_view_audio.view.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class AudioAdapter(var listAudio: ArrayList<Recording>) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {
    lateinit var progressDialog: ProgressDialog
    lateinit var context: Context
    lateinit var view1: View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val lf = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view1 = lf.inflate(R.layout.recycler_view_audio, parent, false)
        return AudioViewHolder(view1)
    }

    override fun getItemCount(): Int = listAudio.size

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        var mediaPlayer: MediaPlayer? = null
        context = holder.itemView.context
        holder.itemView.audioTitle.text = listAudio[position].recordName
        var tag = false
        holder.itemView.audioMediaPlayer.setOnClickListener {
            if (tag == false) {
                it.audioMediaPlayer.setImageResource(R.drawable.media_stop)
                mediaPlayer = MediaPlayer()
                try {
                    holder.itemView.showPlay.visibility = View.VISIBLE
                    holder.itemView.audioMediaPlayer.visibility = View.GONE
                    mediaPlayer!!.setDataSource(listAudio[position].recordUrl)
                    mediaPlayer!!.setOnPreparedListener {
                        it.start()
                        holder.itemView.showPlay.visibility = View.GONE
                        holder.itemView.audioMediaPlayer.visibility = View.VISIBLE
                    }
                    mediaPlayer!!.prepareAsync()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                tag = true
            } else {
                it.audioMediaPlayer.setImageResource(R.drawable.media_play)
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                tag = false
            }
            mediaPlayer!!.setOnCompletionListener {
                tag = false
                holder.itemView.audioMediaPlayer.setImageResource(R.drawable.media_play)
            }
        }
        holder.itemView.audioToolTitle.text = listAudio[position].recordName+"              "
        holder.itemView.audioToolMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.audioToolMenu)
            popupMenu.inflate(R.menu.record_menu)
            popupMenu.setOnMenuItemClickListener {

                if (it.itemId.equals(R.id.menuDelete)) {
                    deleteFromFireBase(listAudio[position], position)
                    Toast.makeText(holder.itemView.context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                } else if (it.itemId.equals(R.id.menuShare)) {
                    GetAudio(listAudio[position]).execute()
                }
                false
            }
            popupMenu.show()
        }
    }

    inner class GetAudio(var url: Recording) : AsyncTask<Recording, Unit, String>() {
        override fun doInBackground(vararg params: Recording?): String {
            var path = ""
            var count = 0
            try {
                val Url = URL(url.recordUrl)
                val rl = Url.openConnection()
                rl.connect()
                val input = BufferedInputStream(Url.openStream())
                val dir = Environment.getExternalStorageDirectory().absoluteFile
                val myDir = File("$dir/Notes/Recording")
                myDir.mkdirs()
                val fileName = "Audio-${url.recordName}.3gp"
                val file = File(myDir, fileName)
                if (file.exists())
                    file.delete()
                val out = FileOutputStream(file)
                val byte = ByteArray(1024)
                while (true) {
                    count = input.read(byte)
                    if (count != -1)
                        out.write(byte, 0, count)
                    else
                        break
                }
                out.flush()
                out.close()
                input.close()
                path = "$dir/Notes/Recording/$fileName"
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return path
        }

        override fun onPostExecute(result: String?) {
            progressDialog.dismiss()
            shareAudio(result!!, context)
            super.onPostExecute(result)
        }

        override fun onPreExecute() {
            progressDialog = ProgressDialog(context)
            progressDialog.create()
            progressDialog.setMessage("Preparing Audio to Share")
            progressDialog.setCancelable(false)
            progressDialog.show()
            super.onPreExecute()
        }
    }

    private fun shareAudio(path: String, context: Context?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("audio/*")
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path)))
        context!!.startActivity(Intent.createChooser(intent, "Share Audio Via"))
    }

    private fun deleteFromFireBase(recording: Recording, position: Int) {
        val auth = FirebaseAuth.getInstance().currentUser!!.uid
        val dataBaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Recordings")
        val query: Query = dataBaseReference.orderByChild("id").equalTo(recording.id)
        val dataStorage = FirebaseStorage.getInstance().reference.child(auth).storage.getReferenceFromUrl(recording.recordUrl)
        dataStorage.delete().addOnSuccessListener {
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if (it.child("id").value!!.equals(recording.id)) {
                            it.ref.removeValue()
                        }
                    }
                    listAudio.removeAt(position)
                    notifyDataSetChanged()
                }
            })
        }
    }

    class AudioViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}