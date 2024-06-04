import React from 'react'
import {
  requireNativeComponent,
  NativeModules,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-libmpv' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type LibmpvProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'LibmpvSurfaceView';

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

export class LibmpvView extends React.Component {
  constructor(props, context) {
    super(props, context)
    this._libmpvEvent = this._libmpvEvent.bind(this);
  }
  _libmpvEvent(nativeEvent) {
    console.log(nativeEvent)
  }

  render() {
    return <SurfaceView playUrl={this.props.playUrl} />
  }
}

export default Libmpv