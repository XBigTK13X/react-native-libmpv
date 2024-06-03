import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import Libmpv, { SurfaceView } from 'react-native-libmpv';

export default function App() {
  const surfaceRef = React.useRef(null);
  const [playing, setPlaying] = React.useState(false)
  const testingURL = "http://192.168.1.20:9064/media/movies/testing/Ocean's Eleven (2001)/Ocean's Eleven (2001) WEBDL-480p.mkv"

  return (
    <View style={styles.container}>
      <Text>Video player below</Text>
      <SurfaceView style={styles.videoPlayer} playUrl={testingURL}></SurfaceView>
      <Text>Video player above</Text>
    </View>
  );
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
    width: 300,
    height: 300
  }
});
