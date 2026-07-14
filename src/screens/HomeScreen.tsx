import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { sendToNative, listenFromNative } from '../native/eventBridge';

const HomeScreen = (props: any) => {
    const [userName, setUserName] = useState(props.userName || 'Usuario');
    const [balance, setBalance] = useState(props.balance || '0');

    useEffect(() => {
        const subscription = listenFromNative('LOAD_HOME', (data: string) => {
            try {
                const parsed = JSON.parse(data);
                setUserName(parsed.userName || 'Usuario');
                setBalance(parsed.balance || '0');
            } catch (e) {
                console.log('LOAD_HOME parse error', e);
            }
        });
        return () => subscription.remove();
    }, []);

    const handleLogout = () => {
        sendToNative('LOGOUT');
    };

    const handleTransfer = () => {
        sendToNative('OPEN_TRANSFER');
    };

    const handleMovements = () => {
        sendToNative('OPEN_MOVEMENTS');
    };

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.welcome}>Hola, {userName}</Text>
                <TouchableOpacity onPress={handleLogout}>
                    <Text style={styles.logout}>Cerrar sesión</Text>
                </TouchableOpacity>
            </View>

            <View style={styles.balanceCard}>
                <Text style={styles.balanceLabel}>Saldo disponible</Text>
                <Text style={styles.balanceAmount}>${balance}</Text>
            </View>

            <View style={styles.menu}>
                <TouchableOpacity style={styles.menuItem} onPress={handleTransfer}>
                    <Text style={styles.menuText}>Transferir</Text>
                </TouchableOpacity>

                <TouchableOpacity style={styles.menuItem} onPress={handleMovements}>
                    <Text style={styles.menuText}>Movimientos</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        backgroundColor: '#f5f5f5',
    },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 20,
    },
    welcome: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#333',
    },
    logout: {
        color: '#e74c3c',
    },
    balanceCard: {
        backgroundColor: '#1A5276',
        padding: 20,
        borderRadius: 12,
        marginTop: 20,
    },
    balanceLabel: {
        color: '#ECF0F1',
        fontSize: 14,
    },
    balanceAmount: {
        color: '#fff',
        fontSize: 28,
        fontWeight: 'bold',
        marginTop: 5,
    },
    menu: {
        marginTop: 30,
    },
    menuItem: {
        backgroundColor: '#fff',
        padding: 15,
        borderRadius: 8,
        marginBottom: 10,
    },
    menuText: {
        fontSize: 16,
        color: '#333',
    },
});

export default HomeScreen;