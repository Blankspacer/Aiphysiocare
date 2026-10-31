package com.example.data

import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.BodyCategory
import com.example.model.ExerciseDefinition
import com.example.model.ExerciseSessionReport
import com.example.model.PainLogEntry
import com.example.model.Patient
import com.example.model.Prescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object PhysioRepository {

  private val _language = MutableStateFlow(AppLanguage.EN)
  val language: StateFlow<AppLanguage> = _language.asStateFlow()

  private val _currentRole = MutableStateFlow(AppRole.PATIENT)
  val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

  fun setLanguage(lang: AppLanguage) {
    _language.value = lang
  }

  fun toggleLanguage() {
    _language.value = if (_language.value == AppLanguage.EN) AppLanguage.BN else AppLanguage.EN
  }

  fun setRole(role: AppRole) {
    _currentRole.value = role
  }

  // --- Exercise Catalog ---
  val exerciseCatalog: List<ExerciseDefinition> = listOf(
    // KNEE
    ExerciseDefinition(
      id = "knee_ext",
      name = "Seated Knee Extension",
      nameBn = "বসা অবস্থায় হাঁটু সোজা করা (Knee Extension)",
      category = BodyCategory.KNEE,
      targetJoint = "Right & Left Knee",
      targetJointBn = "ডান ও বাম হাঁটু",
      startingPosition = "Sit upright on an elevated chair or table with thighs supported and knees bent at 90 degrees.",
      startingPositionBn = "একটি চেয়ারে সোজা হয়ে বসুন যাতে উরু সমতলে থাকে এবং হাঁটু ৯০ ডিগ্রিতে বাঁকানো থাকে।",
      instructions = listOf(
        "Slowly straighten your lower leg upward until your knee is fully extended.",
        "Hold the contracted position at top for 2 seconds while engaging quadriceps.",
        "Smoothly lower your leg back to the 90-degree starting angle."
      ),
      instructionsBn = listOf(
        "ধীরে ধীরে আপনার নিচের পা ওপরের দিকে সোজা করুন যতক্ষণ না হাঁটু সম্পূর্ণ সোজা হয়।",
        "সর্বোচ্চ অবস্থায় পৌঁছানোর পর হাঁটু সোজা রেখে ২ সেকেন্ড ধরে রাখুন।",
        "ধীরেসুস্থে পা নামিয়ে শুরুর ৯০ ডিগ্রি অবস্থানে ফিরিয়ে আনুন।"
      ),
      precautions = listOf(
        "Do not swing or kick your leg rapidly.",
        "Keep your spine tall; do not lean backward when kicking up.",
        "Stop if you feel sharp pain behind the patella."
      ),
      precautionsBn = listOf(
        "পা ঝাঁকিয়ে দ্রুত তুলবেন না।",
        "পা তোলার সময় পেছনের দিকে হেলে পড়বেন না, মেরুদণ্ড সোজা রাখুন।",
        "হাঁটুর বাটিতে তীব্র ব্যথা হলে অবিলম্বে থামুন।"
      ),
      startAngle = 90f,
      targetAngleMin = 165f,
      targetAngleMax = 180f,
      defaultReps = 10,
      defaultSets = 3,
      holdDurationSec = 2
    ),
    ExerciseDefinition(
      id = "knee_heel_slide",
      name = "Heel Slides",
      nameBn = "হিল স্লাইডস (Heel Slides)",
      category = BodyCategory.KNEE,
      targetJoint = "Knee Flexion",
      targetJointBn = "হাঁটুর বাঁকানো সঞ্চালন",
      startingPosition = "Lie flat on your back on a firm bed or mat with legs fully extended.",
      startingPositionBn = "একটি ম্যাট বা শক্ত বিছানায় শুয়ে পড়ুন, পা সোজা রাখুন।",
      instructions = listOf(
        "Slowly slide your heel toward your buttocks, bending your knee as far as comfortable.",
        "Hold the bent position for 3 seconds.",
        "Slowly slide the heel back out to straight position."
      ),
      instructionsBn = listOf(
        "গোড়ালি বিছানার ওপর রেখে ধীরে ধীরে পেছনের দিকে টেনে আনুন।",
        "সর্বোচ্চ বাঁকানো অবস্থায় ৩ সেকেন্ড ধরে রাখুন।",
        "ধীরে ধীরে গোড়ালি আগের সোজা অবস্থানে ফিরিয়ে নিয়ে যান।"
      ),
      precautions = listOf(
        "Keep your heel in constant contact with the surface.",
        "Do not force past severe pain."
      ),
      precautionsBn = listOf(
        "গোড়ালি সবসময় পৃষ্ঠতলের সাথে স্পর্শে রাখুন।",
        "অতিরিক্ত ব্যথা জোর করে সহ্য করবেন না।"
      ),
      startAngle = 175f,
      targetAngleMin = 85f,
      targetAngleMax = 100f,
      defaultReps = 12,
      defaultSets = 3,
      holdDurationSec = 3
    ),
    ExerciseDefinition(
      id = "knee_slr",
      name = "Straight Leg Raises",
      nameBn = "স্ট্রেইট লেগ রেইজ (Straight Leg Raises)",
      category = BodyCategory.KNEE,
      targetJoint = "Hip Flexor & Knee Extensor",
      targetJointBn = "হিপ ও হাঁটু পেশি",
      startingPosition = "Lie on back with one knee bent and foot flat, the target leg kept straight.",
      startingPositionBn = "এক পা বাঁকিয়ে এবং অন্য পা সোজা রেখে সোজা হয়ে শুয়ে থাকুন।",
      instructions = listOf(
        "Lock your knee straight and lift the entire leg about 30-45 cm off the mat.",
        "Hold steady for 3 seconds.",
        "Gently lower the leg without letting it drop."
      ),
      instructionsBn = listOf(
        "হাঁটু লক করে সম্পূর্ণ পা মাটি থেকে প্রায় ৩০-৪৫ সেমি উপরে তুলুন।",
        "৩ সেকেন্ড ধরে রাখুন।",
        "পা আলতো করে নামিয়ে আনুন।"
      ),
      precautions = listOf("Do not arch your lower back off the floor."),
      precautionsBn = listOf("কোমর বিছানা থেকে ধনুকের মতো বাঁকাবেন না।"),
      startAngle = 0f,
      targetAngleMin = 35f,
      targetAngleMax = 50f,
      defaultReps = 10,
      defaultSets = 3,
      holdDurationSec = 3
    ),
    // ARM & SHOULDER
    ExerciseDefinition(
      id = "shoulder_arm_raise",
      name = "Overhead Arm Raises",
      nameBn = "হাত উপরে তোলা (Arm Raises)",
      category = BodyCategory.ARM_SHOULDER,
      targetJoint = "Glenohumeral & Scapula",
      targetJointBn = "কাঁধের জয়েন্ট",
      startingPosition = "Stand or sit tall with arms resting at your sides, thumbs pointing up.",
      startingPositionBn = "সোজা হয়ে দাঁড়ান বা বসুন, হাত শরীরের পাশে রাখুন, বুড়ো আঙুল উপরের দিকে রাখুন।",
      instructions = listOf(
        "Raise your arm forward and upward toward the ceiling in a controlled arc.",
        "Reach as high as pain permits (target 150-170 degrees).",
        "Pause for 2 seconds and gently lower."
      ),
      instructionsBn = listOf(
        "হাত সামনের দিক দিয়ে ওপরের দিকে সিলিং বরাবর তুলুন।",
        "যতদূর আরামদায়ক হয় (টার্গেট ১৫০-১৭০ ডিগ্রি) উপরে তুলুন।",
        "২ সেকেন্ড ধরে রেখে আলতো করে নামান।"
      ),
      precautions = listOf(
        "Avoid shrugging your neck and shoulders.",
        "Maintain controlled tempo."
      ),
      precautionsBn = listOf(
        "কাঁধ কানের দিকে কুঁচকে তুলবেন না।",
        "নিয়ন্ত্রিত গতিতে সঞ্চালন করুন।"
      ),
      startAngle = 20f,
      targetAngleMin = 150f,
      targetAngleMax = 175f,
      defaultReps = 10,
      defaultSets = 3,
      holdDurationSec = 2
    ),
    ExerciseDefinition(
      id = "shoulder_wall_slide",
      name = "Wall Slides",
      nameBn = "ওয়াল স্লাইডস (Wall Slides)",
      category = BodyCategory.ARM_SHOULDER,
      targetJoint = "Shoulder & Upper Back",
      targetJointBn = "কাঁধ ও পিঠের জয়েন্ট",
      startingPosition = "Stand facing a wall with forearms resting against the wall at 90 degrees.",
      startingPositionBn = "দেয়ালের মুখোমুখি দাঁড়ান এবং কনুই ৯০ ডিগ্রিতে দেয়ালে রাখুন।",
      instructions = listOf(
        "Slide forearms upward along the wall into a gentle 'V' shape.",
        "Hold at peak stretch for 2 seconds.",
        "Slide back down to 90 degrees."
      ),
      instructionsBn = listOf(
        "দেয়ালের ওপর দিয়ে হাত দুটো উপরের দিকে 'V' আকারে স্লাইড করুন।",
        "শীর্ষে ২ সেকেন্ড ধরে রাখুন।",
        "পুনরায় ৯০ ডিগ্রিতে নামিয়ে আনুন।"
      ),
      precautions = listOf("Keep core engaged and avoid hyperextending low back."),
      precautionsBn = listOf("পেটের পেশি শক্ত রাখুন এবং কোমর বাঁকাবেন না।"),
      startAngle = 90f,
      targetAngleMin = 145f,
      targetAngleMax = 165f,
      defaultReps = 8,
      defaultSets = 3,
      holdDurationSec = 2
    ),
    // ELBOW
    ExerciseDefinition(
      id = "elbow_flex",
      name = "Elbow Flexion & Extension",
      nameBn = "কনুই বাঁকানো ও সোজা করা (Elbow Flexion)",
      category = BodyCategory.ELBOW,
      targetJoint = "Elbow Joint",
      targetJointBn = "কনুই জয়েন্ট",
      startingPosition = "Sit with arm supported on a table or hanging relaxed at side.",
      startingPositionBn = "টেবিলে হাত রেখে অথবা শরীরের পাশে ঝুলিয়ে বসুন।",
      instructions = listOf(
        "Bend your elbow bringing hand towards your shoulder.",
        "Hold flexed position for 2 seconds.",
        "Smoothly straighten elbow back fully."
      ),
      instructionsBn = listOf(
        "কনুই বাঁকিয়ে হাত কাঁধের দিকে নিয়ে আসুন।",
        "২ সেকেন্ড ধরে রাখুন।",
        "ধীরে ধীরে কনুই সম্পূর্ণ সোজা করুন।"
      ),
      precautions = listOf("Do not twist your wrist or fling the forearm."),
      precautionsBn = listOf("কব্জি মোচড়াবেন না বা হাত দ্রুত ছুড়বেন না।"),
      startAngle = 30f,
      targetAngleMin = 135f,
      targetAngleMax = 150f,
      defaultReps = 12,
      defaultSets = 3,
      holdDurationSec = 2
    ),
    // NECK
    ExerciseDefinition(
      id = "neck_rotation",
      name = "Cervical Neck Rotation",
      nameBn = "ঘাড় ডানে-বামে ঘোরানো (Neck Rotation)",
      category = BodyCategory.NECK,
      targetJoint = "Cervical Spine",
      targetJointBn = "ঘাড়ের মেরুদণ্ড",
      startingPosition = "Sit tall with shoulders relaxed and chin level.",
      startingPositionBn = "সোজা হয়ে বসুন, কাঁধ শিথিল রাখুন এবং চিবুক সমান্তরাল রাখুন।",
      instructions = listOf(
        "Gently rotate your head looking over your right shoulder.",
        "Hold for 3 seconds, then return to center.",
        "Repeat looking over your left shoulder."
      ),
      instructionsBn = listOf(
        "ধীরে ধীরে মাথা ঘুরিয়ে ডান কাঁধের ওপর দিয়ে তাকান।",
        "৩ সেকেন্ড ধরে রাখুন, এরপর সোজা ফিরে আসুন।",
        "একইভাবে বাম কাঁধের দিকে ঘোরান।"
      ),
      precautions = listOf("Do not tilt your head backwards while turning."),
      precautionsBn = listOf("ঘোরানোর সময় মাথা পেছনের দিকে হেলাবেন না।"),
      startAngle = 0f,
      targetAngleMin = 65f,
      targetAngleMax = 80f,
      defaultReps = 8,
      defaultSets = 2,
      holdDurationSec = 3
    ),
    // WRIST & HAND
    ExerciseDefinition(
      id = "wrist_flex_ext",
      name = "Wrist Flexion & Extension",
      nameBn = "কব্জি উপরে-নিচে বাঁকানো (Wrist Flexion/Extension)",
      category = BodyCategory.WRIST_HAND,
      targetJoint = "Radiocarpal Joint",
      targetJointBn = "কব্জি জয়েন্ট",
      startingPosition = "Rest forearm on table with wrist hanging over the edge, palm facing down.",
      startingPositionBn = "টেবিলের প্রান্তে হাত রাখুন যাতে কব্জি বাইরের দিকে থাকে, হাতের তালু নিচের দিকে।",
      instructions = listOf(
        "Lift hand upward bending at the wrist.",
        "Hold for 2 seconds at peak upward extension.",
        "Slowly lower hand downward into gentle flexion."
      ),
      instructionsBn = listOf(
        "কব্জি থেকে হাত ওপরের দিকে তুলুন।",
        "ওপরে তুলে ২ সেকেন্ড ধরে রাখুন।",
        "ধীরে ধীরে হাত নিচের দিকে নামান।"
      ),
      precautions = listOf("Keep forearm flat on the surface without lifting elbow."),
      precautionsBn = listOf("কনুই টেবিল থেকে তুলবেন না, হাত সমান্তরাল রাখুন।"),
      startAngle = 0f,
      targetAngleMin = 60f,
      targetAngleMax = 75f,
      defaultReps = 15,
      defaultSets = 3,
      holdDurationSec = 2
    )
  )

  // --- Patients ---
  private val _patients = MutableStateFlow(
    listOf(
      Patient(
        id = "pat_1",
        name = "Rahat Khan",
        nameBn = "রাহাত খান",
        age = 34,
        gender = "Male",
        phone = "+880 1711-234567",
        condition = "Right Knee ACL Reconstruction (Week 6)",
        conditionBn = "ডান হাঁটুর এসিএল রিকনস্ট্রাকশন (৬ষ্ঠ সপ্তাহ)",
        affectedJoint = "Right Knee",
        affectedJointBn = "ডান হাঁটু",
        complianceRate = 92,
        doctorNotes = "Excellent recovery pace. Emphasize active quadriceps contraction at full 175° extension. Do not add ankle weights yet.",
        doctorNotesBn = "পুনর্বাসন খুব ভালো চলছে। ১৭৫° পুরো এক্সটেনশনে চতুর্ভুজ পেশি ধরে রাখুন। ভারী ওজন ব্যবহার করবেন না।"
      ),
      Patient(
        id = "pat_2",
        name = "Anika Begum",
        nameBn = "আনিকা বেগম",
        age = 52,
        gender = "Female",
        phone = "+880 1819-987654",
        condition = "Adhesive Capsulitis (Frozen Shoulder Stage 2)",
        conditionBn = "ফ্রোজেন শোল্ডার (স্টেজ ২)",
        affectedJoint = "Left Shoulder",
        affectedJointBn = "বাম কাঁধ",
        complianceRate = 78,
        doctorNotes = "Gentle mobilization. If sharp pain exceeds VAS 5, stop immediately and apply hot pack for 10 minutes.",
        doctorNotesBn = "হালকা সঞ্চালন করুন। ব্যথা ৫ মাত্রার বেশি হলে তৎক্ষণাৎ থামুন এবং ১০ মিনিট গরম সেক দিন।"
      ),
      Patient(
        id = "pat_3",
        name = "Kamal Hossain",
        nameBn = "কামাল হোসেন",
        age = 45,
        gender = "Male",
        phone = "+880 1912-334455",
        condition = "Lateral Epicondylitis (Tennis Elbow)",
        conditionBn = "টেনিস এলবো (কনুইয়ের প্রদাহ)",
        affectedJoint = "Right Elbow",
        affectedJointBn = "ডান কনুই",
        complianceRate = 85,
        doctorNotes = "Maintain steady forearm rotation and stretch wrist extensors twice daily.",
        doctorNotesBn = "প্রতিদিন দুইবার কনুই ও কব্জি নিয়মিত হালকা স্ট্রেচিং করুন।"
      )
    )
  )
  val patients: StateFlow<List<Patient>> = _patients.asStateFlow()

  private val _selectedPatientId = MutableStateFlow("pat_1")
  val selectedPatientId: StateFlow<String> = _selectedPatientId.asStateFlow()

  fun selectPatient(id: String) {
    _selectedPatientId.value = id
  }

  fun getSelectedPatient(): Patient {
    return _patients.value.find { it.id == _selectedPatientId.value } ?: _patients.value.first()
  }

  fun addPatient(patient: Patient) {
    _patients.value = _patients.value + patient
    _selectedPatientId.value = patient.id
  }

  // --- Prescriptions ---
  private val _prescriptions = MutableStateFlow(
    listOf(
      Prescription(
        id = "rx_1",
        patientId = "pat_1",
        exerciseId = "knee_ext",
        repetitions = 10,
        sets = 3,
        frequency = "Twice Daily",
        frequencyBn = "দিনে দুইবার",
        durationDays = 14,
        customPrecautions = "Hold terminal extension for full 2s. Keep spine erect against chair.",
        customPrecautionsBn = "শেষ অবস্থায় পুরো ২ সেকেন্ড ধরে রাখুন। মেরুদণ্ড সোজা রাখুন।",
        prescribedAt = "Yesterday"
      ),
      Prescription(
        id = "rx_2",
        patientId = "pat_1",
        exerciseId = "knee_heel_slide",
        repetitions = 12,
        sets = 3,
        frequency = "Once Daily",
        frequencyBn = "দিনে একবার",
        durationDays = 14,
        customPrecautions = "Ensure smooth heel sliding on bed without hitching hip.",
        customPrecautionsBn = "হিপ না বাঁকিয়ে গোড়ালি মসৃণভাবে টানুন।",
        prescribedAt = "Yesterday"
      ),
      Prescription(
        id = "rx_3",
        patientId = "pat_2",
        exerciseId = "shoulder_arm_raise",
        repetitions = 8,
        sets = 3,
        frequency = "Twice Daily",
        frequencyBn = "দিনে দুইবার",
        durationDays = 21,
        customPrecautions = "Do not force arm if sharp anterior impingement pain is felt.",
        customPrecautionsBn = "সামনে তীব্র ব্যথা হলে অতিরিক্ত জোর করবেন না।",
        prescribedAt = "2 days ago"
      )
    )
  )
  val prescriptions: StateFlow<List<Prescription>> = _prescriptions.asStateFlow()

  fun addPrescription(prescription: Prescription) {
    _prescriptions.value = _prescriptions.value + prescription
  }

  fun getPatientPrescriptions(patientId: String): List<Prescription> {
    return _prescriptions.value.filter { it.patientId == patientId && it.isActive }
  }

  // --- Exercise Session Reports ---
  private val _sessionReports = MutableStateFlow(
    listOf(
      ExerciseSessionReport(
        id = "rep_101",
        prescriptionId = "rx_1",
        patientId = "pat_1",
        exerciseId = "knee_ext",
        completedReps = 10,
        targetReps = 10,
        completedSets = 3,
        targetSets = 3,
        avgRomDegrees = 168.5f,
        targetRomDegrees = 175f,
        accuracyScore = 94,
        flaggedForDoctor = false,
        aiObservation = "Excellent quadriceps activation. Average extension reached 168.5° with smooth 2.1s peak hold. Torso remained stable throughout.",
        aiObservationBn = "চমৎকার কোয়াড্রিসেপ্স পেশির সংকোচন। গড় কোণ ১৬৮.৫ ডিগ্রিতে পৌঁছেছে এবং ২.১ সেকেন্ড ধরে রাখা হয়েছে। শরীর স্থির ছিল।",
        timestamp = System.currentTimeMillis() - 86400000L * 1,
        painLevel = 1,
        doctorFeedback = "Great work Rahat! Continue this cadence."
      ),
      ExerciseSessionReport(
        id = "rep_102",
        prescriptionId = "rx_1",
        patientId = "pat_1",
        exerciseId = "knee_ext",
        completedReps = 7,
        targetReps = 10,
        completedSets = 2,
        targetSets = 3,
        avgRomDegrees = 152f,
        targetRomDegrees = 175f,
        accuracyScore = 76,
        flaggedForDoctor = true,
        flagReason = "Patient stopped early due to mild joint stiffness; slight backward torso leaning detected on reps 5-7.",
        aiObservation = "Slight trunk compensation observed at rep 5. Movement range restricted to 152°. Session paused safely.",
        aiObservationBn = "৫ম রিপে শরীর কিছুটা পেছনের দিকে হেলে গিয়েছিল। গতিসীমা ১৫২ ডিগ্রিতে সীমিত ছিল। সেশনটি নিরাপদে বিরতি দেওয়া হয়েছে।",
        timestamp = System.currentTimeMillis() - 86400000L * 2,
        painLevel = 4,
        stoppedEarly = true,
        doctorFeedback = "Good decision to stop at VAS 4. Recommended 5 min ice pack application."
      )
    )
  )
  val sessionReports: StateFlow<List<ExerciseSessionReport>> = _sessionReports.asStateFlow()

  fun recordSessionReport(report: ExerciseSessionReport) {
    _sessionReports.value = listOf(report) + _sessionReports.value
  }

  fun getPatientReports(patientId: String): List<ExerciseSessionReport> {
    return _sessionReports.value.filter { it.patientId == patientId }
  }

  // --- Pain Log ---
  private val _painLogs = MutableStateFlow(
    listOf(
      PainLogEntry(
        id = "pain_1",
        patientId = "pat_1",
        score = 2,
        timestamp = System.currentTimeMillis() - 86400000L,
        note = "Morning mild stiffness after waking up."
      ),
      PainLogEntry(
        id = "pain_2",
        patientId = "pat_1",
        score = 1,
        timestamp = System.currentTimeMillis(),
        note = "Felt comfortable during evening extension."
      )
    )
  )
  val painLogs: StateFlow<List<PainLogEntry>> = _painLogs.asStateFlow()

  fun logPain(patientId: String, score: Int, note: String) {
    val entry = PainLogEntry(
      id = UUID.randomUUID().toString(),
      patientId = patientId,
      score = score,
      timestamp = System.currentTimeMillis(),
      note = note
    )
    _painLogs.value = listOf(entry) + _painLogs.value
  }
}
