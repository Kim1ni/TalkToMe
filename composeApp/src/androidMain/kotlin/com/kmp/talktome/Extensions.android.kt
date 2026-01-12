package com.kmp.talktome

import android.net.Uri
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.StorageReference
import java.io.File

actual suspend fun StorageReference.uploadByteArray(
    bytes: ByteArray
) {
    val tempFile = File.createTempFile("upload",".aac")
    tempFile.writeBytes(bytes)
    this.putFile(dev.gitlive.firebase.storage.File(Uri.fromFile(tempFile)))
}