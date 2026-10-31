package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.ui.theme.PhysioCyanContainer
import com.example.ui.theme.PhysioError
import com.example.ui.theme.PhysioMintAccent
import com.example.ui.theme.PhysioNavy
import com.example.ui.theme.PhysioSuccess
import com.example.ui.theme.PhysioTealPrimary
import com.example.ui.theme.PhysioWarning
import com.example.util.Localization

@Composable
fun AppTopHeader(
  currentRole: AppRole,
  currentLanguage: AppLanguage,
  onToggleRole: () -> Unit,
  onToggleLanguage: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 3.dp,
    shadowElevation = 2.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PhysioNavy)
        ) {
          Icon(
            imageVector = Icons.Default.MedicalServices,
            contentDescription = "AIphysiocare Logo",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
        Column {
          Text(
            text = Localization.get("app_title", currentLanguage),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = if (currentRole == AppRole.DOCTOR) {
              Localization.get("doctor_portal", currentLanguage)
            } else {
              Localization.get("patient_portal", currentLanguage)
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (currentRole == AppRole.DOCTOR) PhysioTealPrimary else PhysioMintAccent
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Language Toggle Chip
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggleLanguage)
            .testTag("language_toggle_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Language",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (currentLanguage == AppLanguage.EN) "BN (বাংলা)" else "EN",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Role Switcher Button
        OutlinedButton(
          onClick = onToggleRole,
          shape = RoundedCornerShape(16.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("role_switcher_button")
        ) {
          Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Switch",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (currentRole == AppRole.DOCTOR) {
              Localization.get("patient_mode", currentLanguage)
            } else {
              Localization.get("doctor_mode", currentLanguage)
            },
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Composable
fun HeroBannerCard(
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column {
      Image(
        painter = painterResource(id = R.drawable.img_physio_hero),
        contentDescription = "Physiotherapy Rehabilitation Demonstration",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      )
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
      }
    }
  }
}

@Composable
fun StatCard(
  label: String,
  value: String,
  icon: ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.12f))
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = value,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = label,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
      }
    }
  }
}

@Composable
fun ComplianceBadge(
  rate: Int,
  modifier: Modifier = Modifier
) {
  val badgeColor = when {
    rate >= 85 -> PhysioSuccess
    rate >= 70 -> PhysioWarning
    else -> PhysioError
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = badgeColor.copy(alpha = 0.15f),
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(badgeColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "$rate% Compliance",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = badgeColor
      )
    }
  }
}

@Composable
fun SafetyAlertBanner(
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = PhysioCyanContainer),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.Top,
      modifier = Modifier.padding(12.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = "Medical Alert",
        tint = PhysioTealPrimary,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = if (language == AppLanguage.EN) {
          "Clinical Safety: Exercise prescriptions are set by your licensed physiotherapist. Never force movement past sharp pain. Tap 'Stop' anytime during a session."
        } else {
          "চিকিৎসা সুরক্ষা: ব্যায়ামের প্রেসক্রিপশন আপনার লাইসেন্সপ্রাপ্ত ফিজিওথেরাপিস্ট দ্বারা নির্ধারিত। তীব্র ব্যথার মধ্যে জোর করবেন না। যেকোনো সময় 'থামুন' চাপুন।"
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun PainVasSelector(
  selectedScore: Int,
  onScoreSelected: (Int) -> Unit,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Healing,
            contentDescription = null,
            tint = PhysioWarning,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = Localization.get("pain_checkin_title", language),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
        val painDescription = when (selectedScore) {
          0 -> if (language == AppLanguage.EN) "0 (No pain)" else "০ (ব্যথামুক্ত)"
          in 1..3 -> if (language == AppLanguage.EN) "$selectedScore (Mild)" else "$selectedScore (মৃদু)"
          in 4..6 -> if (language == AppLanguage.EN) "$selectedScore (Moderate)" else "$selectedScore (মাঝারি)"
          else -> if (language == AppLanguage.EN) "$selectedScore (Severe!)" else "$selectedScore (তীব্র!)"
        }
        Text(
          text = painDescription,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = if (selectedScore > 6) PhysioError else if (selectedScore > 3) PhysioWarning else PhysioSuccess
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        (0..10).forEach { score ->
          val isSelected = selectedScore == score
          val color = when {
            score == 0 -> PhysioSuccess
            score <= 3 -> PhysioMintAccent
            score <= 6 -> PhysioWarning
            else -> PhysioError
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(if (isSelected) color else color.copy(alpha = 0.15f))
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = color,
                shape = CircleShape
              )
              .clickable { onScoreSelected(score) }
              .testTag("pain_score_$score")
          ) {
            Text(
              text = "$score",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else color
            )
          }
        }
      }
    }
  }
}
