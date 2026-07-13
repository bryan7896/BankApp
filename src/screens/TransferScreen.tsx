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

const TransferScreen = (props: any) => {
    const [phone, setPhone] = useState('');
    const [amount, setAmount] = useState('');
    const [loading, setLoading] = useState(false);
    const balance = props.balance || '0';

    const handleTransfer = async () => {
        if (!phone || !amount) {
            Alert.alert('Error', 'Todos los campos son requeridos');
            return;
        }

        if (parseFloat(amount) <= 0) {
            Alert.alert('Error', 'El monto debe ser mayor a cero');
            return;
        }

        setLoading(true);

        try {
            BankBridge.getSession((success: boolean, session: any) => {
                if (success && session) {
                    fetch('http://10.0.2.2:5001/api/transfers', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            sessionId: session.sessionId,
                            toPhone: phone,
                            amount: parseFloat(amount),
                        }),
                    })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            Alert.alert('Éxito', 'Transferencia realizada');
                            BankBridge.goBackToHome();
                        } else {
                            Alert.alert('Error', data.message);
                        }
                    })
                    .catch(err => {
                        Alert.alert('Error', 'Error de conexión');
                    })
                    .finally(() => setLoading(false));
                } else {
                    Alert.alert('Error', 'Sesión inválida');
                    setLoading(false);
                }
            });
        } catch (error) {
            Alert.alert('Error', 'Error de conexión');
            setLoading(false);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Transferencia</Text>
            <Text style={styles.balanceText}>Saldo disponible: ${balance}</Text>

            <TextInput
                style={styles.input}
                placeholder="Teléfono destino"
                placeholderTextColor="#999"
                keyboardType="phone-pad"
                value={phone}
                onChangeText={setPhone}
            />

            <TextInput
                style={styles.input}
                placeholder="Monto"
                placeholderTextColor="#999"
                keyboardType="numeric"
                value={amount}
                onChangeText={setAmount}
            />

            <TouchableOpacity
                style={[styles.button, loading && styles.buttonDisabled]}
                onPress={handleTransfer}
                disabled={loading}
            >
                <Text style={styles.buttonText}>
                    {loading ? 'Procesando...' : 'Transferir'}
                </Text>
            </TouchableOpacity>

            <TouchableOpacity
                style={styles.backButton}
                onPress={() => BankBridge.goBackToHome()}
            >
                <Text style={styles.backText}>Volver</Text>
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        backgroundColor: '#f5f5f5',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#1A5276',
        marginTop: 20,
        marginBottom: 10,
    },
    balanceText: {
        fontSize: 16,
        color: '#333',
        marginBottom: 20,
    },
    input: {
        backgroundColor: '#fff',
        padding: 15,
        marginBottom: 15,
        borderRadius: 8,
        fontSize: 16,
        color: '#333',
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
    backButton: {
        marginTop: 15,
        alignItems: 'center',
    },
    backText: {
        color: '#1A5276',
        fontSize: 16,
    },
});

export default TransferScreen;