package com.bankapp.security

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom

object EncryptionManager {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128
    private const val IV_LENGTH = 12

    private var secretKey: SecretKey? = null

    fun init(key: SecretKey) {
        Log.d("Encrypt", "--- inicializando EncryptionManager ---")
        secretKey = key
    }

    fun encrypt(data: String): String? {
        Log.d("Encrypt", "---cifrando-")
        
        if (secretKey == null) {
            Log.e("Encrypt", "--- clave no ---")
            return null
        }

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            
            val iv = ByteArray(IV_LENGTH)
            SecureRandom().nextBytes(iv)
            
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
            
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            val result = Base64.encodeToString(combined, Base64.DEFAULT)
            Log.d("Encrypt", "--- datos cifrados bienm ---")
            Log.d("Encrypt", "---3---")
            
            result
        } catch (e: Exception) {
            Log.e("Encrypt", "--- error al cifrar: ${e.message} ---")
            null
        }
    }


    fun decrypt(encryptedData: String): String? {
        Log.d("Encrypt", "--- descifrando datos ---")
        Log.d("Encrypt", "---4---")
        
        if (secretKey == null) {
            Log.e("Encrypt", "--- clave no inicializada ---")
            return null
        }

        return try {
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            
            val iv = ByteArray(IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, iv.size)
            
            val encryptedBytes = ByteArray(combined.size - IV_LENGTH)
            System.arraycopy(combined, IV_LENGTH, encryptedBytes, 0, encryptedBytes.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val result = String(decryptedBytes, Charsets.UTF_8)
            
            Log.d("Encrypt", "--- data descifrados ok ---")
            Log.d("Encrypt", "---5---")
            
            result
        } catch (e: Exception) {
            Log.e("Encrypt", "--- error al decrypt: ${e.message} ---")
            null
        }
    }
}