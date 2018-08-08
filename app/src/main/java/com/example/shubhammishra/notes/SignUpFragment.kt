package com.example.shubhammishra.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

class SignUpFragment : Fragment() {

    lateinit var myContext:FragmentActivity
    override fun onAttach(context: Context?) {
        myContext=context as FragmentActivity
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_sign_up, container, false)
        view.backBtn.setOnClickListener({
            myContext.supportFragmentManager.beginTransaction().replace(R.id.mainFrame,MainFragment()).commit()
        })
        view.btnsignup.setOnClickListener({
            progressSignup.visibility=View.VISIBLE
            val email=etmail.text.toString()
            val password=etpass.text.toString()
            val auth:FirebaseAuth=FirebaseAuth.getInstance()
            if(email.isEmpty()||password.isEmpty())
            {
                Toast.makeText(view.context,"Empty Fields",Toast.LENGTH_SHORT).show()
                progressSignup.visibility=View.GONE
            }
            else
            {
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Toast.makeText(view.context,"Registered Successfully!!",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(view.context,Authentication::class.java))
                        activity!!.finish()
                    }
                    else
                    {
                        Toast.makeText(view.context,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    }
                    progressSignup.visibility=View.GONE
                }
            }
        })
        return view
    }



}
