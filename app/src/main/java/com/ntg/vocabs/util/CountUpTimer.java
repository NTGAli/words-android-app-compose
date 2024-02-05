package com.ntg.vocabs.util;
import android.os.Handler;

public abstract class CountUpTimer {
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;

    private Handler handler;
    private Runnable timerRunnable;
    private OnTimeChangeListener onTimeChangeListener;

    public CountUpTimer() {
        handler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    handler.postDelayed(this, 1000); // Update every second

                    if (onTimeChangeListener != null) {
                        onTimeChangeListener.onTimeChange(elapsedTime);
                    }
                }
            }
        };
    }

    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
        this.onTimeChangeListener = listener;
    }

    public interface OnTimeChangeListener {
        void onTimeChange(long elapsedTime);
    }

    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            isRunning = true;
            handler.post(timerRunnable);
        }
    }

    public void stop() {
        if (isRunning) {
            handler.removeCallbacks(timerRunnable);
            isRunning = false;
        }
    }

    public void reset() {
        stop();
        elapsedTime = 0;
        notifyTimeChange();
    }

    private void notifyTimeChange() {
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(elapsedTime);
        }
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public boolean isRunning() {
        return isRunning;
    }
}