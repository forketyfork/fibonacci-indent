# Fibonacci-indent Plugin Development Guidelines

## Project Overview

This is an IntelliJ IDEA plugin project called "Fibonacci-indent" built with Kotlin and the IntelliJ Platform Gradle
Plugin. 
The plugin provides Fibonacci-based indentation functionality where each indentation level uses spaces
corresponding to the Fibonacci sequence (2, 3, 5, 8, 13, 21, ...), excluding the initial 0 and 1 values.

## Plugin Functionality

The Fibonacci Indent plugin automatically applies indentation levels based on the mathematical Fibonacci sequence:

- **Level 1**: 2 spaces
- **Level 2**: 3 spaces
- **Level 3**: 5 spaces
- **Level 4**: 8 spaces
- **Level 5**: 13 spaces
- **Level 6**: 21 spaces
- **And so on...**

This creates a visually distinctive indentation pattern that helps developers better understand code structure and
nesting levels.

## Build/Configuration Instructions

### Prerequisites

- Java 21 (required for compilation and runtime)
- Gradle (use the included wrapper `./gradlew`)

### Key Configuration Files

- **build.gradle.kts**: Main build configuration using IntelliJ Platform Gradle Plugin 2.6.0
- **gradle.properties**: Contains important Gradle optimizations:
    - `kotlin.stdlib.default.dependency=false` - Kotlin stdlib is excluded (IntelliJ plugin requirement)
    - `org.gradle.configuration-cache=true` - Configuration cache enabled for performance
    - **gradle.caching=true** - Build cache enabled for faster builds
- **plugin.xml**: Plugin metadata and configuration

### Build Commands

```bash
# Build the plugin
./gradlew build

# Clean build
./gradlew clean build

# Compile only
./gradlew compileKotlin compileTestKotlin
```

### Important Build Notes

1. **Plugin Version**: Currently targets IntelliJ IDEA Community Edition 2025.1
2. **Compatibility**: `sinceBuild = "251"` (IntelliJ 2025.1+)
3. **Plugin Update**: IntelliJ Platform Gradle Plugin is up to date (2.6.0)

### Running Tests

1. **Compilation**: `./gradlew compileTestKotlin` - Tests compile successfully
2. **Execution**: The standard `./gradlew test` has issues with IntelliJ Platform framework conflicts

### Adding New Tests

1. Place test files in `src/test/kotlin/me/forketyfork/fibonacciindent/`
2. Use simple Kotlin assertions rather than external testing frameworks

## Development Information

### Development guidelines

- **ALWAYS** run the build and test after implementing your task
- **ALWAYS** make sure that the files `PLAN.md`, `README.md` and `.junie/guidelines.md` are up to date after your
  changes; check off the tasks in `PLAN.md` as you go.
- We have **ZERO** toleration to any warnings during compilation and build. Make sure to fix them or create a task in PLAN.md to address them.

### Code Style

- **Language**: Kotlin with Java 21 target
- **Package Structure**: `me.forketyfork.fibonacciindent`
- **Plugin ID**: `me.forketyfork.fibonacci-indent`

### Development Considerations

1. **IntelliJ Platform**: This is an IntelliJ IDEA plugin, so familiarize yourself with:
    - IntelliJ Platform SDK documentation
    - Plugin development guidelines
    - Extension points and services

2. **Gradle Configuration**:
    - Uses IntelliJ Platform Gradle Plugin for plugin-specific tasks
    - Configuration cache and build cache are enabled for performance
    - Kotlin stdlib is explicitly excluded per IntelliJ requirements

3. **Java Version**:
    - Project requires Java 21
    - Both source and target compatibility is set to "21"
    - Kotlin JVM target is "21"

### Plugin Structure

- **Main Resources**: `src/main/resources/META-INF/`
    - `plugin.xml`: Plugin configuration
    - `pluginIcon.svg`: Plugin icon
- **Source Code**: `src/main/kotlin/me/forketyfork/fibonacciindent/` (directory structure set up, implementation files to be added in Phase 1.2+)
- **Tests**: `src/test/kotlin/me/forketyfork/fibonacciindent/`

### Debugging Tips

1. Use IntelliJ IDEA's plugin development features
2. Enable verbose logging in plugin.xml if needed
3. Test plugin in a separate IntelliJ instance using `./gradlew runIde`
4. Check IntelliJ Platform documentation for specific plugin development patterns
