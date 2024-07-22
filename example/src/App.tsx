import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { LibmpvVideo } from 'react-native-libmpv';

export default function App() {
  const surfaceRef = React.useRef(null);
  const [playing, setPlaying] = React.useState(false)
  const mkvUrl = "http://192.168.1.20:9064/media/movies/testing/Ocean's Eleven (2001)/Ocean's Eleven (2001) WEBDL-480p.mkv"
  const frigateUrl = "http://192.168.1.20:8000/api/streamable/direct?streamable_id=58"
  const hdHomeRunUrl = "http://192.168.1.20:8000/api/streamable/direct?streamable_id=1"
  const hdHomeRunUrlTrans = "http://192.168.1.20:8000/api/streamable/transcode?streamable_id=1"
  const iptvUrl = "http://192.168.1.20:8000/api/streamable/direct?streamable_id=124"

  function onLibmpvEvent(libmpvEvent) {
    console.log({ libmpvEvent })
  }

  return (
    <View style={styles.container}>
      <LibmpvVideo playUrl={frigateUrl} onLibmpvEvent={onLibmpvEvent}></LibmpvVideo>
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
  }
});
