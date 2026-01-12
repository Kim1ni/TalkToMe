package com.kmp.talktome

import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.StorageReference
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

@OptIn(ExperimentalForeignApi::class)
actual suspend fun StorageReference.uploadByteArray(bytes: ByteArray) {
    val nsData = bytes.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
    }
    this.putData(Data(nsData))
}