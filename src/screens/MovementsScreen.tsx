import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    FlatList,
    StyleSheet,
    Alert,
    ActivityIndicator,
} from 'react-native';
import { NativeModules } from 'react-native';

const { BankBridge } = NativeModules;

const MovementsScreen = () => {
    const [movements, setMovements] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadMovements();
    }, []);

    const loadMovements = () => {
        setLoading(true);
        BankBridge.getSession((success: boolean, session: any) => {
            if (success && session) {
                fetch('http://10.0.2.2:5001/api/movements', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ sessionId: session.sessionId }),
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        setMovements(data.movements);
                    } else {
                        Alert.alert('Error', data.message || 'Error al cargar movimientos');
                    }
                })
                .catch(() => {
                    Alert.alert('Error', 'Error de conexión');
                })
                .finally(() => setLoading(false));
            } else {
                Alert.alert('Error', 'Sesión inválida');
                setLoading(false);
            }
        });
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    };

    const getTypeColor = (type: string) => {
        return type === 'CREDITO' ? '#27AE60' : '#E74C3C';
    };

    const getTypeIcon = (type: string) => {
        return type === 'CREDITO' ? '+' : '-';
    };

    const renderItem = ({ item }: any) => (
        <View style={styles.movementItem}>
            <View style={styles.movementHeader}>
                <Text style={styles.movementDate}>{formatDate(item.date)}</Text>
                <Text style={[styles.movementValue, { color: getTypeColor(item.type) }]}>
                    {getTypeIcon(item.type)} ${item.value.toLocaleString()}
                </Text>
            </View>
            <Text style={styles.movementDescription}>{item.description}</Text>
            <View style={styles.movementFooter}>
                <Text style={styles.movementType}>{item.type}</Text>
                <Text style={[
                    styles.movementStatus,
                    item.status === 'EXITOSO' ? styles.statusSuccess : styles.statusFailed
                ]}>
                    {item.status}
                </Text>
            </View>
        </View>
    );

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Movimientos</Text>

            {loading ? (
                <View style={styles.loadingContainer}>
                    <ActivityIndicator size="large" color="#1A5276" />
                    <Text style={styles.loadingText}>Cargando mvimientos...</Text>
                </View>
            ) : movements.length === 0 ? (
                <View style={styles.emptyContainer}>
                    <Text style={styles.emptyText}>No hay movimientos</Text>
                </View>
            ) : (
                <FlatList
                    data={movements}
                    renderItem={renderItem}
                    keyExtractor={(item) => item.id}
                    contentContainerStyle={styles.listContent}
                    showsVerticalScrollIndicator={false}
                />
            )}

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
        marginBottom: 20,
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    loadingText: {
        marginTop: 10,
        color: '#666',
    },
    emptyContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    emptyText: {
        fontSize: 16,
        color: '#666',
    },
    listContent: {
        paddingBottom: 20,
    },
    movementItem: {
        backgroundColor: '#fff',
        padding: 15,
        borderRadius: 8,
        marginBottom: 10,
    },
    movementHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 5,
    },
    movementDate: {
        fontSize: 14,
        color: '#666',
    },
    movementValue: {
        fontSize: 16,
        fontWeight: 'bold',
    },
    movementDescription: {
        fontSize: 14,
        color: '#333',
        marginBottom: 8,
    },
    movementFooter: {
        flexDirection: 'row',
        justifyContent: 'space-between',
    },
    movementType: {
        fontSize: 12,
        color: '#666',
        backgroundColor: '#ECF0F1',
        paddingHorizontal: 8,
        paddingVertical: 2,
        borderRadius: 4,
    },
    movementStatus: {
        fontSize: 12,
        paddingHorizontal: 8,
        paddingVertical: 2,
        borderRadius: 4,
    },
    statusSuccess: {
        color: '#27AE60',
        backgroundColor: '#E8F8F0',
    },
    statusFailed: {
        color: '#E74C3C',
        backgroundColor: '#FDEDEC',
    },
    backButton: {
        marginTop: 15,
        alignItems: 'center',
        paddingVertical: 10,
    },
    backText: {
        color: '#1A5276',
        fontSize: 16,
    },
});

export default MovementsScreen;