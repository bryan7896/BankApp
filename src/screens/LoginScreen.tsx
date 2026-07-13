import React, { useState } from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    Alert,
    StyleSheet,
} from 'react-native';
import { NativeModules } from 'react-native';

const { BankBridge } = NativeModules;


const LoginScreen = () => {
    const [username, setUsername] = useState('bryan');
    const [password, setPassword] = useState('123456');
    const [loading, setLoading] = useState(false);

    const handleLogin = async () => {
        if (!username || !password) {
            Alert.alert('Error', 'Todos los campos son requeridos');
            return;
        }

        setLoading(true);

        try {
            const response = await fetch('http://10.0.2.2:5001/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            if (data.success) {
                const eventData = {
                    sessionId: data.sessionId,
                    userId: data.user.id,
                    userName: data.user.name,
                    phone: data.user.phone,
                    balance: String(data.balance),
                };

                BankBridge.loginSuccess(JSON.stringify(eventData));
            } else {
                Alert.alert('Error', data.message || 'Credenciales inválidas');
            }
        } catch (error) {
            console.log(error); 
            Alert.alert('Error', 'Error de conexión con el servidor');
        } finally {
            setLoading(false);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>8ry</Text>
            <Text style={styles.subtitle}>Iniciar sesión</Text>

            <TextInput
                style={styles.input}
                placeholder="Usuario"
                placeholderTextColor="#999"
                value={username}
                onChangeText={setUsername}
            />

            <TextInput
                style={styles.input}
                placeholder="Contraseña"
                placeholderTextColor="#999"
                secureTextEntry
                value={password}
                onChangeText={setPassword}
            />

            <TouchableOpacity
                style={[styles.button, loading && styles.buttonDisabled]}
                onPress={handleLogin}
                disabled={loading}
            >
                <Text style={styles.buttonText}>
                    {loading ? 'Cargando...' : 'Ingresar'}
                </Text>
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        padding: 20,
        backgroundColor: '#1A5276',
    },
    title: {
        fontSize: 48,
        color: '#fff',
        textAlign: 'center',
        marginBottom: 10,
    },
    subtitle: {
        fontSize: 18,
        color: '#ECF0F1',
        textAlign: 'center',
        marginBottom: 30,
    },
    input: {
        backgroundColor: '#fff',
        padding: 15,
        marginBottom: 15,
        color: '#333',
        borderRadius: 8,
        fontSize: 16,
    },
    button: {
        backgroundColor: '#27AE60',
        padding: 15,
        borderRadius: 8,
        alignItems: 'center',
    },
    buttonDisabled: {
        backgroundColor: '#7F8C8D',
    },
    buttonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: 'bold',
    },
});

export default LoginScreen;