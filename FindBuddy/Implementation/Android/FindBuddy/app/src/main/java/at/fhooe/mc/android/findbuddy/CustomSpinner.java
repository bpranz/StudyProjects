package at.fhooe.mc.android.findbuddy;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by David on 03.02.18.
 */

public class CustomSpinner extends android.support.v7.widget.AppCompatSpinner{

    /**
     * An interface which a client of this Spinner could use to receive
     * open/closed events for this Spinner.
     */
    public interface OnSpinnerEventsListener {

        /**
         * Callback triggered when the spinner was opened.
         */
        void onSpinnerOpened(Spinner spinner);

        /**
         * Callback triggered when the spinner was closed.
         */
        void onSpinnerClosed(Spinner spinner);
    }

    private OnSpinnerEventsListener listener;
    private boolean openInitiated = false;

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinner(Context context) {
        super(context);
    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for when the container holding this Spinner may lose focus
        openInitiated = true;
        if (listener != null) {
            listener.onSpinnerOpened(this);
        }
        return super.performClick();
    }

    /**
     * Register the lister which will listen for events.
     */
    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener){
        listener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    public void performClosedEvent() {
        openInitiated = false;
        if(listener != null) {
            listener.onSpinnerClosed(this);
        }
    }

    /**
     * a boolean flag inidicating that the spinner triggered an open event.
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return openInitiated;
    }

    public void onWindowFocusChanged (boolean hasFocus) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent();
        }
    }

}
