package com.nextopen.vo;

import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class IBeaconItem {

    private final IBeaconDevice device;

    public IBeaconItem(final IBeaconDevice device) {
        this.device = device;
    }

    public IBeaconDevice getDevice() {
        return device;
    }

}
