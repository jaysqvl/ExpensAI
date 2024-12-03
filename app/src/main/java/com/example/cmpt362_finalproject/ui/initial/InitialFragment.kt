package com.example.cmpt362_finalproject.ui.initial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cmpt362_finalproject.InitialActivity
import com.example.cmpt362_finalproject.MainActivity
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.ui.PreferenceFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class InitialFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_initial, container, false)

        auth = FirebaseAuth.getInstance()

        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (isPreferencesCompleted()) {
                requireActivity().startActivity(
                    Intent(requireContext(), MainActivity::class.java)
                )
                requireActivity().finish()
            } else {
                (activity as InitialActivity).replaceFragment(PreferenceFragment())
            }
        }

        view.findViewById<View>(R.id.signup_btn).setOnClickListener {
            (activity as? InitialActivity)?.replaceFragment(SignupFragment())
        }
        view.findViewById<View>(R.id.login_btn).setOnClickListener {
            (activity as? InitialActivity)?.replaceFragment(LoginFragment())
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        view.findViewById<View>(R.id.google_signin_btn).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Sign-up with Google successful!", Toast.LENGTH_SHORT).show()
                    if (isPreferencesCompleted()) {
                        requireActivity().startActivity(
                            Intent(requireContext(), MainActivity::class.java)
                        )
                        requireActivity().finish()
                    } else {
                        (activity as InitialActivity).replaceFragment(PreferenceFragment())
                    }
                } else {
                    Toast.makeText(requireContext(), "Firebase sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun isPreferencesCompleted(): Boolean {
        val sharedPreferences = (activity as InitialActivity).getSharedPreferences("ExpensAI", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("preferences_completed", false)
    }
}
