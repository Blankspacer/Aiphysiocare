package com.example.ui.doctor

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.model.Patient
import com.example.ui.components.ComplianceBadge
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.StatCard
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioError
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioNavy
import com.example.ui.theme.PhysioSuccess
import com.example.ui.theme.PhysioTealLight
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization
import java.util.UUID

@Composable
fun DoctorDashboardScreen(
  language: AppLanguage,
  onPatientSelected: (Patient) -> Unit,
  onOpenExerciseLibrary: () -> Unit,
  modifier: Modifier = Modifier
) {
  val patients by PhysioRepository.patients.collectAsState()
  val prescriptions by PhysioRepository.prescriptions.collectAsState()
  val reports by PhysioRepository.sessionReports.collectAsState()
  val flaggedCount = reports.count { it.flaggedForDoctor }

  var showAddPatientDialog by remember { mutableStateOf(false) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize()
  ) {
    // Hero Banner
    item {
      HeroBannerCard(
        title = if (language == AppLanguage.EN) "Dr. Sarah Chowdhury, Senior PT" else "ডাঃ সারাহ চৌধুরী, সিনিয়র ফিজিওথেরাপিস্ট",
        subtitle = if (language == AppLanguage.EN) {
          "Remote Clinical Rehabilitation Hub • Supervise patient recovery with AI kinematics"
        } else {
          "রিমোট পুনর্বাসন ক্লিনিক • এআই মুভমেন্ট অ্যানালাইসিসের মাধ্যমে রোগীদের মনিটর করুন"
        }
      )
    }

    // Stats Grid
    item {
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        StatCard(
          label = Localization.get("active_patients", language),
          value = "${patients.size}",
          icon = Icons.Default.People,
          tint = PhysioTealPrimary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          label = Localization.get("total_sessions", language),
          value = "${reports.size}",
          icon = Icons.Default.CheckCircle,
          tint = PhysioSuccess,
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        StatCard(
          label = Localization.get("prescriptions", language),
          value = "${prescriptions.size}",
          icon = Icons.Default.Assignment,
          tint = PhysioMintAccent,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          label = Localization.get("flagged_sessions", language),
          value = "$flaggedCount",
          icon = Icons.Default.Warning,
          tint = if (flaggedCount > 0) PhysioWarning else PhysioSuccess,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Flagged Session Review Banner (If any)
    if (flaggedCount > 0) {
      item {
        val flaggedReport = reports.first { it.flaggedForDoctor }
        val flaggedPatient = patients.find { it.id == flaggedReport.patientId }
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = PhysioWarning.copy(alpha = 0.12f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, PhysioWarning),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { flaggedPatient?.let { onPatientSelected(it) } }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = PhysioWarning, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (language == AppLanguage.EN) "AI Telemetry Alert" else "এআই মুভমেন্ট অ্যালার্ট",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = PhysioWarning
              )
              Text(
                text = "${flaggedPatient?.name ?: "Patient"}: ${flaggedReport.flagReason ?: "Form deviation observed"}",
                style = MaterialTheme.typography.bodySmall
              )
            }
            TextButton(
              onClick = { flaggedPatient?.let { onPatientSelected(it) } },
              modifier = Modifier.testTag("review_flagged_session_button")
            ) {
              Text(Localization.get("view_details", language), fontWeight = FontWeight.Bold, color = PhysioWarning)
            }
          }
        }
      }
    }

    // Patients Section Header
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = Localization.get("patients", language),
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onOpenExerciseLibrary,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("open_exercise_library_btn")
          ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = Localization.get("exercise_library", language), fontSize = 12.sp)
          }

          Button(
            onClick = { showAddPatientDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("add_patient_btn")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = Localization.get("new_patient", language), fontSize = 12.sp)
          }
        }
      }
    }

    // Patient Cards List
    items(patients, key = { it.id }) { patient ->
      val patientRx = prescriptions.filter { it.patientId == patient.id }
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onPatientSelected(patient) }
          .testTag("patient_card_${patient.id}")
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
                  .clip(CircleShape)
                  .background(PhysioNavy.copy(alpha = 0.1f))
              ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PhysioNavy)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (language == AppLanguage.EN) patient.name else patient.nameBn,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "${patient.gender}, ${patient.age} y/o • ${patient.phone}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
              }
            }
            ComplianceBadge(rate = patient.complianceRate)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = if (language == AppLanguage.EN) patient.condition else patient.conditionBn,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = PhysioTealPrimary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "${patientRx.size} Active Prescriptions",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
              text = Localization.get("view_details", language),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = PhysioTealPrimary
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Add Patient Dialog
  if (showAddPatientDialog) {
    AddPatientDialog(
      language = language,
      onDismiss = { showAddPatientDialog = false },
      onPatientAdded = { newPatient ->
        PhysioRepository.addPatient(newPatient)
        showAddPatientDialog = false
        onPatientSelected(newPatient)
      }
    )
  }
}

@Composable
fun AddPatientDialog(
  language: AppLanguage,
  onDismiss: () -> Unit,
  onPatientAdded: (Patient) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var ageStr by remember { mutableStateOf("") }
  var gender by remember { mutableStateOf("Male") }
  var phone by remember { mutableStateOf("+880 1") }
  var condition by remember { mutableStateOf("") }
  var affectedJoint by remember { mutableStateOf("Right Knee") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    title = {
      Text(
        text = Localization.get("new_patient", language),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text(if (language == AppLanguage.EN) "Full Name" else "রোগীর পুরো নাম") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = ageStr,
            onValueChange = { ageStr = it },
            label = { Text(if (language == AppLanguage.EN) "Age" else "বয়স") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text(if (language == AppLanguage.EN) "Gender" else "লিঙ্গ") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text(if (language == AppLanguage.EN) "Phone Number" else "ফোন নম্বর") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = condition,
          onValueChange = { condition = it },
          label = { Text(if (language == AppLanguage.EN) "Clinical Diagnosis" else "রোগ নির্ণয়") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = affectedJoint,
          onValueChange = { affectedJoint = it },
          label = { Text(if (language == AppLanguage.EN) "Affected Joint / Limb" else "আক্রান্ত জয়েন্ট") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text(Localization.get("doctor_notes", language)) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val patient = Patient(
              id = "pat_${UUID.randomUUID().toString().take(6)}",
              name = name,
              nameBn = name,
              age = ageStr.toIntOrNull() ?: 30,
              gender = gender,
              phone = phone,
              condition = if (condition.isNotBlank()) condition else "Musculoskeletal Rehabilitation",
              conditionBn = if (condition.isNotBlank()) condition else "পেশী ও হাড়ের পুনর্বাসন",
              affectedJoint = affectedJoint,
              affectedJointBn = affectedJoint,
              complianceRate = 100,
              doctorNotes = if (notes.isNotBlank()) notes else "Initial rehabilitation prescription initiated.",
              doctorNotesBn = if (notes.isNotBlank()) notes else "প্রাথমিক পুনর্বাসন শুরু হয়েছে।"
            )
            onPatientAdded(patient)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = PhysioTealPrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(Localization.get("save", language))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(Localization.get("cancel", language))
      }
    }
  )
}
