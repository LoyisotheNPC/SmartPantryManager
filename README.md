\# Smart Pantry Manager



\## 1. Project Overview



Smart Pantry Manager is an Android mobile application developed in Java to help users manage ingredients stored in their pantry and discover recipes they can prepare using the ingredients they have available.



The application allows users to add, view, edit, and delete pantry ingredients, including their quantities, units of measurement, and optional expiry dates. It also provides recipe browsing and ingredient-based recipe suggestions.



The main objective of the application is to reduce food waste, improve pantry organisation, and make meal planning easier.



\## 2. Features



\### Pantry Management

\- Add ingredients to the pantry.

\- View the current pantry ingredient list.

\- Edit existing ingredient details.

\- Delete ingredients that are no longer needed.

\- Record ingredient quantities and units of measurement.

\- Optionally record ingredient expiry dates.

\- Choose whether expiry dates are displayed through the Settings screen.



\### Recipe Suggestions

\- Suggest recipes based on the ingredients available in the pantry.

\- Require all recipe ingredients to be available before suggesting a recipe.

\- Check ingredient quantities to ensure there is enough stock to prepare a recipe.

\- Account for supported unit conversions, including grams and kilograms, and millilitres and litres.

\- Provide feedback when no recipes match the available ingredients.



\### Recipe Browsing

\- Browse the available recipe collection.

\- Open a recipe to view its required ingredients and preparation instructions.

\- Navigate between the pantry, recipe list, and recipe detail screens.



\### Settings

\- Enable or disable the display of ingredient expiry dates.

\- Save the selected expiry-date display preference.



\## 3. Technologies Used



\- \*\*Programming language:\*\* Java

\- \*\*Development environment:\*\* Android Studio

\- \*\*Database:\*\* SQLite

\- \*\*User interface:\*\* Android Views and Java

\- \*\*Navigation:\*\* Android Activities and Intents

\- \*\*Local data storage:\*\* SQLite database and SharedPreferences

\- \*\*Version control:\*\* Git and GitHub



\## 4. Application Screens



The application includes the following screens:



1\. \*\*Pantry Screen:\*\* Displays stored ingredients and provides options to add, edit, and delete ingredients.

2\. \*\*Add/Edit Ingredient Screen:\*\* Allows users to enter or update ingredient names, quantities, units, and optional expiry dates.

3\. \*\*Suggested Recipes Screen:\*\* Displays recipes that satisfy the application's ingredient availability rules.

4\. \*\*Recipe Detail Screen:\*\* Displays a selected recipe's ingredients and preparation instructions.

5\. \*\*All Recipes Screen:\*\* Allows users to browse the complete recipe collection.

6\. \*\*Settings Screen:\*\* Allows users to configure the display of ingredient expiry dates.



\## 5. Database Design



The application uses SQLite to store pantry information and recipe data locally on the device.



The database contains the following main tables:



\### Pantry Ingredients

Stores information about ingredients available in the user's pantry.



Typical fields include:

\- Ingredient ID

\- Ingredient name

\- Quantity

\- Unit of measurement

\- Optional expiry date



\### Recipes

Stores the available recipe collection.



Typical fields include:

\- Recipe ID

\- Recipe name

\- Recipe description or preparation information



\### Recipe Ingredients

Connects recipes to their required ingredients and quantities.



Typical fields include:

\- Recipe ID

\- Ingredient name

\- Required quantity

\- Unit of measurement



The recipe ingredients table allows a single recipe to contain multiple ingredients and allows an ingredient to be used in multiple recipes.



\## 6. Strict Recipe Matching



One of the main features of Smart Pantry Manager is its strict recipe-matching system.



A recipe is suggested only when all its required ingredients are present in the pantry and the available quantities are sufficient to prepare it.



For example, if a recipe requires two eggs and the pantry contains only one egg, that recipe must not be suggested.



Similarly, if a recipe requires an ingredient that is completely missing from the pantry, the recipe remains unavailable.



The application also supports selected unit conversions, such as grams to kilograms and millilitres to litres, to help compare compatible quantities.



This approach ensures that recipe suggestions reflect the ingredients and quantities the user actually has available.



\## 7. Data Persistence



Pantry ingredients are stored in a local SQLite database. This allows ingredient information to remain available after the application is closed and reopened.



The application uses SharedPreferences to store the user's expiry-date display preference.



Local storage allows the application to work with its saved pantry and recipe data without requiring a remote database connection.



\## 8. Validation and Error Handling



The application includes validation and feedback to help users enter ingredient information correctly and understand recipe availability.



Examples include:

\- Checking ingredient details before saving.

\- Validating supported ingredient quantities and units.

\- Preventing recipes from being suggested when ingredients or quantities are insufficient.

\- Displaying feedback when no recipes match the pantry contents.

\- Allowing expiry dates to be optional.



\## 9. Installation and Setup



\### Requirements

\- Android Studio

\- A compatible Android SDK

\- An Android emulator or compatible Android device

\- Git, if cloning the project repository



\### Steps

1\. Clone or download this repository from GitHub.

2\. Open the project in Android Studio.

3\. Allow Android Studio to synchronise the Gradle project.

4\. Connect an Android device or start an emulator.

5\. Run the application using the Run button in Android Studio.



The application uses a local SQLite database, so a separate database server is not required.



\## 10. Testing



The following core behaviours were tested during development:



\- Adding ingredients to the pantry.

\- Editing and deleting stored ingredients.

\- Preventing a recipe from being suggested when the pantry contains one egg but the recipe requires two.

\- Keeping a recipe unavailable when a required ingredient is missing.

\- Suggesting a recipe when all required ingredients and sufficient quantities are available.

\- Preserving pantry data after closing and reopening the application.

\- Saving the expiry-date display preference.



\## 11. Version Control



Git and GitHub are used to track the development of Smart Pantry Manager.



Commits document meaningful stages of development, including project setup, pantry management, recipe database functionality, recipe suggestions, recipe details, recipe browsing, and settings.



Repository: https://github.com/LoyisotheNPC/SmartPantryManager



\## 12. Future Improvements



Possible future improvements include:

\- Implementing expiry reminders and notifications.

\- Improving the visual design and accessibility of the user interface.

\- Adding recipe search and filtering.

\- Allowing users to create their own recipes.

\- Adding more flexible ingredient matching and unit conversions.

\- Providing additional food-waste reduction features.



\## 13. Conclusion



Smart Pantry Manager demonstrates how an Android application can combine a graphical user interface, local database storage, data validation, and ingredient-based decision-making to solve a practical everyday problem.



The project provides experience in Java programming, Android Activities and Intents, SQLite database operations, persistent settings, testing, and Git-based version control.



The application can be extended in the future with additional features to improve usability and help users manage food more efficiently.



