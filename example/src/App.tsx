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
    renderCount += 1
  })

  function onLibmpvEvent(libmpvEvent) {
    console.log({ renderCount, libmpvEvent })
  }

  function onLibmpvLog(libmpvLog) {
    console.log({ renderCount, libmpvLog })
  }
  const videoUrl = 'http://tv-tuner.9914.us:5004/auto/v4.1'
  return (
    <View style={styles.container}>
      <LibmpvVideo
        isPlaying={isPlaying}
        playUrl={videoUrl}
        onLibmpvEvent={onLibmpvEvent}
        onLibmpvLog={onLibmpvLog}
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

