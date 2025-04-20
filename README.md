# Build-To-Order Management System

![UML Class Diagram](https://img.shields.io/badge/UML%20Class%20Diagram-1976D2?style=for-the-badge&logoColor=white)
![UML Sequence Diagram](https://img.shields.io/badge/UML%20Sequence%20Diagram-1976D2?style=for-the-badge&logoColor=white)
![Solid Design Principles](https://img.shields.io/badge/SOLID%20Design%20Principles-C71A36?style=for-the-badge&logoColor=white)
![OOP Concepts](https://img.shields.io/badge/OOP%20Concepts-C71A36?style=for-the-badge&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)

**Team:** [<img src="https://github.com/Danielmark001.png" height="20" width="20" /> Daniel Mark](https://github.com/Danielmark001) |
[<img src="https://github.com/WilliamN40.png" height="20" width="20" /> William Notowibowo](https://github.com/WilliamN40) |
[<img src="https://github.com/Hweewoon151.png" height="20" width="20" /> Lim Hwee Woon](https://github.com/Hweewoon151)
[<img src="https://github.com/CekCong.png" height="20" width="20" /> Soh Cek Cong](https://github.com/CekCong)


**Docs:** [Report](https://github.com/Danielmark001/sc2002-btoms/blob/main/report.png) | 
[UML Class Diagram](https://github.com/Danielmark001/sc2002-btoms/blob/main/uml%20diagram/uml-class-diagram.pdf) |
[UML Sequence Diagram](https://github.com/Danielmark001/sc2002-btoms/blob/main/uml%20diagram/uml-sequence-diagram.png) | 
[Java Docs](https://Danielmark001.github.io/sc2002_btoms/module-summary.html)

A Java-based application system for applicants and HDB staffs to view, apply and manage for BTO projects. This README file provides instructions on how to clone, compile, and run the project.

## Table of Contents

- [Build-to-Order Management System](#build-to-order-management-system)
  - [Table of Contents](#table-of-contents)
- [BTOMS setup instructions](#btoms-setup-instructions)
  - [Compiling and Running the project](#compiling-and-running-the-project)
    - [Using the terminal](#using-the-terminal)
    - [Using Eclipse](#using-eclipse)
  - [Generating JavaDocs](#generating-javadocs)
    - [Using the terminal](#using-the-terminal-1)
    - [Using Eclipse](#using-eclipse-1)
- [Usage](#usage)
  - [Login Credentials](#login-credentials)

# BTOMS setup instructions

## Compiling and Running the project

### Using the terminal

These setup instructions will guide you through the process of cloning the repository, navigating to the cloned repository, compiling the project, and running the project in your terminal.

1. Open your terminal

2. Clone the repository by entering the following command:

   ```bash
   git clone https://github.com/Danielmark001/sc2002-btoms.git
   ```

3. Navigate to the cloned repository by entering the following command:

   ```bash
   cd sc2002-btoms
   ```

4. Compile the project by entering the following command:

   ```bash
   javac -cp src -d bin src/main/BtomsApp.java
   ```

5. Run the project by entering the following command:

   ```bash
   java -cp bin main.BtomsApp
   ```

Congratulations, you have successfully cloned, compiled, and run the BTOMS project!

### Using Eclipse

If you prefer to use Eclipse as your IDE, you can also set up the project there. Here are the steps you need to follow:

1. Open Eclipse
2. Click on `File` > `Import` > `Git` > `Projects from Git` > `Clone URI`
3. In the `Clone URI` window, paste the following URL:

   ```bash
   https://github.com/Danielmark001/sc2002-btoms.git
   ```

4. Click `Next` and follow the prompts to finish the cloning process
5. Once the project is cloned, right-click on the project folder and select `Properties`
6. In the `Properties` window, click on `Java Build Path` > `Source` > `Add Folder`
7. Select the `src` folder from the project directory and click `OK`
8. Now you can run the project by right-clicking on `BtomsApp.java` in the `src/main` folder and selecting `Run As` > `Java Application`

That's it! You should now have the project up and running in Eclipse.

## Generating JavaDocs

### Using the terminal

Follow the steps below to generate JavaDocs using the terminal:

1. Open you terminal.
2. Navigate to the root directory of the project.
3. Run the following command in the terminal:

   ```bash
    javadoc -d docs -sourcepath src -subpackages controllers:enums:interfaces:main:models:services:stores:utils:views -private
   ```

   This command will generate the JavaDocs and save them in the docs directory in HTML format.

4. Navigate to the `docs` directory using the following command:

   ```bash
   cd docs
   ```

5. Open the `index.html` file in a web browser to view the generated JavaDocs.

Congratulations, you have successfully created and viewed the JavaDocs.

### Using Eclipse

1. Open the Eclipse IDE and open your Java project.

2. Select the package or class for which you want to generate JavaDocs.

3. Go to the "Project" menu and select "Generate Javadoc".

4. In the "Generate Javadoc" dialog box, select the "Private" option to generate JavaDocs for private classes and members.

5. Choose the destination folder where you want to save the generated JavaDocs.

6. In the "Javadoc command line arguments" field, add any additional arguments that you want to include, such as `-classpath`.

7. Click the "Finish" button to start the JavaDocs generation process.

8. Once the JavaDocs have been generated, you can view them by opening the `index.html` file in your web browser.

Congratulations, you have successfully created and viewed the JavaDocs.

# Usage

## Login Credentials

This section contains some login credentials for users with different roles. The full list is available in `data/user.csv` file.

**Applicants:**

```bash
# Applicant 1
Name: John
NRIC: S1234567A
Age: 35  
Marital Status: Single
Password: password

# Applicant 2
Name: Sarah
NRIC: T7654321B
Age: 40
Marital Status: Married
Password: password

# Student 3
Name: Grace
NRIC: S9876543C
Age: 37
Marital Status: Married
Password: password
```

**HDB Officer:**

```bash
# HDB Officer 1
Name: Daniel
NRIC: T2109876H
Age: 36
Marital Status: Single
Password: password

# HDB Officer 2
Name: Emily
NRIC: S6543210I  
Age: 28
Marital Status: Single
Password: password
```

**HDB Manager:**
```bash
# HDB Manager 1
Name: Michael
NRIC: T8765432F
Age: 36
Marital Status: Single
Password: password
```
