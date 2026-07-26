# 🛠️ Building from source

### **Requirements:** 
- JDK 21 (Temurin recommended)
- Bundled Apache Maven 3.9.15 (included under `resources/apache-maven-3.9.15/`)

### **Build:** 
**Windows (PowerShell)**
```
.\resources\apache-maven-3.9.15\bin\mvn.cmd
```
**Linux / macOS**
```
./resources/apache-maven-3.9.15/bin/mvn
```

The shaded plugin jar will be produced in `target/Oneblock-*.jar`.
