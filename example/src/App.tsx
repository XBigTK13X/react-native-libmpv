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
  React.useEffect(() => {
    navigation.addListener('beforeRemove', (e) => {
      Libmpv.cleanup()
      return
    })
  })

  function onLibmpvEvent(libmpvEvent) {
    console.log({ libmpvEvent })
  }
  const videoUrl = 'http://192.168.1.5:5004/auto/v4.1'
  return (
    <View style={styles.container}>
      <LibmpvVideo playUrl={videoUrl} onLibmpvEvent={onLibmpvEvent}></LibmpvVideo>
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

