# Among Us 3D
A 3D remake of the hit game 'Among Us' by Innersloth

## Disclaimer
This is NOT an official version of Among Us, nor is it associated with Among Us or Innersloth in any way.
It does not intend any copyright infringement whatsoever. I'm just a fan of Among Us and wanted to make a 3D version of it :P

## Credits
- Textures and sounds are from the original game
- Map made by "Sketchup Aprendizaje Latinoamerica" on [Sketchfab](https://sketchfab.com/3d-models/among-us-map-the-skeld-59a93886f9e74ff6836dff0c269da45f)

  Thanks for making it free :3

Made with the [Neko 3D engine](https://github.com/Twometer/neko-engine/tree/1.x)

## Building from source
1. Clone the repository
2. Open IntelliJ and choose _Open or Import_
3. Select the repository root folder e.g. `among-us-3d`
4. Click on _Import Gradle project_
5. Download the [Ultralight SDK](https://github.com/ultralight-ux/Ultralight#getting-the-latest-sdk)
6. Extract the contents of the downloaded `bin` folder into `among-us-3d/assets/Natives/Ultralight`
7. In IntelliJ, right-click the class `src/main/java/de.twometer.amongus/Bootstrap` and select _Run 'Bootstrap.main()'_

## Building an installer
Currently only available on Windows.

### Prerequisites
- Inno Setup 6
- Launch4j
- Java 11 SDK

**Important**: If you don't have Java 11 as `java` in your path, you have to specify the `JAVA_HOME` environment variable
to point to the Java 11 SDK home location. Also, if you have Inno Setup or Launch4j not installed in `Program files (x86)`,
you have to point the `SOFTWARE_HOME` environment variable to the directory where those programs are installed.

### Building
1. Open `.\release\setup.iss` and update the version number
2. Open PowerShell
3. Make sure you [can run PowerShell scripts](https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.core/about/about_execution_policies?view=powershell-7.1)
4. Run the `build.ps1` script

The generated installer will be at `.\release\among-us-3d-release\amongus3d-setup.exe`