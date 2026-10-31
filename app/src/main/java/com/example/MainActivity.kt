package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.PhysioRepository
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.ExerciseDefinition
import com.example.model.Patient
import com.example.model.Prescription
import com.example.ui.components.AppTopHeader
import com.example.ui.doctor.DoctorDashboardScreen
import com.example.ui.doctor.DoctorExerciseLibraryScreen
import com.example.ui.doctor.DoctorPatientDetailScreen
import com.example.ui.doctor.DoctorPrescriptionDialog
import com.example.ui.patient.PatientCameraSessionScreen
import com.example.ui.patient.PatientDashboardScreen
import com.example.ui.patient.PatientExerciseDetailScreen
import com.example.ui.theme.MyApplicationTheme

sealed interface Screen {
  // Doctor screens
  object DoctorDashboard : Screen
  data class DoctorPatientDetail(val patient: Patient) : Screen
  object DoctorExerciseLibrary : Screen

  // Patient screens
  object PatientDashboard : Screen
  data class PatientExerciseDetail(val prescription: Prescription, val exercise: ExerciseDefinition) : Screen
  data class PatientCameraSession(
    val prescription: Prescription,
    val exercise: ExerciseDefinition,
    val voiceEnabled: Boolean
  ) : Screen
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        PhysioCareApp()
      }
    }
  }
}

@Composable
fun PhysioCareApp() {
  val currentRole by PhysioRepository.currentRole.collectAsState()
  val language by PhysioRepository.language.collectAsState()
  val patients by PhysioRepository.patients.collectAsState()
  val prescriptions by PhysioRepository.prescriptions.collectAsState()
  val sessionReports by PhysioRepository.sessionReports.collectAsState()

  // Active patient for patient mode
  val activePatient = PhysioRepository.getSelectedPatient()

  var currentScreen by remember { mutableStateOf<Screen>(Screen.PatientDashboard) }
  var prescribingExercise by remember { mutableStateOf<ExerciseDefinition?>(null) }

  // Handle system back button for sub-screens
  BackHandler(enabled = currentScreen !is Screen.DoctorDashboard && currentScreen !is Screen.PatientDashboard) {
    currentScreen = if (currentRole == AppRole.DOCTOR) {
      Screen.DoctorDashboard
    } else {
      Screen.PatientDashboard
    }
  }

  Scaffold(
    topBar = {
      // Don't show top bar during full-screen AI camera workout session
      if (currentScreen !is Screen.PatientCameraSession) {
        AppTopHeader(
          currentRole = currentRole,
          currentLanguage = language,
          onToggleRole = {
            val newRole = if (currentRole == AppRole.DOCTOR) AppRole.PATIENT else AppRole.DOCTOR
            PhysioRepository.setRole(newRole)
            currentScreen = if (newRole == AppRole.DOCTOR) Screen.DoctorDashboard else Screen.PatientDashboard
          },
          onToggleLanguage = {
            PhysioRepository.toggleLanguage()
          }
        )
      }
    },
    modifier = Modifier
      .fillMaxSize()
      .testTag("physiocare_scaffold")
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (val screen = currentScreen) {
        // --- DOCTOR FLOWS ---
        is Screen.DoctorDashboard -> {
          DoctorDashboardScreen(
            language = language,
            onPatientSelected = { patient ->
              PhysioRepository.selectPatient(patient.id)
              currentScreen = Screen.DoctorPatientDetail(patient)
            },
            onOpenExerciseLibrary = {
              currentScreen = Screen.DoctorExerciseLibrary
            }
          )
        }

        is Screen.DoctorPatientDetail -> {
          val patientRx = prescriptions.filter { it.patientId == screen.patient.id }
          val patientReports = sessionReports.filter { it.patientId == screen.patient.id }

          DoctorPatientDetailScreen(
            patient = screen.patient,
            prescriptions = patientRx,
            reports = patientReports,
            language = language,
            onBack = { currentScreen = Screen.DoctorDashboard },
            onPrescribeClick = {
              // Open library with intent to prescribe to this patient
              currentScreen = Screen.DoctorExerciseLibrary
            }
          )
        }

        is Screen.DoctorExerciseLibrary -> {
          DoctorExerciseLibraryScreen(
            language = language,
            onPrescribeExercise = { exercise ->
              prescribingExercise = exercise
            }
          )
        }

        // --- PATIENT FLOWS ---
        is Screen.PatientDashboard -> {
          val patientRx = prescriptions.filter { it.patientId == activePatient.id }

          PatientDashboardScreen(
            patient = activePatient,
            prescriptions = patientRx,
            language = language,
            onStartExercise = { rx, ex ->
              currentScreen = Screen.PatientExerciseDetail(rx, ex)
            }
          )
        }

        is Screen.PatientExerciseDetail -> {
          PatientExerciseDetailScreen(
            prescription = screen.prescription,
            exercise = screen.exercise,
            language = language,
            onBack = { currentScreen = Screen.PatientDashboard },
            onLaunchCamera = { voiceEnabled ->
              currentScreen = Screen.PatientCameraSession(
                prescription = screen.prescription,
                exercise = screen.exercise,
                voiceEnabled = voiceEnabled
              )
            }
          )
        }

        is Screen.PatientCameraSession -> {
          PatientCameraSessionScreen(
            patient = activePatient,
            prescription = screen.prescription,
            exercise = screen.exercise,
            initialVoiceEnabled = screen.voiceEnabled,
            language = language,
            onSessionFinished = {
              currentScreen = Screen.PatientDashboard
            }
          )
        }
      }

      // Doctor Prescription Builder Dialog overlay
      prescribingExercise?.let { exercise ->
        DoctorPrescriptionDialog(
          exercise = exercise,
          patients = patients,
          initialPatientId = activePatient.id,
          language = language,
          onDismiss = { prescribingExercise = null },
          onPrescriptionSaved = {
            prescribingExercise = null
            // Navigate back to doctor dashboard to see new prescription in action
            currentScreen = Screen.DoctorDashboard
          }
        )
      }
    }
  }
}
