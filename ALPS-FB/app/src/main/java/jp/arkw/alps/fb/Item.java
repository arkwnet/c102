package jp.arkw.alps.fb;

class Item {
    String name;
    int price;
    int image;
    int quantity;

    Item(String name, int price, int image) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.quantity = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public int getImage() {
        return this.image;
    }

    public int getQuantity() { return this.quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
