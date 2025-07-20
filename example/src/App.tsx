import * as React from 'react';
import { View, Button, Modal, TouchableOpacity, AppState } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { LibmpvVideo, Libmpv } from 'react-native-libmpv';

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
  const libmpvRef = React.useRef(null)
  React.useEffect(() => {
    const navListener = navigation.addListener('beforeRemove', (e) => {
      console.log("=-=-=-=-=-=-                             =-=-=-=-=-=-");
      console.log("=-=-=-=-=-=-Libmpv should have cleaned up=-=-=-=-=-=-");
      console.log("=-=-=-=-=-=-                             =-=-=-=-=-=-");
      if (libmpvRef.current) {
        libmpvRef.current.cleanup()
      }
    })
    return () => {
      navigation.removeListener('beforeRemove', navListener)
    }
  }, [])

  React.useEffect(() => {
    const appStateSubscription = AppState.addEventListener('change', appState => {
      if (appState === 'background') {
        console.log("Cleanup background")
        if (libmpvRef.current) {
          libmpvRef.current.cleanup()
        }
        navigation.goBack()
      }
    });

    return () => {
      appStateSubscription.remove();
    };
  }, []);

  function onLibmpvEvent(libmpvEvent) {
    if (!libmpvEvent.property || libmpvEvent.property !== 'track-list') {
      console.log({ libmpvEvent })
    }
  }

  function onLibmpvLog(libmpvLog) {
    if (libmpvLog.hasOwnProperty('method')) {
      console.log("=-=-=-=-=-=-==- NATIVE METHOD =-=-=-=--==-=")
    }
    if (seekSeconds === 0 && libmpvLog.text && libmpvLog.text.indexOf('audio ready') !== -1) {
      setSeekSeconds(300)
    }
    console.log({ libmpvLog })
  }
  const videoUrl = 'http://juggernaut.9914.us/tv/cartoon/b/Bluey (2018) [Australia]/Season 1/S01E001 - Magic Xylophone.mkv'
  console.log({ videoUrl })
  return (
    <Modal style={styles.container} onRequestClose={() => { navigation.goBack() }}>
      <TouchableOpacity
        transparent
        style={styles.button}
        onPress={() => { setIsPlaying(!isPlaying) }} >
        <LibmpvVideo
          ref={libmpvRef}
          isPlaying={isPlaying}
          playUrl={videoUrl}
          surfaceWidth={1920}
          surfaceHeight={1080}
          onLibmpvEvent={onLibmpvEvent}
          onLibmpvLog={onLibmpvLog}
          selectedAudioTrack={0}
          selectedSubtitleTrack={-1}
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

