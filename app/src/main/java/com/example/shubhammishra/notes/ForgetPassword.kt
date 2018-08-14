package com.example.shubhammishra.notes

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_forget_password.*
import kotlinx.android.synthetic.main.fragment_forget_password.view.*

class ForgetPassword : Fragment() {
    lateinit var myContext: FragmentActivity
    override fun onAttach(context: Context?) {
        myContext=context as FragmentActivity
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_forget_password, container, false)
        view.btnBack.setOnClickListener({
            myContext.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right).replace(R.id.mainFrame,MainFragment()).commit()
        })
        view.btnReset.setOnClickListener({
            authenticate(view)
        })
        return view
    }

    private fun authenticate(view:View) {
        forgetProgress.visibility=View.VISIBLE
         val auth:FirebaseAuth=FirebaseAuth.getInstance()
        val email=view.etRegistered.text.toString()
        if(email.isEmpty())
        {
            Toast.makeText(view.context,"Empty email field",Toast.LENGTH_SHORT).show()
            forgetProgress.visibility=View.GONE
        }
        else
        {
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Toast.makeText(view.context,"We have sent a link to your Registered Email to reset the password",Toast.LENGTH_SHORT).show()
                    myContext.supportFragmentManager.beginTransaction().replace(R.id.mainFrame,MainFragment()).commit()
                }
                else
                {
                    Toast.makeText(view.context,"The email you entered is not registered",Toast.LENGTH_SHORT).show()
                }
                forgetProgress.visibility=View.GONE
            }
        }
    }
}
