package com.example.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.model.AppLanguage
import com.example.model.BodyCategory
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioError
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioNavy
import com.example.ui.theme.PhysioSuccess
import com.example.ui.theme.PhysioTealLight
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import kotlin.math.cos
import kotlin.math.sin

data class PoseLandmark(val x: Float, val y: Float, val name: String)

@Composable
fun CameraPoseTrackerView(
  currentAngle: Float,
  targetAngleMin: Float,
  targetAngleMax: Float,
  category: BodyCategory,
  feedbackText: String,
  feedbackColor: Color,
  isCameraActive: Boolean,
  onToggleCamera: () -> Unit,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
    // 1. CameraX PreviewView or Darkened Background
    if (isCameraActive) {
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
          }
          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
              it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
              cameraProvider.unbindAll()
              cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview
              )
            } catch (exc: Exception) {
              try {
                cameraProvider.bindToLifecycle(
                  lifecycleOwner,
                  CameraSelector.DEFAULT_BACK_CAMERA,
                  preview
                )
              } catch (e: Exception) {
                // Fallback to simulation canvas if device has no physical camera
              }
            }
          }, ContextCompat.getMainExecutor(ctx))
          previewView
        },
        modifier = Modifier.fillMaxSize().testTag("camera_preview_view")
      )
    } else {
      // Clean high-tech motion backdrop
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF0F172A), Color(0xFF020617))
            )
          )
      )
    }

    // 2. Pose skeleton wireframe & joint angle arcs overlay
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .testTag("pose_tracking_canvas")
    ) {
      val w = size.width
      val h = size.height

      // Target joint angle normalized progress (0.0 to 1.0)
      val inTargetZone = currentAngle >= targetAngleMin && currentAngle <= targetAngleMax
      val jointColor = if (inTargetZone) PhysioSuccess else PhysioMintAccent
      val boneColor = Color.White.copy(alpha = 0.75f)

      // Draw Guide Box
      drawRoundRect(
        color = PhysioCyanContainer.copy(alpha = 0.25f),
        topLeft = Offset(w * 0.12f, h * 0.12f),
        size = Size(w * 0.76f, h * 0.76f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f),
        style = Stroke(width = 2.dp.toPx())
      )

      // Render anatomical landmarks based on exercise category
      when (category) {
        BodyCategory.KNEE -> {
          // Seated knee posture wireframe
          val hip = Offset(w * 0.35f, h * 0.45f)
          val knee = Offset(w * 0.52f, h * 0.50f)
          // Compute ankle position based on real currentAngle (90° bent to 175° straight)
          val rad = Math.toRadians((180 - currentAngle).toDouble())
          val legLength = h * 0.24f
          val ankle = Offset(
            (knee.x + legLength * sin(rad)).toFloat(),
            (knee.y + legLength * cos(rad)).toFloat()
          )
          val shoulder = Offset(w * 0.33f, h * 0.26f)
          val head = Offset(w * 0.33f, h * 0.18f)

          // Draw skeleton bones
          drawLine(boneColor, head, shoulder, strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
          drawLine(boneColor, shoulder, hip, strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
          drawLine(boneColor, hip, knee, strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
          drawLine(jointColor, knee, ankle, strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)

          // Joints
          drawCircle(Color.White, radius = 7.dp.toPx(), center = shoulder)
          drawCircle(Color.White, radius = 8.dp.toPx(), center = hip)
          drawCircle(jointColor, radius = 12.dp.toPx(), center = knee)
          drawCircle(Color.White, radius = 8.dp.toPx(), center = ankle)
          drawCircle(PhysioTealLight, radius = 14.dp.toPx(), center = head)

          // Joint Angle Arc around Knee
          drawArc(
            color = jointColor.copy(alpha = 0.8f),
            startAngle = 10f,
            sweepAngle = (currentAngle - 90f).coerceIn(5f, 90f),
            useCenter = false,
            topLeft = Offset(knee.x - 36.dp.toPx(), knee.y - 36.dp.toPx()),
            size = Size(72.dp.toPx(), 72.dp.toPx()),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
          )
        }
        BodyCategory.ARM_SHOULDER -> {
          // Shoulder & Arm wireframe
          val head = Offset(w * 0.50f, h * 0.22f)
          val spine = Offset(w * 0.50f, h * 0.50f)
          val shoulder = Offset(w * 0.38f, h * 0.32f)

          val rad = Math.toRadians((currentAngle).toDouble())
          val armLength = h * 0.20f
          val elbow = Offset(
            (shoulder.x - armLength * sin(rad)).toFloat(),
            (shoulder.y - armLength * cos(rad)).toFloat()
          )
          val wrist = Offset(
            (elbow.x - (armLength * 0.8f) * sin(rad)).toFloat(),
            (elbow.y - (armLength * 0.8f) * cos(rad)).toFloat()
          )

          drawLine(boneColor, head, spine, strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
          drawLine(jointColor, shoulder, elbow, strokeWidth = 7.dp.toPx(), cap = StrokeCap.Round)
          drawLine(jointColor, elbow, wrist, strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)

          drawCircle(PhysioTealLight, radius = 14.dp.toPx(), center = head)
          drawCircle(jointColor, radius = 12.dp.toPx(), center = shoulder)
          drawCircle(Color.White, radius = 8.dp.toPx(), center = elbow)
          drawCircle(Color.White, radius = 7.dp.toPx(), center = wrist)
        }
        else -> {
          // General body wireframe
          val center = Offset(w * 0.50f, h * 0.45f)
          val head = Offset(w * 0.50f, h * 0.25f)
          drawLine(boneColor, head, center, strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
          drawCircle(PhysioTealLight, radius = 14.dp.toPx(), center = head)
          drawCircle(jointColor, radius = 12.dp.toPx(), center = center)
        }
      }
    }

    // 3. Top Overlay Controls: Camera Toggle + Joint Angle readout
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .align(Alignment.TopCenter)
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.65f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (currentAngle >= targetAngleMin) PhysioSuccess else PhysioMintAccent)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${currentAngle.toInt()}°",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Target: ${targetAngleMin.toInt()}-${targetAngleMax.toInt()}°",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }

      // Camera toggle button (Real Camera vs High-contrast Motion Simulation)
      Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.65f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
      ) {
        IconButton(
          onClick = onToggleCamera,
          modifier = Modifier.testTag("toggle_camera_source_button")
        ) {
          Icon(
            imageVector = if (isCameraActive) Icons.Default.Videocam else Icons.Default.VideocamOff,
            contentDescription = "Toggle Camera",
            tint = if (isCameraActive) PhysioMintAccent else Color.White
          )
        }
      }
    }

    // 4. Live AI Movement Feedback Banner
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = feedbackColor.copy(alpha = 0.92f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 16.dp)
        .align(Alignment.BottomCenter)
        .testTag("ai_movement_feedback_card")
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(14.dp)
      ) {
        Icon(
          imageVector = if (feedbackColor == PhysioSuccess) Icons.Default.CheckCircle else Icons.Default.Info,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = feedbackText,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }
  }
}
