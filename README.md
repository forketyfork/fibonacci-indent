# Fibonacci Indent Plugin

An IntelliJ IDEA plugin that provides Fibonacci-based indentation functionality for enhanced code readability and structure.

## Overview

The Fibonacci Indent plugin automatically applies indentation levels based on the Fibonacci sequence (2, 3, 5, 8, 13, 21, ...), excluding the initial 0 and 1 values. This creates a visually distinctive indentation pattern that can help developers better understand code structure and nesting levels.

## Features

- **Fibonacci Indentation**: Automatically applies indentation using Fibonacci sequence spacing
- **Multi-language Support**: Works with all programming languages supported by IntelliJ IDEA
- **Configurable Settings**: Customize indentation behavior through plugin settings
- **Real-time Application**: Indentation is applied as you type
- **Undo/Redo Support**: Full integration with IntelliJ's undo/redo system

## Fibonacci Sequence

The plugin uses the following indentation levels:
- Level 1: 2 spaces
- Level 2: 3 spaces  
- Level 3: 5 spaces
- Level 4: 8 spaces
- Level 5: 13 spaces
- Level 6: 21 spaces
- And so on...

## Installation

### From JetBrains Marketplace (Coming Soon)
1. Open IntelliJ IDEA
2. Go to `File` → `Settings` → `Plugins`
3. Search for "Fibonacci Indent"
4. Click `Install` and restart IntelliJ IDEA

### Manual Installation
1. Download the latest release from the [Releases](https://github.com/forketyfork/fibonacci-indent/releases) page
2. Open IntelliJ IDEA
3. Go to `File` → `Settings` → `Plugins`
4. Click the gear icon and select `Install Plugin from Disk...`
5. Select the downloaded `.zip` file
6. Restart IntelliJ IDEA

### Build from Source
```bash
git clone https://github.com/forketyfork/fibonacci-indent.git
cd fibonacci-indent
./gradlew build
```

## Usage

Once installed, the plugin will automatically apply Fibonacci indentation when:
- Creating new indentation levels
- Auto-formatting code
- Using code completion that creates nested structures

### Configuration

Access plugin settings through:
`File` → `Settings` → `Editor` → `Fibonacci Indent`

Available options:
- Enable/disable Fibonacci indentation
- Configure maximum indentation level
- Set fallback indentation for levels beyond Fibonacci sequence
- Language-specific settings

## Requirements

- IntelliJ IDEA 2025.1 or later
- Java 21 or later

## Development

This plugin is built using:
- **Language**: Kotlin
- **Build System**: Gradle with IntelliJ Platform Gradle Plugin
- **Target Platform**: IntelliJ IDEA 2025.1+
- **Java Version**: 21

### Building the Plugin

```bash
# Build the plugin
./gradlew build

# Run in development IDE
./gradlew runIde

# Run tests
./gradlew test

# Build distribution
./gradlew buildPlugin
```

For detailed development guidelines, see [guidelines.md](.junie/guidelines.md).

## Contributing

We welcome contributions! Please see our [implementation plan](PLAN.md) for current development status and upcoming features.

### Development Setup

1. Clone the repository
2. Open in IntelliJ IDEA
3. Ensure Java 21 is configured
4. Run `./gradlew build` to verify setup

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/forketyfork/fibonacci-indent/issues)
- **Discussions**: [GitHub Discussions](https://github.com/forketyfork/fibonacci-indent/discussions)
- **Documentation**: [Plugin Documentation](https://github.com/forketyfork/fibonacci-indent/wiki)

## Changelog

### Version 1.0.0 (In Development)
- Initial release with basic Fibonacci indentation functionality
- Support for all IntelliJ-supported languages
- Configurable settings panel
- Real-time indentation application

## Acknowledgments

- Built with the [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- Inspired by mathematical beauty in code formatting
- Thanks to the IntelliJ IDEA plugin development community
