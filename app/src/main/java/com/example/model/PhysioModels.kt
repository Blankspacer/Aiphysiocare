package com.example.model

enum class AppLanguage {
  EN, BN
}

enum class AppRole {
  DOCTOR, PATIENT
}

enum class BodyCategory(val displayName: String, val displayNameBn: String) {
  KNEE("Knee", "হাঁটু"),
  ELBOW("Elbow", "কনুই"),
  ARM_SHOULDER("Arm & Shoulder", "হাত ও কাঁধ"),
  NECK("Neck", "ঘাড়"),
  WRIST_HAND("Wrist & Hand", "কব্জি ও হাত")
}

data class ExerciseDefinition(
  val id: String,
  val name: String,
  val nameBn: String,
  val category: BodyCategory,
  val targetJoint: String,
  val targetJointBn: String,
  val startingPosition: String,
  val startingPositionBn: String,
  val instructions: List<String>,
  val instructionsBn: List<String>,
  val precautions: List<String>,
  val precautionsBn: List<String>,
  val startAngle: Float,
  val targetAngleMin: Float,
  val targetAngleMax: Float,
  val defaultReps: Int = 10,
  val defaultSets: Int = 3,
  val holdDurationSec: Int = 2
)

data class Patient(
  val id: String,
  val name: String,
  val nameBn: String,
  val age: Int,
  val gender: String,
  val phone: String,
  val condition: String,
  val conditionBn: String,
  val affectedJoint: String,
  val affectedJointBn: String,
  val complianceRate: Int,
  val doctorNotes: String,
  val doctorNotesBn: String,
  val lastActive: String = "Today"
)

data class Prescription(
  val id: String,
  val patientId: String,
  val exerciseId: String,
  val repetitions: Int,
  val sets: Int,
  val frequency: String,
  val frequencyBn: String,
  val durationDays: Int,
  val customPrecautions: String,
  val customPrecautionsBn: String,
  val prescribedAt: String,
  val isActive: Boolean = true
)

data class ExerciseSessionReport(
  val id: String,
  val prescriptionId: String,
  val patientId: String,
  val exerciseId: String,
  val completedReps: Int,
  val targetReps: Int,
  val completedSets: Int,
  val targetSets: Int,
  val avgRomDegrees: Float,
  val targetRomDegrees: Float,
  val accuracyScore: Int,
  val flaggedForDoctor: Boolean,
  val flagReason: String? = null,
  val aiObservation: String,
  val aiObservationBn: String,
  val timestamp: Long,
  val painLevel: Int = 0,
  val stoppedEarly: Boolean = false,
  val doctorFeedback: String? = null
)

data class PainLogEntry(
  val id: String,
  val patientId: String,
  val score: Int, // 0 to 10
  val timestamp: Long,
  val note: String
)
