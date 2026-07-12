package com.bankapp.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


object KeystoreManager {

    private const val KEY_ALIAS = "bankapp_encryption_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    
    private lateinit var keyStore: KeyStore

    init {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            Log.d("Keystore", "-keystore inicializado-")
        } catch (e: Exception) {
            Log.e("Keystore", "-erro init keystore: ${e.message}--")
        }
    }


    fun getOrCreateKey(): SecretKey {
        Log.d("Keystore", "--- 1 ---")
        
        val existingKey = getKey()
        if (existingKey != null) {
            Log.d("Keystore", "--- 2 ---")
            return existingKey
        }

        Log.d("Keystore", "--- 3 ---")
        return createKey()
    }

    private fun getKey(): SecretKey? {
        return try {
            Log.d("Keystore", "--buscando clave")
            val entry = keyStore.getEntry(KEY_ALIAS, null)
            if (entry is KeyStore.SecretKeyEntry) {
                Log.d("Keystore", "--- clave encontrada")
                entry.secretKey
            } else {
                Log.d("Keystore", "--- no encontro clave")
                null
            }
        } catch (e: Exception) {
            Log.e("Keystore", "-error ${e.message} ---")
            null
        }
    }

    private fun createKey(): SecretKey {
        Log.d("Keystore", "--generando nueva clav")
        
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        Log.d("Keystore", "--- clave generada correctamente ---")
        
        return keyGenerator.generateKey()
    }
}