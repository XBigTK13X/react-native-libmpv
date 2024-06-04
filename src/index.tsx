import React from 'react'
import {
  requireNativeComponent,
  NativeModules,
  UIManager,
  Platform,
  type ViewStyle,
  StyleSheet
} from 'react-native';

import { NativeEventEmitter, type EmitterSubscription } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-libmpv' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type NativeProps = {
  playUrl: String,
  style: ViewStyle
};

const ComponentName = 'LibmpvSurfaceView';
// A way to prevent the "already registered" hot reload error
//const Canvas = global['CanvasComponent'] || (global['CanvasComponent'] = requireNativeComponent('Canvas'));
export const SurfaceView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<NativeProps>(ComponentName)
    : () => {
      throw new Error(LINKING_ERROR);
    };

export const Libmpv = NativeModules.Libmpv
  ? NativeModules.Libmpv
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    }
  );

// Example of another video player in react-native
// https://github.com/razorRun/react-native-vlc-media-player/blob/master/VLCPlayer.js

type LibmpvVideoProps = {
  playUrl: string,
  onLibmpvEvent: (libmpvEvent: object) => void
}

export function LibmpvVideo(props: LibmpvVideoProps) {
  const [libmpvListener, setListener] = React.useState<EmitterSubscription>();
  React.useEffect(() => {
    if (!libmpvListener && props.onLibmpvEvent) {
      const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
      let eventListener = eventEmitter.addListener('libmpv', (libmpvEvent) => {
        if (libmpvEvent.eventId) {
          libmpvEvent.value = parseInt(libmpvEvent.eventId, 10)
        }
        if (libmpvEvent.kind === 'long' || libmpvEvent.kind === 'double') {
          libmpvEvent.value = Number(libmpvEvent.value)
        }
        if (libmpvEvent.kind === 'boolean') {
          libmpvEvent.value = libmpvEvent.value === 'true'
        }
        return props.onLibmpvEvent(libmpvEvent)
      });
      setListener(eventListener);
      return () => {
        eventListener.remove();
      };
    }
    return
  }, []);

  return <SurfaceView style={styles.videoPlayer} playUrl={props.playUrl} />
}

const styles = StyleSheet.create({
  videoPlayer: {
    position: "absolute",
    left: 0,
    bottom: 0,
    right: 0,
    top: 0
  }
});

export default Libmpv