package com.example.ui.doctor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.BodyCategory
import com.example.model.ExerciseDefinition
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioTealLight
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoctorExerciseLibraryScreen(
  language: AppLanguage,
  onPrescribeExercise: (ExerciseDefinition) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf<BodyCategory?>(null) }
  val exercises = remember(selectedCategory) {
    if (selectedCategory == null) {
      PhysioRepository.exerciseCatalog
    } else {
      PhysioRepository.exerciseCatalog.filter { it.category == selectedCategory }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = Localization.get("exercise_library", language),
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
    Text(
      text = if (language == AppLanguage.EN) {
        "Browse verified physiotherapy rehabilitation protocols across 5 anatomical categories."
      } else {
        "৫টি শারীরিক বিভাগের অনুমোদিত ফিজিওথেরাপি পুনর্বাসন ব্যায়ামসমূহ।"
      },
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Category Filter Chips
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      FilterChip(
        selected = selectedCategory == null,
        onClick = { selectedCategory = null },
        label = { Text(Localization.get("filter_all", language), fontSize = 12.sp) },
        leadingIcon = if (selectedCategory == null) {
          { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else null
      )
      BodyCategory.values().forEach { cat ->
        FilterChip(
          selected = selectedCategory == cat,
          onClick = { selectedCategory = cat },
          label = {
            Text(
              if (language == AppLanguage.EN) cat.displayName else cat.displayNameBn,
              fontSize = 12.sp
            )
          },
          leadingIcon = if (selectedCategory == cat) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
          } else null
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(bottom = 80.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(exercises, key = { it.id }) { exercise ->
        ExerciseCard(
          exercise = exercise,
          language = language,
          onPrescribe = { onPrescribeExercise(exercise) }
        )
      }
    }
  }
}

@Composable
fun ExerciseCard(
  exercise: ExerciseDefinition,
  language: AppLanguage,
  onPrescribe: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PhysioCyanContainer
        ) {
          Text(
            text = if (language == AppLanguage.EN) exercise.category.displayName else exercise.category.displayNameBn,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PhysioTealPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Target ROM: ${exercise.targetAngleMin.toInt()}° - ${exercise.targetAngleMax.toInt()}°",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = if (language == AppLanguage.EN) exercise.name else exercise.nameBn,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = "${if (language == AppLanguage.EN) "Target: " else "অঙ্গ: "}${if (language == AppLanguage.EN) exercise.targetJoint else exercise.targetJointBn}",
        style = MaterialTheme.typography.bodySmall,
        color = PhysioTealLight
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (language == AppLanguage.EN) exercise.startingPosition else exercise.startingPositionBn,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Protocol Stats
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        ProtocolPill(label = Localization.get("reps", language), value = "${exercise.defaultReps}")
        ProtocolPill(label = Localization.get("sets", language), value = "${exercise.defaultSets}")
        ProtocolPill(
          label = if (language == AppLanguage.EN) "Hold" else "ধরে রাখুন",
          value = "${exercise.holdDurationSec}s"
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = onPrescribe,
        colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("prescribe_button_${exercise.id}")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = Localization.get("prescribe_exercise", language),
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun ProtocolPill(label: String, value: String) {
  Column {
    Text(
      text = value,
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
  }
}
