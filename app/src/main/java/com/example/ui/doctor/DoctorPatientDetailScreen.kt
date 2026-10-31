package com.example.ui.doctor

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.ExerciseSessionReport
import com.example.model.Patient
import com.example.model.Prescription
import com.example.ui.components.ComplianceBadge
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioError
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioSuccess
import com.example.ui.theme.PhysioTealLight
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization

@Composable
fun DoctorPatientDetailScreen(
  patient: Patient,
  prescriptions: List<Prescription>,
  reports: List<ExerciseSessionReport>,
  language: AppLanguage,
  onBack: () -> Unit,
  onPrescribeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var doctorFeedbackText by remember { mutableStateOf("") }
  var feedbackSubmittedMsg by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize()
  ) {
    // Header
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("patient_detail_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = if (language == AppLanguage.EN) patient.name else patient.nameBn,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "${patient.gender}, ${patient.age} years • ${patient.phone}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
          )
        }
      }
    }

    // Patient Medical Card
    item {
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
            Text(
              text = if (language == AppLanguage.EN) "Clinical Diagnosis" else "চিকিৎসা নির্ণয়",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = PhysioTealPrimary
            )
            ComplianceBadge(rate = patient.complianceRate)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = if (language == AppLanguage.EN) patient.condition else patient.conditionBn,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "${if (language == AppLanguage.EN) "Affected Joint: " else "আক্রান্ত অঙ্গ: "}${if (language == AppLanguage.EN) patient.affectedJoint else patient.affectedJointBn}",
            style = MaterialTheme.typography.bodyMedium,
            color = PhysioTealLight
          )

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = PhysioCyanContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = Localization.get("doctor_notes", language),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = PhysioTealPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (language == AppLanguage.EN) patient.doctorNotes else patient.doctorNotesBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // Active Prescriptions Header & List
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "${Localization.get("prescriptions", language)} (${prescriptions.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Button(
          onClick = onPrescribeClick,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("add_prescription_btn")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = Localization.get("prescribe_exercise", language), fontSize = 12.sp)
        }
      }
    }

    items(prescriptions, key = { it.id }) { rx ->
      val exercise = PhysioRepository.exerciseCatalog.find { it.id == rx.exerciseId }
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(14.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(PhysioMintAccent.copy(alpha = 0.15f))
          ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = PhysioMintAccent)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = exercise?.let { if (language == AppLanguage.EN) it.name else it.nameBn } ?: rx.exerciseId,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${rx.sets} sets x ${rx.repetitions} reps • ${if (language == AppLanguage.EN) rx.frequency else rx.frequencyBn} (${rx.durationDays}d)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            if (rx.customPrecautions.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Note: ${rx.customPrecautions}",
                style = MaterialTheme.typography.labelSmall,
                color = PhysioWarning
              )
            }
          }
        }
      }
    }

    // AI Movement Reports Section
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "${Localization.get("reports", language)} & AI Telemetry",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    }

    if (reports.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = if (language == AppLanguage.EN) "No exercise sessions recorded yet." else "এখনও কোনো সেশন সম্পন্ন হয়নি।",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
          )
        }
      }
    } else {
      items(reports, key = { it.id }) { report ->
        val exercise = PhysioRepository.exerciseCatalog.find { it.id == report.exerciseId }
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (report.flaggedForDoctor) PhysioWarning.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
          ),
          border = if (report.flaggedForDoctor) androidx.compose.foundation.BorderStroke(1.dp, PhysioWarning) else null,
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
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              if (report.flaggedForDoctor) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = PhysioWarning.copy(alpha = 0.2f)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = PhysioWarning, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (language == AppLanguage.EN) "Needs Review" else "পর্যালোচনা প্রয়োজন",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = PhysioWarning
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metrics row
            Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column {
                Text(text = "${report.completedReps}/${report.targetReps}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = Localization.get("reps", language), fontSize = 10.sp, color = Color.Gray)
              }
              Column {
                Text(text = "${report.avgRomDegrees.toInt()}° (Target ${report.targetRomDegrees.toInt()}°)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = Localization.get("target_rom", language), fontSize = 10.sp, color = Color.Gray)
              }
              Column {
                Text(text = "${report.accuracyScore}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (report.accuracyScore >= 80) PhysioSuccess else PhysioWarning)
                Text(text = "AI Accuracy", fontSize = 10.sp, color = Color.Gray)
              }
              Column {
                Text(text = "VAS ${report.painLevel}/10", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (report.painLevel > 3) PhysioError else PhysioSuccess)
                Text(text = "Pain Logged", fontSize = 10.sp, color = Color.Gray)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Observation
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "AI Motion Analysis:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = PhysioTealPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = if (language == AppLanguage.EN) report.aiObservation else report.aiObservationBn,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (report.flagReason != null) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Flag: ${report.flagReason}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PhysioError
                  )
                }
              }
            }

            if (report.doctorFeedback != null) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Doctor Note: ${report.doctorFeedback}",
                style = MaterialTheme.typography.bodySmall,
                color = PhysioSuccess,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }

    // Direct Doctor Clinical Feedback input
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = if (language == AppLanguage.EN) "Send Clinical Feedback to Patient" else "রোগীকে পরামর্শ বার্তা পাঠান",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = doctorFeedbackText,
            onValueChange = { doctorFeedbackText = it },
            placeholder = {
              Text(
                if (language == AppLanguage.EN) "e.g. Good steady reps today. Remember to ice knee for 10 min."
                else "উদাঃ আজ খুব ভালো ব্যায়াম হয়েছে। হাঁটুতে ১০ মিনিট বরফ সেক দিন।"
              )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              if (doctorFeedbackText.isNotBlank()) {
                feedbackSubmittedMsg = if (language == AppLanguage.EN) "Clinical note sent to patient!" else "রোগীর কাছে বার্তা পাঠানো হয়েছে!"
                doctorFeedbackText = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End)
          ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (language == AppLanguage.EN) "Send Note" else "পাঠান")
          }
          if (feedbackSubmittedMsg != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = feedbackSubmittedMsg!!,
              color = PhysioSuccess,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(50.dp))
    }
  }
}
