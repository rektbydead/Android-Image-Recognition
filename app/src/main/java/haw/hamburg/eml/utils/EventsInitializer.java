package haw.hamburg.eml.utils;

import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import haw.hamburg.eml.MainActivity;
import haw.hamburg.eml.R;

public class EventsInitializer extends EventsVariableHandler {
    private EventsInitializer() {}

    private static EventsInitializer instance;

    public static EventsInitializer getInstance() {
        if (instance == null)
            instance = new EventsInitializer();

        return instance;
    }

    public void initializeEvents(MainActivity instance) {
        this.buttonStartEvent(instance.findViewById(R.id.button));
        this.startBar(instance.findViewById(R.id.seekBar));
    }

    private void startBar(Slider seek) {
        seek.addOnChangeListener((slider, value, fromUser) -> {
            imageSize = (int) value;
            Log.d("MainActivity", "" + value);
        });
    }

    private void buttonStartEvent(Button button) {
        button.setOnClickListener((view) -> {
            this.detect = !this.detect;
            button.setText(this.detect ? "Stop" : "Start");
        });
    }
}
