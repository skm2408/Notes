package com.example.shubhammishra.notes.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.shubhammishra.notes.Classes.Notes
import com.example.shubhammishra.notes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_notes.view.*
import kotlinx.android.synthetic.main.recycler_view_notes.view.*
import org.w3c.dom.Text

class NotesAdapter(var listNotes: ArrayList<Notes>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val lf = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        context = parent.context
        return NotesViewHolder(lf.inflate(R.layout.recycler_view_notes, parent, false))
    }

    override fun getItemCount(): Int = listNotes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.itemView.noteTitle.text = listNotes[position].title
        holder.itemView.noteText.setText(listNotes[position].text, TextView.BufferType.EDITABLE)
        holder.itemView.noteText.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        holder.itemView.noteText.movementMethod = ScrollingMovementMethod()
        holder.itemView.noteMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView.noteMenu)
            popupMenu.inflate(R.menu.adapter_menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.menuDelete) {
                    deleteNotes(listNotes[position], position)
                    Toast.makeText(holder.itemView.context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                } else if (it.itemId == R.id.menuShare) {
                    shareNotes(listNotes[position], holder.itemView)
                } else if (it.itemId == R.id.menuEdit) {
                    alertEditNote(listNotes[position], holder.itemView.context, position)
                }
                false
            }
            popupMenu.show()
        }
    }

    private fun alertEditNote(notes: Notes, context: Context?, position: Int) {
        val alertDialog = AlertDialog.Builder(context).create()
        val mView = (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.fragment_notes, null)
        alertDialog.setCancelable(false)
        mView.etNotes.setText(notes.text, TextView.BufferType.EDITABLE)
        mView.etTitle.setText(notes.title, TextView.BufferType.EDITABLE)
        mView.saveNote.setOnClickListener {
            val title=mView.etTitle.text.toString()
            val text=mView.etNotes.text.toString()
            if(!title.isEmpty()&&!text.isEmpty()) {
                val auth = FirebaseAuth.getInstance().currentUser!!.uid
                val dataBaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Notes")
                val query: Query = dataBaseReference.orderByChild("id").equalTo(notes.id)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            if (it.child("id").value!!.equals(notes.id)) {
                                it.ref.setValue(Notes(notes.id,title,text))
                            }
                        }
                        listNotes[position] =Notes(notes.id,title,text)
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

    private fun shareNotes(notes: Notes, view: View) {
        val intent: Intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, "Title:" + notes.title + "\n" + "Text:" + notes.text)
        intent.setType("text/plain")
        view.context.startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun deleteNotes(notes: Notes, position: Int) {
        val auth = FirebaseAuth.getInstance().currentUser!!.uid
        val dataBaseReference = FirebaseDatabase.getInstance().reference.child(auth).child("Notes")
        val query: Query = dataBaseReference.orderByChild("id").equalTo(notes.id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    if (it.child("id").value!!.equals(notes.id)) {
                        it.ref.removeValue()
                    }
                }
                listNotes.removeAt(position)
                notifyDataSetChanged()
            }
        })
    }

    class NotesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    }
}