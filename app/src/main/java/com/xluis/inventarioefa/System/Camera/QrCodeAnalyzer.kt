package com.xluis.inventarioefa.System.Camera

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null && imageProxy.format == ImageFormat.YUV_420_888) {
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            if (barcode.valueType == Barcode.TYPE_TEXT || barcode.valueType == Barcode.TYPE_URL) {
                                barcode.rawValue?.let { qr ->
                                    onQrCodeScanned(qr)
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("QrCodeAnalyzer", "Error analizando QR: ${e.message}")
                    }
                    .addOnCompleteListener {
                        imageProxy.close() // siempre cerrar el frame
                    }
            } else {
                imageProxy.close()
            }
        } catch (e: Exception) {
            Log.e("QrCodeAnalyzer", "Error inesperado: ${e.message}")
            imageProxy.close()
        }
    }
}
