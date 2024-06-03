import {
  requireNativeComponent,
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

const ComponentName = 'LibmpvView';

export const LibmpvView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<LibmpvProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
