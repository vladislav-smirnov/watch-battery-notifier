package io.github.airdaydreamers.batterynotifier.watch.views.activities

import android.app.Activity
import android.os.Bundle
import io.github.airdaydreamers.batterynotifier.watch.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}