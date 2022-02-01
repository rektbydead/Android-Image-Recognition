package haw.hamburg.eml.utils;

import android.widget.Button;

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
    }

    private void buttonStartEvent(Button button) {
        button.setOnClickListener((view) -> {
            this.detect = !this.detect;
            button.setText(this.detect ? "Stop" : "Start");
        });
    }
}
