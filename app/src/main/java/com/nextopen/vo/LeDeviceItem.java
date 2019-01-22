package com.nextopen.vo;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;

public class LeDeviceItem {

    private final BluetoothLeDevice device;

    public LeDeviceItem(final BluetoothLeDevice device) {
        this.device = device;
    }

    public BluetoothLeDevice getDevice() {
        return device;
    }
}
