# 📦 InventarioManager

Aplicación de gestión de inventarios que permite organizar, localizar y compartir objetos dentro de estructuras personalizadas de zonas.

---

## 🧠 Descripción

**InventarioManager** es una app diseñada para resolver un problema muy común:

> saber qué tienes, dónde está y cómo está organizado.

Permite gestionar inventarios tanto a nivel personal como en entornos compartidos como institutos, empresas o equipos de trabajo.

La aplicación destaca por su sistema de **zonas jerárquicas**, donde puedes estructurar espacios de forma flexible:

Ejemplo:

* Instituto

  * Piso A

    * Clase 1A

Además, puedes decidir si tus zonas son privadas o compartidas con otros usuarios, facilitando la colaboración.

---

## 📸 Capturas

*(Añadir aquí imágenes de la app cuando estén disponibles)*

---

## 🚀 Funcionalidades

* 📁 CRUD de zonas (local con Room y remoto con Firestore)
* 📦 Gestión de artículos (crear, eliminar, consultar)
* 🔄 Registro de movimientos de artículos
* 👥 Compartir zonas con otros usuarios
* ➕ Añadir y eliminar usuarios en zonas
* 🔐 Autenticación con Firebase (login y registro)
* ☁️ Sincronización entre almacenamiento local y remoto

---

## 🛠️ Tecnologías

* Kotlin
* Jetpack Compose
* Firebase Firestore
* Firebase Authentication
* Room (base de datos local)
* Navigation Compose

---

## 🧱 Arquitectura

El proyecto sigue principios de **Clean Architecture** y patrón **MVVM**:

* **Data** → acceso a datos (Room + Firestore)
* **Domain** → lógica de negocio
* **Presenter** → gestión de estado de UI
* **UI** → pantallas con Compose

Gestión de estado basada en:

* `ViewModel`
* `UiState`
* `UiEvent`
* `UiEffect`

También incluye:

* Manejo correcto de corrutinas (`suspend`)
* Separación clara de responsabilidades
* Navegación tipada mediante objetos

---

## 📦 Instalación

1. Descargar el APK
2. Instalar en el dispositivo Android

*(En caso de usar el código: configurar Firebase con `google-services.json`)*

---

## 🎯 Estado del proyecto

Proyecto funcional y terminado.
Se podrían añadir mejoras, pero actualmente cumple con todos los objetivos principales.

---

## 🧠 Retos y aprendizaje

Durante el desarrollo, los principales retos fueron:

* 🔄 Gestionar la sincronización entre **Room y Firestore**
* 🧩 Diseñar un sistema flexible para trabajar con datos locales y remotos
* 🧭 Implementar navegación basada en objetos
* 🧠 Encontrar una arquitectura que permitiera escalar sin complicaciones

Aprendizajes clave:

* Mejor comprensión de la sincronización de datos
* Gestión avanzada de estado en Compose
* Diseño de apps híbridas (offline + online)

