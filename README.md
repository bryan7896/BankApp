# 8ry - BankApp

Aplicación bancaria móvil desarrollada con **React Native** (bundles) + **Android Kotlin** (nativo), implementando comunicación segura con cifrado, manejo de sesión y navegación controlada desde el lado nativo.

---

## 🏦 Descripción

BankApp es una aplicación Android que carga diferentes bundles de React Native (Login, Home, Transferencia, Movimientos) desde el lado nativo, con comunicación bidireccional mediante Native Modules y EventEmitter. La sesión se maneja de forma segura con Android Keystore y EncryptedSharedPreferences.


## 🚀 Requisitos previos

- Node.js >= 20
- Yarn (recomendado) o npm
- Android Studio con SDK configurado
- JDK 17+
- Emulador Android o dispositivo físico

---

## 📦 Instalación

Clonar el repositorio e instalar dependencias:

```bash
git clone https://github.com/bryan7896/BankApp.git
cd BankApp
yarn install
yarn android
```

El comadno yarn android genera los bundles y levanta el proyecto
"android": "yarn bundle:login 
    && yarn bundle:home 
    && yarn bundle:transfer 
    && yarn bundle:movements 
    && react-native run-android",
