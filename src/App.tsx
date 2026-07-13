/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import { NativeModules, StatusBar } from 'react-native';
import {
  SafeAreaProvider
} from 'react-native-safe-area-context';
import LoginScreen from './screens/LoginScreen';
import HomeScreen from './screens/HomeScreen';
import { useEffect, useState } from 'react';
const { BankBridge } = NativeModules;

function App(): React.JSX.Element {

  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    BankBridge?.getSession((success: boolean, session: any) => {
      if (success && session) {
        setIsLoggedIn(true);
      } else {
        setIsLoggedIn(false);
      }
    });
  }, []);

  return (
    <SafeAreaProvider style={{ flex: 1 }}>
      <StatusBar barStyle="light-content" />
      {isLoggedIn ? (
        <HomeScreen />
      ) : (
        <LoginScreen/>
      )}
    </SafeAreaProvider>
  );
}

export default App;