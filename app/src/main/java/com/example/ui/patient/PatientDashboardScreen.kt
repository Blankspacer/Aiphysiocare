package com.example.ui.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PhysioRepository
import com.example.model.AppLanguage
import com.example.model.ExerciseDefinition
import com.example.model.Patient
import com.example.model.Prescription
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.PainVasSelector
import com.example.ui.components.SafetyAlertBanner
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioNavy
import com.example.ui.theme.PhysioSuccess
import com.example.ui.theme.PhysioTealLight
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization

@Composable
fun PatientDashboardScreen(
  patient: Patient,
  prescriptions: List<Prescription>,
  language: AppLanguage,
  onStartExercise: (Prescription, ExerciseDefinition) -> Unit,
  modifier: Modifier = Modifier
) {
  val reports by PhysioRepository.sessionReports.collectAsState()
  val patientReports = reports.filter { it.patientId == patient.id }
  var currentPainScore by remember { mutableIntStateOf(1) }
  var painLoggedMsg by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize()
  ) {
    // Welcome Banner
    item {
      HeroBannerCard(
        title = "${if (language == AppLanguage.EN) "Welcome back, " else "স্বাগতম, "}${if (language == AppLanguage.EN) patient.name else patient.nameBn}",
        subtitle = if (language == AppLanguage.EN) {
          "Condition: ${patient.condition} • Supervised by Dr. Sarah Chowdhury"
        } else {
          "চিকিৎসা: ${patient.conditionBn} • তত্ত্বাবধানে: ডাঃ সারাহ চৌধুরী"
        }
      )
    }

    // Daily Compliance & Streak Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(16.dp)
        ) {
          Column {
            Text(
              text = if (language == AppLanguage.EN) "Rehabilitation Progress" else "পুনর্বাসনের অগ্রগতি",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${prescriptions.size} Prescribed Exercises • 6 Day Streak!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(PhysioMintAccent.copy(alpha = 0.15f))
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${patient.complianceRate}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PhysioSuccess
              )
              Text(text = "Score", fontSize = 9.sp, color = PhysioTealPrimary)
            }
          }
        }
      }
    }

    // Safety Banner
    item {
      SafetyAlertBanner(language = language)
    }

    // Daily Pain VAS Check-in
    item {
      Column {
        PainVasSelector(
          selectedScore = currentPainScore,
          onScoreSelected = { score ->
            currentPainScore = score
            PhysioRepository.logPain(patient.id, score, "Patient daily check-in")
            painLoggedMsg = if (language == AppLanguage.EN) "Pain level recorded (VAS $score)" else "ব্যথার মাত্রা সংরক্ষিত হয়েছে ($score)"
          },
          language = language
        )
        if (painLoggedMsg != null) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = painLoggedMsg!!,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PhysioSuccess,
            modifier = Modifier.padding(start = 8.dp)
          )
        }
      }
    }

    // Today's Prescribed Exercises Header
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = Localization.get("today_schedule", language),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PhysioCyanContainer
        ) {
          Text(
            text = "${prescriptions.size} Assigned",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PhysioTealPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Prescribed Exercises Cards
    items(prescriptions, key = { it.id }) { rx ->
      val exercise = PhysioRepository.exerciseCatalog.find { it.id == rx.exerciseId }
      val isCompletedToday = patientReports.any { it.prescriptionId == rx.id && (System.currentTimeMillis() - it.timestamp < 43200000L) }

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isCompletedToday) PhysioSuccess.copy(alpha = 0.15f) else PhysioTealPrimary.copy(alpha = 0.15f))
              ) {
                Icon(
                  imageVector = if (isCompletedToday) Icons.Default.CheckCircle else Icons.Default.FitnessCenter,
                  contentDescription = null,
                  tint = if (isCompletedToday) PhysioSuccess else PhysioTealPrimary
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = exercise?.let { if (language == AppLanguage.EN) it.name else it.nameBn } ?: rx.exerciseId,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "${rx.sets} sets • ${rx.repetitions} reps • ${if (language == AppLanguage.EN) rx.frequency else rx.frequencyBn}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isCompletedToday) PhysioSuccess.copy(alpha = 0.15f) else PhysioWarning.copy(alpha = 0.15f)
            ) {
              Text(
                text = if (isCompletedToday) Localization.get("completed", language) else Localization.get("pending", language),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompletedToday) PhysioSuccess else PhysioWarning,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          if (rx.customPrecautions.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
              ) {
                Text(
                  text = "Doctor: ${rx.customPrecautions}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              if (exercise != null) {
                onStartExercise(rx, exercise)
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isCompletedToday) PhysioTealLight else PhysioTealPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("start_exercise_button_${rx.id}")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isCompletedToday) {
                if (language == AppLanguage.EN) "Repeat with AI Camera" else "পুনরায় এআই ক্যামেরা দিয়ে করুন"
              } else {
                Localization.get("start_ai_camera", language)
              },
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Recent Exercise History Section
    if (patientReports.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = if (language == AppLanguage.EN) "Recent AI Session Feedback" else "সাম্প্রতিক এআই সেশন পর্যবেক্ষণ",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      items(patientReports.take(3), key = { it.id }) { report ->
        val exercise = PhysioRepository.exerciseCatalog.find { it.id == report.exerciseId }
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = exercise?.let { if (language == AppLanguage.EN) it.name else it.nameBn } ?: report.exerciseId,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Score: ${report.accuracyScore}%",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (report.accuracyScore >= 80) PhysioSuccess else PhysioWarning
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (language == AppLanguage.EN) report.aiObservation else report.aiObservationBn,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            if (report.doctorFeedback != null) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Doctor Note: ${report.doctorFeedback}",
                style = MaterialTheme.typography.labelSmall,
                color = PhysioSuccess,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}
