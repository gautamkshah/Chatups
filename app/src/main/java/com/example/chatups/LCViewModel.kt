package com.example.chatups

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chatups.data.CHATS
import com.example.chatups.data.ChatData
import com.example.chatups.data.ChatUser
import com.example.chatups.data.Events
import com.example.chatups.data.MESSAGE
import com.example.chatups.data.Message
import com.example.chatups.data.STATUS
import com.example.chatups.data.Status
import com.example.chatups.data.USER_NODE
import com.example.chatups.data.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore = Firebase.firestore,
    var storage: FirebaseStorage = Firebase.storage,
) : ViewModel() {
    val inProgressStatus = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    var inProgress = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Events<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    val status = mutableStateOf<List<Status>>(listOf())

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Cannot retrieve Chats")
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timestamp }
                    inProgressChatMessage.value = false
                }
            }
    }

    fun depopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener?.remove()
    }

    fun populateChats() {
        inProcessChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve Chats")
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                inProcessChats.value = false
            }
        }
    }

    fun onSendReply(ChatID: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = com.example.chatups.data.Message(userData.value?.userId, message, time)
        db.collection(CHATS).document(ChatID).collection(MESSAGE).document().set(msg)
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        }
        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, number)
                        //Log.d("TAG", "signUp: success")
                    } else {
                        handleException(it.exception, customMessage = "Sign up failed")
                    }
                }
            } else {

                handleException(customMessage = "Number already exists")
                inProgress.value = false
            }
        }.addOnFailureListener {
            handleException(it, "Cannot retrieve User")
        }
    }

    fun loginIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        }else{
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                signIn.value = true
                inProgress.value = false
                auth.currentUser?.uid?.let { getUserData(it) }
                //Log.d("TAG", "signUp: success")
            } else {
                handleException(it.exception, customMessage = "Sign up failed")
            }
        }   }
    }

    fun createOrUpdateProfile(name: String? = null, number: String? = null, imageUrl: String? = null, ) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    inProgress.value = false
                    db.collection(USER_NODE).document(uid).update(userData.toMap())

                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid)
                }
            }.addOnFailureListener {

                handleException(it, "Cannot retrieve User")
            }
        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve User")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                populateChats()
                populateStatuses()
            }
        }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("LiveChatApp", "LiveChat exception:", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage
        eventMutableState.value = Events(message)
        inProgress.value = false

    }

    fun uploadProfileImage(uri: Uri) {

        uploadImage(uri) {
            Log.d("TAAG", "uploadProfileImage: ")
            createOrUpdateProfile(imageUrl = it.toString())

        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {

        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            inProgress.value = false
        }.addOnFailureListener {
            //  inProgress.value = false
            handleException(it)

        }
    }

    fun logout() {
        auth.signOut()
        signIn.value = false
        userData.value = null
        depopulateMessages()
        currentChatMessageListener = null
        eventMutableState.value = Events("Logout Successful")
    }

    fun addChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Please enter a valid number")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ), Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "User does not exist")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id, ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ), ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)

                            }
                        }.addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chat already exists")

                }
            }
        }
    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())

        }
    }

    fun createStatus(imageurl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number
            ), imageurl, System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(newStatus)
    }

    fun populateStatuses() {
        val timeDlta = 24L * 60 * 60 * 1000
        val cutoff = System.currentTimeMillis() - timeDlta
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)

            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve Chats")
            }
            if (value != null) {
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach {
                    if (it.user1.userId == userData.value?.userId) {
                        currentConnections.add(it.user2.userId)
                    } else {
                        currentConnections.add(it.user1.userId)
                    }
                }
                db.collection(STATUS).whereGreaterThan("timestamp", cutoff)
                    .whereIn("user.userId", currentConnections)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            handleException(error, "Cannot retrieve Chats")
                        }
                        if (value != null) {
                            status.value =
                                value.toObjects<Status>().sortedByDescending { it.timestamp }
                            inProgressStatus.value = false
                        }
                    }
            }
        }
    }

}




