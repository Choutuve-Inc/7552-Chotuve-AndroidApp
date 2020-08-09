# 7552-Chotuve-AndroidApp
Choutuve, like YouTube, but without quality (Android Client)

# Setup del Ambiente

Para hacer correr la aplicación correctamente se necesita:

- Crear un nuevo proyecto [Firebase](https://console.firebase.google.com/) y reemplazar el archivo google-services.json en la carpeta app/ con el generado por Firebase

- Dentro del proyecto Firebase, se tiene que habilitar el uso de Storage y crear en root 3 carpetas: thumbnails/, userPic/ y videos/. Puede que sea necesario también deshabilitar las reglas.

- Dentro del proyecto Firebase, se tiene también que habilitar el uso de Realtime Database y deshabilitar las reglas.

- Por último, en el archivo /app/src/main/java/com/app/chotuve/context/ApplicationContext.kt se debe de cambiar la URL de la constante appServer por la dirección del Application Server que será utilizado.

# Manual de Usuario

El manual de usuario de la aplicación Android se puede consultar en el sigueinte link: [manual de usuario](https://docs.google.com/presentation/d/1qDSi2QvLG1aHNembWJedmxVbYJgigHkYh1XG3EMsmLw/edit?usp=sharing)
