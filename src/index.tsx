import React from 'react'
import {
  requireNativeComponent,
  NativeModules,
  UIManager,
  Platform,
  type ViewStyle,
  NativeEventEmitter,
  type EmitterSubscription,
  findNodeHandle
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-libmpv' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type NativeProps = {
  playUrl: String,
  isPlaying: Boolean,
  selectedAudioTrack: Number,
  selectedSubtitleTrack: Number,
  seekToSeconds: Number,
  surfaceWidth: Number,
  surfaceHeight: Number
};

const ComponentName = 'LibmpvSurfaceView';
// A way to prevent the "already registered" hot reload error
//const Canvas = global['CanvasComponent'] || (global['CanvasComponent'] = requireNativeComponent('Canvas'));
const LibmpvSurfaceView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<NativeProps>(ComponentName)
    : () => {
      throw new Error(LINKING_ERROR);
    };

type LibmpvVideoProps = {
  playUrl: string,
  isPlaying: boolean,
  onLibmpvEvent: (libmpvEvent: object) => void,
  onLibmpvLog: (libmpvLog: object) => void,
  surfaceStyle: object,
  selectedAudioTrack: number,
  selectedSubtitleTrack: number,
  seekToSeconds: number,
  surfaceWidth: number,
  surfaceHeight: number
}

const styles = {
  videoPlayer: {
    position: "absolute",
    left: 0,
    bottom: 0,
    right: 0,
    top: 0
  }
};

const EVENT_LOOKUP: any = {
  0: 'NONE',
  1: 'SHUTDOWN',
  2: 'LOG_MESSAGE',
  3: 'GET_PROPERTY_REPLY',
  4: 'SET_PROPERTY_REPLY',
  5: 'COMMAND_REPLY',
  6: 'START_FILE',
  7: 'END_FILE',
  8: 'FILE_LOADED',
  16: 'CLIENT_MESSAGE',
  17: 'VIDEO_RECONFIG',
  18: 'AUDIO_RECONFIG',
  20: 'SEEK',
  21: 'PLAYBACK_RESTART',
  22: 'PROPERTY_CHANGE',
  24: 'QUEUE_OVERFLOW',
  25: 'HOOK'
}

export const LibmpvVideo = React.forwardRef((props: LibmpvVideoProps, parentRef) => {
  const [activityListener, setActivityListener] = React.useState<EmitterSubscription>();
  const [forceRefreshKey, setForceRefreshKey] = React.useState<String>(`mpv-${Date.now()}`);
  React.useEffect(() => {
    if (!activityListener && props.onLibmpvEvent) {
      const eventEmitter = new NativeEventEmitter(NativeModules.Libmpv);
      const eventListener = eventEmitter.addListener('libmpvEvent', (libmpvEvent) => {
        if (props.onLibmpvEvent) {
          if (libmpvEvent.eventId) {
            libmpvEvent.value = parseInt(libmpvEvent.eventId, 10)
            libmpvEvent.eventKind = EVENT_LOOKUP[libmpvEvent.eventId]
          }
          else if (libmpvEvent.kind === 'long' || libmpvEvent.kind === 'double') {
            libmpvEvent.value = Number(libmpvEvent.value)
          }
          else if (libmpvEvent.kind === 'boolean') {
            libmpvEvent.value = libmpvEvent.value === 'true'
          }
          return props.onLibmpvEvent(libmpvEvent)
        }
      })
      const logObserver = eventEmitter.addListener('libmpvLog', (libmpvLog) => {
        if (props.onLibmpvLog) {
          return props.onLibmpvLog(libmpvLog);
        }
      })
      setActivityListener(activityListener)
      return () => {
        eventListener.remove()
        logObserver.remove()
      }
    }
    return () => { }
  }, [])

  console.log({ forceRefreshKey })
  return <LibmpvSurfaceView
    key={forceRefreshKey}
    style={props.surfaceStyle ? props.surfaceStyle : styles.videoPlayer}
    playUrl={props.playUrl}
    surfaceWidth={props.surfaceWidth}
    surfaceHeight={props.surfaceHeight}
    isPlaying={props.isPlaying}
    selectedAudioTrack={props.selectedAudioTrack}
    selectedSubtitleTrack={props.selectedSubtitleTrack}
    seekToSeconds={props.seekToSeconds}
  />
})

export default LibmpvVideo