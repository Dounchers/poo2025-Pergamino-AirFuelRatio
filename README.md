# poo2025-Pergamino-AirFuelRatio
POO 2025 - Pergamino - AirFuelRatio
Analía Santomé
Francisco Galucci
Robertino Mollo
Joaquín Fernández

En el proyecto usamos Maven Wrapper así evitamos problemas con la versión del Maven instalado en cada máquina.
Una vez que clonen el repo se mueven a demo 
cd demo
./mvnw clean install (mvnw.cmd clean install en Windows)
Esto descarga maven y todas las dependencias necesarias del proyecto.
Asegurensé de hacer esto para que todos usemos la misma versión de Maven.

En el gitignore agregué el .idea/ que es una carpeta de config de InteliJ y target/ que son archivos compilados que no debían estar en el repo.
