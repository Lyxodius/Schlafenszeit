package net.lyxodius.schlafenszeit

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private val timer = 0
    private val abInBett = 1

    private val schlafenszeit = LocalDate.now().atTime(22, 0)
    private var now = LocalDateTime.now()

    private var oldMode = timer

    private var soundPool: SoundPool? = null
    private val sounds = IntArray(8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        soundPool = SoundPool.Builder().setMaxStreams(8).setAudioAttributes(audioAttributes).build()

        sounds[0] = soundPool!!.load(this, R.raw.der_vorbildlichste_schlafrhythmus_ever, 1)
        sounds[1] = soundPool!!.load(this, R.raw.ab_in_bett_1, 1)
        sounds[2] = soundPool!!.load(this, R.raw.ab_in_bett_2, 1)
        sounds[3] = soundPool!!.load(this, R.raw.ab_in_bett_3, 1)
        sounds[4] = soundPool!!.load(this, R.raw.ab_in_bett_4, 1)
        sounds[5] = soundPool!!.load(this, R.raw.ab_in_bett_5, 1)
        sounds[6] = soundPool!!.load(this, R.raw.ab_in_bett_6, 1)
        sounds[7] = soundPool!!.load(this, R.raw.ab_in_bett_7, 1)

        updateTime()

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!this.isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            updateTime()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()
    }

    fun updateTime() {
        now = LocalDateTime.now()

        if (ChronoUnit.SECONDS.between(now, schlafenszeit) in 1..57600) {
            val now = LocalDateTime.now()

            val hours = ChronoUnit.HOURS.between(now, schlafenszeit)
            val minutes = ChronoUnit.MINUTES.between(now, schlafenszeit) % 60
            val seconds = ChronoUnit.SECONDS.between(now, schlafenszeit) % 60

            val displayString =
                String.format("%02d:%02d:%02d", hours, minutes, seconds)

            findViewById<TextView>(R.id.upperTextView).text = getString(R.string.nur_noch)
            findViewById<TextView>(R.id.mainTextView).text = displayString
            findViewById<TextView>(R.id.lowerTextView).text =
                getString(R.string.bis_zur_schlafenszeit)

            val minutenString = "Das sind " + ChronoUnit.MINUTES.between(
                now,
                schlafenszeit
            ) + " Minuten"
            val sekundenString = ChronoUnit.SECONDS.between(
                now,
                schlafenszeit
            ).toString() + " Sekunden."

            findViewById<TextView>(R.id.minutenTextView).text = minutenString
            findViewById<TextView>(R.id.bzwTextView).text = getString(R.string.bzw)
            findViewById<TextView>(R.id.sekundenTextView).text = sekundenString

            oldMode = timer
        } else {
            if (oldMode == timer) {
                soundPool?.play(sounds[Random.nextInt(7) + 1], 1f, 1f, 1, 0, 1f)
            }

            findViewById<TextView>(R.id.upperTextView).text = ""
            findViewById<TextView>(R.id.mainTextView).text = getString(R.string.ab_in_bett)
            findViewById<TextView>(R.id.lowerTextView).text = ""

            findViewById<TextView>(R.id.minutenTextView).text = ""
            findViewById<TextView>(R.id.bzwTextView).text = ""
            findViewById<TextView>(R.id.sekundenTextView).text = ""

            oldMode = abInBett
        }
    }

    fun mainTextViewClick(v: View) {
        if(oldMode == timer) {
            soundPool!!.play(sounds[0], 1f, 1f, 1, 0, 1f)
        } else {
            soundPool!!.play(sounds[Random.nextInt(7) + 1], 1f, 1f, 1, 0, 1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
    }
}
