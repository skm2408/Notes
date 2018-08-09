package com.example.shubhammishra.notes.Adapters
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.shubhammishra.notes.Classes.Snaps
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.camera_view.view.*
import kotlinx.android.synthetic.main.recycler_view_snaps.view.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class SnapsAdapter(var snapList: ArrayList<Snaps>) : RecyclerView.Adapter<SnapsAdapter.SnapViewHolder>() {

    lateinit var progressDialog: ProgressDialog
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapViewHolder {
        val lf = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = lf.inflate(R.layout.recycler_view_snaps, parent, false)
        context=view.context
        return SnapViewHolder(view)
    }

    override fun getItemCount(): Int = snapList.size

    override fun onBindViewHolder(holder: SnapViewHolder, position: Int) {
        Glide.with(holder.itemView).load(snapList[position].imgUrl).into(holder.itemView.snapImage)
        holder.itemView.snapTitle.text = snapList[position].title
        holder.itemView.snapText.text = snapList[position].description
        holder.itemView.snapToolbar.toolBarTitle.text = snapList[position].title
        holder.itemView.toolBarMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.toolBarMenu)
            popupMenu.inflate(R.menu.adapter_menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId.equals(R.id.menuEdit)) {
                    alertSnapsEdit(snapList[position],holder.itemView.context,position)
                    true
                } else if (it.itemId.equals(R.id.menuDelete)) {
                    deleteSnaps(snapList[position],position)
                    Toast.makeText(holder.itemView.context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
                    true
                } else if (it.itemId.equals(R.id.menuShare)) {
                    GetSnaps(snapList[position]).execute()
                    true

                }
                false
            }
            popupMenu.show()
        }
        holder.itemView.snapText.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        holder.itemView.snapText.movementMethod = ScrollingMovementMethod()
    }

    private fun alertSnapsEdit(snaps: Snaps, context: Context?, position: Int) {
        val alertDialog = AlertDialog.Builder(context).create()
        val mView = (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.camera_view, null)
        alertDialog.setCancelable(false)
        mView.alertTitle.setText(snaps.title, TextView.BufferType.EDITABLE)
        mView.alertMessage.setText(snaps.description, TextView.BufferType.EDITABLE)
        Picasso.get().load(snaps.imgUrl).placeholder(R.drawable.add_image).into(mView.alertImage)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Save",object:DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val title=mView.alertTitle.text.toString()
                val text=mView.alertMessage.text.toString()
                if(!title.isEmpty()&&!text.isEmpty()) {
                    val auth = FirebaseAuth.getInstance().currentUser!!.uid
                    val dataBaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Snaps")
                    val query: Query = dataBaseReference.orderByChild("id").equalTo(snaps.id)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            p0.children.forEach {
                                if (it.child("id").value!!.equals(snaps.id)) {
                                    it.ref.setValue(Snaps(snaps.id,title,text,snaps.imgUrl))
                                }
                            }
                            snapList[position] =Snaps(snaps.id,title,text,snaps.imgUrl)
                            notifyDataSetChanged()
                            alertDialog.dismiss()
                        }
                    })
                }
                else
                {
                    Toast.makeText(context,"Empty Fields!!",Toast.LENGTH_SHORT).show()
                }
            }
        })
        alertDialog.setView(mView)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                alertDialog.dismiss()
            }
        })
        mView.setPadding(5,25,5,20)
        alertDialog.setTitle("EDIT BOX")
        alertDialog.setIcon(R.drawable.menu_edit)
        alertDialog.show()
    }

    private fun shareImage(context: Context,file:String) {
        val intent:Intent= Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(File(Environment.getExternalStorageDirectory().absolutePath+"/Notes/Snaps/$file")))
        intent.setType("image/*")
        context.startActivity(Intent.createChooser(intent,"Share Via"))
    }
    inner class GetSnaps(var snaps:Snaps):AsyncTask<Snaps,Unit,Bitmap?>()
    {
        override fun onPostExecute(result: Bitmap?) {
            progressDialog.dismiss()
            super.onPostExecute(result)
            val finalBitmap=result
            val pathSave=Environment.getExternalStorageDirectory().absolutePath
            val mydir= File("$pathSave/Notes/Snaps")
            mydir.mkdirs()
            val fileName="Img-"+snaps.title+".jpg"
            val file=File(mydir,fileName)
            if(file.exists())
                shareImage(context,fileName)
            try {
                val out=FileOutputStream(file)
                finalBitmap!!.compress(Bitmap.CompressFormat.JPEG,100,out)
                out.flush()
                out.close()
                shareImage(context,fileName)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
                Log.e("ErrorFile","Invalid File")
            }
        }

        override fun doInBackground(vararg params: Snaps?): Bitmap? {
            val url= snaps.imgUrl
            var bitmap:Bitmap?=null
            try {
                val input=URL(url).openStream()
                bitmap=BitmapFactory.decodeStream(input)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPreExecute() {
            progressDialog= ProgressDialog(context)
            progressDialog.create()
            progressDialog.setMessage("Getting Image Ready")
            progressDialog.setCancelable(false)
            progressDialog.show()
            super.onPreExecute()
        }
    }
    private fun deleteSnaps(snaps: Snaps, position: Int) {

        val auth= FirebaseAuth.getInstance().currentUser!!.uid
        val dataBaseReference= FirebaseDatabase.getInstance().reference.child(auth).child("Snaps")
        val query: Query =dataBaseReference.orderByChild("title").equalTo(snaps.title)
        val dataStorage= FirebaseStorage.getInstance().reference.child(auth).storage.getReferenceFromUrl(snaps.imgUrl)
        dataStorage.delete().addOnSuccessListener {
            query.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        if(it.child("title").value!!.equals(snaps.title))
                        {
                            it.ref.removeValue()
                        }
                    }
                    snapList.removeAt(position)
                    notifyDataSetChanged()
                }
            })
        }
    }

    class SnapViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}