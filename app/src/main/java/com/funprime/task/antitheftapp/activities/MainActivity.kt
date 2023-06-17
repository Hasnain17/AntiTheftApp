package com.funprime.task.antitheftapp.activities
/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 17/06/2023
 */
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.funprime.task.antitheftapp.R
import com.funprime.task.antitheftapp.databinding.ActivityMainBinding
import com.funprime.task.antitheftapp.utils.SharePref
import com.google.android.material.snackbar.Snackbar
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import com.funprime.task.antitheftapp.services.ChargerDetectionService
import com.funprime.task.antitheftapp.services.MotionDetectionService
import com.funprime.task.antitheftapp.services.PocketDetectionService

class MainActivity : AppCompatActivity() {
    private  var binding: ActivityMainBinding?=null
    private var sharePref: SharePref? = null

    private var motion = false
    private var pocket = false
    private var charge = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater)
        setContentView(binding!!.root)
        sharePref = SharePref(this@MainActivity)

        initSettings()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            askNotificationPermission()
        }
        else{
            setupEvents()
        }



    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                setupEvents()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                binding?.let {
                    Snackbar.make(
                        it.root,
                        "Notification blocked",
                        Snackbar.LENGTH_LONG
                    ).setAction("Settings") {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }.show()
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }
    private fun setupEvents() {
        binding!!.chargerDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                sharePref!!.putForCharge(true)
                toggleService(ChargerDetectionService::class.java)
                updateStatus()
            }
            else{
                sharePref!!.putForCharge(false)
                toggleService(ChargerDetectionService::class.java)
                updateStatus()

            }
        }
        binding!!.pocketRemoval.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                sharePref!!.putForPocketRemoval(true)
                toggleService(PocketDetectionService::class.java)
                updateStatus()

            }
            else{
                sharePref!!.putForPocketRemoval(false)
                toggleService(PocketDetectionService::class.java)
                updateStatus()

            }
        }

        binding!!.motionDetection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                sharePref!!.putForMotion(true)
                toggleService(MotionDetectionService::class.java)
                updateStatus()

            }
            else{
                sharePref!!.putForMotion(false)
                toggleService(MotionDetectionService::class.java)
                updateStatus()

            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupEvents()
        } else {
            Toast.makeText(this, "Notification Permission is deny", Toast.LENGTH_SHORT).show()
        }
    }


    private fun toggleService(serviceClass: Class<*>) {
        val isRunning = isServiceRunning(serviceClass)

        if (isRunning)
            stopService(Intent(this, serviceClass))
        else
            startService(Intent(this, serviceClass))

        when (serviceClass) {
            PocketDetectionService::class.java -> pocket = !isRunning
            ChargerDetectionService::class.java -> charge = !isRunning
            MotionDetectionService::class.java -> motion = !isRunning
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatus() {
        binding!!.motionDetectionStatusTextView.text =
            getString(R.string._motion_detection_status) + getStatusText(motion)

        binding!!.pocketRemovalStatusTextView.text =
            getString(R.string._pocket_removal_detection_status) + getStatusText(pocket)

        binding!!.chargerRemovalStatusTextView.text =
            getString(R.string._charger_removal_detection_status) + getStatusText(charge)



    }

    private fun getStatusText(isRunning: Boolean): String {
        return if (isRunning) {
            getString(R.string._running)
        } else {
            getString(R.string._not_running)
        }
    }
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    private fun initSettings() {
        binding!!.chargerDetection.isChecked=sharePref!!.getForCharge()
        binding!!.motionDetection.isChecked=sharePref!!.getForMotion()
        binding!!.pocketRemoval.isChecked=sharePref!!.getForPocketRemoval()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }
    override fun onResume() {
        super.onResume()
        motion = isServiceRunning(MotionDetectionService::class.java)
        pocket = isServiceRunning(PocketDetectionService::class.java)
        charge = isServiceRunning(ChargerDetectionService::class.java)
        updateStatus()
    }

}