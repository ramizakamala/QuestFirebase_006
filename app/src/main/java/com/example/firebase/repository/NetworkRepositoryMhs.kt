package com.example.firebase.repository

import com.example.firebase.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkRepositoryMhs(private val firestore: FirebaseFirestore
): RepositoryMhs {
    override fun getAllMhs(): Flow<List<Mahasiswa>>  = callbackFlow{
        val mhsCollection = firestore.collection("mahasiswa")
            .orderBy("nim", Query.Direction.ASCENDING)
            .addSnapshotListener{value, Error ->
                if (value !=null){
                    val mhslist = value.documents.mapNotNull {
                        it.toObject(Mahasiswa::class.java)!!
                    }
                    trySend(mhslist)
                }
            }
        awaitClose {
            mhsCollection.remove()
        }
    }

    