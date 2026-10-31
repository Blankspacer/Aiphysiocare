package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PhysioRepository
import com.example.model.AppLanguage
import com.example.model.AppRole
import com.example.model.BodyCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AIphysiocare", appName)
  }

  @Test
  fun `verify exercise catalog contains 5 body categories`() {
    val catalog = PhysioRepository.exerciseCatalog
    assertTrue(catalog.isNotEmpty())

    val categories = catalog.map { it.category }.toSet()
    assertTrue(categories.contains(BodyCategory.KNEE))
    assertTrue(categories.contains(BodyCategory.ELBOW))
    assertTrue(categories.contains(BodyCategory.ARM_SHOULDER))
    assertTrue(categories.contains(BodyCategory.NECK))
    assertTrue(categories.contains(BodyCategory.WRIST_HAND))
  }

  @Test
  fun `verify patient prescriptions and role switching`() {
    val patients = PhysioRepository.patients.value
    assertTrue(patients.isNotEmpty())

    val activePatient = PhysioRepository.getSelectedPatient()
    assertNotNull(activePatient)

    PhysioRepository.setRole(AppRole.DOCTOR)
    assertEquals(AppRole.DOCTOR, PhysioRepository.currentRole.value)

    PhysioRepository.setRole(AppRole.PATIENT)
    assertEquals(AppRole.PATIENT, PhysioRepository.currentRole.value)
  }
}
