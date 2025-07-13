import * as React from 'react';
import { StyleSheet, View, Button, Modal, TouchableOpacity } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { LibmpvVideo, Libmpv } from 'react-native-libmpv';

const styles = StyleSheet.create({
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
});

function LandingPage({ navigation }) {
  return (
    <Modal style={styles.container}>
      <View style={styles.button}>
        <Button onPress={() => { navigation.navigate('Video') }} title="Play Video" />
      </View>
    </Modal>
  )
}


function VideoPage({ navigation }) {
  let renderCount = Math.floor(Math.random() * 10000)

  const [isPlaying, setIsPlaying] = React.useState(true);
  const [cleanup, setCleanup] = React.useState(false);
  const [seekSeconds, setSeekSeconds] = React.useState(0)

  React.useEffect(() => {
    if (!cleanup) {
      navigation.addListener('beforeRemove', (e) => {
        console.log("=-=-=-=-=-=-                             =-=-=-=-=-=-");
        console.log("=-=-=-=-=-=-Libmpv should have cleaned up=-=-=-=-=-=-");
        console.log("=-=-=-=-=-=-                             =-=-=-=-=-=-");
        Libmpv.cleanup()
      })
      setCleanup(true)
    }
    renderCount += 1
  })

  function onLibmpvEvent(libmpvEvent) {
    console.log({ renderCount, libmpvEvent })
  }

  function onLibmpvLog(libmpvLog) {
    if (libmpvLog.hasOwnProperty('method')) {
      console.log("=-=-=-=-=-=-==- NATIVE METHOD =-=-=-=--==-=")
    }
    if (seekSeconds === 0 && libmpvLog.text && libmpvLog.text.indexOf('audio ready') !== -1) {
      setSeekSeconds(300)
    }
    console.log({ renderCount, libmpvLog })
  }
  const videoUrl = 'http://juggernaut.9914.us/tv/cartoon/b/Bluey (2018) [Australia]/Season 1/S01E001 - Magic Xylophone.mkv'
  return (
    <Modal style={styles.container} onRequestClose={() => { navigation.goBack() }}>
      <TouchableOpacity
        transparent
        style={styles.button}
        onPress={() => { setIsPlaying(!isPlaying) }} >
        <LibmpvVideo
          isPlaying={isPlaying}
          playUrl={videoUrl}
          surfaceWidth={1920}
          surfaceHeight={1080}
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

