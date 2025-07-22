import * as React from 'react';
import { View, Button, Modal, TouchableOpacity, AppState, Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { LibmpvVideo, Libmpv } from 'react-native-libmpv';

const TRACK_DISABLED = -1;

const resolutions = {
  ultraHd: {
    width: 3840,
    height: 2160
  },
  fullHd: {
    width: 1920,
    height: 1080
  }
}

const styles = {
  homePage: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'black'
  },
  homeButton: {
    width: '75%',
  },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'black'
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  button: {
    flex: 1,
    backgroundColor: 'black'
  }
}

function LandingPage({ navigation }) {
  return (
    <View style={styles.homePage}>
      <View style={styles.homeButton}>
        <Button onPress={() => { navigation.navigate('Video') }} title="Play Video" />
      </View>
    </View>
  )
}


function VideoPage({ navigation }) {
  const [isPlaying, setIsPlaying] = React.useState(true);
  const [seekSeconds, setSeekSeconds] = React.useState(0)
  const [loadError, setError] = React.useState('')
  const nativeRef = React.useRef(null);

  React.useEffect(() => {
    const appStateSubscription = AppState.addEventListener('change', appState => {
      if (appState === 'background') {
        console.log("Cleanup background")
        navigation.goBack()
      }
    });

    return () => {
      appStateSubscription.remove();
    };
  }, []);

  if (loadError) {
    return <Text>{loadError}</Text>
  }

  function onLibmpvEvent(libmpvEvent) {
    if (!libmpvEvent.property || libmpvEvent.property !== 'track-list') {
      console.log({ libmpvEvent })
    }
  }

  function onLibmpvLog(libmpvLog) {
    if (libmpvLog.hasOwnProperty('method')) {
      console.log("=-=-=-=-=-=-==- NATIVE METHOD =-=-=-=--==-=")
    }
    if (seekSeconds === 0 && libmpvLog.text && libmpvLog.text.indexOf('Starting playback') !== -1) {
      //setSeekSeconds(300)
    }
    if (libmpvLog.text && libmpvLog.text.indexOf('Opening failed or was aborted') !== -1) {
      setError("Unable to open file.")
    }
    if (libmpvLog.text && libmpvLog.prefix === 'vd' && libmpvLog.text.indexOf('Using software decoding') !== -1) {
      //setError("Unable to use hardware decoding!.")
    }

    console.log({ libmpvLog })
  }

  const onPress = () => {
    setIsPlaying(!isPlaying)
    if (nativeRef.current) {
      console.log("_----__-__ Running command")
      nativeRef.current.runMpvCommand(`set|sub-ass-override|force`);
      nativeRef.current.runMpvCommand(`set|sub-font-size|${20 + Math.floor(Math.random() * 10)}`)
    }
  }


  const animeUrl = 'http://juggernaut.9914.us/tv/anime/precure/Star ☆ Twinkle Precure/Season 1/S01E006 - An Imagination of Darkness! The Dark Pen Appears!.mkv'
  const videoUrl = animeUrl;
  return (
    <Modal style={styles.container} onRequestClose={() => { navigation.goBack() }}>
      <TouchableOpacity
        transparent
        style={styles.button}
        onPress={onPress} >
        <LibmpvVideo
          ref={nativeRef}
          isPlaying={isPlaying}
          playUrl={videoUrl}
          useHardwareDecoder={true}
          surfaceWidth={resolutions.fullHd.width}
          surfaceHeight={resolutions.fullHd.height}
          onLibmpvEvent={onLibmpvEvent}
          onLibmpvLog={onLibmpvLog}
          selectedAudioTrack={0}
          selectedSubtitleTrack={0}
          seekToSeconds={seekSeconds}
        />
      </TouchableOpacity>
    </Modal>
  )
}


const Stack = createNativeStackNavigator();

export default function App() {
  //const surfaceRef = React.useRef(null);
  //const [playing, setPlaying] = React.useState(false)

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="Home" component={LandingPage} />
        <Stack.Screen name="Video" component={VideoPage} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

