# Colorful Delimiter

A JetBrains IDE plugin that colors matching delimiters with the same color to improve code readability across all JetBrains IDEs.

## Features

- **Delimiter Matching**: Colors matching parentheses `()`, curly braces `{}`, and square brackets `[]` with the same color
- **Quote Matching**: Colors matching quotes `'`, `"`, and `` ` `` with the same color
- **Triple Quote Support**: Handles triple quotes `'''`, `"""`, and ``` ``` for multi-line strings
- **Empty String Handling**: Properly handles empty strings like `""`, `''`, and `''''''`
- **Comment Awareness**: Skips delimiters and quotes inside comments (`//`, `/* */`, `#`)
- **String Content Protection**: Prevents coloring delimiters inside string content
- **Vivid Colors**: Uses 20 distinct, highly visible colors for better contrast
- **Multi-Language Support**: Works with 50+ programming languages including Java, Python, JavaScript, C++, PHP, Ruby, Go, C#, Kotlin, and many others
- **Multi-IDE Support**: Compatible with all JetBrains IDEs

## Examples

```java
function example() {
    if (condition) {
        array[index] = "string with (braces) inside";
        print('another string');
        multiline = """
            triple quote string
            with {braces} inside
        """;
    }
}
```

In the above example:
- The outer `()` and `{}` pairs will each have their own unique colors
- The `[]` brackets will have their own color
- Each pair of quotes will have distinct colors
- Delimiters inside strings won't be colored
- Comments are ignored

## Installation

### Method 1: Download Pre-built Plugin

1. Download the latest `colorful-delimiter-1.0.0.zip` from the releases
2. In your JetBrains IDE, go to **File > Settings > Plugins**
3. Click the gear icon ⚙️ and select **Install Plugin from Disk...**
4. Select the downloaded ZIP file
5. Restart your IDE
6. The plugin will automatically start coloring your delimiters!

### Method 2: Build from Source

#### Prerequisites
- **JDK 11 or higher** (Oracle JDK, OpenJDK, or similar)
- **Git** for cloning the repository
- **Any JetBrains IDE** for testing (IntelliJ IDEA, WebStorm, PyCharm, etc.)

#### Step-by-Step Compilation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/ileeric/colorful_delimiter.git
   cd colorful_delimiter
   ```

2. **Build the Plugin**
   ```bash
   # On macOS/Linux
   ./gradlew clean buildPlugin
   
   # On Windows
   gradlew.bat clean buildPlugin
   ```

3. **Locate the Built Plugin**
   The plugin ZIP file will be generated at:
   ```
   build/distributions/colorful-delimiter-1.0.0.zip
   ```

4. **Install in JetBrains IDE**
   - Open any JetBrains IDE (IntelliJ IDEA, WebStorm, PyCharm, CLion, etc.)
   - Go to **File > Settings > Plugins** (or **IntelliJ IDEA > Preferences > Plugins** on macOS)
   - Click the gear icon ⚙️ and select **Install Plugin from Disk...**
   - Navigate to and select `build/distributions/colorful-delimiter-1.0.0.zip`
   - Click **OK** and restart your IDE

5. **Verify Installation**
   - Open any code file with brackets, braces, or quotes
   - You should see matching delimiters colored with the same vivid colors
   - Different nesting levels will have different colors

#### Troubleshooting Build Issues

- **Java Version**: Ensure you're using JDK 11 or higher
  ```bash
  java -version
  ```
- **Gradle Issues**: If gradle fails, try:
  ```bash
  ./gradlew clean
  ./gradlew --refresh-dependencies buildPlugin
  ```
- **IDE Compatibility**: The plugin supports JetBrains IDEs 2023.2 and later

### From JetBrains Plugin Repository

*Coming soon - plugin will be published to the official JetBrains Plugin Repository*

## Development

### Prerequisites

- JDK 11 or higher
- Any JetBrains IDE 2023.2 or higher for development

### Building

```bash
# Clean build
./gradlew clean

# Build plugin
./gradlew buildPlugin

# Run plugin in development IDE
./gradlew runIde
```

### Project Structure

```
src/main/java/me/seungjun/colorfuldelimiter/
├── DelimiterMatcher.java      # Core logic for finding matching delimiters/quotes
├── ColorScheme.java           # Color palette and assignment logic
└── ColorfulDelimiterAnnotator.java  # IntelliJ annotation system integration

src/main/resources/META-INF/
└── plugin.xml                # Plugin configuration and metadata
```

### Key Components

- **DelimiterMatcher**: Stack-based algorithm to find matching delimiters and quotes while handling comments and strings
- **ColorScheme**: Manages 20 vivid colors with proper light/dark theme support
- **ColorfulDelimiterAnnotator**: Integrates with IntelliJ's annotation system for syntax highlighting

## Compatibility

- **JetBrains IDEs**: All JetBrains IDEs 2023.2 and later
  - IntelliJ IDEA (Ultimate/Community)
  - WebStorm
  - PyCharm (Professional/Community)
  - CLion
  - PHPStorm
  - RubyMine
  - GoLand
  - Rider
  - Android Studio
  - DataGrip
  - AppCode
  - MPS
- **Languages**: 50+ programming languages including:
  - Java, Kotlin, Groovy, Scala
  - JavaScript, TypeScript, HTML, CSS, SCSS, LESS, Vue, Angular
  - Python, Django, Jinja2
  - C, C++, Objective-C
  - PHP, Blade, Twig
  - Ruby, RHTML
  - Go
  - C#, VB.NET, F#
  - SQL
  - Bash, PowerShell, Dockerfile
  - Markdown, LaTeX, YAML, JSON, XML
  - Rust, Swift, Dart, Lua, Perl, Haskell, Erlang, Elixir, Clojure
  - And many more...
- **Themes**: Works with both light and dark themes

## License

This project is licensed under the GNU Lesser General Public License v3.0 (LGPL-3.0) - see the [LICENSE](LICENSE) file for details.

### License Summary

The LGPL-3.0 license allows you to:
- ✅ **Use** the software for any purpose
- ✅ **Modify** the source code
- ✅ **Distribute** copies of the software
- ✅ **Distribute** modified versions
- ✅ **Use** the software in proprietary applications

Requirements:
- 📋 **Include** the original copyright notice
- 📋 **Include** the license text
- 📋 **Disclose** source code changes if distributed
- 📋 **Use** the same license for derivative works of the library itself

The LGPL-3.0 is designed to be business-friendly while ensuring that improvements to the library remain open source.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Author

- **Seungjun Lee** - [GitHub](https://github.com/ileeric)
- Email: work@seungjun.me