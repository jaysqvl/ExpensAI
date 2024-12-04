package com.example.cmpt362_finalproject.ui.initial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.Fragment
import com.example.cmpt362_finalproject.InitialActivity
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.ui.PreferenceFragment
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignupFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        view.findViewById<View>(R.id.back_arrow).setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val emailField = view.findViewById<EditText>(R.id.email_field)
        val passwordField = view.findViewById<EditText>(R.id.password_field)
        val passwordCheckField = view.findViewById<EditText>(R.id.password_check_field)
        val signupButton = view.findViewById<Button>(R.id.signup_button)

        signupButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val checkPassword = passwordCheckField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password == checkPassword){
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { loginTask ->
                                        if (loginTask.isSuccessful) {
                                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                                            (activity as InitialActivity).replaceFragment(PreferenceFragment())
                                        } else {
                                            Toast.makeText(requireContext(), "Login failed: ${loginTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                val errorMessage = when (task.exception) {
                                    is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 6 characters."
                                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                                    is FirebaseAuthUserCollisionException -> "This email is already registered."
                                    else -> task.exception?.message
                                }
                                Toast.makeText(requireContext(), "Sign-up failed: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Password not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
