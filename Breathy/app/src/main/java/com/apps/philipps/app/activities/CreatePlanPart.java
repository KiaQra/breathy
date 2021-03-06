package com.apps.philipps.app.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apps.philipps.app.R;
import com.apps.philipps.source.PlanManager;
import com.apps.philipps.source.SaveData;

public class CreatePlanPart extends AppCompatActivity {

    private PlanManager.Plan.Option part;
    private int planId;

    private boolean create;

    private SeekBar frequency;
    private TextView frequencyText;
    private TextView duration;
    private TextView inValue;
    private TextView outValue;
    private int durationValue;
    private SeekBar in;
    private SeekBar out;
    private EditText partName;

    private Button plus;
    private Button minus;
    private Button btnSubmitPlan;
    private Button btnCancelPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan_part);

        planId = getIntent().getIntExtra("planId", -1);
        int optionId = getIntent().getIntExtra("planPart", -1);
        part = planId >= 0 ? PlanManager.getOption(planId, optionId) : null;
        create = part == null;
        initViews();
        if (!create)
            initData();
        durationValue = Integer.parseInt(duration.getText().toString());
    }

    private void initData() {
        in.setProgress(part.getIn().id);
        out.setProgress(part.getOut().id);
        inValue.setText(part.getIn().name() + " " + part.getIn().value * 100 + "%");
        outValue.setText(part.getOut().name() + " " + part.getOut().value * 100 + "%");
        String temp = (part.getDuration() / 60000) % 60 + "";
        duration.setText(temp);
        frequency.setProgress(part.getFrequency() - 10);
        frequencyText.setText(part.getFrequency() + "");
        partName.setText(part.getName());
    }


    private void initViews() {
        ((TextView) findViewById(R.id.txtCreatePlanTitle)).setText(create ? "Create new part" : "Edit part of plan");

        frequency = (SeekBar) findViewById(R.id.frequency);
        frequencyText = (TextView) findViewById(R.id.frequencyText);
        frequencyText.setText(10 + frequency.getProgress() + "");
        duration = (TextView) findViewById(R.id.duration);

        partName = (EditText) findViewById(R.id.planPartName);
        partName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSubmitPlan.setEnabled(count != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnSubmitPlan = (Button) findViewById(R.id.btnSubmitPlan);
        btnSubmitPlan.setEnabled(!create);
        btnCancelPlan = (Button) findViewById(R.id.btnCancelPlan);
        minus = (Button) findViewById(R.id.minutesMinus);
        plus = (Button) findViewById(R.id.minutesPlus);
        in = (SeekBar) findViewById(R.id.seekIn);
        out = (SeekBar) findViewById(R.id.seekOut);
        inValue = (TextView) findViewById(R.id.inValue);
        outValue = (TextView) findViewById(R.id.outValue);
        if (create) {
            int i = in.getProgress();
            int o = out.getProgress();
            if (i >= 0 && i < 6)
                inValue.setText(PlanManager.Plan.BreathIntensity.get(i).name() + " " +
                        PlanManager.Plan.BreathIntensity.get(in.getProgress()).value * 100 + "%");
            if (o >= 0 && o < 6)
                outValue.setText(PlanManager.Plan.BreathIntensity.get(i).name() + " " +
                        PlanManager.Plan.BreathIntensity.get(in.getProgress()).value * 100 + "%");
        }
        frequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    frequencyText.setText(10 + progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        in.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (out.getProgress() == 0 && progress == 0 && fromUser)
                    seekBar.setProgress(1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        out.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (out.getProgress() == 0 && progress == 0 && fromUser)
                    seekBar.setProgress(1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnCancelPlan.setOnClickListener(v -> finish());
        btnSubmitPlan.setOnClickListener(v -> {
            if (create) {
                PlanManager.Plan plan = PlanManager.getPlan(planId);
                if (plan != null)
                    plan.addOption(partName.getText().toString(), PlanManager.Plan.BreathIntensity.get(in.getProgress()),
                            PlanManager.Plan.BreathIntensity.get(out.getProgress()), frequency.getProgress() + 10,
                            durationValue * 60);
            } else {
                part.setIn(PlanManager.Plan.BreathIntensity.get(in.getProgress()));
                part.setOut(PlanManager.Plan.BreathIntensity.get(out.getProgress()));
                part.setFrequency(frequency.getProgress() + 10);
                part.setDuration(durationValue * 60000);
                part.setName(partName.getText().toString());
            }
            SaveData.savePlanManager(this);
            finish();
        });
        minus.setOnClickListener(v -> duration.setText(durationValue > 1 ? --durationValue + "" : 1 + ""));
        plus.setOnClickListener(v -> duration.setText(durationValue < 110 ? ++durationValue + "" : 110 + ""));
        out.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                outValue.setText(PlanManager.Plan.BreathIntensity.get(progress).name() + " " + PlanManager.Plan.BreathIntensity.get(progress).value * 100 + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        in.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                inValue.setText(PlanManager.Plan.BreathIntensity.get(progress).name() + " " + PlanManager.Plan.BreathIntensity.get(progress).value * 100 + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SaveData.savePlanManager(this);
    }

}
