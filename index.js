/**
 * @format
 */

import { AppRegistry } from 'react-native';
import LoginScreen from './src/screens/LoginScreen';
import HomeScreen from './src/screens/HomeScreen';
import TransferScreen from './src/screens/TransferScreen';
import MovementsScreen from './src/screens/MovementsScreen';

AppRegistry.registerComponent('login', () => LoginScreen);
AppRegistry.registerComponent('home', () => HomeScreen);
AppRegistry.registerComponent('transfer', () => TransferScreen);
AppRegistry.registerComponent('movements', () => MovementsScreen);