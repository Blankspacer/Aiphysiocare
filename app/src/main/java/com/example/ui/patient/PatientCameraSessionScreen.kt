package com.example.ui.patient

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.PhysioRepository
import com.example.model.AppLanguage
import com.example.model.ExerciseDefinition
import com.example.model.ExerciseSessionReport
import com.example.model.Patient
import com.example.model.Prescription
import com.example.ui.components.CameraPoseTrackerView
import com.example.ui.components.PainVasSelector
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioError
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioNavy
import com.example.ui.theme.PhysioSuccess
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization
import com.example.util.SpeechManager
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.math.sin

enum class MovementPhase {
  STARTING, EXTENDING, HOLDING_PEAK, RETURNING, COMPLETED
}

@Composable
fun PatientCameraSessionScreen(
  patient: Patient,
  prescription: Prescription,
  exercise: ExerciseDefinition,
  initialVoiceEnabled: Boolean,
  language: AppLanguage,
  onSessionFinished: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val speechManager = remember { SpeechManager(context) }

  DisposableEffect(Unit) {
    onDispose {
      speechManager.shutdown()
    }
  }

  // Camera Permission handling
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }
  var isCameraActive by remember { mutableStateOf(hasCameraPermission) }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
    isCameraActive = isGranted
  }

  // Camera Setup guide popup state
  var showSetupGuide by remember { mutableStateOf(true) }

  // Exercise tracking states
  var completedReps by remember { mutableIntStateOf(0) }
  var currentSet by remember { mutableIntStateOf(1) }
  var currentAngle by remember { mutableFloatStateOf(exercise.startAngle) }
  var phase by remember { mutableStateOf(MovementPhase.STARTING) }
  var holdSecondsRemaining by remember { mutableIntStateOf(exercise.holdDurationSec) }
  var feedbackText by remember {
    mutableStateOf(
      if (language == AppLanguage.EN) "Extend leg upward smoothly" else "পা ধীরে ধীরে ওপরের দিকে সোজা করুন"
    )
  }
  var feedbackColor by remember { mutableStateOf(PhysioMintAccent) }
  var isPaused by remember { mutableStateOf(false) }

  // Pain / Safety Stop dialog
  var showSafetyStopDialog by remember { mutableStateOf(false) }
  var safetyStopPainScore by remember { mutableIntStateOf(4) }
  var safetyStopNotes by remember { mutableStateOf("") }

  // Session summary dialog
  var showSummaryDialog by remember { mutableStateOf(false) }
  var sessionAccuracy by remember { mutableIntStateOf(92) }
  var recordedRomAvg by remember { mutableFloatStateOf(exercise.targetAngleMin + 4f) }

  // Launch initial permission check
  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    if (initialVoiceEnabled) {
      delay(600)
      speechManager.speak("Welcome to ${exercise.name}. Please follow camera positioning.")
    }
  }

  // Simulation Cadence Loop (Simulates human movement & tracks angles smoothly)
  LaunchedEffect(showSetupGuide, isPaused, showSafetyStopDialog, showSummaryDialog) {
    if (showSetupGuide || isPaused || showSafetyStopDialog || showSummaryDialog) return@LaunchedEffect

    var tick = 0f
    while (completedReps < prescription.repetitions) {
      delay(80) // 12.5 FPS kinematic loop
      tick += 0.12f

      // Smooth wave movement between startAngle and targetAngle
      val minA = exercise.startAngle
      val maxA = exercise.targetAngleMin + 5f
      val normalized = (sin(tick.toDouble()) + 1) / 2 // 0.0 to 1.0
      currentAngle = (minA + (maxA - minA) * normalized).toFloat()

      when {
        currentAngle >= exercise.targetAngleMin -> {
          // In peak target zone
          phase = MovementPhase.HOLDING_PEAK
          feedbackText = Localization.get("good_form", language)
          feedbackColor = PhysioSuccess
        }
        currentAngle > minA + 20f && normalized > 0.5 -> {
          phase = MovementPhase.EXTENDING
          feedbackText = Localization.get("extend_further", language)
          feedbackColor = PhysioTealPrimary
        }
        else -> {
          if (phase == MovementPhase.HOLDING_PEAK) {
            // Completed a rep cycle!
            completedReps += 1
            phase = MovementPhase.RETURNING
            feedbackText = "${Localization.get("rep_complete", language)} ($completedReps/${prescription.repetitions})"
            feedbackColor = PhysioSuccess
            speechManager.speak("Rep $completedReps")
            if (completedReps >= prescription.repetitions) {
              speechManager.speak("Great job! Set completed.")
              showSummaryDialog = true
              break
            }
          } else {
            phase = MovementPhase.STARTING
            feedbackText = Localization.get("move_slowly", language)
            feedbackColor = PhysioMintAccent
          }
        }
      }
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    // 1. Live Pose Tracker View
    CameraPoseTrackerView(
      currentAngle = currentAngle,
      targetAngleMin = exercise.targetAngleMin,
      targetAngleMax = exercise.targetAngleMax,
      category = exercise.category,
      feedbackText = feedbackText,
      feedbackColor = feedbackColor,
      isCameraActive = isCameraActive,
      onToggleCamera = {
        if (!hasCameraPermission) {
          cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
          isCameraActive = !isCameraActive
        }
      },
      language = language
    )

    // 2. Header Overlay: Rep counter, Set, and Back Button
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 60.dp)
        .align(Alignment.TopCenter)
    ) {
      IconButton(
        onClick = onSessionFinished,
        modifier = Modifier
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
          .testTag("exit_camera_session_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit", tint = Color.White)
      }

      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.65f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Text(
            text = "Rep $completedReps / ${prescription.repetitions}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PhysioTealPrimary
          ) {
            Text(
              text = "Set $currentSet/${prescription.sets}",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      IconButton(
        onClick = { speechManager.setMuted(!speechManager.isMuted()) },
        modifier = Modifier
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
      ) {
        Icon(
          imageVector = if (speechManager.isMuted()) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
          contentDescription = "Mute",
          tint = Color.White
        )
      }
    }

    // 3. Bottom Controls: Pause, Safety STOP / Pain Button
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 80.dp)
        .align(Alignment.BottomCenter)
    ) {
      // Pause Button
      Button(
        onClick = { isPaused = !isPaused },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
        shape = CircleShape,
        modifier = Modifier.size(52.dp)
      ) {
        Icon(
          imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
          contentDescription = "Pause",
          tint = Color.White
        )
      }

      // Safety STOP / Pain Button (Prominent Clinical Safety Feature)
      Button(
        onClick = {
          isPaused = true
          showSafetyStopDialog = true
          speechManager.speak("Session paused. Please record any pain for your physiotherapist.")
        },
        colors = ButtonDefaults.buttonColors(containerColor = PhysioError),
        shape = RoundedCornerShape(24.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = Modifier.testTag("safety_stop_button")
      ) {
        Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = Localization.get("safety_stop", language),
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }

  // Camera Setup Guide Modal
  if (showSetupGuide) {
    AlertDialog(
      onDismissRequest = { showSetupGuide = false },
      shape = RoundedCornerShape(20.dp),
      icon = {
        Icon(
          Icons.Default.Info,
          contentDescription = null,
          tint = PhysioTealPrimary,
          modifier = Modifier.size(36.dp)
        )
      },
      title = {
        Text(
          text = Localization.get("camera_setup_title", language),
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = Localization.get("camera_setup_desc", language),
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PhysioCyanContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "Exercise: ${if (language == AppLanguage.EN) exercise.name else exercise.nameBn}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PhysioTealPrimary
              )
              Text(
                text = "Target: ${prescription.sets} sets of ${prescription.repetitions} reps",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showSetupGuide = false
            speechManager.speak("Ready. Starting exercise tracking now.")
          },
          colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("ready_camera_button")
        ) {
          Text(Localization.get("ready_to_start", language), fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  // Safety Stop / Pain Experienced Modal
  if (showSafetyStopDialog) {
    AlertDialog(
      onDismissRequest = { showSafetyStopDialog = false },
      shape = RoundedCornerShape(20.dp),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = PhysioError)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (language == AppLanguage.EN) "Pain / Discomfort Flag" else "ব্যথা বা সমস্যা চিহ্নিত করুন",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = if (language == AppLanguage.EN) {
              "The session has stopped. Record your current sensation so Dr. Sarah Chowdhury can review and adjust your prescription."
            } else {
              "ব্যায়ামটি থামানো হয়েছে। ব্যথার মাত্রা লিখে রাখুন যাতে আপনার ফিজিওথেরাপিস্ট পর্যালোচনা করতে পারেন।"
            },
            style = MaterialTheme.typography.bodySmall
          )

          PainVasSelector(
            selectedScore = safetyStopPainScore,
            onScoreSelected = { safetyStopPainScore = it },
            language = language
          )

          OutlinedTextField(
            value = safetyStopNotes,
            onValueChange = { safetyStopNotes = it },
            placeholder = {
              Text(
                if (language == AppLanguage.EN) "Describe what you felt (e.g. sharp pinch on extension)"
                else "কি ধরণের অস্বস্তি হচ্ছে লিখুন (উদাঃ হাঁটু সোজা করতে টান লাগছে)"
              )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val report = ExerciseSessionReport(
              id = UUID.randomUUID().toString(),
              prescriptionId = prescription.id,
              patientId = patient.id,
              exerciseId = exercise.id,
              completedReps = completedReps,
              targetReps = prescription.repetitions,
              completedSets = currentSet,
              targetSets = prescription.sets,
              avgRomDegrees = currentAngle,
              targetRomDegrees = exercise.targetAngleMin,
              accuracyScore = 72,
              flaggedForDoctor = true,
              flagReason = "Patient aborted session due to VAS $safetyStopPainScore pain: $safetyStopNotes",
              aiObservation = "Session stopped early at rep $completedReps. Max angle reached was ${currentAngle.toInt()}°. Patient logged VAS $safetyStopPainScore.",
              aiObservationBn = "সেশনটি $completedReps তম রিপে থামানো হয়েছে। সর্বোচ্চ কোণ ছিল ${currentAngle.toInt()}°। ব্যথার মাত্রা ছিল $safetyStopPainScore।",
              timestamp = System.currentTimeMillis(),
              painLevel = safetyStopPainScore,
              stoppedEarly = true
            )
            PhysioRepository.recordSessionReport(report)
            showSafetyStopDialog = false
            onSessionFinished()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PhysioError),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("submit_safety_stop_report")
        ) {
          Text(
            text = if (language == AppLanguage.EN) "Save & Notify Doctor" else "সংরক্ষণ ও ডাক্তারকে জানান",
            fontWeight = FontWeight.Bold
          )
        }
      },
      dismissButton = {
        TextButton(onClick = {
          showSafetyStopDialog = false
          isPaused = false
        }) {
          Text(if (language == AppLanguage.EN) "Resume" else "চালিয়ে যান")
        }
      }
    )
  }

  // Session Completion Report Dialog
  if (showSummaryDialog) {
    AlertDialog(
      onDismissRequest = onSessionFinished,
      shape = RoundedCornerShape(20.dp),
      icon = {
        Icon(
          Icons.Default.CheckCircle,
          contentDescription = null,
          tint = PhysioSuccess,
          modifier = Modifier.size(48.dp)
        )
      },
      title = {
        Text(
          text = Localization.get("session_summary", language),
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "${if (language == AppLanguage.EN) "Exercise: " else "ব্যায়াম: "}${if (language == AppLanguage.EN) exercise.name else exercise.nameBn}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
          )

          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(text = "$completedReps / ${prescription.repetitions}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PhysioTealPrimary)
              Text(text = Localization.get("reps", language), fontSize = 11.sp, color = Color.Gray)
            }
            Column {
              Text(text = "${recordedRomAvg.toInt()}°", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PhysioSuccess)
              Text(text = "Avg ROM", fontSize = 11.sp, color = Color.Gray)
            }
            Column {
              Text(text = "$sessionAccuracy%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PhysioMintAccent)
              Text(text = "Accuracy", fontSize = 11.sp, color = Color.Gray)
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = PhysioCyanContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "AI Movement Observation:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PhysioTealPrimary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (language == AppLanguage.EN) {
                  "Excellent range of motion consistency. Maintained smooth tempo and achieved full ${recordedRomAvg.toInt()}° extension across reps."
                } else {
                  "চমৎকার মুভমেন্ট ধারাবাহিকতা। গতি স্বাভাবিক ছিল এবং গড় ${recordedRomAvg.toInt()}° কোণ অর্জিত হয়েছে।"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Text(
            text = Localization.get("session_saved", language),
            fontSize = 12.sp,
            color = PhysioSuccess,
            fontWeight = FontWeight.Bold
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val report = ExerciseSessionReport(
              id = UUID.randomUUID().toString(),
              prescriptionId = prescription.id,
              patientId = patient.id,
              exerciseId = exercise.id,
              completedReps = completedReps,
              targetReps = prescription.repetitions,
              completedSets = currentSet,
              targetSets = prescription.sets,
              avgRomDegrees = recordedRomAvg,
              targetRomDegrees = exercise.targetAngleMin,
              accuracyScore = sessionAccuracy,
              flaggedForDoctor = false,
              aiObservation = "Excellent range of motion consistency. Maintained smooth tempo and achieved full ${recordedRomAvg.toInt()}° extension.",
              aiObservationBn = "চমৎকার মুভমেন্ট ধারাবাহিকতা। গতি স্বাভাবিক ছিল এবং গড় ${recordedRomAvg.toInt()}° কোণ অর্জিত হয়েছে।",
              timestamp = System.currentTimeMillis(),
              painLevel = 1,
              stoppedEarly = false
            )
            PhysioRepository.recordSessionReport(report)
            showSummaryDialog = false
            onSessionFinished()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("confirm_session_summary_button")
        ) {
          Text(Localization.get("close", language), fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}
