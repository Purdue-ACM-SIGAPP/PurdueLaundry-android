package xyz.jhughes.laundry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import xyz.jhughes.laundry.apiclient.MachineAPI;
import xyz.jhughes.laundry.apiclient.MachineService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MachineApiTest {
    @Mock
    MachineService machineService;

    private MachineAPI machineAPI;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        machineAPI = new MachineAPI(machineService);
    }

    @Test
    public void getLocations() {
        machineAPI.getLocations();
        verify(machineService, times(1)).getLocations();
    }

    @Test
    public void getMachines() {
        machineAPI.getAllMachines();
        verify(machineService, times(1)).getAllMachines();
    }

    @Test
    public void getMachineStatus() {
        machineAPI.getMachineStatus("test");
        verify(machineService, times(1)).getMachineStatus("test");
    }
}
