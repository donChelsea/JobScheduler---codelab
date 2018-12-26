package android.example.com.notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    RadioGroup networkOptions = findViewById(R.id.networkOptions);
    private JobScheduler mScheduler;
    private static final int JOB_ID = 0;
    private Switch mDeviceIdleSwitch;
    private Switch mDeviceChargingSwitch;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceIdleSwitch = findViewById(R.id.idleSwitch);
        mDeviceChargingSwitch = findViewById(R.id.chargingSwitch);
        mSeekBar = findViewById(R.id.seekBar);
    }

    public void scheduleJob(View view) {
        int seekBarInteger = mSeekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;
        int selectedNetworkID = networkOptions.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
        switch (selectedNetworkID) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
            break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkID = JobInfo.NETWORK_TYPE_UNMETERED; 
        }

        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked())
                .setRequiresCharging(mDeviceChargingSwitch.isChecked());

        boolean constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE || mDeviceChargingSwitch.isChecked() || mDeviceIdleSwitch.isChecked() || seekBarSet;

        if (constraintSet) {

            JobInfo myJobInfo = builder.build();

            mScheduler.schedule(myJobInfo);

            Toast.makeText(this, "Job scheduled, job will run when " + "the constraints are met.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show();
        }

        final TextView seekBarProgress = findViewById(R.id.seekBarProgress);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0) {
                    seekBarProgress.setText(progress + " s");
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (seekBarSet) {
            builder.setOverrideDeadline(seekBarInteger * 1000);
        }

}

    public void cancelJobs(View view) {
        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
