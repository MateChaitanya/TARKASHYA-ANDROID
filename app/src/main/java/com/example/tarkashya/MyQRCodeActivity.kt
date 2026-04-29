package com.example.tarkashya

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class MyQRCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_qrcode)

        val ivQrCode = findViewById<ImageView>(R.id.ivMyQrCode)
        val tvName = findViewById<TextView>(R.id.tvQrName)
        val tvId = findViewById<TextView>(R.id.tvQrId)

        // Get details passed from MainActivity
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val userId = intent.getStringExtra("USER_ID") ?: "0000"

        tvName.text = userName
        tvId.text = "TKS-$userId"

        // Generate QR Code containing the User ID
        val bitmap = generateQRCode("TARKASHYA_USER_$userId")
        ivQrCode.setImageBitmap(bitmap)
    }

    private fun generateQRCode(content: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}