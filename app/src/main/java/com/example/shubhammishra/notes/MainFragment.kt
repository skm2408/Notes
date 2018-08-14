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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var myContext:FragmentActivity
    override fun onAttach(context: Context?) {
        myContext=context as FragmentActivity
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_main, container, false)
        auth= FirebaseAuth.getInstance()
        view.btnSign.setOnClickListener({
            val imm=myContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.fragmentLayout.windowToken,0)
            view.getProgressStatus.visibility= View.VISIBLE
            val email=view.etEmail.text.toString()
            val password=view.etPassword.text.toString()
            if(email.isEmpty()||password.isEmpty())
            {
                Toast.makeText(view.context,"Empty email or password field", Toast.LENGTH_SHORT).show()
                view.getProgressStatus.visibility=View.GONE
            }
            else
            {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val intent= Intent(view.context,MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    }
                    else
                    {
                        Toast.makeText(view.context,"Email or password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                    view.getProgressStatus.visibility=View.GONE
                }
            }
        })
        view.btnSignup.setOnClickListener({
            myContext.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right).replace(R.id.mainFrame,SignUpFragment()).commit()
        })
        view.tvForgotPassword.setOnClickListener({
            myContext.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right).replace(R.id.mainFrame,ForgetPassword()).commit()
        })
        return view
    }


}
