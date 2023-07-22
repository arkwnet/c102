package jp.arkw.alps.fe;

class Item {
    String name;
    int price;
    int image;

    Item(String name, int price, int image) {
        this.name = name;
        this.price = price;
        this.image = image;
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
}
