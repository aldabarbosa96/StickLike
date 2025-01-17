# StickLike

StickLike es un **videojuego roguelike** con combate estilo *autobattle*, diseñado con una **estética minimalista** inspirada en el cuaderno de un estudiante de la ESO.  
El proyecto utiliza el framework [libGDX](https://libgdx.com/) para ofrecer una experiencia fluida y multiplataforma.

---

## Descripción

En StickLike te sumerges en un mundo **dibujado a mano**, donde el escenario de combate es el **cuaderno** de un adolescente, y tú encarnas a un 'StickMan' que debe **sobrevivir** a oleadas de enemigos variopintos, ganar experiencia y escoger mejoras cada vez que subes de nivel.

El juego se inspira en el género **roguelike** y en títulos como *Vampire Survivors*, donde el combate es automático (*autobattle*), pero tú decides cómo **mover** a tu personaje y qué **mejoras** adquirir. Tu objetivo es **aguantar** hasta que termine el temporizador y te enfrentes al **FINALBOSS**.

---

## Características principales

- **Estética de cuaderno**: Todo el mundo del juego está concebido como dibujos en una libreta de un estudiante. Los enemigos y objetos pueden ser chistes, criaturas extrañas y obscenas… ¡cualquier cosa que uno dibujaría en clase!
- **Progresión de personaje**: Al derrotar enemigos, obtienes experiencia que te permite **subir de nivel** y escoger entre varias mejoras (por ejemplo, más velocidad, más daño, proyectiles múltiples, nuevas armas, etc.).
- **Combate automático**: Tu personaje ataca a los enemigos de forma automática, pero tú decides **el movimiento** para esquivar y recolectar **objetos de experiencia**.
- **Enemigos variopintos**: Cada oleada puede traer diseños disparatados, haciendo honor al espíritu libre del cuaderno y a lo "tontos que somos cuando tenemos 15 años".
- **Sistema de mejoras**: Cuando subes de nivel, aparece un **pop-up** con 3 mejoras a elegir. Escoge sabiamente para sobrevivir a las oleadas.
- **FINALBOSS**: Cuando el tiempo llega a cierto punto, te enfrentas al **FINALBOSS** que pondrá a prueba tus habilidades y configuración de personaje.
- **Hecho con libGDX**: Permite ejecutar el juego en múltiples plataformas (Desktop, Android, etc.) con un único código base.

---

## Controles y Mecánicas

- **Movimiento**: Usa **W, A, S, D** para desplazar al personaje por la hoja/cuaderno (en un futuro el input se procesará con un mando).
- **Subir de nivel**: Al recolectar experiencia suficiente, aparece un pop-up con mejoras. Pulsa **1, 2, o 3** para seleccionar la mejora.
- **Objetos de experiencia**: Al derrotar enemigos (p. ej. "culo"), algunos sueltan objetos (p. ej. “caca”) que al recogerlos te dan experiencia.
- **Pausa para mejoras**: El juego se **pausa** al subir de nivel para que puedas planear con calma la estrategia, luego se reanuda la acción.
- **Objetivo**: Sobrevivir hasta el final del temporizador y derrotar al **FINALBOSS**.

---

## Estructura de Proyecto actual

**main
├── java
│   └── com
│       └── sticklike
│           ├── core
│           │   ├── effects
│           │   └── entities
│           │       ├── Enemy
│           │       ├── InGameText
│           │       ├── Player
│           │       ├── Projectile
│           │       └── XObjects
│           ├── managers
│           │   ├── EnemyManager
│           │   ├── ProjectileManager
│           │   └── UpgradeManager
│           ├── renderers
│           │   └── GridRenderer
│           ├── screens
│           │   ├── GameOverScreen
│           │   └── GameScreen
│           ├── systems
│           │   └── LevelingSystem
│           ├── ui
│           │   └── HUD
│           ├── mejoras
│           │   └── Upgrade
│           ├── utils
│           │   ├── AssetLoader
│           │   └── GameConfig
│           └── MainGame
└── resources
    ├── actions
    │   ├── movement
    │   └── spriteSheets
    ├── drops
    ├── enemies
    ├── hud
    ├── jugador
    └── weapons**


---

## Cómo ejecutar

1. **Clona** o descarga este repositorio.
2. Ábrelo en tu IDE preferido (IntelliJ, Eclipse…) con Gradle.
3. Asegúrate de que `libGDX` se configura correctamente (gradle wrapper) y que las dependencias se descarguen.
4. Ejecuta la clase principal (`MainGame`) para jugar en PC.

---

## Estado de Desarrollo

- **Fase temprana**: StickLike cuenta con mecánicas básicas de movimiento, subida de nivel y oleadas de enemigos. El juego se encuentra en una fase muy alpha de desarrollo pero se seguirá trabajando duro hasta completarlo con éxito y cumplir con los objetivos y expectativas.
- **En proceso**:
    - Sistema de niveles
    - Nuevos enemigos, mejoras, assets y niveles.
    - Jefe final.
    - Mejorar interfaz y HUD.
    - Sistema de menú y pausa.
    - Menú configuración.

---

## Contribuciones

**NO SE PUEDEN NI SE QUIERE ACEPTAR SUGERENCIAS**
