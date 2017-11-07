package com.blundell.zxsensor;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class ZxSensor {

    private static final byte MSG_CODE_GESTURE_EVENT = (byte) 0xFC;
    private static final byte GESTURE_CODE_SWIPE_RIGHT_EVENT = 0x01;
    private static final byte GESTURE_CODE_SWIPE_LEFT_EVENT = 0x02;

    private final UartDevice device;
    private final String pinAddress;

    private GestureListener listener;

    public static ZxSensor newInstance(String uartPinAddress) {
        UartDevice bus;
        try {
            PeripheralManagerService service = new PeripheralManagerService();
            bus = service.openUartDevice(uartPinAddress);
        } catch (IOException e) {
            throw new IllegalStateException(uartPinAddress + " cannot be connected to.", e);
        }

        try {
            bus.setBaudrate(115200);
            bus.setDataSize(8);
            bus.setParity(UartDevice.PARITY_NONE);
            bus.setStopBits(1);
        } catch (IOException e) {
            throw new IllegalStateException(uartPinAddress + " cannot be configured.", e);
        }
        return new ZxSensor(bus, uartPinAddress);
    }

    ZxSensor(UartDevice device, String pinAddress) {
        this.device = device;
        this.pinAddress = pinAddress;
    }

    public void setListener(GestureListener listener) {
        this.listener = listener;
    }

    public void startListeningForGestures() {
        try {
            device.registerUartDeviceCallback(onUartBusHasData);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot listen for input from " + pinAddress, e);
        }
    }

    private final UartDeviceCallback onUartBusHasData = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                byte[] buffer = new byte[3];
                while ((uart.read(buffer, buffer.length)) > 0) {
                    handleGestureSensorEvent(buffer);
                }
            } catch (IOException e) {
                Log.e("TUT", "Cannot read device data.", e);
            }

            return true;
        }

        private void handleGestureSensorEvent(byte[] payload) {
            byte messageCode = payload[0];
            if (messageCode != MSG_CODE_GESTURE_EVENT) {
                return;
            }
            byte gestureCode = payload[1];
            if (gestureCode == GESTURE_CODE_SWIPE_RIGHT_EVENT) {
                listener.onSwipeRight();
            } else if (gestureCode == GESTURE_CODE_SWIPE_LEFT_EVENT) {
                listener.onSwipeLeft();
            }
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.e("TUT", "ERROR " + error);
        }
    };

    public void stopListeningForGestures() {
        device.unregisterUartDeviceCallback(onUartBusHasData);
    }

    public void close() {
        try {
            device.close();
        } catch (IOException e) {
            Log.e("TUT", pinAddress + " connection cannot be closed.", e);
        }
    }

    public interface GestureListener {
        void onSwipeLeft();

        void onSwipeRight();
    }
}
