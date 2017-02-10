package xyz.jhughes.laundry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.jhughes.laundry.LaundryParser.Machine;
import xyz.jhughes.laundry.LaundryParser.MachineStates;
import xyz.jhughes.laundry.LaundryParser.MachineTypes;

public class MachineFilter {

    private final ArrayList<Machine> filteredMachines = new ArrayList<>();
    private final ArrayList<State> states = new ArrayList<>();

    public MachineFilter() {
        Collections.addAll(states, State.values());
    }

    public MachineFilter(List<State> states) {
        this.states.addAll(states);
    }


    public boolean[] getStateBoolean(String[] options) {
        boolean[] ret = new boolean[options.length];
        for(int i = 0; i < options.length; i++) {
            ret[i] = states.contains(MachineFilter.State.getState(options[i]));
        }
        return ret;
    }

    public List<Machine> filter(boolean dryers, List<Machine> allMachines) {
        for(Machine m : allMachines) {
            if(dryers != m.getType().equals(MachineTypes.DRYER)) continue;
            for(State state : states) {
                if(m.getStatus().equals(state.toString()))
                    filteredMachines.add(m);
            }
        }
        return getMachines();
    }

    public List<Machine> getMachines() {
        ArrayList<Machine> ret = new ArrayList<>();
        ret.addAll(filteredMachines);
        return ret;
    }

    public List<State> getStates() {
        return states;
    }

    public enum State {

        IN_USE(MachineStates.IN_USE), AVAILABLE(MachineStates.AVAILABLE),
        ALMOST_DONE(MachineStates.ALMOST_DONE), END_CYCLE(MachineStates.END_CYCLE),
        READY(MachineStates.READY);

        private final String stateString;

        State(String stateString) {
            this.stateString = stateString;
        }

        @Override
        public String toString() {
            return stateString;
        }

        public static String toString(List<MachineFilter.State> states) {
            StringBuilder sb = new StringBuilder();
            for (MachineFilter.State s : states) {
                sb.append(s.toString());
                sb.append("|");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }

        public static State getState(String stateString) {
            if(stateString.equalsIgnoreCase(MachineStates.IN_USE)) return IN_USE;
            if(stateString.equalsIgnoreCase(MachineStates.AVAILABLE)) return AVAILABLE;
            if(stateString.equalsIgnoreCase(MachineStates.ALMOST_DONE)) return ALMOST_DONE;
            if(stateString.equalsIgnoreCase(MachineStates.END_CYCLE)) return END_CYCLE;
            if(stateString.equalsIgnoreCase(MachineStates.READY)) return READY;
            return null;
        }
    }
}
