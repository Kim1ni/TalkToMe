package com.kmp.talktome

import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.StorageReference

expect suspend fun StorageReference.uploadByteArray(bytes: ByteArray)