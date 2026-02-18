# PracticaFinalPAT
Practica final de PAT, Por: Carlos Gárate, Raffaella Boccacci, Martina Iscar, Amalia Varo y Irene Morales
A continuación se hará una breve explicación de cada una de las clases.

# Controllers
## AuthController 
Tenemos los siguientes endpints:
1. Post /register donde se autentifica y crear el nuevo usuario y por defecto se crea con el rol de usuario. Si intentas crearte otra cuenta con el mismo correo te salta un error.
2. Post /login te permite con el email y la contraseña acceder.
3. Post /logout te permite cerrar sesión.
4. Get /me te permite obtener tu perfil aunque no hayas hecho el login.

## UserController
Es un controlador REST para gestionar los perfiles de usuarios del sistema de pádel, interactuando directamente con el AlmacenDatos.
Sus funciones clave son:
- Gestión de Perfiles: Permite listar todos los usuarios, consultar un perfil específico y actualizar datos mediante PATCH.
- Seguridad Granular: Implementa reglas de acceso inteligentes:
- listarUsuarios: Solo accesible para el ADMIN.
- obtener / actualizar: Accesible para el ADMIN o para el propio usuario dueño de la cuenta (authentication.name == #userId).
- Integridad de Datos: Valida que el cuerpo de la petición sea correcto (@Valid) y prohíbe explícitamente modificar el ID del usuario para evitar inconsistencias.
- Persistencia Volátil: Las actualizaciones se guardan directamente en el mapa estático de AlmacenDatos, permitiendo que los cambios se mantengan mientras la aplicación esté ejecutándose.

## Pista Controller
Es un controlador REST para gestionar pistas de pádel con un CRUD completo bajo la ruta /pistaPadel/courts.
Sus funciones clave son:
- Operaciones: Permite listar, ver detalles, crear, modificar (vía PATCH) y eliminar pistas.
- Seguridad: Restringe la creación, edición y borrado solo a usuarios con rol ADMIN.
- Validación: Comprueba que los datos recibidos sean correctos (@Valid) y bloquea cambios accidentales de ID.
- Trazabilidad: Registra en el log tanto las acciones exitosas como los errores de validación.

# Data
## Alamcén Datos
Es un almacén de datos en memoria que simula una base de datos mediante mapas estáticos (ConcurrentHashMap).
Propósito: Centralizar y precargar información de prueba (Semillas/Seeds).
Contenido: Define roles (Admin/User), usuarios, pistas, horarios disponibles y reservas activas.
Seguridad: Usa colecciones concurrentes para evitar errores si varios usuarios acceden a la vez.

Acceso: No se puede instanciar (constructor privado); se accede a los datos de forma directa y global.
# Tareas Programadas
Esta clase TareasProgramadas es un servicio de automatización de Spring que ejecuta procesos en segundo plano de forma periódica sin intervención humana.
Es un @Service que utiliza @Scheduled con expresiones Cron para disparar tareas en momentos específicos.
- Tarea 1: Recordatorios Diarios (2:00 AM): Recorre todas las reservas del AlmacenDatos. Si una reserva coincide con la fecha de "hoy", identifica al usuario para enviarle un aviso (actualmente solo deja el rastro en el log).
- Tarea 2: Boletín Mensual (Día 1 a las 9:00 AM): Genera un resumen de la disponibilidad de todas las pistas y lo "envía" (mediante logs) a todos los usuarios que estén marcados como activos en el sistema.
Dependencia: Se apoya totalmente en los datos estáticos de AlmacenDatos para obtener la información de usuarios, pistas y reservas.
