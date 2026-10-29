# Solute Client

A client-side utility mod for **Minecraft 1.20.1**, built with Fabric.

Solute Client brings together a custom in-game menu, visual enhancements, HUD utilities, and integration with **Adventures in Time (AIT)** for TARDIS management.

## Features

- **Custom GUI** — A dark interface with purple accents.
- **Visual tools** — Visual enhancements and configurable utilities.
- **HUD utilities** — Convenient in-game information and tools.
- **AIT Integration** — Interact with supported TARDIS commands directly through the client menu.
- **TARDIS Management** — Refresh and navigate detected TARDISes, lock or unlock them, adjust fuel, and summon them when supported by the server.

## Compatibility

- Minecraft: `1.20.1`
- Mod loader: Fabric
- Environment: Client-side
- Java: 17
- Dependency: Adventures in Time (AIT), for AIT integration features

## Installation

1. Install Fabric Loader for Minecraft 1.20.1.
2. Install the required dependencies.
3. Download the Solute Client `.jar` file from the project's releases.
4. Place the `.jar` file in your Minecraft `mods` folder.
5. Launch Minecraft using your Fabric profile.

## AIT Integration

AIT features depend on the installed AIT version, server support, and your permissions.

Solute Client does not replace AIT's server-side functionality. Commands that modify TARDIS settings may require operator privileges or other permissions.

## Building from Source

Make sure Java 17 is installed.

Clone the repository:

```bash
git clone https://github.com/Solut3/SoluteClient
cd SoluteMenu
```

Build the mod:

```bash
./gradlew build
```

The compiled mod should be available in `build/libs/`.

## License

This project
