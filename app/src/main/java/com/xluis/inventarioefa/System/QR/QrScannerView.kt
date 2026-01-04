
package com.xluis.inventarioefa.System.QR

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.xluis.inventarioefa.System.Camera.CameraManager

@Composable
fun QrScannerView(
    cameraManager: CameraManager,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier,
    onQrDetected: (String) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                cameraManager.startQrScanner(
                    lifecycleOwner = lifecycleOwner,
                    previewView = this
                ) { qrCode ->
                    onQrDetected(qrCode)
                }
            }
        },
        modifier = modifier
    )
}
