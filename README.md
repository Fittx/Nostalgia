# Nostalgia 📸

A JavaFX-based nostalgic photo capture application with advanced OpenCV image processing capabilities.

## 🎯 Features

- **Real-time Camera Preview**: Live camera feed with OpenCV processing
- **Photo Capture**: Take and save photos with advanced image processing
- **Image Filters**: Apply nostalgic effects and filters to your photos
- **Gallery View**: Browse through captured photos with thumbnail navigation
- **Cross-Platform**: Works on Windows

## 📋 System Requirements

- **Java Development Kit (JDK)**: Version 17 or newer (24 Recommended)
- **Maven**: Version 3.6 or newer
- **OpenCV**: Version 4.5.1 (native installation required)
- **Camera**: Built-in or external webcam
- **Memory**: At least 4GB of RAM (8GB RAM recommended)
- **Operating System**: Windows 10+, macOS 10.14+, or Ubuntu 18.04+

## 🚀 Installation Guide

### Step 1: Install Java and Maven

```bash
# Verify Java version (must be 17+)
java -version
javac -version

# Verify Maven installation
mvn -version
```

If not installed:
- **Java**: Download from [Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.org/)
- **Maven**: Download from [Apache Maven](https://maven.apache.org/download.cgi)

### Step 2: OpenCV Native Installation (Critical!)

⚠️ **Important**: The Maven OpenCV dependency alone is not sufficient. You must install OpenCV natively on your system.

#### Windows Installation

1. **Download OpenCV**
   ```bash
   # Download OpenCV 4.5.1 from: https://opencv.org/releases/
   # Get: opencv-4.5.1-vc14_vc15.exe
   ```

2. **Extract and Setup**
   ```bash
   # Extract to: C:\opencv\
   # Verify structure: C:\opencv\build\java\x64\opencv_java451.dll
   ```

3. **Set Environment Variables**
   ```bash
   # Add System Environment Variable:
   OPENCV_DIR = C:\opencv\build
   
   # Add to PATH:
   C:\opencv\build\x64\vc15\bin
   C:\opencv\build\java\x64
   ```


### Step 3: Clone and Setup Project

```bash
git clone https://github.com/Fittx/Nostalgia.git
cd Nostalgia
```

### Step 4: Verify OpenCV Installation

```bash
# Windows
dir "C:\opencv\build\java\x64\opencv_java451.dll"

# macOS
ls -la /opt/homebrew/lib/libopencv_java451.dylib

# Linux
ls -la /usr/local/lib/libopencv_java451.so
```

### Step 5: Install Project Dependencies

```bash
mvn clean install
```

## 🏃‍♂️ Running the Application

### Method 1: Using Run Scripts (Recommended)

Create platform-specific run scripts in the project root:

**Windows (run.bat):**
```batch
@echo off
set JAVA_LIBRARY_PATH=C:\opencv\build\java\x64
mvn clean javafx:run -Djava.library.path=C:\opencv\build\java\x64
```

Then run:
```bash

# Windows
run.bat
```

### Method 2: Direct Maven Command

**Windows:**
```batch
mvn clean javafx:run -Djava.library.path=C:\opencv\build\java\x64
```

### Method 3: IDE Configuration

**IntelliJ IDEA:**
1. Go to `Run → Edit Configurations`
2. Add VM options: `-Djava.library.path=/path/to/opencv/lib`
3. Set main class: `com.example.nostalgiaapp.HelloApplication`

**Eclipse:**
1. Right-click project → `Run As → Run Configurations`
2. `Arguments` tab → VM arguments: `-Djava.library.path=/path/to/opencv/lib`

## ✅ Verification

When the application starts successfully, you should see:
```
✅ OpenCV library loaded successfully using nu.pattern.OpenCV
```

## 🛠️ Troubleshooting

### Common OpenCV Errors

#### `UnsatisfiedLinkError: no opencv_java451 in java.library.path`

**Solutions:**
1. Verify OpenCV library exists at the expected location
2. Check Java library path configuration
3. Ensure proper file permissions

```bash
# Check Java library path
java -XshowSettings:properties 2>&1 | grep java.library.path
```

#### `Can't load library` Error

**Windows:**
```batch
# Add OpenCV bin to PATH
set PATH=C:\opencv\build\x64\vc15\bin;%PATH%
```

### Camera Issues

**Windows:**
Check camera privacy settings in `Windows Settings → Privacy → Camera`

### Performance Optimization

```bash
# Increase Java heap size
export MAVEN_OPTS="-Xmx4g -Djava.library.path=/path/to/opencv/lib"
mvn clean javafx:run
```

## 🏗️ Project Structure

```
Nostalgia/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/nostalgiaapp/
│       │       ├── CameraSessionPage.java     # Camera functionality
│       │       ├── DownloadPage.java         # Download functionality
│       │       ├── ImportPage.java          # Import functionality
│       │       ├── NostalgiaApp.java         # Main application class
│       │       ├── OpenCVUtil.java          # OpenCV utility functions
│       │       └── PhotoEditingPage.java  # Photo editing functionality
│       └── resources/
│           └── com/example/nostalgiaapp/
├── pom.xml                                    # Maven configuration
├── module-info.java                           # Java module configuration
└── README.md                                  # This file
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 Dependencies

- **JavaFX 17.0.6**: UI framework
- **OpenCV 4.5.1-2**: Computer vision library
- **ControlsFX 11.2.1**: Additional UI controls
- **JUnit 5.10.2**: Testing framework



| Dependency | Current Version | Latest Version | Status |
|------------|----------------|----------------|--------|
| **JavaFX Controls** | 17.0.6 | 24.0.1 | Update recommended |
| **JavaFX FXML** | 17.0.6 | 24.0.1 | Update recommended |
| **ControlsFX** | 11.2.1 | 11.2.2 | Update recommended |
| **OpenCV (OpenPnP)** | 4.5.1-2 | 4.9.0-0 | Update recommended |
| **JUnit Jupiter** | 5.10.2 | 5.10.2 | Up to date |


## 🐛 Issues and Support

If you encounter any issues:

1. Check the [existing issues](https://github.com/Fittx/Nostalgia/issues)
2. Verify OpenCV installation using the troubleshooting guide above
3. Create a new issue with:
    - Operating system and version
    - Java version (`java -version`)
    - Maven version (`mvn -version`)
    - Complete error message
    - Console output showing OpenCV loading status

## 📸 Screenshots

*Add screenshots of your application here*

---

**Note**: This application requires proper OpenCV native library installation. The Maven dependency `org.openpnp.opencv` provides Java bindings but still requires native OpenCV libraries to be installed and accessible through the Java library path.

Made with ❤️ by [Fittx](https://github.com/Fittx)