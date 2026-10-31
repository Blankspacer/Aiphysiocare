package com.example.ui.patient

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.AppLanguage
import com.example.model.ExerciseDefinition
import com.example.model.Prescription
import com.example.ui.components.SafetyAlertBanner
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioTealLight
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization

@Composable
fun PatientExerciseDetailScreen(
  prescription: Prescription,
  exercise: ExerciseDefinition,
  language: AppLanguage,
  onBack: () -> Unit,
  onLaunchCamera: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var voiceGuidanceEnabled by remember { mutableStateOf(true) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize()
  ) {
    // Header Bar
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("exercise_detail_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = if (language == AppLanguage.EN) exercise.name else exercise.nameBn,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "${if (language == AppLanguage.EN) "Target Joint: " else "অঙ্গ: "}${if (language == AppLanguage.EN) exercise.targetJoint else exercise.targetJointBn}",
            style = MaterialTheme.typography.bodySmall,
            color = PhysioTealLight
          )
        }
      }
    }

    // Prescription Summary Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = if (language == AppLanguage.EN) "Doctor's Prescribed Protocol" else "ডাক্তারের নির্ধারিত প্রেসক্রিপশন",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = PhysioTealPrimary
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            DetailPill(label = Localization.get("reps", language), value = "${prescription.repetitions}")
            DetailPill(label = Localization.get("sets", language), value = "${prescription.sets}")
            DetailPill(
              label = if (language == AppLanguage.EN) "Target Angle" else "টার্গেট কোণ",
              value = "${exercise.targetAngleMin.toInt()}°-${exercise.targetAngleMax.toInt()}°"
            )
            DetailPill(
              label = if (language == AppLanguage.EN) "Frequency" else "নিয়ম",
              value = if (language == AppLanguage.EN) prescription.frequency else prescription.frequencyBn
            )
          }

          if (prescription.customPrecautions.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PhysioCyanContainer,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = Localization.get("doctor_notes", language),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = PhysioTealPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = prescription.customPrecautions,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }

    // Starting Position Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = if (language == AppLanguage.EN) "Starting Posture & Setup" else "শুরুর অবস্থান ও প্রস্তুতি",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (language == AppLanguage.EN) exercise.startingPosition else exercise.startingPositionBn,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
          )
        }
      }
    }

    // Step-by-Step Instructions
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = if (language == AppLanguage.EN) "Movement Steps" else "ধাপসমূহ",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(10.dp))

          val instructions = if (language == AppLanguage.EN) exercise.instructions else exercise.instructionsBn
          instructions.forEachIndexed { index, step ->
            Row(
              verticalAlignment = Alignment.Top,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(PhysioTealPrimary)
              ) {
                Text(
                  text = "${index + 1}",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = step,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // Safety Alert & Voice Guidance Toggle
    item {
      SafetyAlertBanner(language = language)
      Spacer(modifier = Modifier.height(10.dp))

      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PhysioTealPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (language == AppLanguage.EN) "Voice Coaching Guidance" else "ভয়েস নির্দেশনা",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = if (language == AppLanguage.EN) "Audio feedback for reps & posture cues" else "অডিওর মাধ্যমে অঙ্গভঙ্গি সংশোধন বার্তা",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }
          Switch(
            checked = voiceGuidanceEnabled,
            onCheckedChange = { voiceGuidanceEnabled = it },
            colors = SwitchDefaults.colors(checkedThumbColor = PhysioTealPrimary)
          )
        }
      }
    }

    // Launch AI Camera Assistant CTA
    item {
      Button(
        onClick = { onLaunchCamera(voiceGuidanceEnabled) },
        colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("launch_ai_camera_button")
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = Localization.get("start_ai_camera", language),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
      Spacer(modifier = Modifier.height(50.dp))
    }
  }
}

@Composable
fun DetailPill(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = PhysioTealPrimary
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
  }
}
