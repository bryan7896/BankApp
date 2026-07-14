import { NativeModules, DeviceEventEmitter, EmitterSubscription } from 'react-native';

const { BankBridge } = NativeModules;

export const sendToNative = (eventName: string, data?: string | null) => {
    BankBridge?.sendEvent(eventName, data ?? null);
};

export const listenFromNative = (
    eventName: string,
    callback: (data: any) => void
): EmitterSubscription => {
    return DeviceEventEmitter.addListener(eventName, callback);
};