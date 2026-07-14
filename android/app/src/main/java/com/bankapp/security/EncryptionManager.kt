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

    private var secretKey: SecretKey? = null

    fun init(key: SecretKey) {
        Log.d("Encrypt", "--- inicializando EncryptionManager ---")
        secretKey = key
    }

    fun encrypt(data: String): String? {
        Log.d("Encrypt", "--- cifrando: $data ---")
        
        if (secretKey == null) {
            Log.e("Encrypt", "--- clave no inicializada ---")
            return null
        }

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            val iv = cipher.iv
            
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            val result = Base64.encodeToString(combined, Base64.DEFAULT)
            Log.d("Encrypt", "--- cifrado OK: $result ---")
            
            result
        } catch (e: Exception) {
            Log.e("Encrypt", "--- error al cifrar: ${e.message} ---")
            null
        }
    }

    fun decrypt(encryptedData: String): String? {
        Log.d("Encrypt", "--- descifrando ---")
        
        if (secretKey == null) {
            Log.e("Encrypt", "--- clave no inicializada ---")
            return null
        }

        return try {
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            
            val ivLength = 12
            val iv = ByteArray(ivLength)
            System.arraycopy(combined, 0, iv, 0, ivLength)
            
            val encryptedBytes = ByteArray(combined.size - ivLength)
            System.arraycopy(combined, ivLength, encryptedBytes, 0, encryptedBytes.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val result = String(decryptedBytes, Charsets.UTF_8)
            
            Log.d("Encrypt", "--- descifrado OK: $result ---")
            
            result
        } catch (e: Exception) {
            Log.e("Encrypt", "--- error al descifrar: ${e.message} ---")
            null
        }
    }
}