/**
 * @format
 */

import { AppRegistry } from 'react-native';
import LoginScreen from './src/screens/LoginScreen';
import HomeScreen from './src/screens/HomeScreen';
import TransferScreen from './src/screens/TransferScreen';

AppRegistry.registerComponent('login', () => LoginScreen);
AppRegistry.registerComponent('home', () => HomeScreen);
AppRegistry.registerComponent('transfer', () => TransferScreen);