import * as React from 'react';
import { StyleSheet, View, Button, Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { LibmpvVideo, Libmpv } from 'react-native-libmpv';

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
  }
});

function LandingPage({ navigation }) {
  return (
    <View style={styles.container}>
      <Button onPress={() => { navigation.navigate('Video') }} title="Play Video" />
    </View>
  )
}


function VideoPage({ navigation }) {
  let renderCount = Math.floor(Math.random() * 10000)

  const [isPlaying, setIsPlaying] = React.useState(true);
  const [cleanup, setCleanup] = React.useState(false);
  const [isReady, setIsReady] = React.useState(false);
  const [seekSeconds, setSeekSeconds] = React.useState(0);

  React.useEffect(() => {
    if (!cleanup) {
      navigation.addListener('beforeRemove', (e) => {
        console.log("=-=-=-=-=-=-                             =-=-=-=-=-=-");
        console.log("=-=-=-=-=-=-Libmpv should have cleaned up=-=-=-=-=-=-");
        console.log("=-=-=-=-=-=-                             =-=-=-=-=-=-");
        Libmpv.cleanup()
        return
      })
      setCleanup(true)
    }
    if (!isReady) { }
    renderCount += 1
  })

  function onLibmpvEvent(libmpvEvent) {
    console.log({ renderCount, libmpvEvent })
  }

  function onLibmpvLog(libmpvLog) {
    if (!isReady) {
      setTimeout(() => {
        setIsReady(true);
        setSeekSeconds(300);
      }, 2000);
    }
    if (libmpvLog.hasOwnProperty('method')) {
      console.log("=-=-=-=-=-=-==- NATIVE METHOD =-=-=-=--==-=")
    }
    console.log({ renderCount, libmpvLog })
  }
  const videoUrl = 'http://juggernaut.9914.us/anime/m/My Happy Marriage/Season 2/S02E007 - TBA.mkv'
  return (
    <View style={styles.container}>
      <LibmpvVideo
        isPlaying={isPlaying}
        playUrl={videoUrl}
        onLibmpvEvent={onLibmpvEvent}
        onLibmpvLog={onLibmpvLog}
        selectedAudioTrack={1}
        selectedSubtitleTrack={1}
        seekToSeconds={seekSeconds}
      ></LibmpvVideo>
      <Button title="Toggle Playing" onPress={() => { setIsPlaying(!isPlaying) }} />
    </View>
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

