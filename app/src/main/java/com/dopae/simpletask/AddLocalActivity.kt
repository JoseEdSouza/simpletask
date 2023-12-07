package com.dopae.simpletask

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import com.dopae.simpletask.component.MenuAddEditComponent
import com.dopae.simpletask.databinding.ActivityAddLocalBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddLocalActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityAddLocalBinding
    private lateinit var menu: MenuAddEditComponent
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: FragmentContainerView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioBntArrive: RadioButton
    private lateinit var radioBntLeave: RadioButton
    private lateinit var seekBar: SeekBar
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var circle: Circle? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation()
            } else {
                val alertDialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle(R.string.locationPermissionDeniedTitle)
                    .setMessage(R.string.locationPermissionDeniedMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.understand) { _, _ ->
                        close()
                    }
                alertDialog.show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.task_theme)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = binding.mapFragmentLocal
        seekBar = binding.seekBarLocalRadius
        radioGroup = binding.radioGroupLocalTrigger
        radioBntArrive = binding.radioBtnLocalArrive
        radioBntLeave = binding.radioBtnLocalLeave
        menu = MenuAddEditComponent(binding.menuAddEdtLocal)
        init()
    }

    fun init(){
        radioGroup.check(radioBntArrive.id)
        menu.init({ save() }, { close() })
        getLastLocation()
        seekBar.progress = 50
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                changeCircleRadius(progress.toDouble())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun close() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun save() {
        //todo
        close()
    }


    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            it?.let {
                currentLocation = it
                val mapFrag =
                    supportFragmentManager.findFragmentById(mapFragment.id) as SupportMapFragment
                mapFrag.getMapAsync(this)
            }
        }
    }

    private fun changeCircleRadius(progress:Double){
        circle?.radius = progress
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val radiusColor = ContextCompat.getColor(this, R.color.marker_circle_radius)
        val curr = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        map.addMarker(
            MarkerOptions()
                .position(curr)
                .title(ContextCompat.getString(this, R.string.currentLocation))
        )
        circle = map.addCircle(
            CircleOptions()
                .center(curr)
                .radius(seekBar.progress.toDouble())
                .strokeColor(Color.BLACK)
                .strokeWidth(2.0f)
                .fillColor(radiusColor)

        )
        map.moveCamera(CameraUpdateFactory.newLatLng(curr))
        map.setOnMapClickListener {
            map.clear()
            val markerPosition = it
            map.addMarker(MarkerOptions().position(markerPosition))
            circle = map.addCircle(
                CircleOptions()
                    .center(markerPosition)
                    .radius(seekBar.progress.toDouble())
                    .strokeColor(Color.BLACK)
                    .strokeWidth(2.0f)
                    .fillColor(radiusColor)
            )
        }
    }
}