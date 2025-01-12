package com.example.firebase.repository

import com.example.firebase.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

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

    override fun getMhs(nim: String): Flow<Mahasiswa> = callbackFlow {
        val mhsDocument = firestore.collection("Mahasiswa")
            .document(nim)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val mhs = value.toObject(Mahasiswa::class.java)!!
                    trySend(mhs)
                }
            }
        awaitClose {
            mhsDocument.remove()
        }
    }

    override suspend fun insertMhs(mahasiswa: Mahasiswa) {
        try{
            firestore.collection("mahasiswa").add(mahasiswa).await()
        }catch (e : Exception){
            throw Exception("Gagal menambahkan dara mahasiswa :${e.message}")
        }
    }

    override suspend fun deleteMhs(mahasiswa: Mahasiswa) {
        try {
            firestore.collection("mahasiswa")
                .document(mahasiswa.nim)
                .delete()
                .await()
        }catch (e : Exception){
            throw Exception("Gagal menghapus data mahasiswa :${e.message}")
        }
    }

    