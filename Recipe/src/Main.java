import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main extends Application {

    private File file = new File("Recipes");
    private int recipeAmount = Objects.requireNonNull(file.listFiles()).length;
    private List<String> files;
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> ingredientsChosen = new ArrayList<>();
    private ArrayList<String> equipment = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<Integer> ticks = new ArrayList<>();
    private ArrayList<Float> percentages = new ArrayList<>();
    private Text name = new Text();
    private Text method = new Text();
    private Text ingredientsToAdd = new Text(" Ingredients needed:");
    private int a = 0;
    private Text missingIngredients = new Text();
    private TreeMap<Float, List<String>> food = new TreeMap<>(Collections.reverseOrder());
    private ArrayList<String> ingredientsNeeded = new ArrayList<>();
    private ArrayList<String> organizedRecipeList = new ArrayList<>();
    private String recipeNameToAdd;
    private String ingredientsToAddString;
    private String equipmentNeeded;
    private String methodNeeded;
    private TextField recipeNameToAddBox = new TextField();
    private TextField ingredientsToAddBox = new TextField();
    private TextField equipmentNeededBox = new TextField();
    private TextField methodNeededBox = new TextField();

    public Main() {
    }


    private void readData() {
        try (Stream<Path> walk = Files.walk(Paths.get(String.valueOf(file)))) {
            files = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < recipeAmount; i++) {
            try {
                String recipe = Files.readAllLines(Paths.get(String.valueOf(files.get(i)))).get(0);
                String ingredientsLine = Files.readAllLines(Paths.get(String.valueOf(files.get(i)))).get(1);
                String[] ingredientArray = ingredientsLine.split(", ");
                List<String> ingredientList = (Arrays.asList(ingredientArray));
                for (String anIngredientList : ingredientList) {
                    if (ingredients.size() != 0) {
                        if (!ingredients.contains(anIngredientList)) {
                            ingredients.add(anIngredientList);
                        }
                    } else {
                        ingredients.add(anIngredientList);
                    }
                }
                String equipmentLine = Files.readAllLines(Paths.get(String.valueOf(files.get(i)))).get(2);
                String[] equipmentArray = equipmentLine.split(", ");
                List<String> equipmentList = (Arrays.asList(equipmentArray));
                for (String anEquipmentList : equipmentList) {
                    if (equipment.size() != 0) {
                        if (!equipment.contains(anEquipmentList)) {
                            equipment.add(anEquipmentList);
                        }
                    } else {
                        equipment.add(anEquipmentList);
                    }
                }
                String method = Files.readAllLines(Paths.get(String.valueOf(files.get(i)))).get(3);
                method = method.replaceAll("\\\\n", System.lineSeparator());
                Recipe latestRecipe = new Recipe(ingredientList, equipmentList, recipe, method);
                recipes.add(latestRecipe);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void compare() {
        float ingredientAmount;
        float percentage;
        percentages.clear();
        for (int i = 0; i < recipes.size(); i++) {
            ingredientAmount = 0;
            for (String anIngredientsChosen : ingredientsChosen) {
                if (recipes.get(i).getIngredients().contains(anIngredientsChosen)) {
                    ingredientAmount++;
                }
            }
            percentage = recipes.get(i).getIngredients().size();
            percentages.add(i, ingredientAmount / percentage * 100);
        }
    }

    private void organize() {
        food.clear();
        for (int i = 0; i < recipes.size(); i++) {
            food.computeIfAbsent(percentages.get(i), k -> new ArrayList<>()).add(recipes.get(i).recipeName);
        }
        organizedRecipeList.clear();
        String recipeString;
        for (int i = 0; i < food.values().size(); i++) {
            recipeString = food.values().toArray()[i].toString();
            recipeString = recipeString.substring(1, recipeString.length() - 1);
            if (recipeString.contains(",")) {
                int comma = (int) recipeString.chars().filter(num -> num == ',').count();
                for (int j = 0; j < comma; j++) {
                    organizedRecipeList.add(recipeString.substring(0, recipeString.indexOf(",")));
                    recipeString = recipeString.substring(recipeString.indexOf(",") + 2, recipeString.length());
                }
                organizedRecipeList.add(recipeString);
            } else {
                organizedRecipeList.add(recipeString);
            }
        }

        for (int i = 0; i < organizedRecipeList.size(); i++) {
            for (int j = 0; j < recipes.size(); j++) {
                if (organizedRecipeList.get(i).equals(recipes.get(j).recipeName)) {
                    Collections.swap(recipes, j, i);
                    Collections.swap(percentages, j, i);
                }
            }
        }
    }

    private void ingredientsToAdd() {
        ingredientsNeeded.clear();
        for (int i = 0; i < recipes.size(); i++) {
            String testIngredientsNeeded = "";
            if (percentages.get(i) != 100.0) {
                for (int k = 0; k < recipes.get(i).ingredients.size(); k++) {
                    if (!ingredientsChosen.contains(recipes.get(i).ingredients.get(k))) {
                        String abc = recipes.get(i).ingredients.get(k);
                        if (!testIngredientsNeeded.contains(abc)) {
                            testIngredientsNeeded = testIngredientsNeeded.concat("     " + recipes.get(i).ingredients.get(k) + "\n");
                            ingredientsNeeded.add(i, testIngredientsNeeded);
                        }
                    }
                }
            } else {
                testIngredientsNeeded = "     No more objects are needed";
                ingredientsNeeded.add(testIngredientsNeeded);
            }
        }
    }

    private void createNewRecipe() {
        File newFile = new File(file, recipeNameToAdd + ".txt");
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String> lines = Arrays.asList(recipeNameToAdd, ingredientsToAddString, equipmentNeeded, methodNeeded);
        Path afile = Paths.get(String.valueOf(newFile));
        try {
            Files.write(afile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void start(Stage primaryStage) {
        readData();
        primaryStage.setTitle("Recipe Chooser");
        TilePane tilePane = new TilePane();
        for (int i = 0; i < ingredients.size(); i++) {
            ticks.add(0);
            CheckBox checkBox = new CheckBox(ingredients.get(i));
            tilePane.getChildren().add(checkBox);
            int finalI = i;
            EventHandler<ActionEvent> event = e -> {
                if (checkBox.isSelected()) {
                    ticks.set(finalI, 1);
                } else {
                    ticks.set(finalI, 0);
                }
            };
            checkBox.setOnAction(event);
        }
        Button next = new Button("Next");
        next.setOnAction(event -> {
            if (a < recipes.size() - 1) {
                a++;
                name.setText(recipes.get(a).getRecipeName());
                method.setText(recipes.get(a).getMethod());
                if (ingredientsNeeded.size() == 1) {
                    missingIngredients.setText(ingredientsNeeded.get(0));
                } else {
                    missingIngredients.setText(ingredientsNeeded.get(a));
                }
            }
        });
        Button previous = new Button("Previous");
        previous.setOnAction(event -> {
            if (a > 0) {
                a--;
                name.setText(recipes.get(a).getRecipeName());
                method.setText(recipes.get(a).getMethod());
                if (ingredientsNeeded.size() == 1) {
                    missingIngredients.setText(ingredientsNeeded.get(0));
                } else {
                    missingIngredients.setText(ingredientsNeeded.get(a));
                }
            }
        });
        Button home = new Button("Back");
        home.setOnAction(event -> a = 0);
        Button button = new Button("Go");
        Button add = new Button("Add");
        Button create = new Button("Create Recipe");
        recipeNameToAddBox.setText("Add Recipe Name here");
        ingredientsToAddBox.setText("Add ingredients here");
        equipmentNeededBox.setText("Add equipment here");
        methodNeededBox.setText("Add method here");


        VBox vBox = new VBox(name, method, home, next, previous, ingredientsToAdd, missingIngredients);
        ingredientsToAddBox.setAlignment(Pos.TOP_LEFT);
        ingredientsToAddBox.setPrefHeight(40);
        ingredientsToAddBox.setPrefWidth(40);
        equipmentNeededBox.setAlignment(Pos.TOP_LEFT);
        equipmentNeededBox.setPrefHeight(40);
        equipmentNeededBox.setPrefWidth(40);
        methodNeededBox.setAlignment(Pos.TOP_LEFT);
        methodNeededBox.setPrefHeight(150);
        methodNeededBox.setPrefWidth(40);
        VBox vBox1 = new VBox(recipeNameToAddBox, ingredientsToAddBox, equipmentNeededBox, methodNeededBox, create);
        Scene scene1 = new Scene(vBox, 500, 500);
        Scene scene2 = new Scene(vBox1, 500, 500);
        tilePane.getChildren().add(button);
        tilePane.getChildren().add(add);

        add.setOnAction(event -> primaryStage.setScene(scene2));

        create.setOnAction(event ->
        {
            recipeNameToAdd = recipeNameToAddBox.getText();
            ingredientsToAddString = ingredientsToAddBox.getText();
            equipmentNeeded = equipmentNeededBox.getText();
            methodNeeded = methodNeededBox.getText();
            createNewRecipe();
        });


        Scene scene = new Scene(tilePane, 150, 200);
        home.setOnAction(event -> primaryStage.setScene(scene));
        button.setOnAction(event -> {
            a = 0;
            primaryStage.setScene(scene1);
            for (int i = 0; i < ticks.size(); i++) {
                if (ticks.get(i).equals(1) && !ingredientsChosen.contains(ingredients.get(i))) {
                    ingredientsChosen.add(ingredients.get(i));
                } else if (ticks.get(i).equals(0)) {
                    ingredientsChosen.remove(ingredients.get(i));
                }
            }
            compare();
            organize();
            ingredientsToAdd();
            name.setText(recipes.get(a).getRecipeName());
            method.setText(recipes.get(a).getMethod());
            if (ingredientsNeeded.size() == 1) {
                missingIngredients.setText(ingredientsNeeded.get(0));
            } else {
                missingIngredients.setText(ingredientsNeeded.get(a));
            }
            for (Recipe ignored : recipes) {
                if (ingredientsChosen.isEmpty()) {
                    next.setVisible(false);
                    previous.setVisible(false);
                    name.setVisible(false);
                    method.setVisible(false);
                    missingIngredients.setVisible(false);
                } else {
                    next.setVisible(true);
                    previous.setVisible(true);
                    name.setVisible(true);
                    method.setVisible(true);
                    missingIngredients.setVisible(true);
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}