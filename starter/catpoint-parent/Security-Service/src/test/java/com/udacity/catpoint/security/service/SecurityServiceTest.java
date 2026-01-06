package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private ImageService imageService;

    @Mock
    private SecurityRepository securityRepository;

    private SecurityService securityService;
    private Sensor sensor;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(securityRepository, imageService);
        sensor = new Sensor("test", SensorType.DOOR);
    }

    // Requirement 1: If alarm armed and sensor activated -> pending alarm
    @Test
    void sensorActivated_systemArmedAndSensorInactive_setStatusPending() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    // Requirement 2: If alarm armed, sensor activated, pending -> alarm
    @Test
    void sensorActivated_systemArmedAndPending_setStatusAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    // Requirement 3: If pending and all sensors inactive -> no alarm
    @Test
    void allSensorsInactive_statusPending_setStatusNoAlarm() {
        sensor.setActive(true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    // Requirement 4: If alarm active, sensor state change -> no effect
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void alarmActive_sensorStateChange_noAlarmStatusChange(boolean active) {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, active);
        verify(securityRepository, never()).setAlarmStatus(any(AlarmStatus.class));
    }

    // Requirement 5: Sensor activated while already active and pending -> alarm
    @Test
    void sensorActivated_alreadyActiveAndPending_setStatusAlarm() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    // Requirement 6: Sensor deactivated while inactive -> no change
    @Test
    void sensorDeactivated_alreadyInactive_noStatusChange() {
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    // Requirement 7: Image with cat detected + armed home -> alarm
    @Test
    void imageContainsCat_systemArmedHome_setStatusAlarm() {
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        securityService.processImage(mock(BufferedImage.class));
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    // Requirement 8: Image no cat + sensors inactive -> no alarm
    @Test
    void imageNoCat_sensorsInactive_setStatusNoAlarm() {
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(sensor);
        when(securityRepository.getSensors()).thenReturn(sensors);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        securityService.processImage(mock(BufferedImage.class));
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    // Requirement 9: System disarmed -> no alarm
    @Test
    void systemDisarmed_setStatusNoAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    // Requirement 10: System armed -> reset all sensors inactive  
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void systemArmed_resetSensorsToInactive(ArmingStatus status) {
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(new Sensor("test1", SensorType.DOOR));
        sensors.add(new Sensor("test2", SensorType.WINDOW));
        sensors.forEach(s -> s.setActive(true));
        when(securityRepository.getSensors()).thenReturn(sensors);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.setArmingStatus(status);
        sensors.forEach(s -> assertFalse(s.getActive()));
    }

    // Requirement 11: Armed home + camera shows cat -> alarm
    @Test
    void armedHome_cameraShowsCat_setStatusAlarm() {
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        securityService.processImage(mock(BufferedImage.class));
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
}
