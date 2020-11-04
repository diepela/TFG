<h3 align="center">Integración de Servicios Telemáticos</h3>

<h1 align="center"><b>Periódico digital</b></h1>




<h3 align="center">Máster Universitario en Ingeniería de Telecomunicación</h3>
<h3 align="center">ETSIT-UPV</h3>

# Explicación del Flujo de Trabajo seguido

He escogido trabajar de forma individual en las prácticas de la asignatura, y por lo tanto el flujo de trabajo que he elegido es simple. He decidido que utilizaré varias ramas para la realización de las prácticas, dependientes de dónde realice el trabajo.

Por un lado, tendré la rama `master` para realizar las entregas al final de cada sesión. Por otro lado, he creado dos ramas más, `en_casa` y `en_clase`, que, como sus nombres indican, serán utilizadas para cargar el proceso seguido en casa y en las sesiones de prácticas, respectivamente.

A modo de esquema de funcionamiento, seguiré la siguiente estructura dentro de cada práctica:

```tree

rama `master`
 |  
 ├── rama `en_casa`├─────────── rama `en_clase`
 │    ├                              ├
 │    o (commit 1)                   ├
 │    ├                              o (commit 1...n)
 |    o (commit 2...n)               .
 |    .                              . 
 │    .                              .
 │    .                              │
 │    └──────────────────────────────├ merge `en_casa` y `en_clase`
 │                                   │
 │                                   │
 │                                   │
 └───────────────────────────────────├ merge con `master` y tag `pr1.0`
```

El último commit, con la etiqueta `pr1.0` será el que se entregará como resultado de la práctica.