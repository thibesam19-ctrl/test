package com.axiom.aicoach.security

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the authenticated user's ID.
 * Returns the Firebase UID when signed in, or "local_user" as an offline fallback
 * so the app remains functional without a network connection.
 */
@Singleton
class UserSession @Inject constructor(
    private val auth: FirebaseAuth,
) {

    val userId: String
        get() = auth.currentUser?.uid ?: LOCAL_USER_ID

    val isSignedIn: Boolean
        get() = auth.currentUser != null

    val userIdFlow: Flow<String> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid ?: LOCAL_USER_ID)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val isSignedInFlow: Flow<Boolean> = userIdFlow.map { it != LOCAL_USER_ID }

    companion object {
        const val LOCAL_USER_ID = "local_user"
    }
}
