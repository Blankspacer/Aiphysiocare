package com.example.ui.doctor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PhysioRepository
import com.example.model.AppLanguage
import com.example.model.ExerciseDefinition
import com.example.model.Patient
import com.example.model.Prescription
import com.example.ui.theme.PhysioTealPrimary
import com.example.util.Localization
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoctorPrescriptionDialog(
  exercise: ExerciseDefinition,
  patients: List<Patient>,
  initialPatientId: String,
  language: AppLanguage,
  onDismiss: () -> Unit,
  onPrescriptionSaved: (Prescription) -> Unit
) {
  var selectedPatientId by remember { mutableStateOf(initialPatientId) }
  var reps by remember { mutableIntStateOf(exercise.defaultReps) }
  var sets by remember { mutableIntStateOf(exercise.defaultSets) }
  val frequencies = listOf("Once Daily", "Twice Daily", "3x Weekly")
  val frequenciesBn = listOf("দিনে একবার", "দিনে দুইবার", "সপ্তাহে ৩ দিন")
  var selectedFreqIndex by remember { mutableIntStateOf(1) } // Twice Daily default
  val durations = listOf(7, 14, 21, 30)
  var selectedDuration by remember { mutableIntStateOf(14) }
  var therapistNotes by remember {
    mutableStateOf(
      if (language == AppLanguage.EN) {
        "Hold terminal extension for full 2s. Stop if sharp pain VAS > 4."
      } else {
        "সর্বোচ্চ অবস্থায় ২ সেকেন্ড ধরে রাখুন। ব্যথা ৪ এর বেশি হলে থামুন।"
      }
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = Localization.get("prescribe_exercise", language),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = if (language == AppLanguage.EN) exercise.name else exercise.nameBn,
            style = MaterialTheme.typography.bodySmall,
            color = PhysioTealPrimary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Patient Selector
        Text(
          text = if (language == AppLanguage.EN) "Select Patient:" else "রোগী নির্বাচন করুন:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          patients.forEach { patient ->
            val isSelected = patient.id == selectedPatientId
            FilterChip(
              selected = isSelected,
              onClick = { selectedPatientId = patient.id },
              label = {
                Text(
                  text = if (language == AppLanguage.EN) patient.name else patient.nameBn,
                  fontSize = 12.sp
                )
              },
              leadingIcon = if (isSelected) {
                { Icon(Icons.Default.Check, contentDescription = null) }
              } else null
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reps Stepper / Selector
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "${Localization.get("reps", language)}: $reps",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
          )
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(8, 10, 12, 15).forEach { r ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (reps == r) PhysioTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clickable { reps = r }
                  .padding(2.dp)
              ) {
                Text(
                  text = "$r",
                  color = if (reps == r) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sets Stepper
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "${Localization.get("sets", language)}: $sets",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
          )
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(2, 3, 4).forEach { s ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (sets == s) PhysioTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clickable { sets = s }
                  .padding(2.dp)
              ) {
                Text(
                  text = "$s",
                  color = if (sets == s) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Frequency Chips
        Text(
          text = if (language == AppLanguage.EN) "Frequency:" else "কতবার করবেন:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          frequencies.forEachIndexed { idx, freq ->
            FilterChip(
              selected = selectedFreqIndex == idx,
              onClick = { selectedFreqIndex = idx },
              label = {
                Text(
                  text = if (language == AppLanguage.EN) freq else frequenciesBn[idx],
                  fontSize = 11.sp
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Duration Chips
        Text(
          text = if (language == AppLanguage.EN) "Program Duration:" else "মেয়াদ:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          durations.forEach { days ->
            FilterChip(
              selected = selectedDuration == days,
              onClick = { selectedDuration = days },
              label = {
                Text(
                  text = if (language == AppLanguage.EN) "$days Days" else "$days দিন",
                  fontSize = 11.sp
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Therapist Instructions / Precautions Field
        OutlinedTextField(
          value = therapistNotes,
          onValueChange = { therapistNotes = it },
          label = { Text(Localization.get("doctor_notes", language)) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val rx = Prescription(
            id = UUID.randomUUID().toString(),
            patientId = selectedPatientId,
            exerciseId = exercise.id,
            repetitions = reps,
            sets = sets,
            frequency = frequencies[selectedFreqIndex],
            frequencyBn = frequenciesBn[selectedFreqIndex],
            durationDays = selectedDuration,
            customPrecautions = therapistNotes,
            customPrecautionsBn = therapistNotes,
            prescribedAt = "Just now",
            isActive = true
          )
          PhysioRepository.addPrescription(rx)
          onPrescriptionSaved(rx)
        },
        colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("publish_prescription_button")
      ) {
        Text(
          text = if (language == AppLanguage.EN) "Publish to Patient" else "প্রেসক্রিপশন প্রকাশ করুন",
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(Localization.get("cancel", language))
      }
    }
  )
}
