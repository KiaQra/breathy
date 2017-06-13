package com.apps.philipps.source;

import android.support.annotation.Nullable;

import com.apps.philipps.source.interfaces.IObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jevgenij Huebert on 06.05.2017. Project Breathy
 */

public class PlanManager implements IObserver, Serializable {

    private static List<Plan> plans;
    private static PlanManager manager = new PlanManager();
    private static int currentPlan;

    private PlanManager(){
        currentPlan = -1;
        plans = new ArrayList<>();
        BreathData.addObserver(manager);
    }

    public static PlanManager getInstance(){
        return manager;
    }

    public static void addPlan(Plan plan){
        plans.add(plan);
    }
    public static void removePlan(Plan plan){
        plans.remove(plan);
    }

    public static List<Part> getParts(){
        ArrayList<Part> result = new ArrayList<>();
        for(Plan plan : plans)
            result.add(plan);
        return result;
    }

    public static boolean setActive(int id){
        if(id>=0 && id<plans.size())
            currentPlan = id;
        else return false;
        return true;
    }
    public static boolean setActive(Plan plan){
        int id = plans.indexOf(plan);
        if(id!=-1)
            currentPlan = id;
        else return false;
        return true;
    }

    public static boolean startPlan(){
        if(currentPlan!=-1) {
            plans.get(currentPlan).startPlan();
            return true;
        }
        return false;
    }

    public static Plan getPlan(int id){
        if(id>=0 && id<plans.size())
            return plans.get(id);
        return null;
    }

    public static Plan deletePlan(int id){
        if(id>=0 && id<plans.size())
            return plans.remove(id);
        return null;
    }

    public static boolean isActive(Plan plan){
        return currentPlan == plans.indexOf(plan);
    }
    public static int getCurrentPlan(){
        return currentPlan;
    }

    @Nullable
    public static Plan.Option getStatus(){
        if(currentPlan!=-1 && plans.get(currentPlan).getCurrentDuration()!=0){
            Plan p = plans.get(currentPlan);
            return new Plan.Option(p.getStrengthIn(), p.getStrengthOut(), p.getFrequency(), p.getCurrentDuration());
        }
        return null;
    }

    public static void setPlans(List<Plan> plans) {
        PlanManager.plans = plans;
    }

    public static List<Plan> getPlans() {
        return plans;
    }

    @Override
    public void call(Object... messages) {
        for (Plan plan : plans) {
            plan.update();
        }
    }

    public static class Plan implements Cloneable, Serializable, Part {
        private List<Option> options;
        private long currentTime;
        private int currentOption;
        private boolean running;
        private long delta;
        private String name;

        public Plan(){
            options = new ArrayList<>();

        }
        public Plan(BreathIntensity in, BreathIntensity out, int frequency, int duration){
            this();
            Option add = new Option(in, out, frequency, duration*1000);
            add.parent = this;
            options.add(add);
        }
        public Plan(String name, BreathIntensity in, BreathIntensity out, int frequency, int duration){
            this(in, out, frequency, duration);
            this.name = name;
        }

        public Plan addOption(BreathIntensity in, BreathIntensity out, int frequency, int seconds){
            Option o = new Option(in, out, frequency, seconds*1000);
            o.parent = this;
            options.add(o);
            return this;
        }
        public void setName(String name){
            this.name = name;
        }
        public Plan removeOption(int id){
            options.remove(id);
            return this;
        }
        public Option getOption(int id){
            if(id>=0 && id<options.size())
                return options.get(id);
            return null;
        }

        public List<Part> getParts(){
            List<Part> result = new ArrayList<>();
            for(Option option : options)
                result.add(option);
            return result;
        }

        public boolean isActivated(){
            return PlanManager.isActive(this);
        }

        public BreathIntensity getStrengthIn(){
            return options.get(currentOption).in;
        }

        public BreathIntensity getStrengthOut(){
            return options.get(currentOption).out;
        }

        public int getFrequency(){
            return options.get(currentOption).frequency;
        }

        public int getCurrentDuration(){
            return (int)(currentTime/1000);
        }


        private boolean startPlan() {
            if(running)
                return false;
            else {
                running = true;
                currentTime = options.get(0).duration;
                delta = System.currentTimeMillis();
            }
            return running;
        }

        private boolean update(){
            delta = System.currentTimeMillis() - delta;
            if(running){
                if(currentTime-delta<=0){
                    currentOption++;
                    if(currentOption == options.size()){
                        running = false;
                        currentOption = 0;
                        currentTime = 0;
                        return false;
                    }
                    else{
                        currentTime = options.get(currentOption).getDuration();
                    }
                }
                else
                    currentTime -= delta;
            }
            delta = System.currentTimeMillis();
            return true;
        }

        @Override
        public int getId() {
            return plans.indexOf(this);
        }

        @Override
        public String getName(){
            return name;
        }


        @Override
        protected Object clone() throws CloneNotSupportedException {
            Plan result = new Plan();
            result.currentOption = currentOption;
            result.currentTime = currentTime;
            result.delta = delta;
            result.running = running;
            result.options = new ArrayList<>();
            for (Option o :
                    options) {
                options.add((Option) o.clone());
            }
            return result;
        }

        @Override
        public String toString() {
            String result = "";
            for (Option o : options) {
                result += o + "\n";
            }
            return result;
        }

        public enum BreathIntensity{
            VeryHigh(1, 5, "Very high"),
            High(0.8, 4, "High"),
            Medium(0.5, 3, "Medium"),
            Low(0.4, 2, "Low"),
            VeryLow(0.2, 1, "Very low"),
            None(0, 0, "");
            public final double value;
            public final String name;
            public final int id;
            BreathIntensity(double value, int id, String name){
                this.name = name;
                this.value = value;
                this.id = id;
            }
            public static BreathIntensity get(int id){
                for(BreathIntensity i : values())
                    if(i.id == id)
                        return i;
                return null;
            }
            public static BreathIntensity get(double value){
                BreathIntensity prev = None;
                for(BreathIntensity i : values())
                    if(i.value <= value && i.value > prev.value)
                        return i;
                    else
                        prev = i;
                return null;
            }
        }

        @Override
        public int hashCode() {
            return options.size() * name.hashCode();
        }



        public static class Option implements Cloneable, Serializable, Part {
            private BreathIntensity out;
            private BreathIntensity in;
            private int frequency;
            private long duration;
            private Plan parent;

            public Option(BreathIntensity in, BreathIntensity out, int frequency, long duration){
                this.in = in;
                this.out = out;
                this.frequency = frequency;
                this.duration = duration;
            }
            public long getDuration(){
                return duration;
            }

            public BreathIntensity getIn(){
                return in;
            }

            public BreathIntensity getOut(){
                return out;
            }

            public int getFrequency(){
                return frequency;
            }
            public void setDuration(int duration){
                this.duration = duration<60*60*1000?duration:this.duration;
            }

            public void setIn(BreathIntensity in){
                this.in = in;
            }

            public void setOut(BreathIntensity out){
                this.out = out;
            }

            public void setFrequency(int frequency){
                this.frequency = frequency;
            }

            @Override
            public int getId() {
                return parent.options.indexOf(this);
            }

            @Override
            public String getName() {
                return parent.getName();
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return new Option(in, out, frequency, duration);
            }

            @Override
            public String toString() {

                return "in: " + in.name + " " + in.value*100 +  "%, out: " + out.name + " " + out.value*100 + "%, frequency: " + frequency + " per minute, time: " + duration/1000 + " seconds";
            }
        }


    }

    public interface Part {
        int getId();
        String getName();
    }
}
