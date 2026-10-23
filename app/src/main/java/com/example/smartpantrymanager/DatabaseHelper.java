
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_PANTRY = "pantry";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPantryTable(db);
        createRecipeTables(db);
        seedRecipes(db);
    }

    private void createPantryTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(sql);
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createRecipeTables(db);
        }

        if (oldVersion < 3) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        String[][] recipes = {
                {"Tomato Rice", "Cook rice. Fry chopped tomato in a little oil, then mix with the cooked rice.",
                        "rice|1|cup", "tomato|2|pieces", "oil|1|tablespoon"},
                {"Egg Fried Rice", "Cook rice. Scramble the eggs in a pan, add rice and stir-fry.",
                        "rice|2|cups", "egg|2|pieces", "oil|1|tablespoon"},
                {"Tomato Pasta", "Boil pasta. Cook tomato with oil, then combine with the drained pasta.",
                        "pasta|200|g", "tomato|2|pieces", "oil|1|tablespoon"},
                {"Simple Omelette", "Beat eggs and cook in a lightly oiled pan until set.",
                        "egg|2|pieces", "oil|1|teaspoon"},
                {"Banana Oatmeal", "Cook oats with milk or water and serve with sliced banana.",
                        "oats|1|cup", "banana|1|piece"},
                {"Peanut Butter Toast", "Spread peanut butter over toasted bread.",
                        "bread|2|slices", "peanut butter|2|tablespoons"},
                {"Cheese Sandwich", "Place cheese between bread slices and toast if desired.",
                        "bread|2|slices", "cheese|2|slices"},
                {"Garlic Pasta", "Boil pasta and toss with garlic cooked gently in oil.",
                        "pasta|200|g", "garlic|2|cloves", "oil|1|tablespoon"},
                {"Potato Mash", "Boil potatoes until soft, mash with milk and butter.",
                        "potato|3|pieces", "milk|0.25|cup", "butter|1|tablespoon"},
                {"Banana Pancakes", "Mash banana, mix with egg and flour, then cook small pancakes.",
                        "banana|1|piece", "egg|1|piece", "flour|0.5|cup"},
                {"Apple Oatmeal", "Cook oats with milk or water and top with chopped apple.",
                        "oats|1|cup", "apple|1|piece"},
                {"Tomato Egg Scramble", "Cook chopped tomato, add beaten eggs and stir until cooked.",
                        "tomato|1|piece", "egg|2|pieces", "oil|1|teaspoon"},
                {"Garlic Rice", "Cook rice and mix with garlic lightly fried in oil.",
                        "rice|1|cup", "garlic|2|cloves", "oil|1|tablespoon"},
                {"Buttered Potatoes", "Boil chopped potatoes and toss with melted butter.",
                        "potato|3|pieces", "butter|1|tablespoon"},
                {"Cheese Omelette", "Beat eggs, add cheese and cook in a lightly oiled pan.",
                        "egg|2|pieces", "cheese|1|slice", "oil|1|teaspoon"},
                {"Milk Porridge", "Cook oats in milk until creamy.",
                        "oats|1|cup", "milk|1|cup"},
                {"Tomato Cheese Toast", "Top bread with tomato and cheese, then toast until warm.",
                        "bread|2|slices", "tomato|1|piece", "cheese|1|slice"},
                {"Peanut Banana Toast", "Spread peanut butter on bread and add sliced banana.",
                        "bread|2|slices", "peanut butter|1|tablespoon", "banana|1|piece"},
                {"Potato Egg Hash", "Dice and cook potatoes in oil, then add egg and cook through.",
                        "potato|2|pieces", "egg|2|pieces", "oil|1|tablespoon"},
                {"Apple Pancakes", "Mix flour, egg and chopped apple into batter and cook in a pan.",
                        "flour|0.5|cup", "egg|1|piece", "apple|1|piece"}
        };

        db.beginTransaction();
        try {
            for (String[] recipe : recipes) {
                ContentValues recipeValues = new ContentValues();
                recipeValues.put("name", recipe[0]);
                recipeValues.put("instructions", recipe[1]);

                long recipeId = db.insertWithOnConflict(
                        "recipes", null, recipeValues, SQLiteDatabase.CONFLICT_IGNORE);

                if (recipeId == -1) {
                    Cursor cursor = db.rawQuery(
                            "SELECT id FROM recipes WHERE name = ?",
                            new String[]{recipe[0]});
                    if (cursor.moveToFirst()) {
                        recipeId = cursor.getLong(0);
                    }
                    cursor.close();
                }

                if (recipeId == -1) {
                    continue;
                }

                for (int i = 2; i < recipe.length; i++) {
                    String[] ingredient = recipe[i].split("\\|");

                    Cursor existing = db.rawQuery(
                            "SELECT id FROM recipe_ingredients " +
                                    "WHERE recipe_id = ? AND ingredient_name = ?",
                            new String[]{String.valueOf(recipeId), ingredient[0]});

                    boolean alreadyExists = existing.moveToFirst();
                    existing.close();

                    if (!alreadyExists) {
                        ContentValues ingredientValues = new ContentValues();
                        ingredientValues.put("recipe_id", recipeId);
                        ingredientValues.put("ingredient_name", ingredient[0]);
                        ingredientValues.put("required_quantity",
                                Double.parseDouble(ingredient[1]));
                        ingredientValues.put("unit", ingredient[2]);

                        db.insert("recipe_ingredients", null, ingredientValues);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long addIngredient(String name, double quantity,
                              String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());
        values.put(COL_EXPIRY, expiryDate);

        return db.insert(TABLE_PANTRY, null, values);
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " COLLATE NOCASE ASC"
        );
    }

    public int updateIngredient(int id, String name,
                                double quantity, String unit,
                                String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, name.trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit.trim());
        values.put(COL_EXPIRY, expiryDate);

        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, name, instructions FROM recipes ORDER BY name COLLATE NOCASE",
                null
        );
    }

    public Cursor getRecipeIngredients(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    public Cursor getSuggestedRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT r.id, r.name, r.instructions " +
                        "FROM recipes r " +
                        "WHERE NOT EXISTS (" +
                        " SELECT 1 FROM recipe_ingredients ri " +
                        " WHERE ri.recipe_id = r.id AND NOT EXISTS (" +
                        "   SELECT 1 FROM pantry p " +
                        "   WHERE LOWER(TRIM(p.name)) = LOWER(TRIM(ri.ingredient_name)) " +
                        "   AND (" +
                        "       (LOWER(TRIM(p.unit)) = LOWER(TRIM(ri.unit)) " +
                        "        AND p.quantity >= ri.required_quantity) " +
                        "       OR " +
                        "       (LOWER(TRIM(p.unit)) IN ('piece','pieces') " +
                        "        AND LOWER(TRIM(ri.unit)) IN ('piece','pieces') " +
                        "        AND p.quantity >= ri.required_quantity) " +
                        "   )" +
                        " )" +
                        ") " +
                        "ORDER BY r.name COLLATE NOCASE",
                null
        );
    }

    public boolean canMakeRecipe(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipe_ingredients ri " +
                        "WHERE ri.recipe_id = ? AND NOT EXISTS (" +
                        " SELECT 1 FROM pantry p " +
                        " WHERE LOWER(TRIM(p.name)) = LOWER(TRIM(ri.ingredient_name)) " +
                        " AND LOWER(TRIM(p.unit)) = LOWER(TRIM(ri.unit)) " +
                        " AND p.quantity >= ri.required_quantity" +
                        ")",
                new String[]{String.valueOf(recipeId)}
        );

        boolean canMake = cursor.moveToFirst() && cursor.getInt(0) == 0;
        cursor.close();

        return canMake;
    }
}
