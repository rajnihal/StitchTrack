package com.example.trackit.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.trackit.R
import java.io.File
import java.io.FileWriter


class Line1Activity : AppCompatActivity() {

    private lateinit var perfEditText: EditText
    private lateinit var button: Button
    private lateinit var effTextView: TextView
    private lateinit var tableLayout: TableLayout


    private val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line1)

        val toolbarBackButton = findViewById<ImageView>(R.id.toolbar_back_button)
        toolbarBackButton.setOnClickListener {
            onBackPressed()
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val selectedDateTextView = findViewById<TextView>(R.id.selectedDateTextView)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            selectedDateTextView.text = "Selected Date: $selectedDate"
            calendarView.visibility = View.GONE
        }

        selectedDateTextView.setOnClickListener {
            calendarView.visibility = View.VISIBLE
        }

        // Request write permission if not granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        }




        // Setup spinner visibility
        val spinner = findViewById<Spinner>(R.id.styleSpinner)
        tableLayout = findViewById(R.id.tablelayout)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "FRONT") {
                    tableLayout.visibility = View.VISIBLE
                } else {
                    tableLayout.visibility = View.GONE
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val totalTextViews = mutableListOf<TextView>()
        val totalSums = mutableListOf<Int>()

        val ranges = listOf(11..19, 21..29, 31..39, 41..49, 51..59)

        for ((index, range) in ranges.withIndex()) {
            var totalSum = 0

            val totalTextViewId = resources.getIdentifier("tot${index + 1}", "id", packageName)
            val totalTextView = findViewById<TextView>(totalTextViewId)
            totalTextViews.add(totalTextView)

            for (i in range) {
                val editTextId = resources.getIdentifier("time$i", "id", packageName)
                val editText = findViewById<EditText>(editTextId)

                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        totalSum = 0
                        for (j in range) {
                            val editTextId =
                                resources.getIdentifier("time$j", "id", packageName)
                            val editText = findViewById<EditText>(editTextId)
                            val timeValue = editText.text.toString().toIntOrNull() ?: 0
                            totalSum += timeValue
                        }
                        totalTextViews[index].text = totalSum.toString()
                        totalSums[index] = totalSum
                    }
                })
            }

            totalSums.add(totalSum)
        }


        // Declare the updateButtonColor function
        fun updateButtonColor(effTextView: TextView, perfEditText: EditText, button: Button) {
            val efficiency = effTextView.text.toString().toDoubleOrNull() ?: return
            val perfValue = perfEditText.text.toString().toIntOrNull() ?: return
            val halfEfficiency = efficiency / 2
            val fullEfficiency = efficiency

            when {
                perfValue < halfEfficiency -> button.setBackgroundColor(Color.RED)
                perfValue.toDouble() in halfEfficiency..fullEfficiency -> button.setBackgroundColor(
                    Color.YELLOW
                )

                else -> button.setBackgroundColor(Color.GREEN)
            }
        }

        // Loop for setting up each row
        val rows = 5 // Number of rows

        for (i in 1..rows) {
            // Find the TextViews
            val capTextView =
                findViewById<TextView>(resources.getIdentifier("cap$i", "id", packageName))
            val targetTextView =
                findViewById<TextView>(resources.getIdentifier("tar$i", "id", packageName))
            val actTextView =
                findViewById<TextView>(resources.getIdentifier("act$i", "id", packageName))
            val effTextView =
                findViewById<TextView>(resources.getIdentifier("eff$i", "id", packageName))

            // Get the value from cap TextView and convert it to an integer
            val capValue = capTextView.text.toString().toInt()
            val targetValue = (capValue * 0.8).toInt()
            targetTextView.text = targetValue.toString()

            // Get the value from act TextView and convert it to an integer
            val actValue = actTextView.text.toString().toInt()
            val efficiency = actValue.toDouble() / capValue.toDouble() * 100
            effTextView.text = efficiency.toString()

            // Initialize views
            val perfEditText =
                findViewById<EditText>(resources.getIdentifier("perf$i", "id", packageName))
            val button = findViewById<Button>(resources.getIdentifier("but$i", "id", packageName))

            // Set a text change listener on perfEditText
            perfEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    updateButtonColor(effTextView, perfEditText, button)
                }
            })
        }


        // Export data to CSV
        val exportButton = findViewById<Button>(R.id.exportButton)
        exportButton.setOnClickListener {
            exportToCSV(this)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform necessary operations
            } else {
                // Permission denied, handle accordingly
            }
        }
    }





    private fun exportToCSV(context: Context) {
        val fileName = "data.csv"
        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        if (downloadsDir != null) {
            val filePath = File(downloadsDir, fileName)

            try {
                val fileWriter = FileWriter(filePath)

                // Write header
                val header = "Cap, Target, Actual, Efficiency, Perf\n"
                fileWriter.append(header)

                // Loop through each row
                val rows = 5 // Number of rows
                for (i in 1..rows) {
                    val capTextView =
                        findViewById<TextView>(resources.getIdentifier("cap$i", "id", packageName))
                    val targetTextView =
                        findViewById<TextView>(resources.getIdentifier("tar$i", "id", packageName))
                    val actTextView =
                        findViewById<TextView>(resources.getIdentifier("act$i", "id", packageName))
                    val effTextView =
                        findViewById<TextView>(resources.getIdentifier("eff$i", "id", packageName))
                    val perfEditText =
                        findViewById<EditText>(resources.getIdentifier("perf$i", "id", packageName))

                    val capValue = capTextView.text.toString()
                    val targetValue = targetTextView.text.toString()
                    val actValue = actTextView.text.toString()
                    val effValue = effTextView.text.toString()
                    val perfValue = perfEditText.text.toString()

                    // Write row data to CSV
                    val row = "$capValue, $targetValue, $actValue, $effValue, $perfValue\n"
                    fileWriter.append(row)
                }

                fileWriter.flush()
                fileWriter.close()

                // Show notification
                showNotification(context, filePath)

                Toast.makeText(context, "Data exported to $filePath", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Failed to access downloads directory", Toast.LENGTH_SHORT).show()
        }
    }




    // Function to show notification
    private fun showNotification(context: Context, filePath: File) {
        // Create a notification channel for Android Oreo and higher
        val channel = NotificationChannel(
            "download_channel",
            "Download Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        // Create notification
        val builder = NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("File Downloaded")
            .setContentText("File downloaded successfully")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(null) // Set your pending intent here if you want to handle click action

        // Function to show notification
        fun showNotification(context: Context, filePath: File) {
            // Create a notification channel for Android Oreo and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "download_channel",
                    "Download Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }

            // Create an intent to open the CSV file when the notification is clicked
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(Uri.fromFile(filePath), "text/csv")

            // Create a PendingIntent
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            // Create notification
            val builder = NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("File Downloaded")
                .setContentText("File downloaded successfully")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // Set the PendingIntent to handle click action

            // Show notification
            val notificationManager = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(1, builder.build())
        }

    }





}