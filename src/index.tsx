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

type LibmpvProps = {
  playUrl: String,
  style: ViewStyle;
};

const ComponentName = 'LibmpvSurfaceView';
//const Canvas = global['CanvasComponent'] || (global['CanvasComponent'] = requireNativeComponent('Canvas'));
export const SurfaceView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<LibmpvProps>(ComponentName)
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

// https://github.com/razorRun/react-native-vlc-media-player/blob/master/VLCPlayer.js

export function LibmpvVideo(props) {
  const [libmpvListener, setListener] = React.useState<EmitterSubscription>();
  React.useEffect(() => {
    if (!libmpvListener) {
      const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
      let eventListener = eventEmitter.addListener('libmpv', libmpvEvent => {
        console.log({ libmpvEvent })
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
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  videoPlayer: {
    position: "absolute",
    left: 0,
    bottom: 0,
    right: 0,
    top: 0
  }
});

export default Libmpv