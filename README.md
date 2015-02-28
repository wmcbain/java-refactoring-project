# Java Refactoring Project

Final project for Application Development Practices at RIT. Large overhaul and refactoring project of a Java utility for creating tables, columns, and relations in SQL and EDGE formats.

Course topics included development life cycles, build utilities, test bed development, version control, and deployment.

## Refactoring Documentation

The original code, while functional, was inflexible and difficult to maintain. Our refactorings focused on a few key areas: making it easy to add new types of input and output files, breaking the tangled “god class” that was EdgeConvertGUI down into smaller more cohesive pieces, and creating a central mediator to store the state of the program and pass method calls between classes to reduce coupling.

To start with, we created more generic, easier to use Table and Field classes. While the original EdgeTable and EdgeField  stored relationships between fields and tables in arrays where the indexes needed to be kept track of and individual classes were identified by numbers, our refactored Table and Field classes keep track of everything by reference in Lists and Maps. This allows us to treat them as objects and not as complex sets of arrays that need to be accessed in a particular way, reducing coupling. A DataType enum was created to keep track of field data types instead of the previous int.

We then decided that the best way to allow for easy addition of new input and output files would be to load them in as plugins at runtime using JSPF (Java Simple Plugin Framework). To do this, we created two interfaces. The first, FileParser, is responsible for turning the contents of a given file into a Schema, which contains all of the tables found in the file. The rest of the program only needs to know that a class implements this interface and it can dynamically load it in and use it to parse a new type of file easily. We took the existing file parsing functionality of the program (Edge Diagram files and Save files) and put them into FileParsers so that all existing functionality was brought up to date with the refactored design.

The second plugin interface we created was DDLBuilder, which is responsible for taking a given list of Tables and turning it into a string of data definition language. As with FileParser, we converted the existing functionality of save file creation (which was originally in EdgeTable and EdgeField) and the CreateDDLMySQL class into individual DDLBuilders. On startup, the PluginManager class looks in the classpath as well as in a “plugins” directory for any classes that implement FileParser or DDLBuilder and dynamically loads them into the program.

In the original program, a large amount of the logic and state of the application was in one place: EdgeConvertGUI. In order to extract that functionality into separate cohesive classes, it was necessary to provide a way for different parts of the program to communicate without being too tightly coupled.

EdgeConvertMediator solves this problem by providing methods that delegate tasks to specific classes while hiding those classes from the class calling the method. This makes calling methods to perform actions such as loading plugins from a specific directory or showing the “define relations” window easy to do without having knowledge of the classes responsible for performing those actions, decreasing coupling across the program. Additionally, we used the mediator to store the program’s state (the schema that was parsed) in one place instead of passing it around, making it easy to ensure consistency across the application.

To break down the enormous (almost 1300 line) GUI class, into smaller more maintainable pieces, we employed a variety of refactoring strategies. Firstly, we extracted two separate “View” classes, DefineTablesView and DefineRelationsView, each of which is responsible for allowing the user to perform a specific task. We then extracted more functionality from those classes, making ListModels (e.g. SchemaTableListModel) that, given a Schema or Table object (setSchema(schema), setTable(table)), keep their displayed information up to date with the state of the schema. This is achieved with the “Observer” design pattern: Schema, Table, and Field objects are “Observable”, and the various view components that display their state are “Observers”.

Another class that was extracted from the DefineTablesView was the EditFieldView, a panel responsible for allowing the user to edit everything about a given Field (this also follows the Observer pattern). Additionally, we were able to “pull up” functionality from DefineTablesView and DefineRelationsView into EdgeConvertView, which is responsible for shared behaviors such as window sizing, the menu bar, and adding window event handlers. The adding of GUI components and the registration of listeners were extracted into separate methods to improve readability, and various listeners were extracted into their own classes (e.g. MainMenuListener, DataTypeRadioListener, FileOpen).

## Deployment

To deploy our refactored program, we created an installer with PackJacket. We felt that, since a library is required and the plugins are loaded from a “plugins” directory, we should use an installer to create the necessary file structure rather than rely on the user to do it on their own.

Since the program’s function is specific to at most a couple people’s particular needs within an organization, we did not think it necessary to consider remote installation or automatic updating; the users should be technically capable enough to install and update the program on their own.

Since the program is capable of opening and saving many different types of files, we thought it appropriate to give the user a choice during installation as to what types of files they would like the installation to be able to open/save, so we made a checkbox for each type input and output file (all selected by default). Since the program already works across multiple platforms, we made our installer an executable jar file.

## Help System

Since EdgeConvert is a fairly simple application with specific and well defined processes we decided to create a user-guide help system that showed how, step by step, to use the application. Being that such a system could better assist users with the use of images we decided to use a html/css formatted web page to display the information.

We were able to keep the help system in-app by inserting a JavaFX scene onto a swing component and loading the web page from within one of the application’s packages. This would allow updates to the help system to be completed while updating the application itself. Furthermore additions to the help system are in a language and format that most developers know off hand, making it easier to add new processes to the user-guide.

## Team Members

- Wyatt McBain
- David Crocker
