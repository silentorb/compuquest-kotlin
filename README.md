# CompuQuest

A 3D strategy RPG created with Godot and Kotlin

## Development Setup

As [godot-kotlin-jvm](https://github.com/utopia-rise/godot-kotlin-jvm) is in Alpha, some temporary workarounds are being made:

```powershell
New-Item -Path src -ItemType SymbolicLink -Value ../compuquest/src
New-Item -Path build/libs -ItemType SymbolicLink -Value ../../compuquest/build/libs
```

To build, run this in the `compuquest` folder:

```powershell
./gradlew build
```

For debugging from IntelliJ IDEA, I setup a run bundle of run configurations:

* Multirun
  * Before launch of the MultiRun, Run Gradle task `compuquest build`
  * Within the Multirun
    * Run an npm run configuration that runs `package.json dev`
    * A Remote JVM Debug configuration with default settings