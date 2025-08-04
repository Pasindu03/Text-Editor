# Text Editor Android Application

A powerful and feature-rich text editor for Android with support for multiple file formats and advanced editing capabilities.

## Features

### Basic Editor Functionality
- **Open, Save, New file functionality** - Complete file management with support for various file formats
- **Automatic saving** - Files are automatically saved every 30 seconds when modified
- **File extension handling** - Support for multiple file types including:
  - `.txt` - Plain text files
  - `.kt` - Kotlin source files
  - `.java` - Java source files
  - `.xml` - XML files
  - `.json` - JSON files
  - `.md` - Markdown files
  - `.html` - HTML files
  - `.css` - CSS files
  - `.js` - JavaScript files
  - `.py` - Python files
  - `.cpp`, `.c`, `.h`, `.hpp` - C/C++ files
  - `.sql` - SQL files
  - `.sh` - Shell scripts
  - `.yml`, `.yaml` - YAML files

### Text Editing Features
- **Copy, Paste, Cut** - Standard text editing operations
- **Undo/Redo** - Full undo/redo functionality with unlimited history
- **Text selection** - Multi-line text selection support
- **Character and Word Counting** - Real-time statistics including:
  - Character count
  - Word count
  - Line count
  - Paragraph count

### Search and Replace
- **Find functionality** - Search through text with real-time highlighting
- **Replace functionality** - Replace individual occurrences or all matches
- **Advanced search options**:
  - Case-sensitive search
  - Whole word matching
  - Navigation between search results
  - Search result counter

### User Interface
- **Modern Material Design 3** - Beautiful and intuitive interface
- **Responsive layout** - Adapts to different screen sizes
- **Dark/Light theme support** - Automatic theme switching
- **Settings screen** - Configurable editor preferences
- **File status indicators** - Shows modified status with asterisk (*)

## Technical Details

### Architecture
- **MVVM Pattern** - Model-View-ViewModel architecture
- **Jetpack Compose** - Modern declarative UI framework
- **Kotlin Coroutines** - Asynchronous operations
- **StateFlow** - Reactive state management
- **Navigation Compose** - Type-safe navigation

### Key Components
- `TextEditorViewModel` - Main business logic and state management
- `TextDocument` - Data model for documents
- `TextStatistics` - Text analysis and counting
- `SearchOptions` - Search and replace configuration
- `FileUtils` - File operations and utilities
- `TextEditorScreen` - Main editor interface
- `SettingsScreen` - Configuration interface

### File Operations
- **DocumentFile API** - Modern Android file access
- **Content Provider** - Secure file handling
- **Auto-save** - Automatic file preservation
- **Error handling** - Graceful error recovery

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.8 or higher

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

### Permissions
The application requires the following permissions:
- `READ_EXTERNAL_STORAGE` - To read files
- `WRITE_EXTERNAL_STORAGE` - To save files
- `MANAGE_EXTERNAL_STORAGE` - For comprehensive file access

## Usage

### Creating a New File
1. Tap the "+" icon in the toolbar
2. Start typing your content
3. Use "Save As" to save with a specific name and location

### Opening a File
1. Tap the folder icon in the toolbar
2. Navigate to and select your file
3. The file will open with syntax highlighting based on its extension

### Saving Files
- **Save** - Saves to the current file location (if available)
- **Save As** - Creates a new file with custom name and location
- **Auto-save** - Automatically saves every 30 seconds when modified

### Search and Replace
1. Tap the search icon to open the search panel
2. Enter your search term
3. Use the navigation buttons to move between results
4. Enable "Case sensitive" or "Whole word" options as needed
5. Use "Replace" or "Replace All" to modify text

### Settings
1. Tap the settings icon to access configuration
2. Toggle auto-save functionality
3. Configure editor preferences
4. View application information

## Development

### Project Structure
```
app/src/main/java/com/example/texteditor/
├── data/                    # Data models
│   ├── TextDocument.kt
│   ├── TextStatistics.kt
│   └── SearchOptions.kt
├── viewmodel/              # ViewModels
│   └── TextEditorViewModel.kt
├── ui/
│   ├── components/         # UI components
│   │   ├── TextEditorScreen.kt
│   │   ├── AdvancedTextEditor.kt
│   │   └── SettingsScreen.kt
│   ├── navigation/         # Navigation
│   │   └── TextEditorNavigation.kt
│   └── theme/             # UI theme
├── utils/                  # Utilities
│   └── FileUtils.kt
└── MainActivity.kt
```

### Adding New Features
1. Create data models in the `data` package
2. Add business logic to the ViewModel
3. Create UI components in the `ui/components` package
4. Update navigation if needed
5. Add tests for new functionality

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design 3 for the beautiful UI components
- Jetpack Compose for the modern UI framework
- Android DocumentFile API for secure file handling 