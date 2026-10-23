
package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout ingredientList;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 32, 24, 24);
        root.setBackgroundColor(0xFFF7F8F2);

        TextView title = new TextView(this);
        title.setText("Smart Pantry Manager");
        title.setTextSize(26);
        title.setTextColor(0xFF244B3B);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 12, 0, 20);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Your ingredients, all in one place");
        subtitle.setTextSize(16);
        subtitle.setTextColor(0xFF555555);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, 20);
        root.addView(subtitle);

        Button addButton = new Button(this);
        addButton.setText("+ Add Ingredient");
        root.addView(addButton);

        Button recipesButton = new Button(this);
        recipesButton.setText("Suggested Recipes");
        recipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, RecipeActivity.class);
            startActivity(intent);
        });
        root.addView(recipesButton);

        Button allRecipesButton = new Button(this);
        allRecipesButton.setText("All Recipes");
        allRecipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, AllRecipesActivity.class);
            startActivity(intent);
        });
        root.addView(allRecipesButton);

        emptyMessage = new TextView(this);
        emptyMessage.setText(
                "Your pantry is empty. Add your first ingredient!");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(0xFF666666);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setPadding(8, 28, 8, 28);
        root.addView(emptyMessage);

        ScrollView scrollView = new ScrollView(this);
        ingredientList = new LinearLayout(this);
        ingredientList.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(ingredientList);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        setContentView(root);

        addButton.setOnClickListener(v -> showIngredientDialog());

        refreshIngredients();
    }

    private void showIngredientDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(24, 8, 24, 8);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Ingredient name (e.g. Rice)");
        form.addView(nameInput);

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity (e.g. 2)");
        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        form.addView(quantityInput);

        EditText unitInput = new EditText(this);
        unitInput.setHint("Unit (e.g. kg, g, pieces)");
        form.addView(unitInput);

        EditText expiryInput = new EditText(this);
        expiryInput.setHint("Expiry date (optional: YYYY-MM-DD)");
        form.addView(expiryInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Ingredient")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String quantityText =
                            quantityInput.getText().toString().trim();
                    String unit = unitInput.getText().toString().trim();
                    String expiry = expiryInput.getText().toString().trim();

                    if (name.isEmpty() || quantityText.isEmpty()
                            || unit.isEmpty()) {
                        Toast.makeText(this,
                                "Please enter a name, quantity and unit.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    double quantity;
                    try {
                        quantity = Double.parseDouble(quantityText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this,
                                "Enter a valid quantity.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!Double.isFinite(quantity) || quantity <= 0) {
                        Toast.makeText(this,
                                "Quantity must be greater than zero.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    long result = databaseHelper.addIngredient(
                            name, quantity, unit,
                            expiry.isEmpty() ? null : expiry);

                    if (result == -1) {
                        Toast.makeText(this,
                                "Could not save ingredient.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Ingredient saved!",
                                Toast.LENGTH_SHORT).show();
                        refreshIngredients();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && ingredientList != null) {
            refreshIngredients();
        }
    }

    private void refreshIngredients() {
        ingredientList.removeAllViews();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            if (cursor == null || cursor.getCount() == 0) {
                emptyMessage.setVisibility(TextView.VISIBLE);
                return;
            }

            emptyMessage.setVisibility(TextView.GONE);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COL_NAME));
                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COL_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COL_EXPIRY));

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 16, 20, 16);
                card.setBackgroundColor(0xFFFFFFFF);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 8, 0, 8);
                card.setLayoutParams(cardParams);

                String quantityDisplay =
                        quantity == Math.floor(quantity)
                                ? String.valueOf((long) quantity)
                                : String.valueOf(quantity);

                TextView ingredientText = new TextView(this);
                ingredientText.setText(String.format(
                        Locale.getDefault(),
                        "%s\nQuantity: %s %s\nExpiry: %s",
                        name,
                        quantityDisplay,
                        unit,
                        expiry == null || expiry.isEmpty()
                                ? "Not specified" : expiry));
                ingredientText.setTextSize(16);
                ingredientText.setTextColor(0xFF222222);
                card.addView(ingredientText);

                Button editButton = new Button(this);
                editButton.setText("Edit");
                editButton.setOnClickListener(v ->
                        showEditIngredientDialog(
                                id, name, quantity, unit, expiry));
                card.addView(editButton);

                Button deleteButton = new Button(this);
                deleteButton.setText("Delete");
                deleteButton.setOnClickListener(v ->
                        new AlertDialog.Builder(this)
                                .setTitle("Delete ingredient?")
                                .setMessage("Remove " + name
                                        + " from your pantry?")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Delete", (d, w) -> {
                                    databaseHelper.deleteIngredient(id);
                                    refreshIngredients();
                                    Toast.makeText(this,
                                            "Ingredient deleted.",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .show());
                card.addView(deleteButton);

                ingredientList.addView(card);
            }
        }
    }

    private void showEditIngredientDialog(
            int id, String currentName, double currentQuantity,
            String currentUnit, String currentExpiry) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Ingredient name");
        nameInput.setText(currentName);
        layout.addView(nameInput);

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        quantityInput.setText(String.valueOf(currentQuantity));
        layout.addView(quantityInput);

        EditText unitInput = new EditText(this);
        unitInput.setHint("Unit (e.g. kg, g, pieces)");
        unitInput.setText(currentUnit);
        layout.addView(unitInput);

        EditText expiryInput = new EditText(this);
        expiryInput.setHint("Expiry date (optional)");
        expiryInput.setText(currentExpiry == null ? "" : currentExpiry);
        layout.addView(expiryInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit ingredient")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String quantityText =
                            quantityInput.getText().toString().trim();
                    String unit = unitInput.getText().toString().trim();
                    String expiry =
                            expiryInput.getText().toString().trim();

                    if (name.isEmpty() || quantityText.isEmpty()
                            || unit.isEmpty()) {
                        Toast.makeText(this,
                                "Please enter a name, quantity and unit.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double quantity = Double.parseDouble(quantityText);

                        if (!Double.isFinite(quantity) || quantity <= 0) {
                            Toast.makeText(this,
                                    "Quantity must be greater than zero.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseHelper.updateIngredient(
                                id, name, quantity, unit,
                                expiry.isEmpty() ? null : expiry);

                        refreshIngredients();

                        Toast.makeText(this,
                                "Ingredient updated.",
                                Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(this,
                                "Please enter a valid quantity.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
