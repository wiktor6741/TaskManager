# TaskManager
A to-do app allowing for splitting tasks into categories, prioritization and deadline setting

## Requirements
* **Java JDK:** 25 or newer

## Getting Started

### Option 1: Using the Pre-built Executable (Recommended)
1. Navigate to the **Releases** section of this repository and download the latest `TaskManager.jar`.
2. **Running via GUI (Icon):**
    * Double-click the `TaskManager.jar` file.
    * **Important:** Ensure that your system's default application for opening `.jar` files is associated with **JDK 25** or newer. 
3. **Running via Terminal:**
    * Open your terminal and verify your Java version:
      ```bash
      java -version
      ```
    * Ensure the output shows version 25 or higher. Then run:
      ```bash
      java -jar TaskManager.jar
      ```
      
### Option 2: Build the binary yourself

1. Clone the repository by running: 
    ```bash
      git clone https://github.com/wiktor6741/TaskManager.git
    ```
2. Navigate to project directory:
    ```bash
      cd TaskManager/project
   ```

3. Run the build command:
    * **macOS / Linux:** `./gradlew jar`
    * **Windows:** `gradlew.bat jar`

4. The jar executable will appear in build/libs/
    
   
