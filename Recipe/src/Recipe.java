import java.util.List;

class Recipe {

    public List<String> ingredients;
    private List<String> cookingEquipment;
    String recipeName;
    String method;

    public Recipe(List<String> ingredients, List<String> cookingEquipment, String recipeName, String method) {
        this.ingredients = ingredients;
        this.cookingEquipment = cookingEquipment;
        this.recipeName = recipeName;
        this.method = method;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
